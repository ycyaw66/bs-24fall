package com.zjubs.backend.controller.dto;

import lombok.Data;

@Data
public class IsLikedBody {
    private String username;
    private String goods;
    private String authorization;
}
