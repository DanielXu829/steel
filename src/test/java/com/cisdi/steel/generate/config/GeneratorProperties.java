package com.cisdi.steel.generate.config;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;

/**
 * <p>Description: 生成参数 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author leaf
 * @version 1.0
 * @since 2018/7/5
 */
@Data
@Accessors(chain = true)
public class GeneratorProperties {

    /**
     * 包名
     */
    private String packageName;
    /**
     * 模块名
     */
    private String moduleName = "";
    /**
     * 作者
     */
    private String author = "leaf";
    /**
     * 输出目录
     */
    private String outPutDir = new File("").getAbsolutePath() + File.separator + "gen";
    /**
     * 生成的表名 为空表示所有
     */
    private String[] tableNames = {};

    /**
     * 表前缀 为空表示不去掉
     */
    private String[] tablePrefix = {};

    /**
     * 不生成的表名
     */
    private String[] exclude;

    /**
     * 数据库url
     */
    private String dbUrl;
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 连接驱动 默认 mysql
     */
    private String driverName = "com.mysql.jdbc.Driver";
    /**
     * 数据库类型 默认mysql
     */
    private DbType dbType = DbType.MYSQL;

    /**
     * 逻辑字段
     */
    private String logicField = "del_flag";

    /**
     * 版本字段
     */
    private String versionFiled = "version";
    /**
     * 是否继承 实体类 默认为false
     */
    private SuperClass superClass = new SuperClass();

    /**
     * 继承service
     */
    private SuperService superService;


    public static String buildSrc() {
        return File.separator + "src" + File.separator + "main" + File.separator + "java";
    }

}


