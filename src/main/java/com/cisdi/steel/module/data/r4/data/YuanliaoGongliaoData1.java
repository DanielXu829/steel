package com.cisdi.steel.module.data.r4.data;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.data.r4.AbstractYglDataExcute;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 供料车间物料外排统计表
 */
@Component
public class YuanliaoGongliaoData1 extends AbstractYglDataExcute {
    /**
     * data1 Q6振动筛粉
     * data2 高炉筛粉
     * data3 中焦外排记录
     *
     * @return
     */
    @Override
    public Map<String, Object> getFirstData() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data1 = new ArrayList<>();
        List<Map<String, Object>> data2 = new ArrayList<>();
        List<Map<String, Object>> data3 = new ArrayList<>();
        List<Map<String, Object>> data4 = new ArrayList<>();
        List<Map<String, Object>> data5 = new ArrayList<>();
        List<Map<String, Object>> data6 = new ArrayList<>();

        Date date = new Date();
        for (int i = 0; i < 34; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", DateUtil.getFormatDateTime(date, DateUtil.MMddChineseFormat));
            date = DateUtil.addDays(date, 1);
            for (int j = 0; j < 6; j++) {
                map.put("t" + j, RandomUtils.nextInt(0, 999) + "");
            }
            Map<String, Object> map1 = new HashMap<>();
            for (int k = 0; k < 6; k++) {
                map1.put("k" + k, RandomUtils.nextInt(0, 999) + "");
            }
            data1.add(map);
            data2.add(map1);
        }

        Date date2 = new Date();

        for (int i = 0; i < 30; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", DateUtil.getFormatDateTime(date2, "yyyy/M/d"));
            date2 = DateUtil.addDays(date2, 1);
            for (int j = 0; j < 3; j++) {
                Map map1 = new HashMap();
                Map map2 = new HashMap();
                Map map3 = new HashMap();

                for (int k = 0; k < 5; k++) {
                    if (k == 0) {
                        map1.put("k" + k, "夜班");
                    } else {
                        map1.put("k" + k, RandomUtils.nextInt(0, 999) + "");
                    }
                }

                for (int k = 0; k < 5; k++) {
                    if (k == 0) {
                        map2.put("k" + k, "白班");
                    } else {
                        map2.put("k" + k, RandomUtils.nextInt(0, 999) + "");
                    }
                }

                for (int k = 0; k < 5; k++) {
                    if (k == 0) {
                        map3.put("k" + k, "中班");
                    } else {
                        map3.put("k" + k, RandomUtils.nextInt(0, 999) + "");
                    }
                }
                map.put("k", map1);
                map.put("m", map2);
                map.put("n", map3);
            }
            data3.add(map);
            data4.add(map);
            data5.add(map);
        }

        Date date3=new Date();
        for (int i = 0; i < 30; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", DateUtil.getFormatDateTime(date3, "yyyy/M/d"));
            date3 = DateUtil.addDays(date3, 1);

            for (int j = 0; j < 40; j++) {
                RandomUtils.nextInt(0, 999);
                map.put("k" + j, RandomUtils.nextInt(0, 999) + "");
            }
            data6.add(map);
        }


        result.put("data1", data1);
        result.put("data2", data2);
        result.put("data3", data3);
        result.put("data4", data4);
        result.put("data5", data5);
        result.put("data6", data6);
        return result;
    }
}
