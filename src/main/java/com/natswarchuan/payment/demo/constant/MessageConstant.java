package com.natswarchuan.payment.demo.constant;

/**
 * Lớp chứa các hằng số là các chuỗi thông báo được sử dụng trong toàn bộ ứng dụng.
 *
 * <p>Việc tập trung các chuỗi thông báo vào một nơi giúp dễ dàng quản lý, cập nhật và quốc tế hóa
 * (internationalization) sau này.
 */
public final class MessageConstant {
  private MessageConstant() {}

  public static final class General {
    private General() {}

    public static final String ENTITY_NOT_FOUND_BY_ID = "Không tìm thấy thực thể với ID: %s";
    public static final String DTO_CONVERSION_ERROR = "Lỗi khi chuyển đổi entity sang DTO.";
    public static final String UNEXPECTED_ERROR = "Đã xảy ra lỗi máy chủ nội bộ không mong muốn.";
    public static final String VALIDATION_ERROR_SEPARATOR = ", ";
    public static final String DTO_TO_ENTITY_UNSUPPORTED =
        "Việc chuyển đổi từ DTO sang Entity không được hỗ trợ cho lớp này.";
    public static final String ENTITY_TO_DTO_UNSUPPORTED =
        "Việc chuyển đổi từ Entity sang DTO không được hỗ trợ cho lớp này.";
    public static final String UPDATE_ENTITY_FROM_DTO_UNSUPPORTED =
        "Việc cập nhật một Entity từ DTO không được hỗ trợ cho lớp này.";
  }

  public static final class Auth {
    private Auth() {}

    public static final String INVALID_OR_EXPIRED_OTP = "Mã OTP không hợp lệ hoặc đã hết hạn.";
    public static final String LOGOUT_SUCCESS = "Đăng xuất thành công.";
    public static final String INVALID_REFRESH_TOKEN = "Refresh token không hợp lệ.";
    public static final String REFRESH_TOKEN_EXPIRED = "Refresh token đã hết hạn.";
    public static final String INVALID_JWT_TOKEN = "JWT Token không hợp lệ.";
    public static final String UNAUTHORIZED = "Không được phép truy cập.";
    public static final String LOGIN_SUCCESS = "Đăng nhập thành công.";
    public static final String REGISTER_SUCCESS = "Đăng ký thành công.";
    public static final String TOKEN_REFRESH_SUCCESS = "Làm mới token thành công.";
    public static final String INVALID_PIN = "Mã PIN không chính xác.";
    public static final String INVALID_SECRET_KEY = "Khóa bí mật không hợp lệ hoặc đã hết hạn.";
  }

  public static final class User {
    private User() {}

    public static final String NOT_FOUND_BY_IDENTIFIER =
        "Không tìm thấy người dùng với email hoặc số điện thoại: %s";
    public static final String NOT_FOUND_BY_IDENTIFIER_FOR_LOGIN =
        "Tài khoản không tồn tại. Vui lòng đăng ký trước.";
    public static final String USER_FOR_REFRESH_TOKEN_NOT_FOUND =
        "Không tìm thấy người dùng cho refresh token.";
    public static final String USER_ALREADY_EXISTS = "Người dùng với định danh này đã tồn tại.";
    public static final String INVALID_IDENTIFIER_FORMAT =
        "Định dạng định danh không hợp lệ. Phải là email hoặc số điện thoại hợp lệ.";
    public static final String AUTHENTICATED_USER_NOT_IN_DB =
        "Không tìm thấy người dùng đã xác thực trong cơ sở dữ liệu với định danh: %s";
    public static final String EKYC_REQUIRED =
        "Vui lòng hoàn thành xác minh danh tính (eKYC) để sử dụng chức năng này.";
    public static final String EKYC_ALREADY_VERIFIED = "Tài khoản của bạn đã được xác minh eKYC.";
    public static final String EKYC_COMPLETED_SUCCESS =
        "Xác minh danh tính eKYC thành công. Bạn có thể bắt đầu giao dịch.";
  }

  public static final class Otp {
    private Otp() {}

