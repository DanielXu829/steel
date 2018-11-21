package com.cisdi.steel.job.a3;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.a3.*;
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

    /**
     * 2018年5烧6烧主抽电耗跟踪表（9月）
     */
    @Autowired
    private ZhuChouWuAndLiuJob zhuChouWuAndLiuJob;

    @Test
    public void test3() {
        zhuChouWuAndLiuJob.execute(null);
    }

    /**
     * 6#脱销运行记录月报
     */
    @Autowired
    private TuoXiaoJob tuoXiaoJob;

    @Test
    public void test4() {
        tuoXiaoJob.execute(null);
    }

    /**
     * 6#烧结机生产日报
     */
    @Autowired
    private JiejiJob6 jieji6Job;

    @Test
    public void test5() {
        jieji6Job.execute(null);
    }

    /**
     * 5#烧结机生产日报
     */
    @Autowired
    private JiejiJob5 jieji5Job;

    @Test
    public void test6() {
        jieji5Job.execute(null);
    }
}
