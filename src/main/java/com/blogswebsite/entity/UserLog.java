package com.blogswebsite.entity;

import lombok.Data;

import java.util.Date;

@Data
public class UserLog {
    private Integer userId;
    private String username;
    private String content;
    private Date createTime;
}
