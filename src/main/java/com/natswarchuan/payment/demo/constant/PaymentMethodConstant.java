package com.natswarchuan.payment.demo.constant;

/** Lớp chứa các hằng số liên quan đến entity PaymentMethod. */
public final class PaymentMethodConstant {
  private PaymentMethodConstant() {}

  /** Hằng số cho thuộc tính 'type' của PaymentMethod. */
  public static final class Type {
    private Type() {}
    public static final Integer BANK_ACCOUNT = 1; // Tài khoản ngân hàng
    public static final Integer CREDIT_CARD = 2; // Thẻ tín dụng
    public static final Integer E_WALLET = 3; // Ví điện tử
  }

  /** Hằng số cho các nhà cung cấp ví điện tử được hỗ trợ. */
  public static final class EwalletProvider {
    private EwalletProvider() {}
    public static final String MOMO = "MOMO";
    public static final String ZALOPAY = "ZALOPAY";
    public static final String VNPAY = "VNPAY";
  }
}
