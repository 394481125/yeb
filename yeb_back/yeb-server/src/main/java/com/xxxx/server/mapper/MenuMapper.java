package com.xxxx.server.mapper;

import com.xxxx.server.pojo.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ppl
 * @since 2020-12-19
 */
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 查询该用户的menu
     * @param id
     * @return
     */
    List<Menu> getMenusByAdminId(Integer id);

    /**
     * 所有权限
     * @return
     */
    List<Menu> getAllMenus();
}
