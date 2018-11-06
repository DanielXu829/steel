package com.cisdi.steel.job.a1;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.job.a1.execute.LudingDayExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.enums.JobExecuteEnum;
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

    @Autowired
    private LudingDayExecute ludingDayExecute;

    private DateQuery dateQuery = DateQueryUtil.buildToday();

    @Test
    public void test1() {
        ludingDayExecute.execute(JobEnum.gaoluludingzhuangliaozuoye_day1, JobExecuteEnum.automatic, dateQuery);
    }

}
