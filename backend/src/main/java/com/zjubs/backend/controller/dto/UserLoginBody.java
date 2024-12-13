package com.zjubs.backend.controller.dto;

import lombok.Data;

@Data
public class UserLoginBody {
    public String username;
    public String password;
}
