package com.natswarchuan.payment.demo.service;

import com.natswarchuan.payment.demo.config.security.SecurityUtil;
import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.constant.PaymentMethodConstant;
import com.natswarchuan.payment.demo.dto.request.paymentmethod.CreatePaymentMethodRequest;
import com.natswarchuan.payment.demo.dto.request.paymentmethod.LinkEwalletRequest;
import com.natswarchuan.payment.demo.entity.PaymentMethod;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.exception.custom.ForbiddenException;
import com.natswarchuan.payment.demo.helper.PaymentMethodHelper;
import com.natswarchuan.payment.demo.interfaces.services.IPaymentMethodService;
import com.natswarchuan.payment.demo.repository.PaymentMethodRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Lớp dịch vụ triển khai các nghiệp vụ liên quan đến phương thức thanh toán. */
@Service
@Transactional(rollbackFor = Exception.class)
public class PaymentMethodService extends AbService<PaymentMethod, UUID>
    implements IPaymentMethodService {

  private final SecurityUtil securityUtil;
  private final PaymentMethodHelper paymentMethodHelper;

  /**
   * Khởi tạo PaymentMethodService với các dependency cần thiết.
   *
   * @param repository repository cho PaymentMethod.
   * @param securityUtil tiện ích để lấy thông tin người dùng đã xác thực.
   * @param paymentMethodHelper helper chứa các logic nghiệp vụ phụ trợ.
   */
  public PaymentMethodService(
      final PaymentMethodRepository repository,
      final SecurityUtil securityUtil,
      final PaymentMethodHelper paymentMethodHelper) {
    super(repository);
    this.securityUtil = securityUtil;
    this.paymentMethodHelper = paymentMethodHelper;
  }

  /** {@inheritDoc} */
  @Override
  public void deleteForCurrentUser(final UUID id) {
    final User currentUser = securityUtil.getCurrentAuthenticatedUser();
    final PaymentMethod paymentMethod = this.findById(id);

    if (!paymentMethod.getWallet().getUser().getId().equals(currentUser.getId())) {
      throw new ForbiddenException(MessageConstant.Payment.PAYMENT_METHOD_NOT_OWNED);
    }
    repository.delete(paymentMethod);
  }

  /** {@inheritDoc} */
  @Override
  public PaymentMethod linkOrUpdateEwallet(final LinkEwalletRequest request) {
    return paymentMethodHelper.upsertPaymentMethod(
        PaymentMethodConstant.Type.E_WALLET,
        request.getProvider(),
        request.getAccountNumber(),
        request.getAccountName());
  }

  /** {@inheritDoc} */
  @Override
  public PaymentMethod linkOrUpdateBankAccount(final CreatePaymentMethodRequest request) {
    return paymentMethodHelper.upsertPaymentMethod(
        PaymentMethodConstant.Type.BANK_ACCOUNT,
        request.getProvider(),
        request.getAccountNumber(),
        request.getAccountName());
  }
}
