package com.cisdi.steel.common.exception.handler;


import com.alibaba.fastjson.JSONException;
import com.cisdi.steel.common.enums.HttpCodeEnum;
import com.cisdi.steel.common.exception.*;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;

/**
 * <p>Description:  通用异常处理类 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng
 * @version 1.0
 * @date 2018/1/4
 * @since 1.8
 */
@SuppressWarnings("ALL")
@ControllerAdvice
@ResponseBody
@Slf4j
public class LeafExceptionHandler {


    /**
     * 业务异常
     *
     * @param e 自定义异常
     * @return 结果
     */
    @ExceptionHandler(LeafException.class)
    public ApiResult handleLeafException(LeafException e) {
        log.error(e.getMessage());
        // 服务器错误
        return e.getApiResult();
    }

    /**
     * 绑定异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler({BindException.class})
    public ApiResult bindExceptionHandler(BindException e) {
        log.error(e.getMessage());
        return ApiUtil.custom(HttpCodeEnum.UNPROCESABLE_ENTITY);
    }

    /**
     * 验证验证错误
     *
     * @param exception
     * @return
     */
    @ExceptionHandler({ValidationException.class})
    public ApiResult handle(ValidationException exception) {
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException exs = (ConstraintViolationException) exception;
            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                return ApiUtil.fail(item.getMessage());
            }
        }
        return ApiUtil.custom(HttpCodeEnum.UNPROCESABLE_ENTITY);
    }

    /**
     * 登陆 相关的异常
     *
     * @param e 登陆异常
     * @return 结果
     */
    @ExceptionHandler(LoginException.class)
    public ApiResult loginExceptionHandler(LoginException e) {
        log.info(e.getMessage());
        // 没有权限
        return e.getApiResult();
    }

    /**
     *  数据处理错误(非程序错误）
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResult businessHandler(BusinessException e) {
        log.info(e.getMessage());
        // 业务异常
        return e.getApiResult();
    }

    /**
     * 业务繁忙
     * @param e
     * @return
     */
    @ExceptionHandler(BusyBusinessException.class)
    public ApiResult busyBusinessHandler(BusyBusinessException e) {
        log.info(e.getMessage());
        // 业务繁忙
        return e.getApiResult();
    }
    /**
     * 编码重复的异常
     *
     * @param e 异常
     * @return 结果
     */
    @ExceptionHandler(CodeRepeatException.class)
    public ApiResult loginExceptionHandler(CodeRepeatException e) {
        log.info(e.getMessage());
        if(Objects.nonNull(e.getApiResult())){
            return e.getApiResult();
        }
        // 业务异常
        return ApiUtil.fail("编码重复");
    }

    /**
     * 方法验证错误
     *
     * @param exception
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ApiResult handle(MethodArgumentNotValidException exception) {
        String defaultMessage = exception.getBindingResult().getFieldError().getDefaultMessage();
        return ApiUtil.custom(HttpCodeEnum.BAD.getCode(), defaultMessage);
    }

    /**
     * 没有权限异常
     *
     * @param exception
     * @return
     */
    @ExceptionHandler({UnauthorizedException.class})
    public ApiResult unAuth(UnauthorizedException exception) {
        return exception.getApiResult();
    }

    /**
     * 400 - Bad Request
     *
     * @param e 异常
     * @return 结果
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResult handleHttpMessageNotReadableException(Exception e) {
        return ApiUtil.custom(HttpCodeEnum.UNPROCESABLE_ENTITY);

    }

    /**
     * 405 - Method Not Allowed
     *
     * @param e 异常
     * @return 结果
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult handleHttpRequestMethodNotSupportedException(Exception e) {
        return ApiUtil.custom(HttpCodeEnum.METHOD_NOT_ALLOWED);
    }

    /**
     * 415 - Unsupported Media Type
     *
     * @param e 异常
     * @return 结果
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ApiResult handleHttpMediaTypeNotSupportedException(Exception e) {
        return ApiUtil.custom(HttpCodeEnum.UNSUPPORTED_MEDIA_TYPE);

    }

    /**
     * 500 - Internal Server Error
     *
     * @param e 异常
     * @return 结果
     */
    @ExceptionHandler(Exception.class)
    public ApiResult handleException(Exception e) {
        // 抛出编码重复的异常
        if (e.getMessage().contains("com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry")) {
            return ApiUtil.fail("编码重复");
        }
        log.error(e.getMessage());
        return ApiUtil.error();
    }


    /**
     * sql 异常
     *
     * @param e 异常
     * @return 结果
     */
    @ExceptionHandler(SQLException.class)
    public ApiResult handleSql(Exception e) {
        log.error(e.getMessage());
        return ApiUtil.error();
    }

    /**
     * json转换错误
     *
     * @param e 异常
     * @return 结果
     */
    @ExceptionHandler(JSONException.class)
    public ApiResult jsonConvert(Exception e) {
      log.error(e.getMessage());
        return ApiUtil.notAcceptable();
    }
}
