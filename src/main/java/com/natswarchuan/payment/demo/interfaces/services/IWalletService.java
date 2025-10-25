package com.natswarchuan.payment.demo.interfaces.services;

import com.natswarchuan.payment.demo.dto.response.wallet.WalletDetailResponse;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.entity.Wallet;
import com.natswarchuan.payment.demo.interfaces.IService;
import java.util.UUID;

/**
 * Interface cho dịch vụ quản lý các nghiệp vụ liên quan đến {@link Wallet}.
 */
public interface IWalletService extends IService<Wallet, UUID> {

  /**
   * Tạo một ví mới cho một người dùng cụ thể.
   *
   * @param user Người dùng cần tạo ví.
   * @return Đối tượng {@code Wallet} vừa được tạo.
   */
  Wallet createWalletForUser(User user);

  /**
   * Tìm một ví dựa trên số ví.
   *
   * @param number Số ví.
   * @return Đối tượng {@code Wallet} tương ứng.
   */
  Wallet findByNumber(String number);

  /**
   * Lấy thông tin chi tiết của ví cho một người dùng.
   *
   * @param userId ID của người dùng.
   * @return Một đối tượng {@code WalletDetailResponse} chứa thông tin chi tiết.
   */
  WalletDetailResponse findWalletForUser(Long userId);

  /**
   * Thiết lập mã PIN cho ví của người dùng.
   *
   * @param userId ID của người dùng.
   * @param pin    Mã PIN mới (dạng chuỗi).
   */
  void setPin(Long userId, String pin);

  /**
   * Tìm thực thể {@link Wallet} dựa trên ID của người dùng.
   * <p>
   * Phương thức này được thiết kế để trả về đối tượng entity đầy đủ, hữu ích khi cần thực hiện các
   * thao tác nghiệp vụ phức tạp hơn là chỉ hiển thị dữ liệu.
   *
   * @param userId ID của người dùng.
   * @return Thực thể {@code Wallet} nếu tìm thấy.
   * @throws com.natswarchuan.payment.demo.exception.custom.ResourceNotFoundException nếu không tìm
   * thấy ví.
   */
  Wallet findWalletEntityByUserId(Long userId);
}
