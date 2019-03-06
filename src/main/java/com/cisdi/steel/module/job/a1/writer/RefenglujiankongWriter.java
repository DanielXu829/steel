package com.cisdi.steel.module.job.a1.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.strategy.api.ApiStrategy;
import com.cisdi.steel.module.job.strategy.date.DateStrategy;
import com.cisdi.steel.module.job.strategy.options.HourOptionStrategy;
import com.cisdi.steel.module.job.strategy.options.OptionsStrategy;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RefenglujiankongWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            if (sheetName.startsWith("_cache")) {
                String[] split = sheetName.split("_");
                DateStrategy dateStrategy = strategyContext.getDate(split[2]);
                DateQuery handlerDate = dateStrategy.handlerDate(date.getRecordDate());
                OptionsStrategy option = strategyContext.getOption(split[3]);
                List<DateQuery> dateQueries = option.execute(handlerDate);
                ApiStrategy tag = strategyContext.getApi("cache");
                // 3、接口处理
                SheetRowCellData execute = tag.execute(workbook, sheet, dateQueries);
                // 设置所有值
                execute.allValueWriteExcel();
            }
            // 处理其他数据
            if (sheetName.startsWith("_template")) {
                HourOptionStrategy hourOptionStrategy = new HourOptionStrategy();
                List<DateQuery> dateQueries = hourOptionStrategy.execute(excelDTO.getDateQuery());
                handlerData(workbook,sheet,dateQueries);
            }
        }
        return workbook;
    }

    private void handlerData(Workbook workbook, Sheet sheet, List<DateQuery> dateQueries) {
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version) + "/hsstate";
        List<CellData> cellData = handlerSixData(url, version,dateQueries);
        SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(cellData)
                .build().allValueWriteExcel();
    }

    private List<CellData> handlerSixData(String url,String version,List<DateQuery> dateQueries) {
        int size = dateQueries.size();
        DateQuery dateQuery;
        // 送风炉号所在的列
        int columnIndex=2;
        // 送风时间所在的列
        int columnIndex2=1;
        List<CellData> resultData =new ArrayList<>();
        for (int i = 0; i < size; i++) {
            dateQuery = dateQueries.get(i);
            Date start = DateUtil.addHours(dateQuery.getStartTime(), -1);
            Date end = DateUtil.addHours(dateQuery.getEndTime(), -1);
            dateQuery.setStartTime(start);
            dateQuery.setEndTime(end);
            List<Map<String, Object>> result = handlerRequestData(url, dateQuery);
            if (Objects.isNull(result)) {
                continue;
            }
            if ("6.0".equals(version)) {
                // 处理送风炉号
                Object handlerluhao = handlerluhao(result);
                CellData cellData=new CellData(i+1,columnIndex,handlerluhao);
                resultData.add(cellData);
            } else if ("8.0".equals(version)) {
                // 处理送风时间
                Object o = handlerTime(result);
                CellData cellData=new CellData(i+1,columnIndex2,o);
                resultData.add(cellData);
            }



        }
        return resultData;
    }

    private Object handlerTime(List<Map<String, Object>> result) {
        List<Map<String, Object>> mapList = result.stream().filter(item -> {
            // 状态为4
            Object state = item.get("state");
            return Objects.nonNull(state) && "4".equals(state.toString());
        }).collect(Collectors.toList());
        OptionalLong endVal = mapList.stream().mapToLong(item -> {
            Object endtime = item.get("endtime");
            if (Objects.isNull(endtime)) {
                return 0L;
            } else {
                return Long.valueOf(endtime.toString());
            }
        }).max();
        if (endVal.isPresent()) {
            return endVal.getAsLong();
        }
        return null;
    }


    private List<Map<String, Object>> handlerRequestData(String url,DateQuery dateQuery) {
        Map<String, String> queries = new HashMap<>();
        queries.put("begintime", dateQuery.getQueryStartTime().toString());
        queries.put("endtime", dateQuery.getQueryEndTime().toString());
        String s = httpUtil.get(url, queries);
        if (StringUtils.isBlank(s)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(s);
        JSONArray data = jsonObject.getJSONArray("data");
        if (Objects.isNull(data)) {
            return null;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (int j = 0; j < data.size(); j++) {
            JSONObject jsonObject1 = data.getJSONObject(j);
            result.add(jsonObject1);
        }
        return result;
    }

    private Object handlerluhao(List<Map<String, Object>> result) {
        List<Map<String, Object>> collect = result.stream().filter(item -> {
            // 状态为4   endtime为null的数据
            Object state = item.get("state");
            if (Objects.nonNull(state) && "4".equals(state.toString())) {
                Object endtime = item.get("endtime");
                return Objects.isNull(endtime);
            }
            return false;
        }).collect(Collectors.toList());
        if (collect.size() > 0) {
            Map<String, Object> stringObjectMap = collect.get(0);
            return stringObjectMap.get("hsno");
        }
        return null;
    }
}
