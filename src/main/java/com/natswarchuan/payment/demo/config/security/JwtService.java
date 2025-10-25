package com.natswarchuan.payment.demo.config.security;

import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.constant.SecurityConstant;
import com.natswarchuan.payment.demo.dto.response.auth.AuthResponse;
import com.natswarchuan.payment.demo.dto.response.user.UserDetailResponse;
import com.natswarchuan.payment.demo.entity.Role;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.entity.UserSession;
import com.natswarchuan.payment.demo.exception.custom.ResourceNotFoundException;
import com.natswarchuan.payment.demo.exception.custom.UnauthorizedException;
import com.natswarchuan.payment.demo.interfaces.services.IUserService;
import com.natswarchuan.payment.demo.repository.UserRepository;
import com.natswarchuan.payment.demo.repository.UserSessionRepository;
import com.natswarchuan.payment.demo.util.HttpUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dịch vụ chịu trách nhiệm tạo, xác thực và quản lý JSON Web Tokens (JWT).
 *
 * <p>Bao gồm việc tạo access token, refresh token, và xử lý logic làm mới token.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

  @Value("${jwt.secret}")
  private String SECRET_KEY;

  @Value("${jwt.access-token-expiration-ms}")
  private long accessTokenExpiration;

  @Value("${jwt.refresh-token-expiration-days}")
  private long refreshTokenExpirationDays;

  private SecretKey key;

  private final UserSessionRepository userSessionRepository;
  private final UserRepository userRepository;
  private final IUserService userService;

  /** Khởi tạo secret key từ chuỗi cấu hình sau khi service được khởi tạo. */
  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
  }

  /**
   * Trích xuất tên người dùng (username) từ một JWT.
   *
   * @param token Chuỗi JWT.
   * @return Tên người dùng.
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Trích xuất một claim cụ thể từ JWT bằng một hàm resolver.
   *
   * @param token Chuỗi JWT.
   * @param claimsResolver Hàm để xử lý và trích xuất claim.
   * @param <T> Kiểu dữ liệu của claim.
   * @return Claim đã được trích xuất.
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Tạo một access token mới cho một người dùng.
   *
   * @param userDetails Chi tiết người dùng.
   * @return Chuỗi access token.
   */
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  /**
   * Tạo một access token mới với các claim bổ sung.
   *
   * @param extraClaims Các claim bổ sung để đưa vào token.
   * @param userDetails Chi tiết người dùng.
   * @return Chuỗi access token.
   */
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    User user = (User) userDetails;
    extraClaims.put(SecurityConstant.JWT_CLAIM_USER_ID, user.getId());
    extraClaims.put(
        SecurityConstant.JWT_CLAIM_ROLES,
        user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Tạo và lưu một refresh token mới cho người dùng.
   *
   * <p>Cho phép người dùng có nhiều phiên đăng nhập cùng lúc trên các thiết bị khác nhau.
   *
   * @param userDetails Chi tiết người dùng.
   * @param request HttpServletRequest để lấy thông tin client.
   * @return Chuỗi refresh token.
   */
  @Transactional
  public String generateRefreshToken(UserDetails userDetails, HttpServletRequest request) {
    User user = (User) userDetails;

    Instant now = Instant.now();
    Instant expiration = now.plus(refreshTokenExpirationDays, ChronoUnit.DAYS);

    String refreshToken =
        Jwts.builder()
            .setSubject(userDetails.getUsername())
            .claim(SecurityConstant.JWT_CLAIM_USER_ID, user.getId())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiration))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

    UserSession userSession =
        UserSession.builder()
            .id(refreshToken)
            .userId(user.getId())
            .ipAddress(HttpUtil.getClientIp(request))
            .userAgent(request.getHeader(SecurityConstant.HEADER_USER_AGENT))
            .createdAt(now)
            .expiresAt(expiration)
            .build();
    userSessionRepository.save(userSession);
    return refreshToken;
  }

  /**
   * Kiểm tra xem một access token có hợp lệ cho một người dùng cụ thể hay không.
   *
   * @param token Chuỗi access token.
   * @param userDetails Chi tiết người dùng để so sánh.
   * @return {@code true} nếu token hợp lệ, ngược lại {@code false}.
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  /**
   * Làm mới access token và refresh token.
   *
   * <p>Phương thức này xác thực refresh token hiện tại, xóa nó đi và tạo một cặp token mới.
   *
   * @param refreshToken Refresh token cũ.
   * @param request HttpServletRequest để tạo refresh token mới.
   * @return Một đối tượng {@link AuthResponse} chứa cặp token mới và thông tin người dùng.
   */
  public AuthResponse refreshToken(String refreshToken, HttpServletRequest request) {
    UserSession userSession =
        userSessionRepository
            .findById(refreshToken)
            .orElseThrow(
                () -> new UnauthorizedException(MessageConstant.Auth.INVALID_REFRESH_TOKEN));

    if (userSession.getExpiresAt().isBefore(Instant.now())) {
      userSessionRepository.delete(userSession);
      throw new UnauthorizedException(MessageConstant.Auth.REFRESH_TOKEN_EXPIRED);
    }

    User user =
        userRepository
            .findById(userSession.getUserId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        MessageConstant.User.USER_FOR_REFRESH_TOKEN_NOT_FOUND));

    String newAccessToken = generateToken(user);
    userSessionRepository.delete(userSession);
    String newRefreshToken = generateRefreshToken(user, request);

    UserDetailResponse userDetails = userService.getUserDetails(user.getId());

    return new AuthResponse(newAccessToken, newRefreshToken, userDetails);
  }

  /**
   * Đăng xuất người dùng bằng cách xóa refresh token (phiên đăng nhập) khỏi Redis.
   *
   * @param refreshToken Refresh token của phiên cần đăng xuất.
   */
  @Transactional
  public void logout(String refreshToken) {
    userSessionRepository.findById(refreshToken).ifPresent(userSessionRepository::delete);
  }
}
