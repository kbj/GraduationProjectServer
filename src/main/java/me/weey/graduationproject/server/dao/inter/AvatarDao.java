package me.weey.graduationproject.server.dao.inter;

import me.weey.graduationproject.server.entity.Avatar;
import me.weey.graduationproject.server.entity.Key;
import org.springframework.stereotype.Repository;

/**
 * Avatar表的dao
 * Created by WeiKai on 2018/01/25.
 */
@Repository
public interface AvatarDao {

    /**
     *  注册时候新增条目
     */
    int insertAvatar(Avatar avatar);

    /**
     * 根据id查找用户头像
     */
    Avatar findById(String id);

    /**
     * 更新头像
     */
    Integer updateAvatar(Avatar avatar);
}
