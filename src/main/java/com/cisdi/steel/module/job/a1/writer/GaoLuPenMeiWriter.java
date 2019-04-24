package com.cisdi.steel.module.job.a1.writer;

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
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 高炉喷煤报表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Slf4j
@Component
public class GaoLuPenMeiWriter extends AbstractExcelReadWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        return this.getMapHandler1(getUrl(version), excelDTO, version);
    }

    protected Workbook getMapHandler1(String url, WriterExcelDTO excelDTO, String version) {
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
                if ("_penmei2_month_day".equals(sheetName)) {
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler1(url, columns, item, index, version);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index += 24;
                    }
                } else {
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler(url, columns, item, index, version);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index += 24;
                    }
                }
            }
        }
        return workbook;
    }

    protected List<CellData> mapDataHandler(String url, List<String> columns, DateQuery dateQuery,
                                            int index, String version) {
        Map<String, String> queryParam = dateQuery.getQueryParam();
        String result = getTagValues(queryParam, columns, version, false);
        List<CellData> resultList = new ArrayList<>();
        if (StringUtils.isNotBlank(result)) {
            JSONObject obj = JSONObject.parseObject(result);
            obj = obj.getJSONObject("data");
            if (Objects.nonNull(obj)) {
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    int indexs = index;
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
            }
        }
        return resultList;
    }

    protected List<CellData> mapDataHandler1(String url, List<String> columns, DateQuery dateQuery,
                                             int index, String version) {
        List<CellData> resultList = new ArrayList<>();

        String coke = "bf8";
        if ("6.0".equals(version)) {
            coke = "bf6";
        } else if ("7.0".equals(version)) {
            coke = "bf7";
        }

        Map<String, String> map = new HashMap<>();
        map.put("coke", coke);
        map.put("starttime", dateQuery.getQueryStartTime().toString());
        map.put("endtime", dateQuery.getQueryEndTime().toString());
        url = getUrl2(version);
        String re = httpUtil.get(url, map);

        JSONArray data = new JSONArray();
        if (StringUtils.isNotBlank(re)) {
            JSONObject ob = JSONObject.parseObject(re);
            if (Objects.nonNull(ob)) {
                data = ob.getJSONArray("data");
            }
        }

        List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
        for (int k = 0; k < columns.size(); k++) {
            int indexs = index;
            for (int i = 0; i < dayHourEach.size(); i++) {
                Object v = null;
                for (int j = 0; j < data.size(); j++) {
                    JSONObject jsonObject = data.getJSONObject(j);
                    if (Objects.nonNull(jsonObject)) {
                        long workdate = jsonObject.getLong("workdate");
                        workdate = dealDate(new Date(workdate));
                        if (dayHourEach.get(i).getStartTime().getTime() == workdate) {
                            v = jsonObject.get(columns.get(k));
                            if ("tankweight".equals(columns.get(k))) {
                                String o = jsonObject.getString(columns.get(k));
                                if (StringUtils.isNotBlank(o)) {
                                    try {
                                        v = new BigDecimal(o);
                                    } catch (Exception e) {
                                        log.error("喷煤罐重转换异常：>>>>>>>>" + e.getMessage());
                                    }
                                }
                            }
                            break;
//                            v = jsonObject.get(columns.get(k));
//                            break;
                        }
                    }
                }
                ExcelWriterUtil.addCellData(resultList, indexs++, k, v);
            }

        }
        return resultList;
    }


    private long dealDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime().getTime();
    }

    private String getTagValues(Map<String, String> param, List<String> col, String version, boolean flag) {
        JSONObject jsonObject = new JSONObject();
        //将时间全部往前推1个小时
        if (flag) {
            dealDate(param);
        }

        jsonObject.put("starttime", param.get("starttime"));
        jsonObject.put("endtime", param.get("endtime"));
        jsonObject.put("tagnames", col);
        String re1 = httpUtil.postJsonParams(getUrl(version), jsonObject.toJSONString());
        return re1;
    }

    private void dealDate(Map<String, String> param) {
        String starttime = param.get("starttime");
        String endtime = param.get("endtime");

        Long aLong = Long.valueOf(starttime);
        Long bLong = Long.valueOf(endtime);

        Date sDate = new Date(aLong);
        Date date = DateUtil.addHours(sDate, -1);

        Date eDate = new Date(bLong);
        Date date1 = DateUtil.addHours(eDate, -1);
        param.put("starttime", date.getTime() + "");
        param.put("endtime", date1.getTime() + "");
    }

    private String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }

    private String getUrl2(String version) {
        return httpProperties.getGlUrlVersion(version) + "/coalInjetion/selectCoalBf6";
    }
}
