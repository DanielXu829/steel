package com.cisdi.steel.job.a1;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.a1.execute.ChutiezuoyeDayExecute;
import com.cisdi.steel.module.job.a1.execute.LudingDayExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.enums.JobExecuteEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * <p>Description:  </p>
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

    @Autowired
    private ChutiezuoyeDayExecute chutiezuoyeDayExecute;

    @Test
    public void test2() {
        chutiezuoyeDayExecute.execute(JobEnum.gl_chutiezuoye_day, JobExecuteEnum.automatic, dateQuery);
    }

}
