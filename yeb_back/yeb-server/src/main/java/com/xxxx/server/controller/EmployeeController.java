package com.xxxx.server.controller;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.xxxx.server.pojo.*;
import com.xxxx.server.service.*;
import com.xxxx.server.service.impl.EmployeeServiceImpl;
import com.xxxx.server.util.AssertUtil;
import com.xxxx.server.vo.EmpInfo;
import com.xxxx.server.vo.EmployeePage;
import com.xxxx.server.vo.RespPageBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lwf
 * @since 2020-12-20
 */
@RestController
@RequestMapping("/employee")
@Api("员工资料")
@Slf4j
public class EmployeeController {
    @Resource
    private EmployeeServiceImpl employeeService;
    @Resource
    private INationService nationService;
    @Resource
    private IDepartmentService departmentService;
    @Resource
    private IPositionService positionService;
    @Resource
    private IJoblevelService joblevelService;
    @Resource
    private IPoliticsStatusService politicsStatusService;

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private IMailLogService mailLogService;

    @ApiOperation("员工分页")
    @GetMapping("/basic/")
    public RespPageBean base(EmployeePage employeePage) throws ParseException {
       return employeeService.pageQueryPlus(employeePage);
   }

    /**
     *  员工添加
     *      1.根据添加的员工身份证号判断员工是否已经存在
     *      2.
     * @param employee
     * @return
     */
    @ApiOperation("员工添加")
    @PostMapping("/basic/")
    public RespBean insert(@RequestBody Employee employee) {
        //根据身份证查询员工
        Employee dbEmployee = employeeService.selectByIdCard(employee.getIdCard());
        //判断员工是否存在
        AssertUtil.isTrue(dbEmployee!=null,"该员工已存在");
        log.info("添加："+employee);
        boolean save = employeeService.save(employee);
        log.info("添加："+save);
        if (save){
            //消息落库
            MailLog mailLog = new MailLog();
            String msgId = UUID.randomUUID().toString();
            mailLog.setMsgId(msgId);
            mailLog.setEid(employee.getId());
            mailLog.setStatus(MailConstants.DELIVERING);
            mailLog.setTryTime(LocalDateTime.now().plusMinutes(MailConstants.MSG_TIMEOUT));
            mailLog.setCount(0);
            mailLog.setExchange(MailConstants.MAIL_EXCHANGE_NAME);
            mailLog.setRouteKey(MailConstants.MAIL_ROUTING_KEY_NAME);
            mailLog.setCreateTime(LocalDateTime.now());
            mailLog.setUpdateTime(LocalDateTime.now());
            AssertUtil.isTrue(!mailLogService.save(mailLog) ,"落库失败");;
            EmpInfo empInfo = employeeService.queryEmpInfoById(employee.getId());
            //发送rabbitmq信息
            rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME,MailConstants.MAIL_ROUTING_KEY_NAME
                    ,empInfo,new CorrelationData(msgId));
        }
        return RespBean.success("添加:"+save, null);
    }
    @ApiOperation("员工删除")
    @DeleteMapping("/basic/{id}")
    public RespBean delete(@PathVariable("id") Integer id) {
        log.info("删除："+id);
        boolean delte = employeeService.removeById(id);
        log.info("删除："+delte);
        return RespBean.success("删除:"+delte, null);
    }
    @ApiOperation("员工修改")
    @PutMapping("/basic/")
    public RespBean update(@RequestBody Employee employee) {
        log.info("更新："+employee);
        boolean delte = employeeService.updateById(employee);
        log.info("更新："+delte);
        return RespBean.success("更新:"+delte, null);
    }
   @ApiOperation("所有民族")
    @GetMapping("/basic/nations")
    public List<Nation> nations(){
        log.info("所有民族");
       List<Nation> list = nationService.list();
       return list;
   }

    @ApiOperation("所有部门")
    @GetMapping("/basic/deps")
    public List<Department> departments(){
        List<Department> list = departmentService.getAllDepartment();
        return list;
    }
    @GetMapping("/basic/positions")
    @ApiOperation("所有职位")
    public List<Position> positions(){
        return positionService.list();
    }
    @GetMapping("/basic/joblevels")
    @ApiOperation("工作等级")
    public List<Joblevel> joblevels(){
        return joblevelService.list();
    }
    @GetMapping("/basic/politicsstatus")
    @ApiOperation("所有政治面貌")
    public List<PoliticsStatus> politicsStatuses(){
        return politicsStatusService.list();
    }
    @GetMapping("/basic/maxWorkID")
    @ApiOperation("最大工号")
    public RespBean maxWorkID(){
        return employeeService.maxWorkId();
    }

    /**
     * 导出exc
     * @param response
     * @throws IOException
     */
    @ApiOperation("导出表格")
    @GetMapping(value = "/basic/export",produces = "application/octet-stream")
    @ResponseBody
    public void export(HttpServletResponse response) {
        List<EmpInfo> empInfos = employeeService.gteAllEmployee();
        ExportParams params = new ExportParams("员工表","员工表", ExcelType.HSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(params,EmpInfo.class,empInfos);
        ServletOutputStream outputStream=null;
        try {
            response.setHeader("content-type","application/octet-stream");
            response.setHeader("content-disposition","attachment;filename="+ URLEncoder.encode("员工表.xls","UTF-8"));

            outputStream = response.getOutputStream();
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @ApiOperation("导入表格")
    @PostMapping("/basic/import")
    public RespBean addUser(@RequestParam("file") MultipartFile file) {
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        try {
            List<Employee> list = ExcelImportUtil.importExcel(file.getInputStream(), EmpInfo.class, params);
            List<Department> departmentList = departmentService.list();
            list.forEach(employee -> {
                Integer hhh = list.get(departmentList.
                        indexOf(new Department(employee.getDepartment().getName()))).getId();
                employee.setDepartmentId(hhh);
            });
            if (employeeService.saveBatch(list)) {
                return RespBean.success("导入成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       return RespBean.error("导入失败");
    }
}
