package com.natswarchuan.payment.demo.dto.request.gateway;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

/**
 * Lớp DTO (Data Transfer Object) đại diện cho yêu cầu tạo một giao dịch thanh toán gửi đến cổng
 * thanh toán.
 *
 * <p>Đây là một đối tượng trung gian, chứa thông tin chung cần thiết cho bất kỳ cổng thanh toán
 * nào.
 */
@Data
@Builder
public class CreatePaymentRequest {

  private UUID transactionId;
  private BigDecimal amount;
  private String orderInfo;
  private String returnUrl;
  private String ipnUrl;
  private String ipAddr;
}
