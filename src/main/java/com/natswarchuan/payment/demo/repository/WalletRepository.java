package com.natswarchuan.payment.demo.repository;

import com.natswarchuan.payment.demo.entity.Wallet;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository để truy cập dữ liệu của thực thể {@link Wallet}.
 *
 * <p>Cung cấp các phương thức CRUD cơ bản và các truy vấn tùy chỉnh để thao tác với ví, bao gồm cả
 * cơ chế khóa bi quan (pessimistic locking) để đảm bảo tính toàn vẹn dữ liệu trong các giao dịch
 * đồng thời.
 */
@Repository
public interface WalletRepository
    extends JpaRepository<Wallet, UUID>, JpaSpecificationExecutor<Wallet> {

  /**
   * Tìm và khóa một ví dựa trên ID của người dùng.
   *
   * @param userId ID của người dùng.
   * @return một {@code Optional<Wallet>} chứa ví đã được khóa nếu tìm thấy.
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId")
  Optional<Wallet> findAndLockByUserId(Long userId);

  /**
   * Tìm và khóa một ví dựa trên số ví.
   *
   * @param number Số ví.
   * @return một {@code Optional<Wallet>} chứa ví đã được khóa nếu tìm thấy.
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT w FROM Wallet w WHERE w.number = :number")
  Optional<Wallet> findAndLockByNumber(String number);

  /**
   * Tìm và khóa một ví dựa trên ID của ví.
   *
   * @param id ID của ví.
   * @return một {@code Optional<Wallet>} chứa ví đã được khóa nếu tìm thấy.
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT w FROM Wallet w WHERE w.id = :id")
  Optional<Wallet> findAndLockById(UUID id);

  /**
   * Tìm một ví dựa trên số ví.
   *
   * @param number Số ví.
   * @return một {@code Optional<Wallet>} chứa ví nếu tìm thấy.
   */
  Optional<Wallet> findByNumber(String number);

  /**
   * Kiểm tra sự tồn tại của một ví dựa trên số ví.
   *
   * @param number Số ví.
   * @return {@code true} nếu tồn tại, ngược lại {@code false}.
   */
  boolean existsByNumber(String number);

  /**
   * Tìm một ví dựa trên ID của người dùng và tải sẵn các phương thức thanh toán liên quan.
   *
   * <p><b>Tối ưu hóa N+1 Query:</b> Sử dụng {@code @EntityGraph} để chỉ thị cho JPA thực hiện một
   * truy vấn {@code LEFT JOIN} để tải sẵn (eagerly fetch) danh sách {@code paymentMethods} cùng lúc
   * với thực thể {@code Wallet}. Điều này giúp ngăn chặn vấn đề N+1 query, tránh việc phải thực
   * hiện một truy vấn riêng cho mỗi phương thức thanh toán khi truy cập vào danh sách này sau đó.
   *
   * @param userId ID của người dùng.
   * @return một {@code Optional<Wallet>} chứa ví và các phương thức thanh toán liên quan.
   */
  @EntityGraph(attributePaths = {"paymentMethods"})
  Optional<Wallet> findByUserId(Long userId);
}

