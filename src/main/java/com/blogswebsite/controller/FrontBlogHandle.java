package com.blogswebsite.controller;

import com.blogswebsite.entity.Comment;
import com.blogswebsite.params.BlogListParams;
import com.blogswebsite.service.ElasticsearchService;
import com.blogswebsite.service.FrontBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/FrontBlogHandle")
public class FrontBlogHandle {

    @Autowired
    private FrontBlogService frontBlogService;

    @Autowired
    private BlogListParams blogListParams;

    @Autowired
    private ElasticsearchService elasticsearchService;



    /**
     * 获取所有blog
     */
    @GetMapping("/getAllBlog")
    public BlogListParams  getAllBlog(
            @RequestParam(value = "currentPage",required = false,defaultValue = "1") Integer currentPage){

        List<Map<String, Object>> allBlog = frontBlogService.getAllBlog(currentPage);
        int totalBlog = frontBlogService.getTotalBlog();

        blogListParams.setCountBlog(totalBlog);
        blogListParams.setListBlog(allBlog);
        return blogListParams;
    }


    /**
     * 按关键字搜索blog
     */
    /*@GetMapping("/getAllBlogByKey")
    public List<Map<String,Object>>  getAllBlogByKey(
            @RequestParam(value = "currentPage",required = false,defaultValue = "1") Integer currentPage,
            @RequestParam(value = "key") String key){


        return frontBlogService.getAllBlogByKey(key, currentPage);
    }*/


    /**
     * 获取置顶推荐
     */
    @GetMapping("/getToppingBlog")
    public List<Map<String,Object>> getToppingBlog(){
        return frontBlogService.getToppingBlog();
    }

    /**
     * 读取文章内容
     */
    @GetMapping("/getArticle")
    public Map<String,Object> getArticle(@RequestParam("blogId") Integer id){
        return frontBlogService.getArticle(id);
    }

    /**
     * 查询blog总数目
     */
    @GetMapping("/getTotalBlog")
    public int getTotalBlog(){
        return frontBlogService.getTotalBlog();
    }

    //添加评论
    @PostMapping("/addComment")
    public Comment addComment(@RequestBody Comment comment){
        return frontBlogService.addComment(comment);
    }

    //查询评论
    @GetMapping("/getComments")
    public List<Map<String,Object>> getComment(@RequestParam("blogId") Integer blogId){
        return frontBlogService.getComments(blogId);
    }

    //点赞
    @GetMapping("/setThumbs")
    public void setThumbs(@RequestParam("blogId") Integer blogId,
                          @RequestParam("isThumbs") boolean isThumbs,
                          @RequestParam("title") String title){

        frontBlogService.setThumbs(blogId,isThumbs,title);
    }

    //计算阅读总数
    @GetMapping("/getViewCount")
    public int getCountView(){
        return frontBlogService.getCountView();
    }


    //关键词搜索blog
    @GetMapping("/getBlogByKey")
    public Map<String,Object> getBlogByKey(@RequestParam("key") String key,
                                            @RequestParam(value = "currentPage",defaultValue = "1") Integer currentPage) throws IOException {

        currentPage = (currentPage-1)*7;
        return elasticsearchService.highLightSearch(key,currentPage);
    }

}
