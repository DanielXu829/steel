package com.cisdi.steel.config.http;

import java.util.Map;

/**
 * <p>Description:  okhttp接口       </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/19 </P>
 *
 * @author leaf
 * @version 1.0
 */
public interface HttpUtil {
    /**
     * get
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return 结果
     */
    String get(String url, Map<String, String> queries);

    /**
     * get请求
     *
     * @param url url
     * @return 结果
     */
    String get(String url);

    /**
     * post
     *
     * @param url    请求的url
     * @param params post form 提交的参数
     * @return 结果
     */
    String post(String url, Map<String, String> params);

    /**
     * get
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     */
    String getForHeader(String url, Map<String, String> queries);

    /**
     * Post请求发送JSON数据....{"name":"zhangsan","pwd":"123456"}
     * 参数一：请求Url
     * 参数二：请求的JSON
     * 参数三：请求回调
     */
    String postJsonParams(String url, String jsonParams);

    /**
     * Post请求发送xml数据....
     * 参数一：请求Url
     * 参数二：请求的xmlString
     * 参数三：请求回调
     */
    String postXmlParams(String url, String xml);
}
