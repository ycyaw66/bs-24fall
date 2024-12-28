package com.zjubs.backend.controller.dto;

import lombok.Data;

@Data
public class SearchBody {
    private String keyword;
    private String platform;
    private String authorization;
}
