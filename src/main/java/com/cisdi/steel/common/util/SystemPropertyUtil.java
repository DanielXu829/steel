package com.cisdi.steel.common.util;

import com.alibaba.fastjson.JSONObject;

import java.util.LinkedHashMap;

/**
 * <p>Description: 系统参数整理  </p>
 * <p>email: ypasdf@163.com </p>
 * <p>Copyright: Copyright (c) 2018 </p>
 * <P>Date: 2018/3/25 </P>
 *
 * @author common
 * @version 1.0
 */
public class SystemPropertyUtil {

    /**
     * @return 获取当前的系统参数 json格式
     */
    public static String propertyJson() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("Java版本号", version());
        map.put("Java提供商", vendor());
        map.put("Java提供商网站", vendorUrl());
        map.put("jre目录", home());
        map.put("Java虚拟机规范版本号", vmSpecificationVersion());
        map.put("Java虚拟机规范提供商", vmSpecificationVendor());
        map.put("Java虚拟机规范名称", vmSpecificationName());
        map.put("Java虚拟机版本号", vmVersion());
        map.put("Java虚拟机提供商", vmVendor());
        map.put("Java虚拟机名称", vmName());
        map.put("Java规范版本号", specificationVersion());
        map.put("Java规范提供商", specificationName());
        map.put("Java类版本号", classVersion());
        map.put("Java类路径", classPath());
        map.put("Java lib 路径", libraryPath());
        map.put("Java输入输出临时路径", tmpdir());
        map.put("Java编译器", compiler());
        map.put("Java执行路径", extDirs());
        map.put("操作系统名称", name());
        map.put("操作系统架构", arch());
        map.put("操作系统版本号", osVersion());
        map.put("文件分隔符", fileSeparator());
        map.put("路径分隔符", pathSeparator());
        map.put("直线分隔符", lineSeparator());
        map.put("操作系统用户名", userName());
        map.put("用户的主目录", userHome());
        map.put("当前程序所在目录", userDir());
        return JSONObject.toJSONString(map);
    }

    /**
     * @return Java版本号
     */
    public static String version() {
        return System.getProperty("java.version");
    }

    /**
     * @return Java提供商
     */
    public static String vendor() {
        return System.getProperty("java.vendor");
    }

    /**
     * @return Java提供商网站
     */
    public static String vendorUrl() {
        return System.getProperty("java.vendor.url");
    }

    /**
     * @return jre目录
     */
    public static String home() {
        return System.getProperty("java.home");
    }

    /**
     * @return Java虚拟机规范版本号
     */
    public static String vmSpecificationVersion() {
        return System.getProperty("java.vm.specification.version");
    }

    /**
     * @return Java虚拟机规范提供商
     */
    public static String vmSpecificationVendor() {
        return System.getProperty("java.vm.specification.vendor");
    }

    /**
     * @return Java虚拟机规范名称
     */
    public static String vmSpecificationName() {
        return System.getProperty("java.vm.specification.name");
    }

    /**
     * @return Java虚拟机版本号
     */
    public static String vmVersion() {
        return System.getProperty("java.vm.version");
    }

    /**
     * @return Java虚拟机提供商
     */
    public static String vmVendor() {
        return System.getProperty("java.vm.vendor");
    }

    /**
     * @return Java虚拟机名称
     */
    public static String vmName() {
        return System.getProperty("java.vm.name");
    }

    /**
     * @return Java规范提供商
     */
    public static String specificationVersion() {
        return System.getProperty("java.specification.version");
    }

    /**
     * @return Java规范提供商
     */
    public static String specificationName() {
        return System.getProperty("java.specification.name");
    }

    /**
     * @return Java类版本号
     */
    public static String classVersion() {
        return System.getProperty("java.class.version");
    }

    /**
     * @return Java类路径
     */
    public static String classPath() {
        return System.getProperty("java.class.path");
    }

    /**
     * @return Java lib 路径
     */
    public static String libraryPath() {
        return System.getProperty("java.library.path");
    }


    /**
     * @return Java输入输出临时路径
     */
    public static String tmpdir() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * @return Java编译器
     */
    public static String compiler() {
        return System.getProperty("java.compiler");
    }

    /**
     * @return Java执行路径
     */
    public static String extDirs() {
        return System.getProperty("java.ext.dirs");
    }

    /**
     * @return 操作系统名称
     */
    public static String name() {
        return System.getProperty("os.name");
    }

    /**
     * @return 操作系统架构
     */
    public static String arch() {
        return System.getProperty("os.arch");
    }

    /**
     * @return 操作系统版本号
     */
    public static String osVersion() {
        return System.getProperty("os.version");
    }

    /**
     * @return 文件分隔符
     */
    public static String fileSeparator() {
        return System.getProperty("file.separator");
    }


    /**
     * @return 路径分隔符
     */
    public static String pathSeparator() {
        return System.getProperty("path.separator");
    }


    /**
     * @return 直线分隔符
     */
    public static String lineSeparator() {
        return System.getProperty("line.separator");
    }


    /**
     * @return 操作系统用户名
     */
    public static String userName() {
        return System.getProperty("user.name");
    }

    /**
     * @return 用户的主目录
     */
    public static String userHome() {
        return System.getProperty("user.home");
    }

    /**
     * @return 当前程序所在目录
     */
    public static String userDir() {
        return System.getProperty("user.dir");
    }
}
