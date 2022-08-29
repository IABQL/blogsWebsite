package com.blogswebsite.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    private Integer id;
    private Integer blogId;
    private String title;
    private String username;
    private String headerUrl;
    private String content;
    private Date createTime;
}
