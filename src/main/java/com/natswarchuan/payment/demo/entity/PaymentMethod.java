package com.natswarchuan.payment.demo.entity;

import com.natswarchuan.payment.demo.constant.EntityConstant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Lớp Entity đại diện cho một phương thức thanh toán được liên kết với ví của người dùng.
 *
 * <p><b>Xóa mềm (Soft Delete):</b> Thực thể này sử dụng cơ chế xóa mềm. Khi một bản ghi bị xóa,
 * trường {@code deleted_at} sẽ được cập nhật thay vì xóa vĩnh viễn.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = EntityConstant.TABLE_PAYMENT_METHODS,
    indexes = {
      @Index(name = EntityConstant.INDEX_PM_WALLET, columnList = EntityConstant.COLUMN_WALLET_ID)
    })
@SQLDelete(sql = "UPDATE payment_methods SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class PaymentMethod extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstant.COLUMN_WALLET_ID, nullable = false)
  private Wallet wallet;

  @Column(nullable = false)
  private Integer type;

  @Column(nullable = false, length = 50)
  private String provider;

  @Column(length = 100)
  private String accountName;

  @Column(nullable = false, length = 50)
  private String accountNumber;

  private LocalDate expiredAt;

  @Column(nullable = false)
  @Builder.Default
  private boolean isDefault = false;
}

