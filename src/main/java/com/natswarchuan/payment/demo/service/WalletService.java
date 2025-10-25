package com.natswarchuan.payment.demo.service;

import com.fasterxml.uuid.Generators;
import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.constant.WalletConstant;
import com.natswarchuan.payment.demo.dto.response.transaction.TransactionSummaryResponse;
import com.natswarchuan.payment.demo.dto.response.wallet.WalletDetailResponse;
import com.natswarchuan.payment.demo.entity.Transaction;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.entity.Wallet;
import com.natswarchuan.payment.demo.exception.custom.ResourceNotFoundException;
import com.natswarchuan.payment.demo.interfaces.services.IWalletService;
import com.natswarchuan.payment.demo.repository.TransactionRepository;
import com.natswarchuan.payment.demo.repository.WalletRepository;
import com.natswarchuan.payment.demo.util.DtoMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lớp dịch vụ triển khai các nghiệp vụ liên quan đến ví điện tử của người dùng.
 *
 * <p>Lớp này chịu trách nhiệm xử lý các logic nghiệp vụ như tạo ví, truy vấn thông tin, và quản lý
 * mã PIN.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WalletService extends AbService<Wallet, UUID> implements IWalletService {

  private final TransactionRepository transactionRepository;
  private final PasswordEncoder passwordEncoder;
  private final WalletRepository walletRepository;

  /**
   * Khởi tạo WalletService với các dependency cần thiết.
   *
   * @param repository repository cho Wallet.
   * @param transactionRepository repository cho Transaction.
   * @param passwordEncoder đối tượng để mã hóa mật khẩu/PIN. Sử dụng {@code @Lazy} để phá vỡ chu
   * trình phụ thuộc (circular dependency) tại thời điểm khởi tạo.
   */
  public WalletService(
      final WalletRepository repository,
      final TransactionRepository transactionRepository,
      @Lazy final PasswordEncoder passwordEncoder) {
    super(repository);
    this.transactionRepository = transactionRepository;
    this.passwordEncoder = passwordEncoder;
    this.walletRepository = repository;
  }

  /** {@inheritDoc} */
  @Override
  public Wallet createWalletForUser(final User user) {
    final Wallet newWallet = new Wallet();
    newWallet.setUser(user);
    newWallet.setNumber(Generators.timeBasedEpochGenerator().generate().toString());
    newWallet.setBalance(BigDecimal.ZERO);
    newWallet.setCurrency(WalletConstant.Currency.VND);
    newWallet.setStatus(WalletConstant.Status.ACTIVE);
    return save(newWallet);
  }

  /** {@inheritDoc} */
  @Override
  public Wallet findByNumber(final String number) {
    return walletRepository
        .findByNumber(number)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(MessageConstant.Wallet.NOT_FOUND_BY_NUMBER, number));
  }

  /**
   * {@inheritDoc}
   *
   * <p><b>Phân tích về hiệu năng (Performance Analysis - 1+1 Query):</b>
   *
   * <p>Phương thức này thực hiện hai truy vấn đến cơ sở dữ liệu:
   *
   * <ol>
   * <li><b>Truy vấn 1:</b> Lấy thông tin {@code Wallet} cùng với danh sách {@code
   * paymentMethods}. Truy vấn này đã được tối ưu hóa bằng {@code @EntityGraph} trong {@link
   * WalletRepository#findByUserId(Long)} để tránh vấn đề N+1 query.
   * <li><b>Truy vấn 2:</b> Lấy một trang (page) các giao dịch gần đây nhất của ví.
   * </ol>
   *
   * <p>Đây là một quyết định thiết kế có chủ đích. Việc gộp cả hai vào một truy vấn duy nhất rất
   * phức tạp và thường không hiệu quả khi cần phân trang trên một tập hợp con (lấy 10 giao dịch gần
   * nhất). Cách tiếp cận hiện tại với hai truy vấn riêng biệt, được đánh index đầy đủ, sẽ cho hiệu
   * năng tốt và dễ bảo trì hơn trong trường hợp này.
   */
  @Override
  @Transactional(readOnly = true)
  public WalletDetailResponse findWalletForUser(final Long userId) {
    final Wallet wallet = findWalletEntityByUserId(userId);

    final Pageable recentTransactionsPageable =
        PageRequest.of(0, 10, Sort.by("createdAt").descending());
    final Page<Transaction> recentTransactions =
        transactionRepository.findByWalletId(wallet.getId(), recentTransactionsPageable);
    final List<TransactionSummaryResponse> transactionDtos =
        DtoMapper.toDtoList(recentTransactions.getContent(), TransactionSummaryResponse::new);

    final WalletDetailResponse response = new WalletDetailResponse();
    response.fromEntity(wallet);
    response.setTransactions(transactionDtos);
    return response;
  }

  /** {@inheritDoc} */
  @Override
  public void setPin(final Long userId, final String pin) {
    final Wallet wallet = findWalletEntityByUserId(userId);
    wallet.setPin(passwordEncoder.encode(pin));
    walletRepository.save(wallet);
  }

  /** {@inheritDoc} */
  @Override
  public Wallet findWalletEntityByUserId(final Long userId) {
    return walletRepository
        .findByUserId(userId)
        .orElseThrow(
            () -> new ResourceNotFoundException(MessageConstant.Wallet.USER_WALLET_NOT_FOUND));
  }
}

