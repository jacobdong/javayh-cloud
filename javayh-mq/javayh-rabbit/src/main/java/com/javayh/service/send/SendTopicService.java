package com.javayh.service.send;

import com.javayh.util.LogOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.javayh.util.StaticNumber.JAVAYOHO_TOPIC;
import static com.javayh.util.StaticNumber.TOPIC_EXCHANGE;
import static com.javayh.util.StaticNumber.YHJ_TOPIC;

/**
 * @author Dylan Yang
 * @Description: 主题实现
 * @Title: SendTopicService
 * @ProjectName javayh-cloud
 * @date 2019/7/20 16:54
 */
@Slf4j
@Component
public class SendTopicService implements SendService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendMessage(Object message) {
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE,JAVAYOHO_TOPIC,message);
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE,YHJ_TOPIC,message);
        log.info("send topic success -> {}",message);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean isSendSuccess, String arg) {
        LogOut.logConfirm(correlationData,isSendSuccess,arg);
    }

    /**
     * 消费失败回调
     * @param message       消息主体
     * @param code          主体
     * @param txt           描述
     * @param exchange      使用的交换器
     * @param key           路由建
     */
    @Override
    public void returnedMessage(Message message, int code, String txt, String exchange, String key) {
        LogOut.logReturnedMessage(message,code,txt,exchange,key);
    }

    @Override
    public void afterPropertiesSet() {
        rabbitTemplate.setConfirmCallback(this::confirm);
        rabbitTemplate.setReturnCallback(this::returnedMessage);
    }
}

