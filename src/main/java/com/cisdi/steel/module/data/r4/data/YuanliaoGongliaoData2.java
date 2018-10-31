package com.cisdi.steel.module.data.r4.data;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.data.r4.AbstractYglDataExcute;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 供料车间集控中心交接班记录
 */
@Component
public class YuanliaoGongliaoData2 extends AbstractYglDataExcute {

    @Override
    public Map<String, Object> getFirstData() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data1 = new ArrayList<>();

        Date date = new Date();
        for (int i = 0; i < 34; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("date" + (i + 1), DateUtil.getFormatDateTime(date, DateUtil.MMddChineseFormat));
            date = DateUtil.addDays(date, 1);

            map.put("banci" + (i + 1), i + 1);
            map.put("jiaob" + (i + 1), "交班人" + i);
            map.put("jieb" + (i + 1), "接班人" + i);

            for (int j = 0; j < 4; j++) {
                map.put("t" + j, RandomUtils.nextInt(0, 999) + "");
            }
            data1.add(map);
        }
        result.put("data1", data1);
        return result;
    }
}
