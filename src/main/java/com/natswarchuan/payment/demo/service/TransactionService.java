package com.natswarchuan.payment.demo.service;

import com.fasterxml.uuid.Generators;
import com.natswarchuan.payment.demo.config.security.SecurityUtil;
import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.constant.TransactionConstant;
import com.natswarchuan.payment.demo.dto.request.transaction.DepositRequest;
import com.natswarchuan.payment.demo.dto.request.transaction.TransactionSearchRequest;
import com.natswarchuan.payment.demo.dto.request.transaction.TransferRequest;
import com.natswarchuan.payment.demo.dto.request.transaction.WithdrawRequest;
import com.natswarchuan.payment.demo.dto.response.gateway.CreatePaymentResponse;
import com.natswarchuan.payment.demo.dto.response.gateway.PayoutResponse;
import com.natswarchuan.payment.demo.dto.response.gateway.ProcessIpnResponse;
import com.natswarchuan.payment.demo.dto.response.transaction.TransactionSummaryResponse;
import com.natswarchuan.payment.demo.entity.PaymentMethod;
import com.natswarchuan.payment.demo.entity.Transaction;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.entity.Wallet;
import com.natswarchuan.payment.demo.exception.custom.ForbiddenException;
import com.natswarchuan.payment.demo.exception.custom.RateLimitExceededException;
import com.natswarchuan.payment.demo.exception.custom.ResourceNotFoundException;
import com.natswarchuan.payment.demo.helper.TransactionHelper;
import com.natswarchuan.payment.demo.helper.TransactionHelper.LockedTransactionContext;
import com.natswarchuan.payment.demo.helper.TransactionHelper.TransferWallets;
import com.natswarchuan.payment.demo.interfaces.services.IRateLimiterService;
import com.natswarchuan.payment.demo.interfaces.services.ITransactionService;
import com.natswarchuan.payment.demo.repository.SecretKeyRepository;
import com.natswarchuan.payment.demo.repository.TransactionRepository;
import com.natswarchuan.payment.demo.repository.WalletRepository;
import com.natswarchuan.payment.demo.repository.specifications.TransactionSpecifications;
import com.natswarchuan.payment.demo.util.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lớp dịch vụ triển khai các nghiệp vụ liên quan đến giao dịch.
 *
 * <p>Lớp này đóng vai trò điều phối, gọi đến {@link TransactionHelper} để thực hiện các logic
 * nghiệp vụ phức tạp. Nó đảm bảo tính toàn vẹn dữ liệu thông qua việc sử dụng annotation
 * {@code @Transactional}.
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class TransactionService extends AbService<Transaction, UUID>
    implements ITransactionService {

  private final TransactionHelper transactionHelper;
  private final SecurityUtil securityUtil;
  private final SecretKeyRepository secretKeyRepository;
  private final WalletRepository walletRepository;
  private final IRateLimiterService rateLimiterService;
  private final TransactionSpecifications transactionSpecifications;

  @Value("${app.secret-key.rate-limit.max-requests}")
  private int maxSecretKeyRequests;

  @Value("${app.secret-key.rate-limit.window-minutes}")
  private long secretKeyWindowInMinutes;

  /**
   * Khởi tạo TransactionService.
   *
   * @param repository Kho lưu trữ cho Transaction.
   * @param transactionHelper Lớp helper chứa logic nghiệp vụ.
   * @param securityUtil Tiện ích để lấy thông tin người dùng.
   * @param secretKeyRepository Kho lưu trữ cho khóa bí mật.
   * @param walletRepository Kho lưu trữ cho ví.
   * @param rateLimiterService Dịch vụ giới hạn tần suất.
   * @param transactionSpecifications Đối tượng để xây dựng truy vấn động.
   */
  public TransactionService(
      final TransactionRepository repository,
      final TransactionHelper transactionHelper,
      final SecurityUtil securityUtil,
      final SecretKeyRepository secretKeyRepository,
      final WalletRepository walletRepository,
      final IRateLimiterService rateLimiterService,
      final TransactionSpecifications transactionSpecifications) {
    super(repository);
    this.transactionHelper = transactionHelper;
    this.securityUtil = securityUtil;
    this.secretKeyRepository = secretKeyRepository;
    this.walletRepository = walletRepository;
    this.rateLimiterService = rateLimiterService;
    this.transactionSpecifications = transactionSpecifications;
  }

  @Override
  public CreatePaymentResponse initiateDeposit(
      final DepositRequest request,
      final String gatewayProvider,
      final HttpServletRequest httpServletRequest) {
    final User currentUser = securityUtil.getCurrentAuthenticatedUser();
    final Wallet userWallet = transactionHelper.findAndValidateUserWallet(currentUser);
    final Transaction transaction =
        transactionHelper.createPendingDepositTransaction(
            request, userWallet, gatewayProvider, httpServletRequest);
    return transactionHelper.callPaymentGatewayToCreatePayment(
        transaction, gatewayProvider, httpServletRequest);
  }

  @Override
  public void completeDeposit(final ProcessIpnResponse ipnResponse) {
    final Optional<LockedTransactionContext> lockedContextOpt =
        transactionHelper.findAndLockTransactionForProcessing(
            ipnResponse.getTransactionId(), TransactionConstant.Status.PENDING);
    lockedContextOpt.ifPresent(
        context ->
            transactionHelper.updateWalletBalanceAndTransactionForDeposit(ipnResponse, context));
  }

  @Override
  public void initiateWithdrawal(
      final WithdrawRequest request,
      final String provider,
      final HttpServletRequest httpServletRequest) {
    final User currentUser = securityUtil.getCurrentAuthenticatedUser();
    final Wallet userWallet =
        walletRepository
            .findAndLockByUserId(currentUser.getId())
            .orElseThrow(
                () -> new ResourceNotFoundException(MessageConstant.Wallet.USER_WALLET_NOT_FOUND));

    final PaymentMethod paymentMethod =
        transactionHelper.validateWithdrawalRequest(request, userWallet);
    final Transaction transaction =
        transactionHelper.createPendingWithdrawalTransaction(
            request, userWallet, paymentMethod, httpServletRequest);

    final PayoutResponse payoutResponse =
        transactionHelper.callPayoutGateway(transaction, provider);

    transactionHelper.finalizeWithdrawal(transaction, userWallet, payoutResponse);
  }

  @Override
  public void completeWithdrawal(final ProcessIpnResponse ipnResponse) {
    final Optional<LockedTransactionContext> lockedContextOpt =
        transactionHelper.findAndLockTransactionForProcessing(
            ipnResponse.getTransactionId(), TransactionConstant.Status.REVIEWING);
    lockedContextOpt.ifPresent(
        context ->
            transactionHelper.updateWalletBalanceAndTransactionForWithdrawal(ipnResponse, context));
  }

  /** {@inheritDoc} */
  @Override
  public String createTransferSecretKey(final HttpServletRequest request) {
    final User currentUser = securityUtil.getCurrentAuthenticatedUser();
    final String clientIp = HttpUtil.getClientIp(request);
    final String rateLimitKey = "secret_key_req:" + currentUser.getId();

    if (!rateLimiterService.isAllowed(rateLimitKey, clientIp)) {
      throw new RateLimitExceededException(
          MessageConstant.Transaction.SECRET_KEY_RATE_LIMIT_EXCEEDED, secretKeyWindowInMinutes);
    }

    final Wallet userWallet =
        walletRepository
            .findByUserId(currentUser.getId())
            .orElseThrow(
                () -> new ResourceNotFoundException(MessageConstant.Wallet.USER_WALLET_NOT_FOUND));

    if (userWallet.getPin() == null) {
      throw new ForbiddenException(MessageConstant.Wallet.PIN_NOT_SET);
    }
    final String secretKey = Generators.timeBasedEpochGenerator().generate().toString();
    secretKeyRepository.saveSecretKey(secretKey, userWallet.getPin());
    return secretKey;
  }

  @Override
  public void initiateTransfer(final TransferRequest request) {
    final User senderUser = securityUtil.getCurrentAuthenticatedUser();
    final TransferWallets wallets =
        transactionHelper.getAndLockWalletsForTransfer(
            senderUser, request.getRecipientWalletNumber());

    final BigDecimal senderBalanceBefore = wallets.sender().getBalance();
    final BigDecimal receiverBalanceBefore = wallets.receiver().getBalance();

    transactionHelper.validatePinAndSecretKey(request, wallets.sender());

    transactionHelper.performBalanceUpdateAndSave(wallets, request.getAmount());

    transactionHelper.createTransferTransactions(
        wallets, request, senderBalanceBefore, receiverBalanceBefore);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public Page<TransactionSummaryResponse> searchTransactionsForCurrentUser(
      final TransactionSearchRequest searchRequest, final Pageable pageable) {
    final User currentUser = securityUtil.getCurrentAuthenticatedUser();
    final Wallet userWallet =
        walletRepository
            .findByUserId(currentUser.getId())
            .orElseThrow(
                () -> new ResourceNotFoundException(MessageConstant.Wallet.USER_WALLET_NOT_FOUND));

    final Specification<Transaction> spec =
        transactionSpecifications.fromSearchRequest(searchRequest, userWallet.getId());

    final Page<Transaction> transactionPage = specificationExecutor.findAll(spec, pageable);

    return transactionPage.map(
        transaction -> {
          final TransactionSummaryResponse dto = new TransactionSummaryResponse();
          dto.fromEntity(transaction);
          return dto;
        });
  }
}
