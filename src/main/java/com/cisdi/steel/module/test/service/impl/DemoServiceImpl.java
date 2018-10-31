//package com.cisdi.steel.module.test.service.impl;
//
//import cn.afterturn.easypoi.excel.ExcelExportUtil;
//import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
//import com.cisdi.steel.common.constant.Constants;
//import com.cisdi.steel.common.resp.ResponseUtil;
//import com.cisdi.steel.common.resp.TestData;
//import com.cisdi.steel.common.util.DateUtil;
//import com.cisdi.steel.common.util.FileUtil;
//import com.cisdi.steel.config.http.HttpUtil;
//import com.cisdi.steel.module.test.entity.Demo;
//import com.cisdi.steel.module.test.service.DemoService;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.net.URL;
//import java.util.*;
//
///**
// * <p>Description:  服务实现类 </p>
// * <P>Date: 2018-10-19 </P>
// *
// * @author leaf
// * @version 1.0
// */
//@Service
//public class DemoServiceImpl implements DemoService {
//
//    @Autowired
//    private HttpUtil httpUtil;
//
//    @Override
//    public void genOfficeFile() throws Exception {
//        String url = Constants.API_URL + "/tagValues?tagname=tcSkullThickB203&starttime=1519034779&endtime=1529034779060";
//        String s = httpUtil.get(url, null);
//        List<TestData> responseArray = ResponseUtil.getResponseArray(s, TestData.class);
//        TemplateExportParams params = new TemplateExportParams("excel/demo.xlsx");
//        Map<String, Object> map = new HashMap<>();
//        map.put("test", responseArray);
//        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
//
//        Date date = new Date();
//        String year = DateUtil.getFormatDateTime(date, DateUtil.yyyyFormat);
//        String month = DateUtil.getFormatDateTime(date, DateUtil.MMFormat);
//        String day = DateUtil.getFormatDateTime(date, DateUtil.ddFormat);
//
//        String fileName = DateUtil.getFormatDateTime(date, DateUtil.NO_SEPARATOR);
//
//        File saveFile = new File("/root/office/" + year + "/" + month + "/" + day);
//        if (!saveFile.exists()) {
//            saveFile.mkdirs();
//        }
//        FileOutputStream fos = new FileOutputStream("/root/office/" + year + "/" + month + "/" + day + "/" + "demo" + fileName + ".xlsx");
//        workbook.write(fos);
//        fos.close();
//        System.out.println("生成路径：：：" + saveFile.getAbsolutePath());
//    }
//
//    @Override
//    public List<Demo> fileListDirectory(String filePath) {
//        List<Demo> list = new ArrayList<>();
//        List<File> files = FileUtil.allFilesChild(new File(filePath));
//        files.forEach(file -> {
//            list.add(new Demo(file.getName(), file.getAbsolutePath()));
//        });
//        return list;
//    }
//}
