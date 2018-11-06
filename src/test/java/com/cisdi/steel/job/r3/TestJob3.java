package com.cisdi.steel.job.r3;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.r3.JobTuoliu;
import com.cisdi.steel.module.job.r3.JobTuoliutuoxiaogongyicaiji;
import com.cisdi.steel.module.job.r3.JobTuoxiaoyunxingjilu;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/29 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class TestJob3 extends SteelApplicationTests {

    @Autowired
    private JobTuoxiaoyunxingjilu jobTuoxiaoyunxingjilu;

    /**
     * 脱硝运行记录表
     */
    @Test
    public void test1() {
        jobTuoxiaoyunxingjilu.execute(null);
    }

    /**
     * 脱硫脱硝工艺参数采集
     */
    @Autowired
    private JobTuoliutuoxiaogongyicaiji jobTuoliutuoxiaogongyicaiji;

    @Test
    public void test2() {
        jobTuoliutuoxiaogongyicaiji.execute(null);
    }


    /**
     * 脱硫
     */
    @Autowired
    private JobTuoliu jobTuoliu;

    @Test
    public void test3() {
        jobTuoliu.execute(null);
    }
}
