package com.cisdi.steel.job.a2;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.a2.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 焦化
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class TestJob2 extends SteelApplicationTests {

    /**
     * 干熄焦
     */
    @Autowired
    private GanxijiaoJob ganxijiaoJob;

    @Test
    public void test1() {
        ganxijiaoJob.execute(null);
    }

    /**
     * 化产
     */
    @Autowired
    private HuachanJob huachanJob;

    @Test
    public void test2() {
        huachanJob.execute(null);
    }

    /**
     * 炼焦
     */
    @Autowired
    private LianjiaoJob lianjiaoJob;

    @Test
    public void test3() {
        lianjiaoJob.execute(null);
    }

    /**
     * 配煤
     */
    @Autowired
    private PeimeizuoyequJob peimeizuoyequJob;

    @Test
    public void test4() {
        peimeizuoyequJob.execute(null);
    }

    /**
     * 炼焦炉温
     */
    @Autowired
    private LianjiaoWDJob lianjiaoWDJob;

    @Test
    public void test5() {
        lianjiaoWDJob.execute(null);
    }

    /**
     * 自动配煤
     */
    @Autowired
    private ZidongpeimeiJob zidongpeimeiJob;

    @Test
    public void test6() {
        zidongpeimeiJob.execute(null);
    }

    /**
     * 粉碎细度
     */
    @Autowired
    private FensuixiduJob fensuixiduJob;

    @Test
    public void test7() {
        fensuixiduJob.execute(null);
    }

    /**
     * CDQ操作运行
     */
    @Autowired
    private CDQcaozuoJob cdQcaozuoJob;

    @Test
    public void test8() {
        cdQcaozuoJob.execute(null);
    }

    /**
     * 出焦除尘
     */
    @Autowired
    private ChujiaochuchenJob chujiaochuchenJob;

    @Test
    public void test9() {
        chujiaochuchenJob.execute(null);
    }

    /**
     * 装煤除尘
     */
    @Autowired
    private ZhuangmeichuchenJob zhuangmeichuchenJob;

    @Test
    public void test10() {
        zhuangmeichuchenJob.execute(null);
    }

    /**
     * CDQ除尘
     */
    @Autowired
    private CDQchuchenJob cdQchuchenJob;

    @Test
    public void test11() {
        cdQchuchenJob.execute(null);
    }


    /**
     * 筛焦除尘
     */
    @Autowired
    private ShaijiaochuchenJob shaijiaochuchenJob;

    @Test
    public void test12() {
        shaijiaochuchenJob.execute(null);
    }


    /**
     * 鼓风冷凝
     */
    @Autowired
    private GufenglengningJob gufenglengningJob;

    @Test
    public void test13() {
        gufenglengningJob.execute(null);
    }


    /**
     * 鼓风冷凝
     */
    @Autowired
    private ZhilengxunhuanshuiJob zhilengxunhuanshuiJob;

    @Test
    public void test14() {
        zhilengxunhuanshuiJob.execute(null);
    }

    /**
     * 蒸氨
     */
    @Autowired
    private ZhenganJob zhenganJob;

    @Test
    public void test15() {
        zhenganJob.execute(null);
    }

    /**
     * 硫铵
     */
    @Autowired
    private LiuanJob liuanJob;

    @Test
    public void test16() {
        liuanJob.execute(null);
    }

    /**
     * 粗苯蒸馏
     */
    @Autowired
    private ChubenzhengliuJob chubenzhengliuJob;

    @Test
    public void test17() {
        chubenzhengliuJob.execute(null);
    }

    /**
     * 终冷洗苯
     */
    @Autowired
    private ZhonglengxibenJob zhonglengxibenJob;

    @Test
    public void test18() {
        zhonglengxibenJob.execute(null);
    }

    /**
     * 脱硫解吸
     */
    @Autowired
    private TuoliujiexiJob tuoliujiexiJob;

    @Test
    public void test19() {
        tuoliujiexiJob.execute(null);
    }


    /**
     * 制酸操作
     */
    @Autowired
    private ZhisuancaozuoJob zhisuancaozuoJob;

    @Test
    public void test20() {
        zhisuancaozuoJob.execute(null);
    }


    /**
     * 炼焦日报
     */
    @Autowired
    private LianjiaoribaoJob lianjiaoribaoJob;

    @Test
    public void test21() {
        lianjiaoribaoJob.execute(null);
    }


    /**
     * 6#焦炉加热
     */
    @Autowired
    private Jiaolujiare6Job jiaolujiare6Job;

    @Test
    public void test22() {
        jiaolujiare6Job.execute(null);
    }

    /**
     * 7#焦炉加热
     */
    @Autowired
    private Jiaolujiare7Job jiaolujiare7Job;

    @Test
    public void test23() {
        jiaolujiare7Job.execute(null);
    }

    /**
     * 6#炉温记录
     */
    @Autowired
    private Luwenjilu6Job luwenjilu6Job;

    @Test
    public void test24() {
        luwenjilu6Job.execute(null);
    }

    /**
     * 7#炉温记录
     */
    @Autowired
    private Luwenjilu7Job luwenjilu7Job;

    @Test
    public void test25() {
        luwenjilu7Job.execute(null);
    }
}
