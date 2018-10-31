package com.cisdi.steel.module.data.r2.data;

import com.cisdi.steel.module.data.r2.AbstractJhDataExecute;
import com.cisdi.steel.module.data.util.DataUtil;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>Description:   炼焦报表设计 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/27 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ShaojiaoData extends AbstractJhDataExecute {
    @Override
    public Map<String, Object> getFirstData() {
        Map<String, Object> map = new HashMap<>();
        Date date = new Date();
        for (int k = 1; k <= 2; k++) {
            List<Map<String, Object>> list1 = new ArrayList<>();

            int currentMonthLastDay = DataUtil.getCurrentMonthLastDay();

            for (int i = 1; i <= currentMonthLastDay; i++) {
                Map<String, Object> map2 = new HashMap<>(6);
                map2.put("date", DataUtil.getMonthDay(date,i));
                for (int j = 2; j <= 40; j++) {
                    map2.put("t" + j, RandomUtils.nextInt(0, 99) + "");
                }
                list1.add(map2);
            }

            map.put("data" + k, list1);
        }
        List<String> dayHourString = DataUtil.getDayHourString();
        int size = dayHourString.size();
        for(int i=0;i<size;i++){
            map.put("time" + (i + 1), dayHourString.get(i));
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
