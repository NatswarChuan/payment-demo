package com.natswarchuan.payment.demo.dto.response.paymentmethod;

import com.natswarchuan.payment.demo.entity.PaymentMethod;
import com.natswarchuan.payment.demo.interfaces.IDto;
import java.util.UUID;
import lombok.Data;

@Data
public class PaymentMethodSummaryResponse implements IDto<PaymentMethod> {

  private UUID id;
  private Integer type;
  private String provider;
  private String accountNumber;
  private String accountName;
  private Boolean isDefault;

  @Override
  public void fromEntity(PaymentMethod entity) {
    this.id = entity.getId();
    this.type = entity.getType();
    this.provider = entity.getProvider();
    this.accountNumber = entity.getAccountNumber();
    this.accountName = entity.getAccountName();
    this.isDefault = entity.isDefault();
  }
}
