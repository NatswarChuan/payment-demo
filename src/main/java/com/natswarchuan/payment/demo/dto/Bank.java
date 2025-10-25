package com.natswarchuan.payment.demo.dto;

/**
 * Record đại diện cho thông tin một ngân hàng được VNPAY hỗ trợ.
 *
 * @param code Mã ngân hàng theo quy định của VNPAY (vnp_BankCode).
 * @param name Tên đầy đủ của ngân hàng.
 * @param shortName Tên viết tắt của ngân hàng.
 */
public record Bank(String code, String name, String shortName) {}
