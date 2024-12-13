package com.zjubs.backend.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zjubs.backend.mapper.UserMapper;
import com.zjubs.backend.model.User;
import com.zjubs.backend.utils.RedisUtils;


@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtils redisUtils;
    
    public User login(String username, String password) {
        String realPassword = userMapper.getPasswordByUsername(username);
        if (realPassword != null && realPassword.equals(password)) {
            return userMapper.selectByUsername(username);
        }
        return null;
    }

    public String generateToken(User user) {
        return UUID.randomUUID().toString();
    }

    public void saveToken(String username, String token) {
        redisUtils.set(username, token);
    }

    public boolean checkToken(String username, String token) {
        String realToken = (String)redisUtils.get(username);
        if (realToken == null || !realToken.equals(token)) {
            return false;
        }
        return true;
    }
}
