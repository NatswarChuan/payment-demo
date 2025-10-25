package com.natswarchuan.payment.demo.entity;

import com.natswarchuan.payment.demo.constant.EntityConstant;
import com.natswarchuan.payment.demo.constant.TransactionConstant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lớp Entity đại diện cho một giao dịch trong hệ thống.
 *
 * <p>Mỗi bản ghi trong bảng này tương ứng với một hành động tài chính như nạp tiền, rút tiền,
 * chuyển tiền.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = EntityConstant.TABLE_TRANSACTIONS,
    indexes = {
      @Index(
          name = EntityConstant.INDEX_TXN_WALLET_CREATED,
          columnList =
              EntityConstant.COLUMN_WALLET_ID + ", " + EntityConstant.FIELD_CREATED_AT + " DESC"),
      @Index(
          name = EntityConstant.INDEX_TXN_STATUS_TYPE,
          columnList = EntityConstant.TXN_FIELD_STATUS + ", " + EntityConstant.TXN_FIELD_TYPE),
      @Index(
          name = EntityConstant.INDEX_TXN_PROVIDER_ID,
          columnList = "providerTransactionId")
    })
public class Transaction extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstant.COLUMN_WALLET_ID, nullable = false)
  private Wallet wallet;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstant.COLUMN_PAYMENT_METHOD_ID)
  private PaymentMethod paymentMethod;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal amount;

  @Column(precision = 19, scale = 4, nullable = false)
  @Builder.Default
  private BigDecimal transactionFee = BigDecimal.ZERO;

  @Column(precision = 19, scale = 4, nullable = false)
  private BigDecimal netAmount;

  @Column(precision = 19, scale = 4)
  private BigDecimal balanceBefore;

  @Column(precision = 19, scale = 4)
  private BigDecimal balanceAfter;

  @Column(nullable = false)
  private Integer type;

  @Column(nullable = false)
  @Builder.Default
  private Integer status = TransactionConstant.Status.PENDING;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(length = 255)
  private String providerTransactionId;

  @Column(columnDefinition = "TEXT")
  private String providerResponse;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstant.COLUMN_RELATED_TRANSACTION_ID)
  private Transaction relatedTransaction;

  @Column(length = 45)
  private String ipAddress; 

  /**
   * Tự động tính toán giá trị `netAmount` trước khi lưu hoặc cập nhật.
   */
  @PrePersist
  @PreUpdate
  private void calculateNetAmount() {
    if (this.amount != null && this.transactionFee != null) {
      this.netAmount = this.amount.subtract(this.transactionFee);
    }
  }
}
