package com.yu.reggie_take_out;

import com.yu.reggie_take_out.utils.Sms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;


//slf4j 用于输出日志
@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
public class ReggieTakeOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieTakeOutApplication.class, args);
        log.info("success");
    }

}
