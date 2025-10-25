package com.natswarchuan.payment.demo.dto.response.wallet;

import com.natswarchuan.payment.demo.dto.response.paymentmethod.PaymentMethodSummaryResponse;
import com.natswarchuan.payment.demo.dto.response.transaction.TransactionSummaryResponse;
import com.natswarchuan.payment.demo.entity.Wallet;
import com.natswarchuan.payment.demo.interfaces.IDto;
import com.natswarchuan.payment.demo.util.DtoMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class WalletDetailResponse implements IDto<Wallet> {

  private UUID id;
  private String number;
  private BigDecimal balance;
  private String currency;
  private Integer status;
  private Instant createdAt;
  private Instant updatedAt;
  private List<PaymentMethodSummaryResponse> paymentMethods;
  private List<TransactionSummaryResponse> transactions;

  @Override
  public void fromEntity(Wallet entity) {
    this.id = entity.getId();
    this.number = entity.getNumber();
    this.balance = entity.getBalance();
    this.currency = entity.getCurrency();
    this.status = entity.getStatus();
    this.createdAt = entity.getCreatedAt();
    this.updatedAt = entity.getUpdatedAt();

    this.paymentMethods =
        DtoMapper.toDtoList(entity.getPaymentMethods(), PaymentMethodSummaryResponse::new);
  }
}
