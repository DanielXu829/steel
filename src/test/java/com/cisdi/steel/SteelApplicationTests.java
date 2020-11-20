package com.cisdi.steel;


import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.config.http.OkHttpUtil;
import com.cisdi.steel.module.job.config.HttpProperties;


import net.sf.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;




@RunWith(SpringRunner.class)
@SpringBootTest
public class SteelApplicationTests {

    @Autowired
    protected HttpUtil httpUtil;

    @Autowired
    protected HttpProperties httpProperties;




}
