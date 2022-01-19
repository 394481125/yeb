package com.xxxx.server.service;

import com.xxxx.server.pojo.Employee;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxxx.server.pojo.RespBean;
import com.xxxx.server.vo.EmpInfo;
import com.xxxx.server.vo.EmployeePage;
import com.xxxx.server.vo.RespPageBean;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lwf
 * @since 2020-12-20
 */
public interface IEmployeeService extends IService<Employee> {

    /**
     * 分页查询 Employee
     * @param page 分页查询的信息
     * @return
     * @throws ParseException
     */
     RespPageBean pageQueryPlus(EmployeePage page);

    /**
     * 查询最大工号对象
     * @return
     */
    RespBean maxWorkId();

    /**
     * 分页查询员工账套
     * @param currentPage
     * @param size
     * @return
     */
    RespPageBean getEmployeeWithSalary(Integer currentPage, Integer size);

    /**
     * 修改工资套账
     * @param eid
     * @param sid
     * @return
     */
    RespBean updateEmployeeSalary(Integer eid, Integer sid);

    /**
     * 导入表格
     * @param fileName
     * @param file
     * @return
     * @throws Exception
     */
    boolean batchImport(String fileName, MultipartFile file) throws Exception;


    List<EmpInfo> gteAllEmployee();
}
