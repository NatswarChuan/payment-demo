package com.natswarchuan.payment.demo.dto.response.gateway;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayoutResponse {
  private boolean isSuccess;
  private String message;
  private String providerTransactionId;
}
