package com.cisdi.steel.module.data.r1.enums;

import com.cisdi.steel.module.data.DataCatalog;

/**
 * <p>Description: 高炉的分类   </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/24 </P>
 *
 * @author leaf
 * @version 1.0
 */
public enum GaoluEnum implements DataCatalog {
    bf5("bf6", "bf6"),
    bf6("bf7", "bf7"),
    bf7("bf8", "bf8");

    /**
     * 文件的目录名
     */
    private String fileCatalog;

    /**
     * 存储在数据库的分类名
     */
    private String sequenceName;

    GaoluEnum(String fileCatalog, String categoryName) {
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
