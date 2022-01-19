package com.xxxx.server.util;
import com.xxxx.server.exception.ParamsException;

/**
 * @Desc 自定义参数异常抛出
 * @Author lsh
 * @Create Time 2020/12/21 11:39
 */
public class AssertUtil {
    public  static void isTrue(Boolean flag,String msg){
        if(flag){
            throw  new ParamsException(msg);
        }
    }
}
