package com.jk.springcloud.service.impl;


import com.alibaba.fastjson.JSON;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jk.springcloud.common.R;
import com.jk.springcloud.entity.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.jk.springcloud.entity.User;
import com.jk.springcloud.mapper.UserMapper;
import com.jk.springcloud.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang.math.RandomUtils.nextInt;


@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

//    -----------------------------redis 哨兵模式测试代码-------------------------------------------

@Autowired
private StringRedisTemplate redisTemplate;

public void Test(){
    String key = "num";
    String value = "123k";
    redisTemplate.opsForValue().set(key,value);
    Object obj = redisTemplate.opsForValue().get(key);
    System.out.println(obj);
}

//    ---------------------------------------------------------------------------------


    //    -----------------------------caffeine进程缓存测试代码-------------------------------------------

    public void Test2(){
        //构建cache对象
        com.github.benmanes.caffeine.cache.Cache<String,String> cache = Caffeine.newBuilder().build();
        //存数据
        cache.put("fruit","苹果");
        //取数据
        String fruit = cache.getIfPresent("fruit");
        System.out.println(fruit);
        //取数据，如果未命中则查数据库 key就是前面的参数，会把返回的结果进行缓存
        String fruit2 = cache.get("fruit2", Key -> {
            // 编写代码根据key去数据库查寻数据
            return "西呱";
        });
        System.out.println(fruit2);

    }

//    ---------------------------------------------------------------------------------



    @CreateCache(name = "jetCache",expire = 180,timeUnit = TimeUnit.SECONDS,cacheType = CacheType.BOTH)
    private Cache<String,String> cacheCode;

    @Resource
    private JavaMailSender javaMailSender;

    //    发送人
    private String from = "1665298409@qq.com";
    //    接收人
    private String to = "lyk166529@qq.com";
    //    标题
    private String subject = "活动通知";
    //    正文
    private String context = "<a herdxref='https//www.baidu.com'>点我<a/>关于学习spring boot时涉及到邮件内容的学习";



    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public void sendMail(String email,String msg) {
        // 交换机名称
        String exchangeName = "activity.notice";
        // 消息
        R mail = new R();
        Map<String,String> map = new HashMap<>();
        map.put("email",email);
        map.put("message",msg);
        mail.setMap(map);
//        // 发送消息
//        rabbitTemplate.convertAndSend(exchangeName, "mail", mail);

// ----------------------------------消息可靠性------------------------------
        //1 准备correlationData,参数要指定id 那就用uuid
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        //2 准备Confirm Callback
        correlationData.getFuture().addCallback(new SuccessCallback<CorrelationData.Confirm>() {
            @Override
            public void onSuccess(CorrelationData.Confirm confirm) {
                if(confirm.isAck()){
                    //ack
                    log.info("消息成功投递到交换机！消息ID:{}",correlationData.getId());
                }else{
                    //nack
                    log.error("消息投递到交换机失败！消息ID：{}",correlationData.getId());
                }
            }
        }, new FailureCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                // 记录日志
                log.error("消息发送失败",throwable);
                System.out.println("消息发送失败"+throwable);
                //消息重发

            }
        });

        // 发送消息,做消息确认要加个参数
        rabbitTemplate.convertAndSend(exchangeName, "mail", mail,correlationData);

// -------------------------------------------------------------

////高端邮件
//        context = msg;
//        to = email;
//
//        MimeMessage message = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message);
//        helper.setFrom(from);
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(context, true);
//
//
////        添加附件
////        File file = new File("");
////        helper.addAttachment("fileName",file);
//
//        javaMailSender.send(message);
    }

    //    验证码
    @Override
    public String setCode(String email){
        String code = "";
        for(int i = 0 ; i < 4 ; i++){
            int c = nextInt(10);
            code = code + c;
        }
        cacheCode.put(email,code);

        return code;
    }

    @Override
    public String getCode(String email){
        String code = cacheCode.get(email);

        return code;
    }


    @Transactional
    public R<String> updateUser(User user){

        System.out.println("-----------"+user);
//         校验用户名合法性
        //        查找这个用户名是否被别的用户使用
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getName,user.getName());
        queryWrapper.ne(User::getEmail,user.getEmail());
        User u = this.getOne(queryWrapper);

        if(u != null){
            return R.error("用户名已被占用");
        }else{
            this.updateById(user);
        }

        return R.success("修改成功");
    }
}

