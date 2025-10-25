package com.natswarchuan.payment.demo.dto.response.user;

import com.natswarchuan.payment.demo.dto.response.wallet.WalletSummaryResponse;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.entity.UserProfile;
import com.natswarchuan.payment.demo.interfaces.IDto;
import java.time.Instant;
import java.util.Set;
import lombok.Data;

@Data
public class UserDetailResponse implements IDto<User> {

  private Long id;
  private String email;
  private String phoneNumber;
  private Instant createdAt;
  private Instant lastLoginAt;
  private WalletSummaryResponse wallet;
  private Set<String> roles;
  private Set<String> permissions;

  private String nickName;
  private String fullName;
  private int gender;
  private String avatar;
  private String bio;

  @Override
  public void fromEntity(User entity) {
    this.id = entity.getId();
    this.email = entity.getEmail();
    this.phoneNumber = entity.getPhoneNumber();
    this.createdAt = entity.getCreatedAt();
    this.lastLoginAt = entity.getLastLoginAt();

    if (entity.getUserProfile() != null) {
      UserProfile profile = entity.getUserProfile();
      this.nickName = profile.getNickName();
      this.fullName = profile.getFullName();
      this.gender = profile.getGender();
      this.avatar = profile.getAvatar();
      this.bio = profile.getBio();
    }

    if (entity.getWallet() != null) {
      this.wallet = new WalletSummaryResponse();
      this.wallet.fromEntity(entity.getWallet());
    }
  }
}
