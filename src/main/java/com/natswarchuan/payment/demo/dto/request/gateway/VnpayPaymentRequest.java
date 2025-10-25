package com.natswarchuan.payment.demo.dto.request.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.natswarchuan.payment.demo.constant.VnpayConstant;
import com.natswarchuan.payment.demo.util.VnpayUtil;
import java.util.Map;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Đại diện cho một đối tượng yêu cầu cơ sở cho các lệnh gọi API VNPAY. Lớp này chứa các trường phổ
 * biến cho các yêu cầu VNPAY.
 */
@Data
@SuperBuilder
public class VnpayPaymentRequest {
  @JsonProperty(VnpayConstant.VNP_VERSION)
  private String vnp_Version;

  @JsonProperty(VnpayConstant.VNP_COMMAND)
  private String vnp_Command;

  @JsonProperty(VnpayConstant.VNP_TMN_CODE)
  private String vnp_TmnCode;

  @JsonProperty(VnpayConstant.VNP_AMOUNT)
  private String vnp_Amount;

  @JsonProperty(VnpayConstant.VNP_CREATE_DATE)
  private String vnp_CreateDate;

  @JsonProperty(VnpayConstant.VNP_CURR_CODE)
  private String vnp_CurrCode;

  @JsonProperty(VnpayConstant.VNP_IP_ADDR)
  private String vnp_IpAddr;

  @JsonProperty(VnpayConstant.VNP_LOCALE)
  private String vnp_Locale;

  @JsonProperty(VnpayConstant.VNP_ORDER_INFO)
  private String vnp_OrderInfo;

  @JsonProperty(VnpayConstant.VNP_ORDER_TYPE)
  private String vnp_OrderType;

  @JsonProperty(VnpayConstant.VNP_RETURN_URL)
  private String vnp_ReturnUrl;

  @JsonProperty(VnpayConstant.VNP_TXN_REF)
  private String vnp_TxnRef;

  @JsonProperty(VnpayConstant.VNP_SECURE_HASH)
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
}
