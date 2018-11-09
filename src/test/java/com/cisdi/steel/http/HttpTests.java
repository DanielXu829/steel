package com.cisdi.steel.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.resp.ResponseUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.job.dto.CellData;
import org.apache.poi.ss.usermodel.*;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

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

        String url = httpProperties.getUrlApiGLOne() + "/batchenos/period";
        DateQuery dateQuery = DateQueryUtil.buildToday();
        String s = httpUtil.get(url, dateQuery.getQueryParam());
        List<String> list = ResponseUtil.getResponseArray(s, String.class);
        for (int i = 0; i < 10; i++) {
            String s1 = list.get(i);
            String detail = httpProperties.getUrlApiGLOne() + "/batch/" + s1;
            String detailData = httpUtil.get(detail);
            if (StringUtils.isNotBlank(detailData)) {
                Map<String, Object> mapType = JSON.parseObject(detailData, Map.class);
                System.err.println(mapType);
            }

        }
    }

    @Test
    public void testApi2() throws Exception {
        String url = httpProperties.getUrlApiGLOne() + "/batchenos/period";
        DateQuery dateQuery = DateQueryUtil.buildToday();
        String s = httpUtil.get(url, dateQuery.getQueryParam());
        List<String> list = ResponseUtil.getResponseArray(s, String.class);
        Collections.sort(list);
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String s1 = list.get(i);
            String detail = httpProperties.getUrlApiGLOne() + "/batch/" + s1;
            String detailData = httpUtil.get(detail);
            if (StringUtils.isNotBlank(detailData)) {
                Map<String, Object> mapType = JSON.parseObject(detailData, Map.class);
                result.add(mapType);
            }
        }
        String templatePath = "D:\\炉顶料日报表.xlsx";
        String savePath = "D:\\1.xlsx";
        Workbook workbook = WorkbookFactory.create(new File(templatePath));
        int numberOfSheets = workbook.getNumberOfSheets();

        Map<Integer, Sheet> sheets = new HashMap<>();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            if (sheetName.contains("_")) {
                sheets.put(i, sheet);
                workbook.setSheetHidden(i, Workbook.SHEET_STATE_HIDDEN);
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                List<CellData> resultData = new ArrayList<>();
                int starRow = 0;
                for (int k = 0; k < result.size(); k++) {
                    Map<String, Object> data = result.get(k);
                    {
                        // 每一行的数据
                        JSONObject rowData = (JSONObject) data.get("data");
                        int size = columns.size();
                        // 存储每一个
                        for (int columnIndex = 0; columnIndex < size; columnIndex++) {
                            String column = columns.get(columnIndex);
                            if (!column.contains("/")) {
                                Object value = rowData.get(column);
                                resultData.add(new CellData(starRow, columnIndex, Objects.isNull(value) ? "" : value));
                            } else {
                                String[] split = column.split("/");
                                String key = split[0];
                                String keyChild = split[1].toLowerCase();
                                Object o = rowData.get(key);
                                if (o instanceof JSONObject) {
                                    JSONObject object = (JSONObject) o;
                                    Object value = object.get(keyChild);
                                    resultData.add(new CellData(starRow, columnIndex, Objects.isNull(value) ? "" : value));
                                } else if (o instanceof JSONArray) {
                                    JSONArray jsonArray = (JSONArray) o;
                                    int childRow = starRow;
                                    int jsonSize = jsonArray.size();
                                    for (int l = 0; l < jsonSize; l++) {
                                        JSONObject item = (JSONObject) jsonArray.get(l);
                                        Object value = item.get(keyChild);
                                        resultData.add(new CellData(childRow++, columnIndex, Objects.isNull(value) ? "" : value));
                                    }
                                }
                            }
                        }
                        starRow += 6;
                        Collections.sort(resultData);
                    }
                }
                Collections.sort(resultData);
                resultData.forEach(cellData -> {
                    int rowNum = cellData.getRowIndex();
                    rowNum++;
                    Row row = sheet.getRow(rowNum);
                    if (Objects.isNull(row)) {
                        row = sheet.createRow(rowNum);
                    }
                    Integer column = cellData.getColumnIndex();
                    Cell cell = row.getCell(column);
                    if (Objects.isNull(cell)) {
                        cell = row.createCell(column);
                    }
                    PoiCustomUtil.setCellValue(cell, cellData.getCellValue());
                });
            }
        }
        FileOutputStream fos = new FileOutputStream(savePath);
        workbook.setForceFormulaRecalculation(true);
        workbook.write(fos);
        fos.close();
    }

}

