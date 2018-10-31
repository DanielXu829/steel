package com.cisdi.steel.common.exception;


import com.cisdi.steel.common.enums.HttpCodeEnum;

/**
 * <p>Description: 权限异常 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/5/13 </P>
 *
 * @author common
 * @version 1.0
 */
public class UnauthorizedException extends LeafException {

    public UnauthorizedException(){
        super(HttpCodeEnum.UNAUTHORIZED);
    }


}
