package com.cisdi.steel.job.a5;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.a5.execute.TwokongDayExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.enums.JobExecuteEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

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

    @Autowired
    private TwokongDayExecute twokongDayExecute;

    @Test
    public void test1() {
        dateQuery.setStartTime(new Date(1541088000000L));
        dateQuery.setEndTime(new Date(1541433600000L));
        twokongDayExecute.execute(JobEnum.nj_twokong, JobExecuteEnum.automatic, dateQuery);
    }
}
