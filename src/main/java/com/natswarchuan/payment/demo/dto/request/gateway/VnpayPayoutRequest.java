package com.natswarchuan.payment.demo.dto.request.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.natswarchuan.payment.demo.constant.VnpayConstant;
import com.natswarchuan.payment.demo.util.VnpayUtil;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Lớp DTO (Data Transfer Object) đại diện cho một yêu cầu rút tiền (payout) gửi đến VNPAY.
 *
 * <p>Chứa tất cả các trường dữ liệu cần thiết theo tài liệu của VNPAY cho API rút tiền.
 */
@Data
@Builder
@Slf4j
public class VnpayPayoutRequest {

  @JsonProperty("vnp_RequestId")
  private String vnp_RequestId;

  @JsonProperty("vnp_Version")
  private String vnp_Version;

  @JsonProperty("vnp_Command")
  private String vnp_Command;

  @JsonProperty("vnp_TmnCode")
  private String vnp_TmnCode;

  @JsonProperty("vnp_TxnRef")
  private String vnp_TxnRef;

  @JsonProperty("vnp_OrderInfo")
  private String vnp_OrderInfo;

  @JsonProperty("vnp_Amount")
  private String vnp_Amount;

  @JsonProperty("vnp_CreateDate")
  private String vnp_CreateDate;

  @JsonProperty("vnp_IpAddr")
  private String vnp_IpAddr;

  @JsonProperty("vnp_BankCode")
  private String vnp_BankCode;

  @JsonProperty("vnp_AccountNo")
  private String vnp_AccountNo;

  @JsonProperty("vnp_AccountName")
  private String vnp_AccountName;

  @Builder.Default
  @JsonProperty("vnp_PayMethod")
  private String vnp_PayMethod = VnpayConstant.PAY_METHOD_ACCOUNT;

  @JsonProperty("vnp_SecureHash")
  private String vnp_SecureHash;

  /**
   * Chuyển đổi các trường của đối tượng thành một map đã được sắp xếp.
   *
   * <p>Phương thức này ủy quyền việc chuyển đổi cho lớp tiện ích {@link VnpayUtil} để tái sử dụng
   * logic và tuân thủ nguyên tắc DRY.
   *
   * @return Một Map đã sắp xếp chứa tên trường (từ @JsonProperty) và giá trị chuỗi của chúng.
   */
  public Map<String, String> toMap() {
    return VnpayUtil.objectToMap(this);
  }

  /**
   * Tạo ra một chuỗi đại diện cho đối tượng với các thông tin nhạy cảm đã được che đi.
   *
   * <p>Phương thức này hữu ích cho việc ghi log an toàn, tránh làm lộ thông tin tài khoản người
   * dùng.
   *
   * @return Một chuỗi đã được che thông tin nhạy cảm.
   */
  public String toMaskedString() {
    return "VnpayPayoutRequest("
        + "vnp_RequestId="
        + vnp_RequestId
        + ", vnp_Version="
        + vnp_Version
        + ", vnp_Command="
        + vnp_Command
        + ", vnp_TmnCode="
        + vnp_TmnCode
        + ", vnp_TxnRef="
        + vnp_TxnRef
        + ", vnp_OrderInfo="
        + vnp_OrderInfo
        + ", vnp_Amount="
        + vnp_Amount
        + ", vnp_CreateDate="
        + vnp_CreateDate
        + ", vnp_IpAddr="
        + vnp_IpAddr
        + ", vnp_BankCode="
        + vnp_BankCode
        + ", vnp_AccountNo=****" 
        + ", vnp_AccountName=****" 
        + ", vnp_PayMethod="
        + vnp_PayMethod
        + ", vnp_SecureHash="
        + vnp_SecureHash
        + ')';
  }
}
