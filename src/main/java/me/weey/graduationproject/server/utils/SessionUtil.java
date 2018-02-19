package me.weey.graduationproject.server.utils;

import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by WeiKai on 2018/01/27.
 */
public class SessionUtil {

    /**
     * 根据传来的id判断map中是否已经有了
     */
    public static boolean isLogin(ConcurrentHashMap<String, WebSocketSession> loginSocketSet, String id) {
        return false;
    }
}
