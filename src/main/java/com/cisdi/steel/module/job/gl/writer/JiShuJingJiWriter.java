package com.cisdi.steel.module.job.gl.writer;

import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.*;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>Description: 8高炉技术经济月报表 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/12/28 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class JiShuJingJiWriter extends BaseGaoLuWriter {

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
            mapDataHandler(sheet, version);
        } catch (Exception e) {
            log.error("处理主工作表时产生错误", e);
        }

        return workbook;
    }

    private void mapDataHandler(Sheet sheet, String version) {
        // 标记行
        int itemRowNum = 6;
        String defaultCellValue = "";
        // 获取excel占位符列
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
        List<CellData> cellDataList = new ArrayList<>();
        List<Date> allDayBeginTimeInCurrentMonth = DateUtil.getAllDayBeginTimeInCurrentMonth(new Date());

        int fixLineCount = 0;
        for (int i = 0; i < allDayBeginTimeInCurrentMonth.size() - 1; i++) {
            // 通过api获取tapTPCDTO和MaterialExpendDTO数据
            Date day = allDayBeginTimeInCurrentMonth.get(i);
            DateQuery dateQuery = DateQueryUtil.buildDayWithBeginTimeForBoth(day);
            TapTPCDTO tapTPCDTO = getTapTPCDTO(version, day);

            MaterialExpendDTO materialExpendDTO = getMaterialExpendDTO(version, day);
            // 计算回用焦丁
            BigDecimal huiYongJiaoDing = getHuiYongJiaoDing(materialExpendDTO);
            BigDecimal ganJiaoLiang = getJiaoTanPingJunPiZhong(materialExpendDTO);

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
                        case "批数": {
                            // 获取批次总数
                            BigDecimal batchCount = getFirstTagValueByRange(version, dateQuery, batchCountTagName, "day");
                            ExcelWriterUtil.addCellData(cellDataList, row, col, batchCount);
                            break;
                        }
                        case "毛重": {
                            if (Objects.nonNull(tapTPCDTO) && CollectionUtils.isNotEmpty(tapTPCDTO.getData())) {
                                BigDecimal sumGrossWgt = getSumGrossWgt(tapTPCDTO);
                                if (sumGrossWgt.doubleValue() > 0) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, sumGrossWgt);
                                }
                            }
                            break;
                        }
                        case "净重": {
                            if (Objects.nonNull(tapTPCDTO) && CollectionUtils.isNotEmpty(tapTPCDTO.getData())) {
                                BigDecimal sumNetWgt = getSumNetWgt(tapTPCDTO);
                                if (sumNetWgt.doubleValue() > 0) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, sumNetWgt);
                                }
                            }
                            break;
                        }
                        case "干焦量": {
                            if (Objects.nonNull(materialExpendDTO) && CollectionUtils.isNotEmpty(materialExpendDTO.getData())) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, ganJiaoLiang);
                            }
                            break;
                        }
                        case "综合干焦量": {
                            if (Objects.nonNull(materialExpendDTO) && CollectionUtils.isNotEmpty(materialExpendDTO.getData())) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, ganJiaoLiang.add(huiYongJiaoDing));
                            }
                            break;
                        }
                        case "燃料比": {
                            // 获取燃料比
                            BigDecimal ranLiaoBi = getFirstTagValueByRange(version, dateQuery, "BF8_L2M_BX_FuelRate_1d_cur", "day");
                            ExcelWriterUtil.addCellData(cellDataList, row, col, ranLiaoBi);
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
        ExcelWriterUtil.replaceCurrentMonthInTitle(sheet, 1, 0);

        // TODO 替换当月天数
        // TODO 隐藏行首两行，改为隐藏一行
        sheet.getRow(itemRowNum).setZeroHeight(true);
        sheet.getRow(itemRowNum - 1).setZeroHeight(true);
    }

}
