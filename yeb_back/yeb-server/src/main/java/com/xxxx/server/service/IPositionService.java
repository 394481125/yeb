package com.xxxx.server.service;

import com.xxxx.server.pojo.Position;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lwf
 * @since 2020-12-20
 */
public interface IPositionService extends IService<Position> {

    Position selectByName(String name);
}
