package com.cisdi.steel.module.data.r4.enums;

import com.cisdi.steel.module.data.DataCatalog;

/**
 * 原材料分类
 */
public enum YuanCaiLiaoEnum implements DataCatalog {
    df1("d1", "gl1"),
    df2("d1", "yl1");

    private String fileCatalog;

    private String categoryName;

    YuanCaiLiaoEnum(String fileCatalog, String categoryName) {
        this.fileCatalog = fileCatalog;
        this.categoryName = categoryName;
    }

    @Override
    public String getFileCatalog() {
        return this.fileCatalog;
    }

    @Override
    public String getSequenceName() {
        return categoryName;
    }

}
