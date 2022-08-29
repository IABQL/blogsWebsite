package com.blogswebsite.controller;

import com.blogswebsite.entity.Blog;
import com.blogswebsite.service.BackBlogService;
import com.blogswebsite.util.BlogsWebsite;
import com.blogswebsite.util.HostHolder;
import com.blogswebsite.util.UpLoad;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 发布博客
 */
@RestController
public class ReleaseBlog {

    @Autowired
    private UpLoad upLoad;//上传图片oss工具类

    @Autowired
    private BackBlogService backBlogService;

    @Autowired
    private HostHolder hostHolder;



    /**
     * 上传博客封面图片
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upLoadCoverImg")
    @CrossOrigin
    public Map<String,String> upLoadCoverImg(@RequestParam("cover") MultipartFile file) throws IOException {

        Map<String,String> res = new HashMap<>();

        if (file != null){
            //生成上传图片随机名
            String fileName = BlogsWebsite.getImgName(file);
            String uploadUrl = upLoad.upLoadImg(file, "blog-cover/", fileName);
            res.put("uploadUrl",uploadUrl);//图片访问路径
            res.put("filePath","blog-cover/"+fileName);//图片上传路径
        }

        return res;
    }


    /**
     * 上传博客内容图片
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upLoadBlogContentImg")
    public String upLoadBlogContentImg(MultipartFile file) throws IOException {

        String uploadUrl = "";
        if (file != null){
            //生成上传图片随机名
            String fileName = BlogsWebsite.getImgName(file);

            uploadUrl = upLoad.upLoadImg(file, "blog-content-img/", fileName);
        }


        return uploadUrl;
    }





    /**
     * 删除上传的图片
     * @param url
     * @return
     */
    @GetMapping("/deleteUpLoadImg")
    public boolean deleteUpLoadImg(@RequestParam("imgUrl") String url) {
        if (StringUtils.isBlank(url)) return false;
        return upLoad.deleteImg(url);
    }

    /**
     * 博客保存
     */
    @PostMapping("/upLoadBlog")
    //用@RequestBody，将请求json中的数据自动解析到java类中,json中key要与Blog的字段同名
    public Map<String,Object> upLoadBlog(@RequestBody Blog blog){

        if(blog == null) return null;

        Map<String,Object> res = new HashMap();

        if(hostHolder.getUser() == null){
            res.put("states",false);
            res.put("msg","账号登入过期，请重新登入");
            return res;
        }
        if(hostHolder.getUser() != null){
            //优先使用ThreadLocal中的用户id
            blog.setUserId(hostHolder.getUser().getId());
        }

        if(blog.getStates() == 0){
            if (StringUtils.isBlank(blog.getTitle())
                    || StringUtils.isBlank(blog.getBlogExplain())
                    || StringUtils.isBlank(blog.getHtmlContent())
                    || blog.getUserId() == 0) {
                System.out.println(blog.getStates());
                System.out.println(blog.getHtmlContent());
                System.out.println(blog.getUserId());
                return null;
            }
        }

        if(blog.getStates() == 1){
            if(StringUtils.isBlank(blog.getTitle())
                    || StringUtils.isBlank(blog.getContent())
                    || blog.getUserId() == 0){

                return  null;
            }
        }


        //如果blogId不为0，表示修改blog
        if(blog.getId() > 0){
            boolean b = backBlogService.updateBlog(blog);
            res.put("blogId",blog.getId());
            res.put("states",b);
            if(b && blog.getStates()==0){
                res.put("msg","发表成功！");
            }else if(!b && blog.getStates()==0){
                res.put("msg","发表失败！");
            }else if(b && blog.getStates()==1){
                res.put("msg","保存成功！");
            }else if(!b && blog.getStates()==1){
                res.put("msg","保存失败！");
            }
        }else{
            //否则为新建
            int blogId = backBlogService.saveBlog(blog);
            res.put("blogId",blogId);
            res.put("states",blogId>0);
            boolean b = blogId>0;
            if(b && blog.getStates()==0){
                res.put("msg","发表成功！");
            }else if(!b && blog.getStates()==0){
                res.put("msg","发表失败！");
            }else if(b && blog.getStates()==1){
                res.put("msg","保存成功！");
            }else if(!b && blog.getStates()==1){
                res.put("msg","保存失败！");
            }
        }

        return res;
    }


}

