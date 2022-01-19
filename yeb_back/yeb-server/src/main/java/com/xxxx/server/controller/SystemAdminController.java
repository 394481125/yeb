package com.xxxx.server.controller;

import com.xxxx.server.pojo.Admin;
import com.xxxx.server.pojo.RespBean;
import com.xxxx.server.pojo.Role;
import com.xxxx.server.service.IAdminService;
import com.xxxx.server.service.IRoleService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * @Desc
 * @Author lsh
 * @Create Time 2020/12/21 9:09
 */

@RequestMapping("/system/admin")
@RestController
public class SystemAdminController {
    @Autowired
    private IAdminService adminService;
    @Autowired
    private IRoleService iRoleService;

    @ApiOperation(value = "获取所有管理员或通过名称查")
    @GetMapping("/")
    public List<Admin> getAdmins(String keywords, Principal principal){
        String userName = principal.getName();
        //查询除了本身的所有管理员
        List<Admin> admins = adminService.selectAdminAll(keywords,userName);

        return admins;
    }

    @ApiOperation(value = "删除管理员")
    @DeleteMapping("/{id}")
    public RespBean deleteAdmin(@PathVariable("id") Integer id){
        return adminService.deleteAdminById(id);
    }
    @PutMapping("/")
    public RespBean updateAdminEnable(@RequestBody Admin admin){
        return adminService.updateAdminEnable(admin);
    }

    @ApiOperation(value = "获取全部角色")
    @GetMapping("/roles")
    public List<Role> getRoles(){
        return iRoleService.list();
    }

    @ApiOperation(value = "更新管理员角色")
    @PutMapping("/role")
    public RespBean updateAdminRole(Integer adminId,String rids){

        return adminService.updateAdminRole(adminId,rids);
    }

}
