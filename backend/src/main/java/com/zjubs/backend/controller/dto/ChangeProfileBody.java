package com.zjubs.backend.controller.dto;

import lombok.Data;

@Data
public class ChangeProfileBody {
    private String phone;
    private String gender;
    private String address;
    private String authorization;
}
