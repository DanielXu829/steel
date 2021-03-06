package com.cisdi.steel.config.http;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * OkHttpClient通过spring 获取单例
 *
 * @author 95765
 */
@SuppressWarnings("All")
@Slf4j
public class OkHttpImpl implements HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);

    @Autowired
    private OkHttpClient okHttpClient;

    @Override
    public byte[] getStrem(String url, Map<String, String> params) {
        StringBuffer sb = new StringBuffer(url);
        try {
            if (params != null && params.keySet().size() > 0) {
                boolean firstFlag = true;
                Iterator iterator = params.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                    if (firstFlag) {
                        sb.append("?" + entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), java.nio.charset.StandardCharsets.UTF_8.toString()));
                        firstFlag = false;
                    } else {
                        sb.append("&" + entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), java.nio.charset.StandardCharsets.UTF_8.toString()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("GET请求URL:" + sb.toString());
        Request request = new Request.Builder()
                .url(sb.toString())
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().bytes();
            }
        } catch (Exception e) {
            logger.error("okhttp3 get error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * get
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return 结果
     */
    @Override
    public String get(String url, Map<String, String> queries) {
        String responseBody = "";
        StringBuffer sb = new StringBuffer(url);
        try {
            if (queries != null && queries.keySet().size() > 0) {
                boolean firstFlag = true;
                Iterator iterator = queries.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                    if (firstFlag) {
                        sb.append("?" + entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), java.nio.charset.StandardCharsets.UTF_8.toString()));
                        firstFlag = false;
                    } else {
                        sb.append("&" + entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), java.nio.charset.StandardCharsets.UTF_8.toString()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("GET请求URL:" + sb.toString());
        Request request = new Request.Builder()
                .url(sb.toString())
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            logger.error("okhttp3 get error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }

    @Override
    public String get(String url) {
        return get(url, null);
    }

    /**
     * post
     *
     * @param url    请求的url
     * @param params post form 提交的参数
     * @return
     */
    @Override
    public String post(String url, Map<String, String> params) {
        String responseBody = "";
        FormBody.Builder builder = new FormBody.Builder();
        //添加参数
        if (params != null && params.keySet().size() > 0) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            logger.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }

    /**
     * get
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return
     */
    @Override
    public String getForHeader(String url, Map<String, String> queries) {
        String responseBody = "";
        StringBuffer sb = new StringBuffer(url);
        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?" + entry.getKey() + "=" + entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&" + entry.getKey() + "=" + entry.getValue());
                }
            }
        }
        Request request = new Request.Builder()
                .addHeader("key", "cellValue")
                .url(sb.toString())
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            logger.error("okhttp3 get error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }

    /**
     * Post请求发送JSON数据....{"name":"zhangsan","pwd":"123456"}
     * 参数一：请求Url
     * 参数二：请求的JSON
     * 参数三：请求回调
     */
    @Override
    public String postJsonParams(String url, String jsonParams) {
        String responseBody = "";
        log.debug("POST请求URL：" + url);
        log.debug("POST请求参数：" + jsonParams);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            logger.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }

    /**
     * Post请求发送xml数据....
     * 参数一：请求Url
     * 参数二：请求的xmlString
     * 参数三：请求回调
     */
    @Override
    public String postXmlParams(String url, String xml) {
        String responseBody = "";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"), xml);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            logger.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }
}