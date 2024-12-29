package com.zjubs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjubs.backend.model.Goods;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
    @Select("SELECT * FROM goods WHERE platform = #{platform} AND keyword = #{keyword}")
    List<Goods> selectByPlatformAndKeyword(String platform, String keyword);
}