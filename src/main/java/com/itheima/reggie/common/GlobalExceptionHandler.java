package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author LJM
 * @create 2022/4/15
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class}) //表示拦截哪些类型的controller注解
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理SQLIntegrityConstraintViolationException异常的方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandle(SQLIntegrityConstraintViolationException exception){
        log.error(exception.getMessage()); //报错记得打日志
        if (exception.getMessage().contains("Duplicate entry")){
            //获取已经存在的用户名，这里是从报错的异常信息中获取的
            String[] split = exception.getMessage().split(" ");
            String msg = split[2] + "这个菜品分类名已经存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }


    /**
     * 处理自定义的异常，为了让前端展示我们的异常信息，这里需要把异常进行全局捕获，然后返回给前端
     * @param exception
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandle(CustomException exception){
        log.error(exception.getMessage()); //报错记得打日志
        //这里拿到的message是业务类抛出的异常信息，我们把它显示到前端
        return R.error(exception.getMessage());
    }


}
