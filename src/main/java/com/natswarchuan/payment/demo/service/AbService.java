package com.natswarchuan.payment.demo.service;

import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.exception.HttpException;
import com.natswarchuan.payment.demo.interfaces.IDto;
import com.natswarchuan.payment.demo.interfaces.IService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lớp cơ sở trừu tượng cung cấp các triển khai mặc định cho các hoạt động CRUD.
 *
 * <p><b>Cơ chế xóa mềm (Soft Delete):</b> Phương thức {@code delete} trong lớp này sẽ tự động kích
 * hoạt cơ chế xóa mềm nếu thực thể (entity) tương ứng được cấu hình với {@code @SQLDelete} và
 * {@code @Where}.
 *
 * @param <E> Kiểu của thực thể.
 * @param <ID> Kiểu của khóa chính của thực thể.
 * @author NatswarChuan
 */
@Service
@Transactional(rollbackFor = Exception.class)
public abstract class AbService<E, ID> implements IService<E, ID> {

  protected final JpaRepository<E, ID> repository;
  protected final JpaSpecificationExecutor<E> specificationExecutor;

  protected <R extends JpaRepository<E, ID> & JpaSpecificationExecutor<E>> AbService(R repository) {
    this.repository = repository;
    this.specificationExecutor = repository;
  }

  @Override
  public <S extends IDto<E>> List<S> findAll(Class<S> dtoClass) {
    List<E> data = repository.findAll();
    List<S> result = new ArrayList<>();
    for (E e : data) {
      try {
        S s = dtoClass.getDeclaredConstructor().newInstance();
        s.fromEntity(e);
        result.add(s);
      } catch (Exception ex) {
        throw new HttpException(
            HttpStatus.INTERNAL_SERVER_ERROR, MessageConstant.General.DTO_CONVERSION_ERROR);
      }
    }
    return result;
  }

  @Override
  public <S extends IDto<E>> S findById(ID id, Class<S> dtoClass) {
    E result = this.findById(id);
    try {
      S s = dtoClass.getDeclaredConstructor().newInstance();
      s.fromEntity(result);
      return s;
    } catch (Exception ex) {
      throw new HttpException(
          HttpStatus.INTERNAL_SERVER_ERROR, MessageConstant.General.DTO_CONVERSION_ERROR);
    }
  }

  @Override
  public List<E> findAll() {
    return repository.findAll();
  }

  @Override
  public Page<E> findAll(int page, int size) {
    Pageable paging = PageRequest.of(page, size);
    return repository.findAll(paging);
  }

  @Override
  public <S extends IDto<E>> Page<S> findAll(int page, int size, Class<S> dtoClass) {
    Pageable paging = PageRequest.of(page, size);
    Page<E> entityPage = repository.findAll(paging);
    return entityPage.map(
        entity -> {
          try {
            S dto = dtoClass.getDeclaredConstructor().newInstance();
            dto.fromEntity(entity);
            return dto;
          } catch (Exception ex) {
            throw new HttpException(
                HttpStatus.INTERNAL_SERVER_ERROR, MessageConstant.General.DTO_CONVERSION_ERROR);
          }
        });
  }

  @Override
  public E findById(ID id) {
    return repository
        .findById(id)
        .orElseThrow(
            () ->
                new HttpException(
                    HttpStatus.NOT_FOUND,
                    String.format(MessageConstant.General.ENTITY_NOT_FOUND_BY_ID, id)));
  }

  @Override
  public <S extends IDto<E>> void create(S newEntity) {
    E ent = newEntity.toEntity();
    repository.save(ent);
  }

  @Override
  public <S extends IDto<E>> void update(S updateEntity, ID id) {
    E existingEntity = this.findById(id);
    E updatedEntity = updateEntity.updateEntity(existingEntity);
    repository.save(updatedEntity);
  }

  @Override
  public <S extends IDto<E>> void delete(S deleteEntity, ID id) {
    this.delete(id);
  }

  @Override
  public void create(E newEntity) {
    repository.save(newEntity);
  }

  @Override
  public void update(E updateEntity, ID id) {
    this.findById(id);
    repository.save(updateEntity);
  }

