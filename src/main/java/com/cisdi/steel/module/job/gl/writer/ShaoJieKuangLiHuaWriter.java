package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.AnalysisValueDTO;
import com.cisdi.steel.dto.response.gl.res.AnalysisValue;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private Workbook workbook;

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version ="8.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e){
            log.info("从模板中获取version失败", e);
        }

        DateQuery date = this.getDateQueryBeforeOneDay(excelDTO);
        DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());

        // 第1个sheet 获取并隐藏标记行
        Sheet sheetSinter = workbook.getSheetAt(0);
        List<String> columnsSinter = PoiCustomUtil.getRowCelVal(sheetSinter, itemRowNum);
        sheetSinter.getRow(itemRowNum).setZeroHeight(true);

        // 第2个sheet 获取并隐藏标记行
        Sheet sheetPellets = workbook.getSheetAt(1);
        List<String> columnsPellets = PoiCustomUtil.getRowCelVal(sheetPellets, itemRowNum);
        sheetPellets.getRow(itemRowNum).setZeroHeight(true);

        // 第3个sheet 获取并隐藏标记行
        Sheet sheetLumpore = workbook.getSheetAt(2);
        List<String> columnsLumpore = PoiCustomUtil.getRowCelVal(sheetLumpore, itemRowNum);
        sheetLumpore.getRow(itemRowNum).setZeroHeight(true);

        String[] handleArray = new String[]{"TFe", "FeO", "CaO", "MgO", "SiO2", "S"};
        List<CellData> cellDataListSinterLC = handleData(sheetSinter, dateQuery, version, columnsSinter, "S4_SINTER",
                "LC", handleArray);
        List<CellData> cellDataListSinterLP = handleData(sheetSinter, dateQuery, version, columnsSinter, "S4_SINTER",
                "LP", new String[]{});
        List<CellData> cellDataListPellets = handleData(sheetPellets, dateQuery, version, columnsLumpore, "PELLETS", "LC", handleArray);
        List<CellData> cellDataListLumpore = handleData(sheetLumpore, dateQuery, version, columnsLumpore, "LUMPORE", "LC", handleArray);

        ExcelWriterUtil.setCellValue(sheetSinter, cellDataListSinterLC);
        ExcelWriterUtil.setCellValue(sheetSinter, cellDataListSinterLP);
        ExcelWriterUtil.setCellValue(sheetPellets, cellDataListPellets);
        ExcelWriterUtil.setCellValue(sheetLumpore, cellDataListLumpore);


        Date currentDate = DateUtil.addDays(new Date(), -1);
        for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet tempSheet = workbook.getSheetAt(i);
            // 清除标记项(例如:{块矿.矿种})
            if (!Objects.isNull(tempSheet) && !workbook.isSheetHidden(i)) {
                // 全局替换 当前日期
                ExcelWriterUtil.replaceCurrentDateInTitle(tempSheet, "%当前日期%", currentDate);
                PoiCustomUtil.clearPlaceHolder(tempSheet);
            }
        }

        return workbook;
    }

    /**
     * 获取数据并处理
     * @param sheet
     * @param dateQuery
     * @param version
     * @param columns
     * @param category
     * @param type
     * @return
     */
    protected List<CellData> handleData(Sheet sheet, DateQuery dateQuery, String version, List<String> columns,
                                        String category, String type, String[] handleArray) {
        List<CellData> cellDataList = new ArrayList<>();
        String jsonData = getData(dateQuery, version, category, type);
        AnalysisValueDTO analysisValueDTO = null;
        if (StringUtils.isNotBlank(jsonData)) {
            analysisValueDTO = JSON.parseObject(jsonData, AnalysisValueDTO.class);
        }

        if (Objects.isNull(analysisValueDTO)) {
            return null;
        }

        List<AnalysisValue> data = analysisValueDTO.getData();
        if (data == null || data.isEmpty()) {
            log.warn(category + "-" + type + " 接口中无数据");
            return null;
        }

        if (columns == null || columns.isEmpty()) {
            log.warn("模板中标记行无数据");
            return null;
        }
        int size = columns.size();
        int dataSize = data.size();

        if (data != null && !data.isEmpty()) {
            for (int j = 0; j < dataSize; j++) {
                if (Objects.nonNull(data.get(j).getValues())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    Date clock = data.get(j).getAnalysis().getClock();
                    String time = sdf.format(clock);
                    if (StringUtils.isNotBlank(time)) {
                        ExcelWriterUtil.addCellData(cellDataList, beginRowNum + j, 1, time);
                    }
                    for (int i = 0; i < size; i++) {
                        String column = columns.get(i);
                        if (StringUtils.isNotBlank(column)) {
                            String[] columnSplit = column.split("_");
                            if (columnSplit.length == 2) {
                                String compoundName = columnSplit[1];
                                if (columnSplit[0].equals(type)) {
                                    BigDecimal cellValue = data.get(j).getValues().get(compoundName);
                                    if (Objects.nonNull(cellValue)) {
                                        if (ArrayUtils.contains(handleArray,compoundName)) {
                                            ExcelWriterUtil.addCellData(cellDataList, beginRowNum + j, i, cellValue.multiply(new BigDecimal(100)));
                                        } else {
                                            ExcelWriterUtil.addCellData(cellDataList, beginRowNum + j, i, cellValue);
                                        }
                                    } else {
                                        log.warn(time + ":  " + category + "-" + compoundName + " 接口返回无数据");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 设置边框样式
        switch (category) {
            case "S4_SINTER":
                setCellStyle(workbook, sheet, dataSize, beginRowNum, beginColumnNum, endColumnNum_sj);
                break;
            case "PELLETS":
                setCellStyle(workbook, sheet, dataSize, beginRowNum, beginColumnNum, endColumnNum_qt);
                break;
            case "LUMPORE":
                setCellStyle(workbook, sheet, dataSize, beginRowNum, beginColumnNum, endColumnNum_kk);
                break;
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
    protected String getData(DateQuery dateQuery, String version, String category, String type) {
        Map<String, String> queryParam = this.getQueryParam(dateQuery);
        queryParam.put("category", category);
        queryParam.put("type", type);

        return httpUtil.get(getUrl(version), queryParam);
    }

    /**
     * 如果数据超出原本行数 则需要设置表格边框样式
     * @param workbook
     * @param sheet
     * @param clockArray
     */
    private void setCellStyle(Workbook workbook, Sheet sheet, int dataSize, int beginRowNum, int beginColumnNum, int endColumnNum) {
        int lastRowNum = itemRowNum + dataSize;
        int lastRowNumOld = sheet.getLastRowNum();
        if (lastRowNum > (lastRowNumOld - 1)) {
            ExcelWriterUtil.setBorderStyle(workbook, sheet, beginRowNum, lastRowNum, beginColumnNum, endColumnNum, BorderStyle.MEDIUM);
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
