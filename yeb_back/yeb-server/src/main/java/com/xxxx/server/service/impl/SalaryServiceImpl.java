package com.xxxx.server.service.impl;

import com.xxxx.server.pojo.Salary;
import com.xxxx.server.mapper.SalaryMapper;
import com.xxxx.server.service.ISalaryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxxx.server.util.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


import javax.annotation.Resource;
import java.time.LocalDateTime;
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
public class SalaryServiceImpl extends ServiceImpl<SalaryMapper, Salary> implements ISalaryService {
    @Resource
    private SalaryMapper salaryMapper;


    @Override
    public List<Salary> getAllSalary(){
        List<Salary> salaries = salaryMapper.selectList(null);
        return salaries;
    }

    /**
     *      更新工资账套
     *          1.根据id判断用户是否存在
     *          2.更新账套
     *                 更新账套名：
     *                      更新的账套名是否已经存在
     *                          存在判断账套名是否是自己的
     *                          账套名是别的账套的
     *                       更新的账套名不存在
     *                          直接更新
     *                 更新其他数据：直接更新
     *          3.执行操作
     * @param salary
     */
    @Override
    public void  updateSalary(Salary salary) {
        //根据id获取账套
        Salary dbSalary = salaryMapper.selectById(salary.getId());
        //判断账套是否存在
        AssertUtil.isTrue(dbSalary==null,"待修改账套不存在");
        //根据账套名获取账套
        Salary salary1 = salaryMapper.selectByName(salary.getName());
        //账套名不存在，或者账套名存在但是为自己的账套名
        AssertUtil.isTrue(salary1!=null&&(salary1.getId()!=salary.getId()),"账套已存在");
        //判断比率是否合理
        AssertUtil.isTrue(ratio(salary),"比率不合理");
        //执行操作
        AssertUtil.isTrue(salaryMapper.updateById(salary)<1,"账套更新失败");
    }

    /**
     * 删除账套
     *      1.判断id是否为空
     *      2.根据id查询数据库是否存在工资账套
     *      3.执行操作
     * @param id  工资账套id  salary.id
     */

    @Override
    public void  deleteSalary(Integer id) {
        //id非空校验
        AssertUtil.isTrue(id==null,"待删除账套不存在");
        //根据id查询数据库账套
        Salary dbSalary = salaryMapper.selectById(id);
        //判断账套是否存在
        AssertUtil.isTrue(dbSalary==null,"待删除账套不存在");
        //执行操作
        AssertUtil.isTrue(salaryMapper.deleteById(id)<1,"账套删除失败");

    }

    /**
     * 账套添加
     *      1.判断账套名是否已经存在
     *      2.设置默认值
     *          createDate
     *      3.角色添加操作
     * @param salary
     */
    @Override
    public void  addSalary(Salary salary) {
        //根据id查询数据库账套
        String name = salary.getName();
        System.out.println(name);
        Salary dbSalary = salaryMapper.selectByName(salary.getName());
        System.out.println(dbSalary);
        //判断查询的账套是否为空
        AssertUtil.isTrue(dbSalary!=null,"账套已存在");
        //判断比率是否合理
        AssertUtil.isTrue(ratio(salary),"比率不合理");
        //设置默认值
        salary.setCreateDate(LocalDateTime.now());
        //执行操作
        AssertUtil.isTrue(salaryMapper.insert(salary)<1,"工资账套添加失败");

    }
    /**
     * 封装比率判断方法
     * 判断比率是否合理
     */
    public Boolean ratio(Salary salary){
        boolean flag=false;
        if (salary.getPensionPer()>1||salary.getMedicalPer()>1||salary.getAccumulationFundPer()>1){
            flag=true;
        }
        return flag;
    }
}
