package com.cisdi.steel.common.exception;


import com.cisdi.steel.common.resp.ApiResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * <p>Description: 编码重复异常  </p>
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
@AllArgsConstructor
@NoArgsConstructor
public class CodeRepeatException extends RuntimeException {

    /**
     * 返回的结果
     */
    private ApiResult apiResult;

}
