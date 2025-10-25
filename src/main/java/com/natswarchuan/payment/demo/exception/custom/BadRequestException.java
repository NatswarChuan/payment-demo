package com.natswarchuan.payment.demo.exception.custom;

import com.natswarchuan.payment.demo.exception.HttpException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends HttpException {
  public BadRequestException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }

  public BadRequestException(String format, Object... args) {
    super(HttpStatus.BAD_REQUEST, format, args);
  }
}
