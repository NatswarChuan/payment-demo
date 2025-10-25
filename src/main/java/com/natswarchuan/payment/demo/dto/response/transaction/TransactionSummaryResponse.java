package com.natswarchuan.payment.demo.dto.response.transaction;

import com.natswarchuan.payment.demo.entity.Transaction;
import com.natswarchuan.payment.demo.interfaces.IDto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class TransactionSummaryResponse implements IDto<Transaction> {

  private UUID id;
  private BigDecimal amount;
  private Integer type;
  private Integer status;
  private Instant createdAt;
  private String description;

  @Override
  public void fromEntity(Transaction entity) {
    this.id = entity.getId();
    this.amount = entity.getAmount();
    this.type = entity.getType();
    this.status = entity.getStatus();
    this.createdAt = entity.getCreatedAt();
    this.description = entity.getDescription();
  }
}
