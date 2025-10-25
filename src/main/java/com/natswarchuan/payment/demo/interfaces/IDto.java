package com.natswarchuan.payment.demo.interfaces;

import com.natswarchuan.payment.demo.constant.MessageConstant;

public interface IDto<E> {

  default E toEntity() {
    throw new UnsupportedOperationException(MessageConstant.General.DTO_TO_ENTITY_UNSUPPORTED);
  }

  default void fromEntity(E entity) {
    throw new UnsupportedOperationException(MessageConstant.General.ENTITY_TO_DTO_UNSUPPORTED);
  }

  default E updateEntity(E entity) {
    throw new UnsupportedOperationException(
        MessageConstant.General.UPDATE_ENTITY_FROM_DTO_UNSUPPORTED);
  }

  default void fromEntity(E entity, String language) {
    fromEntity(entity);
  }
}
