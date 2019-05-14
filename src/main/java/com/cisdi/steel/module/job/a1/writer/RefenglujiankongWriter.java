package com.cisdi.steel.module.job.a1.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
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
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 热风炉监控
 */
@SuppressWarnings("Duplicates")
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
            if ("_template".equals(sheetName)) {
                HourOptionStrategy hourOptionStrategy = new HourOptionStrategy();
                List<DateQuery> dateQueries = hourOptionStrategy.execute(excelDTO.getDateQuery());
                handlerData(workbook, sheet, dateQueries);
            }
            // 处理热风炉重点监控报表
            if ("_temp".equals(sheetName)) {
                DateQuery dateQuery = DateQueryUtil.buildMonth(excelDTO.getDateQuery().getRecordDate());
                handlerRefenglu(workbook, sheet, dateQuery);
            }
        }
        return workbook;
    }

    private void handlerRefenglu(Workbook workbook, Sheet sheet, DateQuery dateQuery) {
        // 第一行
        List<String> firstRowCelVal = PoiCustomUtil.getFirstRowCelVal(sheet);
        // 第二行
        List<String> twoCellVal = PoiCustomUtil.getRowCelVal(sheet, 1);
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
        List<CellData> cellData = handlerRefengluData(url, firstRowCelVal, dateQuery, 2);
        List<CellData> cellData1 = handlerRefengluData(url, twoCellVal, dateQuery, 3);
        cellData.addAll(cellData1);
        SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(cellData)
                .build().allValueWriteExcel();
    }

    private List<CellData> handlerRefengluData(String url, List<String> tagNames, DateQuery dateQuery, int row) {
        JSONObject jsonObject = new JSONObject();
        Map<String, String> queryParam = dateQuery.getQueryParam();
        jsonObject.put("starttime", queryParam.get("starttime"));
        jsonObject.put("endtime", queryParam.get("endtime"));
        List<String> cellList = tagNames.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        jsonObject.put("tagnames", cellList);

        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        JSONObject obj = JSONObject.parseObject(result);
        obj = obj.getJSONObject("data");
        List<CellData> resultList = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < tagNames.size(); columnIndex++) {
            String cell = tagNames.get(columnIndex);
            if (StringUtils.isNotBlank(cell)) {
                JSONObject data = obj.getJSONObject(cell);
                if (Objects.nonNull(data)) {
                    int columnIndexVal = columnIndex;
                    data.keySet().forEach(key -> {
                        Object o = data.get(key);
                        Date date = new Date(Long.valueOf(key));
                        Calendar instance = Calendar.getInstance();
                        instance.setTime(date);
                        int day = instance.get(Calendar.DATE);
                        int hour = instance.get(Calendar.HOUR_OF_DAY);
                        int inr = -2;
                        if (hour == 8) {
                            inr = 0;
                        } else if (hour == 16) {
                            inr = 2;
                        }
                        int rowIndex = row + ((day - 1) * 6) + inr;
                        CellData cellData = new CellData(rowIndex, columnIndexVal, o);
                        resultList.add(cellData);
                    });

                }
            }
        }
        return resultList;
    }

    private void handlerData(Workbook workbook, Sheet sheet, List<DateQuery> dateQueries) {
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version) + "/hsstatebyendtime";
        List<CellData> cellData = handlerSixData(url, version, dateQueries);
        SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(cellData)
                .build().allValueWriteExcel();
    }

    private List<CellData> handlerSixData(String url, String version, List<DateQuery> dateQueries) {
        int size = dateQueries.size();
        DateQuery dateQuery;
        // 送风炉号所在的列
        int columnIndex = 2;
        // 送风时间所在的列
        int columnIndex2 = 1;
        List<CellData> resultData = new ArrayList<>();
        if ("6.0".equals(version)) {
            // 处理送风炉号
            Object handlerluhao = handlerluhao();
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(Calendar.HOUR_OF_DAY);
            if (i == 0) {
                i = 24;
            }
            CellData cellData = new CellData(i + 1, columnIndex, handlerluhao);
            resultData.add(cellData);
        } else if ("8.0".equals(version)) {
            for (int i = 0; i < size; i++) {
                dateQuery = dateQueries.get(i);
                List<Map<String, Object>> result = handlerRequestData(url, dateQuery);
                if (Objects.isNull(result)) {
                    continue;
                }
                // 处理送风时间
                Object o = handlerTime(result);
                CellData cellData = new CellData(i + 1, columnIndex2, o);
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


    private List<Map<String, Object>> handlerRequestData(String url, DateQuery dateQuery) {
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

    private Object handlerluhao() {
        String s = httpProperties.getUrlApiGLOne() + "/blastinghsno";
        String s1 = httpUtil.get(s);
        if (StringUtils.isNotBlank(s1)) {
            JSONObject jsonObject = JSONObject.parseObject(s1);
            return jsonObject.get("data");
        }
        return null;
    }
}
