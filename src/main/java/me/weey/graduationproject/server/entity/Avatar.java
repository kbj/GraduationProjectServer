package me.weey.graduationproject.server.entity;

import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 头像
 * Created by WeiKai on 2018/01/25.
 */
@Component
public class Avatar {
    private String id;
    private byte[] avatar;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Avatar{" +
                "id='" + id + '\'' +
                ", avatar=" + Arrays.toString(avatar) +
                '}';
    }
}
