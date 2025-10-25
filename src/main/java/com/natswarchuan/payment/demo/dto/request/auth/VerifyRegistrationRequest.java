package com.natswarchuan.payment.demo.dto.request.auth;

import com.natswarchuan.payment.demo.constant.UserConstant;
import com.natswarchuan.payment.demo.constant.ValidationConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyRegistrationRequest {
  @NotBlank(message = ValidationConstant.IDENTIFIER_REQUIRED)
  private String identifier;

  @NotBlank(message = ValidationConstant.FULL_NAME_REQUIRED)
  private String fullName;

  private String nickName;

  @NotBlank(message = ValidationConstant.OTP_REQUIRED)
  @Size(min = 6, max = 6, message = ValidationConstant.OTP_LENGTH)
  private String otp;

  private Integer gender = UserConstant.Gender.OTHER;
}
