package com.xxxx.server.exception;

import lombok.Data;


/**
 * @Desc 自定义参数异常
 * @Author lsh
 * @Create Time 2020/12/21 11:39
 */
@Data
public class ParamsException extends RuntimeException{
    private Integer code=300;
    private String msg="参数异常";

    public ParamsException() {
        super("参数异常!");
    }

    public ParamsException(String msg) {
        super(msg);
        this.msg = msg;
    }

}