package com.xxxx.server.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xxxx.server.pojo.MailConstants;
import com.xxxx.server.pojo.MailLog;
import com.xxxx.server.service.IMailLogService;
import com.xxxx.server.service.impl.EmployeeServiceImpl;
import com.xxxx.server.vo.EmpInfo;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Desc  邮件发送定时任务
 * @Author lsh
 * @Create Time 2020/12/22 21:00
 */
@Component
public class MailTask {
    @Autowired
    private IMailLogService mailLogService;
    @Autowired
    private EmployeeServiceImpl employeeService;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Scheduled(cron = "0/10 * * * * ?")
    public void mailTask(){
        List<MailLog> list = mailLogService.list(new QueryWrapper<MailLog>()
                .eq("status", MailConstants.DELIVERING)
                .lt("tryTime", LocalDateTime.now()));
        //遍历要去发的信息，并且重试的时间到了
        list.forEach(mailLog -> {
            String msgId = mailLog.getMsgId();
            //重试次数超过最大次数
            if (mailLog.getCount()>= MailConstants.MAX_TRY_COUNT) {
                mailLogService.update(new UpdateWrapper<MailLog>()
                        .eq("msgId",msgId)
                        .set("status",MailConstants.FAILURE)
                        .set("updateTime",LocalDateTime.now()));
            }
            mailLogService.update(new UpdateWrapper<MailLog>()
                    .eq("msgId",msgId)
                    .set("count",mailLog.getCount()+1)
                    .set("updateTime",LocalDateTime.now())
                    .set("tryTime",LocalDateTime.now().plusMinutes(MailConstants.MSG_TIMEOUT)));
            EmpInfo empInfo = employeeService.queryEmpInfoById(mailLog.getEid());
            //发送信息
            rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME,MailConstants.MAIL_ROUTING_KEY_NAME
                    ,empInfo,new CorrelationData(msgId));
        });
    }
}
