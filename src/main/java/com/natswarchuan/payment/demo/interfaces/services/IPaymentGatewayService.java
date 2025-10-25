package com.natswarchuan.payment.demo.interfaces.services;

import com.natswarchuan.payment.demo.dto.request.gateway.CreatePaymentRequest;
import com.natswarchuan.payment.demo.dto.response.gateway.CreatePaymentResponse;
import com.natswarchuan.payment.demo.dto.response.gateway.PayoutResponse;
import com.natswarchuan.payment.demo.dto.response.gateway.ProcessIpnResponse;
import com.natswarchuan.payment.demo.entity.Transaction;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Interface chung định nghĩa các hợp đồng cho một dịch vụ cổng thanh toán.
 *
 * <p>Cung cấp một lớp trừu tượng cho các hoạt động cốt lõi mà mọi cổng thanh toán phải hỗ trợ, như
 * tạo thanh toán, xử lý chi trả (payout), và xử lý thông báo IPN. Điều này cho phép hệ thống dễ
 * dàng tích hợp và chuyển đổi giữa các nhà cung cấp khác nhau (ví dụ: VNPAY, Momo).
 */
public interface IPaymentGatewayService {

  /**
   * Khởi tạo một yêu cầu thanh toán (ví dụ: tạo URL để chuyển hướng người dùng đến trang thanh
   * toán).
   *
   * @param request dữ liệu cần thiết để tạo thanh toán, bao gồm số tiền, thông tin đơn hàng, v.v.
   * @return một đối tượng {@code CreatePaymentResponse} chứa thông tin cần thiết cho bước tiếp theo
   *     của quy trình thanh toán (ví dụ: URL thanh toán).
   */
  CreatePaymentResponse createPayment(CreatePaymentRequest request);

  /**
   * Khởi tạo một yêu cầu chi trả (payout/rút tiền) đến cổng thanh toán.
   *
   * @param transaction giao dịch rút tiền chứa thông tin cần thiết như số tiền, tài khoản thụ
   *     hưởng.
   * @return một đối tượng {@code PayoutResponse} chứa kết quả từ cổng thanh toán, cho biết yêu cầu
   *     đã được chấp nhận hay từ chối.
   */
  PayoutResponse initiatePayout(Transaction transaction);

  /**
   * Xử lý một thông báo thanh toán tức thời (Instant Payment Notification - IPN) từ cổng thanh
   * toán.
   *
   * <p>Phương thức này chịu trách nhiệm xác thực tính toàn vẹn của dữ liệu IPN (ví dụ: kiểm tra chữ
   * ký), phân tích cú pháp và chuyển đổi thành một đối tượng chuẩn hóa.
   *
   * @param request đối tượng HttpServletRequest chứa dữ liệu IPN do cổng thanh toán gửi đến.
   * @return một đối tượng {@code ProcessIpnResponse} đã được chuẩn hóa, chứa các thông tin quan
   *     trọng như ID giao dịch, trạng thái, và số tiền.
   */
  ProcessIpnResponse processIpn(HttpServletRequest request);

  /**
   * Lấy tên định danh của nhà cung cấp cổng thanh toán.
   *
   * <p>Tên này được sử dụng trong hệ thống để phân biệt giữa các dịch vụ cổng thanh toán khác nhau
   * (ví dụ: "VNPAY", "MOMO").
   *
   * @return chuỗi tên nhà cung cấp.
   */
  String getProviderName();
}
