package com.natswarchuan.payment.demo.helper;

import com.natswarchuan.payment.demo.constant.ApiConstant;
import com.natswarchuan.payment.demo.constant.LogConstant;
import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.constant.TransactionConstant;
import com.natswarchuan.payment.demo.constant.WalletConstant;
import com.natswarchuan.payment.demo.dto.request.gateway.CreatePaymentRequest;
import com.natswarchuan.payment.demo.dto.request.transaction.DepositRequest;
import com.natswarchuan.payment.demo.dto.request.transaction.TransferRequest;
import com.natswarchuan.payment.demo.dto.request.transaction.WithdrawRequest;
import com.natswarchuan.payment.demo.dto.response.gateway.CreatePaymentResponse;
import com.natswarchuan.payment.demo.dto.response.gateway.PayoutResponse;
import com.natswarchuan.payment.demo.dto.response.gateway.ProcessIpnResponse;
import com.natswarchuan.payment.demo.entity.PaymentMethod;
import com.natswarchuan.payment.demo.entity.Transaction;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.entity.Wallet;
import com.natswarchuan.payment.demo.exception.custom.BadRequestException;
import com.natswarchuan.payment.demo.exception.custom.ForbiddenException;
import com.natswarchuan.payment.demo.exception.custom.ResourceNotFoundException;
import com.natswarchuan.payment.demo.exception.custom.UnauthorizedException;
import com.natswarchuan.payment.demo.interfaces.services.IPaymentGatewayService;
import com.natswarchuan.payment.demo.repository.PaymentMethodRepository;
import com.natswarchuan.payment.demo.repository.SecretKeyRepository;
import com.natswarchuan.payment.demo.repository.TransactionRepository;
import com.natswarchuan.payment.demo.repository.WalletRepository;
import com.natswarchuan.payment.demo.service.PaymentGatewayFactory;
import com.natswarchuan.payment.demo.util.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

