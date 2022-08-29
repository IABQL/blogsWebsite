package com.blogswebsite.controller;

import com.blogswebsite.entity.User;
import com.blogswebsite.params.LoginParams;
import com.blogswebsite.service.UserService;
import com.blogswebsite.util.BlogWebsiteConstant;
import com.blogswebsite.util.CookieUtil;
import com.blogswebsite.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/loginController")
public class LoginController implements BlogWebsiteConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Value("${blogwebsite.path.domain}")
    private String domain;


    /**
     *登入
     * @param loginParams  登入参数类
     * @return
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginParams loginParams, HttpServletResponse response,HttpServletRequest request){

        /*
            在登入有效期内重复登入
        */
        if(hostHolder.getUser() != null){
            User user = hostHolder.getUser();
            Map<String,Object> map = new HashMap<>();
            map.put("success",true);
            map.put("userId",user.getId());
            map.put("headerUrl",user.getHeaderUrl());
            map.put("username",user.getUsername());
            return map;
        }


        /*
         *  同一客户机第一次登入或登入过期时
         */
        //免密登入时长，为true则10天免密登入，否则12小时内免密
        int expiredSeconds = loginParams.isRememberMe() ? REMEMBER_SECONDS : DEFAULT_EXPIRED_SECONDS;

        //检测账号，密码
        Map<String, Object> map = userService.login(loginParams.getAccount(), loginParams.getPassword(), expiredSeconds);

        if (map.containsKey("ticket")){
            //有登入凭证说明登入成功
            //将登入凭证发送至客户端cookie中
            Cookie cookie = new Cookie("ticket",(String) map.get("ticket"));
            cookie.setMaxAge(expiredSeconds);

            // 设置域名的cookie
            cookie.setDomain(domain);
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        return  map;
    }


    /**
     * 退出登入
     */
    @GetMapping("/loginOut")
    public boolean loginOut(HttpServletRequest request){
        //从cookie中获取凭证
        String ticket = CookieUtil.getCookeValue(request, "ticket");
        if(ticket != null){
            //生成redis中的ticket
            return userService.deleteLoginTicket(ticket);
        }
        return false;
    }
}
