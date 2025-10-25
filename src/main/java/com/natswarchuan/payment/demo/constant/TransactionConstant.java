package com.natswarchuan.payment.demo.constant;

/** Lớp chứa các hằng số liên quan đến entity Transaction. */
public final class TransactionConstant {
  private TransactionConstant() {}

  /** Hằng số cho thuộc tính 'type' của Transaction. */
  public static final class Type {
    private Type() {}

    public static final Integer DEPOSIT = 1; // Nạp tiền
    public static final Integer WITHDRAWAL = 2; // Rút tiền
    public static final Integer TRANSFER_OUT = 3; // Chuyển đi
    public static final Integer TRANSFER_IN = 4; // Nhận vào
    public static final Integer PAYMENT = 5; // Thanh toán
  }

  /** Hằng số cho thuộc tính 'status' của Transaction. */
  public static final class Status {
    private Status() {}

    public static final Integer FAILED = -1; // Thất bại
    public static final Integer CANCELLED = -2; // Đã hủy
    public static final Integer PENDING = 0; // Đang chờ
    public static final Integer COMPLETED = 1; // Hoàn thành
    public static final Integer REVIEWING = 2; // Đang xem xét
  }

  /** Hằng số cho các mẫu mô tả giao dịch. */
  public static final class Description {
    private Description() {}

    public static final String WITHDRAWAL = "Giao dich rut tien %s";
  }
}
