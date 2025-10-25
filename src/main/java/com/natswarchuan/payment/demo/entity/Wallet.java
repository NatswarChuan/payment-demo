package com.natswarchuan.payment.demo.entity;

import com.natswarchuan.payment.demo.constant.EntityConstant;
import com.natswarchuan.payment.demo.constant.WalletConstant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Lớp Entity đại diện cho ví điện tử của người dùng.
 *
 * <p><b>Xóa mềm (Soft Delete):</b> Thực thể này sử dụng cơ chế xóa mềm. Khi một bản ghi bị xóa,
 * trường {@code deleted_at} sẽ được cập nhật thay vì xóa vĩnh viễn.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = EntityConstant.TABLE_WALLETS)
@Data
@ToString(exclude = {"user", "paymentMethods", "transactions"})
@SQLDelete(sql = "UPDATE wallets SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Wallet extends BaseEntity {

  @Column(length = 60)
  private String pin;

  @Column(nullable = false, unique = true, length = 255)
  private String number;

  @Column(nullable = false, precision = 19, scale = 4)
  @ColumnDefault("0.0000")
  private BigDecimal balance;

  @Column(nullable = false, length = 3)
  @ColumnDefault("'VND'")
  private String currency;

  @Column(nullable = false)
  @ColumnDefault("1")
  private Integer status = WalletConstant.Status.ACTIVE;

  @OneToOne
  @JoinColumn(
      name = EntityConstant.COLUMN_USER_ID,
      referencedColumnName = EntityConstant.FIELD_ID,
      nullable = false,
      unique = true)
  private User user;

  @OneToMany(
      mappedBy = EntityConstant.WALLET_FIELD_SELF,
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY)
  private List<PaymentMethod> paymentMethods;

  @OneToMany(
      mappedBy = EntityConstant.WALLET_FIELD_SELF,
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY)
  private List<Transaction> transactions;
}
