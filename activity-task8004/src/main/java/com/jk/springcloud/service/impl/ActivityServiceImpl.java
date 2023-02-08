package com.jk.springcloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jk.springcloud.clients.FileClient;
import com.jk.springcloud.clients.UserClient;
import com.jk.springcloud.common.R;
import com.jk.springcloud.entity.Activity;
import com.jk.springcloud.entity.Files;
import com.jk.springcloud.entity.Notice;
import com.jk.springcloud.entity.User;
import com.jk.springcloud.mapper.ActivityMapper;
import com.jk.springcloud.service.ActivityService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Resource
    ActivityMapper activityMapper;

    @Autowired
    private FileClient fileClient;

    @Autowired
    private UserClient userClient;

    public List<Activity> getByCondition2(int order,String address,int state, int category, String keyWord ,int current,int size){

        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();

//        活动isAlive  活动还活着 没有删
        queryWrapper.eq(Activity::getIsAlive,true);
//        活动地域
        queryWrapper.like(address!=null,Activity::getAddress,address);
//        状态排序 -1 不限,0 已结束,  1 进行中 , 2 未开始
        LocalDateTime now = LocalDateTime.now();
        queryWrapper.le(state == 0,Activity::getEndTime, now);
        queryWrapper.le(state == 1,Activity::getStartTime, now);
        queryWrapper.ge(state == 1,Activity::getEndTime, now);
        queryWrapper.ge(state == 2,Activity::getStartTime, now);
        //  可有可无多此一属性 删不影响      isActive  活动是否已结束  / 当想看的是进行中或未开始的活动  当然要屏蔽isActive为false的
                queryWrapper.eq(state == 1 || state ==2 ,Activity::getIsActive,true);

//        分类 Id,    -1表示分类不限
        queryWrapper.eq(category > 0,Activity::getCategoryId,category);
//        搜索关键词 , null表示不限关键词
        if(keyWord != null && keyWord != "")
            queryWrapper.and(wrapper ->wrapper
                    .like(Activity::getName,keyWord)
                    .or().like(Activity::getIntroduction,keyWord));

//        综合排序 0 不限 , 1 开始时间先后, 2 浏览人数最多
        queryWrapper.orderByDesc(order == 1,Activity::getStartTime);
        queryWrapper.orderByDesc(order == 2,Activity::getVisitors);

//      分页
        IPage page = new Page(current,size);
        List<Activity> activities = this.page(page,queryWrapper).getRecords();

        return activities;
    }

    public int autoincrementVi(int id){
        return activityMapper.autoincrementVi(id);
    }



    private RestHighLevelClient client;

    //ES实现
//    public List<Activity> getByCondition(int order1,String address1,int state1, int category1, String keyWord1 ,int current1,int size1) throws IOException {
    public List<Activity> getByCondition(int order,String address,int state, int category, String keyWord ,int current,int size) throws IOException {

        this.client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://localhost:9200")
        ));


        System.out.println(order+"-"+address+"-"+state+category+"-"+keyWord+"-"+current+"-"+size);

        //1准备Request
        SearchRequest request = new SearchRequest("activity");
        //2准备DSL
        //2.1准备BooleanQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //2.2添加term
//must 在 should 会失效
        if(keyWord != null && keyWord != "")
            boolQuery.must(QueryBuilders.multiMatchQuery(keyWord,"name","introduction"));

        //地址
        if(address != null && address != "")
            boolQuery.filter(QueryBuilders.matchQuery("address",address));

        LocalDateTime now = LocalDateTime.now();
        if(state == 0)
            boolQuery.filter(QueryBuilders.rangeQuery("endTime").lte(now));
        else if(state == 1) {
            boolQuery.filter(QueryBuilders.rangeQuery("startTime").lte(now));
            boolQuery.filter(QueryBuilders.rangeQuery("endTime").gte(now));
        }
        else if(state == 2)
            boolQuery.filter(QueryBuilders.rangeQuery("startTime").gte(now));

        //分类
        if(category > 0)
            boolQuery.filter(QueryBuilders.termQuery("categoryId",category));


        //2.3分页 from，size
        if(current > 0)
            request.source().from(current-1).size(size);
        else
            request.source().from(0).size(1000);

        //排序
        if(order == 1)
            request.source().sort("startTime", SortOrder.DESC);
        else if(order == 2)
            request.source().sort("visitors", SortOrder.DESC);

        request.source().query(boolQuery);

        //3发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        //4解析结果
        SearchHits searchHits = response.getHits();
        //4.1查询的总条数
        long total = searchHits.getTotalHits();
        System.out.println("查询到总条数:"+total);
        //4.2查询的结果数组
        SearchHit[] hits = searchHits.getHits();

        List<Activity> activities = new ArrayList<>();
        for(SearchHit hit : hits) {
            //4.3得到source
            String json = hit.getSourceAsString();
            //反序列化
            Activity activity = JSON.parseObject(json,Activity.class);

            activities.add(activity);

            System.out.println("");
            System.out.println(activity);
        }

        this.client.close();

        return activities;
    }





    // 测试事务
//    @GlobalTransactional
    public R updateAct(Activity activity){

        this.updateById(activity);


        //try部分为测试事务，临时测试代码
//        try{
//            System.out.println("try!begin");
//
//            R<User> userR = userClient.getById(1);
//            User user = userR.getData();
//            user.setName("jankin2");
//
//            R<Files> filesR = fileClient.getById(2);
//            Files file = filesR.getData();
//            file.setSize(-1);  //数据库中size不能为-1，模拟出错
//            file.setFileName("文件2");
//
//            userClient.updateById(user);
//            System.out.println("1ok");
//            fileClient.updateFile(file);
//            System.out.println("2ok");
//
//        }catch (Exception e){
//            throw new RuntimeException("失败回滚",e);
//        }

        return R.success("更新成功");
    }

}

