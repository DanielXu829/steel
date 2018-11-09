package com.cisdi.steel.job.a1;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.a1.execute.*;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.enums.JobExecuteEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/6 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class TestJob1 extends SteelApplicationTests {

    private DateQuery dateQuery = DateQueryUtil.buildToday();


    /**
     * 高炉炉顶装料作业 日报
     */
    @Autowired
    private LudingDayExecute ludingDayExecute;

    @Test
    public void test1() {
        ludingDayExecute.execute(JobEnum.gl_ludingzhuangliaozuoye_day1, JobExecuteEnum.automatic, dateQuery);
    }

    /**
     * 出铁作业
     */
    @Autowired
    private ChutiezuoyeDayExecute chutiezuoyeDayExecute;

    @Test
    public void test2() {
        chutiezuoyeDayExecute.execute(JobEnum.gl_chutiezuoye_day, JobExecuteEnum.automatic, dateQuery);
    }

    /**
     * 高炉本体温度日报表.xlsx
     */
    @Autowired
    private BentiwenduDayExecute bentiwenduDayExecute;

    @Test
    public void test3() {
        DateQuery dateQuery = DateQueryUtil.buildHour();
        bentiwenduDayExecute.execute(JobEnum.gl_bentiwendu_day, JobExecuteEnum.automatic, dateQuery);
    }

    /**
     * 高炉冷却壁温度日报表
     */
    @Autowired
    private LengquebiwenduExecute lengquebiwenduExecute;

    @Test
    public void test4() {
        lengquebiwenduExecute.execute(JobEnum.gl_lengquebiwendu_day, JobExecuteEnum.automatic, dateQuery);
    }


    /**
     * 高炉本体温度月报表
     */
    @Autowired
    private BentiwenduMonthExecute bentiwenduMonthExecute;

    @Test
    public void test5() {
        bentiwenduMonthExecute.execute(JobEnum.gl_bentiwendu_month, JobExecuteEnum.automatic, dateQuery);
    }

}
