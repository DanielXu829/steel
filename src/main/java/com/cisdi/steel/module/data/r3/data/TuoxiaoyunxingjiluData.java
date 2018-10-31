package com.cisdi.steel.module.data.r3.data;

import com.cisdi.steel.module.data.r3.AbstractSjDataExecute;
import com.cisdi.steel.module.data.util.DataUtil;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>Description: 脱硝运行记录表         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/29 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class TuoxiaoyunxingjiluData extends AbstractSjDataExecute {
    @Override
    public Map<String, Object> getFirstData() {
        int currentMonthLastDay = DataUtil.getCurrentMonthLastDay();
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        int index=1;
        for (int i = 1; i <= currentMonthLastDay*3; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", DataUtil.getMonthDay(new Date(), index++));
            map.put("a1", RandomUtils.nextInt(1, 999));
            map.put("a2", RandomUtils.nextInt(1, 999));
            map.put("a3", RandomUtils.nextInt(1, 999));

            map.put("b1", RandomUtils.nextInt(1, 999));
            map.put("b2", RandomUtils.nextInt(1, 999));
            map.put("b3", RandomUtils.nextInt(1, 999));


            map.put("c1", RandomUtils.nextInt(1, 999));
            map.put("c2", RandomUtils.nextInt(1, 999));
            map.put("c3", RandomUtils.nextInt(1, 999));

            for (int j = 0; j < 7; j++) {
                map.put("detail" + j, getDetails());
            }

            result.add(map);
        }
        resultMap.put("data1", result);
        return resultMap;
    }

    public Map<String, Object> getDetails() {
        Map<String, Object> map = new HashMap<>();
        for (int j = 0; j < 10; j++) {
            map.put("t" + j, RandomUtils.nextInt(1, 999));
        }
        return map;
    }

}
