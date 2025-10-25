package com.natswarchuan.payment.demo.constant;

public final class ApiConstant {
  private ApiConstant() {}

  public static final String API_V1_PREFIX = "/api/v1";
  public static final String AUTH_ENDPOINT = API_V1_PREFIX + "/auth";
  public static final String USERS_ENDPOINT = API_V1_PREFIX + "/users";
  public static final String WALLETS_ENDPOINT = API_V1_PREFIX + "/wallets";
  public static final String TRANSACTIONS_ENDPOINT = API_V1_PREFIX + "/transactions";
  public static final String PAYMENTS_ENDPOINT = API_V1_PREFIX + "/payments";
  public static final String PAYMENT_METHODS_ENDPOINT = API_V1_PREFIX + "/payment-methods";
  public static final String BANKS_ENDPOINT = API_V1_PREFIX + "/banks";
  public static final String PROVIDERS_ENDPOINT = API_V1_PREFIX + "/providers";

  public static final String REGISTER_OTP_REQUEST = "/register/otp/request";
  public static final String REGISTER_OTP_VERIFY = "/register/otp/verify";
  public static final String LOGIN_OTP_REQUEST = "/login/otp/request";
  public static final String LOGIN_OTP_VERIFY = "/login/otp/verify";
  public static final String REFRESH_TOKEN = "/refresh-token";
  public static final String LOGOUT = "/logout";
  public static final String USERS_ME = "/me";
  public static final String USERS_ME_EKYC_COMPLETE = "/me/ekyc/complete";
  public static final String TRANSACTIONS_TRANSFER = "/transfer";
  public static final String TRANSACTIONS_TRANSFER_SECRET_KEY = "/transfer/secret-key";

  public static final String PAYMENTS_DEPOSIT = "/deposit/{provider}";
  public static final String PAYMENTS_WITHDRAW = "/withdraw/{provider}";
  public static final String PAYMENTS_IPN = "/ipn/{provider}";
  public static final String PAYMENT_METHODS_EWALLET = "/ewallets";

  public static final String SWAGGER_UI_PATH = "/swagger-ui/**";
  public static final String API_DOCS_PATH = "/v3/api-docs/**";
}

