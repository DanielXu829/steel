package com.cisdi.steel.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * <p>Description: 在项目启动完成后初始化一些参数  </p>
 * <p>email: ypasdf@163.com </p>
 * <p>Copyright: Copyright (c) 2018 </p>
 * <P>Date: 2018/3/25 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
@Slf4j
public class ApplicationConfigure implements CommandLineRunner {


    @Override
    public void run(String... strings) {
        // 预先加载的一些方法，类，属性。
        log.info("运行完成后初始化操作-----");
    }
}