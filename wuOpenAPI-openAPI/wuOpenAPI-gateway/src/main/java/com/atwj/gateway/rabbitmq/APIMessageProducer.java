package com.atwj.gateway.rabbitmq;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.atwj.apicommon.constant.RabbitmqConstant.EXCHANGE_INTERFACE_CONSISTENT;
import static com.atwj.apicommon.constant.RabbitmqConstant.ROUTING_KEY_INTERFACE_CONSISTENT;

@Component
public class APIMessageProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     *
     * @param message 发送的消息
     */
    public void sendMessage(Object message) {
        rabbitTemplate.convertAndSend(EXCHANGE_INTERFACE_CONSISTENT, ROUTING_KEY_INTERFACE_CONSISTENT, message);
    }
}
