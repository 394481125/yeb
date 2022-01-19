package com.xxxx.server.service;

import com.xxxx.server.pojo.Salary;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lwf
 * @since 2020-12-20
 */
public interface ISalaryService extends IService<Salary> {
    /**
     * 获取所有工资套账信息
     * @return
     */
    List<Salary> getAllSalary();

    /**
     * 更新工资账套信息
     * @return
     */
    void  updateSalary(Salary salary);

    /**
     * 删除工资账套信息 通过id
     * @param id  工资账套id  salary.id
     * @return
     */
    void  deleteSalary(Integer id);

    /**
     * 添加工资账套信息
     * @param salary
     * @return
     */
    void addSalary(Salary salary);
}
