package com.beiying.net.http;

/**
 * Created by beiying on 19/4/7.
 */

public enum HttpStatus {

    Continue(100, "Continue"),
    OK(200, "OK");

    private int mCode;
    private String mMessage;

    private HttpStatus(int code, String message) {
        this.mCode = code;
        this.mMessage = message;
    }

    public static HttpStatus getValue(int code) {
        for(HttpStatus httpStatus: values()) {
            if (httpStatus.mCode == code) {
                return httpStatus;
            }
        }
        return null;
    }
}
