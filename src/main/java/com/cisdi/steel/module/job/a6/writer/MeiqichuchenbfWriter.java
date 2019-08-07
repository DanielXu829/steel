package com.cisdi.steel.module.job.a6.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
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

import java.math.BigDecimal;
import java.util.*;

/**
 * 煤气除尘报表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
public class MeiqichuchenbfWriter extends AbstractExcelReadWriter {
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
                if ("tag".equals(sheetSplit[1])) {
                    if("all".equals(sheetSplit[3])){
                        for (int rowNum = 0; rowNum < size; rowNum++) {
                            DateQuery dateQuery = dateQueries.get(rowNum);
                            List<CellData> cellValInfoList = eachData(columns, getUrl(version), dateQuery);
                            ExcelWriterUtil.setCellValue(sheet, cellValInfoList);
                        }
                    }else if("hour".equals(sheetSplit[3])){
                        for (int rowNum = 0; rowNum < size; rowNum++) {
                            DateQuery dateQuery = dateQueries.get(rowNum);
                            List<CellData> cellValInfoList = eachData(columns, getUrl(version), dateQuery, rowNum);
                            ExcelWriterUtil.setCellValue(sheet, cellValInfoList);
                        }
                    }
                } else if ("maxmin".equals(sheetSplit[1])) {
                    int index = 1;
                    for (int rowNum = 0; rowNum < size; rowNum++) {
                        DateQuery eachDate = dateQueries.get(rowNum);
                        List<CellData> cellValInfoList = mapDataHandler1(columns, getUrl2(version), eachDate, index);
                        ExcelWriterUtil.setCellValue(sheet, cellValInfoList);
                        index++;
                    }
                } else if ("fanchui6".equals(sheetSplit[1])) {
                    int index = 1;
                    for (int rowNum = 0; rowNum < size; rowNum++) {
                        DateQuery eachDate = dateQueries.get(rowNum);
                        List<CellData> cellValInfoList = mapDataHandler3(columns, getUrl(version), eachDate, index, version);
                        ExcelWriterUtil.setCellValue(sheet, cellValInfoList);
                        index++;
                    }
                } else if ("fanchui7".equals(sheetSplit[1])) {
                    int index = 1;
                    for (int rowNum = 0; rowNum < size; rowNum++) {
                        DateQuery eachDate = dateQueries.get(rowNum);
                        List<CellData> cellValInfoList = mapDataHandler3(columns, getUrl(version), eachDate, index, version);
                        ExcelWriterUtil.setCellValue(sheet, cellValInfoList);
                        index++;
                    }
                } else if ("fanchui8".equals(sheetSplit[1])) {
                    int index = 1;
                    for (int rowNum = 0; rowNum < size; rowNum++) {
                        DateQuery eachDate = dateQueries.get(rowNum);
                        List<CellData> cellValInfoList = mapDataHandler3(columns, getUrl(version), eachDate, index, version);
                        ExcelWriterUtil.setCellValue(sheet, cellValInfoList);
                        index++;
                    }
                }
            }
        }
        return workbook;
    }

    public List<CellData> mapDataHandler(List<String> columns, String url, DateQuery dateQuery, int rowBatch) {
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

    private List<CellData> eachData(List<String> columns, String url,DateQuery dateQuery) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("starttime", dateQuery.getStartTime());
        jsonObject.put("endtime", dateQuery.getEndTime());
        jsonObject.put("tagnames", columns);

        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        JSONObject obj = JSONObject.parseObject(result);
        obj = obj.getJSONObject("data");
        List<CellData> resultList = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            int indexs = 1;
            String cell = columns.get(columnIndex);
            if (StringUtils.isNotBlank(cell)) {
                JSONObject data = obj.getJSONObject(cell);
                List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
                for (int i = 0; i < dayHourEach.size(); i++) {
                    Object v = "";
                    if (Objects.nonNull(data)) {
                        Map<String, Object> innerMap = data.getInnerMap();
                        Set<String> keySet = innerMap.keySet();
                        Long[] list = new Long[keySet.size()];
                        int k = 0;
                        for (String key : keySet) {
                            list[k] = Long.valueOf(key);
                            k++;
                        }
                        Arrays.sort(list);
                        Date startTime = dayHourEach.get(i).getStartTime();

                        for (int j = 0; j < list.length; j++) {
                            Long tempTime = list[j];
                            String formatDateTime = DateUtil.getFormatDateTime(new Date(tempTime), "yyyy-MM-dd HH:00:00");
                            Date date = DateUtil.strToDate(formatDateTime, DateUtil.fullFormat);
                            if (date.getTime() == startTime.getTime()) {
                                v = data.get(tempTime + "");
                                break;
                            }
                        }
                    }
                    ExcelWriterUtil.addCellData(resultList, indexs++, columnIndex, v);
                }
            }
        }
        return resultList;
    }

    private List<CellData> eachData(List<String> columns, String url,DateQuery dateQuery,int rowNum) {
        JSONObject jsonObject = new JSONObject();

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateQuery.getStartTime());
        cal.add(Calendar.SECOND, -30);
        jsonObject.put("starttime", cal.getTime());
        cal.add(Calendar.SECOND, 60);
        jsonObject.put("endtime", cal.getTime());
        jsonObject.put("tagnames", columns);

        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        JSONObject obj = JSONObject.parseObject(result);
        obj = obj.getJSONObject("data");
        List<CellData> resultList = new ArrayList<>();
        int rowIndex = rowNum + 1;
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            String cell = columns.get(columnIndex);
            if (StringUtils.isNotBlank(cell)) {
                JSONObject data = obj.getJSONObject(cell);
                BigDecimal v = BigDecimal.ZERO;
                if (Objects.nonNull(data)) {
                    Map<String, Object> innerMap = data.getInnerMap();
                    Set<String> keySet = innerMap.keySet();
                    for (String key : keySet) {
                        v = (BigDecimal)innerMap.get(key);
                        ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, v);
                        break;
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
        return result;
    }

    protected JSONObject getQueryParam(DateQuery dateQuery, List<String> tagNames) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("starttime", dateQuery.getStartTime().getTime());
        jsonObject.put("endtime", dateQuery.getEndTime().getTime());
        jsonObject.put("tagnames", tagNames);
        return jsonObject;
    }

    public List<CellData> mapDataHandler1(List<String> columns, String url, DateQuery dateQuery, int index) {
        List<CellData> resultList = new ArrayList<>();
        Map<String, String> queryParam = getQueryParam(dateQuery);
        queryParam.put("method", "max");
        queryParam.put("tagname", columns.get(0));
        String resultmax = httpUtil.get(url, queryParam);
        queryParam.put("method", "min");
        if (StringUtils.isNotBlank(resultmax)) {
            JSONObject jsonObject1 = JSONObject.parseObject(resultmax);
            if (Objects.nonNull(jsonObject1)) {
                Object v = jsonObject1.get("data");
                ExcelWriterUtil.addCellData(resultList, index, 0, v);
            }
        }
        String resultmin = httpUtil.get(url, queryParam);
        if (StringUtils.isNotBlank(resultmin)) {
            JSONObject jsonObject1 = JSONObject.parseObject(resultmin);
            if (Objects.nonNull(jsonObject1)) {
                Object v = jsonObject1.get("data");
                ExcelWriterUtil.addCellData(resultList, index, 1, v);
            }
        }
        return resultList;
    }

    private static final int MINUTE = 1*60*1000;

    /**
     * 反吹是否开始
     * @param version
     * @param val
     * @return
     */
    private Boolean isFCBegin(String version, int val){
        Boolean isBegin = false;
        switch(version){
            case "7.0":
                isBegin = val < 15;
                break;
            default:// 6/8
                isBegin = val > 0;
        }
        return isBegin;
    }

    /**
     * 反吹是否结束
     * @param version
     * @param val
     * @return
     */
    private Boolean isFCEnd(String version, int val){
        Boolean isEnd = false;
        switch(version){
            case "7.0":
                isEnd = (val == 15);
                break;
            default:// 6/8
                isEnd = (val == 0);
        }
        return isEnd;
    }

    public List<CellData> mapDataHandler3(List<String> columns, String url, DateQuery dateQuery, int index, String version) {
        // 每小时+前一分钟+后一分钟
        DateQuery tmp = new DateQuery(null,dateQuery.getEndTime(),dateQuery.getRecordDate());
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateQuery.getStartTime());
        cal.add(Calendar.MINUTE,-1);
        tmp.setStartTime(cal.getTime());

        List<CellData> resultList = new ArrayList<>();
        List<String> c = new ArrayList<>();
        c.add(columns.get(0));
        JSONObject jsonObject = getQueryParam(tmp, c);
        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        if (StringUtils.isNotBlank(result)) {
            JSONObject obj = JSONObject.parseObject(result);
            obj = obj.getJSONObject("data");
            JSONObject jsonObject1 = obj.getJSONObject(columns.get(0));
            if (Objects.nonNull(jsonObject1)) {
                Map<String, Object> innerMap1 = jsonObject1.getInnerMap();
                // 0分
                Long startTime = tmp.getQueryStartTime()+MINUTE;
                // 59分
                Long endTime = tmp.getQueryEndTime()-MINUTE;
                // 一开始就是0，找开始时间点，找不到说明本小时未开启，结束；
                // +如果找到，就看本小时是否结束。

                // 一开始就是1，找结束时间点，找不到说明本小时未结束，结束；
                // +如果找到，就结束。
                int begin = innerMap1.containsKey(startTime+"")?((BigDecimal)innerMap1.get(startTime+"")).intValue():0;

                Long startIndex = null;
                Long endIndex = null;
                if(begin == 0){// 未开始
                    while (startTime<=endTime) {// 找开始时间点
                        if(innerMap1.containsKey(startTime+"")){
                            int val = ((BigDecimal)innerMap1.get(startTime+"")).intValue();
                            if(isFCBegin(version,val)){
                                startIndex = startTime;
                                break;
                            }
                        }
                        startTime += MINUTE;
                    }
                    if(null != startIndex){// 找到开始时间点,找结束时间点
                        while (startTime<=endTime) {
                            if(innerMap1.containsKey(startTime+"")){
                                int val = ((BigDecimal)innerMap1.get(startTime+"")).intValue();
                                if(isFCEnd(version,val)){
                                    endIndex = startTime;
                                    break;
                                }
                            }
                            startTime += MINUTE;
                        }
                    }
                }else{// 已开始，找结束时间点
                    while (startTime<=endTime) {
                        if(innerMap1.containsKey(startTime+"")){
                            int val = ((BigDecimal)innerMap1.get(startTime+"")).intValue();
                            if(isFCEnd(version,val)){
                                endIndex = startTime;
                                break;
                            }
                        }
                        startTime += MINUTE;
                    }
                }

                if (null != startIndex) {
                    ExcelWriterUtil.addCellData(resultList, index, 0, startIndex);
                    String getValUrl = getUrl3(version);
                    Integer[] colIndex = isYaChaBefore(version);
                    writeVal(getValUrl,resultList,index,colIndex,columns,startIndex,-1);
                }
                if (null != endIndex) {
                    String getValUrl = getUrl3(version);
                    Integer[] colIndex = isYaChaAfter(version);
                    writeVal(getValUrl,resultList,index,colIndex,columns,endIndex,1);
                }
            }
        }

        return resultList;
    }

    /**
     * 压差前计算列
     * @param version
     * @param val
     * @return
     */
    private Integer[] isYaChaBefore(String version){
        Integer[] colIndex = null;
        switch(version){
            case "7.0":
                colIndex = new Integer[]{1,2,3,4,5};
                break;
            default:// 6/8
                colIndex = new Integer[]{1,2};
        }
        return colIndex;
    }

    /**
     * 压差后计算列
     * @param version
     * @param val
     * @return
     */
    private Integer[] isYaChaAfter(String version){
        Integer[] colIndex = null;
        switch(version){
            case "7.0":
                colIndex = new Integer[]{6,7,8,9,10};
                break;
            default:// 6/8
                colIndex = new Integer[]{3,4};
        }
        return colIndex;
    }

    private void writeVal(String url, List<CellData> resultList, Integer rowIndex, Integer[] colIndex, List<String> columns, Long time, int min){
        for (Integer col : colIndex) {
            BigDecimal o1 = (BigDecimal)dealPart1(url, time, columns.get(col), min);
            ExcelWriterUtil.addCellData(resultList, rowIndex, col, o1);
        }
    }

    private Object dealPart1(String url, Long indextime, String tagName, int min) {
        Date date = new Date(indextime);
        Date curr = DateUtil.addMinute(date, min);
        Object v = null;
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("time", curr.getTime() + "");
        queryParam.put("tagname", tagName);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    v = data.get("val");
                }
            }
        }
        return v;
    }

    protected String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }

    protected String getUrl2(String version) {
        return httpProperties.getGlUrlVersion(version) + "/tagValueAction";
    }

    protected String getUrl3(String version) {
        return httpProperties.getGlUrlVersion(version) + "/tagValue/latest";
    }
}
