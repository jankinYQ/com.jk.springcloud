package com.jk.springcloud.dto;

import com.jk.springcloud.entity.Files;
import lombok.Data;

/**
 * @program: activity
 * @description:
 * @author: JanKin
 * @create: 2022-10-25 16:57
 */
@Data
public class FileDto extends Files {
//    文件创建者昵称
    private String nickName;
//    文件创建者头像
    private String headPortrait;

//    文件创建者 在活动中的角色
    private int roleId;
}
