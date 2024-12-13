package com.zjubs.backend.controller;

import com.zjubs.backend.mapper.UserMapper;
import com.zjubs.backend.model.User;
import com.zjubs.backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public String testDbConnection() {
        try {
            List<User> users = userMapper.selectList(null); // 使用 null 作为参数，查询所有用户
            User user = userMapper.selectByUsername("admin");
            String token = userService.generateToken(user);
            userService.saveToken(user.getUsername(), token);
            Boolean check = userService.checkToken(user.getUsername(), token);
            if (users != null && !users.isEmpty()) {
                // 输出第一个用户的用户名
                return "Database connection is successful. The first user is: " + users.get(1).getUsername() + ". Token is " + token + ". Check is " + check;
            } else {
                return "Database connection is successful but no data found.";
            }
        } catch (Exception e) {
            return "Database connection failed: " + e.getMessage();
        }
    }
}