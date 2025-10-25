package com.natswarchuan.payment.demo.helper;

import com.natswarchuan.payment.demo.config.security.SecurityUtil;
import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.entity.PaymentMethod;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.entity.Wallet;
import com.natswarchuan.payment.demo.exception.custom.ConflictException;
import com.natswarchuan.payment.demo.interfaces.services.IWalletService;
import com.natswarchuan.payment.demo.repository.PaymentMethodRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Lớp helper chứa các logic phụ trợ cho {@link
 * com.natswarchuan.payment.demo.service.PaymentMethodService}.
 *
 * <p>Đóng gói các quy trình phức tạp như kiểm tra xung đột sở hữu, khôi phục, cập nhật và tạo mới
 * các phương thức thanh toán.
 */
@Component
@RequiredArgsConstructor
public class PaymentMethodHelper {

  private final PaymentMethodRepository paymentMethodRepository;
  private final IWalletService walletService;
  private final SecurityUtil securityUtil;

  /**
   * Xử lý logic "upsert" (cập nhật hoặc chèn mới) cho một phương thức thanh toán, cho phép tái sử
   * dụng bởi người dùng khác sau khi hủy liên kết.
   *
   * @param type Loại phương thức thanh toán.
   * @param provider Nhà cung cấp (VNPAY, VCB, ...).
   * @param accountNumber Số tài khoản.
   * @param accountName Tên chủ tài khoản.
   * @return Thực thể {@link PaymentMethod} đã được xử lý.
   * @throws ConflictException nếu phương thức thanh toán đang được sử dụng bởi người dùng khác.
   */
  public PaymentMethod upsertPaymentMethod(
      Integer type, String provider, String accountNumber, String accountName) {
    User currentUser = securityUtil.getCurrentAuthenticatedUser();
    Wallet wallet = walletService.findWalletEntityByUserId(currentUser.getId());

    Optional<PaymentMethod> activeOwnerOpt =
        paymentMethodRepository.findByProviderAndAccountNumberAndType(
            provider, accountNumber, type);

    if (activeOwnerOpt.isPresent()) {
      PaymentMethod activeOwner = activeOwnerOpt.get();

      if (!activeOwner.getWallet().getUser().getId().equals(currentUser.getId())) {
        throw new ConflictException(MessageConstant.Payment.PAYMENT_METHOD_IN_USE);
      }
    }

    Optional<PaymentMethod> userPaymentMethodOpt =
        paymentMethodRepository.findSoftDeletedByWalletIdAndProviderAndAccountNumberAndType(
            wallet.getId(), provider, accountNumber, type);

    if (userPaymentMethodOpt.isPresent()) {
      PaymentMethod existingPaymentMethod = userPaymentMethodOpt.get();
      existingPaymentMethod.setDeletedAt(null);
      existingPaymentMethod.setAccountName(accountName);
      return paymentMethodRepository.save(existingPaymentMethod);
    } else {

      PaymentMethod newPaymentMethod =
          PaymentMethod.builder()
              .wallet(wallet)
              .type(type)
              .provider(provider)
              .accountName(accountName)
              .accountNumber(accountNumber)
              .isDefault(false)
              .build();
      return paymentMethodRepository.save(newPaymentMethod);
    }
  }
}
