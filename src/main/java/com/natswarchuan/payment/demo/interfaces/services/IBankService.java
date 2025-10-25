package com.natswarchuan.payment.demo.interfaces.services;

import com.natswarchuan.payment.demo.dto.Bank;
import java.util.List;

/**
 * Interface định nghĩa các hợp đồng cho dịch vụ quản lý thông tin ngân hàng.
 *
 * <p>Cung cấp các phương thức để truy xuất danh sách các ngân hàng được hệ thống hỗ trợ.
 */
public interface IBankService {

  /**
   * Lấy danh sách tất cả các ngân hàng được hỗ trợ.
   *
   * <p>Kết quả trả về sẽ được sắp xếp theo tên viết tắt của ngân hàng để dễ dàng hiển thị cho người
   * dùng.
   *
   * @return một {@code List<Bank>} chứa thông tin các ngân hàng.
   */
  List<Bank> getSupportedBanks();
}
