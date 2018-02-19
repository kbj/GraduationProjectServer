package me.weey.graduationproject.server.dao.inter;

import me.weey.graduationproject.server.entity.Key;
import org.springframework.stereotype.Repository;

/**
 * key表的dao
 * Created by WeiKai on 2018/01/25.
 */
@Repository
public interface KeyDao {

    /**
     * 获取public key
     */
    Key findKey();
}
