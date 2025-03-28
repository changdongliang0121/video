package com.dahuaboke.signaling.vo;

import com.dahuaboke.signaling.constants.Constant;

public class Result {

    private String type;

    private String code;

    private String message;


    private Result(String type, String code, String message) {
        this.type = type;
        this.code = code;
        this.message = message;
    }

    public static Result success(String type, String message) {
        return new Result(type, Constant.SUCCESS_CODE, message);
    }

//    public static Result success(String type, String[] message) {
//        return new Result(type, Constant.SUCCESS_CODE, message);
//    }

    public static Result fail(String type, String message) {
        return new Result(type, Constant.FAIL_CODE, message);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
