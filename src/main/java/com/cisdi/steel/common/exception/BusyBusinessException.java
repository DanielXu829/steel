package com.cisdi.steel.common.exception;


import com.cisdi.steel.common.enums.HttpCodeEnum;

/**
 * <p>Description: 业务繁忙异常 用于并发量多 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author common
 * @version 1.0
 * @since 2018/8/7
 */
public class BusyBusinessException extends LeafException {

    public BusyBusinessException() {
        super(HttpCodeEnum.BUSY_BUSINESS);
    }

    public BusyBusinessException(String message) {
        super(HttpCodeEnum.BUSY_BUSINESS.getCode(), message);
    }
}
