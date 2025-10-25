package com.natswarchuan.payment.demo.repository.specifications;

import com.natswarchuan.payment.demo.constant.EntityConstant;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.entity.Wallet;
import jakarta.persistence.criteria.Join;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class WalletSpecifications {

  public Specification<Wallet> hasStatus(Integer status) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(EntityConstant.WALLET_FIELD_STATUS), status);
  }

  public Specification<Wallet> hasCurrency(String currency) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(EntityConstant.WALLET_FIELD_CURRENCY), currency);
  }

  public Specification<Wallet> balanceGreaterThanOrEqual(BigDecimal amount) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.greaterThanOrEqualTo(root.get(EntityConstant.WALLET_FIELD_BALANCE), amount);
  }

  public Specification<Wallet> forUser(Long userId) {
    return (root, query, criteriaBuilder) -> {
      Join<Wallet, User> userJoin = root.join(EntityConstant.WALLET_FIELD_USER);
      return criteriaBuilder.equal(userJoin.get(EntityConstant.FIELD_ID), userId);
    };
  }
}
