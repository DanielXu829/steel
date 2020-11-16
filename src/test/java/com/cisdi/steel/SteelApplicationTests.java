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
    @Test
    public void test(){
        HashMap<String,Object> map = new HashMap<String,Object>();
        String[] arr = {"BF2_L2M_ChargeRate_1h_avg"};
        map.put("tagnames",arr);
        map.put("starttime","1605286800000");
        map.put("endtime","1605369600000");
        JSONObject jsonObject = JSONObject.fromObject(map);
        String str1 = jsonObject.toString();
        String result = OkHttpUtil.postJsonParams("http://119.84.70.208:92/bf2/getTagValues/tagNamesInRange/report",str1);
        System.out.println(result);
    }
}
