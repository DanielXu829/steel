package com.cisdi.steel.generate;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.cisdi.steel.generate.config.GeneratorProperties;
import com.cisdi.steel.generate.config.SuperClass;
import com.cisdi.steel.generate.config.SuperService;

import java.io.File;
import java.util.*;

/**
 * <p>Description:   </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng
 * @version 1.0
 * 2018/1/6
 * @since 1.8
 */
public class MpGenerator {
    /**
     * 模板路径
     */
    private static final String TEMPLATE_PATH = "/templates/mybatisPlus";
    /**
     * 基础包导入包
     */
    private static final String BASE_PACKAGE = "com.cisdi.steel.common";

    public static void main(String[] args) {
        GeneratorProperties properties = new GeneratorProperties();
        // 需要生成的表名 不填 默认所有
        String[] tableNames = {"sys_user_token"};
        // 表名前缀
        String[] tablePrefix = {};
        properties
                // 包名
                .setPackageName("com.cisdi.steel.module")
                // 模块名
                .setModuleName("sys")
                // 生成的表
                .setTableNames(tableNames)
                // 需要去掉的表前缀
                .setTablePrefix(tablePrefix)
                // 输出路径 默认当前项目的gen文件夹中
//                .setOutPutDir(new File("").getAbsolutePath() + File.separator + "leaf-generate" + GeneratorProperties.buildSrc())
                .setOutPutDir(new File("").getAbsolutePath() + GeneratorProperties.buildSrc())
                .setDbUrl("jdbc:mysql://10.66.3.221:3306/steel1?characterEncoding=utf8&useSSL=true")
                .setUsername("root")
                .setPassword("p@ssw0rd")
                // 设置是否继承实体
                .setSuperClass(new SuperClass(false))
                // 设置 是否继承自定server
                .setSuperService(new SuperService());
        MpGenerator e = new MpGenerator();
        // 设置参数
        e.setProperties(properties);
        // 执行生成
        e.generateByTables();
        // 打包
//        FileUtils.zipFiles(properties.getOutPutDir(),"*",properties.getOutPutDir()+".zip");
        // 删除生成的文件
//        FileUtils.deleteDirectory(properties.getOutPutDir());
    }

    private GeneratorProperties properties;

    private void setProperties(GeneratorProperties properties) {
        this.properties = properties;
    }

    /**
     * 执行
     */
    private void generateByTables() {
        new AutoGenerator()
                //全局配置
                .setGlobalConfig(globalConfig())
                //数据库
                .setDataSource(dataSource())
                // 策略
                .setStrategy(strategyConfig())
                // 包信息
                .setPackageInfo(packageConfig())
                .setTemplateEngine(new FreemarkerTemplateEngine())
                // 设置自定义配置 不建议
                .setCfg(injectionConfig())
                // 模板配置
                .setTemplate(templateConfig()).execute();
    }

