package com.natswarchuan.payment.demo.exception;

import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.dto.response.HttpResponseApi;
import com.natswarchuan.payment.demo.exception.custom.RateLimitExceededException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Bộ xử lý ngoại lệ toàn cục, chịu trách nhiệm bắt và chuyển đổi các ngoại lệ thành các phản hồi
 * HTTP chuẩn.
 * <p>
 * Lớp này sử dụng {@code @ControllerAdvice} để áp dụng cho tất cả các controller trong ứng dụng.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Xử lý các ngoại lệ kiểu {@link HttpException}.
   *
   * @param ex Ngoại lệ HttpException được ném ra.
   * @return Một {@code ResponseEntity} chứa thông tin lỗi và mã trạng thái HTTP tương ứng.
   */
  @ExceptionHandler(HttpException.class)
  public ResponseEntity<HttpResponseApi<Object>> handleHttpException(HttpException ex) {
    log.error("HttpException occurred: {}", ex.getMessage());
    HttpResponseApi<Object> responseBody = new HttpResponseApi<>(ex.getStatus(), ex.getMessage());
    return new ResponseEntity<>(responseBody, ex.getStatus());
  }

  /**
   * Xử lý ngoại lệ {@link RateLimitExceededException} khi người dùng gửi quá nhiều yêu cầu.
   *
   * @param ex Ngoại lệ RateLimitExceededException được ném ra.
   * @return Một {@code ResponseEntity} với mã trạng thái 429 (Too Many Requests).
   */
  @ExceptionHandler(RateLimitExceededException.class)
  public ResponseEntity<HttpResponseApi<Object>> handleRateLimitExceededException(
      RateLimitExceededException ex) {
    log.warn("Rate limit exceeded: {}", ex.getMessage());
    HttpResponseApi<Object> responseBody = new HttpResponseApi<>(ex.getStatus(), ex.getMessage());
    return new ResponseEntity<>(responseBody, ex.getStatus());
  }


  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    log.error("Validation error: {}", ex.getMessage());
    String errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(MessageConstant.General.VALIDATION_ERROR_SEPARATOR));

    HttpResponseApi<Object> responseBody = new HttpResponseApi<>(HttpStatus.BAD_REQUEST, errors);
    return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
  }

  /**
   * Bắt và xử lý tất cả các ngoại lệ không được xử lý cụ thể khác.
   *
   * @param ex      Ngoại lệ không mong muốn.
   * @param request WebRequest hiện tại.
   * @return Một {@code ResponseEntity} với mã trạng thái 500 (Internal Server Error).
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<HttpResponseApi<Object>> handleAllUncaughtException(
      Exception ex, WebRequest request) {
    log.error("An unexpected error occurred: ", ex);
    HttpResponseApi<Object> responseBody =
        new HttpResponseApi<>(
            HttpStatus.INTERNAL_SERVER_ERROR, MessageConstant.General.UNEXPECTED_ERROR);
    return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}