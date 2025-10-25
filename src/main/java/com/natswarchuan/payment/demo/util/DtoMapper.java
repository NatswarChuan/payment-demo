package com.natswarchuan.payment.demo.util;

import com.natswarchuan.payment.demo.interfaces.IDto;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class DtoMapper {

  private DtoMapper() {}

  public static <E, D extends IDto<E>> List<D> toDtoList(
      List<E> entities, Supplier<D> dtoSupplier) {
    if (entities == null || entities.isEmpty()) {
      return Collections.emptyList();
    }
    return entities.stream()
        .map(
            entity -> {
              D dto = dtoSupplier.get();
              dto.fromEntity(entity);
              return dto;
            })
        .collect(Collectors.toList());
  }
}
