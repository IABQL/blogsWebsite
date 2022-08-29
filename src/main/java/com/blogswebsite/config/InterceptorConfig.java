package com.blogswebsite.config;

import com.blogswebsite.interceptor.LoginRequiredInterceptor;
import com.blogswebsite.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//拦截器配置
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
        .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg")
        .addPathPatterns("/upLoadCoverImg")
        .addPathPatterns("/upLoadBlogContentImg")
        .addPathPatterns("/deleteUpLoadImg")
        .addPathPatterns("/upLoadBlog")
        .addPathPatterns("/blogHandle/**")
        .addPathPatterns("/UserController/**")
        .addPathPatterns("/event/**");

        //用注解的方式实现登入访问控制
        registry.addInterceptor(loginRequiredInterceptor);
    }
}
