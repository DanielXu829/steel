package com.cisdi.steel.module.data.r1.data;

import com.cisdi.steel.module.data.r1.AbstractGlDataExecute;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>Description:  出铁作业 月报表 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ChutiezuoyeData2 extends AbstractGlDataExecute {

    @Override
    public Map<String, Object> getFirstData() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> list1 = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            Map<String, String> map2 = new HashMap<>();
            for (int j = 0; j <= 28; j++) {
                map2.put("t" + j, RandomUtils.nextInt(0, 999) + "");
            }
            list1.add(map2);
        }

        List<Map<String, String>> list2 = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            Map<String, String> map2 = new HashMap<>();
            for (int j = 1; j <= 29; j++) {
                map2.put("t" + j, RandomUtils.nextInt(0, 999) + "");
            }
            list2.add(map2);
        }
        map.put("data1", list1);
        map.put("data2", list2);
        map.put("date", new Date());
        return map;
    }

    @Override
    public Map<String, Object> getTwoData() {
        return getFirstData();
    }

    @Override
    public Map<String, Object> getThreeData() {
        return getFirstData();
    }
}
