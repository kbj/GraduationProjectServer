package me.weey.graduationproject.server.dao.inter;

import me.weey.graduationproject.server.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * user表的dao操作
 * Created by WeiKai on 2018/01/24.
 */
@Repository
public interface UserDao {
    /**
     * 往数据库新增用户
     */
    int addUser(User user);

    /**
     * 根据ID、用户名、电子信箱查找用户
     */
    User findUser(@Param("id") String id, @Param("userName") String userName, @Param("email") String email);

    /**
     * 根据用户id获取这个用户的所有好友
     */
    List<String> getFriends(@Param("id") String id);
}
