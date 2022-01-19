package com.xxxx.server.service;

import com.xxxx.server.pojo.Department;
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
public interface IDepartmentService extends IService<Department> {
    /**
     *
     * @return
     */
    List<Department> getAllDepartment();

    /**
     * 根据id获取部门信息
     * @param id
     * @return
     */
    Department selectById(Integer id);

    /**
     * 根据部门名称获取部门信息
     * @param name
     * @return
     */
    Department selectByName(String name);

    List<Department> selectByParentId(Integer parentId);
}
