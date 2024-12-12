package com.zjubs.backend.utils;

public class ApiResult {

    public boolean ok;
    public String message;
    public Object payload;

    public ApiResult(boolean ok, Object payload) {
        this.ok = ok;
        this.payload = payload;
    }

    public ApiResult(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public ApiResult(boolean ok, String message, Object payload) {
        this.ok = ok;
        this.message = message;
        this.payload = payload;
    }

}

