package com.cisdi.steel.job.gl;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.gl.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestJobGl extends SteelApplicationTests {

    @Autowired
    private BianLiaoJiLu8Job bianLiaoJiLu8Job;

    @Autowired
    private PengMeiFengKouDaShi8Job pengMeiFengKouDaShi8Job;

    @Autowired
    private ChuTieHuaXueChengFen8Job chuTieHuaXueChengFen8Job;

    @Autowired
    private LuKuangXiaoShi8Job luKuangXiaoShi8Job;

    @Autowired
    private ShaoJieKuangLiHua8Job shaoJieKuangLiHua8Job;

    @Test
    public void test1() {
        bianLiaoJiLu8Job.execute(null);
    }

    @Test
    public void test2() {
        pengMeiFengKouDaShi8Job.execute(null);
    }

    @Test
    public void test3() {
        chuTieHuaXueChengFen8Job.execute(null);
    }

    @Test
    public void test4() {
        luKuangXiaoShi8Job.execute(null);
    }

    @Test
    public void test5() {
        shaoJieKuangLiHua8Job.execute(null);
    }

    @Test
    public void testAll() {
        test1();
        test2();
        test3();
        test4();
        test5();
    }
}