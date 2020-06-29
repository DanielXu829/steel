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
public class GuiLiuShuangMingZhongLvWriter extends BaseGaoLuWriter {

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
            throw e;
        }

        return workbook;
    }

    private void mapDataHandler(Sheet sheet, String version) {
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
                        case "净重": {
                            if (Objects.nonNull(actWeight) && actWeight > 0) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, actWeight);
                            }
                            break;
                        }
                        case "一级品率": {
                            // 获取批次总数
                            BigDecimal firstGradeRateInRange = glDataUtil.getFirstGradeRateInRange(version, dateQueryNoDelay);
                            if (Objects.nonNull(firstGradeRateInRange)) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, firstGradeRateInRange);
                            }
                            break;
                        }
                        case "回收焦比": {
                            // 回用焦丁 * 1000 / 铁量
                            if (huiYongJiaoDing.doubleValue() > 0 && Objects.nonNull(actWeight) && actWeight > 0){
                                BigDecimal huiShouJiaoBi = huiYongJiaoDing.multiply(new BigDecimal(1000)).divide(new BigDecimal(actWeight), 2, BigDecimal.ROUND_HALF_UP);
                                ExcelWriterUtil.addCellData(cellDataList, row, col, huiShouJiaoBi);
                            }
                            break;
                        }
                        case "小块焦比": {
                            // 小块焦 * 1000 / 铁量
                            if (xiaoKuaiJiao.doubleValue() > 0 && Objects.nonNull(actWeight) && actWeight > 0){
                                BigDecimal xiaoKuaiJiaoBi = xiaoKuaiJiao.multiply(new BigDecimal(1000)).divide(new BigDecimal(actWeight), 2, BigDecimal.ROUND_HALF_UP);
                                ExcelWriterUtil.addCellData(cellDataList, row, col, xiaoKuaiJiaoBi);
                            }
                            break;
                        }
                        case "焦炭负荷": {
                            // 矿石重量 /（大块焦+小块焦+回用焦丁）
                            if (materialZhongLiang.doubleValue() > 0 && (huiYongJiaoDing.doubleValue() > 0 || ganJiaoLiang.doubleValue() > 0)){
                                BigDecimal kuangShiZhongLiang = materialZhongLiang.subtract(ganJiaoLiang).subtract(huiYongJiaoDing).subtract(penMei);
                                BigDecimal jiaoTanFuHe = kuangShiZhongLiang.divide(ganJiaoLiang.add(huiYongJiaoDing), 2, BigDecimal.ROUND_HALF_UP);
                                ExcelWriterUtil.addCellData(cellDataList, row, col, jiaoTanFuHe);
                            }
                            break;
                        }
                        case "干焦量": {
                            if (ganJiaoLiang.doubleValue() > 0) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, ganJiaoLiang);
                            }
                            break;
                        }
                        case "综合干焦量": {
                            if (ganJiaoLiang.doubleValue() > 0 || huiYongJiaoDing.doubleValue() > 0) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, ganJiaoLiang.add(huiYongJiaoDing));
                            }
                            break;
                        }
                        case "燃料比": {
                            // 获取燃料比
                            BigDecimal ranLiaoBi = getFirstTagValueByRange(version, dateQuery, "BF8_L2M_BX_FuelRate_1d_cur", "day");
                            if(ranLiaoBi.doubleValue() > 0) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, ranLiaoBi);
                            }
                            break;
                        }
                        case "铁水温度": {
                            // 获取铁水温度
                            if (Objects.nonNull(tapSummaryListDTO) && CollectionUtils.isNotEmpty(tapSummaryListDTO.getData())) {
                                Double tapTemp = tapSummaryListDTO.getData().get(0).getTapTemp();
                                if (Objects.nonNull(tapTemp) && tapTemp > 0) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, tapTemp);
                                }
                            }
                            break;
                        }
                        case "出铁次数": {
                            // 获取出铁次数
                            if (Objects.nonNull(tapSummaryListDTO) && CollectionUtils.isNotEmpty(tapSummaryListDTO.getData())) {
                                Integer tapNum = tapSummaryListDTO.getData().get(0).getTapNum();
                                if (Objects.nonNull(tapNum) && tapNum > 0) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, tapNum);
                                }
                            }
                            break;
                        }
                        case "茵巴作业率": {
                            // 获取批次总数
                            BigDecimal inbaRateInRange = glDataUtil.getInbaRateInRange(version, dateQueryNoDelay);
                            if (Objects.nonNull(inbaRateInRange)) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, inbaRateInRange);
                            }
                            break;
                        }
                        case "铁量差": {
                            // 获取燃料比
                            if (Objects.nonNull(tapSummaryListDTO) && CollectionUtils.isNotEmpty(tapSummaryListDTO.getData())) {
                                Double theoryWeight = tapSummaryListDTO.getData().get(0).getTheoryWeight();
                                if (Objects.nonNull(actWeight) && actWeight > 0 && Objects.nonNull(theoryWeight) && theoryWeight > 0) {
                                    double tieLiangCha = actWeight.doubleValue() - theoryWeight.doubleValue();
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, tieLiangCha);
                                }
                            }
                            break;
                        }
                        case "大块焦": {
                            if (Objects.nonNull(daKuaiJiao)) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, daKuaiJiao);
                            }
                            break;
                        }
                        case "小块焦": {
                            if (Objects.nonNull(xiaoKuaiJiao)) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, xiaoKuaiJiao);
                            }
                            break;
                        }
                        case "回用焦丁": {
                            if (Objects.nonNull(huiYongJiaoDing)) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, huiYongJiaoDing);
                            }
                            break;
                        }
                        case "喷煤": {
                            if (Objects.nonNull(penMei)) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, penMei);
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
        Date lastDay = new Date();
        ExcelWriterUtil.replaceCurrentMonthInTitle(sheet, 1, 0, lastDay);
        ExcelWriterUtil.replaceDaysOfMonthInTitle(sheet, 0, 4, lastDay);
        // TODO 隐藏行首两行，改为隐藏一行
        sheet.getRow(itemRowNum).setZeroHeight(true);
        sheet.getRow(itemRowNum - 1).setZeroHeight(true);
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
