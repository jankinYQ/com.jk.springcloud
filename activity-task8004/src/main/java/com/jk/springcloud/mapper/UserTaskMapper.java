package com.jk.springcloud.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jk.springcloud.entity.UserTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: 221005springboot
 * @description:
 * @author: JanKin
 * @create: 2022-10-06 18:28
 */
@Mapper
public interface UserTaskMapper extends BaseMapper<UserTask> {
}
