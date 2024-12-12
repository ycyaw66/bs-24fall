package com.zjubs.backend.controller;

import com.zjubs.backend.mapper.UserMapper;
import com.zjubs.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/testDb")
    public String testDbConnection() {
        try {
            List<User> users = userMapper.selectList(null); // 使用 null 作为参数，查询所有用户
            if (users != null && !users.isEmpty()) {
                // 输出第一个用户的用户名
                return "Database connection is successful. The first user is: " + users.get(1).getUsername();
            } else {
                return "Database connection is successful but no data found.";
            }
        } catch (Exception e) {
            return "Database connection failed: " + e.getMessage();
        }
    }
}