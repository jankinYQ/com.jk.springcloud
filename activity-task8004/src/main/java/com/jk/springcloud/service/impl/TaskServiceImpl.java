package com.jk.springcloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jk.springcloud.entity.Task;
import com.jk.springcloud.entity.UserTask;
import com.jk.springcloud.mapper.TaskMapper;
import com.jk.springcloud.service.TaskService;
import com.jk.springcloud.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    @Autowired
    UserTaskService userTaskService;

    public void delWithTaskUser(int taskId){
        //清理当前任务

        Boolean state1 = this.removeById(taskId);

        //清理当前任务对应的userTask
        LambdaQueryWrapper<UserTask> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(UserTask::getTaskId,taskId);

        Boolean state2 = userTaskService.remove(queryWrapper);

    }
}
