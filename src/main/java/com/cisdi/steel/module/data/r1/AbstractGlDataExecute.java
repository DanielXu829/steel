package com.cisdi.steel.module.data.r1;

import com.cisdi.steel.module.data.AbstractDataExecute;
import com.cisdi.steel.module.data.DataCatalog;
import com.cisdi.steel.module.data.r1.enums.GaoluEnum;

import java.util.Map;

/**
 * <p>Description: 高炉的数据获取        </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/24 </P>
 *
 * @author leaf
 * @version 1.0
 */
public abstract class AbstractGlDataExecute extends AbstractDataExecute {

    @Override
    public Map<DataCatalog, Map<String, Object>> getExcelData() {
        Map<DataCatalog, Map<String, Object>> result = initMap();
        result.put(GaoluEnum.bf5, getFirstData());
        result.put(GaoluEnum.bf6, getTwoData());
        result.put(GaoluEnum.bf7, getThreeData());
        return result;
    }

    /**
     * 处理第一座 高炉数据
     *
     * @return 数据
     */
    public abstract Map<String, Object> getFirstData();

    /**
     * 处理第二座  高炉数据
     *
     * @return 数据
     */
    public abstract Map<String, Object> getTwoData();

    /**
     * 处理第三座  高炉数据
     *
     * @return 数据
     */
    public abstract Map<String, Object> getThreeData();
}
