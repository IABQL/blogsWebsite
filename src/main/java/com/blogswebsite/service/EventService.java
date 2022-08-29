package com.blogswebsite.service;

import com.blogswebsite.entity.Event;
import com.blogswebsite.mapper.EventMapper;
import com.blogswebsite.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventService {

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private HostHolder hostHolder;

    public List<Event> getEvent(){

        if (hostHolder.getUser() != null){
            threadService.setEventStates(hostHolder.getUser().getId());
            return eventMapper.selectEvent(hostHolder.getUser().getId());
        }
        return null;
    }


    public Map<String,Integer> getCountUnreadEvent() {
        if (hostHolder.getUser() != null){
            HashMap<String, Integer> res = new HashMap<>();
            int comment = eventMapper.selectCountUnreadComment(hostHolder.getUser().getId());
            int event = eventMapper.selectCountUnread(hostHolder.getUser().getId());
            res.put("unreadComment",comment);
            res.put("unreadEvent",event);
            return res;
        }
        return null;
    }

    public void clearEvent() {
        if (hostHolder.getUser() != null){
            eventMapper.delEvent(hostHolder.getUser().getId());
        }

    }
}
