package com.blogswebsite.mapper;

import com.blogswebsite.entity.User;
import com.blogswebsite.entity.UserLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    //根据用户id查询其信息
    User selectById(int id);

    User selectByName(String name);//todo.

    User selectByEmail(String email);

    @Insert({"insert into user ( username, password, salt, email, header_url, create_time)" +
            " values (#{username},#{password},#{salt},#{email},#{headerUrl},#{createTime})"})
    int insertUser(User user);//插入用户

    int updateStatus(int id, int status);//状态修改

    int updateHeader(int id, String url);//头像修改

    @Update({"update user set password = #{password},salt = #{salt} where id = #{id}"})
    int updatePassword(int id, String password,String salt);

    //记录用户操作信息
    @Insert({"insert into user_log (userId,username,content,createTime) values (#{userId},#{username},#{content},#{createTime})"})
    int insertUserLog(UserLog userLog);

    //读取用户账号历史
    @Select({"select content,createTime,username from user_log where userId = #{userId} order by createTime desc"})
    @ResultType(UserLog.class)
    List<UserLog> selectUserLog(Integer userId);
}
