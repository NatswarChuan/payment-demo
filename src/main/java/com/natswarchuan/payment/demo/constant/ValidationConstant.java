package com.natswarchuan.payment.demo.constant;

public final class ValidationConstant {
  private ValidationConstant() {}

  public static final String IDENTIFIER_REQUIRED = "Email hoặc số điện thoại là bắt buộc";
  public static final String FULL_NAME_REQUIRED = "Họ và tên là bắt buộc";
  public static final String OTP_REQUIRED = "Mã OTP là bắt buộc";
  public static final String OTP_LENGTH = "Mã OTP phải có 6 chữ số";
  public static final String REFRESH_TOKEN_REQUIRED = "Refresh token là bắt buộc";
  public static final String AMOUNT_REQUIRED = "Số tiền là bắt buộc";
  public static final String AMOUNT_POSITIVE = "Số tiền phải là số dương";
  public static final String PAYMENT_METHOD_ID_REQUIRED = "ID phương thức thanh toán là bắt buộc";
  public static final String RECIPIENT_WALLET_NUMBER_REQUIRED = "Số ví người nhận là bắt buộc";
  public static final String PIN_REQUIRED = "Mã PIN là bắt buộc";
  public static final String PIN_LENGTH = "Mã PIN phải có 6 chữ số";
  public static final String SECRET_KEY_REQUIRED = "Khóa bí mật là bắt buộc";

  // E-wallet Linking
  public static final String EWALLET_PROVIDER_REQUIRED = "Nhà cung cấp ví điện tử là bắt buộc";
  public static final String EWALLET_ACCOUNT_REQUIRED = "Số điện thoại hoặc định danh ví là bắt buộc";
  public static final String EWALLET_ACCOUNT_NAME_REQUIRED = "Tên chủ ví điện tử là bắt buộc";
}
