package com.jk.springcloud.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: activity
 * @description:
 * @author: JanKin
 * @create: 2022-10-25 16:59
 */
@Data
public class Task {
    private int id;
    private int activityId;
    private String name;
    private String introduction;
    private int taskType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isActive;
    private int createUser;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
