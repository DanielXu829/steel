package com.cisdi.steel.job.a4;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.a4.*;
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

    /**
     * 3.中焦外排记录
     */
    @Autowired
    private ZhongjiaowaipaiMonthJob zhongjiaowaipaiMonthJob;

    @Test
    public void test3() {
        zhongjiaowaipaiMonthJob.execute(null);
    }


    /**
     * 4.煤头外排记录
     */
    @Autowired
    private MeitouwaipaiMonthJob meitouwaipaiMonthJob;

    @Test
    public void test4() {
        meitouwaipaiMonthJob.execute(null);
    }

    @Autowired
    private GongliaochejianMonthJob gongliaochejianMonthJob;

    @Test
    public void test5() {
        gongliaochejianMonthJob.execute(null);
    }


    @Autowired
    private LiaojiaomeiDayJob liaojiaomeiDayJob;

    @Test
    public void test8() {
        liaojiaomeiDayJob.execute(null);
    }


    @Autowired
    private ChengPinCangJob chengPinCangJob;

    @Test
    public void test6() {
        chengPinCangJob.execute(null);
    }


    @Autowired
    private GongliaochejianyichangJob gongliaochejianyichangJob;

    @Test
    public void test7() {
        gongliaochejianyichangJob.execute(null);
    }

    @Autowired
    private ShengchanxiechedegjiJob shengchanxiechedegjiJob;

    @Test
    public void test10(){
        shengchanxiechedegjiJob.execute(null);
    }

    @Autowired
    private JinchangwuziJob jinchangwuziJob;

    @Test
    public void test11(){
        jinchangwuziJob.execute(null);
    }
}
