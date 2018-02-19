package me.weey.graduationproject.server.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import me.weey.graduationproject.server.entity.DataStructure;
import me.weey.graduationproject.server.entity.User;
import me.weey.graduationproject.server.service.inter.IDataStructure;
import me.weey.graduationproject.server.service.inter.IUserService;
import me.weey.graduationproject.server.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 有关发收的数据包相关的工具类
 * Created by WeiKai on 2018/01/26.
 */
@Service
public class DataStructureServiceImpl implements IDataStructure {

    private static final Log log = LogFactory.get();

    @Autowired
    private IUserService userService;

    /**
     * 检查收到的DataStructure是否完整
     */
    public boolean checkDataStructureIntact(String payload) {
        if (StrUtil.hasEmpty(payload)) {
            //非空校验
            return false;
        }
        //对消息反序列化成DataStructure对象
        DataStructure dataStructure;
        try {
            dataStructure = JSON.parseObject(payload, DataStructure.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        if (StrUtil.hasEmpty(dataStructure.getToID()) || StrUtil.hasEmpty(dataStructure.getMessageType().toString())
                || StrUtil.hasEmpty(dataStructure.getModelType().toString())
                || StrUtil.hasEmpty(dataStructure.getProcess().toString())) {
            return false;
        }
        //判断如果是登录的话是没有带fromId的
        if (StrUtil.hasEmpty(dataStructure.getFromId())) {
            if (dataStructure.getModelType().equals(Constant.MODEL_TYPE_ACCOUNT)
                    && dataStructure.getMessageType().equals(Constant.MESSAGE_TYPE_LOGIN)) {
                //表示这个请求就是登录的
                return true;
            } else {
                //表明这个请求不是登录，也没有带任何请求参数
                return false;
            }
        }
        //查找有没有这个id
        User user = userService.findUser(dataStructure.getFromId(), "", "");
        if (user == null) return false;
        String toID = dataStructure.getToID();
        if (!Constant.getServerInstant().getId().equals(toID)) {
            User user1 = userService.findUser(toID, "", "");
            return user1 != null;
        }

        return true;
    }
}
