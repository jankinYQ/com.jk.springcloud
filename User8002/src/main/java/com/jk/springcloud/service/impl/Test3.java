package com.jk.springcloud.service.impl;

import com.jk.springcloud.entity.Activity;
import com.jk.springcloud.entity.Activity2;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

/**
 * @program: com.jk.springcloud
 * @description: 测试缓存同步
 * @author: JanKin
 * @create: 2023-01-15 15:58
 */
//    -----------------------canal缓存同步测试代码-------------------------------------------

//    要监听的表activity
    @CanalTable("activity")
    @Component
    class Test3 implements EntryHandler<Activity2> {
        @Override
        public void insert(Activity2 activity){
            System.out.println("-----插入数据-----");
            System.out.println("insert:"+activity);
            // 编写代码更新缓存。。。
        }
        @Override
        public void update(Activity2 before, Activity2 after){
            System.out.println("-----更新数据-----");
            System.out.println(after);
            // 编写代码更新缓存。。。
        }
        @Override
        public void delete(Activity2 activity){
            System.out.println("------删除数据------");
            System.out.println("delete:"+activity);
            // 编写代码更新缓存。。。
        }
    }

//    ---------------------------------------------------------------------------------


