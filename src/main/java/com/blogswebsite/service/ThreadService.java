package com.blogswebsite.service;

import com.blogswebsite.entity.Blog;
import com.blogswebsite.entity.Event;
import com.blogswebsite.entity.UserLog;
import com.blogswebsite.mapper.BackBlogMapper;
import com.blogswebsite.mapper.EventMapper;
import com.blogswebsite.mapper.FrontBlogMapper;
import com.blogswebsite.mapper.UserMapper;
import com.blogswebsite.util.BlogsWebsite;
import com.blogswebsite.util.MailClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ThreadService {

    @Autowired
    private FrontBlogMapper frontBlogMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BackBlogMapper backBlogMapper;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private EventMapper eventMapper;

    //邮件发送对象（自定义实现类）
    @Autowired
    private MailClient mailClient;

    //模板引擎（帮助生成动态html）
    @Autowired
    private TemplateEngine templateEngine;

    //开启线程，更新文章阅读数
    @Async("taskExecutor")
    public void updateArticleView(Integer blogId){
        frontBlogMapper.updateBlogView(blogId);
    }

    //开启线程，记录用户操作日志
    @Async("taskExecutor")
    public void saveUserLog(UserLog userLog){
        userMapper.insertUserLog(userLog);
    }

    //修改评论状态，已读
    @Async("taskExecutor")
    public void updateCommentSates(List<Map<String, Object>> maps){
        List<Integer> list = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            if ((Integer) map.get("states") == 0){
                list.add((Integer) map.get("id"));
            }
        }
        if(list.size() != 0){
            backBlogMapper.updateCommentSates(list);
        }
    }


    //删除elastic中的blog数据
    @Async("taskExecutor")
    public void deleteEsBlogById(Integer id){
        try{
            elasticsearchService.delDoc(id);
        }catch (Exception e){
            log.error("ES删除失败");
        }
    }

    //向elastic中添加blog数据
    @Async("taskExecutor")
    public void addEsBlog(Blog blog){
        blog.setContent(null);
        blog.setHtmlContent(null);
        try{
            elasticsearchService.addDoc(blog);
        }catch (Exception e){
            log.error("ES添加失败");
        }
    }

    //修改事件状态,已读
    @Async("taskExecutor")
    public void setEventStates(Integer toId){
        eventMapper.updateEventStates(toId);
    }

    //发送邮件
    @Async("taskExecutor")
    public void sendCode(String email,String code){
        //发送code邮件
        Context context = new Context();
        context.setVariable("email", email);
        context.setVariable("code", code);
        //生成邮件模板
        //String content = templateEngine.process("/mail/sendCode", context);todo 模板加载不到
        String content = "<div>\n" +
                "    <p>\n" +
                "        <b>" +
                email +
                "</b>, 您好!\n" +
                "    </p>\n" +
                "    <p>\n" +
                "        您正在修改丑小鸭博客网账号密码, 这是您的验证码:<b style=\"font-weight: 600;color: red\">" +
                code +
                "</b>,\n" +
                "        5分钟内有效。\n" +
                "    </p>\n" +
                "</div>\n" +
                "</body>";

        //发送邮件
        mailClient.sendMail(email, "修改账号验证码",content);
    }

    public void sendEvent(Event event){
        eventMapper.insertEvent(event);
    }
}
