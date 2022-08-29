package com.blogswebsite.service;

import com.blogswebsite.entity.Blog;
import com.blogswebsite.entity.User;
import com.blogswebsite.entity.UserLog;
import com.blogswebsite.mapper.BackBlogMapper;
import com.blogswebsite.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BackBlogService {

    //日志打印
    private static final Logger logger = LoggerFactory.getLogger(BackBlogService.class);

    @Autowired
    private BackBlogMapper blogMapper;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private UserMapper userMapper;



    /**
     * 保存博客
     * @param blog
     * @return
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED,rollbackFor = Exception.class)
    public int saveBlog(Blog blog){

        //设置创建时间
        blog.setCreateTime(new Date());

        int res = 0;

        //插入blog并返回自增id值,将取到的主键值赋值给blog的id属性：blog.getTag()
        res = blogMapper.insertBlog(blog);

        if(blog.getStates() == 0 && res>0){
            //查询标签对应的id
            List<Integer> list = blogMapper.selectBlogTags(blog.getTag());

            //添加blog的tag到blog-tag关系表中
            res = blogMapper.insertBlogTag(blog.getId(), list, blog.getUserId());

            //记录用户日志
            UserLog userLog = new UserLog();
            userLog.setUserId(blog.getUserId());
            userLog.setContent("发表了一篇文章，"+blog.getTitle());
            userLog.setCreateTime(blog.getCreateTime());
            User user = userMapper.selectById(blog.getUserId());
            userLog.setUsername(user.getUsername());

            //开启线程执行
            threadService.saveUserLog(userLog);

            threadService.addEsBlog(blog);
        }

        //返回blog的id，id不为0表示成功，返回id的原因，当用户保存成功后，如果没有离开编辑页面，而是继续编辑保存
        //那么再次保存时，将携带id，只需要修改原有的数据，而不会新增blog数据
        return blog.getId();
    }


    /**
     * 删除blog
     */
    //@Transactional开启事务
    @Transactional
    public boolean deleteBlog(Integer blogId) {
        //必须先删除其标签，因为标签是blog的外键
        blogMapper.deleteBlogTag(blogId);
        //再删除blog
        int i = blogMapper.deleteBlog(blogId);
        //删除elastic中的blog数据
        threadService.deleteEsBlogById(blogId);
        return i>0;
    }



    /**
     * 修改博客
     */
    public boolean updateBlog(Blog blog){

        int i = 0;
        try{
            //修改blog
            i = blogMapper.updateBlog(blog);
            //当状态为0时才保存标签  1：草稿
            if(blog.getStates() == 0){
                //先删除所有标签
                blogMapper.deleteBlogTag(blog.getId());
                //查询标签对应的id
                List<Integer> list = blogMapper.selectBlogTags(blog.getTag());
                //再添加标签
                i = blogMapper.insertBlogTag(blog.getId(), list, blog.getUserId());

                threadService.addEsBlog(blog);
            }

        }catch (Exception e){
            logger.error("修改博客失败："+e.getMessage());
        }

        return i>0;
    }




    /**
     * 根据userId获取博客
     */
    public List<Map<String,Object>> getBlogByUserId(Integer userId,Integer currentPag,
                                                    Integer states,Integer year,Integer month){


        String fromDate = null;
        String toDate = null;
        if(year!=null && month!=null && year !=0 && month != 0){
            fromDate = year + "-" + month + "-" + "1 00:00:00";
            month += 1;
            toDate = year + "-" + month + "-" + "1 00:00:00";
        }
        int fromIndex = (currentPag-1)*9;//查询数据的起始位置
        List<Map<String, Object>> maps = blogMapper.selectBlogByUserId(userId,fromIndex, states,fromDate,toDate);
        for (Map<String, Object> map : maps) {
            //根据id查博客对应的所有标签
            List<String> tags = blogMapper.selectTagsByBlogId((Integer) map.get("id"));
            map.put("tags",tags);
            map.put("isCheck",false);
        }

        return maps;
    }

    //根据userId获取已经发布blog的总数目
    public int getBlogTotalData(Integer userId, Integer states) {
        return blogMapper.selectTotalBlogByUserId(userId,states);
    }

    //获取该用户blog条目总数(按照标签名)
    public int getBlogTotalDataByTag(Integer userId,String tagName){
        int tagId = blogMapper.selectTagId(tagName);//根据标签名查询其id
        return blogMapper.selectTotalBlogByUserIdTag(userId,tagId);
    }


    public int getTotalDataByUserIdKey(Integer userId,String key){

        return blogMapper.selectTotalBlogByUserIdKey(userId,key);
    }

    //根据blogId获取blog信息
    public Map<String,Object> getBlogAllByBlogId(Integer blogId){
        Map<String, Object> map = blogMapper.selectBlogAllByBlogId(blogId);

        //查询blog的所有标签
        List<String> tags = blogMapper.selectTagsByBlogId(blogId);
        map.put("tags",tags);
        return map;
    }


    //设置是否置顶
    public boolean changeBlogType(int blogId, String type) {
        int i = 0;
        if("true".equals(type)){
            i = 1;
        }
        int change = blogMapper.updateBlogType(blogId, i);
        return change>0;
    }


    //获取用户某一标签下的所有blog，分页显示
    public List<Map<String,Object>> getBlogByUserIdTag(Integer userId, Integer currentPag,
                                                          Integer states, String tagName,
                                                          Integer year, Integer month){


        String fromDate = null;
        String toDate = null;
        if(year!=null && month!=null && year !=0 && month != 0){
            fromDate = year + "-" + month + "-" + "1 00:00:00";
            month += 1;
            toDate = year + "-" + month + "-" + "1 00:00:00";
        }
        currentPag = (currentPag-1)*9;

        int tagId = blogMapper.selectTagId(tagName);
        List<Map<String, Object>> maps = blogMapper.selectBlogByUserIdTag(userId, currentPag, states, tagId,fromDate,toDate);
        for (Map<String, Object> map : maps) {
            //根据id查博客对应的所有标签
            List<String> tags = blogMapper.selectTagsByBlogId((Integer) map.get("id"));
            map.put("tags",tags);
            map.put("isCheck",false);
        }
        return maps;
    }


    public List<Map<String,Object>> getBlogByKey(Integer userId,String key,Integer currentPage){
        currentPage = (currentPage-1)*9;
        List<Map<String, Object>> maps = blogMapper.selectBlogByUserIdKey(userId, key, currentPage);
        for (Map<String, Object> map : maps) {
            //根据id查博客对应的所有标签
            List<String> tags = blogMapper.selectTagsByBlogId((Integer) map.get("id"));
            map.put("tags",tags);
            map.put("isCheck",false);
        }
        return maps;
    }

    //搜寻用户的草稿blog
    public List<Map<String,Object>> getDraftBlog(Integer userId) {
        return blogMapper.selectDraftBlog(userId);
    }

    //查询评论
    public List<Map<String,Object>> getComments(Integer userId,Integer currentPage) {
        currentPage = (currentPage-1)*5;
        List<Map<String, Object>> maps = blogMapper.selectComments(userId,currentPage);
        //修改评论状态，已读
        threadService.updateCommentSates(maps);
        return maps;
    }

    public int getCountComments(Integer userId){
        return blogMapper.selectCountComment(userId);
    }


    //删除评论
    public boolean removeComment(Integer id){
        int i = blogMapper.deleteComment(id);
        if(i>0){
            return true;
        }else{
            return false;
        }
    }
}
