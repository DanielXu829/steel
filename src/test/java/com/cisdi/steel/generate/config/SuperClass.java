package com.cisdi.steel.generate.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Leaf
 */
@Data
@Accessors(chain = true)
public class SuperClass {
    private String basePackage = "com.cisdi.steel.common";

    /**
     * 是否继承 实体类 默认为false
     */
    private boolean isSuperClass = false;
    /**
     * 默认继承的类
     */
    private String superEntityClass = ".base.entity.AbstractDataEntity";

    private String[] superEntityColumns = new String[]{"id", "create_time", "create_id", "update_id", "update_time", "remark"};

    public String getSuperEntityClass() {
        return basePackage + superEntityClass;
    }


    public SuperClass() {

    }

    public SuperClass(String basePackage) {
        this.basePackage = basePackage;
    }

    public SuperClass(boolean isSuperClass, String basePackage) {
        this.isSuperClass = isSuperClass;
        this.basePackage = basePackage;
    }

    public SuperClass(boolean isSuperClass) {
        this.isSuperClass = isSuperClass;
    }
}