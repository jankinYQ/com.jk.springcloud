package com.jk.springcloud.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jk.springcloud.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @program: 221005springboot
 * @description:
 * @author: JanKin
 * @create: 2022-10-06 18:28
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    @Update("update activity set visitors=visitors+1 where id=#{id}")
    int autoincrementVi(int id);
}
