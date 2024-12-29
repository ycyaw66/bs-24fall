package com.zjubs.backend.model;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@TableName("userlike")
public class Userlike {
    private String username;
    private String goodsTitle;
}
