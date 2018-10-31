package com.cisdi.steel.generate.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Leaf
 */
@Data
@Accessors(chain = true)
public class SuperService {
    private String basePackage = "com.cisdi.steel.common";

    /**
     * 是否继承 service 默认true
     */
    private boolean isSuperService = true;

    private String superServiceClass = ".base.service.IBaseService";

    private String superServiceImplClass = ".base.service.impl.BaseServiceImpl";

    public String getSuperServiceClass() {
        return basePackage + superServiceClass;
    }

    public String getSuperServiceImplClass() {
        return basePackage + superServiceImplClass;
    }


    public SuperService(boolean isSuperService, String basePackage) {
        this.isSuperService = isSuperService;
        this.basePackage = basePackage;
    }

    public SuperService(boolean isSuperService) {
        this.isSuperService = isSuperService;
    }

    public SuperService(String basePackage) {
        this.basePackage = basePackage;
    }

    public SuperService() {

    }
}