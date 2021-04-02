package com.katouyi.tools.system.core;

import java.io.Serializable;
import java.util.Date;

public class Result<T> implements Serializable {
    private static final long serialVersionUID = 8066938185032422786L;
    private int code;
    private String message;
    private T data;
    private Date serverTime;

    public Result() {
        this.code = 0;
        this.serverTime = new Date();
    }

    public Result(int errorCode, String message) {
        this(errorCode, message, null);
    }

    public Result(String message, T data) {
        this(0, message, data);
    }

    public Result(T data) {
        this(0, (String)null, data);
    }

    public Result(ErrorCode errorCode, T data) {
        this.code = 0;
        this.serverTime = new Date();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.data = data;
    }

    public Result(int errorCode, String message, T data) {
        this.code = 0;
        this.serverTime = new Date();
        this.code = errorCode;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result("成功", data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result(msg, data);
    }

    public static <T> Result<T> failed(T data) {
        return new Result(-1, "失败", data);
    }

    public static <T> Result<T> failed(int errorCode, String message, T data) {
        return new Result(errorCode, message, data);
    }

    public static <T> Result<T> failed(int errorCode, String message) {
        return new Result(errorCode, message, (Object)null);
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Date getServerTime() {
        return this.serverTime;
    }

    public void setServerTime(Date serverTime) {
        this.serverTime = serverTime;
    }
}