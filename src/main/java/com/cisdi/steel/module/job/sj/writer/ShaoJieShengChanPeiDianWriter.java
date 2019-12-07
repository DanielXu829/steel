package com.cisdi.steel.module.job.sj.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@SuppressWarnings("ALL")
@Slf4j
public class ShaoJieShengChanPeiDianWriter extends AbstractExcelReadWriter {

    @Autowired
    private TargetManagementMapper targetManagementMapper;

    public Map<String, String> dayWorkRelativePositionMap;

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        dayWorkRelativePositionMap = PoiCustomUtil.getSheetDayWorkRelativePosition(workbook);
        DateQuery date = this.getDateQuery(excelDTO);
        List<DateQuery> dateQueries = null;

        int numberOfSheets = workbook.getNumberOfSheets();
        String version = "4.0";
        try{
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        }catch(Exception e){
            log.error("在模板中获取version失败", e);
        }

        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表 待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
            }
        }

        Sheet sheet = workbook.getSheetAt(0);
        String url = getUrl(version);
        List<CellData> cellDataList = mapDataHandler(sheet, getUrl(version), dateQueries);
        ExcelWriterUtil.setCellValue(sheet, cellDataList);

        return workbook;
    }

    protected List<CellData> mapDataHandler(Sheet sheet, String url,  List<DateQuery> dateQueries) {
        Map<String, String> queryParam = new HashMap<>();
        List<CellData> cellDataList = new ArrayList<>();
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();

        // 遍历整个excel
        for (int rowNum = firstRowNum; rowNum < lastRowNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (Objects.isNull(row)) {
                continue;
            }
            short lastCellNum = row.getLastCellNum();
            String value;
            for (int colNum = 0; colNum < lastCellNum; colNum++) {
                Cell cell = row.getCell(colNum);
                value = PoiCellUtil.getCellValue(cell);
                if (StringUtils.isNotBlank(value) && value.startsWith("ZP")) {
                    String tagValue = targetManagementMapper.selectTargetFormulaByTargetName(value);
                    // 夜班、白班, size肯定为2
                    if (StringUtils.isNotBlank(tagValue)) {
                        int size = dateQueries.size();
                        String apiResult;
                        for (int i = 0; i < size; i++) {
                            if (i == 0) {
                                queryParam.put("startTime", Objects.requireNonNull(dateQueries.get(i).getQueryStartTime()).toString());
                                queryParam.put("endTime", Objects.requireNonNull(dateQueries.get(i).getQueryEndTime()).toString());
                                queryParam.put("tagName", tagValue);
                                apiResult = httpUtil.get(url, queryParam);
                                if (StringUtils.isNotBlank(apiResult)) {
                                    JSONObject jsonObject = JSONObject.parseObject(apiResult);
                                    if (Objects.nonNull(jsonObject)) {
                                        JSONArray dataArray = jsonObject.getJSONArray("data");
                                        int arraySize = dataArray.size();
                                        if (Objects.nonNull(dataArray) && arraySize != 0) {
                                            // 只有夜班一条数据
                                            JSONObject dataObj = dataArray.getJSONObject(0);
                                            if (Objects.nonNull(dataObj)) {
                                                Double cellValue = dataObj.getDouble("val");
                                                ExcelWriterUtil.addCellData(cellDataList, rowNum, colNum, cellValue);
                                            }
                                        }
                                    }
                                }
                            } else if (i == 1) {
                                queryParam.put("startTime", Objects.requireNonNull(dateQueries.get(i).getQueryStartTime()).toString());
                                queryParam.put("endTime", Objects.requireNonNull(dateQueries.get(i).getQueryEndTime()).toString());
                                queryParam.put("tagName", tagValue);
                                apiResult = httpUtil.get(url, queryParam);
                                JSONObject jsonObject = JSONObject.parseObject(apiResult);
                                if (Objects.nonNull(jsonObject)) {
                                    JSONArray dataArray = jsonObject.getJSONArray("data");
                                    int arraySize = dataArray.size();
                                    if (Objects.nonNull(dataArray) && arraySize != 0) {
                                        JSONObject dataObj = dataArray.getJSONObject(0);
                                        if (Objects.nonNull(dataObj)) {
                                            Double cellValue = dataObj.getDouble("val");
                                            String relativePosition = dayWorkRelativePositionMap.get(value);
                                            switch (relativePosition) {
                                                case "left":
                                                    ExcelWriterUtil.addCellData(cellDataList, rowNum, colNum - 1, cellValue);
                                                    break;
                                                case "right":
                                                    ExcelWriterUtil.addCellData(cellDataList, rowNum, colNum + 1, cellValue);
                                                    break;
                                                case "top":
                                                    ExcelWriterUtil.addCellData(cellDataList, rowNum - 1, colNum, cellValue);
                                                    break;
                                                case "bottom":
                                                    ExcelWriterUtil.addCellData(cellDataList, rowNum + 1, colNum, cellValue);
                                                    break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return cellDataList;
    }

    /**
     * 通过tag点拿数据的API，路径可能会改变
     * @param version
     * @return
     */
    protected String getUrl(String version) {
//        return httpProperties.getSJUrlVersion(version) + "/glTagValue/getTagValue";
        return httpProperties.getSJUrlVersion(version) + "/tagValues";
    }
}
