package com.blogswebsite.params;

import lombok.Data;

//登入参数
@Data
public class LoginParams {
    private String account;
    private String password;
    private boolean rememberMe;

}
