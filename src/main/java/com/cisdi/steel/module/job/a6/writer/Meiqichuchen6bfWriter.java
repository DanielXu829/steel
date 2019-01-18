package com.cisdi.steel.module.job.a6.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class Meiqichuchen6bfWriter<mapDa> extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                int size = dateQueries.size();
                if("tag".equals(sheetSplit[1])){
                    for (int rowNum = 0; rowNum < size; rowNum++) {
                        DateQuery eachDate = dateQueries.get(rowNum);
                        List<CellData> cellValInfoList = eachData(columns, getUrl(version), eachDate.getQueryParam(), sheetSplit[2]);
                        ExcelWriterUtil.setCellValue(sheet, cellValInfoList);
                    }
                }else if("maxmin".equals(sheetSplit[1])){
                    for (int rowNum = 0; rowNum < size; rowNum++) {

                    }
                }else if("fanchui6".equals(sheetSplit[1])){
                    for (int rowNum = 0; rowNum < size; rowNum++) {

                    }
                }else if("fanchui7".equals(sheetSplit[1])){
                    for (int rowNum = 0; rowNum < size; rowNum++) {

                    }
                }else if("fanchui8".equals(sheetSplit[1])){
                    for (int rowNum = 0; rowNum < size; rowNum++) {

                    }
                }
            }
        }
        return workbook;
    }

    public List<CellData> mapDataHandler(List<String> columns,String url, DateQuery dateQuery, int rowBatch) {
        Map<String, String> queryParam = getQueryParam(dateQuery);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return null;
        }
        JSONArray r = data.getJSONArray("particleDistribution");
        if (Objects.isNull(r) || r.size() == 0) {
            return null;
        }
        int startRow = 1;
        return handlerJsonArray(columns, rowBatch, r, startRow);
    }

    private List<CellData> eachData(List<String> cellList, String url, Map<String, String> queryParam, String type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("starttime", queryParam.get("starttime"));
        jsonObject.put("endtime", queryParam.get("endtime"));
        jsonObject.put("tagnames", cellList);

        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        JSONObject obj = JSONObject.parseObject(result);
        obj = obj.getJSONObject("data");
        List<CellData> resultList = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < cellList.size(); columnIndex++) {
            String cell = cellList.get(columnIndex);
            if (StringUtils.isNotBlank(cell)) {
                JSONObject data = obj.getJSONObject(cell);
                if (Objects.nonNull(data)) {
                    Set<String> keys = data.keySet();
                    Long[] list = new Long[keys.size()];
                    int k = 0;
                    for (String key : keys) {
                        list[k] = Long.valueOf(key);
                        k++;
                    }
                    // 按照顺序排序
                    Arrays.sort(list);
                    if (StringUtils.isNotBlank(type)) {
                        if ("day".equals(type)) {
                            int size = list.length;
                            for (int i = 0; i < size; i++) {
                                Long key = list[i];
                                Date date = new Date(key);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                int rowIndex = calendar.get(Calendar.HOUR_OF_DAY);
                                if (rowIndex == 0) {
                                    if (i == size - 1) {
                                        rowIndex = 24;
                                    } else {
                                        continue;
                                    }
                                }
                                Object o = data.get(key + "");
                                ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, o);
                            }
                        } else if ("month".equals(type)) {
                            for (Long key : list) {
                                Date date = new Date(key);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                int rowIndex = calendar.get(Calendar.DATE);
                                Object o = data.get(key + "");
                                ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, o);
                            }
                        }
                    } else {
                        int rowIndex = 1;
                        for (Long key : list) {
                            Object o = data.get(key + "");
                            ExcelWriterUtil.addCellData(resultList, rowIndex++, columnIndex, o);
                        }
                    }

                }
            }
        }
        return resultList;
    }

    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();

        result.put("endtime", dateQuery.getQueryEndTime().toString());
        result.put("starttime", dateQuery.getQueryStartTime().toString());
        result.put("method", "max,min");
        return result;
    }

    protected String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }
    protected String getUrl2(String version) {
        return httpProperties.getGlUrlVersion(version) + "/tagValueAction";
    }
}
