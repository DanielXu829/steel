package com.cisdi.steel.module.data.r2.data;

import com.cisdi.steel.module.data.r2.AbstractJhDataExecute;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>Description:  干熄焦报表设计 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/27 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GanxijiaoData extends AbstractJhDataExecute {
    @Override
    public Map<String, Object> getFirstData() {
        Map<String, Object> map = new HashMap<>();
        for (int k = 1; k <= 16; k++) {
            List<Map<String, String>> list1 = new ArrayList<>();
            for (int i = 0; i <= 23; i++) {
                Map<String, String> map2 = new HashMap<>(6);
                map2.put("t1", i + ": 00");
                for (int j = 2; j <= 72; j++) {
                    map2.put("t" + j, RandomUtils.nextInt(0, 99) + "");
                }
                list1.add(map2);
            }
            map.put("data" + k, list1);
        }
        for(int i=1;i < 40;i++){
            map.put("content"+i, RandomStringUtils.randomAlphabetic(4));
        }
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
