package com.natswarchuan.payment.demo.util;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.lang.Nullable;

/**
 * Lớp tiện ích để làm sạch (sanitize) dữ liệu đầu vào nhằm chống lại các cuộc tấn công XSS.
 *
 * <p>Lớp này tích hợp thư viện OWASP Java HTML Sanitizer, một thư viện chuyên dụng và mạnh mẽ, để
 * đảm bảo rằng mọi dữ liệu đầu vào không an toàn đều được xử lý một cách an toàn.
 */
public final class SanitizationUtil {

  /**
   * Định nghĩa một chính sách làm sạch nghiêm ngặt.
   *
   * <p>Chính sách này được tạo ra từ {@code new HtmlPolicyBuilder().toFactory()}, không cho phép
   * bất kỳ thẻ HTML nào. Kết quả là nó sẽ loại bỏ tất cả các thẻ, chỉ giữ lại nội dung văn bản
   * thuần túy. Đây là chính sách an toàn nhất cho các trường hợp như tên người dùng, tên đầy đủ,
   * v.v., nơi không mong muốn có bất kỳ định dạng HTML nào.
   */
  private static final PolicyFactory POLICY = new HtmlPolicyBuilder().toFactory();

  private SanitizationUtil() {}

  /**
   * Làm sạch một chuỗi đầu vào để loại bỏ hoàn toàn các thẻ HTML và script tiềm ẩn nguy hiểm.
   *
   * @param unsafeString chuỗi đầu vào có thể chứa mã độc.
   * @return chuỗi đã được làm sạch, chỉ còn lại văn bản thuần túy, hoặc null nếu đầu vào là null.
   */
  public static String sanitize(@Nullable final String unsafeString) {
    if (unsafeString == null) {
      return null;
    }
    return POLICY.sanitize(unsafeString);
  }
}
