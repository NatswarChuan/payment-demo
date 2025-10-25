package com.natswarchuan.payment.demo.dto.response.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VnpayApiResponse {

  @JsonProperty("vnp_ResponseCode")
  private String responseCode;

  @JsonProperty("vnp_Message")
  private String message;

  @JsonProperty("vnp_TransactionNo")
  private String transactionNo;

  @JsonProperty("vnp_TxnRef")
  private String txnRef;

  @JsonProperty("vnp_SecureHash")
  private String secureHash;
}
