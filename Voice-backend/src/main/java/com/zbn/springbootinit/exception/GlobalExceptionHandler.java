package com.zbn.springbootinit.exception;

import com.zbn.springbootinit.common.BaseResponse;
import com.zbn.springbootinit.common.ErrorCode;
import com.zbn.springbootinit.common.ResultUtils;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author <a href="https://github.com/qwerzbn">zbn</a>
 * @date 2024/06/23
 */
@RestControllerAdvice
@Slf4j
@Hidden
// @Hidden 表示该类中的方法不会被OpenAPI文档中包含，即不会被Swagger扫描到。
// @Hidden注解一般用于隐藏一些不需要在文档中展示的类和方法，比如一些内部使用的工具类、配置类等。
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
