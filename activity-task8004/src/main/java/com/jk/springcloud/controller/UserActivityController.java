package com.jk.springcloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.jk.springcloud.common.R;
import com.jk.springcloud.entity.UserActivity;
import com.jk.springcloud.entity.UserTask;
import com.jk.springcloud.service.UserActivityService;
import com.jk.springcloud.service.UserTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/userActivity")
@CrossOrigin
@Slf4j
public class UserActivityController {

    @Autowired
    private UserActivityService userActivityService;

    /**
     * getById
     * @param
     * @return
     */
    @GetMapping("/{userId}/{actId}")
    public R<UserActivity> getByUserActId(@PathVariable int userId, @PathVariable int actId){
        LambdaQueryWrapper<UserActivity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserActivity::getUserId,userId);
        queryWrapper.eq(UserActivity::getActivityId,actId);

        UserActivity userActivity = userActivityService.getOne(queryWrapper);

        return R.success(userActivity);
    }

    /**
     * 某活任务的所有参与者完成者
     * @param
     * @param
     * @return
     */
    @GetMapping("/{actId}")
    public R<List<UserActivity>> getAll(@PathVariable int actId){

        LambdaQueryWrapper<UserActivity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserActivity::getActivityId,actId);

        List<UserActivity> userActivities = userActivityService.list(queryWrapper);

        return R.success(userActivities);
    }

    /**
     *新增UserActivity
     * @param
     * @return
     */
    @PostMapping
    public R saveUserAct(@RequestBody UserActivity userActivity){

        userActivityService.save(userActivity);

        return R.success("新增任务成功");
    }

    /**
     *更改UserActivity
     * @param
     * @return
     */
    @PutMapping
    public R updateAct(@RequestBody UserActivity userActivity){

        userActivityService.updateById(userActivity);

        return R.success("更新成功");
    }



    /**
     * 某人退出活动，要删除个人UserActivity 和 个人在活动中的 UserTask
     * 根据UserActivity id删除
     * @ param
     * @return
     */
    @Autowired
    private UserTaskService userTaskService;

    @DeleteMapping
    public R delete(@RequestBody UserActivity userActivity){
System.out.println(userActivity);
        userActivityService.removeById(userActivity.getId());

        //清理当前任务对应的userTask
        LambdaQueryWrapper<UserTask> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(UserTask::getUserId,userActivity.getUserId());
        queryWrapper.eq(UserTask::getActivityId,userActivity.getActivityId());
        userTaskService.remove(queryWrapper);

        return R.success("删除成功");
    }

}
