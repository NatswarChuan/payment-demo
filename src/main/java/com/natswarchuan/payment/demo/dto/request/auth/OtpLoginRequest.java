package com.natswarchuan.payment.demo.dto.request.auth;

import com.natswarchuan.payment.demo.constant.ValidationConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpLoginRequest {

  @NotBlank(message = ValidationConstant.IDENTIFIER_REQUIRED)
  private String identifier;
}
