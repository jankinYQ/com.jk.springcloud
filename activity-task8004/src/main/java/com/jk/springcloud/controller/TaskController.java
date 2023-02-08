package com.jk.springcloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.jk.springcloud.clients.UserClient;
import com.jk.springcloud.common.R;
import com.jk.springcloud.dto.TaskDto;
import com.jk.springcloud.dto.TaskDto2;
import com.jk.springcloud.entity.*;
import com.jk.springcloud.mapper.TaskMapper;
import com.jk.springcloud.mapper.TaskTypeMapper;
import com.jk.springcloud.service.ActivityService;
import com.jk.springcloud.service.UserActivityService;
import com.jk.springcloud.service.impl.TaskServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/task")
@CrossOrigin
@Slf4j
public class TaskController {

    @Autowired
    private TaskServiceImpl taskService;

    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserActivityService userActivityService;

//    @Autowired
//    private UserServiceImpl userService;
    @Autowired
    private UserClient userClient;

    @Resource
    private TaskMapper taskMapper;
    /**
     * getById
     * @param
     * @return
     */
    @GetMapping("/{id}")
    public R<TaskDto2> getById(@PathVariable int id, HttpSession session){

        Task task = taskService.getById(id);
        TaskDto2 taskDto2 = new TaskDto2();
        BeanUtils.copyProperties(task,taskDto2);

//        获取发布者在活动中的昵称头像，如果不存在则为活动创建者，直接获取创建者用户头像名字
        LambdaQueryWrapper<UserActivity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserActivity::getActivityId,task.getActivityId());
        queryWrapper.eq(UserActivity::getUserId,task.getCreateUser());
        UserActivity userActivity = userActivityService.getOne(queryWrapper);
        if(userActivity != null ) {
            taskDto2.setNickName(userActivity.getNickName());
            taskDto2.setHeadPortrait(userActivity.getHeadPortrait());
        }else{
           R<User> userR = userClient.getById( task.getCreateUser() );
           User user = userR.getData();

           taskDto2.setNickName(user.getName());
           taskDto2.setHeadPortrait(user.getHeadPortrait());
        }

//        获取任务类型
        TaskType taskType = taskTypeMapper.selectById(task.getTaskType());
        taskDto2.setTypeName( taskType.getName());

        return R.success(taskDto2);
    }
    @GetMapping("/task/{id}")
    public R<Task> getTask(@PathVariable int id) {
        Task task = taskService.getById(id);

        return R.success(task);
    }

    /**
     * 某活动的所有任务
     * @param
     * @param
     * @return
     */
    @GetMapping("/getByActId/{actId}")
    public R<List<Task>> getByActId(@PathVariable int actId){

        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getActivityId,actId);

        List<Task> tasks = taskService.list(queryWrapper);

        return R.success(tasks);
    }

    /**
     *新增任务
     * @param
     * @return
     */
    @PostMapping
    public R saveTask(@RequestBody Task task){
        //紧急任务还要邮件通知
        if(task.getTaskType() == 1){
            LambdaQueryWrapper<UserActivity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserActivity::getActivityId,task.getActivityId());

            List<UserActivity> userActivities = userActivityService.list(queryWrapper);

            //   获取所有参与活动的用户
            List<UserActivity> list = userActivities.stream().map((item) -> {
                R<User> userR = userClient.getById(item.getUserId());
                User user = userR.getData();

                Activity activity = activityService.getById(item.getActivityId());

                String msg = "<h3>"+activity.getName()+"</h3>";
                msg = msg + "<br/>"+"任务名:"+task.getName();
                msg = msg + "<br/>"+"简介:"+task.getIntroduction();
                msg = msg + "<br/>"+"开始时间:"+task.getStartTime();
                msg = msg + "<br/>"+"结束时间:"+task.getEndTime();

                try {
                    userClient.sendMail(user.getEmail(),msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return item;
            }).collect(Collectors.toList());


        }




        taskService.save(task);

        return R.success("新增任务成功");
    }

    /**
     *更改任务
     * @param
     * @return
     */
    @PutMapping("/updateTask")
    public R updateTask(@RequestBody Task task){

        taskService.updateById(task);

        return R.success("更新成功");
    }



    /**
     * 根据任务id删除 同时删除taskId = id 的 userTask
     * @ param
     * @return
     */
    @DeleteMapping
    public R<String> delTask(@RequestBody Task task){

        taskService.delWithTaskUser(task.getId());

        return R.success("删除成功");
    }

//      taskDto
    @Resource
    private TaskTypeMapper taskTypeMapper;
    /**
     * 某活动的所有任务 - 并按照任务类型分类好
     * @param
     * @param
     * @return
     */
    @GetMapping("/getByTaskType/{actId}")
    public R<List<TaskDto>> getByTaskType(@PathVariable int actId){
//获取该活动创建了哪些任务类型的任务，group by taskType
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getActivityId,actId);
        queryWrapper.groupBy(Task::getTaskType);

        List<Task> types = taskService.list(queryWrapper);

//        想要用Stream流来操作集合，那么就需要将数组或者集合先转换成Stream流才可以操作
        List<TaskDto> list = types.stream().map((item) -> {

//           某活动中 某类任务taskType 下的 tasks
            LambdaQueryWrapper<Task> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Task::getActivityId,item.getActivityId());
            queryWrapper1.eq(Task::getTaskType,item.getTaskType());
            List<Task> tasks = taskService.list(queryWrapper1);

//           通过item.taskType 获取该类任务id的任务名name
            TaskType taskType = taskTypeMapper.selectById(item.getTaskType());

//封装 taskDto
            TaskDto taskDto = new TaskDto();
            taskDto.setTaskTypeId(item.getTaskType());
            taskDto.setTypeName(taskType.getName());
            taskDto.setTasks(tasks);

            return taskDto;
        }).collect(Collectors.toList());

        return R.success(list);
    }

}
