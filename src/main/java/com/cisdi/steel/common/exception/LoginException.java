package com.cisdi.steel.common.exception;


import com.cisdi.steel.common.enums.HttpCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * <p>Description: 登陆异常类 表示没有登陆  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng
 * @version 1.0
 * @date 2018/1/4
 * @since 1.8
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginException extends LeafException {

    public LoginException(HttpCodeEnum httpCodeEnum) {
        super(httpCodeEnum);
    }
}
