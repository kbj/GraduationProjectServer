package me.weey.graduationproject.server.entity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 用户的登录状态
 * Created by WeiKai on 2018/03/12.
 */
@Component
@Scope("prototype")
public class OnlineStatus {
    private String userId;      //用户ID
    private Boolean isOnline;   //当前的在线状态
    private Date loginTime;     //最新一次登录时间
    private Date logOutTime;    //最新一次注销时间

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLogOutTime() {
        return logOutTime;
    }

    public void setLogOutTime(Date logOutTime) {
        this.logOutTime = logOutTime;
    }

    @Override
    public String toString() {
        return "OnlineStatus{" +
                "userId='" + userId + '\'' +
                ", isOnline=" + isOnline +
                ", loginTime=" + loginTime +
                ", logOutTime=" + logOutTime +
                '}';
    }
}
