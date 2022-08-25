package com.yu.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yu.reggie_take_out.common.R;
import com.yu.reggie_take_out.entity.User;
import com.yu.reggie_take_out.sevice.UserService;
import com.yu.reggie_take_out.utils.Sms;
import com.yu.reggie_take_out.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     * @param session
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(HttpSession session, @RequestBody User user){
        // 获得手机号
        String phone = user.getPhone();
        String smsPhone = "+1" + phone;

        // 生成随机4位验证码
        if(StringUtils.hasLength(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            // 发送短信
            log.info("code ={} ", code);
//            Sms.send(code, smsPhone);
            // 生成的验证码保存到session
            session.setAttribute(phone, code);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");

    }

    /**
     * 验证登录
     */
    @PostMapping("/login")
    public R<User> login(HttpSession session, @RequestBody Map map){
        log.info(map.toString());
        // 获取手机号，验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        // 获取session中的验证码
        Object codeSession = session.getAttribute(phone);
        // 验证码比对

        if (codeSession != null && codeSession.equals(code)){
            // 判断是否是新用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);

            if(user == null){
                // 创建新用户
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }

        return R.error("登录失败");


    }
}
