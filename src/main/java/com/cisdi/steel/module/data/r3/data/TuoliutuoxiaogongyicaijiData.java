package com.cisdi.steel.module.data.r3.data;

import com.cisdi.steel.module.data.r3.AbstractSjDataExecute;
import com.cisdi.steel.module.data.util.DataUtil;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>Description:  脱硫脱硝工艺参数采集    </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/30 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class TuoliutuoxiaogongyicaijiData extends AbstractSjDataExecute {
    @Override
    public Map<String, Object> getFirstData() {
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        List<Date> dayHour = DataUtil.getDayHour();
        for (Date date : dayHour) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", date);
            for (int j = 1; j < 80; j++) {
                map.put("t" + j, RandomUtils.nextInt(0, 99));
            }

            result.add(map);
        }
        resultMap.put("data1", result);
        return resultMap;
    }
}
