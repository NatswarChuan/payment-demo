package com.natswarchuan.payment.demo.dto.response.auth;

import com.natswarchuan.payment.demo.dto.response.user.UserDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

  private String accessToken;
  private String refreshToken;
  private UserDetailResponse user;
}
