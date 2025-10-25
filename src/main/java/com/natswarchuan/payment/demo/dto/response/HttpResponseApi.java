package com.natswarchuan.payment.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpResponseApi<T> {

  private int statusCode;
  private String message;
  private T data;

  public HttpResponseApi(HttpStatus status, String message, T data) {
    this.statusCode = status.value();
    this.message = message;
    this.data = data;
  }

  public HttpResponseApi(HttpStatus status, String message) {
    this.statusCode = status.value();
    this.message = message;
  }

  public static class Ok<T> extends ResponseEntity<HttpResponseApi<T>> {

    public Ok(String message, T data) {
      super(new HttpResponseApi<>(HttpStatus.OK, message, data), HttpStatus.OK);
    }

    public Ok(String message) {
      super(new HttpResponseApi<>(HttpStatus.OK, message), HttpStatus.OK);
    }
  }

  public static class Created<T> extends ResponseEntity<HttpResponseApi<T>> {

    public Created(String message, T data) {
      super(new HttpResponseApi<>(HttpStatus.CREATED, message, data), HttpStatus.CREATED);
    }

    public Created(String message) {
      super(new HttpResponseApi<>(HttpStatus.CREATED, message), HttpStatus.CREATED);
    }
  }

  public static class Accepted<T> extends ResponseEntity<HttpResponseApi<T>> {

    public Accepted(String message, T data) {
      super(new HttpResponseApi<>(HttpStatus.ACCEPTED, message, data), HttpStatus.ACCEPTED);
    }

    public Accepted(String message) {
      super(new HttpResponseApi<>(HttpStatus.ACCEPTED, message), HttpStatus.ACCEPTED);
    }
  }

  public static class NoContent extends ResponseEntity<Void> {

    public NoContent() {
      super(HttpStatus.NO_CONTENT);
    }
  }

  public static class BadRequest<T> extends ResponseEntity<HttpResponseApi<T>> {

    public BadRequest(String message) {
      super(new HttpResponseApi<>(HttpStatus.BAD_REQUEST, message), HttpStatus.BAD_REQUEST);
    }
  }

  public static class Unauthorized<T> extends ResponseEntity<HttpResponseApi<T>> {

    public Unauthorized(String message) {
      super(new HttpResponseApi<>(HttpStatus.UNAUTHORIZED, message), HttpStatus.UNAUTHORIZED);
    }
  }

  public static class Forbidden<T> extends ResponseEntity<HttpResponseApi<T>> {

    public Forbidden(String message) {
      super(new HttpResponseApi<>(HttpStatus.FORBIDDEN, message), HttpStatus.FORBIDDEN);
    }
  }

  public static class NotFound<T> extends ResponseEntity<HttpResponseApi<T>> {

    public NotFound(String message) {
      super(new HttpResponseApi<>(HttpStatus.NOT_FOUND, message), HttpStatus.NOT_FOUND);
    }
  }

  public static class Conflict<T> extends ResponseEntity<HttpResponseApi<T>> {

    public Conflict(String message) {
      super(new HttpResponseApi<>(HttpStatus.CONFLICT, message), HttpStatus.CONFLICT);
    }
  }
}
