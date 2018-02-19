package me.weey.graduationproject.server.dao.inter;

import me.weey.graduationproject.server.entity.UserStatus;
import org.springframework.stereotype.Repository;

/**
 * user_status表的dao操作
 * Created by WeiKai on 2018/01/24.
 */
@Repository
public interface UserStatusDao {

    /**
     * 创建条目
     */
    int addUserStatus(UserStatus userStatus);

    /**
     * 修改状态
     */
    int modifyStatus(UserStatus userStatus);

    /**
     * 根据激活码查找
     */
    UserStatus findByActiveCode(String activeCode);

    /**
     * 根据id查找
     */
    UserStatus findById(String id);
}