    /**
     * 数据库配置
     *
     * @return 数据库配置
     */
    private DataSourceConfig dataSource() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(properties.getDbType())
                .setUrl(properties.getDbUrl())
                .setUsername(properties.getUsername())
                .setPassword(properties.getPassword())
                .setDriverName(properties.getDriverName());
        return dataSourceConfig;
    }

    /**
     * 设置 包 配置
     *
     * @return 配置
     */
    private PackageConfig packageConfig() {
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent(properties.getPackageName())
                // controller包
                .setController("controller")
                .setModuleName(properties.getModuleName())
                // 实体包
                .setEntity("entity");
        return packageConfig;
    }

    /**
     * 全局 配置
     *
     * @return 全局配置
     */
    private GlobalConfig globalConfig() {
        GlobalConfig gc = new GlobalConfig();
        // 输出目录
        gc.setOutputDir(properties.getOutPutDir());
        // 文件是否重写
        gc.setFileOverride(true);
        // 不需要ActiveRecord特性的请改为false
        gc.setActiveRecord(true);
        // XML 二级缓存
        gc.setEnableCache(false);
        // XML ResultMap
        gc.setBaseResultMap(false);
        // XML columnList
        gc.setBaseColumnList(false);
        // .setKotlin(true) 是否生成 kotlin 代码
        gc.setAuthor(properties.getAuthor());
        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setControllerName("%sController");
        return gc;
    }

    /**
     * 策略
     *
     * @return mybatis策略
     */
    private StrategyConfig strategyConfig() {
        StrategyConfig strategy = new StrategyConfig();
        // 全局大写命名 ORACLE 注意
        // strategy.setCapitalMode(true);
        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 设置是否为lombok模式
        strategy.setEntityLombokModel(true);
        // 设置逻辑字段
        strategy.setLogicDeleteFieldName(properties.getLogicField());
        // 乐观锁
        strategy.setVersionFieldName(properties.getVersionFiled());
        // 是否开始restController
        strategy.setRestControllerStyle(true);
        // 此处可以修改为您的表前缀
        strategy.setTablePrefix(properties.getTablePrefix());
        // 需要生成的表
        strategy.setInclude(properties.getTableNames());
        if (Objects.nonNull(properties.getExclude())) {
            // 排除生成的表
            strategy.setExclude(properties.getExclude());
        }
        if (properties.getSuperClass().isSuperClass()) {
            // 自定义实体父类
            strategy.setSuperEntityClass(properties.getSuperClass().getSuperEntityClass());
            // 自定义实体，公共字段
            strategy.setSuperEntityColumns(properties.getSuperClass().getSuperEntityColumns());
        }

        if (properties.getSuperService().isSuperService()) {
            // 自定义 service 父类
            strategy.setSuperServiceClass(properties.getSuperService().getSuperServiceClass());
            // 自定义 service 实现类父类
            strategy.setSuperServiceImplClass(properties.getSuperService().getSuperServiceImplClass());
        }
        // 【实体】是否为构建者模型（默认 false）
        strategy.setEntityBuilderModel(true);
        return strategy;
    }

    /**
     * 模板 相关配置 不需要改 模板放在当前templates/mybatisPlus下面
     * 如上任何一个模块如果设置 空 OR Null 将不生成该模块。
     *
     * @return 模板配置
     */
    private TemplateConfig templateConfig() {
        TemplateConfig tc = new TemplateConfig();
        String templateEntity = TEMPLATE_PATH + "/entity.java";
        String templateMapper = TEMPLATE_PATH + "/mapper.java";
        String templateXml = TEMPLATE_PATH + "/mapper.xml";
        String templateService = TEMPLATE_PATH + "/service.java";
        String templateServiceImpl = TEMPLATE_PATH + "/serviceImpl.java";
        String templateController = TEMPLATE_PATH + "/controller.java";
        tc.setController(templateController);
        tc.setEntity(templateEntity);
        tc.setMapper(templateMapper);
        tc.setXml(templateXml);
        tc.setService(templateService);
        tc.setServiceImpl(templateServiceImpl);
        return tc;
    }

    /**
     * 自定义配置
     *
     * @return 自定义配置
     */
    private InjectionConfig injectionConfig() {
        final String path = dealWithPath();
        // 包名
        StringBuilder sb = new StringBuilder(properties.getPackageName());
        // 包名+模块名
        if (properties.getModuleName() != null && !"".equals(properties.getModuleName())) {
            sb.append(".");
            sb.append(properties.getModuleName());
        }
        final String packagePath = sb.toString();
        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>();
                // 查询
                map.put("queryPackage", packagePath + ".query");
                // 基础包
                map.put("basePackage", BASE_PACKAGE);
                setMap(map);
            }
        };
        List<FileOutConfig> lists = new ArrayList();
        // 自定义模板 templates/mybatisPlus 模板存放路径
        FileOutConfig data1 = new FileOutConfig(TEMPLATE_PATH + "/entityQuery.java.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 输出文件名
                return path + File.separator + "query" + File.separator + tableInfo.getEntityName() + "Query.java";
            }
        };
        lists.add(data1);
        injectionConfig.setFileOutConfigList(lists);
        return injectionConfig;
    }


    /**
     * 处理路径
     *
     * @return 生成文件的路径
     */
    private String dealWithPath() {
        String parentDir = properties.getOutPutDir();
        if (StringUtils.isEmpty(parentDir)) {
            parentDir = System.getProperty(ConstVal.JAVA_TMPDIR);
        }
        if (!StringUtils.endsWith(parentDir, File.separator)) {
            parentDir += File.separator;
        }
        // 输出路径
        StringBuilder sb = new StringBuilder(parentDir);
        String result = properties.getPackageName().replaceAll("\\.", "\\" + File.separator);
        sb.append(result);
        // 模块名
        if (StringUtils.isNotEmpty(properties.getModuleName())) {
            sb.append(File.separator);
            sb.append(properties.getModuleName());
        }
        return sb.toString();
    }

}
