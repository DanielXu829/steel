package com.cisdi.steel.module.data;

/**
 * <p>Description:  目录结构名称   </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/24 </P>
 *
 * @author leaf
 * @version 1.0
 */
public interface DataCatalog {
    /**
     * 生成文件的 目录名或者路径
     * 如 a 或者  a/b/c
     *
     * @return 路径
     */
    String getFileCatalog();

    /**
     * 存储在数据库对应的分类名称
     *
     * @return 分类名
     */
    String getSequenceName();
}