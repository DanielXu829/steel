package com.cisdi.steel.module.data.r4.data;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.data.r4.AbstractYglDataExcute;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 原料车间中控室原始记录表
 */
@Component
public class YuanliaoGongliaoData6 extends AbstractYglDataExcute {
    @Override
    public Map<String, Object> getFirstData() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data1 = new ArrayList<>();

        Date date = new Date();
        for (int i = 0; i < 30; i++) {
            Map<String, Object> map = new HashMap<>();
            for (int j = 0; j < 24; j++) {
                map.put("t" + j, RandomUtils.nextInt(0, 999) + "");
            }
            data1.add(map);
        }
        result.put("data1", data1);
        return result;
    }
}
