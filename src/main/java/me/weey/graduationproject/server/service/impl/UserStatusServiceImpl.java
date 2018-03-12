package me.weey.graduationproject.server.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import me.weey.graduationproject.server.dao.inter.KeyDao;
import me.weey.graduationproject.server.dao.inter.OnlineStatusDao;
import me.weey.graduationproject.server.dao.inter.UserStatusDao;
import me.weey.graduationproject.server.entity.OnlineStatus;
import me.weey.graduationproject.server.entity.UserStatus;
import me.weey.graduationproject.server.service.inter.IKeyService;
import me.weey.graduationproject.server.service.inter.IUserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * UserStatus相关的服务
 * Created by WeiKai on 2018/01/25.
 */
@Service
public class UserStatusServiceImpl implements IUserStatusService {
    private static final Log log = LogFactory.get();
    private final UserStatusDao userStatusDao;
    private final OnlineStatusDao onlineStatusDao;
    private final OnlineStatus onlineStatus;

    @Autowired
    public UserStatusServiceImpl(UserStatusDao userStatusDao, OnlineStatusDao onlineStatusDao, OnlineStatus onlineStatus) {
        this.userStatusDao = userStatusDao;
        this.onlineStatusDao = onlineStatusDao;
        this.onlineStatus = onlineStatus;
    }

    @Override
    public UserStatus findUserStatus(String id) {
        if (StrUtil.hasEmpty(id) || id.length() != 32) {
            return null;
        }
        return userStatusDao.findById(id);
    }

    /**
     * 根据用户的ID查询用户的在线状态
     * @param id 用户ID
     */
    @Override
    public OnlineStatus findOnlineStatus(String id) {
        if (StrUtil.hasEmpty(id)) {
            return null;
        } else {
            OnlineStatus onlineStatus = onlineStatusDao.findByUserId(id);
            if (onlineStatus == null) return null;
            return onlineStatus;
        }
    }

    /**
     * 更新用户的最新登录登出的状态
     * @param id 用户ID
     */
    @Override
    @Transactional
    public void updateOnlineStatus(String id, Boolean isLogin) {
        if (StrUtil.hasEmpty(id)) return;
        //确认这个用户的状态是否在表中存在，不存在需要先新增
        OnlineStatus status = onlineStatusDao.findByUserId(id);
        if (status == null) {
            //新增条目
            onlineStatus.setOnline(isLogin);
            if (isLogin) {
                onlineStatus.setLogOutTime(null);
                onlineStatus.setLoginTime(new Date());
            } else {
                onlineStatus.setLogOutTime(new Date());
                onlineStatus.setLoginTime(null);
            }
            onlineStatus.setUserId(id);

            int i = onlineStatusDao.addNewUserStatus(onlineStatus);
            if (i > 0) {
                log.info("成功往online_status表新增了条目");
            }
        } else {
            //修改状态
            if (isLogin) {
                int i = onlineStatusDao.updateLoginStatus(id, new Date());
                log.info("修改了用户登录时间信息，更新的条目总数为：" + i);
            } else {
                int i = onlineStatusDao.updateLogOutStatus(id, new Date());
                log.info("修改了用户注销时间信息，更新的条目总数为：" + i);
            }
        }
    }
}
