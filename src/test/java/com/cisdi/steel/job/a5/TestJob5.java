package com.cisdi.steel.job.a5;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.a5.execute.FourkongDayExecute;
import com.cisdi.steel.module.job.a5.execute.NewOnekongDayExecute;
import com.cisdi.steel.module.job.a5.execute.ThreekongDayExecute;
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

    /**
     * 三空压站运行记录表.xlsx
     */
    @Autowired
    private ThreekongDayExecute threekongDayExecute;

    @Test
    public void test2() {
        dateQuery.setStartTime(new Date(1541088000000L));
        dateQuery.setEndTime(new Date(1541433600000L));
        threekongDayExecute.execute(JobEnum.nj_threekong, JobExecuteEnum.automatic, dateQuery);
    }

    /**
     * 四空压站运行记录表
     */
    @Autowired
    private FourkongDayExecute fourkongDayExecute;

    @Test
    public void test3() {
        dateQuery.setStartTime(new Date(1541088000000L));
        dateQuery.setEndTime(new Date(1541433600000L));
        fourkongDayExecute.execute(JobEnum.nj_fourkong, JobExecuteEnum.automatic, dateQuery);
    }

    /**
     * 新一空压站运行记录
     */
    @Autowired
    private NewOnekongDayExecute newOnekongDayExecute;

    @Test
    public void test4() {
        dateQuery.setStartTime(new Date(1541088000000L));
        dateQuery.setEndTime(new Date(1541433600000L));
        newOnekongDayExecute.execute(JobEnum.nj_xinyikong, JobExecuteEnum.automatic, dateQuery);
    }
}
