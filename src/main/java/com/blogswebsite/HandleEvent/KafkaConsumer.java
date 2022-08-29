package com.blogswebsite.HandleEvent;

import com.alibaba.fastjson.JSONObject;
import com.blogswebsite.entity.Event;
import com.blogswebsite.mapper.EventMapper;
import com.blogswebsite.util.BlogWebsiteConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer implements BlogWebsiteConstant {
    /**
     * 暂时关闭kafka功能，服务器太小
     */

/*    @Autowired
    private EventMapper eventMapper;


    @KafkaListener(topics = {PRIVATER_LETTER,THUMBS})
    public void write(ConsumerRecord record){
        Event event = JSONObject.parseObject((String) record.value(), Event.class);
        eventMapper.insertEvent(event);
    }*/
}
