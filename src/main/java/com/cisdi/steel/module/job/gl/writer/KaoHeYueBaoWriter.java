package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.*;
import com.cisdi.steel.dto.response.gl.res.MaterialExpend;
import com.cisdi.steel.dto.response.gl.res.ShiftTagValue;
import com.cisdi.steel.dto.response.gl.res.TapTPC;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>Description: 8高炉考核月报表 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/12/23 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class KaoHeYueBaoWriter extends BaseGaoLuWriter {


    /**
     * @param excelDTO 数据
     * @return
     */
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = "8.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
            log.error("在模板中获取version失败", e);
        }

        try {
            Sheet sheet = workbook.getSheet("铁罐装载率");
            // 填充报表主工作表“铁罐装载率”数据
            mapDataHandler(sheet, version);
        } catch (Exception e) {
            log.error("处理 铁罐装载率 时产生错误", e);
            throw e;
        }

        // 填充报表第二个工作表“班产燃料比”数据
        try {
            Sheet sheet2 = workbook.getSheet("班产燃料比");
            mapDataHandler2(sheet2, version);
        } catch (Exception e) {
            log.error("处理 班产燃料比 时产生错误", e);
            throw e;
        }

        return workbook;
    }

    private void mapDataHandler(Sheet sheet, String version) {
        // 标记行
        int itemRowNum = 3;
        String defaultCellValue = "";
        // 获取excel占位符列
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
        List<CellData> cellDataList = new ArrayList<>();
        List<Date> allDayBeginTimeInCurrentMonth = DateUtil.getAllDayBeginTimeInCurrentMonth(new Date());

        int fixLineCount = 0;
        for (int i = 0; i < allDayBeginTimeInCurrentMonth.size() - 1; i++) {
            // 通过api获取MaterialExpendDTO数据
            Date day = allDayBeginTimeInCurrentMonth.get(i);
            TapTPCDTO tapTPCDTO = getTapTPCDTO(version, day);
            long count = getCount(tapTPCDTO);
            long scopeCount = getScopeCount(tapTPCDTO);

            // 计算行
            if (i > 0 && i%10 ==0) {
                fixLineCount++;
            }
            int row = itemRowNum + 1 + fixLineCount + i;
            // 循环列
            for (int j = 0; j < itemNameList.size(); j++) {
                // 获取标记项单元格中的值
                String itemName = itemNameList.get(j);
                int col = j;
                if (StringUtils.isNotBlank(itemName)) {
                    switch (itemName) {
                        case "COUNT": {
                            if (count > 0) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, count);
                            }
                            break;
                        }
                        case "SCOPE_COUNT": {
                            if (scopeCount > 0) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, scopeCount);
                            }
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            }
        }
        ExcelWriterUtil.setCellValue(sheet, cellDataList);
        Date lastDay = allDayBeginTimeInCurrentMonth.get(allDayBeginTimeInCurrentMonth.size() - 1);
        ExcelWriterUtil.replaceCurrentMonthInTitle(sheet, 1, 1, lastDay);
        sheet.getRow(itemRowNum).setZeroHeight(true);
    }

    private void mapDataHandler2(Sheet sheet, String version) {
        // 标记行
        int itemRowNum = 2;
        String defaultCellValue = "";
        // 获取excel占位符列
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
        List<CellData> cellDataList = new ArrayList<>();
        List<Date> allDayBeginTimeInCurrentMonth = DateUtil.getAllDayBeginTimeInCurrentMonth(new Date());

        for (int i = 0; i < allDayBeginTimeInCurrentMonth.size() - 1; i++) {
            // 通过api获取MaterialExpendDTO数据
            Date day = allDayBeginTimeInCurrentMonth.get(i);
            TapTPCDTO tapTPCDTO = getTapTPCDTO(version, day);
            MaterialExpendDTO materialExpendDTO = getMaterialExpendDTO(version, day, "shift");

            // 计算行
            int row = itemRowNum + 1 + i * 2;
            // 循环列
            for (int j = 0; j < itemNameList.size(); j++) {
                // 获取标记项单元格中的值
                String itemName = itemNameList.get(j);
                int col = j;
                if (StringUtils.isNotBlank(itemName)) {
                    switch (itemName) {
                        case "出铁量": {
                            if (Objects.nonNull(tapTPCDTO) && CollectionUtils.isNotEmpty(tapTPCDTO.getData())) {
                                BigDecimal nightShiftSumNetWgt = getSumNetWgt(tapTPCDTO, "1");
                                if (nightShiftSumNetWgt.doubleValue() > 0) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, nightShiftSumNetWgt);
                                }
                                BigDecimal dayShiftSumNetWgt = getSumNetWgt(tapTPCDTO, "2");
                                if (dayShiftSumNetWgt.doubleValue() > 0) {
                                    ExcelWriterUtil.addCellData(cellDataList, row + 1, col, dayShiftSumNetWgt);
                                }
                            }
                            break;
                        }
                        case "焦炭": {
                            if (Objects.nonNull(materialExpendDTO) && CollectionUtils.isNotEmpty(materialExpendDTO.getData())) {
                                BigDecimal nightShiftSumWetWgt = getMaterialExpendWetWgt(materialExpendDTO, Arrays.asList("小块焦","大块焦"), "1");
                                if (nightShiftSumWetWgt.doubleValue() > 0) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, nightShiftSumWetWgt);
                                }
                                BigDecimal dayShiftSumWetWgt = getMaterialExpendWetWgt(materialExpendDTO, Arrays.asList("小块焦","大块焦"), "2");
                                if (dayShiftSumWetWgt.doubleValue() > 0) {
                                    ExcelWriterUtil.addCellData(cellDataList, row + 1, col, dayShiftSumWetWgt);
                                }
                            }
                            break;
                        }
                        case "回用焦": {
                            if (Objects.nonNull(materialExpendDTO) && CollectionUtils.isNotEmpty(materialExpendDTO.getData())) {
                                BigDecimal nightShiftSumWetWgt = getMaterialExpendWetWgt(materialExpendDTO, Arrays.asList("回用焦丁"), "1");
                                if (nightShiftSumWetWgt.doubleValue() > 0) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, nightShiftSumWetWgt);
                                }
                                BigDecimal dayShiftSumWetWgt = getMaterialExpendWetWgt(materialExpendDTO, Arrays.asList("回用焦丁"), "2");;
                                if (dayShiftSumWetWgt.doubleValue() > 0) {
                                    ExcelWriterUtil.addCellData(cellDataList, row + 1, col, dayShiftSumWetWgt);
                                }
                            }
                            break;
                        }
                        case "煤量": {
                            ShiftTagValueListDTO shiftTagValueListDTO = getShiftTagValueListDTO(version, day);
                            if (Objects.nonNull(shiftTagValueListDTO) && CollectionUtils.isNotEmpty(shiftTagValueListDTO.getData())) {
                                double nightShiftSumVal = getValSumByShift(shiftTagValueListDTO, "1");
                                if (nightShiftSumVal > 0) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, nightShiftSumVal);
                                }
                                double dayShiftSumVal = getValSumByShift(shiftTagValueListDTO, "2");
                                if (dayShiftSumVal > 0) {
                                    ExcelWriterUtil.addCellData(cellDataList, row + 1, col, dayShiftSumVal);
                                }
                            }
                            break;
                        }

                        default: {
                            break;
                        }
                    }
                }
            }
        }
        ExcelWriterUtil.setCellValue(sheet, cellDataList);
        Date lastDay = allDayBeginTimeInCurrentMonth.get(allDayBeginTimeInCurrentMonth.size() - 1);
        ExcelWriterUtil.replaceCurrentMonthInTitle(sheet, 0, 0, lastDay);
        sheet.getRow(itemRowNum).setZeroHeight(true);
    }

    /**
     * 每日0点0分0秒获取每日煤量数据
     * @param version
     * @param chargeNo
     * @return api数据
     */
    private ShiftTagValueListDTO getShiftTagValueListDTO(String version, Date date) {
        ShiftTagValueListDTO shiftTagValueListDTO = null;
        Map<String, String> queryParam = new HashMap();
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        queryParam.put("dateTime",  String.valueOf(dateBeginTime.getTime()));
        queryParam.put("granularity",  "hour");
        String meiLiangTagName = "BF8_L2C_BD_PCI_1h_avg";
        queryParam.put("tagName", meiLiangTagName);

        String shiftTagValueListUrl = httpProperties.getGlUrlVersion(version) + "/report/tagValue/getValSumByShift";
        String shiftTagValueListDTOStr = httpUtil.get(shiftTagValueListUrl, queryParam);
        if (StringUtils.isNotBlank(shiftTagValueListDTOStr)) {
            shiftTagValueListDTO = JSON.parseObject(shiftTagValueListDTOStr, ShiftTagValueListDTO.class);
            if (Objects.isNull(shiftTagValueListDTO) || CollectionUtils.isEmpty(shiftTagValueListDTO.getData())) {
                log.warn("[{}] 的ShiftTagValueListDTO数据为空", dateBeginTime);
            }
        }
        return shiftTagValueListDTO;
    }


    /**
     * 计算总罐数
     * @param tapTPCDTO
     * @return
     */
    private long getCount(TapTPCDTO tapTPCDTO) {
        long count = 0;
        if (Objects.nonNull(tapTPCDTO) && CollectionUtils.isNotEmpty(tapTPCDTO.getData())) {
            count = tapTPCDTO.getData().size();
        }
        return count;
    }

    /**
     * 计算netWt在240-275区间的个数
     * @param tapTPCDTO
     * @return
     */
    private long getScopeCount(TapTPCDTO tapTPCDTO) {
        long count = 0;
        if (Objects.nonNull(tapTPCDTO) && CollectionUtils.isNotEmpty(tapTPCDTO.getData())) {
             count = tapTPCDTO.getData().stream()
                    .filter(p -> (240d <= p.getNetWt().doubleValue() && p.getNetWt().doubleValue() <= 275d))
                     .count();
        }
        return count;
    }

    /**
     * 计算煤量
     * @param shiftTagValueListDTO
     * @param shift (1：夜班，2：白班)
     * @return
     */
    private double getValSumByShift(ShiftTagValueListDTO shiftTagValueListDTO, String shift) {
        double sum = 0;
        ShiftTagValue shiftTagValue = shiftTagValueListDTO.getData().stream()
                .filter(p -> (StringUtils.isBlank(shift) || shift.equals(p.getWorkShift())))
                .findAny().orElse(null);
        if (Objects.nonNull(shiftTagValue)) {
            sum = shiftTagValue.getVal();
        }
        return sum;
    }

}
