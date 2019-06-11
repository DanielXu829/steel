package com.cisdi.steel.job.a3;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.a3.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class TestJob3 extends SteelApplicationTests {

    @Autowired
    private TuoliuJob5 tuoliuJob5;

    /**
     * 5#脱硫系统运行日报
     */
    @Test
    public void test1() {
        tuoliuJob5.execute(null);
    }

    /**
     * 6#脱硫脱硝工艺参数采集
     */
    @Autowired
    private TuoliuTuoxiaoGongyiJob tuoliuTuoxiaoGongyiJob;

    @Test
    public void test2() {
        tuoliuTuoxiaoGongyiJob.execute(null);
    }

    /**
     * 2018年5烧6烧主抽电耗跟踪表（9月）
     */
    @Autowired
    private ZhuChouWuAndLiuJob zhuChouWuAndLiuJob;

    @Test
    public void test3() {
        zhuChouWuAndLiuJob.execute(null);
    }

    /**
     * 6#脱销运行记录月报
     */
    @Autowired
    private TuoXiaoJob tuoXiaoJob;

    @Test
    public void test4() {
        tuoXiaoJob.execute(null);
    }


    /**
     * 5#烧结机生产日报
     */
    @Autowired
    private JiejiJob5 jieji5Job;

    @Test
    public void test6() {
        jieji5Job.execute(null);
    }

    /**
     * 4小时发布-五烧主要工艺参数及实物质量情况日报
     */
    @Autowired
    private GycanshuJob5 gycanshuJob5;

    @Test
    public void test7() {
        gycanshuJob5.execute(null);
    }

    /**
     * 熔剂燃料质量管控
     */
    @Autowired
    private RongjiJob5 rongjiJob5;

    @Test
    public void test8() {
        rongjiJob5.execute(null);
    }

    /**
     * 烧结机生产月报
     */
    @Autowired
    private JiejiMonthJob5 jiejiMonthJob5;

    @Test
    public void test9() {
        jiejiMonthJob5.execute(null);
    }


    /**
     * 脱硫脱硝年报
     */
    @Autowired
    private TuoliuTuoxiaoYearJob tuoliuTuoxiaoYearJob;

    @Test
    public void test10() {
        tuoliuTuoxiaoYearJob.execute(null);
    }

    /**
     * 烧结分厂主要工艺参数及实物质量情况
     */
    @Autowired
    private GycanshuTotalJob gycanshuTotalJob;

    @Test
    public void test11() {
        gycanshuTotalJob.execute(null);
    }

    /**
     * 烧结公辅环保设施运行情况及在线监测数据发布
     */
    @Autowired
    private HuanbaoJiankongJob huanbaoJiankongJob;

    @Test
    public void test12() {
        huanbaoJiankongJob.execute(null);
    }


    /**
     * 烧结无纸办公通用执行类-工作流水账
     */
    @Autowired
    private GongzuoliushuizhangJob gongzuoliushuizhangJob;

    @Test
    public void test13() {
        gongzuoliushuizhangJob.execute(null);
    }

    /**
     * 烧结无纸办公通用执行类-烧结生产作业区雨季生产记录表
     */
    @Autowired
    private YujishengchanjiluJob yujishengchanjiluJob;

    @Test
    public void test14() {
        yujishengchanjiluJob.execute(null);
    }


    /**
     * 5烧结精益生产管控系统
     */
    @Autowired
    private JingyiJob5 jingyiJob5;

    @Test
    public void test15() {
        jingyiJob5.execute(null);
    }

    /**
     * 6烧结精益生产管控系统
     */
    @Autowired
    private JingyiJob6 jingyiJob6;

    @Test
    public void test151() {
        jingyiJob6.execute(null);
    }

    /**
     * 烧结混合机加水蒸汽预热温度统计表
     */
    @Autowired
    private Gongzuozhengqi5Job gongzuozhengqi5Job;

    @Test
    public void test16() {
        gongzuozhengqi5Job.execute(null);
    }

    /**
     * 缓5料情况记录表
     */
    @Autowired
    private Gongzuohuancun5Job gongzuohuancun5Job;

    @Test
    public void test174() {
        gongzuohuancun5Job.execute(null);
    }

    /**
     * 6缓料情况记录表
     */
    @Autowired
    private Gongzuohuancun6Job gongzuohuancun6Job;

    @Test
    public void test17() {
        gongzuohuancun6Job.execute(null);
    }

    /**
     * 脱硫无纸化
     */
    @Autowired
    private GongzuotuoliuJob gongzuotuoliuJob;

    @Test
    public void test18() {
        gongzuotuoliuJob.execute(null);
    }

    /**
     * 5烧结机生产工艺检查项目表
     */
    @Autowired
    private Gongzuogongyijiancha5Job gongzuogongyijiancha5Job;

    @Test
    public void test19() {
        gongzuogongyijiancha5Job.execute(null);
    }

    /**
     * 5烧结机指标运行记录
     */
    @Autowired
    private ZhibiaoyunxingJob5 zhibiaoyunxingJob5;

    @Test
    public void test20() {
        zhibiaoyunxingJob5.execute(null);
    }

    /**
     * 烧结能源消耗
     */
    @Autowired
    private ShaojieNengyuanxiaohaoJob shaojieNengyuanxiaohaoJob;

    @Test
    public void test21() {
        shaojieNengyuanxiaohaoJob.execute(null);
    }

}
