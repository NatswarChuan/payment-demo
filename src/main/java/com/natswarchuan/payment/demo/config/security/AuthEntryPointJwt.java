package com.natswarchuan.payment.demo.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.natswarchuan.payment.demo.constant.AppConfigConstant;
import com.natswarchuan.payment.demo.constant.LogConstant;
import com.natswarchuan.payment.demo.constant.MessageConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Lớp xử lý các lỗi xác thực (authentication failure) trong Spring Security.
 *
 * <p>Lớp này được kích hoạt khi một người dùng chưa xác thực cố gắng truy cập vào một tài nguyên
 * được bảo vệ. Nó sẽ trả về một phản hồi lỗi HTTP 401 Unauthorized theo định dạng JSON chuẩn.
 */
@Component
@Slf4j
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

  /**
   * Ghi đè phương thức commence để tùy chỉnh phản hồi lỗi.
   *
   * @param request Yêu cầu HTTP đã gây ra lỗi xác thực.
   * @param response Phản hồi HTTP để gửi lại cho client.
   * @param authException Ngoại lệ xác thực đã được ném ra.
   * @throws IOException Nếu có lỗi I/O xảy ra khi ghi vào response.
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {
    log.error(LogConstant.UNAUTHORIZED_ERROR, authException.getMessage());

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    final Map<String, Object> body = new HashMap<>();
    body.put(AppConfigConstant.JSON_KEY_STATUS, HttpServletResponse.SC_UNAUTHORIZED);
    body.put(AppConfigConstant.JSON_KEY_ERROR, MessageConstant.Auth.UNAUTHORIZED);
    body.put(AppConfigConstant.JSON_KEY_MESSAGE, MessageConstant.Auth.UNAUTHORIZED);
    body.put(AppConfigConstant.JSON_KEY_PATH, request.getServletPath());

    final ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getOutputStream(), body);
  }
}
