package com.cisdi.steel.job.a5;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.a5.*;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class TestJob5 extends SteelApplicationTests {

    private DateQuery dateQuery = DateQueryUtil.buildToday();

    /**
     * 二空压站运行记录表.xlsx
     */
    @Autowired
    private TwokongJob twokongJob;

    @Test
    public void test1() {
        twokongJob.execute(null);
    }

    /**
     * 三空压站运行记录表.xlsx
     */
    @Autowired
    private ThreekongJob threekongJob;

    @Test
    public void test2() {
        threekongJob.execute(null);
    }

    /**
     * 四空压站运行记录表
     */
    @Autowired
    private FourkongJob fourkongJob;

    @Test
    public void test3() {
        fourkongJob.execute(null);
    }

    /**
     * 新一空压站运行记录
     */
    @Autowired
    private NewOnekongJob newOnekongJob;

    @Test
    public void test4() {
        newOnekongJob.execute(null);
    }


    /**
     * 三四柜区运行记录表
     */
    @Autowired
    private ThreeFourKongJob threeFourKongJob;

    @Test
    public void test5() {
        threeFourKongJob.execute(null);
    }

    /**
     * 一空压站启停次数表
     */
    @Autowired
    private OnekongCountJob onekongCountJob;

    @Test
    public void test6() {
        onekongCountJob.execute(null);
    }

    /**
     * 二空压站启停次数表
     */
    @Autowired
    private TwokongCountJob twokongCountJob;

    @Test
    public void test7() {
        twokongCountJob.execute(null);
    }

    /**
     * 三空压站启停次数表
     */
    @Autowired
    private ThreekongCountJob threekongCountJob;

    @Test
    public void test8() {
        threekongCountJob.execute(null);
    }

    /**
     * 四空压站启停次数表
     */
    @Autowired
    private FourkongCountJob fourkongCountJob;

    @Test
    public void test9() {
        fourkongCountJob.execute(null);
    }

    /**
     * 压缩空气生产情况汇总表
     */
    @Autowired
    private YasuoKongQiJob yasuoKongQiJob;

    @Test
    public void test10() {
        yasuoKongQiJob.execute(null);
    }

    /**
     *  煤气柜作业区混合煤气情况表
     */
    @Autowired
    private MeiqihunhemeiJob meiqihunhemeiJob;

    @Test
    public void test11() {
        meiqihunhemeiJob.execute(null);
    }

    /**
     *  柜区风机煤压机时间统计表
     */
    @Autowired
    private GuifengjimeiyajiJob guifengjimeiyajiJob;

    @Test
    public void test12() {
        guifengjimeiyajiJob.execute(null);
    }
}
