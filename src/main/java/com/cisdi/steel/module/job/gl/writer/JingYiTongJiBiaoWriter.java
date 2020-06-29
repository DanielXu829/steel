package com.cisdi.steel.module.job.gl.writer;

import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.res.TapJyDTO;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import lombok.extern.slf4j.Slf4j;
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
public class JingYiTongJiBiaoWriter extends BaseGaoLuWriter {

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
            // 按月
            mapDataHandlerMonth(excelDTO, workbook, version);

            // 按月
            mapDataHandlerYear(excelDTO, workbook, version);

        } catch (Exception e) {
            log.error("处理模板数据失败", e);
        } finally {
            // 替换第二个sheet中标题中的年份
            //ExcelWriterUtil.replaceCurrentYearInTitle(workbook.getSheetAt(1), 0, 1, currentDate);
        }

        return workbook;
    }

    private void mapDataHandlerYear(WriterExcelDTO excelDTO, Workbook workbook, String version) {

        Sheet sheet = workbook.getSheetAt(1);
        // 标记行
        int itemRowNum = 4;
        BigDecimal defaultCellValue = new BigDecimal(0.0);
        // 获取excel占位符列
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
        List<CellData> cellDataList = new ArrayList<>();

        List<DateQuery> allMonthInCurrentYear = DateQueryUtil.buildYearMonthEach(new Date());

        for (int i = 0; i < allMonthInCurrentYear.size(); i++) {
            // 通过api获取按天的精益数据
            DateQuery dateQueryNoDelay = allMonthInCurrentYear.get(i);

            // 判断当前是何种精益报表，使用不同的dataType
            String dataType = null;
            if (JobEnum.gl_luwenhegelv.getCode().equals(excelDTO.getJobEnum().getCode())) {
                dataType = "lw";
            } else if (JobEnum.gl_luzhajianduhegelv.getCode().equals(excelDTO.getJobEnum().getCode())){
                dataType = "lz";
            } else if (JobEnum.gl_tieshuiyijipinlv.getCode().equals(excelDTO.getJobEnum().getCode())){
                dataType = "ts";
            }

            TapJyDTO tapJyDTO = this.getTapJyDTO(version, dateQueryNoDelay, dataType, "day");
            if (Objects.isNull(tapJyDTO)) {
                log.error("获取精益信息失败");
            }

            int row = itemRowNum + 1 + i;
            // 循环列
            for (int j = 0; j < itemNameList.size(); j++) {
                // 获取标记项单元格中的值
                String itemName = itemNameList.get(j);
                int col = j;
                if (StringUtils.isNotBlank(itemName)) {
                    switch (itemName) {
                        case "子项": {
                            // 获取子项
                            if (Objects.nonNull(tapJyDTO)) {
                                Integer fz = tapJyDTO.getFz();
                                if (Objects.nonNull(fz)) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, fz);
                                }
                            }
                            break;
                        }
                        case "母项": {
                            // 获取批次总数
                            if (Objects.nonNull(tapJyDTO)) {
                                Integer fm = tapJyDTO.getFm();
                                if (Objects.nonNull(fm)) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, fm);
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

    private void mapDataHandlerMonth(WriterExcelDTO excelDTO, Workbook workbook, String version) {

        Sheet sheet = workbook.getSheetAt(0);
        // 标记行
        int itemRowNum = 6;
        BigDecimal defaultCellValue = new BigDecimal(0.0);
        // 获取excel占位符列
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
        List<CellData> cellDataList = new ArrayList<>();

        // 构建一年12个月的每个月的起止时间
        List<Date> allDayBeginTimeInCurrentMonth = DateUtil.getAllDayBeginTimeInCurrentMonthBeforeDays(new Date(), 1);

        int fixLineCount = 0;
        for (int i = 0; i < allDayBeginTimeInCurrentMonth.size(); i++) {
            // 通过api获取按天的精益数据
            Date day = allDayBeginTimeInCurrentMonth.get(i);
            DateQuery dateQueryNoDelay = DateQueryUtil.buildTodayNoDelay(day);

            // 判断当前是何种精益报表，使用不同的dataType
            String dataType = null;
            if (JobEnum.gl_luwenhegelv.getCode().equals(excelDTO.getJobEnum().getCode())) {
                dataType = "lw";
            } else if (JobEnum.gl_luzhajianduhegelv.getCode().equals(excelDTO.getJobEnum().getCode())){
                dataType = "lz";
            } else if (JobEnum.gl_tieshuiyijipinlv.getCode().equals(excelDTO.getJobEnum().getCode())){
                dataType = "ts";
            }

            TapJyDTO tapJyDTO = this.getTapJyDTO(version, dateQueryNoDelay, dataType, "day");
            if (Objects.isNull(tapJyDTO)) {
                log.error("获取精益信息失败");
            }

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
                        case "子项": {
                            // 获取子项
                            if (Objects.nonNull(tapJyDTO)) {
                                Integer fz = tapJyDTO.getFz();
                                if (Objects.nonNull(fz)) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, fz);
                                }
                            }
                            break;
                        }
                        case "母项": {
                            // 获取批次总数
                            if (Objects.nonNull(tapJyDTO)) {
                                Integer fm = tapJyDTO.getFm();
                                if (Objects.nonNull(fm)) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, fm);
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
        //Date lastDay = allDayBeginTimeInCurrentMonth.get(allDayBeginTimeInCurrentMonth.size() - 1);
        // 替换当月天数和当前月份
        Date currentDate = DateUtil.addDays(new Date(), -1);
        // 替换第一个sheet中的标题中的日期
        ExcelWriterUtil.replaceCurrentMonthInTitle(sheet, 0, 1, currentDate);
        ExcelWriterUtil.replaceCurrentDateInTitle(sheet, "%月份%", currentDate, DateUtil.MMFormat);
        // 隐藏标题行
        sheet.getRow(itemRowNum).setZeroHeight(true);
    }

}
