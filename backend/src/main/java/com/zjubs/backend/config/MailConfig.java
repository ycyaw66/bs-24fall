package com.zjubs.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "mail")
public class MailConfig {
    private String mailAddress; 
    private String password; 
    private String redisStartWith; 
}
