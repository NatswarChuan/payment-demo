package com.natswarchuan.payment.demo.dto.response.user;

import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.entity.UserProfile;
import com.natswarchuan.payment.demo.interfaces.IDto;
import lombok.Data;

@Data
public class UserSummaryResponse implements IDto<User> {

  private Long id;
  private String nickName;
  private String fullName;
  private String avatar;

  @Override
  public void fromEntity(User entity) {
    this.id = entity.getId();
    UserProfile profile = entity.getUserProfile();
    if (profile != null) {
      this.nickName = profile.getNickName();
      this.fullName = profile.getFullName();
      this.avatar = profile.getAvatar();
    }
  }
}
