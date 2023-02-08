package com.jk.springcloud.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.jk.springcloud.common.R;
import com.jk.springcloud.entity.User;
import com.jk.springcloud.service.impl.UserServiceImpl;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/user")
@CrossOrigin
@Slf4j
@RefreshScope  //属性刷新，nacos配置文件最新更新信息
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    /**
     * 发送手机短信验证码
     * @param
     * @return
     */
    @GetMapping("/send/{email}")
    public R<String> sendMsg(@PathVariable String email, HttpSession session) throws MessagingException {

        String code = userService.setCode(email);
        String msg = "注册/找回密码的验证码为:"+code+"，请在3分钟完成操作。";
        userService.sendMail(email,msg);

        return R.success("验证码发送成功！");
    }

    /**
     *
     * @param
     * @return
     */
    @GetMapping("/{id}")
    public R<User> getById(@PathVariable int id, HttpSession session) throws MessagingException {

        User user = userService.getById(id);

        return R.success(user);
    }


    /**
     * 移动端用户登录
     * @param
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody User user, HttpSession session){
        System.out.println("-----------"+user);

        //获取用户名
        String name = user.getName().toString();
        //获取密码
        String password = user.getPassword().toString();

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getName,name);

        User u = userService.getOne(queryWrapper);

        if(u == null){
            //判断当前用户是否为新用户，如果是新用户就自动完成注册
            return R.error("用户名不存在~");
        }else if( !( password.equals(u.getPassword()) ) )
            return R.error("密码错误~");

        session.setAttribute("user",user.getId());
        return R.success(u);

    }

    /**
     * 注册
     * @param
     * @param
     * @return
     */
    @PostMapping("/register")
    public R<User> register(@RequestBody User user){
        System.out.println("-----------"+user);

//        查找这个用户名是否被别的用户使用
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getName,user.getName());
        queryWrapper.ne(User::getEmail,user.getEmail());
        User u = userService.getOne(queryWrapper);

//        查找该用户是否已经注册过  注册过则更新用户名密码
        LambdaQueryWrapper<User> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(User::getEmail,user.getEmail());
        User u2 = userService.getOne(queryWrapper2);

        String code = userService.getCode(user.getEmail());
        if(u != null){
            return R.error("用户名已被使用~");
        }else if(u2 != null){//忘记密码更新用户信息
            //  验证码
            if(code != null && code.equals(user.getCode()) ) {
                user.setId(u2.getId());
                userService.updateById(user);
                return R.success(user);
            }else{
                return R.error("验证码不正确");
            }
        }else{//注册新增用户
            //    如果注册用户没有填用户名则用当前时间戳
            if(user.getName() == null || user.getName() == ""){
                //获取毫秒数
                Long milliSecond = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
                user.setName(milliSecond.toString());
            }

//            新用户默认头像
            user.setHeadPortrait("77d1f7fa-e41a-4b9a-bbb8-b99d8488d62d.jpg");
            //  验证码
            if(code != null && code.equals(user.getCode()) ) {
                userService.save(user);
                //   新增用户后返回新增的用户信息
                LambdaQueryWrapper<User> queryWrapper3 = new LambdaQueryWrapper<>();
                queryWrapper3.eq(User::getEmail,user.getEmail());
                User u3 = userService.getOne(queryWrapper3);
                return R.success(u3);
            }else{
                return R.error("验证码不正确");
            }
        }


    }

//    /**
//     * 校验名字合法性
//     * @param
//     * @param
//     * @return
//     */
//    @PostMapping("/checkName")
//    public R<String> checkName(@RequestBody User user){
//
//        if(user.getEmail() == null || user.getEmail() == ""){//登录时校验
//            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//            queryWrapper.eq(User::getName,user.getName());
//
//            User u = userService.getOne(queryWrapper);
//            if(u == null){
//                return R.success("用户名不存在");
//            }
//        }else {  //找回密码或注册时校验名字合法
//            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//            queryWrapper.ne(User::getEmail,user.getEmail());
//            queryWrapper.eq(User::getName,user.getName());
//
//            User u = userService.getOne(queryWrapper);
//            if(u == null){
//                return R.success("用户名已被使用");
//            }
//        }
//
//
//        return R.success("名字合法");
//
//    }
//
//
//    /**
//         * 找回用户名&密码
//     * @param
//     * @param
//     * @return
//            */
//    @PutMapping("/Retrieve")
//    public R<User> Retrieve(@RequestBody User user){
//        System.out.println("-----------"+user);
////        发送邮箱验证码
//
////        更改用户名&密码
//
//        return R.success(null);
//
//    }

    /**
     * 修改用户名&密码
     * @param
     * @param
     * @return
     */
    @PutMapping("/update")
    public R<String> updateById(@RequestBody User user){

        return userService.updateUser(user);

    }

//
//    @GetMapping("/publisher")
//    public void testSendMessage() throws IOException, TimeoutException {
//        // 1.建立连接
//        ConnectionFactory factory = new ConnectionFactory();
//        // 1.1.设置连接参数，分别是：主机名、端口号、vhost、用户名、密码
//        factory.setHost("127.0.0.1");
//        factory.setPort(5672);
//        factory.setVirtualHost("/");
//        factory.setUsername("guest");
//        factory.setPassword("guest");
//        factory.setConnectionTimeout(60000);
//        // 1.2.建立连接
//        Connection connection = factory.newConnection();
//
//        // 2.创建通道Channel
//        Channel channel = connection.createChannel();
//
//        // 3.创建队列
//        String queueName = "simple.queue";
//        channel.queueDeclare(queueName, false, false, false, null);
//
//        // 4.发送消息
//        String message = "hello, rabbitmq!";
//        channel.basicPublish("", queueName, null, message.getBytes());
//        System.out.println("发送消息成功：【" + message + "】");
//
//        // 5.关闭通道和连接
//        channel.close();
//        connection.close();
//
//    }

    @GetMapping("/sendMail")
    public void sendMail(String email,String message ) {
        userService.sendMail(email,message);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

//   spring amqp
@GetMapping("/publisher")
public void testSendMessage() throws IOException, TimeoutException {

    // 4.发送消息
    String queueName = "simple.queue";
    String message = "hello, rabbitmq!";
    rabbitTemplate.convertAndSend(queueName,message);
    System.out.println("发送消息成功：【" + message + "】");

}

//通过交换机发
    @GetMapping("/publisher2")
    public void testSendDirectExchange() {
        // 交换机名称
        String exchangeName = "itcast.direct";
        // 消息
        String message = "hello, red!";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, "red", message);
    }

//测试redis主从+哨兵模式
    @GetMapping("/test")
    void t(){
        // redis
        userService.Test();

        //caffeine
//        userService.Test2();
    }

//    nacos添加的配置文件中的值
    @Value("${student.name}")
    private String name;

    @GetMapping("/testBootstrap")
    String tt(){
        System.out.println(name);
        return name;
    }
}
