package com.natswarchuan.payment.demo.constant;

public final class AppConfigConstant {
  private AppConfigConstant() {}

  public static final String REQUEST_ATTRIBUTE_START_TIME = "startTime";
  public static final String REDIS_OTP_PREFIX = "otp:";
  public static final String REDIS_RATE_LIMIT_PREFIX = "rate_limit:";
  public static final String REDIS_SECRET_KEY_PREFIX = "secret_key:";
  public static final String REDIS_USER_SESSION_HASH = "user_sessions";
  public static final String COMMA_DELIMITER = ",";
  public static final String UNKNOWN_IP = "unknown";
  public static final String EMAIL_AT_SIGN = "@";
  public static final String OTP_FORMAT = "000000";

  // Date and Time
  public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
  public static final String TIMEZONE_GMT7 = "Etc/GMT+7";

  // Network
  public static final String DEFAULT_IP_ADDRESS = "127.0.0.1";

  // CORS
  public static final String CORS_ALL = "*";
  public static final String CORS_CONFIG_PATH_PATTERN = "/**";

  // JSON Response keys for AuthEntryPointJwt
  public static final String JSON_KEY_STATUS = "status";
  public static final String JSON_KEY_ERROR = "error";
  public static final String JSON_KEY_MESSAGE = "message";
  public static final String JSON_KEY_PATH = "path";
}