package com.natswarchuan.payment.demo.interfaces.services;

/**
 * Interface định nghĩa các hợp đồng cho một dịch vụ giới hạn tần suất yêu cầu (rate limiting).
 *
 * <p>Dịch vụ này cung cấp chức năng để kiểm soát số lượng yêu cầu cho một hành động cụ thể (được
 * định danh bằng một khóa) trong một khoảng thời gian nhất định, nhằm ngăn chặn lạm dụng và các
 * cuộc tấn công từ chối dịch vụ.
 */
public interface IRateLimiterService {

  /**
   * Kiểm tra xem một yêu cầu với khóa được cung cấp có được phép thực hiện hay không.
   *
   * <p>Phương thức này sẽ theo dõi số lần yêu cầu cho khóa và trả về {@code false} nếu vượt quá
   * giới hạn đã cấu hình.
   *
   * @param key một chuỗi định danh duy nhất cho hành động cần giới hạn (ví dụ: email của người dùng
   *     khi yêu cầu OTP, hoặc địa chỉ IP).
   * @param ipAddress địa chỉ IP của người yêu cầu, có thể được sử dụng như một yếu tố phụ để giới
   *     hạn.
   * @return {@code true} nếu yêu cầu được cho phép, ngược lại {@code false}.
   */
  boolean isAllowed(String key, String ipAddress);
}
