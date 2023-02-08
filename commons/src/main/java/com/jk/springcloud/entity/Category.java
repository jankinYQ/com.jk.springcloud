package com.jk.springcloud.entity;

import lombok.Data;

/**
 * @program: activity
 * @description:
 * @author: JanKin
 * @create: 2022-10-25 16:58
 */
@Data
public class Category {
    private int id;
    private String name;
    private int fatherCategory;
}
