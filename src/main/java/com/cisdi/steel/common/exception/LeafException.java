package com.cisdi.steel.common.exception;

import com.cisdi.steel.common.enums.HttpCodeEnum;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * <p>Description: 公共业务异常类  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng
 * @version 1.0
 * @date 2018/1/4
 * @since 1.8
 */
@SuppressWarnings("ALL")
@EqualsAndHashCode(callSuper = true)
@Data
public class LeafException extends RuntimeException {

    /**
     * 返回的结果
     */
    private ApiResult apiResult;

    public LeafException(String message) {
        this(HttpCodeEnum.INTERNAL_SERVER_ERROR.getCode(), message);
    }

    public LeafException(int code, String message) {
        super(message);
        this.apiResult= ApiUtil.custom(code,message);
    }

    public LeafException(HttpCodeEnum httpCodeEnum) {
        super(httpCodeEnum.getMsg());
        this.apiResult= ApiUtil.custom(httpCodeEnum.getCode(),httpCodeEnum.getMsg());
    }

    public LeafException(){

    }

    /**
     * 抛出LeafException工具方法
     * @param message
     */
    public static void castException(String message) {
        throw new LeafException(message);
    }
}
