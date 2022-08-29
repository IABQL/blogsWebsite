package com.blogswebsite.controller;

import com.blogswebsite.HandleEvent.KafkaProducer;
import com.blogswebsite.entity.Event;
import com.blogswebsite.params.LeaveInfo;
import com.blogswebsite.util.BlogWebsiteConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/leaveMsg")
public class LeaveMsgController implements BlogWebsiteConstant {

    @Autowired
    private KafkaProducer kafkaProducer;

    @PostMapping("/submitMsg")
    public Map<String,Object> submitMsg(@RequestBody LeaveInfo msg){

        Map<String, Object> map = new HashMap<>();
        map.put("states",true);
        map.put("msg","感谢您的反馈！");

        Event event = new Event();
        event.setTopic(PRIVATER_LETTER);
        event.setFromId(0);
        event.setToId(1);
        event.setType("私信");
        event.setContent("给你发来了私信："+msg.getMsg());
        kafkaProducer.send(event);
        return map;
    }
}
