package com.natswarchuan.payment.demo.helper;

import com.natswarchuan.payment.demo.constant.AppConfigConstant;
import com.natswarchuan.payment.demo.constant.LogConstant;
import com.natswarchuan.payment.demo.constant.SecurityConstant;
import com.natswarchuan.payment.demo.constant.UserConstant;
import com.natswarchuan.payment.demo.dto.request.auth.VerifyRegistrationRequest;
import com.natswarchuan.payment.demo.entity.Role;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.entity.UserProfile;
import com.natswarchuan.payment.demo.entity.Wallet;
import com.natswarchuan.payment.demo.exception.HttpException;
import com.natswarchuan.payment.demo.interfaces.services.IWalletService;
import com.natswarchuan.payment.demo.repository.RoleRepository;
import com.natswarchuan.payment.demo.repository.UserRepository;
import com.natswarchuan.payment.demo.util.IdentifierValidator;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Lớp helper chứa các logic phụ trợ cho {@link com.natswarchuan.payment.demo.service.UserService}.
 *
 * <p>Lớp này đóng gói các logic nghiệp vụ phức tạp liên quan đến việc tạo, kích hoạt lại và cập
 * nhật thông tin người dùng, giúp lớp service chính gọn gàng và tập trung vào việc điều phối.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UserHelper {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final IWalletService walletService;

  /**
   * Tạo một người dùng hoàn toàn mới trong hệ thống.
   *
   * @param request Dữ liệu đăng ký từ người dùng.
   * @return Thực thể {@link User} đã được tạo và lưu vào cơ sở dữ liệu.
   */
  public User createNewUser(VerifyRegistrationRequest request) {
    User newUser = new User();
    String identifier = request.getIdentifier();

    if (IdentifierValidator.isEmail(identifier)) {
      newUser.setEmail(identifier);
    } else {
      newUser.setPhoneNumber(identifier);
    }

    Role userRole =
        roleRepository
            .findByName(SecurityConstant.ROLE_USER)
            .orElseThrow(
                () ->
                    new HttpException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Default user role not found."));
    newUser.getRoles().add(userRole);

    newUser.setLastLoginAt(Instant.now());

    UserProfile userProfile = new UserProfile();
    updateProfileFromRequest(userProfile, request, identifier);
    userProfile.setUser(newUser);
    newUser.setUserProfile(userProfile);

    User savedUser = userRepository.save(newUser);
    log.info(LogConstant.USER_CREATED, savedUser.getId());

    walletService.createWalletForUser(savedUser);
    log.info(LogConstant.WALLET_CREATED_FOR_USER, savedUser.getId());

    return savedUser;
  }

  /**
   * Kích hoạt lại một tài khoản người dùng đã bị xóa mềm trước đó.
   *
   * @param user Thực thể {@link User} cần được kích hoạt lại.
   * @param request Dữ liệu đăng ký mới để cập nhật thông tin.
   * @return Thực thể {@link User} đã được khôi phục và cập nhật.
   */
  public User reactivateUser(User user, VerifyRegistrationRequest request) {
    user.setDeletedAt(null);
    user.setLastLoginAt(Instant.now());
    user.setEkycStatus(UserConstant.EkycStatus.NOT_VERIFIED); 

    UserProfile profile = user.getUserProfile();
    if (profile == null) {
      profile = new UserProfile();
      profile.setUser(user);
      user.setUserProfile(profile);
    }
    updateProfileFromRequest(profile, request, request.getIdentifier());

    Wallet wallet = user.getWallet();
    if (wallet != null) {
      wallet.setStatus(com.natswarchuan.payment.demo.constant.WalletConstant.Status.ACTIVE);
    }

    log.info("Reactivated user with ID: {}", user.getId());
    return userRepository.save(user);
  }

  /**
   * Cập nhật thông tin {@link UserProfile} từ một đối tượng yêu cầu.
   *
   * @param profile {@link UserProfile} cần được cập nhật.
   * @param request Dữ liệu chứa thông tin mới.
   * @param identifier Định danh của người dùng (email/số điện thoại).
   */
  private void updateProfileFromRequest(
      UserProfile profile, VerifyRegistrationRequest request, String identifier) {
    String nickName = request.getNickName();
    if (nickName == null || nickName.isBlank()) {
      if (IdentifierValidator.isEmail(identifier)) {
        profile.setNickName(identifier.split(AppConfigConstant.EMAIL_AT_SIGN)[0]);
      } else {
        profile.setNickName(identifier);
      }
    } else {
      profile.setNickName(nickName);
    }
    profile.setFullName(request.getFullName());
  }
}
