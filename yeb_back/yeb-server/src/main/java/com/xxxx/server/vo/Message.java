package com.xxxx.server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Comparator;

/**
 * @author lwf
 * @title: Message
 * @projectName yeb
 * @description: TODO
 * @date 2020/12/2211:50
 */
@Data
@ApiModel("消息体")
public class Message implements Comparable {
    private String notSelf;
    private String to;
    private String content;
    private String time;
    @ApiModelProperty("是否登录本人")
    private boolean self;
    @ApiModelProperty("头像地址")
    private String userFace;

    @Override
    public int compareTo(Object o) {
        if(null==o){
            return 1;
        }
        if(null==time){
            return -1;
        }
        if(o==null){
            return 1;
        }
        if(null==((Message)o).getTime())
            return 1;
        return time.compareTo(((Message)o).getTime())>0?1:-1;
    }
}
