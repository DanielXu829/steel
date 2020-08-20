package com.cisdi.steel.job.sj;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.sj.*;
import com.cisdi.steel.module.job.sj.doc.HuanDuiCaoYeHuiYiJiYaoDocMain;
import com.cisdi.steel.module.job.sj.doc.ShaoJieFenXiDocMain;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestJobSj extends SteelApplicationTests {

    @Autowired
    private ShaoJieShengChan4Job shaoJieShengChanJob4;

    @Autowired
    private DuiChangYunXingTongJiJob duiChangYunXingTongJiJob;

    @Autowired
    private ZuoYeQuShengChanQingKuangJob zuoYeQuShengChanQingKuangJob;

    @Autowired
    private HuanDuiCaoYeHuiYiJiYaoDocMain huanDuiCaoYeHuiYiJiYaoDocMain;

    @Autowired
    private ShaoJieFenXiDocMain shaoJieFenXiDocMain;

    @Autowired
    private MeiZhouChanLiangFenXiJob meiZhouChanLiangFenXiJob;

    @Test
    public void test1() {
        shaoJieShengChanJob4.execute(null);
    }

    @Test
    public void testDuiChangYunXingTongJiJob() {
        duiChangYunXingTongJiJob.execute(null);
    }

    @Test
    public void testZuoYeQuShengChanQingKuangJob() {
        zuoYeQuShengChanQingKuangJob.execute(null);
    }

    @Test
    public void testHuanDuiCaoYeHuiYiJiYaoDocMain() {
        huanDuiCaoYeHuiYiJiYaoDocMain.mainTask();
    }

    @Test
    public void testShaoJieFenXiDocMain() {
        shaoJieFenXiDocMain.mainTask();
    }

    @Test
    public void testMeiZhouChanLiangFenXiJob() {
        meiZhouChanLiangFenXiJob.execute(null);
    }

    @Autowired
    private ShengChanFenXiZhouBaoJob shengChanFenXiZhouBaoJob;

    @Test
    public void testShengChanFenXiZhouBaoJon() {
        shengChanFenXiZhouBaoJob.execute(null);
    }
}
