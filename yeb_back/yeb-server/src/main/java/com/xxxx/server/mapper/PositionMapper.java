package com.xxxx.server.mapper;

import com.xxxx.server.pojo.Position;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lwf
 * @since 2020-12-20
 */
public interface PositionMapper extends BaseMapper<Position> {

    Position selectByName(String name);
}
