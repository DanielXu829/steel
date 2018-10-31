//package com.cisdi.steel.module.test.controller;
//
//import cn.afterturn.easypoi.cache.manager.POICacheManager;
//import cn.afterturn.easypoi.excel.ExcelXorHtmlUtil;
//import cn.afterturn.easypoi.excel.entity.ExcelToHtmlParams;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.ss.usermodel.WorkbookFactory;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.*;
//
///**
// * <p>Description:         </p>
// * <p>email: ypasdf@163.com</p>
// * <p>Copyright: Copyright (c) 2018</p>
// * <P>Date: 2018/10/27 </P>
// *
// * @author leaf
// * @version 1.0
// */
//@Controller
//public class DemoController3 {
//    @RequestMapping("/testoffice")
//    public void testoffice(HttpServletResponse response) throws IOException, InvalidFormatException {
////        InputStream resourceAsStream = DemoController3.class.getClassLoader().getResourceAsStream("excel/demo.xlsx");
//        String str = "F:\\赛迪\\报表 - 12.15日前\\焦化 - 12.15日前\\干熄焦报表设计0926-12.15前.xlsx";
//        ExcelToHtmlParams params = new ExcelToHtmlParams(WorkbookFactory.create(POICacheManager.getFile(str)),true,"yes");
//        response.getOutputStream().write(ExcelXorHtmlUtil.excelToHtml(params).getBytes());
////        return POIReadExcel.readExcelToHtml(resourceAsStream, false);
//    }
//}
