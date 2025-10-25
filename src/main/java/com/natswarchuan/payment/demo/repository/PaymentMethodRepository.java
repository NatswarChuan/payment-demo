package com.natswarchuan.payment.demo.repository;

import com.natswarchuan.payment.demo.entity.PaymentMethod;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository để truy cập dữ liệu của thực thể {@link PaymentMethod}.
 *
 * <p>Cung cấp các phương thức CRUD cơ bản và các truy vấn tùy chỉnh để thao tác với phương thức
 * thanh toán, bao gồm cả các truy vấn native để xử lý logic xóa mềm.
 */
@Repository
public interface PaymentMethodRepository
    extends JpaRepository<PaymentMethod, UUID>, JpaSpecificationExecutor<PaymentMethod> {

  /**
   * Tìm danh sách các phương thức thanh toán đang hoạt động (chưa bị xóa mềm) theo ID của ví.
   *
   * @param walletId ID của ví.
   * @return Danh sách các {@link PaymentMethod}.
   */
  List<PaymentMethod> findByWalletId(UUID walletId);

  /**
   * Tìm một phương thức thanh toán của một ví cụ thể dựa trên nhà cung cấp và số tài khoản, bao gồm
   * cả các bản ghi đã bị xóa mềm.
   *
   * @param walletId ID của ví.
   * @param provider Nhà cung cấp (mã ngân hàng, tên ví điện tử).
   * @param accountNumber Số tài khoản/số định danh ví.
   * @param type Loại phương thức thanh toán.
   * @return {@code Optional<PaymentMethod>} chứa phương thức thanh toán nếu tìm thấy.
   */
  @Query(
      value =
          "SELECT * FROM payment_methods WHERE wallet_id = :walletId AND provider = :provider AND"
              + " account_number = :accountNumber AND type = :type LIMIT 1",
      nativeQuery = true)
  Optional<PaymentMethod> findSoftDeletedByWalletIdAndProviderAndAccountNumberAndType(
      UUID walletId, String provider, String accountNumber, Integer type);

  /**
   * Tìm một phương thức thanh toán đang hoạt động (chưa bị xóa mềm) trên toàn hệ thống dựa trên nhà
   * cung cấp và số tài khoản.
   *
   * <p>Phương thức này sẽ tự động áp dụng điều kiện `deleted_at IS NULL` nhờ vào annotation
   * `@SQLRestriction` trên entity.
   *
   * @param provider Nhà cung cấp (mã ngân hàng, tên ví điện tử).
   * @param accountNumber Số tài khoản/số định danh ví.
   * @param type Loại phương thức thanh toán.
   * @return {@code Optional<PaymentMethod>} chứa phương thức thanh toán nếu tìm thấy.
   */
  Optional<PaymentMethod> findByProviderAndAccountNumberAndType(
      String provider, String accountNumber, Integer type);
}

