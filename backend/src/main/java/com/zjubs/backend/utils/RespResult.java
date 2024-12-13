package com.zjubs.backend.utils;

public class RespResult {
    
    public int code;
    public String err; 
    public Object payload; 

    public RespResult(int code, String errString, Object payload) {
        this.code = code;
        this.err = errString;
        this.payload = payload;
    }

    public static RespResult success() {
        return new RespResult(0, null, null);
    }

    public static RespResult success(Object payload) {
        return new RespResult(0, null, payload);
    }

    public static RespResult fail(String err) {
        return new RespResult(1, err, null);
    }
}