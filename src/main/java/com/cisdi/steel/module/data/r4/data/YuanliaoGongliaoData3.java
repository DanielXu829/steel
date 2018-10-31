package com.cisdi.steel.module.data.r4.data;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.data.r4.AbstractYglDataExcute;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 供料异常跟踪表
 */
@Component
public class YuanliaoGongliaoData3 extends AbstractYglDataExcute {
    @Override
    public Map<String, Object> getFirstData() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data1 = new ArrayList<>();

        Date date = new Date();
        for (int i = 0; i < 12; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", DateUtil.getFormatDateTime(date, DateUtil.MMddChineseFormat));
            date = DateUtil.addDays(date, 1);

            for (int j = 0; j <16; j++) {
                map.put("t" + j, RandomUtils.nextInt(0, 999) + "");
            }
            for (int k = 0; k <6; k++) {
                Map map1 = new HashMap();
                Map map2 = new HashMap();
                Map map3 = new HashMap();
                Map map4 = new HashMap();
                Map map5 = new HashMap();
                Map map6 = new HashMap();
                for (int j = 0; j <16; j++) {
                    map1.put("t" + j, RandomUtils.nextInt(0, 999) + "");
                    map2.put("t" + j, RandomUtils.nextInt(0, 999) + "");
                    map3.put("t" + j, RandomUtils.nextInt(0, 999) + "");
                    map4.put("t" + j, RandomUtils.nextInt(0, 999) + "");
                    map5.put("t" + j, RandomUtils.nextInt(0, 999) + "");
                    map6.put("t" + j, RandomUtils.nextInt(0, 999) + "");
                }
                map.put("k", map1);
                map.put("m", map2);
                map.put("n", map3);
                map.put("o", map3);
                map.put("p", map3);
                map.put("q", map3);
            }

            data1.add(map);
        }
        result.put("data1", data1);
        return result;
    }
}
