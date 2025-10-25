package com.natswarchuan.payment.demo.dto.request.transaction;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * DTO (Data Transfer Object) đại diện cho các tiêu chí tìm kiếm và lọc giao dịch.
 *
 * <p>Đối tượng này được sử dụng để nhận các tham số từ request của người dùng, cho phép tìm kiếm
 * giao dịch một cách linh hoạt theo nhiều điều kiện khác nhau.
 */
@Data
public class TransactionSearchRequest {

  /** Loại giao dịch cần tìm (ví dụ: DEPOSIT, WITHDRAWAL). */
  private Integer type;

  /** Trạng thái của giao dịch (ví dụ: PENDING, COMPLETED). */
  private Integer status;

  /**
   * Ngày bắt đầu của khoảng thời gian tìm kiếm.
   *
   * <p>Sử dụng annotation {@code @DateTimeFormat} để đảm bảo Spring có thể phân tích cú pháp chuỗi
   * ngày tháng theo định dạng ISO.
   */
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Instant fromDate;

  /** Ngày kết thúc của khoảng thời gian tìm kiếm. */
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Instant toDate;

  /** Số tiền tối thiểu của giao dịch. */
  private BigDecimal minAmount;

  /** Số tiền tối đa của giao dịch. */
  private BigDecimal maxAmount;
}
