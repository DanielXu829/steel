package com.cisdi.steel.module.test.service;

import com.cisdi.steel.module.test.entity.Demo;

import java.util.List;

/**
 * <p>Description:  服务类 </p>
 * <P>Date: 2018-10-19 </P>
 *
 * @author leaf
 * @version 1.0
 */
public interface DemoService {
//    void genOfficeFile() throws Exception;

    List<Demo> fileListDirectory(String filePath);
}
