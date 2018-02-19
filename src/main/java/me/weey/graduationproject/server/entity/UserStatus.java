package me.weey.graduationproject.server.entity;

import org.springframework.stereotype.Component;

/**
 * 用户状态
 * Created by WeiKai on 2018/01/24.
 */
@Component
public class UserStatus {
    private String userId;              //用户的id
    private Integer userStatus;         //账户的状态
    private String activeCode;          //激活码

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public String getActiveCode() {
        return activeCode;
    }

    public void setActiveCode(String activeCode) {
        this.activeCode = activeCode;
    }


    @Override
    public String toString() {
        return "UserStatus{" +
                "userId='" + userId + '\'' +
                ", userStatus=" + userStatus +
                ", activeCode='" + activeCode + '\'' +
                '}';
    }
}
