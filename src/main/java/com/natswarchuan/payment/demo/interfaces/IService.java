package com.natswarchuan.payment.demo.interfaces;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

public interface IService<E, ID> {

  void delete(ID id);

  <S extends IDto<E>> List<S> findAll(Class<S> dtoClass);

  <S extends IDto<E>> S findById(ID id, Class<S> dtoClass);

  List<E> findAll();

  Page<E> findAll(int page, int size);

  E findById(ID id);

  <S extends IDto<E>> void create(S newEntity);

  <S extends IDto<E>> void update(S updateEntity, ID id);

  <S extends IDto<E>> void delete(S deleteEntity, ID id);

  void create(E newEntity);

  void update(E updateEntity, ID id);

  void delete(E deleteEntity, ID id);

  List<E> findAllById(Collection<ID> ids);

  <S extends IDto<E>> Page<S> findAll(int page, int size, Class<S> dtoClass);

  <D extends IDto<E>, S extends IDto<E>> D create(S newEntity, Class<D> dtoClass);

  <RQ extends IDto<E>, RP extends IDto<E>> RP update(RQ updateEntity, ID id, Class<RP> rsClass);

  <S extends IDto<E>> Page<S> findAll(int page, int size, Class<S> dtoClass, String language);

  <S extends IDto<E>> Page<S> findAll(
      int page, int size, Specification<E> spec, Class<S> dtoClass, String language);

  <S extends IDto<E>> Page<S> findAll(int page, int size, Specification<E> spec, Class<S> dtoClass);

  E save(E entity);
}
