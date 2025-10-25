package com.natswarchuan.payment.demo.dto.request.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Đối tượng chuyển dữ liệu (DTO) cho yêu cầu rút tiền.
 * <p>
 * Kế thừa từ {@link BasePaymentRequest} để sử dụng lại các trường chung.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WithdrawRequest extends BasePaymentRequest {}
