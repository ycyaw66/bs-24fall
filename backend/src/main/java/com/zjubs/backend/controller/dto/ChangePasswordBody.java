package com.zjubs.backend.controller.dto;

import lombok.Data;

@Data
public class ChangePasswordBody {
    private String username;
    private String password;
    private String newpassword;
}
