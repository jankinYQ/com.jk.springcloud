package com.jk.springcloud;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * @program: com.jk.springcloud
 * @description:
 * @author: JanKin
 * @create: 2022-12-02 22:51
 */
@SpringBootApplication
@EnableCreateCacheAnnotation
@EnableFeignClients
public class NoticeMain8003 {
    public static void main(String[] args){

        SpringApplication.run(NoticeMain8003.class);

    }


//    方便MQ中对象Jason格式查看
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
