package com.cisdi.steel.module.data.r1.data;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.data.r1.AbstractGlDataExecute;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>Description:  炉顶作业日报表  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/25 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GaoluludingzhuangliaozuoyeData2 extends AbstractGlDataExecute {
    @Override
    public Map<String, Object> getFirstData() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Map<String, Object> map = new HashMap<>();
            // 批号
            map.put("batchno", i);
            // 富 批号
            map.put("sequnceno", i);
            // 时间
            map.put("endTime", DateUtil.getFormatDateTime(new Date(), DateUtil.hhmmFormat));
            // 类型
            map.put("type", RandomStringUtils.randomAlphabetic(5));
            map.put("brand1", getDetail());
            map.put("brand2", getDetail());
            map.put("brand3", getDetail());
            map.put("brand4", getDetail());
            map.put("brand5", getDetail());
            map.put("brand6", getDetail());
            mapList.add(map);
        }
        result.put("list", mapList);
        result.put("date", new Date());
        return result;
    }

    public Map<String, Object> getDetail() {
        Map<String, Object> map = new HashMap<>();
        for(int i =1;i<=11;i++){
            map.put("a"+i,RandomUtils.nextInt(1,11));
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
