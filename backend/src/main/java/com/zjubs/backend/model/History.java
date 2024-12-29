package com.zjubs.backend.model;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@TableName("history")
public class History {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String productTitle;
    private String productPrice;
    private Timestamp timeStamp;
}
