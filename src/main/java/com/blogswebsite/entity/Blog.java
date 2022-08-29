package com.blogswebsite.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

//@Data相当于@Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode这5个注解的合集。
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Blog {
    private int id;
    private String title;//标题
    private String[] tag;//标签
    private String blogExplain;//博客介绍
    private String htmlContent;//内容
    private String content;//富文本内容
    private String blogCover;//博客封面路径
    private Date createTime;//创建时间
    private int userId;//作者id
    private int type;//类型，是否置顶，0：否，1：是
    private int states;//状态，1：该博客为草稿 0：已发表
    private int view;
    private int thumbs;
}
