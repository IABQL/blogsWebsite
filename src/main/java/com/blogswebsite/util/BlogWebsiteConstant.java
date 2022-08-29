package com.blogswebsite.util;

public interface BlogWebsiteConstant {

    //默认登入超时时间,s
    int DEFAULT_EXPIRED_SECONDS = 3600*12;

    //勾选记住我时登入超时时间，10天
    int REMEMBER_SECONDS = 3600*24*10;

    //主题，私信
    String PRIVATER_LETTER = "privaterLetter";

    //主题，点赞
    String THUMBS = "thumbs";

    //验证码过期时间
    int CODE_EXPIRED_SECONDS = 300;

    //用户默认头像
    String HEADER_URL = "http://blogs-community.oss-cn-beijing.aliyuncs.com/user-header/917254109dc2464e9d947d9fc37d7466.png?Expires=1651502942&OSSAccessKeyId=LTAI5tLJDTEmVekc3Rq7UXzJ&Signature=kgfFn6GuUCfL3ji4vLyb38sqJpM%3D";
}
