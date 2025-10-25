package com.natswarchuan.payment.demo.repository;

import com.natswarchuan.payment.demo.constant.EntityConstant;
import com.natswarchuan.payment.demo.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  @EntityGraph(attributePaths = {EntityConstant.USER_FIELD_ROLES, "userProfile"})
  Optional<User> findByEmail(String email);

  @EntityGraph(attributePaths = {EntityConstant.USER_FIELD_ROLES, "userProfile"})
  Optional<User> findByPhoneNumber(String phoneNumber);

  Optional<User> findByEmailOrPhoneNumber(String email, String phoneNumber);

  @EntityGraph(
      attributePaths = {
        EntityConstant.USER_FIELD_WALLET,
        "userProfile",
        EntityConstant.USER_FIELD_ROLES
      })
  Optional<User> findWithWalletById(Long id);

  /**
   * Tìm người dùng bằng email, bao gồm cả những người dùng đã bị xóa mềm.
   *
   * @param email Email cần tìm.
   * @return {@code Optional<User>} chứa người dùng nếu tìm thấy.
   */
  @Query(value = "SELECT * FROM users WHERE email = :email LIMIT 1", nativeQuery = true)
  Optional<User> findWithSoftDeletedByEmail(String email);

  /**
   * Tìm người dùng bằng số điện thoại, bao gồm cả những người dùng đã bị xóa mềm.
   *
   * @param phoneNumber Số điện thoại cần tìm.
   * @return {@code Optional<User>} chứa người dùng nếu tìm thấy.
   */
  @Query(
      value = "SELECT * FROM users WHERE phone_number = :phoneNumber LIMIT 1",
      nativeQuery = true)
  Optional<User> findWithSoftDeletedByPhoneNumber(String phoneNumber);
}

