package com.jk.springcloud.config;

import com.jk.springcloud.common.R;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: com.jk.springcloud
 * @description: 消费者失败消息处理策略之republishMessageRecoverer
 * @author: JanKin
 * @create: 2023-01-23 13:27
 */
@Configuration
public class RabbitMQErrorMessageConfig {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "error.queue"),
            exchange = @Exchange(name = "error.direct", type = ExchangeTypes.DIRECT),
            key = {"error"}
    ))
    public void listenDirectQueue(R mail){

        System.out.println("正在处理投递失败的消息：【" + mail + "】");
    }

    @Bean
    public MessageRecoverer republishMessageRecoverer(RabbitTemplate rabbitTemplate){
        return new RepublishMessageRecoverer(rabbitTemplate,"error.direct","error");
    }

}
