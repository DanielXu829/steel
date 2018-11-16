package com.cisdi.steel.module.test.controller;

import cn.afterturn.easypoi.cache.manager.POICacheManager;
import cn.afterturn.easypoi.excel.ExcelXorHtmlUtil;
import cn.afterturn.easypoi.excel.entity.ExcelToHtmlParams;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/27 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Controller
public class DemoController3 {
    @RequestMapping("/testoffice")
    public void testoffice(HttpServletResponse response) throws IOException, InvalidFormatException {
//        InputStream resourceAsStream = DemoController3.class.getClassLoader().getResourceAsStream("excel/demo.xlsx");
        String str = "C:\\Users\\cj\\Desktop\\高炉本体温度日报表_2018-11-14_16.xlsx";
        ExcelToHtmlParams params = new ExcelToHtmlParams(WorkbookFactory.create(POICacheManager.getFile(str)),true,"yes");
        response.getOutputStream().write(ExcelXorHtmlUtil.excelToHtml(params).getBytes());
//        return POIReadExcel.readExcelToHtml(resourceAsStream, false);
    }
}
