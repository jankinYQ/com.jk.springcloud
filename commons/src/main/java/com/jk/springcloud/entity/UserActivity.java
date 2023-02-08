package com.jk.springcloud.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: activity
 * @description:
 * @author: JanKin
 * @create: 2022-10-25 16:57
 */
@Data
public class UserActivity {
    private int id;
    private int activityId;
    private int userId;
    private int roleId;
    private String nickName;
    private String headPortrait;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
