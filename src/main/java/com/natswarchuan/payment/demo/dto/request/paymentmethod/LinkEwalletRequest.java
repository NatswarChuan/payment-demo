package com.natswarchuan.payment.demo.dto.request.paymentmethod;

import com.natswarchuan.payment.demo.constant.PaymentMethodConstant;
import com.natswarchuan.payment.demo.constant.ValidationConstant;
import com.natswarchuan.payment.demo.entity.PaymentMethod;
import com.natswarchuan.payment.demo.interfaces.IDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO (Data Transfer Object) cho yêu cầu liên kết một ví điện tử mới làm phương thức thanh toán.
 */
@Data
public class LinkEwalletRequest implements IDto<PaymentMethod> {

  /**
   * Nhà cung cấp dịch vụ ví điện tử (ví dụ: MOMO, ZALOPAY).
   */
  @NotBlank(message = ValidationConstant.EWALLET_PROVIDER_REQUIRED)
  private String provider;

  /**
   * Tên của chủ sở hữu tài khoản ví điện tử.
   */
  @NotBlank(message = ValidationConstant.EWALLET_ACCOUNT_NAME_REQUIRED)
  private String accountName;

  /**
   * Số tài khoản hoặc định danh của ví điện tử (thường là số điện thoại).
   */
  @NotBlank(message = ValidationConstant.EWALLET_ACCOUNT_REQUIRED)
  private String accountNumber;

  /**
   * Chuyển đổi DTO này thành một thực thể {@link PaymentMethod}.
   *
   * @return một thực thể {@code PaymentMethod} với loại là E_WALLET.
   */
  @Override
  public PaymentMethod toEntity() {
    return PaymentMethod.builder()
        .type(PaymentMethodConstant.Type.E_WALLET)
        .provider(this.provider.toUpperCase())
        .accountName(this.accountName)
        .accountNumber(this.accountNumber)
        .isDefault(false)
        .build();
  }
}
