package com.cisdi.steel.job.gl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.*;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestJobGl extends SteelApplicationTests {

    @Autowired
    private ReportIndexMapper reportIndexMapper;

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

    @Autowired
    private ShangLiaoZhuangLiao8Job shangLiaoZhuangLiao8Job;

    @Autowired
    private BuLiaoZhiDuBianDongJiZai8Job buLiaoZhiDuBianDongJiZai8Job;

    @Autowired
    private LuLiaoXiaoHao8Job luLiaoXiaoHao8Job;

    @Autowired
    private KaoHeYueBaoJob kaoHeYueBaoJob;

    @Autowired
    private JiShuJingJi8Job jiShuJingJi8Job;

    @Autowired
    private CaoZuoGuanLiRiJiJob caoZuoGuanLiRiJiJob;

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
    public void testShangLiaoZhuangLiao8Job() {
        shangLiaoZhuangLiao8Job.execute(null);
    }

    @Test
    public void testBuLiaoZhiDuBianDongJiZai8Job() {
        buLiaoZhiDuBianDongJiZai8Job.execute(null);
    }

    @Test
    public void testLuLiaoXiaoHao8Job() {
        luLiaoXiaoHao8Job.execute(null);
    }

    /**
     * 测试 8高炉考核月报表
     */
    @Test
    public void testKaoHeYueBao() {
        kaoHeYueBaoJob.execute(null);
    }

    /**
     * 测试 8高炉操作管理日记
     */
    @Test
    public void testCaoZuoGuanLiRiJiJob() {
        caoZuoGuanLiRiJiJob.execute(null);
    }

    /**
     * 测试 8高炉考核月报表
     */
    @Test
    public void testJiShuJingJi8Job() {
        jiShuJingJi8Job.execute(null);
    }


    @Test
    public void testAllGaoLuNew() {
        testShangLiaoZhuangLiao8Job();
        testBuLiaoZhiDuBianDongJiZai8Job();
        testLuLiaoXiaoHao8Job();
        testKaoHeYueBao();
        testJiShuJingJi8Job();
        testCaoZuoGuanLiRiJiJob();
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
