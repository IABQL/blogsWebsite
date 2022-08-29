package com.blogswebsite.service;

import com.blogswebsite.HandleEvent.KafkaProducer;
import com.blogswebsite.entity.*;
import com.blogswebsite.mapper.BackBlogMapper;
import com.blogswebsite.mapper.FrontBlogMapper;
import com.blogswebsite.mapper.UserMapper;
import com.blogswebsite.util.BlogWebsiteConstant;
import com.blogswebsite.util.HostHolder;
import com.blogswebsite.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FrontBlogService implements BlogWebsiteConstant {

    @Autowired
    private FrontBlogMapper frontBlogMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BackBlogMapper backBlogMapper;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private KafkaProducer kafkaProducer;




    //获取所有blog
    public List<Map<String,Object>> getAllBlog(Integer currentPage) {
        currentPage = (currentPage-1)*4;
        return frontBlogMapper.selectAllBlog(currentPage);
    }

    //按关键字搜索blog
    public List<Map<String,Object>> getAllBlogByKey(String key, Integer currentPage) {
        currentPage = (currentPage-1)*8;
        return frontBlogMapper.selectAllBlogByKey(key, currentPage);
    }

    //获取置顶推荐
    public List<Map<String,Object>> getToppingBlog(){
        return frontBlogMapper.selectToppingBlog();
    }

    //读取blog内容
    public Map<String,Object> getArticle(Integer id){
        //获取blog
        Map<String, Object> maps = frontBlogMapper.selectBlogById(id);
        //获取author
        if(maps != null){
            User user = userMapper.selectById((Integer) maps.get("userId"));
            maps.put("author",user.getUsername());
            //获取blog标签
            List<String> tags = backBlogMapper.selectTagsByBlogId(id);
            maps.put("tags",tags);
            maps.put("success",true);
            //更新阅读数
            threadService.updateArticleView(id);
            Blog esBlog = frontBlogMapper.getBlogById(id);
            threadService.addEsBlog(esBlog);
        }

        return maps;
    }

    //查询blog总数目
    public int getTotalBlog(){
        return frontBlogMapper.selectTotalBlog();
    }

    //添加评论
    public Comment addComment(Comment comment){
        comment.setCreateTime(new Date());
        String content = comment.getContent();
        //过滤敏感词
        content = sensitiveFilter.filter(content);
        comment.setContent(content);
        frontBlogMapper.insertComment(comment);
        return comment;
    }

    //查询评论
    public List<Map<String,Object>> getComments(Integer blogId){
        return frontBlogMapper.selectComments(blogId);
    }

    //点赞
    public void setThumbs(Integer blogId,boolean isThumbs,String title){
        int num = 0;
        if (isThumbs){
            num=1;
        }else {
            num = -1;
        }
        frontBlogMapper.updateBlogThumbs(blogId,num);

        Blog esBlog = frontBlogMapper.getBlogById(blogId);

        threadService.addEsBlog(esBlog);

        Event event = new Event();
        event.setTopic(THUMBS);
        event.setFromId(0);
        int userId = backBlogMapper.selectUserId(blogId);
        event.setToId(userId);
        event.setType("点赞");
        event.setContent("点赞了你的文章："+title);
        kafkaProducer.send(event);
    }

    //计算阅读总数
    public int getCountView(){
        return frontBlogMapper.selectCountView();
    }
}
