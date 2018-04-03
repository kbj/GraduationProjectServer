package me.weey.graduationproject.server.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import me.weey.graduationproject.server.dao.inter.AvatarDao;
import me.weey.graduationproject.server.dao.inter.UserDao;
import me.weey.graduationproject.server.dao.inter.UserStatusDao;
import me.weey.graduationproject.server.entity.Avatar;
import me.weey.graduationproject.server.entity.HttpResponse;
import me.weey.graduationproject.server.entity.User;
import me.weey.graduationproject.server.entity.UserStatus;
import me.weey.graduationproject.server.service.inter.IKeyService;
import me.weey.graduationproject.server.service.inter.IUserService;
import me.weey.graduationproject.server.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 有关用户的Service
 * Created by WeiKai on 2018/01/24.
 */
@Service
public class UserServiceImpl implements IUserService {

    private static final Log log = LogFactory.get();

    private final UserDao userDao;
    private final UserStatusDao userStatusDao;
    private final AvatarDao avatarDao;
    private final HttpResponse httpResponse;
    private final IKeyService keyService;
    private final Avatar avatarObject;

    @Autowired
    public UserServiceImpl(UserDao userDao, UserStatusDao userStatusDao, AvatarDao avatarDao, HttpResponse httpResponse, IKeyService keyService, Avatar avatarObject) {
        this.userDao = userDao;
        this.userStatusDao = userStatusDao;
        this.avatarDao = avatarDao;
        this.httpResponse = httpResponse;
        this.keyService = keyService;
        this.avatarObject = avatarObject;
    }

