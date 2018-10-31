package com.cisdi.steel.module.data;

import java.util.Map;

/**
 * <p>Description:   获取数据     </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/23 </P>
 *
 * @author leaf
 * @version 1.0
 */
public interface IDataHandler {

    /**
     * 获取生成报表需要的数据
     *
     * @return 结果
     */
    Map<DataCatalog, Map<String, Object>> getExcelData();
}
