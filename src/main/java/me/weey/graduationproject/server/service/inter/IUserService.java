package me.weey.graduationproject.server.service.inter;

import me.weey.graduationproject.server.entity.Avatar;
import me.weey.graduationproject.server.entity.User;
import me.weey.graduationproject.server.entity.UserStatus;

import java.util.List;

/**
 * 有关用户的Service Interface
 * Created by WeiKai on 2018/01/24.
 */
public interface IUserService {

    /**
     * 新增用户
     */
    public Integer register(User user, UserStatus userStatus, Avatar avatar);

    /**
     * 对用户校验数据是否有效
     */
    public boolean checkUser(User user);

    /**
     * 查找三个主键对应的那个用户
     */
    public User findUser(String id, String userName, String email);

    /**
     * 激活账户
     */
    public Integer activeAccount(String activeCode);

    /**
     * 使用salt对给定文本进行SHA512混淆
     */
    public String saltEncryption(String salt, String rawString);

    /**
     * 根据id找到头像
     */
    public Avatar getAvatar(String id);

    /**
     * 根据用户id获取这个用户的所有好友
     */
    List<User> getFriends(String userID);

    /**
     * 添加好友
     */
    int addFriend(String myID, String friendID);
}
