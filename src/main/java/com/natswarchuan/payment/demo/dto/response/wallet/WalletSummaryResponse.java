package com.natswarchuan.payment.demo.dto.response.wallet;

import com.natswarchuan.payment.demo.entity.Wallet;
import com.natswarchuan.payment.demo.interfaces.IDto;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class WalletSummaryResponse implements IDto<Wallet> {

  private UUID id;
  private String number;
  private BigDecimal balance;
  private String currency;
  private Integer status;

  @Override
  public void fromEntity(Wallet entity) {
    this.id = entity.getId();
    this.number = entity.getNumber();
    this.balance = entity.getBalance();
    this.currency = entity.getCurrency();
    this.status = entity.getStatus();
  }
}
