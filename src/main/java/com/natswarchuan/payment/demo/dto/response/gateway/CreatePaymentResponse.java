package com.natswarchuan.payment.demo.dto.response.gateway;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePaymentResponse {

  private String paymentUrl;
  private String qrCodeData;
}
