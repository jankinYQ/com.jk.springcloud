package com.jk.springcloud.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jk.springcloud.entity.Notice;
import com.jk.springcloud.mapper.NoticeMapper;
import com.jk.springcloud.service.NoticeService;
import org.springframework.stereotype.Service;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
}
