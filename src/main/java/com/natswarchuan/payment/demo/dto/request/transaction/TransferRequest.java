package com.natswarchuan.payment.demo.dto.request.transaction;

import com.natswarchuan.payment.demo.constant.ValidationConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class TransferRequest {

  @NotBlank(message = ValidationConstant.RECIPIENT_WALLET_NUMBER_REQUIRED)
  private String recipientWalletNumber;

  @NotNull(message = ValidationConstant.AMOUNT_REQUIRED)
  @Positive(message = ValidationConstant.AMOUNT_POSITIVE)
  private BigDecimal amount;

  private String description;

  @NotBlank(message = ValidationConstant.PIN_REQUIRED)
  @Size(min = 6, max = 6, message = "PIN must be 6 digits")
  private String pin;

  @NotBlank(message = ValidationConstant.SECRET_KEY_REQUIRED)
  private String secretKey;
}