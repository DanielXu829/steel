package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.*;
import com.cisdi.steel.dto.response.gl.res.*;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
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
    // 标记行
    private static int itemRowNum = 3;

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

        Sheet sheet = workbook.getSheetAt(0);
        sheet.getRow(itemRowNum).setZeroHeight(true);

        // 填充报表主工作表数据
        mapDataHandler(sheet, version);

        // 动态替换报表首行标题中的日期
        Cell titleCell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(sheet, 1), 1);
        String stringCellValue = titleCell.getStringCellValue();
        String currentMonth = DateFormatUtils.format(new Date(), DateUtil.yyyyMM);
        stringCellValue = stringCellValue.replaceAll("%当前月份%", currentMonth);
        titleCell.setCellValue(stringCellValue);

        return workbook;
    }

    private void mapDataHandler(Sheet sheet, String version) {
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
    }

    /**
     * 每日20点0分0秒获取每日炉料消耗数据，可用于月报展示
     * @param version
     * @param chargeNo
     * @return api数据
     */
    private TapTPCDTO getTapTPCDTO(String version, Date date) {
        TapTPCDTO tapTPCDTO = null;
        Map<String, String> queryParam = new HashMap();
        Long dateTime = DateUtil.getDateBeginTimeOfTwenty(date).getTime();
        queryParam.put("dateTime",  String.valueOf(dateTime));

        String tapTPCUrl = httpProperties.getGlUrlVersion(version) + "/report/tap/getTapTPCByRange";
        String tapTPCDTOStr = httpUtil.get(tapTPCUrl, queryParam);
        if (StringUtils.isNotBlank(tapTPCDTOStr)) {
            tapTPCDTO = JSON.parseObject(tapTPCDTOStr, TapTPCDTO.class);
        }
        return tapTPCDTO;
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


}
