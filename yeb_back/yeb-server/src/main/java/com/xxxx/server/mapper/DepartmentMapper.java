package com.xxxx.server.mapper;

import com.xxxx.server.pojo.Department;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.awt.*;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lwf
 * @since 2020-12-20
 */
public interface DepartmentMapper extends BaseMapper<Department> {

    List<Department> getAllDepartment(Integer id);
    Department selectById(Integer id);

    Department selectByName(String name);

    List<Department> selectByParentId(Integer parentId);
}
