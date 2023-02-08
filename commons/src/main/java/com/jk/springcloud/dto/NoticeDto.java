package com.jk.springcloud.dto;

import com.jk.springcloud.entity.Notice;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: activity
 * @description:
 * @author: JanKin
 * @create: 2022-10-25 16:57
 */
@Data
public class NoticeDto extends Notice {
//    消息发送者昵称
    private String nickName;
//    消息发送者头像
    private String headPortrait;

//通知类型 0系统通知，1普通通知，2申请通知, 3活动消息
    private String typeName;

//    申请通知
//    活动名
    private String actName;
    private LocalDateTime endTime;
//    任务提醒督促通知
//    任务名
    private String taskName;

}
