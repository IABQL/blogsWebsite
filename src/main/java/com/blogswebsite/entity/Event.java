package com.blogswebsite.entity;

import lombok.Data;

@Data
public class Event {
    private String topic;
    private int id;
    private int fromId;//来自
    private int toId;//发至
    private String content;
    private String type;
    private int states;//0:未读，1：已读
}
