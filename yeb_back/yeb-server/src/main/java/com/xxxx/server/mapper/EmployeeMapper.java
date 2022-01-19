package com.xxxx.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxxx.server.pojo.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxxx.server.vo.EmpInfo;
import com.xxxx.server.vo.EmployeePage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lwf
 * @since 2020-12-20
 */
public interface EmployeeMapper extends BaseMapper<Employee> {
    /**
     * 查询员工账套
     * @param page
     * @return
     */
    IPage<Employee> getEmployeeWithSalary(Page<Employee> page);

    /**
     * 修改工资账套
     * @param eid
     * @param sid
     * @return
     */
    int updateEmployeeSalary(@Param("eid") Integer eid, @Param("sid") Integer sid);

    /**
     * 分页查询员工基本信息数据
     * @param page
     * @param employeePage
     * @return
     */
    Page<EmpInfo> selectPageEmployee(Page<EmpInfo> page, EmployeePage employeePage);

    /**
     * 根据身份证号查询员工
     * @param idCard
     * @return
     */
    Employee selectByIdCard(String idCard);

    /**
     * 通过id查找员工所有信息
     * @param id
     * @return
     */
    EmpInfo queryEmpInfoById(Integer id);

    List<EmpInfo> getAllEmployee();
}
