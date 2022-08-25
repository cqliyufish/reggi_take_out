package com.yu.reggie_take_out.sevice.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.reggie_take_out.common.R;
import com.yu.reggie_take_out.entity.User;
import com.yu.reggie_take_out.mapper.UserMapper;
import com.yu.reggie_take_out.sevice.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Service
@RestController
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserService userService;
    public R<String> sendMsg(@RequestBody User user){
        log.info(user.getPhone().toString());
        return null;
    }
}
