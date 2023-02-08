package com.jk.springcloud.dto;

import com.jk.springcloud.entity.UserTask;
import lombok.Data;

/**
 * @program: activity
 * @description:
 * @author: JanKin
 * @create: 2022-10-25 16:57
 */
@Data
public class UserTaskDto extends UserTask {
//    在活动中的昵称和头像
    private String nickName;
    private String headPortrait;
}
