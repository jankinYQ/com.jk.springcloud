package com.jk.springcloud.clients.fallback;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jk.springcloud.clients.ActivityTaskClient;
import com.jk.springcloud.common.R;
import com.jk.springcloud.entity.Activity;
import com.jk.springcloud.entity.Task;
import com.jk.springcloud.entity.UserActivity;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.jk.springcloud
 * @description: 微服务开启sentinel降级熔断，降级熔断执行的回调函数
 * @author: JanKin
 * @create: 2022-12-06 12:06
 */
@Slf4j
public class ActivityClientFallbackFactory implements FallbackFactory<ActivityTaskClient> {
    @Override
    public ActivityTaskClient create(Throwable throwable) {
        return new ActivityTaskClient() {
            @Override
            public R<Activity> getAct(int id) {
                log.error("activity服务异常",throwable);
                return R.error("Activity服务异常");
            }

            @Override
            public R<UserActivity> getByUserActId(int userId, int actId) {
                log.error("userActivity服务异常",throwable);
                return R.error("userActivity服务异常");
            }


            @Override
            public R<Task> getTask(int id) {
                log.error("task服务异常",throwable);
                return R.error("task服务异常");
            }
        };
    }

}
