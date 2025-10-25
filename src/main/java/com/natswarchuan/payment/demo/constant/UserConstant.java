package com.natswarchuan.payment.demo.constant;

/** Lớp chứa các hằng số liên quan đến entity User. */
public final class UserConstant {
  private UserConstant() {}

  /** Hằng số cho thuộc tính 'gender' của User. */
  public static final class Gender {
    private Gender() {}

    public static final int MALE = 0;
    public static final int FEMALE = 1;
    public static final int OTHER = 2;
  }

  /** Hằng số cho thuộc tính 'ekycStatus' của User. */
  public static final class EkycStatus {
    private EkycStatus() {}

    public static final int NOT_VERIFIED = 0; // Chưa xác minh
    public static final int PENDING = 1; // Đang chờ xử lý
    public static final int VERIFIED = 2; // Đã xác minh
    public static final int REJECTED = -1; // Bị từ chối
  }
}
