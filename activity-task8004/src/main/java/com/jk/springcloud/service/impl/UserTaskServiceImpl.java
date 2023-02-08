package com.jk.springcloud.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jk.springcloud.entity.UserTask;
import com.jk.springcloud.mapper.UserTaskMapper;
import com.jk.springcloud.service.UserTaskService;
import org.springframework.stereotype.Service;

@Service
public class UserTaskServiceImpl extends ServiceImpl<UserTaskMapper, UserTask> implements UserTaskService {
}
