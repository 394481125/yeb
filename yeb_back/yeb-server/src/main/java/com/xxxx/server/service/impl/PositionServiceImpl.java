package com.xxxx.server.service.impl;

import com.xxxx.server.pojo.Position;
import com.xxxx.server.mapper.PositionMapper;
import com.xxxx.server.service.IPositionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lwf
 * @since 2020-12-20
 */
@Service
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position> implements IPositionService {
    @Resource
    private PositionMapper positionMapper;
    @Override
    public Position selectByName(String name) {
        return positionMapper.selectByName(name) ;
    }
}
