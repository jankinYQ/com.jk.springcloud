package com.jk.springcloud.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;

/**
 * @program: activity
 * @description: 做缓存同步时需要做一些改进，改进后的封装类可通用
 * @author: JanKin
 * @create: 2022-10-25 16:57
 */
@Data
@TableName("activity")
public class Activity2 implements Serializable {
    @Id
    private Integer id;
    private String name;
    private String imgs;
    private String introduction;
    private String details;
    @Column(name = "category_id")
    private Integer categoryId;
    private String address;
    @Column(name = "people_num")
    private Integer peopleNum;
    @Column(name = "start_time")
    private Date startTime;
    @Column(name = "end_time")
    private Date endTime;
    private String visitors;
    @TableField(fill = FieldFill.INSERT)
    @Column(name = "create_time")
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Column(name = "update_time")
    private Date updateTime;
    @Column(name = "create_user")
    private Integer createUser;
//    活动结束与否
    @Column(name = "is_active")
    private Boolean isActive;
//    活动删除与否
    @Column(name = "is_alive")
    private Boolean isAlive;
//    加入活动是否要审核
    private Boolean examine;
}
