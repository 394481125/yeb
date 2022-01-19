package com.xxxx.server.service.impl;

import com.xxxx.server.pojo.Department;
import com.xxxx.server.mapper.DepartmentMapper;
import com.xxxx.server.service.IDepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lwf
 * @since 2020-12-20
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements IDepartmentService {

    @Resource
    private DepartmentMapper departmentMapper;
    @Override
    public List<Department> getAllDepartment() {
        return departmentMapper.getAllDepartment(-1);
    }

    @Override
    public Department selectById(Integer id) {
        return departmentMapper.selectById(id);
    }

    @Override
    public Department selectByName(String name) {
        return departmentMapper.selectByName(name);
    }

    @Override
    public List<Department> selectByParentId(Integer parentId) {
        return departmentMapper.selectByParentId(parentId);
    }
}
