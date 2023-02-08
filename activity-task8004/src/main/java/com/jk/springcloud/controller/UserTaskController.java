package com.jk.springcloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.jk.springcloud.common.R;
import com.jk.springcloud.dto.UserTaskDto;
import com.jk.springcloud.entity.Task;
import com.jk.springcloud.entity.UserActivity;
import com.jk.springcloud.entity.UserTask;
import com.jk.springcloud.service.TaskService;
import com.jk.springcloud.service.UserActivityService;
import com.jk.springcloud.service.UserTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/userTask")
@CrossOrigin
@Slf4j
public class UserTaskController {

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private UserActivityService userActivityService;

    @Autowired
    private TaskService taskService;
    /**
     * 通过用户ID和TaskId查UserTask
     * getById
     * @param
     * @return
     */
    @GetMapping("/{userId}/{taskId}")
    public R<UserTaskDto> getById(@PathVariable int userId, @PathVariable int taskId, HttpSession session){

        LambdaQueryWrapper<UserTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTask::getUserId,userId);
        queryWrapper.eq(UserTask::getTaskId,taskId);

        UserTask userTask = userTaskService.getOne(queryWrapper);

//-----------------------------   UserTaskDto ------------------------------------------------
        List<UserTask> userTasks = new ArrayList<>();
        userTasks.add(userTask);

        UserTaskDto userTaskDto = this.userTaskDto(userTasks).get(0);

        return R.success(userTaskDto);
    }

    /**
     * 某活任务TaskId的所有参与者完成者
     * @param
     * @param
     * @return
     */
    @GetMapping("/{taskId}")
    public R<List<UserTaskDto>> getAll(@PathVariable int taskId){

        LambdaQueryWrapper<UserTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTask::getTaskId,taskId);
        queryWrapper.orderByAsc(UserTask::getFinishTime);

        List<UserTask> userTasks = userTaskService.list(queryWrapper);

//-----------------------------   UserTaskDto ------------------------------------------------
        List<UserTaskDto> userTaskDtos = this.userTaskDto(userTasks);

        return R.success(userTaskDtos);
    }

    public List<UserTaskDto> userTaskDto(List<UserTask> userTasks){

//        想要用Stream流来操作集合，那么就需要将数组或者集合先转换成Stream流才可以操作
        List<UserTaskDto> list = userTasks.stream().map((item) -> {

            UserTaskDto userTaskDto = new UserTaskDto();
            BeanUtils.copyProperties(item,userTaskDto);

//            获取用户在活动中的昵称和头像
            LambdaQueryWrapper<UserActivity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserActivity::getUserId,item.getUserId());
            queryWrapper.eq(UserActivity::getActivityId,item.getActivityId());
            UserActivity userActivity = userActivityService.getOne(queryWrapper);

            if(userActivity != null) {
                userTaskDto.setNickName(userActivity.getNickName());
                userTaskDto.setHeadPortrait(userActivity.getHeadPortrait());
            }

            return userTaskDto;
        }).collect(Collectors.toList());

        return list;
    }

// 获取该活动下所有加入了分组任务的成员
    @GetMapping("/grouped/{actId}")
    public R<List<UserTaskDto>> getGrouped(@PathVariable int actId){
//      获取该活动所有分组任务id
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getActivityId,actId);
        queryWrapper.eq(Task::getTaskType,3);  //任务类型3是分组任务
        List<Task> tasks = taskService.list(queryWrapper);


        List<Integer> taskIdList = new ArrayList<>();
        for(Task item : tasks){
            taskIdList.add(item.getId());
        }
        LambdaQueryWrapper<UserTask> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(UserTask::getActivityId,actId);
        queryWrapper2.in(UserTask::getTaskId,taskIdList);
        List<UserTask> userTasks = userTaskService.list(queryWrapper2);

        List<UserTaskDto> userTaskDtos = this.userTaskDto(userTasks);

        return R.success(userTaskDtos);
    }

    /**
     *新增任务记录 用户任务
     * @param
     * @return
     */
    @PostMapping
    public R saveUserTask(@RequestBody UserTask userTask){
        userTask.setFinishTime(LocalDateTime.now());

//        如果已添加userTask则更新
        LambdaQueryWrapper<UserTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTask::getTaskId,userTask.getTaskId());
        queryWrapper.eq(UserTask::getUserId,userTask.getUserId());
        UserTask u = userTaskService.getOne(queryWrapper);

        if(u != null) {
            userTask.setId(u.getId());
            userTaskService.updateById(userTask);
        }
        else
            userTaskService.save(userTask);

        return R.success("更新用户任务成功");
    }

    /**
     * 加入分组,可批量
     * @param
     * @return
     */
    @PostMapping("/grouped")
    public R saveUserTasks(@RequestBody UserTask userTask){
System.out.println("grouped:"+userTask);

        userTaskService.save(userTask);

        return R.success("添加用户任务成功");
    }





    /**
     *更改用户任务设置finishTime为null
     * @param
     * @return
     */
    @PutMapping
    public R updateUserTask(@RequestBody UserTask userTask){

        userTaskService.updateById(userTask);

        return R.success("更新成功");
    }



    /**
     * 根据用户任务id删除
     * @ param
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestBody UserTask userTask){

        userTaskService.removeById(userTask.getId());

        return R.success("删除成功");
    }

}
