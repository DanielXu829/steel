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
     * 炉温管控
     */
    @Autowired
    private LuwenguankongJob luwenguankongJob;
    @Test
    public void test100() {
        luwenguankongJob.execute(null);
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
     * 自动配煤CK12
     */
    @Autowired
    private CK12ZidongpeimeiJob ck12ZidongpeimeiJob;

    @Test
    public void test66() {
        ck12ZidongpeimeiJob.execute(null);
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
     * CDQ操作运行A
     */
    @Autowired
    private CDQcaozuoAJob cdQcaozuoAJob;

    @Test
    public void test8() {
        cdQcaozuoAJob.execute(null);
    }

    /**
     * CDQ操作运行b
     */
    @Autowired
    private CDQcaozuoBJob cdQcaozuoBJob;

    @Test
    public void test82() {
        cdQcaozuoBJob.execute(null);
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
     * 鼓风冷凝1
     */
    @Autowired
    private Gufenglengning1Job gufenglengning1Job;

    @Test
    public void test13() {
        gufenglengning1Job.execute(null);
    }

    /**
     * 鼓风冷凝2
     */
    @Autowired
    private Gufenglengning2Job gufenglengning2Job;

    @Test
    public void test132() {
        gufenglengning2Job.execute(null);
    }

    /**
     * 制冷循环水
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
     * 炼焦月报
     */
    @Autowired
    private LianjiaoyuebaoJob lianjiaoyuebaoJob;

    @Test
    public void test21() {
        lianjiaoyuebaoJob.execute(null);
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


    /**
     * 关键指标
     */
    @Autowired
    private GuanjianzhibiaoJob guanjianzhibiaoJob;

    @Test
    public void test26() {
        guanjianzhibiaoJob.execute(null);
    }

    /**
     * 配煤量月
     */
    @Autowired
    private PeimeiliangJob peimeiliangJob;

    @Test
    public void test27() {
        peimeiliangJob.execute(null);
    }

    /**
     * 指标管控
     */
    @Autowired
    private ZhibiaoguankongJob zhibiaoguankongJob;

    @Test
    public void test28() {
        zhibiaoguankongJob.execute(null);
    }


    /**
     * 主要工艺参数
     */
    @Autowired
    private ZhuyaogycsJob zhuyaogycsJob;

    @Test
    public void test29() {
        zhuyaogycsJob.execute(null);
    }

    /**
     * 产耗综合报表
     */
    @Autowired
    private ChanhaozongheJob chanhaozongheJob;

    @Test
    public void test30() {
        chanhaozongheJob.execute(null);
    }

    /**
     * CK12转运站除尘报表
     */
    @Autowired
    private ZhuanyunzhanChuchenJob zhuanyunzhanChuchenJob;

    @Test
    public void test31() {
        zhuanyunzhanChuchenJob.execute(null);
    }

    /**
     * CK12取样除尘报表
     */
    @Autowired
    private QuyangChuchenJob quyangChuchenJob;

    @Test
    public void test32() {
        quyangChuchenJob.execute(null);
    }

    /**
     * CK45鼓风冷宁报表
     */
    @Autowired
    private CK45GufenglengningJob ck45GufenglengningJob;

    @Test
    public void test33() {
        ck45GufenglengningJob.execute(null);
    }

    /**
     * CK45粗苯1
     */
    @Autowired
    private CK45Cuben1Job ck45Cuben1Job;

    @Test
    public void test34() {
        ck45Cuben1Job.execute(null);
    }

    /**
     * CK45粗苯2
     */
    @Autowired
    private CK45Cuben2Job ck45Cuben2Job;

    @Test
    public void test35() {
        ck45Cuben2Job.execute(null);
    }

    /**
     * CK45余热煤气回收1
     */
    @Autowired
    private CK45Meiqihuishou1Job ck45Meiqihuishou1Job;

    @Test
    public void test36() {
        ck45Meiqihuishou1Job.execute(null);
    }

    /**
     * CK45余热煤气回收2
     */
    @Autowired
    private CK45Meiqihuishou2Job ck45Meiqihuishou2Job;

    @Test
    public void test37() {
        ck45Meiqihuishou2Job.execute(null);
    }

    /**
     * CK45中控操作1
     */
    @Autowired
    private CK45Zkcaozuo1Job ck45Zkcaozuo1Job;

    @Test
    public void test38() {
        ck45Zkcaozuo1Job.execute(null);
    }

    /**
     * CK45中控操作2
     */
    @Autowired
    private CK45Zkcaozuo2Job ck45Zkcaozuo2Job;

    @Test
    public void test39() {
        ck45Zkcaozuo2Job.execute(null);
    }
}
