package com.cisdi.steel.module.job.a3.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
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
 * 5#、6#烧结机生产日报
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JiejiWriter extends AbstractExcelReadWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = PoiCustomUtil.getSheetCell(workbook, "_dictionary", 0, 1);
        return getMapHandler2(1, excelDTO, version);
    }


    private Workbook getMapHandler2(Integer rowBatch, WriterExcelDTO excelDTO, String version) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String url=getUrl(version);
            if ("_sjmain2_day_hour".equals(sheetName)) {
                url = getUrl2(version);
            }
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                for (DateQuery item : dateQueries) {
                    List<CellData> cellDataList = this.mapDataHandler(url, columns, item, rowBatch, sheetName);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }
        return workbook;
    }

    private List<CellData> mapDataHandler(String url, List<String> columns, DateQuery dateQuery, int rowBatch, String sheetName) {
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
        if ("_sjmain2_day_hour".equals(sheetName)) {
            JSONArray data2 = jsonObject.getJSONArray("data");
            if (Objects.isNull(data2)) {
                return null;
            }
            return this.handlerJsonArray(columns, rowBatch, data2, 1);
        } else {
            JSONObject data = jsonObject.getJSONObject("data");
            if (Objects.isNull(data)) {
                return null;
            }
            return this.handlerJsonArray(columns, data);
        }
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
                    for (String key : keys) {
                        Object o = innerMap.get(key);
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, o);
                        rowIndex++;
                    }
                }
            }
        }
        return cellDataList;
    }

    @Override
    protected List<CellData> handlerJsonArray(List<String> columns, int rowBatch, JSONArray data, int startRow) {
        List<CellData> cellDataList = new ArrayList<>();
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            int rowIndex = 1;
            if (StringUtils.isNotBlank(column)) {
                JSONObject o1 = data.getJSONObject(i);
                Object val = o1.get("val");
                if (Objects.nonNull(o1)) {
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
                        rowIndex++;
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

    private String getUrl2(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/tagValues/latest";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/tagValues/latest";
        }
    }
}
