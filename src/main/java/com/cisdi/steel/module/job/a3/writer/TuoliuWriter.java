package com.cisdi.steel.module.job.a3.writer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
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
public class TuoliuWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = PoiCustomUtil.getSheetCell(workbook, "_dictionary", 0, 1);
        String url = getUrl(version);
        return getMapHandler1(url, 1, excelDTO);
    }

    private Workbook getMapHandler1(String url, Integer rowBatch, WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);

                DateQuery dateQuery = DateQueryUtil.buildToday(new Date());
                List<CellData> cellDataList = mapDataHandler(url, columns, dateQuery, rowBatch);
                ExcelWriterUtil.setCellValue(sheet, cellDataList);


//                dateQueries.forEach(item -> {
//
//                });
            }
        }
        return workbook;
    }

    @Override
    protected List<CellData> mapDataHandler(String url, List<String> columns, DateQuery dateQuery, int rowBatch) {
        JSONObject query = new JSONObject();
        query.put("start", dateQuery.getQueryStartTime());
        query.put("end", dateQuery.getQueryEndTime());
        query.put("tagNames", columns);
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String result = httpUtil.postJsonParams(url, jsonString);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return null;
        }
        return handlerJsonArray(columns, data);
    }

    private List<CellData> handlerJsonArray(List<String> columns, JSONObject data) {
        List<CellData> cellDataList = new ArrayList<>();
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            int rowIndex = 1;
            if (StringUtils.isNotBlank(column)) {
                JSONObject jsonObject = data.getJSONObject(column);
                if (Objects.nonNull(jsonObject)) {
                    Map<String, Object> innerMap = jsonObject.getInnerMap();
                    Set<String> keys = innerMap.keySet();
                    Long[] list = new Long[keys.size()];
                    int k = 0;
                    for (String key : keys) {
                        list[k] = Long.valueOf(key);
                        k++;
                    }
                    Arrays.sort(list);

                    List<DateQuery> dateQueries = DateQueryUtil.buildDayHourEach(new Date());

                    for (int j = 0; j < dateQueries.size(); j++) {
                        long time = dateQueries.get(j).getStartTime().getTime();
                        Object v = "";
                        for (int m = 0; m < list.length; m++) {
                            if (time == list[m].longValue()) {
                                Object o = innerMap.get(String.valueOf(list[m]));
                                v = o;
                                break;
                            }
                        }
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, v);
                        rowIndex++;
                    }


//                    for (String key : keys) {
//                        Object o = innerMap.get(key);
//                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, o);
//                        rowIndex++;
//                    }
                }
            }
        }
        return cellDataList;
    }

    /**
     * 不同的版本获取不同的接口地址
     *
     * @param version 版本号
     * @return 结果
     */
    private String getUrl(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/tagValues/tagNames";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/tagValues/tagNames";
        }
    }
}
