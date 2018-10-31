package com.cisdi.steel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 95765
 */
@SpringBootApplication
@EnableTransactionManagement
//@EnableScheduling
@EnableAsync
public class SteelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SteelApplication.class, args);
    }
}
