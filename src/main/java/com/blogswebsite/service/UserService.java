package com.blogswebsite.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blogswebsite.entity.User;
import com.blogswebsite.entity.UserLog;
import com.blogswebsite.mapper.UserMapper;
import com.blogswebsite.util.BlogWebsiteConstant;
import com.blogswebsite.util.BlogsWebsite;
import com.blogswebsite.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements BlogWebsiteConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private HostHolder hostHolder;



    /**
     * 登入
     * 1.验证账号密码是否为空
     * 2.验证账号密码是否正确
     * 3.正确，生成ticket，将user信息放入redis
     * @param account   账号
     * @param password  密码
     * @param expired   过期时间
     * @return
     */
    public Map<String,Object> login(String account, String password, int expired){
        Map<String,Object> map = new HashMap<>();
        //
        if(StringUtils.isBlank(account)){
            map.put("usernameMsg","账号不能为空！");
            map.put("success",false);
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空！");
            map.put("success",false);
            return map;
        }

        User user = userMapper.selectByEmail(account);

        if(user == null){
            map.put("usernameMsg","账号不存在！");
            map.put("success",false);
            return map;
        }
        if(user.getStatus() == 1){
            map.put("usernameMsg","该账号被封禁！");
            map.put("success",false);
            return map;
        }
        
        //验证密码
        password = BlogsWebsite.md5(password+user.getSalt());//md5加密
        //数据库存放密码是加了密的
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确！");
            map.put("success",false);
            return map;
        }

        //生成一个登入凭证，(返回给浏览器，下次浏览器访问时携带进来，如果有凭证就直接进login_ticket表查询)
        String ticket = BlogsWebsite.generateUUID();
        //向redis存储登入凭证
        addLoginTicket(ticket,JSON.toJSONString(user),expired);

        map.put("ticket",ticket);//向客户端返回登入凭证
        map.put("userId",user.getId());
        map.put("headerUrl",user.getHeaderUrl());
        map.put("username",user.getUsername());
        map.put("expires",expired*1000);//返回过期时间
        map.put("success",true);
        return map;
    }


    /*
     * 向redis存储登入凭证
     * key：ticket      value:user   expired:过期时间 /秒
     */
    public void addLoginTicket(String key,String value,int expired){
        //向redis写入登入信息,key:ticket,value:user,过期时间：秒
        redisTemplate.opsForValue().set("ticket_"+key, value,expired, TimeUnit.SECONDS);
    }

    //登入凭证查询（redis）
    public User findLoginTicket(String ticket){
        //取出json字符串，将字符串转为user对象
        return JSONObject.parseObject(redisTemplate.opsForValue().get("ticket_"+ticket),User.class);
    }

    //删除redis登入凭证信息
    public boolean deleteLoginTicket(String ticket){
        return redisTemplate.delete("ticket_" + ticket);
    }


    //查询用户信息
    //查询用户
    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    //修改用户头像
    public void updateHeaderImg(int userId , String url){
        userMapper.updateHeader(userId, url);
    }

    //获取用户历史信息
    public List<UserLog> getUserLog(Integer userId){
        return userMapper.selectUserLog(userId);
    }

    //检查邮箱，并发送验证码
    public Map<String,Object> checkUser(Integer userId,String email){
        Map<String,Object> map = new HashMap<>();
        User user = findUserById(userId);
        if(user != null){
            if (!user.getEmail().equals(email)){
                map.put("msg","邮箱验证失败！");
                map.put("states",false);
            }else {
                //验证成功，发送验证码
                //生成随机验证码
                String code = BlogsWebsite.generateUUID().substring(0, 4);
                threadService.sendCode(email,code);
                //生成一个code凭证，存入redis，并返回客服端,有效期5分钟
                String codeTicket = BlogsWebsite.generateUUID();
                redisTemplate.opsForValue().set("codeTicket_"+codeTicket, code,CODE_EXPIRED_SECONDS, TimeUnit.SECONDS);

                map.put("codeTicket",codeTicket);
                map.put("msg","验证码已通过邮箱发送，请注意查收！");
                map.put("states",true);
            }
        }else {
            map.put("msg","邮箱验证失败！");
            map.put("states",false);
        }
        return map;
    }

    //校验验证码
    public Map<String, Object> checkCode(String code,String codeTicket){

        Map<String, Object> res = new HashMap<>();
        String code_redis = redisTemplate.opsForValue().get("codeTicket_" + codeTicket);

        //验证成功
        if (code_redis != null && code_redis.equals(code)){
            res.put("states",true);
            res.put("msg","验证码正确！");
        }else {
            //验证失败
            res.put("states",false);
            res.put("msg","验证码错误！");
        }
        return res;
    }

    public Map<String, Object> setPassword(String password){
        Map<String, Object> res = new HashMap<>();
        if(hostHolder.getUser() != null){
            //获取盐
            String salt = BlogsWebsite.generateUUID().substring(0, 5);
            //md5加密
            String md5_Password = BlogsWebsite.md5(password + salt);
            int i = userMapper.updatePassword(hostHolder.getUser().getId(), md5_Password,salt);
            if(i>0){
                res.put("states",true);
                res.put("msg","修改成功!");
            }else{
                res.put("states",false);
                res.put("msg","修改失败！");
            }
        }
        return res;
    }
}
