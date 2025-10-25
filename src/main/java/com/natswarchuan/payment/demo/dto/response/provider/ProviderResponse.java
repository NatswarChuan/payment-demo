package com.natswarchuan.payment.demo.dto.response.provider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) đại diện cho thông tin của một nhà cung cấp dịch vụ thanh toán.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderResponse {

  private String name;
}
