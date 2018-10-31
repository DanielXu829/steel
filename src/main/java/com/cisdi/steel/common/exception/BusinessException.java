package com.cisdi.steel.common.exception;


import com.cisdi.steel.common.enums.HttpCodeEnum;

/**
 * <p>Description:  业务异常 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author common
 * @version 1.0
 * @since 2018/5/24
 */
public class BusinessException extends LeafException {

    public BusinessException() {
        super(HttpCodeEnum.BUSINESS);
    }

    public BusinessException(String message) {
        super(HttpCodeEnum.BUSINESS.getCode(), message);
    }
}
