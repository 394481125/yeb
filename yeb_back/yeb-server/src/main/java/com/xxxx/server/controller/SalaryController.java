package com.xxxx.server.controller;


import com.xxxx.server.pojo.RespBean;
import com.xxxx.server.pojo.Salary;
import com.xxxx.server.service.impl.SalaryServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lwf
 * @since 2020-12-20
 */
@RestController
@RequestMapping("/salary")
public class SalaryController {
    @Resource
    private SalaryServiceImpl salaryService;

    @ApiOperation(value = "获取所有工资套账")
    @GetMapping("/sob")
    public Object findAllSalary() {
        List<Salary> allSalary = salaryService.getAllSalary();
        return allSalary;
    }

    @ApiOperation(value = "增加工资套账")
    @PostMapping("/sob")
    public RespBean addSalary(@RequestBody Salary salary) {


        salaryService.addSalary(salary);
        return RespBean.success("添加成功");
    }

    @ApiOperation(value = "修改工资套账")
    @PutMapping("/sob")
    public RespBean updateSalary(@RequestBody Salary salary) {
        salaryService.updateSalary(salary);
        return RespBean.success("修改成功");
    }

    @ApiOperation(value = "删除工资套账")
    @DeleteMapping("/sob/{id}")
    public RespBean deleteSalary(@PathVariable("id") Integer id) {
        salaryService.deleteSalary(id);
        return RespBean.success("删除成功");
    }
}
