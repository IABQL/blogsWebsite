package com.blogswebsite.params;

import lombok.Data;

@Data
public class RegisterUser {
    private String email;
    private String password;
    private String code;
}
