package com.natswarchuan.payment.demo.dto.request.transaction;

import com.natswarchuan.payment.demo.constant.ValidationConstant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

/**
 * Lớp cơ sở trừu tượng cho các yêu cầu giao dịch liên quan đến cổng thanh toán.
 * <p>
 * Chứa các trường dữ liệu chung như số tiền, ID phương thức thanh toán và mô tả, giúp giảm thiểu
 * sự trùng lặp mã trong các lớp request cụ thể như {@link DepositRequest} và {@link
 * WithdrawRequest}.
 */
@Data
public abstract class BasePaymentRequest {

  /**
   * Số tiền của giao dịch. Phải là một số dương.
   */
  @NotNull(message = ValidationConstant.AMOUNT_REQUIRED)
  @Positive(message = ValidationConstant.AMOUNT_POSITIVE)
  private BigDecimal amount;

  /**
   * ID của phương thức thanh toán được sử dụng cho giao dịch.
   * <p>
   * <b>Lưu ý:</b> Ý nghĩa của trường này có thể khác nhau. Ví dụ, đối với rút tiền, đây là ID của
   * tài khoản ngân hàng đã lưu.
   */
  @NotNull(message = ValidationConstant.PAYMENT_METHOD_ID_REQUIRED)
  private UUID paymentMethodId;

  /**
   * Mô tả tùy chọn cho giao dịch.
   */
  private String description;
}
