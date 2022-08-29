package com.blogswebsite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//解决跨域问题,请求方法上加上@CrossOrigin也能解决
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").//设置映射
                allowedOriginPatterns("*").//允许哪些域访问
                allowedMethods("GET","POST","PUT").//允许哪些请求方法访问
                allowCredentials(true).//是否可以携带Cookie
                maxAge(3600).//3600秒内可以不用再访问该配置（是否允许该配置中的设置）
                allowedHeaders("*");//允许哪些请求头访问
    }
}
