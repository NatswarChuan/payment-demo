package com.natswarchuan.payment.demo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Lớp tiện ích để xác thực và xử lý các định danh người dùng như email và số điện thoại. */
public final class IdentifierValidator {

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile(
          "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

  private static final Pattern PHONE_PATTERN = Pattern.compile("^(0|84|\\+84)?([1-9][0-9]{8})$");

  private IdentifierValidator() {}

  /**
   * Kiểm tra một chuỗi có phải là định dạng email hợp lệ hay không.
   *
   * @param identifier Chuỗi cần kiểm tra.
   * @return {@code true} nếu là email hợp lệ, ngược lại {@code false}.
   */
  public static boolean isEmail(String identifier) {
    if (identifier == null) {
      return false;
    }
    return EMAIL_PATTERN.matcher(identifier).matches();
  }

  /**
   * Kiểm tra một chuỗi có phải là định dạng số điện thoại Việt Nam hợp lệ hay không.
   *
   * @param identifier Chuỗi cần kiểm tra.
   * @return {@code true} nếu là số điện thoại hợp lệ, ngược lại {@code false}.
   */
  public static boolean isPhoneNumber(String identifier) {
    if (identifier == null) {
      return false;
    }
    return PHONE_PATTERN.matcher(identifier).matches();
  }

  /**
   * Kiểm tra xem một chuỗi có phải là email hoặc số điện thoại hợp lệ hay không.
   *
   * @param identifier Chuỗi cần kiểm tra.
   * @return {@code true} nếu hợp lệ, ngược lại {@code false}.
   */
  public static boolean isValid(String identifier) {
    return isEmail(identifier) || isPhoneNumber(identifier);
  }

  /**
   * Chuẩn hóa số điện thoại Việt Nam sang định dạng E.164 (+84xxxxxxxxx).
   *
   * <p>Định dạng này là tiêu chuẩn quốc tế và thường được yêu cầu bởi các nhà cung cấp dịch vụ SMS
   * như Twilio.
   *
   * @param phoneNumber Số điện thoại cần chuẩn hóa.
   * @return Số điện thoại ở định dạng E.164, hoặc {@code null} nếu không hợp lệ.
   */
  public static String normalizePhoneNumber(String phoneNumber) {
    if (phoneNumber == null) {
      return null;
    }
    Matcher matcher = PHONE_PATTERN.matcher(phoneNumber);
    if (matcher.matches()) {
      return "+84" + matcher.group(2);
    }
    return null;
  }
}
