package com.blogswebsite.controller;

import com.blogswebsite.service.BackBlogService;
import com.blogswebsite.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/blogHandle")
public class BackBlogHandle {

    @Autowired
    private BackBlogService backBlogService;

    @Autowired
    private HostHolder hostHolder;


    /**
     * 删除博客
     * @return
     */
    @GetMapping("/deleteBlog")
    public boolean deleteBlog(@RequestParam("blogId") Integer blogId) {
        //删除除了要删除blog表，还有tag
        return backBlogService.deleteBlog(blogId);
    }

    /**
     * 根据userId获取所有已发表博客(标题，创建时间，type，标签)
     * @param userId
     * @param states  发布状态
     * @param currentPage 当前页
     * @return
     */
    @GetMapping("/getMyBlog")
    public List<Map<String,Object>> getBlogByUserId(
            @RequestParam(value = "userId",required = false) Integer userId,
            @RequestParam(value = "states",required = false,defaultValue = "0") Integer states,
            @RequestParam(value = "page",required = false,defaultValue = "1") Integer currentPage,
            @RequestParam(value = "year",required = false) Integer year,
            @RequestParam(value = "month",required = false) Integer month){

        if(hostHolder.getUser() != null){
            //优先使用ThreadLocal中的用户id
            userId = hostHolder.getUser().getId();

        }
        List<Map<String, Object>> blogs = backBlogService.getBlogByUserId(userId,currentPage,states,year,month);
        return blogs;
    }


    /**
     * 获取该用户blog条目总数(按照已发表、草稿区分)
     * @param userId
     * @param states  发布状态
     * @return
     */
    @GetMapping("/getBlogTotalData")
    public int getBlogTotalData(
            @RequestParam(value = "userId",required = false) Integer userId,
            @RequestParam(value = "states",required = false,defaultValue = "0") Integer states){

        if(hostHolder.getUser() != null){
            //优先使用ThreadLocal中的用户id
            userId = hostHolder.getUser().getId();
        }
        return backBlogService.getBlogTotalData(userId,states);
    }


    /**
     * 获取该用户blog条目总数(按照标签名)
     * @param userId
     * @return
     */
    @GetMapping("/getBlogTotalDataByTag")
    public int getBlogTotalDataByTag(
            @RequestParam(value = "userId",required = false) Integer userId,
            @RequestParam(value = "tagName") String tagName){

        if(hostHolder.getUser() != null){
            //优先使用ThreadLocal中的用户id
            userId = hostHolder.getUser().getId();
        }
        return backBlogService.getBlogTotalDataByTag(userId,tagName);
    }

    @GetMapping("/getBlogTotalDataByKey")
    public int getBlogTotalDataByKey(
            @RequestParam(value = "userId",required = false) Integer userId,
            @RequestParam("key") String key){

        if(hostHolder.getUser() != null){
            //优先使用ThreadLocal中的用户id
            userId = hostHolder.getUser().getId();
        }
        return backBlogService.getTotalDataByUserIdKey(userId,key);
    }

    /**
     * 根据blogId获取博客全部信息,修改博客时
     */
    @GetMapping("/getBlogAllInformation")
    public Map<String,Object> getBlogAllByBlogId(@RequestParam("blogId") Integer blogId){
        Map<String, Object> blog = backBlogService.getBlogAllByBlogId(blogId);
        return blog;
    }

    /**
     * 修改blog type类型
     */
    @GetMapping("/changeType")
    public boolean changeType(@RequestParam("blogId") int blogId,@RequestParam("type") String type){
        boolean res = backBlogService.changeBlogType(blogId,type);
        return res;
    }


    //根据标签获取用户的blog
    @GetMapping("/getBlogByUserIdTag")
    public List<Map<String,Object>> getBlogByUserIdTag(
            @RequestParam(value = "userId",required = false) Integer userId,
            @RequestParam(value = "currentPage",defaultValue = "1") Integer currentPage,
            @RequestParam(value = "states",defaultValue = "0") Integer states,
            @RequestParam("tagName") String tagName,
            @RequestParam(value = "year",required = false) Integer year,
            @RequestParam(value = "month",required = false) Integer month){


        if(hostHolder.getUser() != null){
            //优先使用ThreadLocal中的用户id
            userId = hostHolder.getUser().getId();
        }
        List<Map<String, Object>> list = backBlogService.getBlogByUserIdTag(userId, currentPage, states, tagName,year,month);
        return list;
    }

    /**
     *  根据关键词搜索blog
     */
    @GetMapping("/searchBlogByKey")
    public List<Map<String,Object>> searchBlogByKey(
            @RequestParam("key") String key,
            @RequestParam(value = "userId",required = false) Integer userId,
            @RequestParam(value = "page",defaultValue = "1") Integer currentPage){

        if(hostHolder.getUser() != null){
            //优先使用ThreadLocal中的用户id
            userId = hostHolder.getUser().getId();
        }
        return backBlogService.getBlogByKey(userId, key, currentPage);

    }


    /**
     * 获取用户的草稿
     * @param userId
     * @return
     */
    @GetMapping("/getDraftBlog")
    public List<Map<String,Object>> getDraftBlog(@RequestParam(value = "userId",required = false) Integer userId) {

        if(hostHolder.getUser() != null){
            //优先使用ThreadLocal中的用户id
            userId = hostHolder.getUser().getId();
        }

        return backBlogService.getDraftBlog(userId);
    }


    @GetMapping("/getComments")
    public List<Map<String,Object>> getComments(@RequestParam(value = "currentPage",defaultValue = "1",required = false)
                                                            Integer currentPage){
        Integer userId = null;
        if(hostHolder.getUser() != null){
            userId = hostHolder.getUser().getId();
        }
        return backBlogService.getComments(userId,currentPage);
    }


    @GetMapping("/getCountComments")
    public int getCountComments(){
        Integer userId = null;
        if(hostHolder.getUser() != null){
            userId = hostHolder.getUser().getId();
        }
        return backBlogService.getCountComments(userId);
    }


    //删除评论
    @GetMapping("deleteComment")
    public boolean removeComment(@RequestParam("id") Integer id){
        return backBlogService.removeComment(id);
    }
}
