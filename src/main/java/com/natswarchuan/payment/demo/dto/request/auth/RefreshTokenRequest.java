package com.natswarchuan.payment.demo.dto.request.auth;

import com.natswarchuan.payment.demo.constant.ValidationConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

  @NotBlank(message = ValidationConstant.REFRESH_TOKEN_REQUIRED)
  private String refreshToken;
}
