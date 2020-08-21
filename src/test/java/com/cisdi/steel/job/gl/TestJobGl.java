package com.cisdi.steel.job.gl;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.job.gl.*;
import com.cisdi.steel.module.job.gl.doc.GaoLuRiFenXiBaoGao;
import com.cisdi.steel.module.job.gl.doc.GaoLuRiFenXiBaoGao7;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

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
    private ShangLiaoZhuangLiao7Job shangLiaoZhuangLiao7Job;

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

    @Autowired
    private CaoZuoGuanLiRiJi7Job caoZuoGuanLiRiJi7Job;

    @Autowired
    private LuDiWenDu8Job luDiWenDu8Job;

    @Autowired
    private LengQueBiYueBaoJob lengQueBiYueBaoJob;

    @Autowired
    private YueBaoHuiZong8Job yueBaoHuiZong8Job;

    @Autowired
    private YueBaoHuiZong7Job yueBaoHuiZong7Job;

    @Autowired
    private XiuFengTongJi8Job xiuFengTongJi8Job;

    @Autowired
    private XiuFengTongJi7Job xiuFengTongJi7Job;

    @Autowired
    private BanChanRanLiaoBi7Job banChanRanLiaoBi7Job;

    @Autowired
    private BanChanRanLiaoBi8Job banChanRanLiaoBi8Job;

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
//        shangLiaoZhuangLiao8Job.execute(null);
        shangLiaoZhuangLiao7Job.execute(null);
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
//        caoZuoGuanLiRiJi7Job.execute(null);
        caoZuoGuanLiRiJiJob.execute(null);
    }

    /**
     * 测试 8高炉月报汇总
     */
    @Test
    public void testYueBaoHuiZong8Job() {
        yueBaoHuiZong8Job.execute(null);
//        yueBaoHuiZong7Job.execute(null);
    }

    /**
     * 测试 8高炉考核月报表
     */
    @Test
    public void testJiShuJingJi8Job() {
        jiShuJingJi8Job.execute(null);
    }

    /**
     * 测试 7、8高炉休风统计
     */
    @Test
    public void testXiuFengTongJiJob() {
        xiuFengTongJi7Job.execute(null);
        xiuFengTongJi8Job.execute(null);
    }

    //banChanRanLiaoBi8Job
    /**
     * 测试 7、8高炉班产燃料比
     */
    @Test
    public void testBanChanRanLiaoBiJob() {
        banChanRanLiaoBi7Job.execute(null);
//        banChanRanLiaoBi8Job.execute(null);
    }

    /**
     * 测试 8高炉鱼雷罐装载率
     */
    @Autowired
    private YuLeiGuanZhuangZaiLv8Job yuLeiGuanZhuangZaiLv8Job;
    @Autowired
    private YuLeiGuanZhuangZaiLv7Job yuLeiGuanZhuangZaiLv7Job;
    @Test
    public void testYuLeiGuanZhuangZaiLv8Job() {
        yuLeiGuanZhuangZaiLv7Job.execute(null);
        yuLeiGuanZhuangZaiLv8Job.execute(null);
    }

    /**
     * 测试 8高炉硅硫双命中率
     */
    @Autowired
    private GuiLiuShuangMingZhongLv8Job guiLiuShuangMingZhongLv8Job;
    @Autowired
    private GuiLiuShuangMingZhongLv7Job guiLiuShuangMingZhongLv7Job;
    @Test
    public void testGuiLiuShuangMingZhongLv8Job() {
        guiLiuShuangMingZhongLv7Job.execute(null);
        guiLiuShuangMingZhongLv8Job.execute(null);
    }

    /**
     * 测试 8高炉炉温合格率精益统计表
     */
    @Autowired
    private LuWenHeGeLv8Job luWenHeGeLv8Job;
    @Autowired
    private LuWenHeGeLv7Job luWenHeGeLv7Job;
    @Test
    public void testLuWenHeGeLv8Job() {
        luWenHeGeLv8Job.execute(null);
        luWenHeGeLv7Job.execute(null);
    }

    /**
     * 测试 8高炉铁水一级品率精益统计表
     */
    @Autowired
    private TieShuiYiJiPinLv8Job tieShuiYiJiPinLv8Job;

    @Autowired
    private TieShuiYiJiPinLv7Job tieShuiYiJiPinLv7Job;

    @Test
    public void testTieShuiYiJiPinLv8Job() {
        tieShuiYiJiPinLv7Job.execute(null);
        tieShuiYiJiPinLv8Job.execute(null);
    }

    /**
     * 测试 8高炉炉渣碱度合格率精益统计表
     */
    @Autowired
    private LuZhaJianDuHeGeLv8Job luZhaJianDuHeGeLv8Job;
    @Autowired
    private LuZhaJianDuHeGeLv7Job luZhaJianDuHeGeLv7Job;
    @Test
    public void testLuZhaJianDuHeGeLv8Job() {
        luZhaJianDuHeGeLv7Job.execute(null);
        luZhaJianDuHeGeLv8Job.execute(null);
    }

    /**
     * 测试 8高炉有效出铁比率及出渣比率
     */
    @Autowired
    private ChuTieXiaoLv8Job chuTieXiaoLv8Job;
    @Autowired
    private ChuTieXiaoLv7Job chuTieXiaoLv7Job;
    @Test
    public void testChuTieXiaoLv8Job() {
//        chuTieXiaoLv7Job.execute(null);
        chuTieXiaoLv8Job.execute(null);
    }

    @Test
    public void testBuildYearEach() {
        Date current = new Date();
        List<DateQuery> dateQueries = DateQueryUtil.buildYearDayWithThur2Wed(current);
        System.out.println("本年的到今天截止的所有天：" + dateQueries);

        System.out.println("本年的第一天：" + DateQueryUtil.getYearStartTime(current));
        System.out.println("本年的第一天的开始时间：" + DateUtil.getDateBeginTime(DateQueryUtil.getYearStartTime(current)));
        System.out.println("本年的第一天是星期几？：" + DateUtil.getWeekString(DateQueryUtil.getYearStartTime(current)));

        System.out.println("本年的最后一天：" + DateQueryUtil.getYearEndTime(current));
        System.out.println("本年的最后一天是星期几？：" + DateUtil.getWeekString(DateQueryUtil.getYearEndTime(current)));
    }

    @Test
    public void testAllGaoLuNew() {
        testShangLiaoZhuangLiao8Job();
        testBuLiaoZhiDuBianDongJiZai8Job();
        testLuLiaoXiaoHao8Job();
        testKaoHeYueBao();
        testJiShuJingJi8Job();
        testCaoZuoGuanLiRiJiJob();
        testYueBaoHuiZong8Job();
        testXiuFengTongJiJob();
    }

    @Test
    public void testAll() {
        test1();
        test2();
        test3();
        test4();
        test5();
    }

    @Autowired
    private GaoLuRiFenXiBaoGao7 gaoLuRiFenXiBaoGao7;

    @Autowired
    private GaoLuRiFenXiBaoGao gaoLuRiFenXiBaoGao8;

    // 测试日分析
    @Test
    public void testRifenxi() {
        gaoLuRiFenXiBaoGao7.mainTask();
        gaoLuRiFenXiBaoGao8.mainTask();
    }

    @Autowired
    private TRTGongYiNengHaoTongJi8Job trtGongYiNengHaoTongJi8Job;

    @Test
    public void testTRTGongYINengHao() {
        trtGongYiNengHaoTongJi8Job.execute(null);
    }

    @Autowired
    private TRTJiXieJianKong8Job trtJiXieJianKong8Job;

    @Test
    public void testTRTJiXieJianKong() {
        trtJiXieJianKong8Job.execute(null);
    }
}
