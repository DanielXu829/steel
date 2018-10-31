package com.cisdi.steel.module.data.r2;

import com.cisdi.steel.module.data.AbstractDataExecute;
import com.cisdi.steel.module.data.DataCatalog;
import com.cisdi.steel.module.data.r2.enums.JiaohuaEnum;

import java.util.Map;

/**
 * <p>Description:   烧结抽象类      </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/26 </P>
 *
 * @author leaf
 * @version 1.0
 */
public abstract class AbstractJhDataExecute extends AbstractDataExecute {
    @Override
    public Map<DataCatalog, Map<String, Object>> getExcelData() {
        Map<DataCatalog, Map<String, Object>> result = initMap();
        result.put(JiaohuaEnum.jh1, getFirstData());
        result.put(JiaohuaEnum.jh2, getTwoData());
        result.put(JiaohuaEnum.jh3, getThreeData());
        return result;
    }

    /**
     * 3
     *
     * @return 数据
     */
    public abstract Map<String, Object> getFirstData();

    /**
     * 2
     *
     * @return 数据
     */
    public abstract Map<String, Object> getTwoData();

    /**
     * 3
     *
     * @return 数据
     */
    public abstract Map<String, Object> getThreeData();
}
