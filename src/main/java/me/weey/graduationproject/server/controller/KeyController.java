package me.weey.graduationproject.server.controller;

import me.weey.graduationproject.server.service.inter.IKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 对外提供服务器的public key的controller
 * Created by WeiKai on 2018/01/25.
 */
@Controller
public class KeyController {

    private final IKeyService keyService;

    @Autowired
    public KeyController(IKeyService keyService) {
        this.keyService = keyService;
    }

    /**
     * 对外提供公钥
     */
    @RequestMapping("/getKey")
    @ResponseBody
    public String getPublicKey(HttpServletResponse response) throws IOException {
        //直接返回字符串
        response.setContentType("charset=UTF-8");
        response.getWriter().write(keyService.getPublicKey());
        return null;
    }
}
