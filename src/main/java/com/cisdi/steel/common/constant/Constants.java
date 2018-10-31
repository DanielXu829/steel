package com.cisdi.steel.common.constant;

/**
 * <p>Description: 全局常量类  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng
 * @version 1.0
 * @date 2018/1/4
 * @since 1.8
 */
public final class Constants {
    /**
     * 请求的路径
     */
    public static final String API_URL = "http://10.66.3.30:9001";
    /**
     * 是/否
     */
    public static final String YES = "1";
    public static final String NO = "0";
    /**
     * 锁定后 不能 删除 修改
     */
    public static final String LOCKED = "-1";
    /**
     * 删除标记（0：正常；1：删除）
     */
    public static final String DEL_FLAG_NORMAL = "0";
    public static final String DEL_FLAG_DELETE = "1";

    /**
     * 请求头的token
     */
    public static final String USER_TOKEN = "user-token";

    /**
     * 树的顶级 节点id
     */
    public static final Long PARENT_ID = 0L;

    /**
     * 所属语言的编码
     */
    public static final String LANGUAGE_CODE = "LANGUAGE_CODE";

    private Constants() {

    }
}
