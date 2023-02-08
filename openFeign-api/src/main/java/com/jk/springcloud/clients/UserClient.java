package com.jk.springcloud.clients;

import com.jk.springcloud.clients.fallback.UserClientFallbackFactory;
import com.jk.springcloud.common.R;
import com.jk.springcloud.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @program: com.jk.springcloud
 * @description:
 * @author: JanKin
 * @create: 2022-12-04 21:03
 */

@FeignClient(value = "userservice",fallbackFactory = UserClientFallbackFactory.class)
public interface UserClient {

    @GetMapping("/user/{id}")
    R<User> getById(@PathVariable("id") int id);

    @GetMapping("/user/sendMail")
    void sendMail(@RequestParam("email") String email,
                  @RequestParam("message")  String message);


    @PutMapping("/user/update")
    R<String> updateById(@RequestBody User user);

    }
