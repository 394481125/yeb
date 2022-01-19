package com.xxxx.server;

import com.xxxx.server.exception.ParamsException;
import com.xxxx.server.pojo.RespBean;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;


/**
 * @Desc 全局异常处理类
 * @Author lsh
 * @Create Time 2020/12/20 21:53
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 捕捉 BindException
     * entity 实体的注解抛出的异常
     * @param e
     * @return
     */
    @ResponseBody       //全局异常处理 返回json
    @ExceptionHandler(value = BindException.class)
    public RespBean entityException(BindException e) {
        String msg=e.getBindingResult().getFieldError().getDefaultMessage();
        return  RespBean.error(msg);
    }

    /**
     * 捕捉 BindException
     * entity 实体的注解抛出的异常
     * @param e
     * @return
     */
    @ResponseBody       //全局异常处理 返回json
    @ExceptionHandler(value = ParamsException.class)
    public RespBean paramsException(ParamsException e) {
        String msg=e.getMsg();
        long code=e.getCode();
        return  RespBean.error(code,msg);
    }

    /**
     * 捕捉 BindException
     * entity 实体的注解抛出的异常
     * @param e
     * @return
     */
    @ResponseBody       //全局异常处理 返回json
    @ExceptionHandler(value = IOException.class)
    public RespBean iOException(IOException e) {
        return  RespBean.error(700,"文件导出异常");
    }

    /**
     * 数据库异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = SQLException.class)
    public RespBean sqlException(SQLException e){
        if (e instanceof SQLIntegrityConstraintViolationException){
            return RespBean.error("数据存在关联数据，操作异常");
        }
        return RespBean.error("数据库异常，操作失败");
    }
}
