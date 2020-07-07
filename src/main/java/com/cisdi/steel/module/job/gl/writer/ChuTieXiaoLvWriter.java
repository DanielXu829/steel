package com.cisdi.steel.module.job.gl.writer;

import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.TapSummaryListDTO;
import com.cisdi.steel.dto.response.gl.res.TapJyDTO;
import com.cisdi.steel.dto.response.gl.res.TapSummary;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Component
@SuppressWarnings("ALL")
@Slf4j
public class ChuTieXiaoLvWriter extends BaseGaoLuWriter {

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
        // 填充报表主工作表数据
        try {
            Sheet sheet = workbook.getSheetAt(0);
            // 填充报表主工作表数据
            mapDataHandler(sheet, version, excelDTO);
        } catch (Exception e) {
            log.error("处理主工作表时产生错误", e);
            throw e;
        }

        return workbook;
    }

    private void mapDataHandler(Sheet sheet, String version, WriterExcelDTO excelDTO) {
        // 标记行
        int itemRowNum = 4;
        // 获取excel占位符列
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
        List<CellData> cellDataList = new ArrayList<>();

        List<DateQuery> allDayBeginTimeInCurrentYear = DateQueryUtil.buildYearDayWithThur2Wed(DateUtil.addDays(new Date(), -1));

        for (int i = 0; i < allDayBeginTimeInCurrentYear.size(); i++) {
            // 通过api获取按天的精益数据
            DateQuery dateQueryNoDelay = allDayBeginTimeInCurrentYear.get(i);

            // 获取tapSummary
            TapSummary tapSummary = null;
            TapSummaryListDTO tapSummaryListDTO = getTapSummaryListDTO(version, dateQueryNoDelay.getStartTime(), "today");
            if (Objects.nonNull(tapSummaryListDTO) && CollectionUtils.isNotEmpty(tapSummaryListDTO.getData())) {
                tapSummary = tapSummaryListDTO.getData().get(0);
            }

            int row = itemRowNum + 1 + i;
            // 循环列
            for (int j = 0; j < itemNameList.size(); j++) {
                // 获取标记项单元格中的值
                String itemName = itemNameList.get(j);
                int col = j;
                if (StringUtils.isNotBlank(itemName)) {
                    switch (itemName) {
                        case "日期": {
                            ExcelWriterUtil.addCellData(cellDataList, row, col, dateQueryNoDelay.getStartTime());
                            break;
                        }
                        case "星期": {
                            ExcelWriterUtil.addCellData(cellDataList, row, col, DateUtil.getWeekString(dateQueryNoDelay.getStartTime()).substring(2));
                            break;
                        }
                        case "hmRatio": {
                            // 获取有效出铁比率
                            if (Objects.nonNull(tapSummary)) {
                                Double hmRatio = tapSummary.getHmRatio();
                                if (Objects.nonNull(hmRatio)) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, hmRatio);
                                }
                            }
                            break;
                        }
                        case "slagRatio": {
                            // 获取有效出渣比率
                            if (Objects.nonNull(tapSummary)) {
                                Double slagRatio = tapSummary.getSlagRatio();
                                if (Objects.nonNull(slagRatio)) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, slagRatio);
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
        // 替换当月天数和当前月份
        Date currentDate = DateUtil.addDays(new Date(), -1);
        // 替换第一个sheet中的标题中的年份
        ExcelWriterUtil.replaceCurrentYearInTitle(sheet, 0, 1, currentDate);
        // 隐藏标题行
        sheet.getRow(itemRowNum).setZeroHeight(true);
    }

}
