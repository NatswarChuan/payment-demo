package com.natswarchuan.payment.demo.dto.request.wallet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SetPinRequest {

  @NotBlank(message = "PIN is required")
  @Size(min = 6, max = 6, message = "PIN must be 6 digits")
  private String pin;
}