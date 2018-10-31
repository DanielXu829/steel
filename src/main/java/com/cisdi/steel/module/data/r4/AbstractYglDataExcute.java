package com.cisdi.steel.module.data.r4;

import com.cisdi.steel.module.data.AbstractDataExecute;
import com.cisdi.steel.module.data.DataCatalog;
import com.cisdi.steel.module.data.r4.enums.YuanCaiLiaoEnum;

import java.util.Map;

/**
 * 原供料的数据获取
 */
public abstract class AbstractYglDataExcute extends AbstractDataExecute {
    @Override
    public Map<DataCatalog, Map<String, Object>> getExcelData() {
        Map<DataCatalog, Map<String, Object>> result = initMap();
        result.put(YuanCaiLiaoEnum.df1, getFirstData());
        result.put(YuanCaiLiaoEnum.df2, getFirstData());
        return result;
    }

    public abstract Map<String, Object> getFirstData();
}
