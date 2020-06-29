package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.MaterialExpendDTO;
import com.cisdi.steel.dto.response.gl.TapSummaryListDTO;
import com.cisdi.steel.dto.response.gl.TapTPCDTO;
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


@Component
@SuppressWarnings("ALL")
@Slf4j
public class LuWenHeGeLvWriter extends BaseGaoLuWriter {

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
            log.error("执行LuWenHeGeLvWriter");
//            // 正面-小时参数
//            handleFacadeXiaoShiCanShu(excelDTO, workbook, version);
//
//            // 正面-风口信息
//            handleFengKouXinXi(excelDTO, workbook, version);


        } catch (Exception e) {
            log.error("处理模板数据失败", e);
        } finally {
            // 替换第二个sheet中标题中的年份
            //ExcelWriterUtil.replaceCurrentYearInTitle(workbook.getSheetAt(1), 0, 1, currentDate);
        }

        return workbook;
    }

    private void mapDataHandlerMonth(Sheet sheet, String version) {
        // 标记行
        int itemRowNum = 6;
        BigDecimal defaultCellValue = new BigDecimal(0.0);
        // 获取excel占位符列
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
        List<CellData> cellDataList = new ArrayList<>();

        List<Date> allDayBeginTimeInCurrentMonth = DateUtil.getAllDayBeginTimeInCurrentMonthBeforeDays(new Date(), 1);

        int fixLineCount = 0;
        for (int i = 0; i < allDayBeginTimeInCurrentMonth.size(); i++) {
            // 通过api获取tapTPCDTO、MaterialExpendDTO、TapSummaryListDTO数据
            Date day = allDayBeginTimeInCurrentMonth.get(i);
            DateQuery dateQuery = DateQueryUtil.buildDayWithBeginTimeForBoth(day);
            TapTPCDTO tapTPCDTO = getTapTPCDTO(version, day);
            TapSummaryListDTO tapSummaryListDTO = getTapSummaryListDTO(version, day);

            DateQuery dateQueryNoDelay = DateQueryUtil.buildTodayNoDelay(day);
            //计算铁量净重
            Double actWeight = defaultCellValue.doubleValue();
            if (Objects.nonNull(tapSummaryListDTO) && CollectionUtils.isNotEmpty(tapSummaryListDTO.getData())) {
                actWeight = tapSummaryListDTO.getData().get(0).getActWeight();
            }
            // 计算回用焦丁、焦量、小块焦、矿石重量
            MaterialExpendDTO materialExpendDTO = getMaterialExpendDTO(version, day);
            BigDecimal huiYongJiaoDing = defaultCellValue, ganJiaoLiang = defaultCellValue, xiaoKuaiJiao = defaultCellValue,
                    materialZhongLiang = defaultCellValue, daKuaiJiao = defaultCellValue, penMei = defaultCellValue;
            if (Objects.nonNull(materialExpendDTO) && CollectionUtils.isNotEmpty(materialExpendDTO.getData())) {
                huiYongJiaoDing = getMaterialExpendWetWgt(materialExpendDTO, Arrays.asList("回用焦丁"));
                daKuaiJiao = getMaterialExpendWetWgt(materialExpendDTO, Arrays.asList("大块焦"));
                ganJiaoLiang = getMaterialExpendWetWgt(materialExpendDTO, Arrays.asList("小块焦","大块焦"));
                xiaoKuaiJiao = getMaterialExpendWetWgt(materialExpendDTO, Arrays.asList("小块焦"));
                penMei = getMaterialExpendWetWgt(materialExpendDTO, Arrays.asList("炼铁无烟煤","高挥发份烟煤"));
                materialZhongLiang = getMaterialExpendWetWgt(materialExpendDTO);
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
                        case "批数": {
                            // 获取批次总数
                            BigDecimal batchCount = getFirstTagValueByRange(version, dateQuery, batchCountTagName, "day");
                            if (batchCount.doubleValue() > 0) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, batchCount);
                            }
                            break;
                        }
                        case "出铁间批数": {
                            // 获取批次总数
                            BigDecimal countChargeNumByTapTimeRange = glDataUtil.getCountChargeNumByTapTimeRange(version, dateQueryNoDelay);
                            if (Objects.nonNull(countChargeNumByTapTimeRange)) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, countChargeNumByTapTimeRange);
                            }
                            break;
                        }
                        case "毛重": {
                            if (Objects.nonNull(tapSummaryListDTO) && CollectionUtils.isNotEmpty(tapSummaryListDTO.getData())) {
                                Double grossWgt = tapSummaryListDTO.getData().get(0).getGrossWgt();
                                if (Objects.nonNull(grossWgt) && grossWgt > 0) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, grossWgt);
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

    protected TapSummaryListDTO getTapSummaryListDTO(String version, Date date) {
        TapSummaryListDTO tapSummaryListDTO = null;
        Map<String, String> queryParam = new HashMap();
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        queryParam.put("dateTime",  String.valueOf(dateBeginTime.getTime()));
        queryParam.put("workShift",  "day");

        String tapSummaryListUrl = httpProperties.getGlUrlVersion(version) + "/report/tap/getTapSummary";
        String tapSummaryListStr = httpUtil.get(tapSummaryListUrl, queryParam);
        if (StringUtils.isNotBlank(tapSummaryListStr)) {
            tapSummaryListDTO = JSON.parseObject(tapSummaryListStr, TapSummaryListDTO.class);
            if (Objects.isNull(tapSummaryListDTO) || CollectionUtils.isEmpty(tapSummaryListDTO.getData())) {
                log.warn("[{}] 的TapSummaryListDTO数据为空", dateBeginTime);
            }
        }
        return tapSummaryListDTO;
    }

}
