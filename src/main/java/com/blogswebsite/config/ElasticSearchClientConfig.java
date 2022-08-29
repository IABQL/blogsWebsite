package com.blogswebsite.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchClientConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        //高级由低级客户端构造器创建
        RestHighLevelClient client = new RestHighLevelClient(
                //linux:120.24.229.5
                RestClient.builder(
                        new HttpHost("120.24.229.5", 9200, "http")));
        return  client;
    }
}
