package com.natswarchuan.payment.demo.config.interceptor;

import com.natswarchuan.payment.demo.constant.AppConfigConstant;
import com.natswarchuan.payment.demo.constant.LogConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    long startTime = System.currentTimeMillis();
    request.setAttribute(AppConfigConstant.REQUEST_ATTRIBUTE_START_TIME, startTime);
    log.info(
        LogConstant.INTERCEPTOR_START_PROCESSING, request.getRequestURI(), request.getMethod());
    return true;
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {}

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    long startTime = (Long) request.getAttribute(AppConfigConstant.REQUEST_ATTRIBUTE_START_TIME);
    long endTime = System.currentTimeMillis();
    long executeTime = endTime - startTime;

    log.info(
        LogConstant.INTERCEPTOR_FINISH_PROCESSING,
        request.getRequestURI(),
        response.getStatus(),
        executeTime);

    if (ex != null) {
      log.error(LogConstant.INTERCEPTOR_REQUEST_ERROR, ex.getMessage());
    }
  }
}
