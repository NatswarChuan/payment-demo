package com.natswarchuan.payment.demo.exception.custom;

import com.natswarchuan.payment.demo.exception.HttpException;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends HttpException {
  public ResourceNotFoundException(String message) {
    super(HttpStatus.NOT_FOUND, message);
  }

  public ResourceNotFoundException(String format, Object... args) {
    super(HttpStatus.NOT_FOUND, format, args);
  }
}
