package com.natswarchuan.payment.demo.dto.request.paymentmethod;

import com.natswarchuan.payment.demo.constant.PaymentMethodConstant;
import com.natswarchuan.payment.demo.entity.PaymentMethod;
import com.natswarchuan.payment.demo.interfaces.IDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePaymentMethodRequest implements IDto<PaymentMethod> {

  @NotNull(message = "Loại phương thức thanh toán là bắt buộc")
  private Integer type = PaymentMethodConstant.Type.BANK_ACCOUNT;

  @NotBlank(message = "Nhà cung cấp (mã ngân hàng) là bắt buộc")
  private String provider; 

  @NotBlank(message = "Tên chủ tài khoản là bắt buộc")
  private String accountName;

  @NotBlank(message = "Số tài khoản là bắt buộc")
  private String accountNumber;

  @Override
  public PaymentMethod toEntity() {
    return PaymentMethod.builder()
        .type(this.type)
        .provider(this.provider)
        .accountName(this.accountName)
        .accountNumber(this.accountNumber)
        .isDefault(false) 
        .build();
  }
}
