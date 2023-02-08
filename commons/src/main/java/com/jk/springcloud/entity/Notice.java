package com.jk.springcloud.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: activity
 * @description:
 * @author: JanKin
 * @create: 2022-11-19 17:33
 */
@Data
public class Notice {
    private int id;
    private int activityId;
    private int taskId;
    private int fromUser;
    private int toUser;
    private String message;
    private String img;
    private int type;//消息类型
    private Boolean confirm;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
