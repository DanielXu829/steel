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
    private static int itemRowNum = 3; // 标记行
    private static int beginRowNum = 4; // 数据填充起始行
    private static int beginColumnNum = 1; // 数据填充起始列
    private static int endColumnNum_sj = 17; // 烧结矿理化指标最后一列
    private static int endColumnNum_qt = 5; // 球团矿理化指标最后一列
    private static int endColumnNum_kk = 5; // 块矿理化指标最后一列

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

        // 第1个sheet 获取并隐藏标记行
        Sheet sheetSinter = workbook.getSheetAt(0);
        List<String> columnsSinter = PoiCustomUtil.getRowCelVal(sheetSinter, 3);
        sheetSinter.getRow(itemRowNum).setZeroHeight(true);

        // 第2个sheet 获取并隐藏标记行
        Sheet sheetPellets = workbook.getSheetAt(1);
        List<String> columnsPellets = PoiCustomUtil.getRowCelVal(sheetPellets, 3);
        sheetPellets.getRow(itemRowNum).setZeroHeight(true);

        // 第3个sheet 获取并隐藏标记行
        Sheet sheetLumpore = workbook.getSheetAt(2);
        List<String> columnsLumpore = PoiCustomUtil.getRowCelVal(sheetLumpore, 3);
        sheetLumpore.getRow(itemRowNum).setZeroHeight(true);

        // 获取API url
        String sinterData = getData("S4_SINTER", dateQuery, version); // 烧结矿理化数据
        JSONArray sinterDataArray = convertJsonStringToJsonArray(sinterData);
        String pelletsData = getData("PELLETS", dateQuery, version); // 球团矿理化数据
        JSONArray pelletsDataArray = convertJsonStringToJsonArray(pelletsData);
        String lumporeData = getData("LUMPORE", dateQuery, version);  // 块矿理化数据
        JSONArray lumporeDataArray = convertJsonStringToJsonArray(lumporeData);

        Map<Long, Map<String, JSONObject>> clockSinterDataMap = this.convertJsonDataToMap(sinterDataArray);
        Map<Long, Map<String, JSONObject>> clockPelletsDataMap = this.convertJsonDataToMap(pelletsDataArray);
        Map<Long, Map<String, JSONObject>> clockLumporeDataMap = this.convertJsonDataToMap(lumporeDataArray);

        Long[] clockArraySinter = getClock(sinterDataArray);
        Long[] clockArrayPellets = getClock(pelletsDataArray);
        Long[] clockArrayLumpore = getClock(lumporeDataArray);

        setCellStyle(workbook, sheetSinter, clockArraySinter, beginRowNum, beginColumnNum, endColumnNum_sj);
        setCellStyle(workbook, sheetPellets, clockArrayPellets, beginRowNum, beginColumnNum, endColumnNum_qt);
        setCellStyle(workbook, sheetLumpore, clockArrayLumpore, beginRowNum, beginColumnNum, endColumnNum_kk);

        List<CellData> cellDataListSinter = mapDataHandler(clockSinterDataMap, columnsSinter, clockArraySinter, "sj");
        List<CellData> cellDataListPellets = mapDataHandler(clockPelletsDataMap, columnsPellets, clockArrayPellets, "qt");
        List<CellData> cellDataListLumpore = mapDataHandler(clockLumporeDataMap, columnsLumpore, clockArrayLumpore, "kk");
        ExcelWriterUtil.setCellValue(sheetSinter, cellDataListSinter);
        ExcelWriterUtil.setCellValue(sheetPellets, cellDataListPellets);
        ExcelWriterUtil.setCellValue(sheetLumpore, cellDataListLumpore);

        return workbook;
    }

    protected List<CellData> mapDataHandler(Map<Long, Map<String, JSONObject>> clockDataMap, List<String> columns,  Long[] clockArray, String prefix) {
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
                            ExcelWriterUtil.addCellData(cellDataList, 4 + i, j, doubleValue);
                            // 添加时间数据
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            String time = sdf.format(new Date(clock));
                            if (StringUtils.isNotBlank(time)) {
                                ExcelWriterUtil.addCellData(cellDataList, 4 + i, 1, time);
                            }
                        }
                    }
                }
            }
        }

        return cellDataList;
    }

    /**
     * 封装查询条件 并访问api获取数据
     * @param category
     * @param dateQuery
     * @param version
     * @return
     */
    protected String getData(String category, DateQuery dateQuery, String version) {
        Map<String, String> queryParam = this.getQueryParam(dateQuery);
        queryParam.put("category", category);
        if ("PELLETS".equals(category) || "LUMPORE".equals(category)) {
            queryParam.put("type", "LC");
        }

        return httpUtil.get(getUrl(version), queryParam);
    }

    /**
     * 将JSON串转换为JSON数组
     * @param data
     * @return
     */
    protected JSONArray convertJsonStringToJsonArray(String data) {
        JSONArray dataArray = null;
        if (StringUtils.isNotBlank(data)) {
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (Objects.nonNull(jsonObject)) {
                dataArray = jsonObject.getJSONArray("data");
            }
        }

        return dataArray;
    }

    /**
     * 将数据根据时间分组，获取时间戳数组
     * @param jsonArray
     * @return
     */
    protected Long[] getClock(JSONArray jsonArray) {
        List<Long> clockList = new ArrayList();

        if (Objects.nonNull(jsonArray) && jsonArray.size() > 0) {
            int arraySize = jsonArray.size();
            for (int i = 0; i < arraySize; i++) {
                JSONObject dataObj = jsonArray.getJSONObject(i);
                if (Objects.isNull(dataObj)) {
                    continue;
                }
                JSONObject analysisObj = dataObj.getJSONObject("analysis");
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

    /**
     * 将Json数组数据存入HashMap中
     * 结构：clockDataMap(clock, dataMap)  其中dataMap(type, values);  type:LG LC LP
     * @param jsonArray
     * @return
     */
    protected Map<Long, Map<String, JSONObject>> convertJsonDataToMap(JSONArray jsonArray) {
        // 获取时间戳作为Map的key
        Long[] clockArray = getClock(jsonArray);
        Map<Long, Map<String, JSONObject>> clockDataMap = new HashMap();

        if (Objects.nonNull(clockArray) && clockArray.length > 0) {
            int arraySize = jsonArray.size();
            Map<String, JSONObject> dataMap;
            for (int i = 0; i < clockArray.length; i++) {
                dataMap = new HashMap();
                for (int j = 0; j < arraySize; j++) {
                    JSONObject dataObj = jsonArray.getJSONObject(j);
                    JSONObject analysisObj = dataObj.getJSONObject("analysis");
                    JSONObject valuesObj = dataObj.getJSONObject("values");
                    Long clock = analysisObj.getLong("clock");
                    if (clockArray[i].equals(clock)) {
                        String type = analysisObj.getString("type");
                        dataMap.put(type, valuesObj);
                    }
                }

                clockDataMap.put(clockArray[i], dataMap);
            }
        }

        return clockDataMap;
    }

    /**
     * 如果数据超出原本行数 则需要设置表格边框样式
     * @param workbook
     * @param sheet
     * @param clockArray
     */
    private void setCellStyle(Workbook workbook, Sheet sheet, Long[] clockArray, int beginRowNum, int beginColumnNum, int endColumnNum) {
        int lastRowNum = itemRowNum + clockArray.length;
        int lastRowNumOld = sheet.getLastRowNum();
        if (lastRowNum > (lastRowNumOld - 1)) {
            ExcelWriterUtil.setBorderStyle(workbook, sheet, beginRowNum, lastRowNum, beginColumnNum, endColumnNum);
        }
    }

    /**
     * 根据version获取api端口以及前面一部分路径
     * @param version
     * @return
     */
    protected String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/analysis/byRange";
    }
}
