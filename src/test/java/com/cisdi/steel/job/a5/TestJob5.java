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


    @Autowired
    private ThreeFourKongJob threeFourKongJob;

    @Test
    public void test5(){
        threeFourKongJob.execute(null);
    }
}
