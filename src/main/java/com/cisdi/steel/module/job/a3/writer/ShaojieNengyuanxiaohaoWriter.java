package com.cisdi.steel.module.job.a3.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 烧结能源消耗及成本统计
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ShaojieNengyuanxiaohaoWriter extends AbstractExcelReadWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        return this.getMapHandler2(excelDTO);
    }

    /**
     * 同样处理 方式
     *
     * @param excelDTO 数据
     * @return 结果
     */
    public Workbook getMapHandler2(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            String version = "5.0";
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                //获取版本
                String main1 = sheetSplit[1];
                if (main1.startsWith("6")) {
                    version = "6.0";
                }
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                String url = "";
                if ("_5tag_month_all".equals(sheetName) || "_6tag_month_all".equals(sheetName)) {
                    url = getUrl4(version);
                    for (DateQuery dateQuery : dateQueries) {
                        int rowBatch = 1;
                        //获取对应的请求接口地址
                        Map query = new HashMap();
                        query.put("startTime", dateQuery.getQueryStartTime());
                        query.put("endTime", dateQuery.getQueryEndTime());

                        List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                        List<CellData> cellDataList = dealByTime(url, date.getRecordDate(), columns, query, rowBatch);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else if ("_5tag2_month_all".equals(sheetName) || "_6tag2_month_all".equals(sheetName)) {
                    url = getUrl5(version);
                    for (DateQuery dateQuery : dateQueries) {
                        List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                        int rowBatch = 1;
                        //获取对应的请求接口地址
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
                        List<CellData> cellDataList = handlerJsonArray(columns, data, rowBatch, dateQuery);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                }

            }
        }
        return workbook;
    }

    private List<CellData> handlerJsonArray(List<String> columns, JSONObject data, int rowBatch, DateQuery dateQuery) {
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

                    List<DateQuery> dateQueries = DateQueryUtil.buildMonthDayEach(dateQuery.getRecordDate());

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
                        rowIndex += rowBatch;
                    }
                }
            }
        }
        return cellDataList;
    }

    private List<CellData> dealByTime(String url, Date date, List<String> columns, Map query, int rowbatch) {
        List<CellData> cellDataList = new ArrayList<>();
        String result = httpUtil.get(url, query);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONArray rows = jsonObject.getJSONArray("data");
                if (Objects.nonNull(rows) && rows.size() > 0) {
                    List<DateQuery> monthDayEach = DateQueryUtil.buildMonthDayEach(date);
                    int rowIndex = 1;
                    for (int j = 0; j < monthDayEach.size(); j++) {
                        DateQuery dateQuery = monthDayEach.get(j);
                        int r = rowIndex;
                        for (int i = 0; i < rows.size(); i++) {
                            JSONObject jsonObject1 = rows.getJSONObject(i);
                            if (Objects.nonNull(jsonObject1)) {
                                String recordDate = jsonObject1.getString("recordDate");
                                Date strToDate = DateUtil.strToDate(recordDate, DateUtil.fullFormat);
                                String formatDateTime = DateUtil.getFormatDateTime(strToDate, "yyyy-MM-dd");
                                if (formatDateTime.equals(DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy-MM-dd"))) {
                                    for (int m = 0; m < columns.size(); m++) {
                                        String s = columns.get(m);
                                        if (StringUtils.isNotBlank(s)) {
                                            Object o = jsonObject1.get(s);
                                            ExcelWriterUtil.addCellData(cellDataList, r, m, o);
                                        }
                                    }
                                    r++;
                                }
                            }
                        }
                        rowIndex += rowbatch;
                    }
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
    //无纸化-脱硫运行记录
    private String getUrl4(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/ploDesulfuration/statistics";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/ploDesulfuration/statistics";
        }
    }


    /**
     * 不同的版本获取不同的接口地址
     *
     * @param version 版本号
     * @return 结果
     */
    private String getUrl5(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/tagValues/tagNames";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/tagValues/tagNames";
        }
    }

}
