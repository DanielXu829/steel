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
public class ZhuChouWuLiuWriter extends AbstractExcelReadWriter {

    public static void main(String[] args) {

        List<DateQuery> all = new ArrayList<>();

        List<DateQuery> dateQueries = DateQueryUtil.buildMonthDayEach(new Date());
        for (DateQuery dateQuery : dateQueries) {
            List<DateQuery> dateQueries8 = DateQueryUtil.buildDay8HourEach(dateQuery.getEndTime());
            all.addAll(dateQueries8);
        }
        System.out.println(all);

        DateQuery dateQuery = DateQueryUtil.buildMonth(new Date());
        System.out.println(dateQuery);
    }

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (null != sheetSplit && sheetSplit.length == 4) {
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                if (sheetSplit[1].indexOf("5") != -1) {
                    int rowBaatch = 1;

                    DateQuery dateQuery = DateQueryUtil.buildMonth(new Date());
                    dateQuery.setEndTime(dateQuery.getRecordDate());

                    List<CellData> cellDataList = this.mapDataHandler(getUrl("5.0"), columns, dateQuery, rowBaatch, sheetName);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else {
                    int rowBaatch = 1;

                    DateQuery dateQuery = DateQueryUtil.buildMonth(new Date());
                    dateQuery.setEndTime(dateQuery.getRecordDate());

                    List<CellData> cellDataList = this.mapDataHandler(getUrl("6.0"), columns, dateQuery, rowBaatch, sheetName);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }
        return workbook;
    }

    protected List<CellData> mapDataHandler(String url, List<String> columns, DateQuery dateQuery, int rowBatch, String sheetName) {
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
        return handlerJsonArray(columns, data, rowBatch, sheetName);
    }

    private List<CellData> handlerJsonArray(List<String> columns, JSONObject data, int rowBatch, String sheetName) {
        List<CellData> cellDataList = new ArrayList<>();
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            int rowIndex = rowBatch;
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

                    List<DateQuery> all = new ArrayList<>();
                    List<DateQuery> dateQueries = DateQueryUtil.buildMonthDayEach(new Date());

                    if ("_cuofeng5_month_day".equals(sheetName) || "_cuofeng6_month_day".equals(sheetName)) {
                        for (DateQuery dateQuery : dateQueries) {
                            List<DateQuery> dateQueriesOther = DateQueryUtil.buildDayOtherHourEach(dateQuery.getStartTime());
                            all.addAll(dateQueriesOther);
                        }
                    } else {
                        for (DateQuery dateQuery : dateQueries) {
                            List<DateQuery> dateQueries8 = DateQueryUtil.buildDay8HourEach(dateQuery.getEndTime());
                            all.addAll(dateQueries8);
                        }
                    }

                    for (int j = 0; j < all.size(); j++) {
                        long time = all.get(j).getStartTime().getTime();
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
