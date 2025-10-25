package com.natswarchuan.payment.demo.controller;

import com.natswarchuan.payment.demo.config.security.JwtService;
import com.natswarchuan.payment.demo.constant.ApiConstant;
import com.natswarchuan.payment.demo.constant.LogConstant;
import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.dto.request.auth.OtpLoginRequest;
import com.natswarchuan.payment.demo.dto.request.auth.RefreshTokenRequest;
import com.natswarchuan.payment.demo.dto.request.auth.RegisterOtpRequest;
import com.natswarchuan.payment.demo.dto.request.auth.VerifyOtpRequest;
import com.natswarchuan.payment.demo.dto.request.auth.VerifyRegistrationRequest;
import com.natswarchuan.payment.demo.dto.response.HttpResponseApi;
import com.natswarchuan.payment.demo.dto.response.auth.AuthResponse;
import com.natswarchuan.payment.demo.dto.response.user.UserDetailResponse;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.exception.custom.ConflictException;
import com.natswarchuan.payment.demo.exception.custom.ResourceNotFoundException;
import com.natswarchuan.payment.demo.exception.custom.UnauthorizedException;
import com.natswarchuan.payment.demo.interfaces.services.IOtpService;
import com.natswarchuan.payment.demo.interfaces.services.IUserService;
import com.natswarchuan.payment.demo.util.SanitizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý tất cả các yêu cầu liên quan đến xác thực người dùng.
 *
 * <p>Bao gồm các chức năng như đăng ký, đăng nhập qua OTP, làm mới token và đăng xuất.
 */
