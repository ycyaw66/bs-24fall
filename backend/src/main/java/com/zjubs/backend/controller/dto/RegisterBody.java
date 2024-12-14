package com.zjubs.backend.controller.dto;

import lombok.Data;

@Data
public class RegisterBody {
    private String username;
    private String password;
    private String email;
    private String code;
    private String uuid;
}