    /**
     * 新增用户
     */
    @Transactional
    @Override
    public Integer register(User user, UserStatus userStatus, Avatar avatar) {
        //校验传参是否符合要求
        if (!checkUser(user)) return Constant.CODE_CHECK_FAILURE;
        //性别校验
        boolean gender = user.getGender() >= Constant.GENDER_NONE && user.getGender() <= Constant.GENDER_FEMALE;
        //邮件地址正则校验
        boolean match = Validator.isEmail(user.getEmail());
        if (!match || !gender || avatar == null) return Constant.CODE_CHECK_FAILURE;
        //校验是否已经存在这个用户
        User dbUser = findUser(user.getId(), user.getUserName(), user.getEmail());
        if (dbUser != null) {
            if (dbUser.getUserName().equals(user.getUserName())) {
                return Constant.CODE_USERNAME_EXIST;
            } else if (dbUser.getEmail().equals(user.getEmail())) {
                return Constant.CODE_EMAIL_EXIST;
            }
            return Constant.CODE_FAILURE;
        }
        //加密密码
        user.setPassword(saltEncryption(userStatus.getActiveCode(), user.getPassword()));

        //加入数据库
        int i = userDao.addUser(user) + userStatusDao.addUserStatus(userStatus) + avatarDao.insertAvatar(avatar);

        //插入判断
        if (i != 3) return Constant.CODE_FAILURE;

        //异步发邮件
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                log.info("开始发送邮件到：" + user.getEmail());
                MailUtil.send(user.getEmail(), "您的 Email 地址的确认链接",
                        "十分感谢您的注册</br> <h2><a href=\"http://localhost:8080/account/activate/" + userStatus.getActiveCode() + "\">您可以点击这里完成激活</a></h2>",
                        true);
            }
        };
        ThreadUtil.excAsync(runnable, false);
        return i;
    }

    /**
     * 数据校验
     */
    @Override
    public boolean checkUser(User user) {
        if (StrUtil.hasEmpty(user.getId()) || StrUtil.hasEmpty(user.getUserName()) || StrUtil.hasEmpty(user.getEmail())) {
            return false;
        }
        return true;
    }

    /**
     * 根据三个参数查找数据库
     */
    @Override
    @Transactional(readOnly = true)
    public User findUser(String id, String userName, String email) {
        if (StrUtil.hasEmpty(id) && StrUtil.hasEmpty(userName) && StrUtil.hasEmpty(email)) {
            return null;
        }
        return userDao.findUser(id, userName, email);
    }

    /**
     * 激活账户
     */
    @Override
    @Transactional
    public Integer activeAccount(String activeCode) {
        //非空校验以及长度校验
        if (StrUtil.hasEmpty(activeCode) || activeCode.length() != 64) return Constant.ACCOUNT_STATUS_ERROR_ACTIVE_CODE;
        //根据验证码查找
        UserStatus userStatus = userStatusDao.findByActiveCode(activeCode);
        if (userStatus == null) {
            //非法数值判断
            return Constant.ACCOUNT_STATUS_ERROR_ACTIVE_CODE;
        } else if (userStatus.getUserStatus() != Constant.ACCOUNT_STATUS_NOT_ACTIVE) {
            return Constant.ACCOUNT_STATUS_ALREADY_ACTIVE;
        }
        //修改状态
        userStatus.setUserStatus(Constant.ACCOUNT_STATUS_NORMAL);
        return userStatusDao.modifyStatus(userStatus);
    }

    /**
     * 使用salt对给定文本进行SHA512混淆
     */
    @Override
    public String saltEncryption(String salt, String rawString) {
        if (StrUtil.hasEmpty(salt) || StrUtil.hasEmpty(rawString)) {
            return "";
        }
        Digester sha512 = new Digester(DigestAlgorithm.SHA512);
        return sha512.digestHex(salt + rawString);
    }

    @Override
    @Transactional(readOnly = true)
    public Avatar getAvatar(String id) {
        if (StrUtil.hasEmpty(id) || id.length() != 32) return null;
        return avatarDao.findById(id);
    }

    /**
     * 获取相对应用户的好友列表
     * @param userID 用户ID
     */
    @Override
    public List<User> getFriends(String userID) {
        if (StrUtil.hasEmpty(userID)) {
            return null;
        }
        //找到相对应的ID
        List<String> friendsId = userDao.getFriends(userID);
        if (friendsId == null) {
            return null;
        }
        //根据ID找到相对应的人，返回给controller
        ArrayList<User> usersList = new ArrayList<>();
        for (String id : friendsId) {
            User user = findUser(id, id, id);
            user.setRegisterModel("");
            user.setRegisterBrand("");
            user.setEmail("");
            user.setIMEI("");
            user.setRegisterIp("");
            usersList.add(user);
        }
        return usersList;
    }

    /**
     * 添加好友
     */
    @Transactional
    @Override
    public int addFriend(String myID, String friendID) {
        //获取本人的好友列表
        List<User> friends = getFriends(myID);
        for (User user : friends) {
            if (user.getId().equals(friendID)) {
                return Constant.CODE_FAILURE;
            }
        }
        //不是好友，那就加
        int i = userDao.addFriend(friendID, myID);
        if (i > 0) {
            return Constant.CODE_SUCCESS;
        }
        return Constant.CODE_FAILURE;
    }

    /**
     * 更新用户信息
     * @param infoType      信息类型
     * @param infoContent   信息内容
     * @param userID        对应用户ID
     * @param token         token值
     * @param avatar        头像
     * @return              更新结果
     */
    @Transactional
    @Override
    public HttpResponse updateInfo(Integer infoType, String infoContent, String userID, String token, byte[] avatar) {
        //判断该用户是否在线
        WebSocketSession socketSession = Constant.getLoginSessionInstant().get(userID);
        if (socketSession == null) {
            httpResponse.setStatusCode(Constant.CODE_CHECK_FAILURE);
            httpResponse.setMessage("登录状态错误!");
            httpResponse.setTime(new Date());
            log.info("错误参数，此userID并没有登录！" + userID);
            return httpResponse;
        }
        //计算Token
        String correctToken = DigestUtil.sha256Hex(userID + keyService.getPublicKey() + "token");
        if (!correctToken.equals(token)) {
            //token错误
            log.info("token校验错误，输入的token：" + token);
            log.info("真实的token为：" + correctToken);
            httpResponse.setStatusCode(Constant.CODE_CHECK_FAILURE);
            httpResponse.setTime(new Date());
            httpResponse.setMessage("token check error");
            return httpResponse;
        }
        //根据类型更新相应数据
        Integer updateResult = 0;
        switch (infoType) {
            case Constant.INFO_TYPE_AVATAR:
                //更新头像
                avatarObject.setId(userID);
                avatarObject.setAvatar(avatar);
                avatarDao.updateAvatar(avatarObject);
                updateResult = 1;
                break;
            case Constant.INFO_TYPE_GENDER:
                //更新性别
                updateResult = userDao.updateGender(userID, Integer.valueOf(infoContent.trim()));
                break;
            case Constant.INFO_TYPE_NAME:
                //更新名称
                //判断新改的名称是否重复
                User user = findUser(infoContent, infoContent, infoContent);
                if (user != null) {
                    httpResponse.setStatusCode(Constant.CODE_CHECK_FAILURE);
                    httpResponse.setMessage("已有人使用该名称啦！换个新的试试吧！");
                    httpResponse.setTime(new Date());
                    return httpResponse;
                } else {
                    //表示这个名称可以使用
                    updateResult = userDao.updateName(userID, infoContent);
                }
                break;
            case Constant.INFO_TYPE_BIO:
                //更新简介
                updateResult = userDao.updateBio(userID, infoContent);
                break;
        }
        //对结果判断
        if (updateResult == 1) {
            //更新成功要返回新的数据回去更新
            httpResponse.setStatusCode(Constant.CODE_SUCCESS);
            httpResponse.setTime(new Date());
            httpResponse.setMessage("更新成功!");
        } else {
            httpResponse.setStatusCode(Constant.CODE_CHECK_FAILURE);
            httpResponse.setMessage("更新失败！请重试！");
            httpResponse.setTime(new Date());
        }
        return httpResponse;
    }
}
