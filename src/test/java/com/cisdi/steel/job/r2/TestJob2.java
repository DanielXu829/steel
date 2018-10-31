package com.cisdi.steel.job.r2;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.r2.JobGanxijiao;
import com.cisdi.steel.module.job.r2.JobHuachan;
import com.cisdi.steel.module.job.r2.JobPeimeizuoyequ;
import com.cisdi.steel.module.job.r2.JobShaojiao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Description:   焦化      </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/26 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class TestJob2 extends SteelApplicationTests {

    @Autowired
    private JobPeimeizuoyequ jobPeimeizuoyequ;

    /**
     * 配煤作业区报表设计
     */
    @Test
    public void test1(){
        jobPeimeizuoyequ.execute(null);
    }

    /**
     * 化产报表设计
     */
    @Autowired
    private JobHuachan jobHuachan;
    @Test
    public void test2(){
        jobHuachan.execute(null);
    }


    @Autowired
    private JobGanxijiao jobGanxijiao;
    @Test
    public void test3(){
        jobGanxijiao.execute(null);
    }


    @Autowired
    private JobShaojiao jobShaojiao;
    @Test
    public void test4(){
        jobShaojiao.execute(null);
    }

}
