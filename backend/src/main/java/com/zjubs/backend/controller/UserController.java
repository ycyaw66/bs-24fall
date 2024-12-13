package com.zjubs.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zjubs.backend.controller.dto.UserLoginBody;
import com.zjubs.backend.model.User;
import com.zjubs.backend.service.UserService;
import com.zjubs.backend.utils.RespResult;

@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public RespResult login(@Validated @RequestBody UserLoginBody body) {
        String username = body.getUsername();
        String password = body.getPassword();

        User user = userService.login(username, password);
        if (user == null) {
            return RespResult.fail("用户名或密码错误");
        }

        String token = userService.generateToken(user);
        userService.saveToken(user.getUsername(), token);
        Map<String, Object> data = new HashMap<String, Object>(2) {{
            put("username", user.getUsername());
            put("token", token);
        }};
        
        return RespResult.success(data);
    }
    
}
