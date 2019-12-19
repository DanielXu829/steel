package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description: 烧结矿理化指标处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/12/19 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class ShaoJieKuangLiHuaWriter extends AbstractExcelReadWriter {
    // 标记行
    private static int itemRowNum = 3;
    // 平均值计算起始行
    private static int beginRowNum = 4;
    private static int leftCellColumn = 1;
    private static int rightCellColumn = 28;

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version ="8.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e){
            log.info("从模板中获取version失败", e);
        }

        DateQuery date = this.getDateQuery(excelDTO);
        DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());

        Sheet sheet = workbook.getSheetAt(0);
        List<String> columns = PoiCustomUtil.getRowCelVal(sheet, 3);
        sheet.getRow(3).setZeroHeight(true);

        // 获取API url
        String sinterData = getData("SINTER", dateQuery, version);
        JSONArray sinterDataArray = convertJsonStringToJsonArray(sinterData);
        String pelletsData = getData("PELLETS", dateQuery, version);
        JSONArray pelletsDataArray = convertJsonStringToJsonArray(pelletsData);
        String lumporeData = getData("LUMPORE", dateQuery, version);
        JSONArray lumporeDataArray = convertJsonStringToJsonArray(lumporeData);

        int sinterDataArraySize = 0;
        int pelletsDataArraySize = 0;
        int lumporeDataArraySize = 0;

        if (Objects.nonNull(sinterDataArray)) {
            sinterDataArraySize = sinterDataArray.size();
        }
        if (Objects.nonNull(pelletsDataArray)) {
            pelletsDataArraySize = pelletsDataArray.size();
        }
        if (Objects.nonNull(lumporeDataArray)) {
            lumporeDataArraySize = lumporeDataArray.size();
        }

        Map<Long, Map<String, JSONObject>> clockSinterDataMap = this.convertJsonDataToMap(sinterDataArray);
        Map<Long, Map<String, JSONObject>> clockPelletsDataMap = this.convertJsonDataToMap(pelletsDataArray);
        Map<Long, Map<String, JSONObject>> clockLumporeDataMap = this.convertJsonDataToMap(lumporeDataArray);

        Long[] clockArray = getClock(sinterDataArray);
        // 表格最后一行
        int dataArraySize = sinterDataArraySize >= pelletsDataArraySize ? sinterDataArraySize : sinterDataArraySize;
        dataArraySize = dataArraySize >= lumporeDataArraySize ? dataArraySize : lumporeDataArraySize;
        int lastRowNum = itemRowNum + clockArray.length;
        int lastRowNumOld = sheet.getLastRowNum();
        if (lastRowNum > (lastRowNumOld - 1)) {
            setBorderStyle(workbook, sheet, lastRowNum);
        }

        List<CellData> cellDataList1 = mapDataHandler1(clockSinterDataMap, columns, clockArray, "sj");
        List<CellData> cellDataList2 = mapDataHandler1(clockPelletsDataMap, columns, clockArray, "qt");
        List<CellData> cellDataList3 = mapDataHandler1(clockLumporeDataMap, columns, clockArray, "kk");
        ExcelWriterUtil.setCellValue(sheet, cellDataList1);
        ExcelWriterUtil.setCellValue(sheet, cellDataList2);
        ExcelWriterUtil.setCellValue(sheet, cellDataList3);

        return workbook;
    }

    protected List<CellData> mapDataHandler1(Map<Long, Map<String, JSONObject>> clockDataMap, List<String> columns,  Long[] clockArray, String prefix) {
        List<CellData> cellDataList = new ArrayList<>();
        for (int i = 0; i < clockArray.length; i++) {
            Long clock = clockArray[i];
            Map<String, JSONObject> dataMap = clockDataMap.get(clock);
            JSONObject lpJson = dataMap.get("LP");
            JSONObject lcJson= dataMap.get("LC");
            JSONObject lgJson = dataMap.get("LG");
            if (Objects.nonNull(columns) && !columns.isEmpty()) {
                int size = columns.size();
                for (int j = 0; j < size; j++) {
                    String column = columns.get(j);
                    if (StringUtils.isNotBlank(column)) {
                        String[] columnSplit = column.split("_");
                        Double doubleValue = null;
                        if (prefix.equals(columnSplit[0])) {
                            String sjColumn = columnSplit[1];
                            Object valueObject = null;
                            if (Objects.nonNull(lpJson)) {
                                valueObject = lpJson.get(sjColumn);
                            }
                            if (Objects.isNull(valueObject)) {
                                if (Objects.nonNull(lcJson)) {
                                    valueObject = lcJson.get(sjColumn);
                                    if (Objects.isNull(valueObject)) {
                                        if (Objects.nonNull(lgJson)) {
                                            valueObject = lgJson.get(sjColumn);
                                        }
                                    }
                                }
                            }
                            if (Objects.nonNull(valueObject)) {
                                String value = String.valueOf(valueObject);
                                doubleValue = Double.parseDouble(value);
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            String time = sdf.format(new Date(clock));
                            if (StringUtils.isNotBlank(time)) {
                                ExcelWriterUtil.addCellData(cellDataList, 4 + i, 1, time);
                            }
                            ExcelWriterUtil.addCellData(cellDataList, 4 + i, j, doubleValue);
                        }
                    }
                }
            }
        }

        return cellDataList;
    }


    private String getData(String category, DateQuery dateQuery, String version) {
        Map<String, String> queryParam = this.getQueryParam(dateQuery);
        queryParam.put("category", category);
        queryParam.put("granularity", "hour");
        return httpUtil.get(getUrl(version), queryParam);
    }

    /**
     * 将JSON串转换为JSON数组
     * @param data
     * @return
     */
    private JSONArray convertJsonStringToJsonArray(String data) {
        JSONArray dataArray = null;
        if (StringUtils.isNotBlank(data)) {
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (Objects.nonNull(jsonObject)) {
                dataArray = jsonObject.getJSONArray("data");
            }
        }

        return dataArray;
    }

    private Long[] getClock(JSONArray jsonArray) {
        List<Long> clockList = new ArrayList();

        if (Objects.nonNull(jsonArray) && jsonArray.size() > 0) {
            int arraySize = jsonArray.size();
            for (int i = 0; i < arraySize; i++) {
                JSONObject dataObj = jsonArray.getJSONObject(i);
                if (Objects.isNull(dataObj)) {
                    continue;
                }
                JSONObject analysisObj = dataObj.getJSONObject("analysisCharge");
                if (Objects.isNull(analysisObj)) {
                    continue;
                }
                Long clock = analysisObj.getLong("clock");
                clockList.add(clock);
            }
        }

        Map<Long, List<Long>> groupBy = clockList.stream().collect(Collectors.groupingBy(s->s));
        Set<Long> keySet = groupBy.keySet();
        Long[] clockArray = keySet.toArray(new Long[0]);
        Arrays.sort(clockArray);

        return clockArray;
    }

    private Map<Long, Map<String, JSONObject>> convertJsonDataToMap(JSONArray jsonArray) {
        Long[] clockArray = getClock(jsonArray);
        Map<Long, Map<String, JSONObject>> clockDataMap = new HashMap();
        int arraySize = jsonArray.size();
        Map<String, JSONObject> dataMap;
        for (int i = 0; i < clockArray.length; i++) {
            dataMap = new HashMap();
            for (int j = 0; j < arraySize; j++) {
                JSONObject dataObj = jsonArray.getJSONObject(j);
                JSONObject analysisObj = dataObj.getJSONObject("analysisCharge");
                JSONObject valuesObj = dataObj.getJSONObject("values");
                Long clock = analysisObj.getLong("clock");
                if (clockArray[i].equals(clock)) {
                    String type = analysisObj.getString("type");
                    dataMap.put(type, valuesObj);
                }
            }
            // clockDataMap(clock, dataMap)  dataMap(type, values); type:LG LC LP
            clockDataMap.put(clockArray[i], dataMap);
        }

        return clockDataMap;
    }

    /**
     *
     */
    private void setBorderStyle(Workbook workbook, Sheet sheet, int lastRowNum) {
        //设置每个单元格的四周边框
        CellStyle cellNormalStyle = workbook.createCellStyle();
        cellNormalStyle.setBorderRight(BorderStyle.THIN);
        cellNormalStyle.setBorderBottom(BorderStyle.THIN);
        for (int i = beginRowNum; i <= lastRowNum - 1; i++) {
            for (int j = leftCellColumn; j < rightCellColumn; j++) {
                Cell cell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(sheet, i), j);
                cell.setCellStyle(cellNormalStyle);
            }
        }

        // 最左侧列边框
        CellStyle cellLeftStyle = workbook.createCellStyle();
        cellLeftStyle.setBorderLeft(BorderStyle.THICK);
        cellLeftStyle.setBorderBottom(BorderStyle.THIN);
        cellLeftStyle.setBorderRight(BorderStyle.THIN);
        for (int i = beginRowNum; i <= lastRowNum; i++) {
            Cell cell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(sheet, i), leftCellColumn);
            cell.setCellStyle(cellLeftStyle);
        }

        // 最右侧边框
        CellStyle cellRightStyle = workbook.createCellStyle();
        cellRightStyle.setBorderRight(BorderStyle.THICK);
        cellRightStyle.setBorderBottom(BorderStyle.THIN);
        for (int i = beginRowNum; i <= lastRowNum; i++) {
            Cell cell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(sheet, i), rightCellColumn);
            cell.setCellStyle(cellRightStyle);
        }

        // 最后一行下边框
        CellStyle cellBottomStyle = workbook.createCellStyle();
        cellBottomStyle.setBorderBottom(BorderStyle.THICK);
        cellBottomStyle.setBorderRight(BorderStyle.THIN);
        Cell cell = null;
        for (int i = leftCellColumn; i <= rightCellColumn; i++) {
            cell = ExcelWriterUtil.getCellOrCreate(sheet.getRow(lastRowNum), i);
            if (i == leftCellColumn) {
                CellStyle cellBottomLeftStyle = workbook.createCellStyle();
                cellBottomLeftStyle.setBorderLeft(BorderStyle.THICK);
                cellBottomLeftStyle.setBorderBottom(BorderStyle.THICK);
                cellBottomLeftStyle.setBorderRight(BorderStyle.THIN);
                cell.setCellStyle(cellBottomLeftStyle);
                continue;
            }
            if (i == rightCellColumn) {
                CellStyle cellBottomRightStyle = workbook.createCellStyle();
                cellBottomRightStyle.setBorderRight(BorderStyle.THICK);
                cellBottomRightStyle.setBorderBottom(BorderStyle.THICK);
                cell.setCellStyle(cellBottomRightStyle);
                continue;
            }

            cell.setCellStyle(cellBottomStyle);
        }
    }

    /**
     * 组装API url
     * @param version
     * @return
     */
    protected String getUrl(String version) {
        // return httpProperties.getGlUrlVersion(version) + "/analysisCharges";
        return httpProperties.getGlUrlVersion(version) + "/anaChargeValue/byRange";
    }
}
