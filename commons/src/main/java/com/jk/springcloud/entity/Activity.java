package com.jk.springcloud.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * @program: activity
 * @description:
 * @author: JanKin
 * @create: 2022-10-25 16:57
 */
@Data
@TableName("activity")
public class Activity {
    @TableId(type = IdType.AUTO)
    @Id
    private int id;
    private String name;
    private String imgs;
    private String introduction;
    private String details;
    private int categoryId;
    private String address;
    private int peopleNum;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int visitors;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    private int createUser;
//    活动结束与否
    private Boolean isActive;
//    活动删除与否
    private Boolean isAlive;
//    加入活动是否要审核
    private Boolean examine;
}
