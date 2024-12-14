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
        redisUtils.set(token, username);
    }

    public String getUsernameByToken(String token) {
        return (String)redisUtils.get(token);
    }

    public void removeToken(String token) {
        String username = (String)redisUtils.get(token);
        redisUtils.del(token);
        redisUtils.del(username);
    }

    public boolean checkToken(String username, String token) {
        String realToken = (String)redisUtils.get(username);
        if (realToken == null || !realToken.equals(token)) {
            return false;
        }
        return true;
    }

    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    public void updateProfile(String username, User user) {
        userMapper.updateProfile(username, user.getPassword(), user.getPhone(), user.getGender(), user.getAddress());
    }

    public Boolean register(User user) {
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            return false;
        }
        if (userMapper.selectByEmail(user.getEmail()) != null) {
            return false;
        }
        userMapper.insert(user);
        return true;
    }
}
