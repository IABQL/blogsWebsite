package com.blogswebsite.mapper;

import com.blogswebsite.entity.Event;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EventMapper {

    //保存通知
    @Insert({"insert into event (fromId,content,states,toId,type) values (#{fromId},#{content},#{states},#{toId},#{type})"})
    void insertEvent(Event event);

    //读取未读通知数
    @Select({"select count(*) from event where toId = #{toId} and states = 0"})
    @ResultType(java.lang.Integer.class)
    int selectCountUnread(Integer toId);

    //读取通知
    @Select({"select fromId,content,states,toId,type from event where toId = #{toId} order by states"})
    @ResultType(com.blogswebsite.entity.Event.class)
    List<Event> selectEvent(Integer toId);


    //读取未读评论数
    @Select({"select count(*) from comment where blogId in (select blogId from blog where userId = #{userId}) and states = 0"})
    @ResultType(java.lang.Integer.class)
    int selectCountUnreadComment(Integer userId);

    //修改消息状态
    @Update({"update event set states = 1 where toId = #{toId}"})
    int updateEventStates(Integer toId);

    @Delete({"delete from event where toId = #{userId}"})
    void delEvent(Integer userId);
}
