package com.natswarchuan.payment.demo.constant;

public final class VnpayConstant {
  private VnpayConstant() {}

  // Common Params
  public static final String VNP_VERSION = "vnp_Version";
  public static final String VNP_COMMAND = "vnp_Command";
  public static final String VNP_TMN_CODE = "vnp_TmnCode";
  public static final String VNP_AMOUNT = "vnp_Amount";
  public static final String VNP_CREATE_DATE = "vnp_CreateDate";
  public static final String VNP_CURR_CODE = "vnp_CurrCode";
  public static final String VNP_IP_ADDR = "vnp_IpAddr";
  public static final String VNP_LOCALE = "vnp_Locale";
  public static final String VNP_ORDER_INFO = "vnp_OrderInfo";
  public static final String VNP_TXN_REF = "vnp_TxnRef";
  public static final String VNP_SECURE_HASH = "vnp_SecureHash";

  // Payment Params
  public static final String VNP_ORDER_TYPE = "vnp_OrderType";
  public static final String VNP_RETURN_URL = "vnp_ReturnUrl";
  public static final String VNP_BANK_CODE = "vnp_BankCode";

  // IPN / Response Params
  public static final String VNP_RESPONSE_CODE = "vnp_ResponseCode";
  public static final String VNP_TRANSACTION_NO = "vnp_TransactionNo";
  public static final String VNP_TRANSACTION_STATUS = "vnp_TransactionStatus";

  // Payout Params
  public static final String VNP_REQUEST_ID = "vnp_RequestId";
  public static final String VNP_ACCOUNT_NO = "vnp_AccountNo";
  public static final String VNP_ACCOUNT_NAME = "vnp_AccountName";
  public static final String VNP_PAY_METHOD = "vnp_PayMethod";

  // Values
  public static final String CURRENCY_CODE_VND = "VND";
  public static final String LOCALE_VN = "vn";
  public static final String ORDER_TYPE_OTHER = "other";
  public static final String COMMAND_PAY = "pay";
  public static final String COMMAND_PAYOUT = "payout"; 
  public static final String RESPONSE_SUCCESS_CODE = "00";
  public static final String API_RESPONSE_SUCCESS_CODE = "00";
  public static final String PAY_METHOD_ACCOUNT = "ACCOUNT"; 
  public static final String PAY_METHOD_CARD = "CARD"; 
}

