package com.cisdi.steel.common.constant;

/**
 * *<p>
 * Email 122741482@qq.com
 * <p>
 * Describe: 收索条件 常量
 * @author chen
 *
 */
@SuppressWarnings("SpellCheckingInspection")
public final class SearchParam {
    private SearchParam(){

    }
    /**
     * 等于
     */
    public static final String SEARCH_EQ="search_eq_";

    /**
     * 左模糊(不推荐)
     */
    public static final String SEARCH_LLIKE="search_llike_";

    /**
     * 右模糊
     */
    public static final String SEARCH_RLIKE="search_rlike_";

    /***
     * 全模糊(不推荐）
     */
    public static final String SEARCH_LIKE="search_like_";

}
