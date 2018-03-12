package me.weey.graduationproject.server.dao.inter;

import me.weey.graduationproject.server.entity.OnlineStatus;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 关于用户在线状态的Dao接口
 * Created by WeiKai on 2018/03/12.
 */
@Repository
public interface OnlineStatusDao {

    /**
     * 根据用户id获取在线状态
     */
    OnlineStatus findByUserId(@Param("userID") String userID);

    /**
     * 更新登录成功的时间
     * @param id        用户id
     * @param time      时间
     */
    int updateLoginStatus(@Param("id") String id, @Param("time") Date time);

    /**
     * 新增新的登录时间的条目
     */
    int addNewUserStatus(OnlineStatus onlineStatus);

    /**
     * 更新注销的时间
     */
    int updateLogOutStatus(@Param("id") String id, @Param("time") Date date);
}
