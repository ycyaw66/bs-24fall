package com.zjubs.backend.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@TableName("user") // 对应数据库中的表名
public class User {
    @TableId(type = IdType.AUTO) // 主键生成策略为自增
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer gender; // 0: 不愿透露, 1: 男, 2: 女
    private String address;
}