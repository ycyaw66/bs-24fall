package com.zjubs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjubs.backend.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT password FROM user WHERE username = #{username}")
    String getPasswordByUsername(String username);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User selectByEmail(String email);

    // 更新 phone，gender和address
    @Update("UPDATE user SET password = #{password}, phone = #{phone}, gender = #{gender}, address = #{address} WHERE username = #{username}")
    void updateProfile(String username, String password, String phone, String gender, String address);
}