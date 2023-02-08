package com.jk.springcloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jk.springcloud.entity.Category;
import com.jk.springcloud.mapper.CategoryMapper;
import com.jk.springcloud.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
