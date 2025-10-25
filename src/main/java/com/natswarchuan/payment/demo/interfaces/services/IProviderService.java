package com.natswarchuan.payment.demo.interfaces.services;

import com.natswarchuan.payment.demo.dto.response.provider.ProviderResponse;
import java.util.List;

/**
 * Interface định nghĩa các hợp đồng cho dịch vụ quản lý thông tin nhà cung cấp.
 *
 * <p>Cung cấp các phương thức để truy xuất danh sách các nhà cung cấp dịch vụ (ví dụ: cổng thanh
 * toán) được tích hợp trong hệ thống.
 */
public interface IProviderService {

  /**
   * Lấy danh sách tất cả các nhà cung cấp cổng thanh toán hiện có trong hệ thống.
   *
   * @return một danh sách các đối tượng {@link ProviderResponse}, mỗi đối tượng chứa tên của một
   *     nhà cung cấp.
   */
  List<ProviderResponse> getAvailableProviders();
}
