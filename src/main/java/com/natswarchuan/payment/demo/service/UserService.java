package com.natswarchuan.payment.demo.service;

import com.natswarchuan.payment.demo.constant.LogConstant;
import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.constant.SecurityConstant;
import com.natswarchuan.payment.demo.constant.UserConstant;
import com.natswarchuan.payment.demo.dto.request.auth.VerifyRegistrationRequest;
import com.natswarchuan.payment.demo.dto.request.user.UserSearchRequest;
import com.natswarchuan.payment.demo.dto.response.user.UserDetailResponse;
import com.natswarchuan.payment.demo.dto.response.user.UserSummaryResponse;
import com.natswarchuan.payment.demo.entity.Role;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.exception.HttpException;
import com.natswarchuan.payment.demo.exception.custom.BadRequestException;
import com.natswarchuan.payment.demo.exception.custom.ConflictException;
import com.natswarchuan.payment.demo.exception.custom.ResourceNotFoundException;
import com.natswarchuan.payment.demo.helper.UserHelper;
import com.natswarchuan.payment.demo.interfaces.services.IUserService;
import com.natswarchuan.payment.demo.repository.RoleRepository;
import com.natswarchuan.payment.demo.repository.RoleRepository.AuthorityProjection;
import com.natswarchuan.payment.demo.repository.UserRepository;
import com.natswarchuan.payment.demo.repository.specifications.UserSpecifications;
import com.natswarchuan.payment.demo.util.IdentifierValidator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lớp triển khai cho {@link IUserService}, xử lý các logic nghiệp vụ liên quan đến người dùng.
 *
 * <p>Bao gồm các chức năng như tạo người dùng, tìm kiếm, lấy thông tin chi tiết và quản lý trạng
 * thái eKYC.
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class UserService extends AbService<User, Long> implements IUserService {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final UserHelper userHelper;
  private final UserSpecifications userSpecifications;

  /**
   * Khởi tạo UserService với các dependency cần thiết.
   *
   * @param repository repository cho User.
   * @param roleRepository repository cho Role.
   * @param userHelper helper chứa các logic nghiệp vụ phụ trợ.
   * @param userSpecifications lớp chứa các Specification để xây dựng truy vấn động.
   */
  public UserService(
      final UserRepository repository,
      final RoleRepository roleRepository,
      final UserHelper userHelper,
      final UserSpecifications userSpecifications) {
    super(repository);
    this.userRepository = repository;
    this.roleRepository = roleRepository;
    this.userHelper = userHelper;
    this.userSpecifications = userSpecifications;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    log.debug(LogConstant.USER_SERVICE_LOAD_USER_NOTE, username);
    return this.findByIdentifier(username)
        .orElseThrow(
            () ->
                new UsernameNotFoundException(
                    String.format(MessageConstant.User.NOT_FOUND_BY_IDENTIFIER, username)));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public User createUser(final VerifyRegistrationRequest request) {
    final String identifier = request.getIdentifier();
    log.info(LogConstant.CREATING_USER, identifier);

    if (!IdentifierValidator.isValid(identifier)) {
      throw new BadRequestException(MessageConstant.User.INVALID_IDENTIFIER_FORMAT);
    }

    final Optional<User> existingUserOpt = findByIdentifierWithSoftDeleted(identifier);

    if (existingUserOpt.isPresent()) {
      final User existingUser = existingUserOpt.get();
      if (existingUser.getDeletedAt() == null) {
        throw new ConflictException(MessageConstant.User.USER_ALREADY_EXISTS);
      } else {
        return userHelper.reactivateUser(existingUser, request);
      }
    } else {
      return userHelper.createNewUser(request);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<User> findByIdentifier(final String identifier) {
    if (IdentifierValidator.isEmail(identifier)) {
      return userRepository.findByEmail(identifier);
    } else if (IdentifierValidator.isPhoneNumber(identifier)) {
      return userRepository.findByPhoneNumber(identifier);
    }
    return Optional.empty();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<User> findByIdentifierWithSoftDeleted(final String identifier) {
    if (IdentifierValidator.isEmail(identifier)) {
      return userRepository.findWithSoftDeletedByEmail(identifier);
    } else if (IdentifierValidator.isPhoneNumber(identifier)) {
      return userRepository.findWithSoftDeletedByPhoneNumber(identifier);
    }
    return Optional.empty();
  }

  /**
   * Lấy thông tin chi tiết của một người dùng, bao gồm cả vai trò và quyền hạn theo hệ thống phân
   * cấp.
   *
   * <p>Phương thức này đã được tối ưu hóa để giảm số lượng truy vấn đến cơ sở dữ liệu. Nó thực hiện
   * một truy vấn để lấy thông tin User và Wallet, sau đó thực hiện thêm một truy vấn duy nhất để
   * lấy toàn bộ cây vai trò và quyền hạn của người dùng đó.
   *
   * @param userId ID của người dùng cần lấy thông tin.
   * @return một đối tượng {@code UserDetailResponse} chứa thông tin chi tiết.
   */
  @Override
  @Transactional(readOnly = true)
  public UserDetailResponse getUserDetails(final Long userId) {
    final User user =
        userRepository
            .findWithWalletById(userId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        MessageConstant.General.ENTITY_NOT_FOUND_BY_ID, userId));

    final Set<String> permissions = new HashSet<>();
    final Set<String> roles = new HashSet<>();
    final List<AuthorityProjection> results =
        roleRepository.findAuthoritiesInHierarchyByUserId(user.getId());

    for (final AuthorityProjection result : results) {
      final String authority = result.getAuthority();
      final String type = result.getType();
      if ("ROLE".equals(type)) {
        roles.add(authority);
      } else if ("PERMISSION".equals(type)) {
        permissions.add(authority);
      }
    }

    final UserDetailResponse userDetail = new UserDetailResponse();
    userDetail.fromEntity(user);
    userDetail.setPermissions(permissions);
    userDetail.setRoles(roles);

    return userDetail;
  }

  /** {@inheritDoc} */
  @Override
  public void completeEkyc(final Long userId) {
    final User user = findById(userId);
    if (user.getEkycStatus().equals(UserConstant.EkycStatus.VERIFIED)) {
      throw new ConflictException(MessageConstant.User.EKYC_ALREADY_VERIFIED);
    }

    final Role verifiedRole =
        roleRepository
            .findByName(SecurityConstant.ROLE_VERIFIED_USER)
            .orElseThrow(
                () ->
                    new HttpException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Verified user role not found."));

    user.getRoles().add(verifiedRole);
    user.setEkycStatus(UserConstant.EkycStatus.VERIFIED);
    save(user);

    log.info("eKYC completed and VERIFIED_USER role assigned for user ID: {}", userId);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public Page<UserSummaryResponse> searchUsers(
      final UserSearchRequest searchRequest, final Pageable pageable) {
    final Specification<User> spec = userSpecifications.fromSearchRequest(searchRequest);
    final Page<User> userPage = userRepository.findAll(spec, pageable);
    return userPage.map(
        user -> {
          final UserSummaryResponse dto = new UserSummaryResponse();
          dto.fromEntity(user);
          return dto;
        });
  }
}
