package com.jk.springcloud.clients.fallback;

import com.jk.springcloud.clients.UserClient;
import com.jk.springcloud.common.R;
import com.jk.springcloud.entity.User;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.jk.springcloud
 * @description: 微服务开启sentinel降级熔断，降级熔断执行的回调函数
 * @author: JanKin
 * @create: 2022-12-06 12:06
 */
@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable throwable) {
        return new UserClient() {
            @Override
            public R<User> getById(int id) {
                log.error("user服务异常",throwable);
                return R.error("user服务异常");
            }

            @Override
            public void sendMail(String email, String message) {
                log.error("user-sendMail服务异常",throwable);
            }

            @Override
            public R<String> updateById(User user) {
                log.error("user-update服务异常",throwable);
                return R.error("user服务异常.");
            }
        };
    }
}
