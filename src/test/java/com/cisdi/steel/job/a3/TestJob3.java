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
    private TuoliuJob5 tuoliuJob5;

    /**
     * 5#脱硫系统运行日报
     */
    @Test
    public void test1() {
        tuoliuJob5.execute(null);
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
     * 5#烧结机生产日报
     */
    @Autowired
    private JiejiJob5 jieji5Job;

    @Test
    public void test6() {
        jieji5Job.execute(null);
    }

    /**
     * 4小时发布-五烧主要工艺参数及实物质量情况日报
     */
    @Autowired
    private GycanshuJob5 gycanshuJob5;

    @Test
    public void test7() {
        gycanshuJob5.execute(null);
    }

    /**
     * 熔剂燃料质量管控
     */
    @Autowired
    private RongjiJob5 rongjiJob5;

    @Test
    public void test8() {
        rongjiJob5.execute(null);
    }

    /**
     * 烧结机生产月报
     */
    @Autowired
    private JiejiMonthJob5 jiejiMonthJob5;

    @Test
    public void test9() {
        jiejiMonthJob5.execute(null);
    }


    /**
     * 脱硫脱硝年报
     */
    @Autowired
    private TuoliuTuoxiaoYearJob tuoliuTuoxiaoYearJob;

    @Test
    public void test10() {
        tuoliuTuoxiaoYearJob.execute(null);
    }


}
