package com.blogswebsite.controller;


import com.blogswebsite.annotation.LoginRequired;
import com.blogswebsite.entity.Event;
import com.blogswebsite.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;


    @GetMapping("/getEvent")
    public List<Event> getEvent(){

        return eventService.getEvent();
    }


    @GetMapping("/getCountUnread")
    public Map<String,Integer> getCountUnread(){

        return eventService.getCountUnreadEvent();
    }

    @GetMapping("/clearEvent")
    public void clearEvent(){
        eventService.clearEvent();
    }
}
