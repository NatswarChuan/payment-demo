package com.natswarchuan.payment.demo.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.natswarchuan.payment.demo.constant.VnpayConstant;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;

/**
 * Lớp tiện ích chứa các hàm hỗ trợ cho việc tích hợp với cổng thanh toán VNPAY.
 *
 * <p>Bao gồm các chức năng như tạo chữ ký HmacSHA512, tạo chuỗi truy vấn URL, và các hàm tiện ích
 * khác để xử lý dữ liệu theo định dạng VNPAY yêu cầu.
 */
@Slf4j
public final class VnpayUtil {

  private VnpayUtil() {}

  /**
   * Tạo chuỗi hash HmacSHA512 từ một khóa và dữ liệu.
   *
   * @param key khóa bí mật.
   * @param data dữ liệu cần hash.
   * @return chuỗi hash dưới dạng hex.
   */
  public static String hmacSHA512(final String key, final String data) {
    try {
      if (key == null || data == null) {
        throw new NullPointerException();
      }
      final Mac hmac512 = Mac.getInstance("HmacSHA512");
      final byte[] hmacKeyBytes = key.getBytes();
      final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
      hmac512.init(secretKey);
      final byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
      final byte[] result = hmac512.doFinal(dataBytes);
      final StringBuilder sb = new StringBuilder(2 * result.length);
      for (byte b : result) {
        sb.append(String.format("%02x", b & 0xff));
      }
      return sb.toString();

    } catch (Exception e) {
      throw new RuntimeException("Failed to generate HMAC-SHA512", e);
    }
  }

  /**
   * Lấy địa chỉ IP của client từ request.
   *
   * @param request đối tượng HttpServletRequest.
   * @return chuỗi địa chỉ IP.
   */
  public static String getIpAddress(final HttpServletRequest request) {
    String ipAddress;
    try {
      ipAddress = request.getHeader("X-FORWARDED-FOR");
      if (ipAddress == null) {
        ipAddress = request.getRemoteAddr();
      }
    } catch (Exception e) {
      ipAddress = "Invalid IP:" + e.getMessage();
    }
    return ipAddress;
  }

  /**
   * Tạo chuỗi hash cho tất cả các trường dữ liệu trong một Map.
   *
   * @param fields map chứa các trường dữ liệu.
   * @param secret khóa bí mật.
   * @return chuỗi hash.
   */
  public static String hashAllFields(final Map<String, String> fields, final String secret) {
    final List<String> fieldNames = new ArrayList<>(fields.keySet());
    Collections.sort(fieldNames);
    final StringBuilder sb = new StringBuilder();
    final Iterator<String> itr = fieldNames.iterator();
    while (itr.hasNext()) {
      String fieldName = itr.next();
      String fieldValue = fields.get(fieldName);
      if ((fieldValue != null) && (fieldValue.length() > 0)) {
        sb.append(fieldName);
        sb.append("=");
        sb.append(fieldValue);
      }
      if (itr.hasNext()) {
        sb.append("&");
      }
    }
    return hmacSHA512(secret, sb.toString());
  }

  /**
   * Tạo chuỗi hash cho tất cả các trường dữ liệu của một đối tượng DTO.
   *
   * @param dto đối tượng DTO.
   * @param secret khóa bí mật.
   * @return chuỗi hash.
   */
  public static String hashAllFields(final Object dto, final String secret) {
    final Map<String, String> fields = objectToMap(dto);
    fields.remove(VnpayConstant.VNP_SECURE_HASH);
    return hashAllFields(fields, secret);
  }

  /**
   * Chuyển đổi một đối tượng DTO thành một Map đã được sắp xếp.
   *
   * <p>Phương thức này sử dụng reflection để duyệt qua tất cả các trường (bao gồm cả lớp cha) của
   * đối tượng, lấy tên key từ annotation {@code @JsonProperty} và giá trị của trường đó.
   *
   * @param dto đối tượng cần chuyển đổi.
   * @return một {@code TreeMap} chứa các cặp key-value từ đối tượng.
   */
  public static Map<String, String> objectToMap(final Object dto) {
    final Map<String, String> fields = new TreeMap<>();
    Class<?> clazz = dto.getClass();

    while (clazz != null) {
      for (Field field : clazz.getDeclaredFields()) {
        field.setAccessible(true);
        try {
          JsonProperty annotation = field.getAnnotation(JsonProperty.class);
          if (annotation != null) {
            String key = annotation.value();
            Object value = field.get(dto);
            if (value != null) {
              fields.put(key, value.toString());
            }
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException("Không thể truy cập giá trị của trường để chuyển đổi", e);
        }
      }
      clazz = clazz.getSuperclass();
    }
    return fields;
  }

  /**
   * Xây dựng một chuỗi truy vấn (query string) URL từ một Map các tham số.
   *
   * <p>Các giá trị của tham số sẽ được mã hóa URL (URL-encoded) theo chuẩn UTF-8.
   *
   * @param params map chứa các tham số.
   * @return một chuỗi truy vấn đã được định dạng và mã hóa.
   */
  public static String createQueryString(final Map<String, String> params) {
    final StringBuilder query = new StringBuilder();
    params.forEach(
        (key, value) -> {
          try {
            query.append(URLEncoder.encode(key, StandardCharsets.UTF_8.name()));
            query.append('=');
            query.append(URLEncoder.encode(value, StandardCharsets.UTF_8.name()));
            query.append('&');
          } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is unknown", e);
          }
        });
    if (!query.isEmpty()) {
      query.setLength(query.length() - 1); 
    }
    return query.toString();
  }
}

