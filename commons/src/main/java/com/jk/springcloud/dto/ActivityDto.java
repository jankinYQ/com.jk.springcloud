package com.jk.springcloud.dto;

import com.jk.springcloud.entity.Activity;
import lombok.Data;

/**
 * @program: activityDto
 * @description:
 * @author: JanKin
 * @create: 2022-10-25 16:57
 */
@Data
public class ActivityDto extends Activity {
//    活动发布者的昵称
    private String nickName;
//    活动发布者的头像
    private String headPortrait;

//    分类名
    private String categoryName;
}
