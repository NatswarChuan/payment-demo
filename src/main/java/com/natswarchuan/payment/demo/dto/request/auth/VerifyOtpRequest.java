package com.natswarchuan.payment.demo.dto.request.auth;

import com.natswarchuan.payment.demo.constant.ValidationConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyOtpRequest {
  @NotBlank(message = ValidationConstant.IDENTIFIER_REQUIRED)
  private String identifier;

  @NotBlank(message = ValidationConstant.OTP_REQUIRED)
  @Size(min = 6, max = 6, message = ValidationConstant.OTP_LENGTH)
  private String otp;
}
