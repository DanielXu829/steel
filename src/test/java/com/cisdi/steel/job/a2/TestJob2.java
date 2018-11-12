package com.cisdi.steel.job.a2;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.a2.GanxijiaoJob;
import com.cisdi.steel.module.job.a2.HuachanJob;
import com.cisdi.steel.module.job.a2.LianjiaoJob;
import com.cisdi.steel.module.job.a2.PeimeizuoyequJob;
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
public class TestJob2 extends SteelApplicationTests {

    @Autowired
    private GanxijiaoJob ganxijiaoJob;

    @Test
    public void test1() {
        ganxijiaoJob.execute(null);
    }

    @Autowired
    private HuachanJob huachanJob;

    @Test
    public void test2() {
        huachanJob.execute(null);
    }

    @Autowired
    private LianjiaoJob lianjiaoJob;

    @Test
    public void test3() {
        lianjiaoJob.execute(null);
    }


    @Autowired
    private PeimeizuoyequJob peimeizuoyequJob;

    @Test
    public void test4() {
        peimeizuoyequJob.execute(null);
    }
}
