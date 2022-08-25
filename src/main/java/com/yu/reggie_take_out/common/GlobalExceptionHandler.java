package com.yu.reggie_take_out.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常捕获
 * @ControllerAdvice(待拦截Ctroller)
 *
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
@ResponseBody
public class GlobalExceptionHandler {
    //@ExceptionHandler(异常信息)
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        if(ex.getMessage().contains("Duplicate entry")){
            //Duplicate entry '111' for key 'idx_username'
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + " already exsits";
            return R.error(msg);
        }
        return R.error("unknown error");
    }



    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        return R.error(ex.getMessage());
    }

}
