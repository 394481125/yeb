package com.xxxx.server.mapper;

import com.xxxx.server.pojo.Salary;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lwf
 * @since 2020-12-20
 */
public interface SalaryMapper extends BaseMapper<Salary> {

    Salary selectByName(String name);
}
