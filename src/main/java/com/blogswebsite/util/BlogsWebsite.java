package com.blogswebsite.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
//工具类
public class BlogsWebsite {

    /**
     * 生成随机字符串
     * @return
     */
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * MD5加密
     * @param key
     * @return
     */
    public static String md5(String key){
        //判断是否是一个空串（空格、null）
        if(StringUtils.isAllBlank(key)){
            return null;
        }
        //spring有自带的md5加密封装
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 生成上传图片随机名
     * @param file
     * @return
     */
    public static String getImgName(MultipartFile file){
        /*截取文件后缀名*/
        String name = file.getOriginalFilename();//获取文件上传名
        int i = name.lastIndexOf(".");
        String type = name.substring(i+1);//从i开始将文件后缀名截取出来

        //生成一个随机文件名
        String s = BlogsWebsite.generateUUID();
        String fileName = s + "." + type;

        return fileName;
    }

}
