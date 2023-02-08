package com.jk.springcloud.dto;

import com.jk.springcloud.entity.Category;
import lombok.Data;

import java.util.List;

/**
 * @program: activity
 * @description:
 * @author: JanKin
 * @create: 2022-10-25 16:58
 */
@Data
public class CategoryDto {
    private int id;
    private String mainClass;
    private List<Category> subClass;
}
