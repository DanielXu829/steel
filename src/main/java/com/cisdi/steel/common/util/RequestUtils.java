package com.cisdi.steel.common.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;

/**
 * <p>Description:  Request工具类  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng
 * @version 1.0
 * @date 2017/12/6
 * @since 1.8
 */
@SuppressWarnings("ALL")
public class RequestUtils {

    /**
     * 获取项目的绝对路径
     *
     * @return 项目的绝对路径
     */
    public static String getRootPath() {
        String classPath = "", rootPath = "";
        try {
            //防止有空格,%20等的出现
            classPath = URLDecoder.decode(Objects.requireNonNull(RequestUtils.class.getClassLoader().getResource("/")).getPath(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (!"".equals(classPath)) {
            //windows下
            if ("\\".equals(File.separator)) {
                rootPath = classPath.substring(1, classPath.indexOf("/WEB-INF/classes"));
                rootPath = rootPath.replace("/", "\\");
            }
            //linux下
            if ("/".equals(File.separator)) {
                rootPath = classPath.substring(0, classPath.indexOf("/WEB-INF/classes"));
                rootPath = rootPath.replace("\\", "/");
            }
        }
        return rootPath;
    }

    /**
     * @return 当前的reqeust
     */
    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes a = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return a.getRequest();
    }

    /**
     * 获取当前的真实ip
     *
     * @return ip地址
     */
    public static String getIpAddr() {
        return getIpAddr(getCurrentRequest());
    }

    /**
     * 获取真实地址
     *
     * @param request 请求
     * @return 当前ip
     */
    public static String getIpAddr(final HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个路由时，取第一个非unknown的ip
        final String[] arr = ip.split(",");
        for (final String str : arr) {
            if (!"unknown".equalsIgnoreCase(str)) {
                ip = str;
                break;
            }
        }
        return ip;
    }
}
