package com.xxxx.server.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 *  查询Employee分页数据
 * @author lwf
 * @title: EmployeePage
 * @projectName yeb
 * @date 2020/12/2013:24
 */
@Data
@ApiModel("员工资料首页请求")
public class EmployeePage  implements Serializable{
    @ApiModelProperty(value = "当前页",required = true)
    private Integer currentPage=1;
    @ApiModelProperty(value = "页面数据条数",required = true)
    private Integer size=10;
    @ApiModelProperty(value = "员工姓名")
    private String name;
    @ApiModelProperty(value = "政治面貌")
    private String politicId;
    @ApiModelProperty(value = "民族")
    private String nationId;
    @ApiModelProperty(value = "职位")
    private Integer posId;
    @ApiModelProperty(value = "职称")
    private Integer jobLevelId;
    @ApiModelProperty(value = "聘用形式")
    private String engageForm;
    @ApiModelProperty("部门编号")
    private Integer departmentId;
    @ApiModelProperty("在职，开始结束时间以,隔开")
    private String beginDateScope;
}
