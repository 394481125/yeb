package com.xxxx.mail;

import com.rabbitmq.client.Channel;
import com.xxxx.server.pojo.Employee;
import com.xxxx.server.pojo.MailConstants;
import com.xxxx.server.vo.EmpInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;

/**
 * @Desc 消息接收者
 * @Author lsh
 * @Create Time 2020/12/22 9:51
 */
@Component
public class MailReceiver {
    private static final Logger LOGGER= LoggerFactory.getLogger(MailReceiver.class);

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private MailProperties mailProperties;
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @RabbitListener(queues = MailConstants.MAIL_QUEUE_NAME)
    public void handler(Message message, Channel channel){
        EmpInfo empInfo = ((EmpInfo) message.getPayload());
        MessageHeaders headers = message.getHeaders();
        //消息序号
        long tag = ((long) headers.get(AmqpHeaders.DELIVERY_TAG));
        String msgId = ((String) headers.get("spring_returned_message_correlation"));
        HashOperations hashOperations = redisTemplate.opsForHash();

        try {
            if (hashOperations.entries("mail_log").containsKey(msgId)){
                LOGGER.error("消息已经被消费==========>{}",msgId);
                //手动确认
                channel.basicAck(tag,false);
                return;
            }
            MimeMessage msg=javaMailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(msg);
            helper.setFrom(mailProperties.getUsername());
            helper.setTo(empInfo.getEmail());
            helper.setSubject("入职欢迎邮件");
            helper.setSentDate(new Date());
            //邮件内容
            Context context=new Context();
            context.setVariable("name",empInfo.getName());
            context.setVariable("departmentName",empInfo.getDepartment().getName());
            context.setVariable("jobName",empInfo.getJoblevel().getName());
            context.setVariable("nationName",empInfo.getNation().getName());

            String mail = templateEngine.process("mail", context);
            helper.setText(mail,true);
            javaMailSender.send(msg);
            LOGGER.info("邮件发送成功");
            //消息id存入redis中
            hashOperations.put("mail_log",msgId,"ok");
            //手动确认消息
            channel.basicAck(tag,false);
        } catch (Exception e) {
            try {
                /**
                 * 手动确认
                 * requeue 是否重回队列
                 */
                channel.basicNack(tag,false,true);
            } catch (IOException ex) {
                LOGGER.info("邮件发送失败！！"+ex.getMessage());
            }
            LOGGER.info("邮件发送失败！！"+e.getMessage());

        }

    }

}
