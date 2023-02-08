package com.jk.springcloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jk.springcloud.entity.User;

import javax.mail.MessagingException;

public interface UserService extends IService<User> {
    void sendMail(String email,String msg) throws MessagingException;
    String  setCode(String email);
    String getCode(String email);
}

