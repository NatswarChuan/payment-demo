package com.natswarchuan.payment.demo.util;

import com.natswarchuan.payment.demo.constant.AppConfigConstant;
import com.natswarchuan.payment.demo.constant.SecurityConstant;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

/** Lớp tiện ích chứa các hàm hỗ trợ liên quan đến HTTP request. */
public final class HttpUtil {

  private HttpUtil() {}

  /**
   * Lấy địa chỉ IP thực của client từ request, có xử lý trường hợp đi qua proxy.
   *
   * <p>Phương thức này ưu tiên lấy IP từ header {@code X-Forwarded-For} (XFF), vốn thường được các
   * reverse proxy, load balancer thêm vào để chuyển tiếp IP gốc của client. Nếu header XFF không
   * tồn tại hoặc rỗng, phương thức sẽ lấy IP từ {@code request.getRemoteAddr()}.
   *
   * @param request đối tượng HttpServletRequest không được null.
   * @return một chuỗi chứa địa chỉ IP của client.
   */
  public static String getClientIp(final HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(SecurityConstant.HEADER_X_FORWARDED_FOR))
        .filter(ip -> !ip.isEmpty() && !AppConfigConstant.UNKNOWN_IP.equalsIgnoreCase(ip))
        .map(ip -> ip.split(AppConfigConstant.COMMA_DELIMITER)[0].trim())
        .orElse(request.getRemoteAddr());
  }
}
