package me.weey.graduationproject.server.controller;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ImageUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import me.weey.graduationproject.server.entity.Avatar;
import me.weey.graduationproject.server.entity.HttpResponse;
import me.weey.graduationproject.server.entity.User;
import me.weey.graduationproject.server.entity.UserStatus;
import me.weey.graduationproject.server.service.inter.IUserService;
import me.weey.graduationproject.server.utils.Constant;
import me.weey.graduationproject.server.utils.IPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.Date;

/**
 * Created by WeiKai on 2018/01/23.
 */
@Controller
@RequestMapping("/account")
public class UserController {
    private static final Log log = LogFactory.get();

    private final HttpResponse response;
    private final IUserService userService;
    private final UserStatus userStatus;
    private final Avatar avatar;

    @Autowired
    public UserController(HttpResponse response, IUserService userService, UserStatus userStatus, Avatar avatar) {
        this.response = response;
        this.userService = userService;
        this.userStatus = userStatus;
        this.avatar = avatar;
    }

    /**
     * 注册对应的Controller
     */
    @ResponseBody
    @RequestMapping(value = "/register")
    public HttpResponse register(@RequestBody User user, @RequestParam(value = "picture", required = false) MultipartFile picture,
                                 HttpServletRequest request) throws IOException {
        //赋值
        user.setId(RandomUtil.simpleUUID());
        //校验
        //性别
        boolean gender = user.getGender() >= Constant.GENDER_NONE && user.getGender() <= Constant.GENDER_FEMALE;
        //邮件地址正则校验
        boolean match = Validator.isEmail(user.getEmail());
        if (!userService.checkUser(user) || !match || !gender) {
            //校验失败直接返回
            response.setStatusCode(Constant.CODE_CHECK_FAILURE);
            response.setMessage("check error!");
            response.setTime(new Date());
            //日志记录
            log.info("ip:" + IPUtil.getIpAddr(request) + "|| " + response.toString());
            return response;
        }
        user.setRegisterIp(IPUtil.getIpAddr(request));
        //对头像的封装
        avatar.setId(user.getId());
        if (picture == null) {
            //把头像设置成默认
            avatar.setAvatar(null);
        } else {
            //不为空的话要先把图片转成PNG
            File file = new File(user.getId());
            OutputStream outputStream = new FileOutputStream(file);
            ImageUtil.convert(picture.getInputStream(), "PNG", outputStream);
            byte[] pngBytes = Files.readAllBytes(file.toPath());
            avatar.setAvatar(pngBytes);
            //删除图片
            file.delete();
        }
        //封装状态
        userStatus.setUserId(user.getId());
        userStatus.setUserStatus(Constant.ACCOUNT_STATUS_NOT_ACTIVE);
        userStatus.setActiveCode(RandomUtil.simpleUUID() + RandomUtil.simpleUUID());
        //添加进数据库
        Integer result = userService.register(user, userStatus, avatar);
        //根据返回的状态码返回相对应的信息
        switch (result) {
            case Constant.ACCOUNT_STATUS_ALREADY_ACTIVE:
                //校验失败
                response.setStatusCode(Constant.CODE_CHECK_FAILURE);
                response.setMessage("check error!");
                break;
            case Constant.CODE_EMAIL_EXIST:
                //邮箱已经存在
                response.setStatusCode(Constant.CODE_EMAIL_EXIST);
                response.setMessage("email exists!");
                break;
            case Constant.CODE_USERNAME_EXIST:
                //用户名已经存在
                response.setStatusCode(Constant.CODE_USERNAME_EXIST);
                response.setMessage("username exists!");
                break;
            case Constant.CODE_FAILURE:
                //直接输出系统错误
                response.setStatusCode(Constant.CODE_FAILURE);
                response.setMessage("error happened!");
                break;
            case 3:
                response.setStatusCode(Constant.CODE_SUCCESS);
                response.setMessage("success! please verify the email from your inbox!");
                break;
        }
        response.setTime(new Date());
        log.info("ip:" + IPUtil.getIpAddr(request) + "|| " + response.toString());
        return response;
    }

    /**
     * 邮件激活
     */
    @ResponseBody
    @RequestMapping("/activate/{activeCode}")
    public HttpResponse verifyEmail(@PathVariable("activeCode") String activeCode, HttpServletRequest request) {
        //非空校验
        if (StrUtil.hasEmpty(activeCode) || activeCode.length() != 64) {
            response.setStatusCode(Constant.ACCOUNT_STATUS_ERROR_ACTIVE_CODE);
            response.setMessage("active code error!");
            response.setTime(new Date());
            log.info("ip:" + IPUtil.getIpAddr(request) + "|| " + response.toString());
            return response;
        }
        //激活账户
        Integer code = userService.activeAccount(activeCode);
        //根据返回的状态码返回相对应的信息
        switch (code) {
            case Constant.ACCOUNT_STATUS_ERROR_ACTIVE_CODE:
                //错误的验证码
                response.setStatusCode(Constant.ACCOUNT_STATUS_ERROR_ACTIVE_CODE);
                response.setMessage("error active code!");
                break;
            case Constant.ACCOUNT_STATUS_ALREADY_ACTIVE:
                //已经激活过了
                response.setStatusCode(Constant.ACCOUNT_STATUS_ALREADY_ACTIVE);
                response.setMessage("already active!");
                break;
            case 1:
                //成功修改
                response.setStatusCode(Constant.CODE_SUCCESS);
                response.setMessage("active successful !");
                break;
        }
        response.setTime(new Date());
        log.info("ip:" + IPUtil.getIpAddr(request) + "|| " + response.toString());
        return response;
    }

    /**
     * 根据ID返回图片
     */
    @RequestMapping("/avatars/{id}")
    public void getAvatar(@PathVariable("id") String id, HttpServletResponse response) throws IOException {
        //先进行校验
        if (StrUtil.hasEmpty(id) || id.length() != 32) return;
        Avatar avatar = userService.getAvatar(id);
        if (avatar == null) return;

        //判断是否有图片
        byte[] bytes = avatar.getAvatar();
        if (bytes == null || bytes.length == 0) {
            //表示没有图片
            String relativelyPath=this.getClass().getResource("/").getPath();
            FileReader fileReader = new FileReader(relativelyPath + "static/book_user.png");
            bytes = fileReader.readBytes();
        }
        //返回显示
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        ImageIO.write(bufferedImage, "PNG", response.getOutputStream());
    }
}
