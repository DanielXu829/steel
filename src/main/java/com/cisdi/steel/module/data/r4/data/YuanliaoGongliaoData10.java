package com.cisdi.steel.module.data.r4.data;

import com.cisdi.steel.module.data.r4.AbstractYglDataExcute;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 原料混匀矿粉A4干转湿配比换算计算表
 */
@Component
public class YuanliaoGongliaoData10 extends AbstractYglDataExcute {
    @Override
    public Map<String, Object> getFirstData() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data1 = new ArrayList<>();

        Date date = new Date();
        for (int i = 0; i < 40; i++) {
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
