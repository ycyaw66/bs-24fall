package com.zjubs.backend.controller.dto;

import lombok.Data;

@Data
public class LikeBody {
    public String username;
    public String goods;
    public String operation;
    public String authorization;
}
