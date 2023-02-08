package com.jk.springcloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.jk.springcloud.clients.ActivityTaskClient;
import com.jk.springcloud.clients.UserClient;
import com.jk.springcloud.common.R;
import com.jk.springcloud.dto.NoticeDto;
import com.jk.springcloud.entity.*;
import com.jk.springcloud.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notice")
@CrossOrigin
@Slf4j
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private ActivityService activityService;
//    @Autowired
//    private TaskService taskService;
//
//    @Autowired
//    private UserActivityService userActivityService;
//
    @Autowired
    private UserClient userClient;

    @Autowired
    private ActivityTaskClient activityTaskClient;

    /**
     *
     *获取某个用户收到的所有消息
     * @param
     * @return
     */
    @GetMapping("/getByUser/{userId}")
    public R<List<NoticeDto>> getByUserId(@PathVariable int userId, HttpSession session) {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper ->wrapper.eq(Notice::getToUser, userId).or().eq(Notice::getType, 0));
        queryWrapper.orderByDesc(Notice::getCreateTime);

        List<Notice> notices = noticeService.list(queryWrapper);

        //        想要用Stream流来操作集合，那么就需要将数组或者集合先转换成Stream流才可以操作
        List<NoticeDto> list = notices.stream().map((item) -> {
            NoticeDto noticeDto = new NoticeDto();
            BeanUtils.copyProperties(item,noticeDto);

            R<User> userR = userClient.getById(item.getFromUser());
            User user = userR.getData();

//            0系统通知，1普通通知，2申请通知, 3活动消息
            if(item.getType() == 0)
                noticeDto.setTypeName("系统通知");
            else if(item.getType() == 1)
                noticeDto.setTypeName("普通通知");
            else if(item.getType() == 2)
                noticeDto.setTypeName("申请通知");

//            发信人昵称头像
            noticeDto.setNickName(user.getName());
            noticeDto.setHeadPortrait(user.getHeadPortrait());

//            活动名
            if(item.getActivityId() > 0){


                R<Activity> activityR = activityTaskClient.getAct(item.getActivityId());
                Activity activity = activityR.getData();

                noticeDto.setActName(activity.getName());
                noticeDto.setEndTime(activity.getEndTime());
            }
//            任务名
            if(item.getTaskId() > 0){
                R<Task> taskR = activityTaskClient.getTask(item.getTaskId());
                Task task = taskR.getData();

                noticeDto.setTaskName(task.getName());
            }

            return noticeDto;
        }).collect(Collectors.toList());


        return R.success(list);
    }

    /**
     *
     *获取某个活动中的所有消息
     * @param
     * @return
     */
    @GetMapping("/getByAct/{actId}")
    public R<List<NoticeDto>> getByActivityId(@PathVariable int actId, HttpSession session) {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getActivityId,actId);
        queryWrapper.eq(Notice::getType,3);
        queryWrapper.orderByDesc(Notice::getCreateTime);

        List<Notice> notices = noticeService.list(queryWrapper);

        //        想要用Stream流来操作集合，那么就需要将数组或者集合先转换成Stream流才可以操作
        List<NoticeDto> list = notices.stream().map((item) -> {
            NoticeDto noticeDto = new NoticeDto();
            BeanUtils.copyProperties(item,noticeDto);

            R<UserActivity> userActivityR = activityTaskClient.getByUserActId(item.getFromUser(),item.getActivityId());
            UserActivity userActivity = userActivityR.getData();

            if(userActivity == null) {
                R<User> userR = userClient.getById(item.getFromUser());
                User user = userR.getData();

                noticeDto.setNickName(user.getName());
                noticeDto.setHeadPortrait(user.getHeadPortrait());
            }else{
                noticeDto.setNickName(userActivity.getNickName());
                noticeDto.setHeadPortrait(userActivity.getHeadPortrait());
            }

            return noticeDto;
        }).collect(Collectors.toList());

        return R.success(list);
    }


    /**
     * 添加消息
     *
     * @param
     * @return
     */
    @PostMapping
    public R saveFile(@RequestBody Notice notice) {
//       申请通知  每个活动已发过申请了就不再添加了
        if(notice.getType() == 2) {
            LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Notice::getActivityId, notice.getActivityId());
            queryWrapper.eq(Notice::getFromUser, notice.getFromUser());
            Notice notice1 = noticeService.getOne(queryWrapper);
            if(notice1 != null)
                return R.success("已申请过");
        }

        noticeService.save(notice);

        return R.success("发送成功");

    }

    /**
     * 删除消息
     * @ param
     * @return
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable int id){

        noticeService.removeById(id);

        return R.success("删除成功");
    }

    /**
     *  更新消息 以及消息confirm状态
     * @param
     * @return
     */
    @PutMapping
    public R updateAct(@RequestBody Notice notice){

        noticeService.updateById(notice);

        return R.success("更新成功");
    }

}