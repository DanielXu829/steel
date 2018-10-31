package com.cisdi.steel.common.util;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * <p>Description:   </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author common
 * @date 2018/3/24
 * @version 1.0
 *
 */
@Slf4j
public class FreemarkerUtil {

    /**
     * 单例(必须)  模版配置对象
     */
    private static final Configuration cfg;
    //初始化FreeMarker配置，详情看
    static {
        //创建一个Configuration实例，建议带参，
        //不带参的构造方法被标明为过期了，这里我用的是  freemarker-2.3.23.jar
        //可以从  maven repository 官网下载  jar 包 ，不会下载请拉最后
        cfg = new Configuration(Configuration.VERSION_2_3_23);
        //设置FreeMarker的模版文件夹位置，只到文件夹，不带文件，
        //如：C:/freemarker_ftl/xxx.ftl  那么只需要 C:/freemarker_ftl
        try {
            //三者选一，另外两个注释
            //一个文件夹路径 具体路径
//            one_dir1();
            //一个文件夹路径 类路径
            one_dir2();
            //多个文件夹路径 多个路径
//            more_dir();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 设置默认编码
        cfg.setDefaultEncoding("utf-8");

        //错误控制器，控制异常，详情看图4
        //RETHROW_HANDLER ：错误信息会输出到控制台
        //HTML_DEBUG_HANDLER : 错误信息会输出到你要生成的html页面，详情看图4_1
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        //cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
    }
    /**
     * 单例
     */
    private static class LazyHolder {
        /**
         * 实例化工具类
         */
        private static final FreemarkerUtil fk = new FreemarkerUtil();
    }
    /**
     * 私有构造函数
     */
    private FreemarkerUtil() {

    }
    public static FreemarkerUtil getInstance(){
        return LazyHolder.fk;
    }

    /**
     * 所有模板都在一个文件夹路径
     * @throws IOException
     */
    private static void one_dir1() throws IOException {
        cfg.setDirectoryForTemplateLoading(new File("C:/freemarker_ftl"));
    }

    /**
     * 所有模板都在一个文件夹路径
     * @throws IOException
     */
    private static void one_dir2() throws IOException {
        //ftl模板文件统一放至 com.testExample.freemarker.template 包下面  
        cfg.setClassForTemplateLoading(FreemarkerUtil.class, "/templates/");
    }

    /**
     * 所有模板分别在多个文件夹路径
     * @throws IOException
     */
    private static void more_dir() throws IOException {
        //建议，就算不同文件夹，但是模板文件名也不能相同，详情看 图3  
        FileTemplateLoader ftl1 = new FileTemplateLoader(new File("C:/freemarker_ftl"));
        FileTemplateLoader ftl2 = new FileTemplateLoader(new File("E:/freemarker_ftl"));
        ClassTemplateLoader ctl = new ClassTemplateLoader(FreemarkerUtil.class, "/com/testExample/freemarker/template/");
        TemplateLoader[] loaders = new TemplateLoader[]{ftl1, ftl2, ctl};
        MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);
        cfg.setTemplateLoader(mtl);
    }


    /**
     * 生成模板
     *
     * @param templateName   模板名称
     * @param targetFileName 目标文件路径
     * @param root           模板里面变量的值
     * @return 是否生成成功 true 成功 false失败
     */
    private boolean process(String templateName, String targetFileName, Map<String, Object> root) {
        Writer out = null;
        try {
            File file = new File(targetFileName);
            if (!file.exists()) {
                file.createNewFile();
                //因为是生成到 Linux 上，需要设置权限，自己写的
                //设置可执行权限，第二个参数默认为true（表示root权限才可以执行该文件，false为所有人都可以）
                file.setExecutable(true, false);
                //设置可读权限，第二个参数默认为true（同上）
                file.setReadable(true, false);
                //设置可写权限，第二个参数默认为true（这里可以省略不写）
                file.setWritable(true);
            }
            // 创建模版对象
            Template t = cfg.getTemplate(templateName);
            // 设置输出流
            // out = new OutputStreamWriter(System.out); //-> 输出到控制台
            // 设置编码  UTF-8
            out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            // 在模版上执行插值操作，并输出到制定的输出流中，详情看 图5
            // root 是模板ftl中的变量的值
            t.process(root, out);
            return true;
        } catch (Exception e) {
            log.error("generate freemarker fail  {} {} {}", templateName, targetFileName, e);
        } finally {
            if (null != out) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 根据模版名称加载对应模版
     *
     * @param templateName 模版名称
     * @return 返回对应的模板
     */
    private static Template getTemplate(String templateName) {
        try {
            return cfg.getTemplate(templateName);
        } catch (IOException e) {
            log.error("fail to getTemplate", e);
        }
        return null;
    }

    /**
     * 从模板生成静态html页面
     *
     * @param templateName   模板文件名
     * @param targetFileName 目标文件名，即 html 的文件名，包含路径，绝对路径
     * @param root           模板里面变量的值
     * @return 是否生成成功 true 成功 false失败
     */
    public static boolean generatorHtmlFromTemplate(String templateName, String targetFileName, Map<String, Object> root) {
        try {
            return getInstance().process(templateName, targetFileName, root);
        } catch (Exception e) {
            log.error("generatorHtmlFromTemplate fail {} {} {}", templateName, targetFileName, root, e);
        }
        return false;
    }

    /**
     * 控制台打印通过模板生成的文件
     *
     * @param dataModel    数据模型
     * @param templateName 输出模版
     * @return 模板生成的数据
     */
    public static String getContent(Map<String, Object> dataModel, String templateName) {
        try {
            StringWriter stringWriter = new StringWriter();
            Template template = getTemplate(templateName);
            if(null ==template){
                log.warn("fail to getTemplate is null");
                return null;
            }
            template.process(dataModel, stringWriter);
            stringWriter.flush();
            String result = stringWriter.toString();
            stringWriter.close();
            return result;
        } catch (TemplateException | IOException e) {
            log.error("fail to getContent {} {}", dataModel, templateName, e);
        }
        return null;
    }
    /**
     * 根据地址获得数据的字节流
     *
     * @param strUrl 网络连接地址
     * @return 图片Base64码
     */
    public static String getImgBase64ByUrl(String strUrl) {
        try {
            // 建立 Http 链接
            HttpURLConnection conn = (HttpURLConnection) new URL(strUrl).openConnection();
            // 5秒响应超时
            conn.setConnectTimeout(5 * 1000);
            conn.setDoInput(true);
            // 判断http请求是否正常响应请求数据，如果正常获取图片 Base64 码
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 获取图片输入流
                InputStream inStream = conn.getInputStream();
                // 用于存储图片输出流
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                // 定义缓存流，用于存储图片输出流
                byte[] buffer = new byte[1024];
                int len = 0;
                // 图片输出流循环写入
                while ((len = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                // 图片输出流转字节流
                byte[] btImg = outStream.toByteArray();
                inStream.close();
                outStream.flush();
                outStream.close();
                return new String(Base64.encodeBase64(btImg));
            }
        }
        catch (Exception e) {
            log.error("getImgBase64ByUrl to fail {} ",strUrl,e);
        }
        return null;
    }


    public static void main(String[] args) throws Exception {
        Map<String, Object> root = new HashMap<>();
        root.put("username", "common forget");
        root.put("age", 22);
        root.put("sex", "男");
        String content = FreemarkerUtil.getContent(root, "test.ftl");
        System.out.println(content);
    }
}