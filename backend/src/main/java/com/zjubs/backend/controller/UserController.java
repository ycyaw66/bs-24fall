package com.zjubs.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zjubs.backend.controller.dto.AuthorizationBody;
import com.zjubs.backend.controller.dto.ChangePasswordBody;
import com.zjubs.backend.controller.dto.ChangeProfileBody;
import com.zjubs.backend.controller.dto.UserLoginBody;
import com.zjubs.backend.model.User;
import com.zjubs.backend.service.UserService;
import com.zjubs.backend.utils.RespResult;

@RestController
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
    
    @PostMapping("/logout")
    public RespResult logout(@Validated @RequestBody AuthorizationBody body) {
        String token = body.getAuthorization();
        String username = userService.getUsernameByToken(token);
        if (!userService.checkToken(username, token)) {
            return RespResult.fail("身份验证失败");
        }
        userService.removeToken(token);
        return RespResult.success();
    }

    @PostMapping("/getprofile")
    public RespResult getProfile(@Validated @RequestBody AuthorizationBody body) {
        String token = body.getAuthorization();
        String username = userService.getUsernameByToken(token);
        if (!userService.checkToken(username, token)) {
            return RespResult.fail("身份验证失败");
        }
        User user = userService.getUserByUsername(username);
        Map<String, Object> data = new HashMap<String, Object>(5) {{
            put("username", user.getUsername());
            put("email", user.getEmail());
            put("phone", user.getPhone());
            put("gender", user.getGender());
            put("address", user.getAddress());
        }};
        return RespResult.success(data);
    }

    @PostMapping("/changeprofile")
    public RespResult changeProfile(@Validated @RequestBody ChangeProfileBody body) {
        String token = body.getAuthorization();
        String username = userService.getUsernameByToken(token);
        if (!userService.checkToken(username, token)) {
            return RespResult.fail("身份验证失败");
        }
        User user = userService.getUserByUsername(username);
        user.setPhone(body.getPhone());
        user.setGender(body.getGender());
        user.setAddress(body.getAddress());
        userService.updateProfile(username, user);
        return RespResult.success();
    }

    @PostMapping("/password")
    public RespResult changePassword(@Validated @RequestBody ChangePasswordBody body) {
        String username = body.getUsername();
        String password = body.getPassword();
        String newpassword = body.getNewpassword();
        User user = userService.login(username, password);
        if (user == null) {
            return RespResult.fail("原密码错误");
        }
        user.setPassword(newpassword);
        userService.updateProfile(username, user);
        return RespResult.success();
    }
}
