package com.jk.springcloud;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import io.lettuce.core.ReadFrom;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * @program: com.jk.springcloud
 * @description:
 * @author: JanKin
 * @create: 2022-12-02 22:51
 */
//@SpringBootApplication(exclude = {GlobalTransactionAutoConfiguration.class})
@SpringBootApplication
@EnableCreateCacheAnnotation
@EnableFeignClients
public class UserMain8002 {
    public static void main(String[] args){

        SpringApplication.run(UserMain8002.class);

    }


//    -----------------------------redis 哨兵模式测试代码-------------------------------------------
//redis 底层用的是lettuce
//指定读取模式，指定为优先从slave节点读取，所以slave都不可用时才读取master

    @Bean
    public LettuceClientConfigurationBuilderCustomizer configurationBuilderCustomizer(){
        return clientConfigurationBuilder -> clientConfigurationBuilder.readFrom(ReadFrom.REPLICA_PREFERRED);
    }

//    ---------------------------------------------------------------------------------



    //    方便MQ中对象Jason格式查看,rabbitmq消息队列的对象序列化
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
