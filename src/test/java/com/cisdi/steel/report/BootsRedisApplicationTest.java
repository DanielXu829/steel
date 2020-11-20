package com.cisdi.steel.report; /**
 * All rights Reserved, Designed By 溪云阁
 * Copyright:    Copyright(C) 2016-2020
 */



import com.cisdi.steel.common.util.RedisUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;




@RunWith(SpringRunner.class)
@SpringBootTest
public class BootsRedisApplicationTest {

    @Autowired
    private RedisUtils redisUtils;

    @Before
    public void setUp() throws Exception {
        redisUtils.set("aaa", "121212");
        System.out.println("插入值成功");
    }

    /**
     * 执行获取值
     * @author 溪云阁
     * @throws Exception void
     */
    @Test
    public void test() throws Exception {
        System.out.println(redisUtils.get("aaa"));
    }

}
