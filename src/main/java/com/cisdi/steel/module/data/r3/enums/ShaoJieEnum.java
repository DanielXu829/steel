package com.cisdi.steel.module.data.r3.enums;

import com.cisdi.steel.module.data.DataCatalog;

/**
 * <p>Description:   烧结      </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/29 </P>
 *
 * @author leaf
 * @version 1.0
 */
public enum ShaoJieEnum implements DataCatalog {
    sj1("sj1", "sj1");

    private String fileCatalog;

    private String categoryName;

    ShaoJieEnum(String fileCatalog, String categoryName) {
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