    public static final String OTP_SENT_SUCCESS = "Mã OTP đã được gửi tới định danh của bạn.";
    public static final String EMAIL_SUBJECT = "Mã OTP của bạn cho Payment Demo";
    public static final String EMAIL_BODY =
        "Xin chào,\n\nMã xác thực một lần (OTP) của bạn là: %s\n\nMã này sẽ hết hạn sau 5 phút.";
    public static final String OTP_RATE_LIMIT_EXCEEDED =
        "Bạn đã yêu cầu OTP quá nhiều lần. Vui lòng thử lại sau %d phút.";
  }

  public static final class Wallet {
    private Wallet() {}

    public static final String USER_WALLET_NOT_FOUND = "Không tìm thấy ví của người dùng.";
    public static final String SENDER_WALLET_NOT_FOUND = "Không tìm thấy ví của người gửi.";
    public static final String NOT_FOUND_BY_NUMBER = "Không tìm thấy ví với số: %s";
    public static final String INSUFFICIENT_BALANCE = "Số dư không đủ.";
    public static final String PIN_NOT_SET =
        "Vui lòng đặt mã PIN cho ví của bạn trước khi giao dịch.";
  }

  public static final class Transaction {
    private Transaction() {}

    public static final String DEPOSIT_DESCRIPTION = "Nạp tiền vào ví qua %s";
    public static final String TRANSACTION_FAILED = "Giao dịch thất bại: %s";
    public static final String SELF_TRANSFER_ERROR = "Không thể chuyển tiền vào cùng một ví.";
    public static final String WALLET_INACTIVE_ERROR =
        "Ví của người gửi hoặc người nhận không hoạt động.";
    public static final String TRANSFER_OUT_DESCRIPTION = "Chuyển tiền tới ví %s. Lời nhắn: %s";
    public static final String TRANSFER_IN_DESCRIPTION = "Nhận tiền từ ví %s";
    public static final String WITHDRAW_NOT_IMPLEMENTED =
        "Chức năng rút tiền chưa được triển khai.";
    public static final String AMOUNT_MUST_BE_POSITIVE = "Số tiền phải là số dương.";
    public static final String WITHDRAWAL_INITIATION_FAILED =
        "Khởi tạo yêu cầu rút tiền thất bại: %s";
    public static final String SECRET_KEY_RATE_LIMIT_EXCEEDED =
        "Bạn đã yêu cầu khóa bí mật quá nhiều lần. Vui lòng thử lại sau %d phút.";
  }

  public static final class Payment {
    private Payment() {}

    public static final String GATEWAY_NOT_SUPPORTED = "Cổng thanh toán không được hỗ trợ: %s";
    public static final String PAYMENT_METHOD_NOT_OWNED =
        "Phương thức thanh toán không thuộc sở hữu của người dùng.";
    public static final String IPN_RECEIVED = "Đã nhận IPN";
    public static final String INVALID_SIGNATURE = "Chữ ký không hợp lệ.";
    public static final String EWALLET_LINK_SUCCESS = "Liên kết ví điện tử thành công.";
    public static final String EWALLET_UPDATE_SUCCESS = "Cập nhật liên kết ví điện tử thành công.";
    public static final String EWALLET_ALREADY_LINKED = "Ví điện tử này đã được liên kết.";
    public static final String BANK_ACCOUNT_LINK_SUCCESS = "Liên kết tài khoản ngân hàng thành công.";
    public static final String BANK_ACCOUNT_UPDATE_SUCCESS =
        "Cập nhật tài khoản ngân hàng thành công.";
    public static final String PAYMENT_METHOD_IN_USE =
        "Phương thức thanh toán này hiện đang được liên kết với một tài khoản khác.";
  }

  public static final class Vnpay {
    private Vnpay() {}

    public static final String PAYOUT_ACCEPTED = "Yêu cầu rút tiền đã được VNPAY chấp nhận: %s";
    public static final String PAYOUT_REJECTED = "VNPAY từ chối yêu cầu rút tiền: %s";
    public static final String PAYOUT_CONNECTION_FAILED = "Không thể kết nối đến VNPAY: %s";
    public static final String PAYOUT_INTERNAL_ERROR =
        "Đã xảy ra lỗi nội bộ khi khởi tạo yêu cầu rút tiền: %s";
    public static final String UNKNOWN_ERROR = "Lỗi không xác định từ VNPAY";
    public static final String IPN_PROCESSED_WITH_CODE =
        "IPN đã được xử lý. Mã phản hồi VNPAY: %s";
    public static final String IPN_PROCESSING_ERROR = "Lỗi xử lý IPN";
  }
}
