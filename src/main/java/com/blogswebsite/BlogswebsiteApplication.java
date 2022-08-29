package com.blogswebsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class BlogswebsiteApplication {

    @PostConstruct
    public void init(){
        //解决netty冲突问题
        //System.setProperty("es.set.netty.runtime.available.processors","false");
    }
    public static void main(String[] args) {
        SpringApplication.run(BlogswebsiteApplication.class, args);
    }

}
