package com.cisdi.steel.module.job.gl.writer;

import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.FastJSONUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>Description: 8高炉冷却水冷却壁月报 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020/06/18 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class LengQueBiYueBaoWriter extends BaseGaoLuWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = "8.0";
        Date date = new Date();
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
            log.error("在模板中获取version失败", e);
        }
        List<CellData> resultList = new ArrayList<>();
        try {
            DateQuery dateQuery = getDateQuery(excelDTO);
            int beginRow = 3;
            Sheet sheet = workbook.getSheetAt(0);
            List<Date> allDayBeginTimeInCurrentMonth = DateUtil.getAllDayBeginTimeInCurrentMonthBeforeDays(dateQuery.getRecordDate(), 1);
            if(allDayBeginTimeInCurrentMonth.size() > 0) {
                date = allDayBeginTimeInCurrentMonth.get(0);
            }
            int fixLineCount = 0;
            for (int i = 0; i < allDayBeginTimeInCurrentMonth.size(); i++) {
//                DateQuery eachDateQuery = DateQueryUtil.buildDayAheadTwoHour(allDayBeginTimeInCurrentMonth.get(i));
//                Date day = eachDateQuery.getRecordDate();
                Date day = allDayBeginTimeInCurrentMonth.get(i);
                // 计算行
                if (i == 10 || i == 20) {
                    fixLineCount++;
                }
                int rowIndex = beginRow + fixLineCount + i;
                handleData(workbook, version, rowIndex, day);
            }
            ExcelWriterUtil.setCellValue(sheet, resultList);
        } catch (Exception e) {
            log.error("处理 冷却水冷却壁月报 时产生错误", e);
            throw e;
        } finally {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (!Objects.isNull(sheet) && !workbook.isSheetHidden(i)) {
                    // 全局替换 当前日期
                    ExcelWriterUtil.replaceCurrentMonthInTitleWithSpace(sheet, 0, 0, date);
                    PoiCustomUtil.clearPlaceHolder(sheet);
                }
            }
        }
        return workbook;
    }

    /**
     * 处理一行冷却水数据
     * @param version
     * @param tagName
     * @param date
     * @return
     */
    private Double handleLengQueShui (String version, String tagName, Date date) {
        String[] names = tagName.split(" - ");
        BigDecimal value = null;
        if (names.length == 2) {
            value = getLatestTagValue(version, names[0], date).subtract(getLatestTagValue(version, names[1], date));
        } else {
            value = getLatestTagValue(version, tagName, date);
        }
        if (Objects.nonNull(value)) {
            return value.doubleValue();
        }
        return null;
    }

    /**
     * 处理冷却壁数据
     * @param version
     * @param tagName
     * @param date
     * @return
     */
    private Double handleLengQueBi (String version, String tagName, Date date) {
        Double value = null;
        String[] names = tagName.split(";");
        List<String> tagNames = new ArrayList<>();
        Collections.addAll(tagNames, names);
        //处理一列数据
        List<Double> list = getLatestTagValuesList(version, tagNames, date);
        if (list.size() > 0 && tagName.length() > 2) {
            value = ExcelWriterUtil.executeSpecialList(tagName.substring(tagName.lastIndexOf("_") + 1, tagName.length() - 1), list);
        }
        return value;
    }

    /**
     * 处理冷却水和冷却壁数据
     * @param workbook
     * @param version
     * @param rowIndex
     * @param date
     */
    private void handleData(Workbook workbook, String version, int rowIndex, Date date) {
        try {
            List<CellData> resultList = new ArrayList<>();
            Sheet sheet = workbook.getSheet("_data");
            if (Objects.isNull(sheet)) {
                return;
            }
            // 直接拿到tag点名, 无需根据别名再去获取tag点名
            List<String> tagNames = PoiCustomUtil.getRowCelVal(sheet, 2);
            for (int i = 0; i < tagNames.size(); i++) {
                String tagName = tagNames.get(i);
                Double value = null;
                if(!tagName.contains(";")) {
                    value = handleLengQueShui(version, tagName, date);
                } else {
                    value = handleLengQueBi(version, tagName, date);
                }
                ExcelWriterUtil.addCellData(resultList, rowIndex, i, value);
            }
            ExcelWriterUtil.setCellValue(sheet, resultList);
        } catch (Exception e) {
            log.error("冷却水冷却壁月报处理冷却水时产生错误", e);
        }
    }

    /**
     * 获取latest tag value list
     *
     * @param version
     * @param tagNames
     * @param nameStufix
     * @param date
     * @return
     */
    private List<Double> getLatestTagValuesList(String version, List<String> tagNames, Date date) {
        List<Double> list = new ArrayList<>();
        for (String tagName : tagNames) {
            BigDecimal tagValue = getLatestTagValue(version, tagName, date);
            if (Objects.nonNull(tagValue)) {
                list.add(tagValue.doubleValue());
            }
        }
        return list;
    }
}
