package com.cisdi.steel.job.a6;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.a6.BF6trtJob;
import com.cisdi.steel.module.job.a6.BF7trtJob;
import com.cisdi.steel.module.job.a6.BF8trtJob;
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
public class TestJob6 extends SteelApplicationTests {

    /**
     * BF6 - TRT 日表.xlsx
     */
    @Autowired
    private BF6trtJob bf6trtJob;

    @Test
    public void test1() {
        bf6trtJob.execute(null);
    }


   /**
     * BF7 - TRT 日表.xlsx
     */
    @Autowired
    private BF7trtJob bf7trtJob;

    @Test
    public void test2() {
        bf7trtJob.execute(null);
    }


   /**
     * BF8 - TRT 日表.xlsx
     */
    @Autowired
    private BF8trtJob bf8trtJob;

    @Test
    public void test3() {
        bf8trtJob.execute(null);
    }



}
