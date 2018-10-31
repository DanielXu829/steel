package com.cisdi.steel.module.data.r2.data;

import com.cisdi.steel.module.data.r2.AbstractJhDataExecute;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Description:   配煤作业区报表设计      </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/26 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class PeimeizuoyequData extends AbstractJhDataExecute {
    @Override
    public Map<String, Object> getFirstData() {
        Map<String, Object> map = new HashMap<>();
        for (int k = 1; k <= 9; k++) {
            List<Map<String, String>> list1 = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                Map<String, String> map2 = new HashMap<>(6);
                for (int j = 0; j <= 6; j++) {
                    map2.put("t" + j, RandomUtils.nextInt(0, 99) + "");
                }
                list1.add(map2);
            }
            map.put("data" + k, list1);
        }
        map.put("content", "一些详细情况");
        map.put("change", "某" + RandomStringUtils.randomAlphabetic(2));
        map.put("accept", "某" + RandomStringUtils.randomAlphabetic(2));
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
