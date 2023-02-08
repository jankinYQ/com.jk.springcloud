package com.jk.springcloud.config;

import com.esotericsoftware.minlog.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * @program: com.jk.springcloud
 * @description: 每个rabbit template只能装配一个return callback，而spring容器中只有一个rabbit template，所有这里要注入callback
 * @author: JanKin
 * @create: 2023-01-20 23:47
 */
@Slf4j
@Configuration
public class RabbitMQConfig implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //获取rabbit template对象
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        //装配returnCallback
        rabbitTemplate.setReturnCallback((message,replyCode,replyText,exchange,routingKey)->{
            //记录日志
            log.error("消息发送到队列失败，响应码：{}，失败原因；{}，交换机：{}，路由key：{}，消息：{}",
                    replyCode,replyText,exchange,routingKey,message.toString());
        });

    }
}
