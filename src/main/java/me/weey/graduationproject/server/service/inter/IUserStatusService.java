package me.weey.graduationproject.server.service.inter;

import me.weey.graduationproject.server.entity.UserStatus;

/**
 * UserStatus相关的服务
 * Created by WeiKai on 2018/01/25.
 */
public interface IUserStatusService {

    UserStatus findUserStatus(String id);
}
