package com.xxxx.server.controller;

import com.xxxx.server.service.IAdminService;
import com.xxxx.server.vo.Message;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author lwf
 * @title: ChatWithRedis
 * @projectName yeb
 * @description:
 * @date 2020/12/2215:16
 */
/*
@Api("发消息，有效期一天")
@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatWithRedis {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private IAdminService adminService;
    @ApiOperation("发送信息,有效期默认一天")
    @PostMapping("/send")
    public Message sendMessage(@RequestBody Message message, Principal principal){
        message.setNotSelf(principal.getName());
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
        message.setTime(format.format(new Date()));
        //hash存  发起人 接收人  消息key
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();
        //有效一天，作为消息key
        LocalDate date=LocalDate.now();
        String msgKey=message.getNotSelf()+message.getTo()+ date.toString().trim();
        stringObjectObjectHashOperations.put(message.getNotSelf(), message.getTo(),msgKey);
        //存当天消息  消息key  消息列表     一天过期
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.leftPush(msgKey, message);
        redisTemplate.expire(msgKey, 1, TimeUnit.DAYS);
        return message;
    }
    @ApiOperation("取消息")
    @GetMapping("/get")
    public List<Message> getMessage(String username, Principal principal){
        //今天的消息key
        LocalDate date=LocalDate.now();
        String msgKey=username+principal.getName()+ date.toString().trim();
        log.info("用户"+principal.getName()+",使用"+msgKey+"取消息");
        //取当天消息
        ListOperations<String, Object> list = redisTemplate.opsForList();
        List<Message> msg=new ArrayList<>();
        list.range(msgKey,0,-1).forEach(o->{
            Message message = (Message)o ;
            message.setSelf(true);
            message.setUserFace(adminService.getAdminByUserName(message.getNotSelf()).getUserFace());
            msg.add(message);
            });
        String msgKey1=principal.getName()+username+ date.toString().trim();
        //取当天消息
        list.range(msgKey1,0,-1).forEach(o->{
            Message message = (Message)o ;
            message.setSelf(true);
            message.setUserFace(adminService.getAdminByUserName(message.getNotSelf()).getUserFace());
            msg.add(message);
        });
        Collections.sort(msg);
        return msg;
    }
}
*/
