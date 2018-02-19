package me.weey.graduationproject.server.entity;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Key相关的表
 * Created by WeiKai on 2018/01/25.
 */
@Component
public class Key {
    private int id;
    private String publicKey;
    private String privateKey;
    private Date updateTime;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Key{" +
                "id=" + id +
                ", publicKey='" + publicKey + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
