package com.xxxx.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxxx.server.exception.ParamsException;
import com.xxxx.server.mapper.*;
import com.xxxx.server.pojo.Employee;
import com.xxxx.server.pojo.RespBean;
import com.xxxx.server.service.IEmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxxx.server.vo.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;



    public RespPageBean
    pageQueryPlus(EmployeePage page){
        Page<EmpInfo> empInfoIPage = new Page<>(page.getCurrentPage(), page.getSize());

        IPage<EmpInfo> empInfoPage = employeeMapper.selectPageEmployee(empInfoIPage, page);
        RespPageBean response = new RespPageBean(empInfoPage.getTotal(), empInfoPage.getRecords());
        return response;
    }



    @Override
    public RespBean maxWorkId() {
       QueryWrapper<Employee> wrapper=new QueryWrapper<>();
        Employee workID = employeeMapper.selectOne(wrapper.orderByDesc("workID").last("limit 1"));
        return RespBean.success("最大工号", workID.getWorkID());
    }

    @Override
    public RespPageBean getEmployeeWithSalary(Integer currentPage, Integer size) {
        Page<Employee>page=new Page<>(currentPage,size);
        IPage<Employee>employeeIPage = employeeMapper.getEmployeeWithSalary(page);
        RespPageBean respPageBean = new RespPageBean(employeeIPage.getTotal(),employeeIPage.getRecords());
        return respPageBean;
    }

    

    @Override
    @Transactional
    public RespBean updateEmployeeSalary(Integer eid, Integer sid) {
        RespBean respBean = new RespBean();
        if (employeeMapper.updateEmployeeSalary(eid,sid)<1){
            respBean.setCode(500);
            respBean.setMessage("修改失败");
        }
        return respBean;
    }


    @Transactional(readOnly = false,rollbackFor = Exception.class)
    @Override
    public boolean batchImport(String fileName, MultipartFile file) throws Exception {

        boolean notNull = false;
        List<Employee> userList = new ArrayList<>();
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            throw new ParamsException("上传文件格式不正确");
        }
        boolean isExcel2003 = true;
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            isExcel2003 = false;
        }
        InputStream is = file.getInputStream();
        Workbook wb = null;
        if (isExcel2003) {
            wb = new HSSFWorkbook(is);
        } else {
            wb = new XSSFWorkbook(is);
        }
        Sheet sheet = wb.getSheetAt(0);
        if(sheet!=null){
            notNull = true;
        }
        Employee user;
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null||row.getCell(1)==null){
                continue;
            }
            user = new Employee();
            //String name = row.getCell(0).getStringCellValue();
            if(user.getId()!=null)
             user.setId( Integer.valueOf(row.getCell(0).getStringCellValue()));
            user.setName(row.getCell(1).getStringCellValue());
            user.setGender(row.getCell(2).getStringCellValue());
            String value = row.getCell(3).getStringCellValue();
            String[] dates=value.trim().split("-");
            if(value.equals("birthday")){
                continue;
            }
            LocalDate of = LocalDate.of(Integer.valueOf(dates[0]),Integer.valueOf(dates[1]),Integer.valueOf(dates[2]));
            user.setBirthday(of);
            user.setIdCard(row.getCell(4).getStringCellValue());
            user.setWedlock(row.getCell(5).getStringCellValue());
            user.setNationId((int) row.getCell(6).getNumericCellValue());
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println(user);
            userList.add(user);
        }
        for (Employee userResord : userList) {
            QueryWrapper<Employee> wrapper=new QueryWrapper<>();
            if(userResord.getId()!=null){
                wrapper.eq("id", userResord.getId());
              employeeMapper.update(userResord,wrapper) ;
            }else {
                employeeMapper.insert(userResord);
            }

        }
        return notNull;
    }

    @Override
    public List<EmpInfo> gteAllEmployee() {
        return employeeMapper.getAllEmployee();
    }

    /**
     * 根据身份证id查询员工
     */
    public  Employee selectByIdCard(String idCard){
        return employeeMapper.selectByIdCard(idCard);
    }
    /**
     * 通过id查找员工所有信息
     * @param id
     * @return
     */
    public EmpInfo queryEmpInfoById(Integer id) {
        return  employeeMapper.queryEmpInfoById(id);
    }
}
