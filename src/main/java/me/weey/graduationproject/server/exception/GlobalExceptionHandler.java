package me.weey.graduationproject.server.exception;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.log.level.Level;
import me.weey.graduationproject.server.entity.HttpResponse;
import me.weey.graduationproject.server.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * 全局异常处理，捕获所有Controller中抛出的异常
 * Created by WeiKai on 2018/01/24.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Log log = LogFactory.get();

    @Autowired
    private HttpResponse httpResponse;

    //关于邮件的异常
    @ExceptionHandler(cn.hutool.extra.mail.MailException.class)
    @ResponseBody
    public HttpResponse emailHandler(cn.hutool.extra.mail.MailException e){
        e.printStackTrace();
        log.error(e.getMessage());
        httpResponse.setStatusCode(Constant.CODE_FAILURE);
        httpResponse.setMessage(e.getMessage());
        httpResponse.setTime(new Date());
        return httpResponse;
    }


     //处理关于数据库当插入、删除和修改数据的时候，违背的数据完整性约束抛出的异常
    @ExceptionHandler({org.springframework.dao.DataIntegrityViolationException.class, org.springframework.validation.BindException.class})
    @ResponseBody
    public HttpResponse dbHandler(Exception e) {
        e.printStackTrace();
        log.error("数据库修改异常" + e.getMessage());
        httpResponse.setStatusCode(Constant.CODE_FAILURE);
        httpResponse.setMessage("数据库修改异常：" + e.getMessage());
        httpResponse.setTime(new Date());
        return httpResponse;
    }

    //参数错误
    @ExceptionHandler(java.lang.IllegalArgumentException.class)
    @ResponseBody
    public HttpResponse paraError(java.lang.IllegalArgumentException e) {
        e.printStackTrace();
        log.error("参数错误：java.lang.IllegalArgumentException" + e.getMessage());
        httpResponse.setStatusCode(Constant.CODE_CHECK_FAILURE);
        httpResponse.setMessage("参数错误：" + e.getMessage());
        httpResponse.setTime(new Date());
        return httpResponse;
    }

    //其他未处理的异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public HttpResponse exceptionHandler(Exception e){
        httpResponse.setStatusCode(Constant.CODE_FAILURE);
        httpResponse.setMessage("系统错误！错误信息：" + e.getMessage());
        httpResponse.setTime(new Date());
        //记录下日志
        log.error("发生错误：" + e.getMessage());
        e.printStackTrace();
        return httpResponse;
    }
}
