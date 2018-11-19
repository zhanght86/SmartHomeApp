package com.gatz.smarthomeapp.bean;

import com.gatz.smarthomeapp.utils.ResultCode;

import java.io.Serializable;

public class Result<T> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public final static String SUCCESS = "success";
    public final static String FAILED = "failed";

    private String status = SUCCESS;
    private String code = ResultCode.CODE_SUCCESS;
    private T t;
    private String msg;

    public Result() {
    }

    public Result(T t) {
        this.t = t;
    }

    public Result(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public Result<T> failed(String msg) {
        this.status = FAILED;
        this.code = ResultCode.CODE_FAILED;

        return this;
    }

    public Result<T> failed(String msg, String errorCode) {
        this.status = FAILED;
        this.code = errorCode;

        return this;
    }

    public Result<T> success(T t) {
        this.status = SUCCESS;
        this.code = ResultCode.CODE_SUCCESS;
        this.t = t;
        return this;
    }

    public Result<T> success() {
        this.status = SUCCESS;
        this.code = ResultCode.CODE_SUCCESS;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}