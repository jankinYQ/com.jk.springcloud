package com.jk.springcloud.clients;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jk.springcloud.clients.fallback.UserClientFallbackFactory;
import com.jk.springcloud.common.R;
import com.jk.springcloud.dto.ActivityDto;
import com.jk.springcloud.entity.Activity;
import com.jk.springcloud.entity.Task;
import com.jk.springcloud.entity.User;
import com.jk.springcloud.entity.UserActivity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @program: com.jk.springcloud
 * @description:
 * @author: JanKin
 * @create: 2022-12-04 21:03
 */

@FeignClient(value = "activity-task-service")
public interface ActivityTaskClient {

    @GetMapping("/activity/act/{id}")
    R<Activity> getAct(@PathVariable("id") int id);


    @GetMapping("/userActivity/{userId}/{actId}")
    R<UserActivity> getByUserActId(@PathVariable("userId") int userId, @PathVariable("actId") int actId);




    @GetMapping("/task/{id}")
    R<Task> getTask(@PathVariable("id") int id);

}
