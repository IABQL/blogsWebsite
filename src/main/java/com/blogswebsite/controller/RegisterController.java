package com.blogswebsite.controller;

import com.blogswebsite.params.RegisterUser;
import com.blogswebsite.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping("/newUser")
    public Map<String,Object> registerUser(@RequestBody RegisterUser registerUser) {
        Map<String,Object> res = new HashMap<>();
        if (registerUser !=null && registerUser.getCode().equals("yb92")){
            return registerService.saveUser(registerUser);
        }else {
            res.put("states",false);
            res.put("msg","注册码错误！");
        }
        return res;
    }
}
