package com.natswarchuan.payment.demo.exception.custom;

import com.natswarchuan.payment.demo.exception.HttpException;
import org.springframework.http.HttpStatus;

/**
 * Lớp ngoại lệ (exception) được ném ra khi một yêu cầu vượt quá giới hạn tần suất (rate limit)
 * đã định cấu hình.
 * <p>
 * Lớp này mở rộng từ {@link HttpException} và mặc định trả về mã trạng thái HTTP 429 (Too Many
 * Requests).
 */
public class RateLimitExceededException extends HttpException {

  /**
   * Khởi tạo một RateLimitExceededException với một thông báo cụ thể.
   *
   * @param message Thông báo lỗi chi tiết.
   */
  public RateLimitExceededException(String message) {
    super(HttpStatus.TOO_MANY_REQUESTS, message);
  }

  /**
   * Khởi tạo một RateLimitExceededException với một chuỗi định dạng và các đối số.
   *
   * @param format Chuỗi định dạng cho thông báo lỗi.
   * @param args   Các đối số được tham chiếu bởi các mã định dạng trong chuỗi format.
   */
  public RateLimitExceededException(String format, Object... args) {
    super(HttpStatus.TOO_MANY_REQUESTS, format, args);
  }
}