package com.cisdi.steel.module.data;

import com.cisdi.steel.config.http.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Description: 数据获取的执行类 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/24 </P>
 *
 * @author leaf
 * @version 1.0
 */
public abstract class AbstractDataExecute implements IDataHandler {

    /**
     * 由于获取接口数据
     */
    @Autowired
    protected HttpUtil httpUtil;

    /**
     * 模拟一个 map数据
     *
     * @return Hash数据
     */
    public Map<DataCatalog, Map<String, Object>> initMap() {
        return new HashMap<>();
    }
}
