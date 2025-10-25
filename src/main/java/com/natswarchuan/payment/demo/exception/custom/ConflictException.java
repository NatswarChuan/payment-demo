package com.natswarchuan.payment.demo.exception.custom;

import com.natswarchuan.payment.demo.exception.HttpException;
import org.springframework.http.HttpStatus;

public class ConflictException extends HttpException {
  public ConflictException(String message) {
    super(HttpStatus.CONFLICT, message);
  }

  public ConflictException(String format, Object... args) {
    super(HttpStatus.CONFLICT, format, args);
  }
}