  @Override
  public void delete(E deleteEntity, ID id) {
    this.delete(id);
  }

  /**
   * Xóa một thực thể dựa trên ID.
   *
   * <p>Phương thức này sẽ thực hiện xóa mềm nếu thực thể đã được cấu hình với {@code @SQLDelete}.
   * Nó tìm thực thể trong cơ sở dữ liệu (các bản ghi đã xóa mềm sẽ được bỏ qua nhờ {@code
   * @Where}), sau đó gọi {@code repository.delete()}, hành động này sẽ kích hoạt lệnh SQL đã được
   * định nghĩa trong {@code @SQLDelete} để cập nhật trường {@code deleted_at}.
   *
   * @param id ID của thực thể cần xóa.
   */
  @Override
  public void delete(ID id) {
    E deleteEntity = this.findById(id);
    repository.delete(deleteEntity);
  }

  public E save(E entity) {
    return repository.save(entity);
  }

  @Override
  public List<E> findAllById(Collection<ID> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    return repository.findAllById(ids);
  }

  @Override
  public <D extends IDto<E>, S extends IDto<E>> D create(S newEntity, Class<D> dtoClass) {
    E ent = newEntity.toEntity();
    E savedEntity = repository.save(ent);
    try {
      D dto = dtoClass.getDeclaredConstructor().newInstance();
      dto.fromEntity(savedEntity);
      return dto;
    } catch (Exception ex) {
      throw new HttpException(
          HttpStatus.INTERNAL_SERVER_ERROR, MessageConstant.General.DTO_CONVERSION_ERROR);
    }
  }

  @Override
  public <RQ extends IDto<E>, RP extends IDto<E>> RP update(
      RQ updateEntity, ID id, Class<RP> rsClass) {
    try {
      E savedEntity = this.findById(id);
      savedEntity = updateEntity.updateEntity(savedEntity);
      savedEntity = repository.save(savedEntity);

      RP dto = rsClass.getDeclaredConstructor().newInstance();
      dto.fromEntity(savedEntity);
      return dto;
    } catch (Exception ex) {
      throw new HttpException(
          HttpStatus.INTERNAL_SERVER_ERROR, MessageConstant.General.DTO_CONVERSION_ERROR);
    }
  }

  @Override
  public <S extends IDto<E>> Page<S> findAll(
      int page, int size, Class<S> dtoClass, String language) {
    Pageable paging = PageRequest.of(page, size);
    Page<E> entityPage = repository.findAll(paging);
    return entityPage.map(
        entity -> {
          try {
            S dto = dtoClass.getDeclaredConstructor().newInstance();
            dto.fromEntity(entity, language);
            return dto;
          } catch (Exception ex) {
            throw new HttpException(
                HttpStatus.INTERNAL_SERVER_ERROR, MessageConstant.General.DTO_CONVERSION_ERROR);
          }
        });
  }

  @Override
  public <S extends IDto<E>> Page<S> findAll(
      int page, int size, Specification<E> spec, Class<S> dtoClass, String language) {
    Pageable paging = PageRequest.of(page, size);
    Page<E> entityPage = specificationExecutor.findAll(spec, paging);

    return entityPage.map(
        entity -> {
          try {
            S dto = dtoClass.getDeclaredConstructor().newInstance();
            dto.fromEntity(entity, language);
            return dto;
          } catch (Exception ex) {
            throw new HttpException(
                HttpStatus.INTERNAL_SERVER_ERROR, MessageConstant.General.DTO_CONVERSION_ERROR);
          }
        });
  }

  @Override
  public <S extends IDto<E>> Page<S> findAll(
      int page, int size, Specification<E> spec, Class<S> dtoClass) {
    Pageable paging = PageRequest.of(page, size);
    Page<E> entityPage = specificationExecutor.findAll(spec, paging);
    return entityPage.map(
        entity -> {
          try {
            S dto = dtoClass.getDeclaredConstructor().newInstance();
            dto.fromEntity(entity);
            return dto;
          } catch (Exception ex) {
            throw new HttpException(
                HttpStatus.INTERNAL_SERVER_ERROR, MessageConstant.General.DTO_CONVERSION_ERROR);
          }
        });
  }
}
