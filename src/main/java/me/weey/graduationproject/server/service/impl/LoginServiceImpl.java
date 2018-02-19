package me.weey.graduationproject.server.service.impl;

import cn.hutool.Hutool;
import cn.hutool.core.util.StrUtil;
import me.weey.graduationproject.server.entity.User;
import me.weey.graduationproject.server.entity.UserStatus;
import me.weey.graduationproject.server.service.inter.ILoginService;
import me.weey.graduationproject.server.service.inter.IUserService;
import me.weey.graduationproject.server.service.inter.IUserStatusService;
import me.weey.graduationproject.server.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Login部分的Socket的Service实现
 * Created by WeiKai on 2018/01/26.
 */
@Service
public class LoginServiceImpl implements ILoginService {

    private final IUserService userService;
    private final IUserStatusService userStatusService;

    @Autowired
    public LoginServiceImpl(IUserService userService, IUserStatusService userStatusService) {
        this.userService = userService;
        this.userStatusService = userStatusService;
    }

    /**
     * 登录
     */
    @Transactional(readOnly = true)
    @Override
    public User login(User user, WebSocketSession session) {
        //校验用户名和密码
        User fromUser = userService.findUser(user.getUserName(), user.getUserName(), user.getUserName());
        if (fromUser == null) {
            return null;
        }
        UserStatus userStatus = userStatusService.findUserStatus(fromUser.getId());
        //获取加密后的密码
        String encryptionPwd = userService.saltEncryption(userStatus.getActiveCode(), user.getPassword());
        //判断
        if (!encryptionPwd.equals(fromUser.getPassword())) {
            return null;
        }
        //判断是否已经登录了，如果已经有登录信息，那就需要把之前的session替换掉
        ConcurrentHashMap<String, WebSocketSession> map = Constant.getLoginSessionInstant();
        WebSocketSession socketSession = map.get(fromUser.getId());
        if (socketSession == null) {
            //新登录
            map.put(fromUser.getId(), session);
        } else {
            //把原先的session替换掉
            map.replace(fromUser.getId(), session);
        }

        //过滤敏感信息
        fromUser.setRegisterIp(null);
        fromUser.setPassword(null);
        fromUser.setIMEI(null);
        fromUser.setRegisterBrand(null);
        fromUser.setRegisterModel(null);
        return fromUser;
    }

}
