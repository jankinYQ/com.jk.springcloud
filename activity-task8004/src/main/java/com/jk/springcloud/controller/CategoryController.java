package com.jk.springcloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.jk.springcloud.common.R;
import com.jk.springcloud.dto.CategoryDto;
import com.jk.springcloud.entity.Category;
import com.jk.springcloud.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/category")
@CrossOrigin
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 通过用户ID和TaskId查UserTask
     * getById
     * @param
     * @return
     */
    @GetMapping
    public R<List<CategoryDto>> get(){

//        获取主类
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getFatherCategory,-1);

        List<Category> categories = categoryService.list(queryWrapper);

//        想要用Stream流来操作集合，那么就需要将数组或者集合先转换成Stream流才可以操作
        List<CategoryDto> list = categories.stream().map((item) -> {

            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setMainClass(item.getName());

//            获取相应子类
            LambdaQueryWrapper<Category> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(Category::getFatherCategory,item.getId());
            List<Category> subClass = categoryService.list(queryWrapper2);

            categoryDto.setSubClass(subClass);

            return categoryDto;
        }).collect(Collectors.toList());

        return R.success(list);
    }



}