@RestController
@RequestMapping(ApiConstant.AUTH_ENDPOINT)
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final IUserService userService;
  private final JwtService jwtService;
  private final IOtpService otpService;

  /**
   * Yêu cầu gửi mã OTP để bắt đầu quá trình đăng ký.
   *
   * <p>Endpoint này kiểm tra xem người dùng đã tồn tại chưa. Nếu chưa, nó sẽ gọi {@link
   * IOtpService} để gửi OTP. Chức năng này được bảo vệ bởi cơ chế giới hạn tần suất (rate
   * limiting).
   *
   * @param request đối tượng chứa định danh (email/số điện thoại) của người dùng.
   * @param httpRequest đối tượng HttpServletRequest để lấy thông tin về request cho rate limiting.
   * @return một ResponseEntity cho biết OTP đã được gửi thành công.
   */
  @PostMapping(ApiConstant.REGISTER_OTP_REQUEST)
  public ResponseEntity<HttpResponseApi<Void>> requestRegistrationOtp(
      @Valid @RequestBody final RegisterOtpRequest request, final HttpServletRequest httpRequest) {
    log.info(LogConstant.REG_OTP_REQUEST, request.getIdentifier());
    userService
        .findByIdentifier(request.getIdentifier())
        .ifPresent(
            user -> {
              throw new ConflictException(MessageConstant.User.USER_ALREADY_EXISTS);
            });
    otpService.sendOtp(request.getIdentifier(), httpRequest);
    return new HttpResponseApi.Ok<>(MessageConstant.Otp.OTP_SENT_SUCCESS);
  }

  /**
   * Xác thực mã OTP và hoàn tất quá trình đăng ký người dùng mới.
   *
   * @param request đối tượng chứa thông tin đăng ký và mã OTP.
   * @param httpRequest đối tượng HttpServletRequest để lấy thông tin về request.
   * @return một ResponseEntity chứa token truy cập, token làm mới và thông tin chi tiết của người
   *     dùng mới.
   */
  @PostMapping(ApiConstant.REGISTER_OTP_VERIFY)
  public ResponseEntity<HttpResponseApi<AuthResponse>> verifyOtpAndRegister(
      @Valid @RequestBody final VerifyRegistrationRequest request,
      final HttpServletRequest httpRequest) {
    log.info(LogConstant.REG_VERIFY_REQUEST, request.getIdentifier());
    verifyOtpOrThrow(request.getIdentifier(), request.getOtp());

    request.setFullName(SanitizationUtil.sanitize(request.getFullName()));
    request.setNickName(SanitizationUtil.sanitize(request.getNickName()));

    final User newUser = userService.createUser(request);
    log.info(LogConstant.USER_REGISTERED_SUCCESS, newUser.getId());

    final String accessToken = jwtService.generateToken(newUser);
    final String refreshToken = jwtService.generateRefreshToken(newUser, httpRequest);
    final UserDetailResponse userDetails = userService.getUserDetails(newUser.getId());

    final AuthResponse authResponse = new AuthResponse(accessToken, refreshToken, userDetails);

    return new HttpResponseApi.Created<>(MessageConstant.Auth.REGISTER_SUCCESS, authResponse);
  }

  /**
   * Yêu cầu gửi mã OTP để bắt đầu quá trình đăng nhập.
   *
   * <p>Endpoint này kiểm tra xem người dùng có tồn tại không trước khi gửi OTP. Chức năng này cũng
   * được bảo vệ bởi cơ chế giới hạn tần suất (rate limiting).
   *
   * @param request đối tượng chứa định danh (email/số điện thoại) của người dùng.
   * @param httpRequest đối tượng HttpServletRequest để lấy thông tin về request cho rate limiting.
   * @return một ResponseEntity cho biết OTP đã được gửi thành công.
   */
  @PostMapping(ApiConstant.LOGIN_OTP_REQUEST)
  public ResponseEntity<HttpResponseApi<Void>> requestLoginOtp(
      @Valid @RequestBody final OtpLoginRequest request, final HttpServletRequest httpRequest) {
    log.info(LogConstant.LOGIN_OTP_REQUEST, request.getIdentifier());
    userService
        .findByIdentifier(request.getIdentifier())
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    MessageConstant.User.NOT_FOUND_BY_IDENTIFIER_FOR_LOGIN));
    otpService.sendOtp(request.getIdentifier(), httpRequest);
    return new HttpResponseApi.Ok<>(MessageConstant.Otp.OTP_SENT_SUCCESS);
  }

  /**
   * Xác thực mã OTP và hoàn tất quá trình đăng nhập.
   *
   * @param request đối tượng chứa định danh và mã OTP.
   * @param httpRequest đối tượng HttpServletRequest để lấy thông tin về request.
   * @return một ResponseEntity chứa token truy cập, token làm mới và thông tin chi tiết của người
   *     dùng.
   */
  @PostMapping(ApiConstant.LOGIN_OTP_VERIFY)
  public ResponseEntity<HttpResponseApi<AuthResponse>> verifyOtpAndLogin(
      @Valid @RequestBody final VerifyOtpRequest request, final HttpServletRequest httpRequest) {
    log.info(LogConstant.LOGIN_VERIFY_REQUEST, request.getIdentifier());
    verifyOtpOrThrow(request.getIdentifier(), request.getOtp());

    final User user =
        userService
            .findByIdentifier(request.getIdentifier())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        MessageConstant.User.NOT_FOUND_BY_IDENTIFIER, request.getIdentifier()));

    user.setLastLoginAt(Instant.now());
    userService.save(user);
    log.info(LogConstant.USER_LOGGED_IN_SUCCESS, user.getId());

    final String accessToken = jwtService.generateToken(user);
    final String refreshToken = jwtService.generateRefreshToken(user, httpRequest);
    final UserDetailResponse userDetails = userService.getUserDetails(user.getId());

    final AuthResponse authResponse = new AuthResponse(accessToken, refreshToken, userDetails);

    return new HttpResponseApi.Ok<>(MessageConstant.Auth.LOGIN_SUCCESS, authResponse);
  }

  /**
   * Làm mới access token bằng cách sử dụng refresh token.
   *
   * @param request đối tượng chứa refresh token.
   * @param httpRequest đối tượng HttpServletRequest để tạo refresh token mới.
   * @return một ResponseEntity chứa cặp token mới và thông tin người dùng.
   */
  @PostMapping(ApiConstant.REFRESH_TOKEN)
  public ResponseEntity<HttpResponseApi<AuthResponse>> refreshToken(
      @Valid @RequestBody final RefreshTokenRequest request, final HttpServletRequest httpRequest) {
    final AuthResponse refreshedTokens =
        jwtService.refreshToken(request.getRefreshToken(), httpRequest);
    return new HttpResponseApi.Ok<>(MessageConstant.Auth.TOKEN_REFRESH_SUCCESS, refreshedTokens);
  }

  /**
   * Đăng xuất người dùng bằng cách vô hiệu hóa refresh token.
   *
   * @param request đối tượng chứa refresh token cần vô hiệu hóa.
   * @return một ResponseEntity cho biết đã đăng xuất thành công.
   */
  @PostMapping(ApiConstant.LOGOUT)
  public ResponseEntity<HttpResponseApi<Void>> logout(
      @Valid @RequestBody final RefreshTokenRequest request) {
    jwtService.logout(request.getRefreshToken());
    log.info(LogConstant.USER_LOGGED_OUT_SUCCESS);
    return new HttpResponseApi.Ok<>(MessageConstant.Auth.LOGOUT_SUCCESS);
  }

  /**
   * Phương thức trợ giúp để xác thực OTP và ném ra ngoại lệ nếu không hợp lệ.
   *
   * @param identifier định danh để xác thực.
   * @param otp mã OTP để kiểm tra.
   */
  private void verifyOtpOrThrow(final String identifier, final String otp) {
    if (!otpService.verifyOtp(identifier, otp)) {
      throw new UnauthorizedException(MessageConstant.Auth.INVALID_OR_EXPIRED_OTP);
    }
  }
}
