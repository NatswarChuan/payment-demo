package com.natswarchuan.payment.demo.helper;

import com.natswarchuan.payment.demo.constant.LogConstant;
import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.constant.TransactionConstant;
import com.natswarchuan.payment.demo.constant.VnpayConstant;
import com.natswarchuan.payment.demo.dto.response.gateway.ProcessIpnResponse;
import com.natswarchuan.payment.demo.exception.custom.BadRequestException;
import com.natswarchuan.payment.demo.util.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Lớp helper chứa các logic phụ trợ cho {@link com.natswarchuan.payment.demo.service.VnpayService}.
 *
 * <p>Lớp này chịu trách nhiệm xử lý các tác vụ phức tạp và đặc thù của VNPAY như phân tích và xác
 * thực thông báo IPN, giúp lớp service chính tập trung vào việc tích hợp với hệ thống.
 */
@Component
@Slf4j
public class VnpayHelper {

  @Value("${vnpay.hash-secret}")
  private String hashSecret;

  /**
   * Phương thức chung để xử lý thông báo IPN (Instant Payment Notification) từ VNPAY.
   *
   * <p>Nó thực hiện việc phân tích cú pháp, xác thực checksum và chuyển đổi dữ liệu từ request
   * thành một đối tượng {@link ProcessIpnResponse} chuẩn hóa. Đây là nơi tập trung logic xử lý IPN
   * để tránh lặp code.
   *
   * @param request Đối tượng HttpServletRequest chứa dữ liệu IPN từ VNPAY.
   * @return Một đối tượng {@link ProcessIpnResponse} chứa thông tin đã được xử lý.
   * @throws BadRequestException nếu chữ ký không hợp lệ hoặc có lỗi xảy ra.
   */
  public ProcessIpnResponse processGenericIpn(HttpServletRequest request) {
    try {
      Map<String, String> ipnParams = parseAndValidateIpnRequest(request);

      String responseCode = ipnParams.get(VnpayConstant.VNP_RESPONSE_CODE);
      UUID transactionId = UUID.fromString(ipnParams.get(VnpayConstant.VNP_TXN_REF));
      String providerTransactionId = ipnParams.get(VnpayConstant.VNP_TRANSACTION_NO);
      BigDecimal amount =
          new BigDecimal(ipnParams.get(VnpayConstant.VNP_AMOUNT)).divide(new BigDecimal(100));

      Integer status =
          VnpayConstant.RESPONSE_SUCCESS_CODE.equals(responseCode)
              ? TransactionConstant.Status.COMPLETED
              : TransactionConstant.Status.FAILED;

      String message = String.format(MessageConstant.Vnpay.IPN_PROCESSED_WITH_CODE, responseCode);

      return ProcessIpnResponse.builder()
          .transactionId(transactionId)
          .providerTransactionId(providerTransactionId)
          .amount(amount)
          .transactionStatus(status)
          .message(message)
          .build();
    } catch (Exception e) {
      log.error(LogConstant.VNPAY_IPN_PROCESSING_ERROR, e);
      throw new BadRequestException(MessageConstant.Vnpay.IPN_PROCESSING_ERROR);
    }
  }

  /**
   * Trích xuất các tham số từ request IPN, xác thực chữ ký và trả về một Map các tham số.
   *
   * @param request Đối tượng HttpServletRequest từ VNPAY.
   * @return Một Map chứa các tham số từ request nếu chữ ký hợp lệ.
   * @throws BadRequestException nếu chữ ký không hợp lệ.
   */
  private Map<String, String> parseAndValidateIpnRequest(HttpServletRequest request) {
    Map<String, String> fields = new HashMap<>();
    for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
      String fieldName = params.nextElement();
      String fieldValue = request.getParameter(fieldName);
      if ((fieldValue != null) && (fieldValue.length() > 0)) {
        fields.put(fieldName, fieldValue);
      }
    }

    String vnp_SecureHash = fields.get(VnpayConstant.VNP_SECURE_HASH);
    fields.remove(VnpayConstant.VNP_SECURE_HASH);

    String signValue = VnpayUtil.hashAllFields(fields, hashSecret);

    if (signValue.equals(vnp_SecureHash)) {
      return fields;
    } else {
      log.error(LogConstant.VNPAY_IPN_INVALID_CHECKSUM);
      throw new BadRequestException(MessageConstant.Payment.INVALID_SIGNATURE);
    }
  }
}
