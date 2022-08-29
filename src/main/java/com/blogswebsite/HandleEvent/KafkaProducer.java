package com.blogswebsite.HandleEvent;

import com.alibaba.fastjson.JSONObject;
import com.blogswebsite.entity.Event;
import com.blogswebsite.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    /**
     * 暂时关闭kafka功能，服务器太小
     */


    /*@Autowired
    private KafkaTemplate kafkaTemplate;

    public void send(Event event){
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }*/


    @Autowired
    private ThreadService threadService;

    //替代
    public void send(Event event){
        threadService.sendEvent(event);
    }
}
