package com.cisdi.steel.module.data.r3;

import com.cisdi.steel.module.data.AbstractDataExecute;
import com.cisdi.steel.module.data.DataCatalog;
import com.cisdi.steel.module.data.r3.enums.ShaoJieEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/29 </P>
 *
 * @author leaf
 * @version 1.0
 */
public abstract class AbstractSjDataExecute extends AbstractDataExecute {
    @Override
    public Map<DataCatalog, Map<String, Object>> getExcelData() {
        Map<DataCatalog, Map<String, Object>> map = new HashMap<>();
        map.put(ShaoJieEnum.sj1, getFirstData());
        return map;
    }

    public abstract Map<String, Object> getFirstData();
}
