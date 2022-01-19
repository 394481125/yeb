package com.xxxx.server.config;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xxxx.server.pojo.MailConstants;
import com.xxxx.server.pojo.MailLog;
import com.xxxx.server.service.IMailLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Desc  RabbitMQ配置类
 * @Author lsh
 * @Create Time 2020/12/22 20:25
 */
@Component
public class RabbitMQConfig {
    private static final Logger LOGGER= LoggerFactory.getLogger(RabbitMQConfig.class);

    @Autowired
    private CachingConnectionFactory cachingConnectionFactory;

    @Autowired
    private IMailLogService mailLogService;

    @Bean
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 消息确认回调
             * @param data 消息唯一标识
             * @param ack  确认结果
             * @param cause  失败原因
             */
            @Override
            public void confirm(CorrelationData data, boolean ack, String cause) {
                String msgId = data.getId();
                if (ack){
                    LOGGER.info("{}======>消息发送成功",msgId);
                    mailLogService.update(new UpdateWrapper<MailLog>()
                            .set("status",MailConstants.SUCCESS)
                            .eq("msgId",msgId));
                }else{
                    LOGGER.error("{}======>消息发送失败",msgId);
                }
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 消息失败回调
             * @param message  消息主题
             * @param replyCode  响应码
             * @param replyText  响应描述
             * @param exchange  交换机
             * @param routingKey  路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                LOGGER.error("{}======>消息发送queue时失败",message.getBody());
            }
        });

        return rabbitTemplate;
    }


    @Bean
    public Queue queue(){
        return new Queue(MailConstants.MAIL_QUEUE_NAME);
    }
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(MailConstants.MAIL_EXCHANGE_NAME);
    }
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(directExchange()).with(MailConstants.MAIL_ROUTING_KEY_NAME);
    }
}
