package com.zjubs.backend.controller.dto;

import lombok.Data;

@Data
public class ForgetBody {
    private String email;
    private String password;
    private String code;
    private String uuid;
}
