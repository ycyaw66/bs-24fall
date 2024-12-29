package com.zjubs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjubs.backend.model.History;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface HistoryMapper extends BaseMapper<History> {
    @Select("SELECT * FROM history WHERE product_title = #{title} ORDER BY time_stamp ASC")
    List<History> getHistoryByTitle(String title);
}