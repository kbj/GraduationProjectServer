package me.weey.graduationproject.server.utils;

import me.weey.graduationproject.server.entity.User;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 一些常量的定义
 */
public class Constant {
    private Constant() {}


    /**
     * ---------------------------------------------------
     * |---------------------状态码-----------------------|
     * ---------------------------------------------------
     */

    /**
     * 返回信息成功状态的状态码
     */
    public static final int CODE_SUCCESS = 200;

    /**
     * 操作失败的校验码
     */
    public static final int CODE_FAILURE = 500;

    /**
     * 校验失败的状态码
     */
    public static final int CODE_CHECK_FAILURE = 501;

    /**
     * 注册时候邮箱已经存在
     */
    public static final int CODE_EMAIL_EXIST = 502;

    /**
     * 注册时候用户名已经存在
     */
    public static final int CODE_USERNAME_EXIST = 503;

    /**
     * 连接断开
     */
    public static final int CODE_CONNECTION_LOST = 504;

    /**
     * ---------------------------------------------------
     * |---------------------性别-------------------------|
     * ---------------------------------------------------
     */

    /**
     * 性别为空
     */
    public static final short GENDER_NONE = 0;

    /**
     * 男性
     */
    public static final short GENDER_MALE = 1;

    /**
     * 女性
     */
    public static final short GENDER_FEMALE = 2;


    /**
     * ---------------------------------------------------
     * |-----------------账户激活状态----------------------|
     * ---------------------------------------------------
     */
    //正常使用状态
    public static final int ACCOUNT_STATUS_NORMAL = 600;
    //被封禁限制登陆状态
    public static final int ACCOUNT_STATUS_BAN = 601;
    //未激活状态
    public static final int ACCOUNT_STATUS_NOT_ACTIVE = 602;
    //删除状态
    public static final int ACCOUNT_STATUS_DELETE = 603;
    //重复激活的话的状态码
    public static final int ACCOUNT_STATUS_ALREADY_ACTIVE = 604;
    //错误的激活码
    public static final int ACCOUNT_STATUS_ERROR_ACTIVE_CODE = 605;


    /**
     * --------------------------------------------------------------
     * |-----------------Socket中的加密聊天请求流程-------------------|
     * --------------------------------------------------------------
     */
    //流程一
    public static final int PROCESS_CLIENT_FIRST_REQUEST = 801;


    /**
     * --------------------------------------------------------------
     * |--------------------Socket中的ModelType----------------------|
     * --------------------------------------------------------------
     */
    //关于账号方面
    public static final int MODEL_TYPE_ACCOUNT = 1001;
    //聊天方面
    public static final int MODEL_TYPE_CHAT = 1002;

    /**
     * --------------------------------------------------------------
     * |--------------------Socket中的MessageType-------------------|
     * --------------------------------------------------------------
     */
    //登陆的请求
    public static final int MESSAGE_TYPE_LOGIN = 1101;
    //获取这个账号的好友列表
    public static final int MESSAGE_TYPE_GET_FRIENDS_LIST = 1102;
    //判断另外一个ID的好友在线状态
    public static final int MESSAGE_TYPE_IS_ONLINE = 1103;
    //握手的第二步对公钥进行签名
    public static final int MESSAGE_TYPE_SIGNATURE = 1104;
    //握手的第三部以后的信息，发送消息到对应客户端
    public static final int MESSAGE_TYPE_SEND_MESSAGE = 1105;


    /**
     * --------------------------------------------------------------
     * |--------------------更新消息的类型---------------------------|
     * --------------------------------------------------------------
     */
    public static final int INFO_TYPE_NAME = 1201;      //昵称
    public static final int INFO_TYPE_GENDER = 1202;    //性别
    public static final int INFO_TYPE_AVATAR = 1203;    //头像
    public static final int INFO_TYPE_BIO = 1204;       //个人简介
    public static final int INFO_TYPE_PASSWORD = 1205;  //密码

    /**
     * 用户登陆以及除了聊天以外的Socket连接的Session信息的集合
     */
    private static final ConcurrentHashMap<String, WebSocketSession> loginSocketSet = new ConcurrentHashMap<String, WebSocketSession>();

    public static ConcurrentHashMap<String, WebSocketSession> getLoginSessionInstant() {
        return loginSocketSet;
    }

    /**
     * 等待在线聊天的对象
     */
    private static final ConcurrentHashMap<String, String> chatSocketSet = new ConcurrentHashMap<String, String>();

    public static ConcurrentHashMap<String, String> getWaitSessionInstant() {
        return chatSocketSet;
    }

    /**
     * 这个map存储的是加聊天的随机码与相关用户的对应
     */
    private static final ConcurrentHashMap<String, String> addFriendMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, String> getAddFriendMap() {
        return addFriendMap;
    }

    /**
     * 服务器的ID用户
     * 静态内部类的单例模式
     */
    private static class UserHolder {
        private static final User server =
                new User("a0383f4ee0d447718df1c8d053d18823", "", "server",
                        (short)0, null, null, null, null,
                        null, null);
    }
    public static final User getServerInstant() {
        return UserHolder.server;
    }
}
