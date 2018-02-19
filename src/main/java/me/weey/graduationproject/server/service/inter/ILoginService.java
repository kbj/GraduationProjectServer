package me.weey.graduationproject.server.service.inter;

import me.weey.graduationproject.server.entity.User;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 * Login部分的Socket的Service接口
 * Created by WeiKai on 2018/01/26.
 */
public interface ILoginService {
    User login(User user, WebSocketSession session);
}
