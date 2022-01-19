package com.xxxx.server.mapper;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xxxx.server.pojo.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ppl
 * @since 2020-12-19
 */
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 通过id删除管理员
     * @param id
     * @return
     */
    Integer deleteAdminById(Integer id);

    /**
     * 查询全部管理员及角色
     * @return
     */
    List<Admin> selectAdminAll(@Param("name") String name,@Param("userName") String userName);

    /**
     * 修改管理员状态
     * @param id
     * @param enabled
     * @return
     */
    Integer updateAdminEnable(@Param("id")Integer id,@Param("enabled") Boolean enabled);

    /**
     * 删除管理员角色
     * @param adminId
     * @return
     */
    Integer deleteAdminRolesByAdminId(Integer adminId);

    Integer addAdminRole(@Param("adminId") Integer adminId,@Param("rid") Integer rid);

    Integer updateByAdmin(Admin admin);

    Integer updatePassword(@Param("id") Integer id, @Param("newPass") String newPass);
}
