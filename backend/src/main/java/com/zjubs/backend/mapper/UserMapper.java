package com.zjubs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjubs.backend.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT password FROM user WHERE username = #{username}")
    String getPasswordByUsername(String username);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);
}