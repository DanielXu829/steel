package com.cisdi.steel.job.a3;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.a4.GongliaochejianJob;
import com.cisdi.steel.module.job.a4.ShaixiafentongjiDayJob;
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

    /**
     * 1.供料车间集控中心交接班记录
     */
    @Autowired
    private GongliaochejianJob gongliaochejianJob;

    @Test
    public void test1() {
        gongliaochejianJob.execute(null);
    }

    /**
     * 2.筛下粉统计
     */
    @Autowired
    private ShaixiafentongjiDayJob shaixiafentongjiDayJob;

    @Test
    public void test2() {
        shaixiafentongjiDayJob.execute(null);
    }

}
