package com.cisdi.steel.job.jh;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.jh.GxjShenCan5Job;
import com.cisdi.steel.module.job.jh.GxjShenCan6Job;
import com.cisdi.steel.module.job.jh.JiaoLuJiaRe10Job;
import com.cisdi.steel.module.job.jh.JiaoLuJiaRe9Job;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestJh extends SteelApplicationTests {

    @Autowired
    private GxjShenCan5Job gxjShenCan5Job;

    /**
     * 测试“5#干熄焦生产报表”
     */
    @Test
    public void test1() {
        gxjShenCan5Job.execute(null);
    }

    @Autowired
    private GxjShenCan6Job gxjShenCan6Job;

    /**
     * 测试“6#干熄焦生产报表”
     */
    @Test
    public void test2() {
        gxjShenCan6Job.execute(null);
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
}
