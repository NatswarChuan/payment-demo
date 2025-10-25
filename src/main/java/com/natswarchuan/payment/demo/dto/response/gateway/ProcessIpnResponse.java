package com.natswarchuan.payment.demo.dto.response.gateway;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessIpnResponse {

  private UUID transactionId;
  private String providerTransactionId;
  private BigDecimal amount;
  private Integer transactionStatus;
  private String message;
}
