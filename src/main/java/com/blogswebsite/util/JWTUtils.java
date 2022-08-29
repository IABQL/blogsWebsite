package com.blogswebsite.util;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtils {

    private static final String jwtToken = "123456Mszlu!@#$$";//加密的密钥

    public static String createToken(int userId) {
        Map<String,Object> msg = new HashMap<>();
        msg.put("userId",userId);
        JwtBuilder jwtBuilder = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256,jwtToken)//加密
                .setClaims(msg)//存放的信息
                .setIssuedAt(new Date())//设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + 3600*24*1000));//设置过期时间

        String token = jwtBuilder.compact();//生成加密后的信息
        return token;
    }

    public static Map<String,Object> checkToken(String token) {
        try {
            //解密信息
            Jwt parse = Jwts.parser().setSigningKey(jwtToken).parse(token);
            return (Map<String,Object>)parse.getBody();
        }catch (Exception e){

        }
        return  null;
    }

    public static void main(String[] args) {
        String token = JWTUtils.createToken(100);
        System.out.println(token);
        Map<String, Object> map = JWTUtils.checkToken(token);
        System.out.println(map.get("userId"));
    }
}
