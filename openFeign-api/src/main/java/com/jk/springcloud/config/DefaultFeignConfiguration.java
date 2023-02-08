package com.jk.springcloud.config;

import com.jk.springcloud.clients.fallback.UserClientFallbackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @program: com.jk.springcloud
 * @description: 降级熔断，bean声明
 * @author: JanKin
 * @create: 2022-12-06 15:48
 */
@Component
public class DefaultFeignConfiguration {
    @Bean
    public UserClientFallbackFactory userClientFallbackFactory(){
        return new UserClientFallbackFactory();
    }
}
