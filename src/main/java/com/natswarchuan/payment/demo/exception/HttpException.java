package com.natswarchuan.payment.demo.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = false)
public class HttpException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final HttpStatus status;
  private final String message;

  public HttpException(HttpStatus status, String message) {
    super(message);
    this.status = status;
    this.message = message;
  }

  public HttpException(HttpStatus status, String format, Object... args) {
    super(String.format(format, args));
    this.status = status;
    this.message = super.getMessage();
  }
}
