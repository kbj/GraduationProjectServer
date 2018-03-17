package me.weey.graduationproject.server.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import me.weey.graduationproject.server.entity.DataStructure;
import me.weey.graduationproject.server.entity.HttpResponse;
import me.weey.graduationproject.server.entity.OnlineStatus;
import me.weey.graduationproject.server.entity.User;
import me.weey.graduationproject.server.service.inter.*;
import me.weey.graduationproject.server.utils.Constant;
import me.weey.graduationproject.server.utils.ECDSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登陆相关的Handler
 * Created by WeiKai on 2018/01/23.
 */
@Controller
public class LoginHandler extends TextWebSocketHandler {

    private static final Log log = LogFactory.get();

    private final IDataStructure dataStructureUtil;
    private final IUserService userService;
    private final DataStructure mDataStructure;
    private final HttpResponse response;
    private final IUserStatusService userStatusService;
    private final ILoginService loginService;
    private final IKeyService keyService;

    @Autowired
    public LoginHandler(IDataStructure dataStructureUtil, IUserService userService, DataStructure dataStructure, HttpResponse response, IUserStatusService userStatusService, ILoginService loginService, IKeyService keyService) {
        this.dataStructureUtil = dataStructureUtil;
        this.userService = userService;
        this.mDataStructure = dataStructure;
        this.response = response;
        this.userStatusService = userStatusService;
        this.loginService = loginService;
        this.keyService = keyService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info(" IP:" + session.getRemoteAddress() + " connect!");
        super.afterConnectionEstablished(session);
    }

