package com.cisdi.steel.job.sj;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.sj.ShaoJieShengChan4Job;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestJobSj extends SteelApplicationTests {

    @Autowired
    private ShaoJieShengChan4Job shaoJieShengChanJob4;

    @Test
    public void test1() {
        shaoJieShengChanJob4.execute(null);
    }
}
