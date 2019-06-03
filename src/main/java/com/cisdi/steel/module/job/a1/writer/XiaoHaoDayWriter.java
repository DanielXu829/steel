package com.cisdi.steel.module.job.a1.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.ExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.strategy.date.DateStrategy;
import com.cisdi.steel.module.job.strategy.options.OptionsStrategy;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消耗日报
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/11 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class XiaoHaoDayWriter extends ExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = super.excelExecute(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            if (sheetName.startsWith("_ana")) {
                String[] split = sheetName.split("_");
                DateStrategy dateStrategy = strategyContext.getDate(split[2]);
                DateQuery handlerDate = dateStrategy.handlerDate(excelDTO.getDateQuery().getRecordDate());
                OptionsStrategy option = strategyContext.getOption(split[3]);
                List<DateQuery> dateQueries = option.execute(handlerDate);
                // 3、接口处理
                SheetRowCellData execute = execute(workbook, sheet, dateQueries);
                // 设置所有值
                execute.allValueWriteExcel();
            }
        }
        return workbook;
    }

    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version);


        List<String> firstRowCelVal = PoiCustomUtil.getFirstRowCelVal(sheet);
        Set<String> categorys = firstRowCelVal.stream().filter(StringUtils::isNotBlank).map(v -> v.split("/")[0]).collect(Collectors.toSet());
        Map<Long, Map<String, Object>> result = new HashMap<>();
        for (DateQuery dateQuery : queryList) {
            for (String category : categorys) {
                requestData(url, category, dateQuery, result);
            }
        }
        List<CellData> resultData = new ArrayList<>();

        Set<Long> set = result.keySet();
        List<Long> list = new ArrayList<>(set);
        Collections.sort(list);

        List<DateQuery> dayEach = DateQueryUtil.buildStartAndEndDayEach(new Date(Long.valueOf(queryList.get(0).getQueryStartTime())),
                new Date(Long.valueOf(queryList.get(0).getQueryEndTime())));
        int rowIndex = 1;
        for (int i = 0; i < dayEach.size(); i++) {
            DateQuery query = dayEach.get(i);
            Date recordDate = query.getRecordDate();
            for (int k = 0; k < list.size(); k++) {
                boolean flag = false;
                Long tempTime = list.get(k);
                String formatDateTime = DateUtil.getFormatDateTime(new Date(tempTime), "yyyy-MM-dd 00:00:00");
                Date date = DateUtil.strToDate(formatDateTime, DateUtil.fullFormat);
                if (date.getTime() == recordDate.getTime()) {
                    Map<String, Object> map = result.get(tempTime);
                    for (int j = 0; j < firstRowCelVal.size(); j++) {
                        String val = firstRowCelVal.get(j);
                        if (StringUtils.isBlank(val)) {
                            continue;
                        }
                        if (map.containsKey(val)) {
                            Object o = map.get(val);
                            ExcelWriterUtil.addCellData(resultData, rowIndex, j, o);
                            flag = true;
                        }
                    }
                }
                if (flag) {
                    break;
                }
            }
            rowIndex += 1;
        }
//        for (int k = 0; k < list.size(); k++) {
//            Long key = list.get(k);
//            Map<String, Object> map = result.get(key);
//            for (int i = 0; i < firstRowCelVal.size(); i++) {
//                String val = firstRowCelVal.get(i);
//                if (StringUtils.isBlank(val)) {
//                    continue;
//                }
//                if (map.containsKey(val)) {
//                    Calendar calendar=Calendar.getInstance();
//                    calendar.setTime(new Date(key));
//                    int rowIndex = calendar.get(Calendar.DATE);
//                    Object o = map.get(val);
//                    ExcelWriterUtil.addCellData(resultData, rowIndex, i, o);
////                    ExcelWriterUtil.addCellData(resultData, k+1, i, o);
//                }
//            }
//        }
        return SheetRowCellData.builder()
                .sheet(sheet)
                .workbook(workbook)
                .cellDataList(resultData)
                .build();
    }

    private void requestData(String url, String category, DateQuery dateQuery, Map<Long, Map<String, Object>> result) {
        url = url + "/anaChargeValue/range";
        Map<String, String> queryParam = dateQuery.getQueryParam();

        queryParam.put("granularity", "day");
        queryParam.put("type", "LC");

        queryParam.put("category", category);
        String s = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(s)) {
            return;
        }
        JSONObject data = JSONObject.parseObject(s);
        JSONArray datas = data.getJSONArray("data");
        if (Objects.isNull(datas) || datas.size() == 0) {
            return;
        }
        int size = datas.size();
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = datas.getJSONObject(i);
            if (Objects.isNull(jsonObject)) {
                continue;
            }
            JSONObject analysisCharge = jsonObject.getJSONObject("analysisCharge");
            JSONObject values = jsonObject.getJSONObject("values");
            Long clock = analysisCharge.getLong("clock");
            Set<String> keySet = values.keySet();
            Map<String, Object> mapChild = result.get(clock);
            if (Objects.isNull(mapChild)) {
                mapChild = new HashMap<>();
                result.put(clock, mapChild);
            }
            for (String key : keySet) {
                BigDecimal bigDecimal = values.getBigDecimal(key);
                mapChild.put(category + "/" + key, bigDecimal);
            }
        }

    }
}
