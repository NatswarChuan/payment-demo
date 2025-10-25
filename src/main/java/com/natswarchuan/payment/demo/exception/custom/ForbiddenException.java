package com.natswarchuan.payment.demo.exception.custom;

import com.natswarchuan.payment.demo.exception.HttpException;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends HttpException {
  public ForbiddenException(String message) {
    super(HttpStatus.FORBIDDEN, message);
  }

  public ForbiddenException(String format, Object... args) {
    super(HttpStatus.FORBIDDEN, format, args);
  }
}
