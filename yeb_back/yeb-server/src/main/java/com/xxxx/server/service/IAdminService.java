package com.xxxx.server.service;

import com.xxxx.server.pojo.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxxx.server.pojo.RespBean;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ppl
 * @since 2020-12-19
 */
public interface IAdminService extends IService<Admin> {


    /**
     * 根据用户名获取用户
     * @param username
     */
    Admin getAdminByUserName(String username);

    /**
     * 登录返回token
     * @param username
     * @param password
     * @param code
     * @param request
     * @return
     */
    RespBean login(String username, String password, String code, HttpServletRequest request);

    /**
     * 删除管理员
     * @param id
     * @return
     */
    RespBean deleteAdminById(Integer id);

    /**
     * 查询管理员
     * @return
     */
    List<Admin> selectAdminAll(String name,String userName);

    /**
     * 更新管理员是否可用
     * @param admin
     * @return
     */
    RespBean updateAdminEnable(Admin admin);

    /**
     * 更新管理员的角色
     * @param adminId
     * @param rids
     * @return
     */
    RespBean updateAdminRole(Integer adminId, String rids);

    /**
     * 修改信息
     * @param admin
     * @return
     */
    Integer updateByAdmin(Admin admin);
    /**
     * 修改密码
     */
    Integer updatePassword(Integer id,String newPass);
}
