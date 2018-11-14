package com.cisdi.steel.job.a3;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.a3.TuoliuJob;
import com.cisdi.steel.module.job.a3.TuoliuTuoxiaoGongyiJob;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class TestJob3 extends SteelApplicationTests {

    @Autowired
    private TuoliuJob tuoliuJob;

    /**
     * version 5 6
     * 5#脱硫系统运行日报
     * 6#脱硫系统运行日报
     */
    @Test
    public void test1() {
        tuoliuJob.execute(null);
    }


    /**
     * 6#脱硫脱硝工艺参数采集
     */
    @Autowired
    private TuoliuTuoxiaoGongyiJob tuoliuTuoxiaoGongyiJob;

    @Test
    public void test2() {
        tuoliuTuoxiaoGongyiJob.execute(null);
    }
}
