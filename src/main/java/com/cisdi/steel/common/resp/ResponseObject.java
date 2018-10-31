package com.cisdi.steel.common.resp;

import lombok.Data;

/**
 * <p>Description:  数据类型为 对象  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/19 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Data
public class ResponseObject<T> {
    /**
     * 对象数据属性
     */
    private T data;
}
