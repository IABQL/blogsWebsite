package com.blogswebsite.controller;

import com.blogswebsite.entity.UserLog;
import com.blogswebsite.service.UserService;
import com.blogswebsite.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/UserController")
public class UserController implements BlogWebsiteConstant {

    @Autowired
    private UpLoad upLoad;//上传图片oss工具类

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Value("${blogwebsite.path.domain}")
    private String domain;



    /**
     * 上传用户头像
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upLoadUserHeaderImg")
    public String upLoadUserHeaderImg(MultipartFile file) throws IOException {
        String uploadUrl = "";
        if(file != null){
            //生成上传图片随机名
            String fileName = BlogsWebsite.getImgName(file);
            
            uploadUrl = upLoad.upLoadImg(file, "user-header/", fileName);
        }

        if(hostHolder.getUser() != null){

            //使用ThreadLocal中的用户id
            //修改用户头像
            userService.updateHeaderImg(hostHolder.getUser().getId(),uploadUrl);
        }

        return uploadUrl;
    }


    /**
     * 获取用户历史信息
     */
    @GetMapping("/getUserLog")
    public List<UserLog> getUserLog() {
        List<UserLog> userLogs = null;
        if (hostHolder.getUser() != null){
            userLogs = userService.getUserLog(hostHolder.getUser().getId());
        }
        return userLogs;
    }

    @GetMapping("/getCode")
    public Map<String, Object> getCode(@RequestParam("email") String email, HttpServletResponse response,HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        if (hostHolder.getUser() != null){
            map = userService.checkUser(hostHolder.getUser().getId(), email);
            Cookie cookie = new Cookie("codeTicket",(String) map.get("codeTicket"));
            cookie.setMaxAge(CODE_EXPIRED_SECONDS);

            // 设置域名的cookie
            cookie.setDomain(domain);
            cookie.setPath("/");
            response.addCookie(cookie);
        }else {
            map.put("msg","邮箱验证失败！");
            map.put("states",false);
        }

        return map;
    }


    @GetMapping("/checkCode")
    public Map<String, Object> checkCode(@RequestParam("code") String code, HttpServletRequest request){

        String codeTicket = CookieUtil.getCookeValue(request, "codeTicket");
        return userService.checkCode(code,codeTicket);
    }


    @PostMapping("/updatePassword")
    public Map<String, Object> updatePassword(@RequestBody String password){
        password = password.substring(0, password.length() - 1);
        return userService.setPassword(password);
    }
}
