package com.cisdi.steel.job.jh;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.jh.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestJh extends SteelApplicationTests {

    @Autowired
    private GxjShengChan5Job gxjShengChan5Job;

    /**
     * 测试“5#干熄焦生产报表”
     */
    @Test
    public void test1() {
        gxjShengChan5Job.execute(null);
    }

    @Autowired
    private GxjShengChan6Job gxjShengChan6Job;

    /**
     * 测试“6#干熄焦生产报表”
     */
    @Test
    public void test2() {
        gxjShengChan6Job.execute(null);
    }

    @Autowired
    private JiaoLuJiaRe9Job jiaoLuJiaRe9Job;

    /**
     * 测试“9#焦炉加热制度”
     */
    @Test
    public void test3() {
        jiaoLuJiaRe9Job.execute(null);
    }

    @Autowired
    private JiaoLuJiaRe10Job jiaoLuJiaRe10Job;

    /**
     * 测试“10#焦炉加热制度”
     */
    @Test
    public void test4() {
        jiaoLuJiaRe10Job.execute(null);
    }

    @Autowired
    private ChuJiaoChuChenJob chuJiaoChuChenJob;

    @Test
    public void test5() {
        chuJiaoChuChenJob.execute(null);
    }

    @Autowired
    private GxjChuChenJob gxjChuChenJob;

    @Test
    public void test6() {
        gxjChuChenJob.execute(null);
    }
}
