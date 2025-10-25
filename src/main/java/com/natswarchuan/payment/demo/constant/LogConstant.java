package com.natswarchuan.payment.demo.constant;

/** Lớp chứa tất cả các mẫu chuỗi (template) cho việc ghi log. */
public final class LogConstant {
  private LogConstant() {}

  // OTP Service
  public static final String OTP_INFO =
      """
      ===================================================
      OTP cho {} là: {}
      OTP này sẽ hết hạn sau 5 phút.
      ===================================================
      """;
  public static final String INVALID_IDENTIFIER_FOR_OTP =
      "Định dạng định danh không hợp lệ cho yêu cầu OTP: {}";
  public static final String OTP_VERIFIED_SUCCESS = "Xác thực OTP thành công cho định danh: {}";
  public static final String INVALID_OTP_PROVIDED =
      "Mã OTP không hợp lệ được cung cấp cho định danh: {}";
  public static final String SENDING_OTP_EMAIL = "Đang thử gửi OTP qua email tới {}";
  public static final String OTP_EMAIL_SENT_SUCCESS = "Đã gửi email OTP thành công tới {}";
  public static final String OTP_EMAIL_SEND_ERROR = "Lỗi khi gửi email OTP tới {}: {}";

  // Transaction Service
  public static final String TRANSACTION_ALREADY_PROCESSED =
      "Cố gắng xử lý một giao dịch đã được xử lý: {}";

  // Interceptor
  public static final String INTERCEPTOR_START_PROCESSING =
      "BẮT ĐẦU XỬ LÝ: URI=[{}], Phương thức=[{}]";
  public static final String INTERCEPTOR_FINISH_PROCESSING =
      "KẾT THÚC XỬ LÝ: URI=[{}], Trạng thái=[{}], Thời gian=[{}ms]";
  public static final String INTERCEPTOR_REQUEST_ERROR = "Yêu cầu hoàn tất với ngoại lệ: {}";

  // JWT & Security
  public static final String UNAUTHORIZED_ERROR = "Lỗi không được phép: {}";
  public static final String INVALID_JWT_SIGNATURE = "Chữ ký JWT không hợp lệ";
  public static final String INVALID_JWT_TOKEN = "Token JWT không hợp lệ";
  public static final String EXPIRED_JWT_TOKEN = "Token JWT đã hết hạn";
  public static final String UNSUPPORTED_JWT_TOKEN = "Token JWT không được hỗ trợ";
  public static final String EMPTY_JWT_CLAIMS = "Chuỗi JWT claims trống.";

  // User Service
  public static final String USER_SERVICE_LOAD_USER_NOTE =
      "UserService.loadUserByUsername được gọi cho {}. Ghi chú: Quyền hạn nên được tải bởi"
          + " UnifiedUserDetailsService.";
  public static final String CREATING_USER = "Đang thử tạo người dùng mới với định danh: {}";
  public static final String USER_CREATED = "Người dùng mới đã được tạo với ID: {}";
  public static final String WALLET_CREATED_FOR_USER = "Ví đã được tạo cho người dùng ID: {}";

  // Auth Controller
  public static final String REG_OTP_REQUEST = "Nhận được yêu cầu OTP đăng ký cho định danh: {}";
  public static final String REG_VERIFY_REQUEST =
      "Nhận được yêu cầu xác thực đăng ký cho định danh: {}";
  public static final String USER_REGISTERED_SUCCESS =
      "Người dùng đã đăng ký thành công với ID: {}";
  public static final String LOGIN_OTP_REQUEST =
      "Nhận được yêu cầu OTP đăng nhập cho định danh: {}";
  public static final String LOGIN_VERIFY_REQUEST =
      "Nhận được yêu cầu xác thực đăng nhập cho định danh: {}";
  public static final String USER_LOGGED_IN_SUCCESS =
      "Người dùng đã đăng nhập thành công với ID: {}";
  public static final String USER_LOGGED_OUT_SUCCESS = "Người dùng đã đăng xuất thành công.";

  // User Controller
  public static final String FETCHING_USER_DETAILS =
      "Đang lấy thông tin chi tiết cho người dùng ID: {}";

  // Data Initializer
  public static final String DATA_INIT_START =
      "Bắt đầu khởi tạo dữ liệu cho vai trò và quyền hạn...";
  public static final String CREATING_PERMISSION = "Đang tạo quyền: {}";
  public static final String CREATING_ROLE = "Đang tạo vai trò: {}";
  public static final String DATA_INIT_FINISH = "Khởi tạo dữ liệu hoàn tất.";

  // Vnpay Service
  public static final String VNPAY_PAYOUT_INITIATE =
      "Khởi tạo yêu cầu rút tiền VNPAY thực tế cho giao dịch ID: {}";
  public static final String VNPAY_PAYOUT_REQUEST_SEND = "Đang gửi yêu cầu rút tiền tới VNPAY: {}";
  public static final String VNPAY_PAYOUT_RESPONSE_RECEIVE =
      "Đã nhận phản hồi rút tiền từ VNPAY: {}";
  public static final String VNPAY_PAYOUT_HTTP_ERROR =
      "Lỗi HTTP trong khi gọi API rút tiền VNPAY: {} - {}";
  public static final String VNPAY_PAYOUT_GENERAL_ERROR =
      "Ngoại lệ trong khi gọi API rút tiền VNPAY";
  public static final String VNPAY_IPN_INVALID_CHECKSUM = "Checksum IPN của VNPAY không hợp lệ";
  public static final String VNPAY_IPN_PROCESSING_ERROR = "Lỗi xử lý IPN của VNPAY";
  public static final String VNPAY_IPN_WITHDRAWAL_PROCESSING =
      "Đang xử lý IPN Rút tiền từ VNPAY...";
}
