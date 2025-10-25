package com.natswarchuan.payment.demo.repository.specifications;

import com.natswarchuan.payment.demo.constant.EntityConstant;
import com.natswarchuan.payment.demo.entity.PaymentMethod;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodSpecifications {

  public Specification<PaymentMethod> forWallet(UUID walletId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(
            root.get(EntityConstant.PM_FIELD_WALLET).get(EntityConstant.FIELD_ID), walletId);
  }

  public Specification<PaymentMethod> hasType(Integer type) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(EntityConstant.PM_FIELD_TYPE), type);
  }

  public Specification<PaymentMethod> hasProvider(String provider) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.like(
            criteriaBuilder.lower(root.get(EntityConstant.PM_FIELD_PROVIDER)),
            "%" + provider.toLowerCase() + "%");
  }

  public Specification<PaymentMethod> isDefault(boolean isDefault) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(EntityConstant.PM_FIELD_IS_DEFAULT), isDefault);
  }
}
