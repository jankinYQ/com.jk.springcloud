package com.jk.springcloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import com.jk.springcloud.clients.FileClient;
import com.jk.springcloud.clients.UserClient;
import com.jk.springcloud.common.R;
import com.jk.springcloud.dto.ActivityDto;
import com.jk.springcloud.entity.*;
import com.jk.springcloud.service.CategoryService;
import com.jk.springcloud.service.UserActivityService;
import com.jk.springcloud.service.impl.ActivityServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/activity")
@CrossOrigin
@Slf4j
public class ActivityController {

    @Autowired
    private ActivityServiceImpl activityService;

//    @Autowired
//    private UserService userser;
    @Autowired
    private UserClient userClient;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserActivityService userActivityService;

    /**
     * getById
     * @param
     * @return
     */
    @GetMapping("/{id}")
    public R<ActivityDto> getById(@PathVariable int id){

        Activity activity = activityService.getById(id);

        R<User> rUser = userClient.getById( activity.getCreateUser() );
        User user = rUser.getData();

        ActivityDto activityDto = new ActivityDto();
//  对象拷贝
        BeanUtils.copyProperties(activity,activityDto);
        activityDto.setNickName(user.getName());
        activityDto.setHeadPortrait(user.getHeadPortrait());

//        根据分类Id获取分类名
        Category category = categoryService.getById( activity.getCategoryId() );
        Category parentCategory = categoryService.getById( category.getFatherCategory() );

        activityDto.setCategoryName( category.getName() +"--"+parentCategory.getName() );

        return R.success(activityDto);
    }

//    微服务调用接口
    @GetMapping("/act/{id}")
    public R<Activity> getAct(@PathVariable int id){

        Activity activity = activityService.getById(id);

        return R.success(activity);
    }

    /**
     * 已加入的活动
     * @param
     * @return
     */
    @GetMapping("/joined/{userId}")
    public R<List<Activity>> getByJoined(@PathVariable int userId, HttpSession session) {
        LambdaQueryWrapper<UserActivity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserActivity::getUserId,userId);
        List<UserActivity> userActivitys = userActivityService.list(queryWrapper);


//        想要用Stream流来操作集合，那么就需要将数组或者集合先转换成Stream流才可以操作
        List<Activity> list = userActivitys.stream().map((item) -> {
            LambdaQueryWrapper<Activity> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(Activity::getId,item.getActivityId());
            queryWrapper2.eq(Activity::getIsAlive,true);//还活着 没有删除

            Activity activity = activityService.getOne(queryWrapper2);
//            Activity activity = activityService.getById(item.getActivityId());

            return activity;
        }).collect(Collectors.toList());

        return R.success(list);
    }
    /**
     * 自己创建的活动
     * @param
     * @return
     */
    @GetMapping("/created/{userId}")
    public R<List<Activity>> getByCreated(@PathVariable int userId, HttpSession session) {
        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Activity::getCreateUser,userId);
        queryWrapper.eq(Activity::getIsAlive,true); //还活着 没有删除
        List<Activity> activities = activityService.list(queryWrapper);

        return R.success(activities);
    }


        /**
         * 条件查询
         * @param map
         * @param session
         * @return
         */
    @PostMapping("/getByCondition")
    public R<List<Activity>> getByCondition(@RequestBody Map map, HttpSession session) throws IOException {
        System.out.println(map);
//        综合排序 0 不限 , 1 开始时间先后, 2 浏览人数最多
        int order = (int) map.get("order");
//        活动地域
        String address = map.get("address").toString();
//        状态排序 -1 不限,0 已结束,  1 进行中 , 2 未开始
        int state = (int) map.get("state");
//        分类 Id,    -1表示分类不限
        int categoryId = (int) map.get("category");
//        搜索关键词 , null表示不限关键词
        String keyWord = map.get("keyWord").toString();


//        分页
        int current = (int) map.get("current");
        int size = (int) map.get("size");

        List<Activity> activities= activityService.getByCondition( order,address,state, categoryId, keyWord,current,size);


        return R.success(activities);
    }

    /**
     *新增活动
     * @param
     * @return
     */
    @PostMapping("/add")
    public R saveAct(@RequestBody Activity activity){

        activityService.save(activity);

        return R.success("新增活动成功");
    }

    /**
     *更新活动
     * @param
     * @return
     */
    @PutMapping("/updateAct")
    public R updateAct(@RequestBody Activity activity){

        activityService.updateById(activity);

        return R.success("更新成功act");
    }

    /**
     *更新活动是否结束
     * @ param (id, is_active = false)
     * @return
     */
    @PutMapping("/updateActActive")
    public R updateActActive(@RequestBody Activity activity){

        return activityService.updateAct(activity);

    }

    /**
     *更新浏览人数 +1
     * @param
     * @return
     */
    @PutMapping("/updateVi/{activityId}")
    public R updateVisitors(@PathVariable int activityId){

        activityService.autoincrementVi(activityId);

        return R.success("更新成功");
    }


    /**
     * 根据活动id删除 活动
     * @ param (id, is_alive = false)
     * @return
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable int id){

        LambdaUpdateWrapper<Activity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Activity::getId,id);
        updateWrapper.set(Activity::getIsAlive, false);

        activityService.update(updateWrapper);

        return R.success("删除成功");
    }

}
