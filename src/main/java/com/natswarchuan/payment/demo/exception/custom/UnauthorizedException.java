package com.natswarchuan.payment.demo.exception.custom;

import com.natswarchuan.payment.demo.exception.HttpException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends HttpException {
  public UnauthorizedException(String message) {
    super(HttpStatus.UNAUTHORIZED, message);
  }

  public UnauthorizedException(String format, Object... args) {
    super(HttpStatus.UNAUTHORIZED, format, args);
  }
}
