package com.zjubs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjubs.backend.model.Userlike;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserlikeMapper extends BaseMapper<Userlike> {
    @Select("SELECT * FROM userlike WHERE username = #{username} AND goods_link = #{goodsLink}")
    Userlike selectUserlike(String username, String goodsLink);

    @Delete("DELETE FROM userlike WHERE username = #{username} AND goods_link = #{goodsLink}")
    void deleteUserlike(String username, String goodsLink);

    @Select("SELECT * FROM userlike WHERE username = #{username}")
    List<Userlike> selectUserlikeByUsername(String username);
}