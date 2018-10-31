package com.cisdi.steel.job.r4;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.r4.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class DataWorkTest extends SteelApplicationTests {

    @Autowired
    private YuanliaoGongliaoJob1 gongliaoJob1;

    @Autowired
    private YuanliaoGongliaoJob2 gongliaoJob2;

    @Autowired
    private YuanliaoGongliaoJob3 gongliaoJob3;

    @Autowired
    private YuanliaoGongliaoJob4 gongliaoJob4;

    @Autowired
    private YuanliaoGongliaoJob5 gongliaoJob5;

    @Autowired
    private YuanliaoGongliaoJob6 gongliaoJob6;

    @Autowired
    private YuanliaoGongliaoJob7 gongliaoJob7;

    @Autowired
    private YuanliaoGongliaoJob8 gongliaoJob8;

    @Autowired
    private YuanliaoGongliaoJob9 gongliaoJob9;

    @Autowired
    private YuanliaoGongliaoJob10 gongliaoJob10;

    @Test
    public void yuanliaodataTest() {
        gongliaoJob1.execute(null);
    }

    @Test
    public void test2() {
        gongliaoJob2.execute(null);
    }

    @Test
    public void test3() {
        gongliaoJob3.execute(null);
    }

    @Test
    public void test4() {
        gongliaoJob4.execute(null);
    }

    @Test
    public void test5() {
        gongliaoJob5.execute(null);
    }

    @Test
    public void test6() {
        gongliaoJob6.execute(null);
    }

    @Test
    public void test7() {
        gongliaoJob7.execute(null);
    }

    @Test
    public void test8() {
        gongliaoJob8.execute(null);
    }

    @Test
    public void test9() {
        gongliaoJob9.execute(null);
    }

    @Test
    public void test10() {
        gongliaoJob10.execute(null);
    }
}
