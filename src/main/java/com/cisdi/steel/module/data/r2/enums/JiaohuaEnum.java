package com.cisdi.steel.module.data.r2.enums;

import com.cisdi.steel.module.data.DataCatalog;

/**
 * <p>Description: 烧结   </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/26 </P>
 *
 * @author leaf
 * @version 1.0
 */
public enum JiaohuaEnum implements DataCatalog {
    jh1("jh1", "jh1"),
    jh2("jh2", "jh2"),
    jh3("jh3", "jh3");

    /**
     * 文件的目录名
     */
    private String fileCatalog;

    /**
     * 存储在数据库的分类名
     */
    private String sequenceName;

    JiaohuaEnum(String fileCatalog, String categoryName) {
        this.fileCatalog = fileCatalog;
        this.sequenceName = categoryName;
    }

    @Override
    public String getFileCatalog() {
        return this.fileCatalog;
    }

    @Override
    public String getSequenceName() {
        return this.sequenceName;
    }
}
