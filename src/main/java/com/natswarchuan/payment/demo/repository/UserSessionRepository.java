package com.natswarchuan.payment.demo.repository;

import com.natswarchuan.payment.demo.entity.UserSession;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends CrudRepository<UserSession, String> {

  Optional<UserSession> findById(String refreshToken);

  List<UserSession> findByUserId(Long userId);

  void deleteAllByUserId(Long userId);
}