/**
 * Lớp helper chứa các logic nghiệp vụ phụ trợ cho {@link
 * com.natswarchuan.payment.demo.service.TransactionService}.
 *
 * <p>Lớp này đóng gói các logic phức tạp liên quan đến việc xử lý các loại giao dịch khác nhau như
 * nạp tiền, rút tiền và chuyển tiền.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionHelper {

  /** Record để giữ các thực thể Transaction và Wallet đã được khóa một cách an toàn. */
  public record LockedTransactionContext(Transaction transaction, Wallet wallet) {}

  /** Record để giữ ví của người gửi và người nhận đã được khóa. */
  public record TransferWallets(Wallet sender, Wallet receiver) {}

  private final WalletRepository walletRepository;
  private final TransactionRepository transactionRepository;
  private final PaymentGatewayFactory paymentGatewayFactory;
  private final PaymentMethodRepository paymentMethodRepository;
  private final PasswordEncoder passwordEncoder;
  private final SecretKeyRepository secretKeyRepository;

  @Value("${app.base-url}")
  private String appBaseUrl;

  @Value("${app.payment.return-url}")
  private String paymentReturnUrl;

  /**
   * Tìm và xác thực ví của người dùng.
   *
   * @param user Người dùng hiện tại.
   * @return Ví của người dùng nếu hợp lệ.
   */
  public Wallet findAndValidateUserWallet(User user) {
    Wallet userWallet =
        walletRepository
            .findByUserId(user.getId())
            .orElseThrow(
                () -> new ResourceNotFoundException(MessageConstant.Wallet.USER_WALLET_NOT_FOUND));
    if (!userWallet.getStatus().equals(WalletConstant.Status.ACTIVE)) {
      throw new ForbiddenException(MessageConstant.Transaction.WALLET_INACTIVE_ERROR);
    }
    return userWallet;
  }

  /**
   * Tạo một giao dịch nạp tiền đang chờ xử lý.
   *
   * @param request Dữ liệu yêu cầu nạp tiền.
   * @param userWallet Ví của người dùng.
   * @param gatewayProvider Nhà cung cấp cổng thanh toán.
   * @param httpServletRequest Đối tượng request HTTP.
   * @return Giao dịch đã được tạo và lưu.
   */
  public Transaction createPendingDepositTransaction(
      DepositRequest request,
      Wallet userWallet,
      String gatewayProvider,
      HttpServletRequest httpServletRequest) {
    Transaction transaction =
        Transaction.builder()
            .wallet(userWallet)
            .amount(request.getAmount())
            .transactionFee(BigDecimal.ZERO)
            .balanceBefore(userWallet.getBalance())
            .type(TransactionConstant.Type.DEPOSIT)
            .status(TransactionConstant.Status.PENDING)
            .description(
                String.format(MessageConstant.Transaction.DEPOSIT_DESCRIPTION, gatewayProvider))
            .ipAddress(HttpUtil.getClientIp(httpServletRequest))
            .build();
    return transactionRepository.save(transaction);
  }

  /**
   * Gọi đến cổng thanh toán để tạo yêu cầu thanh toán.
   *
   * @param transaction Giao dịch đã được tạo.
   * @param gatewayProvider Nhà cung cấp cổng thanh toán.
   * @param httpServletRequest Đối tượng request HTTP.
   * @return Phản hồi từ cổng thanh toán.
   */
  public CreatePaymentResponse callPaymentGatewayToCreatePayment(
      Transaction transaction, String gatewayProvider, HttpServletRequest httpServletRequest) {
    IPaymentGatewayService gatewayService = paymentGatewayFactory.getGateway(gatewayProvider);
    String ipnPath = ApiConstant.PAYMENTS_IPN.replace("{provider}", gatewayProvider);
    CreatePaymentRequest paymentRequest =
        CreatePaymentRequest.builder()
            .transactionId(transaction.getId())
            .amount(transaction.getAmount())
            .orderInfo(transaction.getDescription())
            .returnUrl(paymentReturnUrl)
            .ipnUrl(appBaseUrl + ipnPath)
            .ipAddr(HttpUtil.getClientIp(httpServletRequest))
            .build();
    return gatewayService.createPayment(paymentRequest);
  }

  /**
   * Xác thực yêu cầu rút tiền.
   *
   * @param request Yêu cầu rút tiền.
   * @param userWallet Ví của người dùng (đã được khóa).
   * @return Phương thức thanh toán hợp lệ.
   */
  public PaymentMethod validateWithdrawalRequest(WithdrawRequest request, Wallet userWallet) {
    if (!userWallet.getStatus().equals(WalletConstant.Status.ACTIVE)) {
      throw new ForbiddenException(MessageConstant.Transaction.WALLET_INACTIVE_ERROR);
    }
    if (userWallet.getBalance().compareTo(request.getAmount()) < 0) {
      throw new BadRequestException(MessageConstant.Wallet.INSUFFICIENT_BALANCE);
    }
    PaymentMethod paymentMethod =
        paymentMethodRepository
            .findById(request.getPaymentMethodId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        MessageConstant.General.ENTITY_NOT_FOUND_BY_ID,
                        request.getPaymentMethodId()));
    if (!paymentMethod.getWallet().getId().equals(userWallet.getId())) {
      throw new ForbiddenException(MessageConstant.Payment.PAYMENT_METHOD_NOT_OWNED);
    }
    return paymentMethod;
  }

  /**
   * Tạo một giao dịch rút tiền đang chờ xử lý.
   *
   * @param request Dữ liệu yêu cầu rút tiền.
   * @param userWallet Ví của người dùng.
   * @param paymentMethod Phương thức thanh toán.
   * @param httpServletRequest Đối tượng request HTTP.
   * @return Giao dịch đã được tạo.
   */
  public Transaction createPendingWithdrawalTransaction(
      WithdrawRequest request,
      Wallet userWallet,
      PaymentMethod paymentMethod,
      HttpServletRequest httpServletRequest) {
    return Transaction.builder()
        .wallet(userWallet)
        .amount(request.getAmount())
        .paymentMethod(paymentMethod)
        .transactionFee(BigDecimal.ZERO)
        .balanceBefore(userWallet.getBalance())
        .type(TransactionConstant.Type.WITHDRAWAL)
        .status(TransactionConstant.Status.REVIEWING)
        .description(request.getDescription())
        .ipAddress(HttpUtil.getClientIp(httpServletRequest))
        .build();
  }

  /**
   * Gọi API chi trả của cổng thanh toán.
   *
   * @param transaction Giao dịch rút tiền.
   * @param provider Nhà cung cấp dịch vụ.
   * @return Phản hồi từ cổng thanh toán.
   */
  public PayoutResponse callPayoutGateway(Transaction transaction, String provider) {
    try {
      IPaymentGatewayService gatewayService = paymentGatewayFactory.getGateway(provider);
      return gatewayService.initiatePayout(transaction);
    } catch (HttpClientErrorException e) {
      log.error(
          "Lỗi HTTP khi gọi cổng thanh toán rút tiền cho ví {}: {} - {}",
          transaction.getWallet().getId(),
          e.getStatusCode(),
          e.getResponseBodyAsString());
      throw new BadRequestException(
          MessageConstant.Transaction.WITHDRAWAL_INITIATION_FAILED, e.getResponseBodyAsString());
    } catch (ResourceAccessException e) {
      log.error(
          "Lỗi kết nối khi gọi cổng thanh toán rút tiền cho ví {}: {}",
          transaction.getWallet().getId(),
          e.getMessage());
      throw new BadRequestException(
          MessageConstant.Transaction.WITHDRAWAL_INITIATION_FAILED,
          "Lỗi kết nối tới nhà cung cấp.");
    } catch (Exception e) {
      log.error(
          "Ngoại lệ không mong muốn khi gọi cổng thanh toán rút tiền cho ví {}: {}",
          transaction.getWallet().getId(),
          e.getMessage(),
          e);
      throw new BadRequestException(
          MessageConstant.Transaction.WITHDRAWAL_INITIATION_FAILED,
          "Đã xảy ra lỗi không mong muốn.");
    }
  }

  /**
   * Hoàn tất giao dịch rút tiền sau khi có phản hồi từ cổng thanh toán.
   *
   * @param transaction Giao dịch rút tiền.
   * @param userWallet Ví của người dùng (đã được khóa).
   * @param payoutResponse Phản hồi từ cổng thanh toán.
   */
  public void finalizeWithdrawal(
      Transaction transaction, Wallet userWallet, PayoutResponse payoutResponse) {
    if (payoutResponse.isSuccess()) {
      userWallet.setBalance(userWallet.getBalance().subtract(transaction.getAmount()));
      walletRepository.save(userWallet);
      transaction.setProviderTransactionId(payoutResponse.getProviderTransactionId());
      transaction.setBalanceAfter(userWallet.getBalance());
      transactionRepository.save(transaction);
      log.info(
          "Yêu cầu rút tiền cho giao dịch {} đã được nhà cung cấp chấp nhận.", transaction.getId());
    } else {
      log.error(
          "Cổng thanh toán từ chối yêu cầu rút tiền cho ví {}: {}",
          userWallet.getId(),
          payoutResponse.getMessage());
      throw new BadRequestException(
          MessageConstant.Transaction.WITHDRAWAL_INITIATION_FAILED, payoutResponse.getMessage());
    }
  }

  /**
   * Lấy và khóa ví của người gửi và người nhận.
   *
   * @param senderUser Người dùng gửi.
   * @param recipientWalletNumber Số ví của người nhận.
   * @return Một record chứa cả hai ví đã được khóa.
   */
  public TransferWallets getAndLockWalletsForTransfer(
      User senderUser, String recipientWalletNumber) {
    Wallet senderWallet =
        walletRepository
            .findAndLockByUserId(senderUser.getId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(MessageConstant.Wallet.SENDER_WALLET_NOT_FOUND));

    Wallet receiverWallet =
        walletRepository
            .findAndLockByNumber(recipientWalletNumber)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        MessageConstant.Wallet.NOT_FOUND_BY_NUMBER, recipientWalletNumber));

    if (senderWallet.getId().equals(receiverWallet.getId())) {
      throw new BadRequestException(MessageConstant.Transaction.SELF_TRANSFER_ERROR);
    }
    if (!senderWallet.getStatus().equals(WalletConstant.Status.ACTIVE)
        || !receiverWallet.getStatus().equals(WalletConstant.Status.ACTIVE)) {
      throw new ForbiddenException(MessageConstant.Transaction.WALLET_INACTIVE_ERROR);
    }
    return new TransferWallets(senderWallet, receiverWallet);
  }

  /**
   * Xác thực mã PIN và khóa bí mật cho giao dịch chuyển tiền.
   *
   * @param request Yêu cầu chuyển tiền.
   * @param senderWallet Ví của người gửi (đã khóa).
   */
  public void validatePinAndSecretKey(TransferRequest request, Wallet senderWallet) {
    if (senderWallet.getPin() == null) {
      throw new ForbiddenException(MessageConstant.Wallet.PIN_NOT_SET);
    }
    if (!passwordEncoder.matches(request.getPin(), senderWallet.getPin())) {
      throw new UnauthorizedException(MessageConstant.Auth.INVALID_PIN);
    }
    String hashedPinFromRedis =
        secretKeyRepository
            .getHashedPinBySecretKey(request.getSecretKey())
            .orElseThrow(() -> new UnauthorizedException(MessageConstant.Auth.INVALID_SECRET_KEY));
    if (!hashedPinFromRedis.equals(senderWallet.getPin())) {
      throw new UnauthorizedException(MessageConstant.Auth.INVALID_SECRET_KEY);
    }
    secretKeyRepository.deleteSecretKey(request.getSecretKey());
  }

  /**
   * Thực hiện cập nhật số dư cho cả hai ví và lưu lại.
   *
   * @param wallets Các ví liên quan (đã khóa).
   * @param amount Số tiền chuyển.
   */
  public void performBalanceUpdateAndSave(TransferWallets wallets, BigDecimal amount) {
    if (wallets.sender().getBalance().compareTo(amount) < 0) {
      throw new BadRequestException(MessageConstant.Wallet.INSUFFICIENT_BALANCE);
    }
    wallets.sender().setBalance(wallets.sender().getBalance().subtract(amount));
    wallets.receiver().setBalance(wallets.receiver().getBalance().add(amount));
    walletRepository.save(wallets.sender());
    walletRepository.save(wallets.receiver());
  }

  /**
   * Tạo và lưu các bản ghi giao dịch cho cả người gửi và người nhận.
   *
   * @param wallets Các ví liên quan (đã khóa).
   * @param request Yêu cầu chuyển tiền.
   * @param senderBalanceBefore Số dư của người gửi trước giao dịch.
   * @param receiverBalanceBefore Số dư của người nhận trước giao dịch.
   */
  public void createTransferTransactions(
      TransferWallets wallets,
      TransferRequest request,
      BigDecimal senderBalanceBefore,
      BigDecimal receiverBalanceBefore) {
    Transaction senderTx =
        Transaction.builder()
            .wallet(wallets.sender())
            .amount(request.getAmount())
            .transactionFee(BigDecimal.ZERO)
            .balanceBefore(senderBalanceBefore)
            .balanceAfter(wallets.sender().getBalance())
            .type(TransactionConstant.Type.TRANSFER_OUT)
            .status(TransactionConstant.Status.COMPLETED)
            .description(
                String.format(
                    MessageConstant.Transaction.TRANSFER_OUT_DESCRIPTION,
                    wallets.receiver().getNumber(),
                    request.getDescription()))
            .build();
    Transaction savedSenderTx = transactionRepository.save(senderTx);

    Transaction receiverTx =
        Transaction.builder()
            .wallet(wallets.receiver())
            .amount(request.getAmount())
            .transactionFee(BigDecimal.ZERO)
            .balanceBefore(receiverBalanceBefore)
            .balanceAfter(wallets.receiver().getBalance())
            .type(TransactionConstant.Type.TRANSFER_IN)
            .status(TransactionConstant.Status.COMPLETED)
            .description(
                String.format(
                    MessageConstant.Transaction.TRANSFER_IN_DESCRIPTION,
                    wallets.sender().getNumber()))
            .relatedTransaction(savedSenderTx)
            .build();
    transactionRepository.save(receiverTx);
  }

  /**
   * Cập nhật số dư ví và trạng thái giao dịch sau khi xử lý IPN nạp tiền.
   *
   * @param ipnResponse Dữ liệu từ IPN.
   * @param context Các thực thể đã được khóa.
   */
  public void updateWalletBalanceAndTransactionForDeposit(
      ProcessIpnResponse ipnResponse, LockedTransactionContext context) {
    BigDecimal balanceBefore = context.wallet().getBalance();
    context.transaction().setBalanceBefore(balanceBefore);

    if (ipnResponse.getTransactionStatus().equals(TransactionConstant.Status.COMPLETED)) {
      context.wallet().setBalance(balanceBefore.add(context.transaction().getAmount()));
      walletRepository.save(context.wallet());
      context.transaction().setStatus(TransactionConstant.Status.COMPLETED);
      context.transaction().setBalanceAfter(context.wallet().getBalance());
    } else {
      context.transaction().setStatus(ipnResponse.getTransactionStatus());
      context.transaction().setBalanceAfter(balanceBefore);
      context
          .transaction()
          .setDescription(
              String.format(
                  MessageConstant.Transaction.TRANSACTION_FAILED, ipnResponse.getMessage()));
    }
    context.transaction().setProviderTransactionId(ipnResponse.getProviderTransactionId());
    transactionRepository.save(context.transaction());
  }

  /**
   * Cập nhật số dư ví và trạng thái giao dịch sau khi xử lý IPN rút tiền.
   *
   * @param ipnResponse Dữ liệu từ IPN.
   * @param context Các thực thể đã được khóa.
   */
  public void updateWalletBalanceAndTransactionForWithdrawal(
      ProcessIpnResponse ipnResponse, LockedTransactionContext context) {
    if (ipnResponse.getTransactionStatus().equals(TransactionConstant.Status.COMPLETED)) {
      context.transaction().setStatus(TransactionConstant.Status.COMPLETED);
      context.transaction().setBalanceAfter(context.wallet().getBalance());
    } else {
      context
          .wallet()
          .setBalance(context.wallet().getBalance().add(context.transaction().getAmount()));
      walletRepository.save(context.wallet());
      context.transaction().setStatus(TransactionConstant.Status.FAILED);
      context.transaction().setBalanceAfter(context.wallet().getBalance());
      context
          .transaction()
          .setDescription(
              String.format(
                  MessageConstant.Transaction.TRANSACTION_FAILED, ipnResponse.getMessage()));
    }
    context.transaction().setProviderTransactionId(ipnResponse.getProviderTransactionId());
    transactionRepository.save(context.transaction());
  }

  /**
   * Tìm, xác thực trạng thái và khóa một giao dịch cùng với ví liên quan của nó.
   *
   * @param transactionId ID của giao dịch.
   * @param expectedStatus Trạng thái mong đợi của giao dịch.
   * @return Optional chứa context đã khóa nếu hợp lệ.
   */
  public Optional<LockedTransactionContext> findAndLockTransactionForProcessing(
      UUID transactionId, Integer expectedStatus) {
    Transaction initialTransaction =
        transactionRepository
            .findById(transactionId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        MessageConstant.General.ENTITY_NOT_FOUND_BY_ID, transactionId));
    if (!initialTransaction.getStatus().equals(expectedStatus)) {
      log.warn(LogConstant.TRANSACTION_ALREADY_PROCESSED, initialTransaction.getId());
      return Optional.empty();
    }
    Wallet lockedWallet =
        walletRepository
            .findAndLockById(initialTransaction.getWallet().getId())
            .orElseThrow(
                () -> new ResourceNotFoundException(MessageConstant.Wallet.USER_WALLET_NOT_FOUND));
    Transaction lockedTransaction =
        transactionRepository
            .findAndLockById(transactionId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        MessageConstant.General.ENTITY_NOT_FOUND_BY_ID, transactionId));
    if (!lockedTransaction.getStatus().equals(expectedStatus)) {
      log.warn(LogConstant.TRANSACTION_ALREADY_PROCESSED, lockedTransaction.getId());
      return Optional.empty();
    }
    return Optional.of(new LockedTransactionContext(lockedTransaction, lockedWallet));
  }
}
