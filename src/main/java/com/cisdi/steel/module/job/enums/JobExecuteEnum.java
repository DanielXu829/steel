package com.cisdi.steel.module.job.enums;

/**
 * <p>Description:  任务执行状态   </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/6 </P>
 *
 * @author leaf
 * @version 1.0
 */
public enum JobExecuteEnum {
    /**
     * 自动
     */
    automatic("automatic"),

    /**
     * 手动执行
     */
    manual("manual");

    private String name;

    JobExecuteEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
