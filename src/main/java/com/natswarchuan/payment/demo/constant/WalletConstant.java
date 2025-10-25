package com.natswarchuan.payment.demo.constant;

/**
 * Lớp chứa các hằng số liên quan đến entity Wallet.
 */
public final class WalletConstant {
  private WalletConstant() {}

  /** Hằng số cho thuộc tính 'status' của Wallet. */
  public static final class Status {
    private Status() {}
    public static final Integer INACTIVE = 0; // Không hoạt động
    public static final Integer ACTIVE = 1;   // Đang hoạt động
    public static final Integer SUSPENDED = -1; // Bị đình chỉ
    public static final Integer CLOSED = -2; // Đã đóng
  }

  /** Hằng số cho thuộc tính 'currency' của Wallet. */
  public static final class Currency {
    private Currency() {}
    public static final String VND = "VND";
    public static final String USD = "USD";
  }
}
