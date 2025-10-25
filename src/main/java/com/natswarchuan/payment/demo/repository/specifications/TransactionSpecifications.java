package com.natswarchuan.payment.demo.repository.specifications;

import com.natswarchuan.payment.demo.constant.EntityConstant;
import com.natswarchuan.payment.demo.dto.request.transaction.TransactionSearchRequest;
import com.natswarchuan.payment.demo.entity.Transaction;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Lớp chứa các {@link Specification} có thể tái sử dụng để xây dựng các truy vấn động cho thực thể
 * {@link Transaction}.
 */
@Component
public class TransactionSpecifications {

  /**
   * Xây dựng một đối tượng {@link Specification} hoàn chỉnh từ các tiêu chí tìm kiếm.
   *
   * <p>Phương thức này đóng gói logic kết hợp nhiều điều kiện lọc khác nhau thành một truy vấn duy
   * nhất, giúp cho lớp service trở nên gọn gàng và dễ đọc hơn.
   *
   * @param searchRequest đối tượng chứa các tiêu chí tìm kiếm.
   * @param walletId ID của ví mà các giao dịch phải thuộc về.
   * @return một {@code Specification<Transaction>} tổng hợp tất cả các điều kiện lọc.
   */
  public Specification<Transaction> fromSearchRequest(
      final TransactionSearchRequest searchRequest, final UUID walletId) {

    Specification<Transaction> spec = Specification.where(forWallet(walletId));

    if (searchRequest.getType() != null) {
      spec = spec.and(hasType(searchRequest.getType()));
    }
    if (searchRequest.getStatus() != null) {
      spec = spec.and(hasStatus(searchRequest.getStatus()));
    }
    if (searchRequest.getFromDate() != null || searchRequest.getToDate() != null) {
      spec = spec.and(createdAtBetween(searchRequest.getFromDate(), searchRequest.getToDate()));
    }
    if (searchRequest.getMinAmount() != null || searchRequest.getMaxAmount() != null) {
      spec = spec.and(amountBetween(searchRequest.getMinAmount(), searchRequest.getMaxAmount()));
    }

    return spec;
  }

  /**
   * Tạo một Specification để lọc các giao dịch thuộc về một ví cụ thể.
   *
   * @param walletId ID của ví.
   * @return một {@code Specification} để lọc theo walletId.
   */
  private Specification<Transaction> forWallet(final UUID walletId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(
            root.get(EntityConstant.TXN_FIELD_WALLET).get(EntityConstant.FIELD_ID), walletId);
  }

  /**
   * Tạo một Specification để lọc các giao dịch theo loại.
   *
   * @param type loại giao dịch.
   * @return một {@code Specification} để lọc theo loại.
   */
  private Specification<Transaction> hasType(final Integer type) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(EntityConstant.TXN_FIELD_TYPE), type);
  }

  /**
   * Tạo một Specification để lọc các giao dịch theo trạng thái.
   *
   * @param status trạng thái giao dịch.
   * @return một {@code Specification} để lọc theo trạng thái.
   */
  private Specification<Transaction> hasStatus(final Integer status) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(EntityConstant.TXN_FIELD_STATUS), status);
  }

  /**
   * Tạo một Specification để lọc các giao dịch có số tiền nằm trong một khoảng nhất định.
   *
   * @param minAmount số tiền tối thiểu.
   * @param maxAmount số tiền tối đa.
   * @return một {@code Specification} để lọc theo khoảng số tiền.
   */
  private Specification<Transaction> amountBetween(
      final BigDecimal minAmount, final BigDecimal maxAmount) {
    return (root, query, criteriaBuilder) -> {
      if (minAmount != null && maxAmount != null) {
        return criteriaBuilder.between(
            root.get(EntityConstant.TXN_FIELD_AMOUNT), minAmount, maxAmount);
      } else if (minAmount != null) {
        return criteriaBuilder.greaterThanOrEqualTo(
            root.get(EntityConstant.TXN_FIELD_AMOUNT), minAmount);
      } else if (maxAmount != null) {
        return criteriaBuilder.lessThanOrEqualTo(
            root.get(EntityConstant.TXN_FIELD_AMOUNT), maxAmount);
      }
      return criteriaBuilder.conjunction();
    };
  }

  /**
   * Tạo một Specification để lọc các giao dịch được tạo trong một khoảng thời gian.
   *
   * @param startDate thời gian bắt đầu.
   * @param endDate thời gian kết thúc.
   * @return một {@code Specification} để lọc theo khoảng thời gian.
   */
  private Specification<Transaction> createdAtBetween(
      final Instant startDate, final Instant endDate) {
    return (root, query, criteriaBuilder) -> {
      if (startDate != null && endDate != null) {
        return criteriaBuilder.between(
            root.get(EntityConstant.TXN_FIELD_CREATED_AT), startDate, endDate);
      } else if (startDate != null) {
        return criteriaBuilder.greaterThanOrEqualTo(
            root.get(EntityConstant.TXN_FIELD_CREATED_AT), startDate);
      } else if (endDate != null) {
        return criteriaBuilder.lessThanOrEqualTo(
            root.get(EntityConstant.TXN_FIELD_CREATED_AT), endDate);
      }
      return criteriaBuilder.conjunction();
    };
  }
}
