package com.jk.springcloud.dto;

import com.jk.springcloud.entity.Task;
import lombok.Data;

/**
 * @program: activity
 * @description:
 * @author: JanKin
 * @create: 2022-10-25 16:57
 */
@Data
public class TaskDto2 extends Task {
//    taskType 任务分类名
    private String typeName;

//任务发布者 在活动中的昵称头像
    private String nickName;
    private String headPortrait;

}
