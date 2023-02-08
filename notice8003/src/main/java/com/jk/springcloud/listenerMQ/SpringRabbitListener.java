package com.jk.springcloud.listenerMQ;

import com.jk.springcloud.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@Slf4j
public class SpringRabbitListener {

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


    public R<String> sendMail(String email, String msg) {
//高端邮件
        context = msg;
        to = email;

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            //        添加附件
            //        File file = new File("");
            //        helper.addAttachment("fileName",file);

            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setText(context, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            return R.error("邮件异常");
        }
        return R.error("邮件已发送");
    }



    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "notice.mail1"),
            exchange = @Exchange(name = "activity.notice", type = ExchangeTypes.DIRECT),
            key = {"mail"}
    ))
    public void listenDirectQueue1(R mail) {
        String email = (String) mail.getMap().get("email");
        String message = (String) mail.getMap().get("message");

        log.info("正在发送~");

//        int j = 1/0;  // 此处模拟消费者处理出错抛异常
        sendMail(email,message);

        System.out.println("1号邮箱接收到的消息：【" + mail + "】");
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "notice.mail2"),
            exchange = @Exchange(name = "activity.notice", type = ExchangeTypes.DIRECT),
            key = {"mail"}
    ))
    public void listenDirectQueue2(R mail){

        System.out.println("2号邮箱接收到的消息：【" + mail + "】");
    }

//    ------------------------rabbit MQ集群 - 仲裁队列 ---------------------
    @Bean
    public org.springframework.amqp.core.Queue quorumQueue(){
        return QueueBuilder
                .durable("quorum.queue")  // 持久化
                .quorum()   //  仲裁队列
                .build();
    }
//    -------------------------------------------------------

}
