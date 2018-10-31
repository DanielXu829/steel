package com.cisdi.steel.module.data;

import org.apache.commons.lang3.RandomUtils;

import java.util.*;

/**
 * <p>Description:  模拟数据的生成 临时   </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/25 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class MockDataUtil {

    /**
     * 获取模拟的数据
     *
     * @param kSize 几个tag1
     * @param iSize 多少列
     * @param jSize 每列多少个数据
     * @return 结果
     */
    public static Map<String, Object> getMockData(int kSize, int iSize, int jSize) {
        Map<String, Object> map = new HashMap<>();
        for (int k = 1; k <= kSize; k++) {
            List<Map<String, String>> list1 = new ArrayList<>();
            for (int i = 1; i <= iSize; i++) {
                Map<String, String> map2 = new HashMap<>(jSize);
                for (int j = 0; j <= jSize; j++) {
                    map2.put("t" + j, RandomUtils.nextInt(0, 999) + "");
                }
                list1.add(map2);
            }
            map.put("data" + k, list1);
        }
        return map;
    }
}
