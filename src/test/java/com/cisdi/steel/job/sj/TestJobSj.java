package com.cisdi.steel.job.sj;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.sj.DuiChangYunXingTongJiJob;
import com.cisdi.steel.module.job.sj.ShaoJieShengChan4Job;
import com.cisdi.steel.module.job.sj.ZuoYeQuShengChanQingKuangJob;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestJobSj extends SteelApplicationTests {

    @Autowired
    private ShaoJieShengChan4Job shaoJieShengChanJob4;

    @Autowired
    private DuiChangYunXingTongJiJob duiChangYunXingTongJiJob;

    @Autowired
    private ZuoYeQuShengChanQingKuangJob zuoYeQuShengChanQingKuangJob;

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
}
