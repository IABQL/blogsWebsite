package com.blogswebsite.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.blogswebsite.entity.LoginTicket;
import com.blogswebsite.entity.User;
import com.blogswebsite.service.UserService;
import com.blogswebsite.util.CookieUtil;
import com.blogswebsite.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;//该类中使用了ThreadLocal来存放当前登入用户信息

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        //如果访问的是静态资源，直接放行
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        String token = request.getHeader("token");

        //从cookie中获取凭证
        String ticket = CookieUtil.getCookeValue(request, "ticket");

        if(token != null){
            //如果存在凭证，说明账号登入过，直接去redis查询用户信息
            User user = userService.findLoginTicket(token);

            //如果从redis中能够读取到数据
            if (user != null) {
                //将用户信息放置threadLocal
                hostHolder.setUser(user);
            }else{
                return false;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // controller处理完、视图渲染完成后
        hostHolder.clear();//ThreadLocal内存清理
    }
}
