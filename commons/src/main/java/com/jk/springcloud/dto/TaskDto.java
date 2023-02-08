package com.jk.springcloud.dto;

import com.jk.springcloud.entity.Task;
import lombok.Data;

import java.util.List;

/**
 * @program: activity
 * @description:
 * @author: JanKin
 * @create: 2022-10-25 16:57
 */
@Data
public class TaskDto{
//    活动中按任务类型分类的任务集合
//    taskType
    private int taskTypeId;
    private String typeName;

    private List<Task> tasks;

}
