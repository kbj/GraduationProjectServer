package me.weey.graduationproject.server.service.impl;

import cn.hutool.core.util.StrUtil;
import me.weey.graduationproject.server.dao.inter.KeyDao;
import me.weey.graduationproject.server.dao.inter.UserStatusDao;
import me.weey.graduationproject.server.entity.UserStatus;
import me.weey.graduationproject.server.service.inter.IKeyService;
import me.weey.graduationproject.server.service.inter.IUserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserStatus相关的服务
 * Created by WeiKai on 2018/01/25.
 */
@Service
public class UserStatusServiceImpl implements IUserStatusService {

    @Autowired
    private UserStatusDao userStatusDao;
    @Autowired
    private UserStatus userStatus;

    @Override
    public UserStatus findUserStatus(String id) {
        if (StrUtil.hasEmpty(id) || id.length() != 32) {
            return null;
        }
        return userStatusDao.findById(id);
    }
}