    /**
     * 接收到文本消息的处理
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        //接收传来的消息
        String payload = message.getPayload();
        log.info("收到信息" + payload);
        //非空校验
        boolean structureIntact = dataStructureUtil.checkDataStructureIntact(payload);
        if (!structureIntact) {
            response(Constant.CODE_CHECK_FAILURE, "check data structure error", session, -1);
            session.close();
            return;
        }
        //校验完毕，建立连接
        log.info(" IP：" + session.getRemoteAddress() + " 建立连接！");
        DataStructure dataStructure = JSON.parseObject(payload, DataStructure.class);

        switch (dataStructure.getModelType()) {
            case Constant.MODEL_TYPE_ACCOUNT:
                //是账号方面的
                if (dataStructure.getToID().equals(Constant.getServerInstant().getId())) {
                    account(session, dataStructure);
                }
                break;
            case Constant.MODEL_TYPE_CHAT:
                //聊天方面
                //判断是否已经登录
                if (Constant.getLoginSessionInstant().get(dataStructure.getFromId()).equals(session)) {
                    chat(session, dataStructure);
                }
                break;
        }
    }

    /**
     * 接收到二进制消息
     * @param session
     * @param message
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        super.handleBinaryMessage(session, message);
        ByteBuffer byteBuffer = message.getPayload();
    }

    /**
     * 表示要请求聊天了
     */
    private void chat(WebSocketSession session, DataStructure dataStructure) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        //根据MessageType来判断
        switch (dataStructure.getMessageType()) {
            case Constant.MESSAGE_TYPE_IS_ONLINE:
                //流程一，判断在线状态
                OnlineStatus onlineStatus = userStatusService.findOnlineStatus(dataStructure.getToID());
                if (onlineStatus != null) {
                    //返回在线状态
                    dataStructure.setMessage(JSON.toJSONString(onlineStatus));
                    response(Constant.CODE_SUCCESS, JSON.toJSONString(dataStructure), session, dataStructure.getMessageType());
                } else {
                    //在线状态为空，默认返回不在线
                    dataStructure.setMessage("false");
                    response(Constant.CODE_FAILURE, JSON.toJSONString(dataStructure), session, dataStructure.getMessageType());
                }
                break;
            case Constant.MESSAGE_TYPE_SIGNATURE:
                //流程二，对客户端传来的publicKey进行签名
                String clientPublicKey = dataStructure.getMessage();
                //if (StrUtil.hasEmpty(clientPublicKey) || !dataStructure.getToID().equals(Constant.getServerInstant().getId())) {
                if (StrUtil.hasEmpty(clientPublicKey)) {
                    response(Constant.CODE_FAILURE, "empty public key", session, dataStructure.getMessageType());
                    return;
                }
                //签名
                String sign = sign(clientPublicKey);
                //返回签名的数据
                HashMap<String, String> map = new HashMap<>();
                map.put("signature", sign);
                map.put("publicKey", clientPublicKey);

                dataStructure.setMessage(JSON.toJSONString(map));

                response(Constant.CODE_SUCCESS, JSON.toJSONString(dataStructure), session, dataStructure.getMessageType());
                break;
            case Constant.MESSAGE_TYPE_SEND_MESSAGE:
                //流程三以后的步骤，发送消息给另外一个客户端
                String toID = dataStructure.getToID();
                if (Constant.getLoginSessionInstant().get(toID) == null) {
                    //为空
                    response(Constant.CODE_FAILURE, "error chat", session, dataStructure.getMessageType());
                    return;
                }
                //转发消息
                WebSocketSession toSession = Constant.getLoginSessionInstant().get(toID);
                response(Constant.CODE_SUCCESS, JSON.toJSONString(dataStructure), toSession, dataStructure.getMessageType());
                break;
        }
    }

    /**
     * 针对客户端传来的公钥进行签名
     */
    private String sign(String clientPublicKey) throws UnsupportedEncodingException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        //获取服务器的私钥
        String privateKey = keyService.getPrivateKey();
        //使用私钥签名
        return ECDSAUtil.signature(clientPublicKey.getBytes("UTF-8"), privateKey, null);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(" IP:" + session.getRemoteAddress() + " disconnect!");
        super.afterConnectionClosed(session, status);
        //连接断开以后，把session从set移除
        ConcurrentHashMap<String, WebSocketSession> map = Constant.getLoginSessionInstant();
        //判断session是否存在，存在的话就移除
        String id = "";
        Set<Map.Entry<String, WebSocketSession>> entrySet = map.entrySet();
        for (Map.Entry<String, WebSocketSession> maps : entrySet) {
            if (maps.getValue().equals(session)) {
                id = maps.getKey();
                map.remove(id);
                userStatusService.updateOnlineStatus(id, false);
                break;
            }
        }
        //移除这个账号对应的随机码
        String finalId = id;
        Constant.getAddFriendMap().entrySet().removeIf(item -> finalId.equals(item.getValue()));
    }

    /**
     * 有关账号的请求
     */
    private void account(WebSocketSession session, DataStructure dataStructure) throws IOException {
        //Session的有效性判断
        if (!session.isOpen()) return;
        //获取MessageType类型
        Integer messageType = dataStructure.getMessageType();
        switch (messageType) {
            case Constant.MESSAGE_TYPE_LOGIN:
                //登录
                login(session, dataStructure);
                break;
            case Constant.MESSAGE_TYPE_GET_FRIENDS_LIST:
                //是获取好友列表
                getFriendsList(session, dataStructure);
                break;
        }
    }

    /**
     * 登录成功以后获取好友列表的逻辑
     */
    private void getFriendsList(WebSocketSession session, DataStructure dataStructure) throws IOException {
        //Session的有效性判断
        if (!session.isOpen()) return;
        //登录状态的判断
        WebSocketSession socketSession = Constant.getLoginSessionInstant().get(dataStructure.getFromId());
        if (socketSession == null) {
            return;
        }
        //调用Service获得好友列表
        List<User> friendsList = userService.getFriends(dataStructure.getFromId());
        //返回结果
        String json = "";
        try {
            json = JSON.toJSONString(friendsList);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        response(Constant.CODE_SUCCESS, json, session, dataStructure.getMessageType());
    }


    /**
     * 登录的逻辑
     */
    private void login(WebSocketSession session, DataStructure dataStructure) throws IOException {
        //Session的有效性判断
        if (!session.isOpen()) return;
        //请求登录
        //获取消息
        String message = dataStructure.getMessage();
        //把message转为User对象
        User user;
        try {
            user = JSON.parseObject(message, User.class);
            if (user == null || StrUtil.hasEmpty(user.getUserName()) || StrUtil.hasEmpty(user.getPassword())) throw new Exception("信息错误");
        } catch (Exception e) {
            log.error(e.getMessage());
            response(Constant.CODE_CHECK_FAILURE, "格式错误", session, dataStructure.getMessageType());
            session.close();
            return;
        }
        User login = loginService.login(user, session);
        if (login != null) {
            //成功
            String userJSON = JSON.toJSONString(login);
            response(Constant.CODE_SUCCESS, userJSON, session, dataStructure.getMessageType());
            //更新登录状态
            userStatusService.updateOnlineStatus(login.getId(), true);
        } else {
            //失败
            response(Constant.CODE_CHECK_FAILURE, "用户名/邮箱/密码错误", session, dataStructure.getMessageType());
            session.close();
        }
    }

    /**
     * 往相应的客户端发送信息
     * @param statusCode        返回的状态码
     * @param message           要往客户端回应的数据内容
     * @param session           客户端的session
     * @param messageType       这条消息属于的消息类型
     */
    private void response(int statusCode, String message, WebSocketSession session, int messageType) throws IOException {
        //Session的有效性判断
        if (!session.isOpen()) return;
        response.setStatusCode(statusCode);
        response.setMessage(message);
        response.setTime(new Date());
        response.setMessageType(messageType);
        session.sendMessage(new TextMessage(JSON.toJSONString(response)));
    }
}
