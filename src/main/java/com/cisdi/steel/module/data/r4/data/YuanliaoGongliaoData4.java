package com.cisdi.steel.module.data.r4.data;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.data.r4.AbstractYglDataExcute;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 堆混匀矿粉配比通知单
 */
@Component
public class YuanliaoGongliaoData4 extends AbstractYglDataExcute {
    @Override
    public Map<String, Object> getFirstData() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data1 = new ArrayList<>();
        List<Map<String, Object>> data2 = new ArrayList<>();

        Date date = new Date();
        for (int i = 0; i < 12; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", DateUtil.getFormatDateTime(date, DateUtil.MMddChineseFormat));
            date = DateUtil.addDays(date, 1);

            for (int j = 0; j < 7; j++) {
                map.put("t" + j, RandomUtils.nextInt(0, 999) + "");
            }
            data1.add(map);

            Map<String, Object> map2 = new HashMap<>();
            map2.put("t" + i, RandomUtils.nextInt(0, 999) + "");
            data2.add(map2);

        }
        result.put("data1", data1);
        result.put("data2", data1);
        return result;
    }
}
