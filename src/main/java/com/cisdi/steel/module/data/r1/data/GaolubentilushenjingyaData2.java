package com.cisdi.steel.module.data.r1.data;

import com.cisdi.steel.module.data.r1.AbstractGlDataExecute;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>Description:   高炉本体炉身静压 月      </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GaolubentilushenjingyaData2 extends AbstractGlDataExecute {

    @Override
    public Map<String, Object> getFirstData() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> list1 = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            Map<String, String> map2 = new HashMap<>();
            for (int j = 0; j <= 26; j++) {
                map2.put("t" + j, RandomUtils.nextInt(0, 999) + "");
            }
            list1.add(map2);
        }
        map.put("data1", list1);
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
