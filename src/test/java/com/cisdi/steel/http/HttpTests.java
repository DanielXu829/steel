package com.cisdi.steel.http;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.common.constant.Constants;
import com.cisdi.steel.common.resp.ResponseUtil;
import com.cisdi.steel.common.resp.TestData;
import org.junit.Test;

import java.util.List;

/**
 * <p>Description:  http请求测试类</p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/22 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class HttpTests extends SteelApplicationTests {

    /**
     * 接口测试 工具类使用
     */
    @Test
    public void testApi() {
        String url = Constants.API_URL + "/tagValues?tagname=tcSkullThickB203&starttime=1519034779&endtime=1529034779060";
        String s = httpUtil.get(url, null);
        String response = ResponseUtil.getResponse(s);
        System.out.println(response);
        System.out.println("2");
        List<TestData> responseArray = ResponseUtil.getResponseArray(s, TestData.class);
        System.err.println(responseArray.get(1));
    }

}

