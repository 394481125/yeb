package com.xxxx.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Desc
 * @Author lsh
 * @Create Time 2020/12/21 14:56
 */
public interface SysMsgMapper extends BaseMapper {
    Integer deleteSysMagByAdminId(Integer mid);
}