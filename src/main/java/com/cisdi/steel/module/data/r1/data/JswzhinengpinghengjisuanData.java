package com.cisdi.steel.module.data.r1.data;

import com.cisdi.steel.module.data.MockDataUtil;
import com.cisdi.steel.module.data.r1.AbstractGlDataExecute;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>Description:   JSW质能平衡计算报表      </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/25 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JswzhinengpinghengjisuanData extends AbstractGlDataExecute {
    @Override
    public Map<String, Object> getFirstData() {
        return MockDataUtil.getMockData(6,15,40);
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
