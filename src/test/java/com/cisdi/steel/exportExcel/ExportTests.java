package com.cisdi.steel.exportExcel;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.common.constant.Constants;
import com.cisdi.steel.common.resp.ResponseUtil;
import com.cisdi.steel.common.resp.TestData;
import com.cisdi.steel.common.util.DateUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * excel导出测试类
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/22 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class ExportTests extends SteelApplicationTests {

    /**
     * 导出模板测试 1
     */
    @Test
    public void exportExcel1() throws Exception {
        String url = Constants.API_URL + "/tagValues?tagname=tcSkullThickB203&starttime=1519034779&endtime=1529034779060";
        String s = httpUtil.get(url, null);
        List<TestData> responseArray = ResponseUtil.getResponseArray(s, TestData.class);
        TemplateExportParams params = new TemplateExportParams("excel/demo.xlsx");
        Map<String, Object> map = new HashMap<>();
        map.put("test", responseArray);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);

        Date date = new Date();
        String year = DateUtil.getFormatDateTime(date, DateUtil.yyyyFormat);
        String month = DateUtil.getFormatDateTime(date, DateUtil.MMFormat);
        String day = DateUtil.getFormatDateTime(date, DateUtil.ddFormat);

        String fileName = DateUtil.getFormatDateTime(date, DateUtil.NO_SEPARATOR);

        File saveFile = new File("D:/excel/" + year + "/" + month + "/" + day);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream("D:/excel/" + year + "/" + month + "/" + day + "/" + "demo" + fileName + ".xlsx");
        workbook.write(fos);
        fos.close();
    }

    /**
     * 导出模板测试 2
     */
    @Test
    public void exportExcel2() throws Exception {
        TemplateExportParams params = new TemplateExportParams("excel/demo1.xlsx");
        params.setScanAllsheet(true);
        params.setDataSheetNum(1);
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 1; i < 25; i++) {
            Map<String, String> map2 = new HashMap<>();
            map2.put("id", RandomUtils.nextInt(0, 9) + "");
            for (int j = 1; j < 20; j++) {
                map2.put("t" + j, RandomUtils.nextInt(0, 99) + "");
            }
            list.add(map2);
        }
        Map<String, String> map2 = new HashMap<>();
        list.add(map2);
        map.put("test", list);
        map.put("date", DateUtil.getFormatDateTime(new Date(), DateUtil.yyyyMMddChineseFormat));
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        workbook.setForceFormulaRecalculation(true);
        String fileName = "demo1";
        File saveFile = new File("D:/excel/");
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream("D:/excel/" + fileName + ".xlsx");
        workbook.write(fos);
        fos.close();
    }

    /**
     * 修改模板位置
     */
    @Test
    public void exportExcel3() throws Exception {
        TemplateExportParams params = new TemplateExportParams("/root/template/demo1.xlsx");
        params.setScanAllsheet(true);
        params.setDataSheetNum(1);
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 1; i < 25; i++) {
            Map<String, String> map2 = new HashMap<>();
            map2.put("id", RandomUtils.nextInt(0, 9) + "");
            for (int j = 1; j < 20; j++) {
                map2.put("t" + j, RandomUtils.nextInt(0, 99) + "");
            }
            list.add(map2);
        }
        Map<String, String> map2 = new HashMap<>();
        list.add(map2);
        map.put("test", list);
        map.put("date", DateUtil.getFormatDateTime(new Date(), DateUtil.yyyyMMddChineseFormat));
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        workbook.setForceFormulaRecalculation(true);
        String fileName = "demo1";
        File saveFile = new File("D:/excel/");
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream("/root/template" + fileName + ".xlsx");
        workbook.write(fos);
        fos.close();
    }
}
