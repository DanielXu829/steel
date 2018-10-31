package com.cisdi.steel.job.r1;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.r1.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Description:    高炉的测试类     </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/25 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class TestJob extends SteelApplicationTests {


    @Autowired
    protected ChutiezuoyeJob1 chutiezuoyeJob1;

    /**
     * 出铁作业
     */
    @Test
    public void test1() {
        chutiezuoyeJob1.execute(null);
    }

    @Autowired
    private ChutiezuoyeJob2 chutiezuoyeJob2;

    /**
     * 出铁作业 月报
     */
    @Test
    public void test2() {
        chutiezuoyeJob2.execute(null);
    }


    @Autowired
    private GaoluludingzhuangliaozuoyeJob1 gaoluludingzhuangliaozuoyeJob1;

    /**
     * 高炉炉顶装料作业 日报
     */
    @Test
    public void test3() {
        gaoluludingzhuangliaozuoyeJob1.execute(null);
    }

    @Autowired
    private GaoluludingzhuangliaozuoyeJob2 gaoluludingzhuangliaozuoyeJob2;

    /**
     * 高炉炉顶装料作业 月报
     */
    @Test
    public void test4() {
        gaoluludingzhuangliaozuoyeJob2.execute(null);
    }


    @Autowired
    private Gaowenlengquebiwendu1 gaowenlengquebiwendu1;

    /**
     * 高炉冷却壁温度 日报
     */
    @Test
    public void test5() {
        gaowenlengquebiwendu1.execute(null);
    }

    @Autowired
    private Gaowenlengquebiwendu2 gaowenlengquebiwendu2;

    /**
     * 高炉冷却壁温度 月报
     */
    @Test
    public void test6() {
        gaowenlengquebiwendu2.execute(null);
    }


    @Autowired
    private JobGaolubentilushenjingya1 jobGaolubentilushenjingya1;

    /**
     * 高炉本体炉身静压 日报
     */
    @Test
    public void test7() {
        jobGaolubentilushenjingya1.execute(null);
    }

    @Autowired
    private JobGaolubentilushenjingya2 jobGaolubentilushenjingya2;

    /**
     * 高炉本体炉身静压 月报
     */
    @Test
    public void test8() {
        jobGaolubentilushenjingya2.execute(null);
    }

    /**
     * 高炉本体温度 日报
     */
    @Autowired
    private JobGaolubentiwendu1 jobGaolubentiwendu1;

    @Test
    public void test9() {
        jobGaolubentiwendu1.execute(null);
    }

    /**
     * 高炉本体温度 月报
     */
    @Autowired
    private JobGaolubentiwendu2 jobGaolubentiwendu2;

    @Test
    public void test10() {
        jobGaolubentiwendu2.execute(null);
    }

    /**
     * JSW高炉 日报
     */
    @Autowired
    private Jobjswgaolu jobjswgaolu;

    @Test
    public void test11() {
        jobjswgaolu.execute(null);
    }

    /**
     * JSW质能平衡计算报表
     */
    @Autowired
    private Jobjswzhinengpinghengjisuan jobjswzhinengpinghengjisuan;

    @Test
    public void test12() {
        jobjswzhinengpinghengjisuan.execute(null);
    }

    /**
     * 热风炉 日报
     */
    @Autowired
    private JobRefenglu1 jobRefenglu1;

    @Test
    public void test13() {
        jobRefenglu1.execute(null);
    }

    /**
     * 热风炉 月报
     */
    @Autowired
    private JobRefenglu2 jobRefenglu2;

    @Test
    public void test14() {
        jobRefenglu2.execute(null);
    }

    /**
     * 台塑1高炉 月报
     */
    @Autowired
    private JobTaisu1Month jobTaisu1Month;

    @Test
    public void test15() {
        jobTaisu1Month.execute(null);
    }
}
