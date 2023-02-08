package com.jk.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @program: com.jk.springcloud
 * @description:
 * @author: JanKin
 * @create: 2022-12-02 22:51
 */
//@SpringBootApplication(exclude = {GlobalTransactionAutoConfiguration.class})
@SpringBootApplication
@EnableFeignClients
public class FileMain8001 {
    public static void main(String[] args){
        SpringApplication.run(FileMain8001.class);
    }
}
