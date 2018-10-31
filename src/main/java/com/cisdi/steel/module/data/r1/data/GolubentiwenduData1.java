package com.cisdi.steel.module.data.r1.data;

import com.cisdi.steel.module.data.MockDataUtil;
import com.cisdi.steel.module.data.r1.AbstractGlDataExecute;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>Description:    高炉本体温度 日报     </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GolubentiwenduData1 extends AbstractGlDataExecute {

    @Override
    public Map<String, Object> getFirstData() {
        return MockDataUtil.getMockData(9,27,49);
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
