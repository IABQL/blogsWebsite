package com.blogswebsite.mapper;

import com.blogswebsite.entity.Blog;
import com.blogswebsite.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface FrontBlogMapper {

    @Select({"select id,title,blogExplain,blogCover,createTime,view,thumbs,type from blog where states = 0 order by type desc limit #{currentPage},4"})
    @ResultType(java.util.HashMap.class)
    List<Map<String,Object>> selectAllBlog(Integer currentPage);


    List<Map<String,Object>> selectAllBlogByKey(String key, Integer currentPage);

    @Select({"select id,title,blogCover,createTime,view,thumbs from blog where type = 1 order by view desc limit 0,3"})
    @ResultType(java.util.HashMap.class)
    List<Map<String,Object>> selectToppingBlog();

    @Select({"select id,userId,title,htmlContent,createTime,view,thumbs from blog where id = #{id}"})
    @ResultType(java.util.HashMap.class)
    Map<String,Object> selectBlogById(Integer id);


    @Select({"select id,title,blogExplain,blogCover,createTime,view,thumbs,type from blog where" +
            " id = #{id}"})
    @ResultType(com.blogswebsite.entity.Blog.class)
    Blog getBlogById(Integer id);


    //查询blog总数目(按照已发表与草稿区分)
    @Select({"select  count(*) from blog where states = 0"})
    @ResultType(int.class)
    int selectTotalBlog();

    //评论保存
    //keyColumn指的是数据库表中自增主键的字段名,keyProperty:指定对象中定义的自增主键属性，后续可通过获取对象的该属性，得到插入数据库的新记录的id值
    @Insert({"insert into comment (blogId, username, headerUrl, content, createTime,blogTitle) values (#{blogId},#{username},#{headerUrl},#{content},#{createTime},#{title})"})
    @Options(keyProperty = "id",keyColumn = "id")
    int insertComment(Comment comment);

    //读取评论
    @Select({"select username, headerUrl, content, createTime from comment where blogId = #{blogId}"})
    @ResultType(java.util.HashMap.class)
    List<Map<String,Object>> selectComments(Integer blogId);

    //更新阅读数
    @Update({"update blog set view = view+1 where id = #{id}"})
    int updateBlogView(Integer id);

    //点赞
    @Update({"update blog set thumbs = thumbs+#{num} where id = #{id}"})
    int updateBlogThumbs(Integer id,Integer num);

    //计算阅读总数
    @Select({"select sum(view) from blog"})
    @ResultType(Integer.class)
    int selectCountView();
}
