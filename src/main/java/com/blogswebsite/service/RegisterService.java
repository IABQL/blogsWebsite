package com.blogswebsite.service;

import com.blogswebsite.entity.User;
import com.blogswebsite.mapper.UserMapper;
import com.blogswebsite.params.RegisterUser;
import com.blogswebsite.util.BlogWebsiteConstant;
import com.blogswebsite.util.BlogsWebsite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class RegisterService implements BlogWebsiteConstant {

    @Autowired
    private UserMapper userMapper;

    public Map<String,Object> saveUser(RegisterUser registerUser) {
        Map<String,Object> res = new HashMap<>();

        //检测该账号是否已被注册
        User check_user = userMapper.selectByEmail(registerUser.getEmail());
        if (check_user != null){
            res.put("states",false);
            res.put("msg","该账号已被注册！");
            return res;
        }
        User user = new User();
        //设置随机账号名
        String username = BlogsWebsite.generateUUID().substring(0, 7);
        user.setUsername(username);
        //设置密码
        //加密
        String salt = BlogsWebsite.generateUUID().substring(0, 5);
        String md5_password = BlogsWebsite.md5(registerUser.getPassword() + salt);
        user.setPassword(md5_password);
        user.setSalt(salt);

        user.setEmail(registerUser.getEmail());
        user.setHeaderUrl(HEADER_URL);
        user.setCreateTime(new Date());

        int i = userMapper.insertUser(user);
        if (i>0){
            res.put("states",true);
            res.put("msg","注册成功");
        }else {
            res.put("states",false);
            res.put("msg","注册失败");
        }
        return res;
    }

}
