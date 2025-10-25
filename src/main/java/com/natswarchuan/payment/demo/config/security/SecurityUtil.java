package com.natswarchuan.payment.demo.config.security;

import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.constant.SecurityConstant;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.exception.HttpException;
import com.natswarchuan.payment.demo.interfaces.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

  private final IUserService userService;

  public User getCurrentAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || SecurityConstant.ANONYMOUS_USER.equals(authentication.getPrincipal())) {
      throw new HttpException(HttpStatus.UNAUTHORIZED, MessageConstant.Auth.UNAUTHORIZED);
    }

    String identifier = authentication.getName();
    return userService
        .findByIdentifier(identifier)
        .orElseThrow(
            () ->
                new HttpException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format(MessageConstant.User.AUTHENTICATED_USER_NOT_IN_DB, identifier)));
  }
}
