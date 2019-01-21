package com.cisdi.steel.job.a1;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.a1.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/6 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class TestJob1 extends SteelApplicationTests {

    /**
     * 配料单
     */
    @Autowired
    private PeiLiaoDanJob peiLiaoDanJob;

    @Test
    public void test11111() {
        peiLiaoDanJob.execute(null);
    }

    /**
     * 高炉炉顶装料作业 日报
     */
    @Autowired
    private LudingzhuangliaoDayJob ludingDayJob;

    @Test
    public void test1() {
        ludingDayJob.execute(null);
    }

    /**
     * 出铁作业 日
     */
    @Autowired
    private ChutiezuoyeDayJob chutiezuoyeDayJob;

    @Test
    public void test2() {
        chutiezuoyeDayJob.execute(null);
    }

    /**
     * 出铁作业 月
     */
    @Autowired
    private ChutiezuoyeMonthJob chutiezuoyeMonthJob;

    @Test
    public void testa() {
        chutiezuoyeMonthJob.execute(null);
    }

    /**
     * 高炉本体温度日报表.xlsx
     */
    @Autowired
    private BentiwenduDayJob bentiwenduDayJob;

    @Test
    public void test3() {
        bentiwenduDayJob.execute(null);
    }


    /**
     * 高炉本体温度月报表
     */
    @Autowired
    private BentiwenduMonthJob bentiwenduMonthJob;

    @Test
    public void test4() {
        bentiwenduMonthJob.execute(null);
    }

    /**
     * 高炉冷却壁温度日报表
     */
    @Autowired
    private LengquebiwenduDayJob lengquebiwenduDayJob;

    @Test
    public void test5() {
        lengquebiwenduDayJob.execute(null);
    }


    /**
     * 高炉冷却壁温度月报表,
     */
    @Autowired
    private LengquebiwenduMonthJob lengquebiwenduMonthJob;

    @Test
    public void test8() {
        lengquebiwenduMonthJob.execute(null);
    }


    /**
     * 高炉日报表
     */
    @Autowired
    private GaoLuDayJob gaoLuDayJob;

    @Test
    public void test6() {
        gaoLuDayJob.execute(null);
    }


    /**
     * 高炉月报表
     */
    @Autowired
    private GaoLuMonthJob gaoLuMonthJob;

    @Test
    public void test7() {
        gaoLuMonthJob.execute(null);
    }

    /**
     * 炉顶布料
     */
    @Autowired
    private LudingbuliaoJob ludingbuliaoJob;

    @Test
    public void test9() {
        ludingbuliaoJob.execute(null);
    }


    /**
     * 热风炉 日报
     */
    @Autowired
    private RefengluDayJob refengluDayJob;

    @Test
    public void test11() {
        refengluDayJob.execute(null);
    }

    /**
     * 热风炉 月报
     */
    @Autowired
    private ReFengluMonthJob reFengluMonthJob;

    @Test
    public void test10() {
        reFengluMonthJob.execute(null);
    }

    /**
     * 高炉消耗月报表
     */
    @Autowired
    private XiaoHaoDayJob xiaoHaoDayJob;

    @Test
    public void test13() {
        xiaoHaoDayJob.execute(null);
    }

    /**
     * 炉缸温度日报表
     */
    @Autowired
    private LugangWenduDayJob lugangWenduDayJob;

    @Test
    public void test14() {
        lugangWenduDayJob.execute(null);
    }


    /**
     * 重点部位参数监控报表
     */
    @Autowired
    private ZhongdianbuweicanshuJob zhongdianbuweicanshuJob;

    @Test
    public void test15() {
        zhongdianbuweicanshuJob.execute(null);
    }

    /**
     * 高炉图表
     */
    @Autowired
    private ZhongdianbuweicanshuTubiaoJob zhongdianbuweicanshuTubiaoJob;

    @Test
    public void test16() {
        zhongdianbuweicanshuTubiaoJob.execute(null);
    }

    /**
     * 高炉布料
     */
    @Autowired
    private GaolubuliaoJob gaolubuliaoJob;

    @Test
    public void test17() {
        gaolubuliaoJob.execute(null);
    }

    /**
     * 高炉喷煤
     */
    @Autowired
    private GaoLuPenMeiJob gaoLuPenMeiJob;

    @Test
    public void test18() {
        gaoLuPenMeiJob.execute(null);
    }
}
