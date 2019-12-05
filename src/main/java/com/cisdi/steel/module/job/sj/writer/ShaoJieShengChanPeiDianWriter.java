package com.cisdi.steel.module.job.sj.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        Map<String, String> queryParam;
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
                    // 夜班、白班, size肯定为2
                    int size = dateQueries.size();
                    String apiResult;
                    for (int j = 0; j < size; j++) {
                        if (j == 0) {
                            queryParam = this.getQueryParam(dateQueries.get(j));
                            queryParam.put("tagName", value);
                            // apiResult = httpUtil.get(url, queryParam);
                            apiResult = "{'data': {'value': '0'}}";
                            JSONObject jsonObject = JSONObject.parseObject(apiResult);
                            if (Objects.nonNull(jsonObject)) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                if (Objects.nonNull(data)) {
                                    Double cellValue = data.getDouble("value");
                                    ExcelWriterUtil.addCellData(cellDataList, rowNum, colNum, cellValue);
                                }
                            }
                        } else if (j == 1) {
                            queryParam = this.getQueryParam(dateQueries.get(j));
                            queryParam.put("tagNames", value);
                            // apiResult = httpUtil.get(url, queryParam);
                            apiResult = "{'data': {'value': '1'}}";
                            JSONObject jsonObject = JSONObject.parseObject(apiResult);
                            if (Objects.nonNull(jsonObject)) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                if (Objects.nonNull(data)) {
                                    Double cellValue = data.getDouble("value");
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

        return cellDataList;
    }

    /**
     * 通过tag点拿数据的API，路径可能会改变
     * @param version
     * @return
     */
    protected String getUrl(String version) {
//        return httpProperties.getSJUrlVersion(version) + "/glTagValue/getTagValue";
        return httpProperties.getSJUrlVersion(version) + "/tagValue";
    }
}
