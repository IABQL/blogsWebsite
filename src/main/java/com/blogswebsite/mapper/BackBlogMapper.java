package com.blogswebsite.mapper;

import com.blogswebsite.entity.Blog;
import org.apache.ibatis.annotations.*;


import java.util.List;
import java.util.Map;

@Mapper
public interface BackBlogMapper {

    //增加blog
    int insertBlog(Blog blog);

    //修改blog
    int updateBlog(Blog blog);

    //查询所有标签对应的id
    List<Integer> selectBlogTags(String[] tags);

    //根据标签名查询其id
    int selectTagId(String tag);

    //根据userId、tagId、states分页查询
    List<Map<String,Object>> selectBlogByUserIdTag(Integer userId, Integer currentPag, Integer states,
                                                   Integer tagId,String fromDate,String toDate);

    //添加blog的tag到blog-tag关系表中
    int insertBlogTag(int blogId,List<Integer> list, int userId);

    //根据blogId删除blog-tag关系表中的标签数据
    int deleteBlogTag(int blogId);

    //删除blog
    int deleteBlog(int blogId);


    /**
     * 根据userId获取所有博客
     * @param userId
     * @param currentPag 当前页
     * @param states  状态（已发表与草稿）
     * @return
     */
    List<Map<String,Object>> selectBlogByUserId(Integer userId, Integer currentPag,
                                                Integer states,String fromDate,String toDate);



    //查询blog总数目(按照已发表与草稿区分)
    int selectTotalBlogByUserId(Integer userId, Integer states);

    //查询blog总数目(按照标签)
    @Select({"select count(*) from tagblogrelation where userId = #{userId} and tagId = #{tagId}"})
    int selectTotalBlogByUserIdTag(Integer userId, Integer tagId);

    //查询blog总数目(按照key,userId)
    @Select({"select count(*) from blog where userId = #{userId} and title like '%${key}%' "})
    int selectTotalBlogByUserIdKey(Integer userId, String key);


    //根据id查博客对应的所有标签
    List<String> selectTagsByBlogId(int id);

    //修改blog——type
    int updateBlogType(int blogId, int type);

    //根据blogId获取博客全部信息
    Map<String,Object> selectBlogAllByBlogId(int blogId);

    //根据关键字查询用户下的blog
    List<Map<String,Object>> selectBlogByUserIdKey(Integer userId, String key, Integer currentPag);

    //搜寻用户的草稿blog
    List<Map<String,Object>> selectDraftBlog(Integer userId);

    //查询评论
    @Select({"select id,blogId,username,headerUrl,content,createTime,blogTitle,states from comment where" +
            " blogId in (select blogId from blog where userId = #{userId}) order by states limit #{currentPage},5"})
    @ResultType(java.util.HashMap.class)
    List<Map<String,Object>> selectComments(Integer userId,Integer currentPage);

    //删除评论
    @Delete({"delete from comment where id = #{id}"})
    int deleteComment(Integer id);

    //修改评论状态，已读
    int updateCommentSates(List<Integer> list);

    @Select({"select count(*) from comment where blogId in (select blogId from blog where userId = #{userId})"})
    @ResultType(java.lang.Integer.class)
    int selectCountComment(Integer userId);

    @Select({"select userId from blog where id = #{id}"})
    @ResultType(java.lang.Integer.class)
    int selectUserId(Integer id);
}
