package com.jk.springcloud.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jk.springcloud.entity.UserActivity;
import com.jk.springcloud.mapper.UserActivityMapper;
import com.jk.springcloud.service.UserActivityService;
import org.springframework.stereotype.Service;

@Service
public class UserActivityServiceImpl extends ServiceImpl<UserActivityMapper, UserActivity> implements UserActivityService {
}
