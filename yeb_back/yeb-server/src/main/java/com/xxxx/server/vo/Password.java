package com.xxxx.server.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("密码修改")
public class Password {
    @ApiModelProperty(value = "用户id")
    private Integer adminId;
    @ApiModelProperty(value = "旧密码")
    private String oldPass;
    @ApiModelProperty(value = "新密码")
    private String pass;
    @ApiModelProperty(value = "校验密码")
    private String checkPass;

}
