package com.natswarchuan.payment.demo.interfaces.services;

import jakarta.servlet.http.HttpServletRequest;

public interface IOtpService {

  /**
   * Tạo và gửi mã OTP đến một định danh (email hoặc số điện thoại).
   *
   * @param identifier định danh của người nhận.
   * @param request đối tượng HttpServletRequest để lấy thông tin như địa chỉ IP cho việc giới hạn
   * tần suất.
   */
  void sendOtp(String identifier, HttpServletRequest request);

  /**
   * Xác thực một mã OTP.
   *
   * @param identifier định danh đã được dùng để gửi OTP.
   * @param otp mã OTP cần xác thực.
   * @return {@code true} nếu mã OTP hợp lệ, ngược lại {@code false}.
   */
  boolean verifyOtp(String identifier, String otp);
}
