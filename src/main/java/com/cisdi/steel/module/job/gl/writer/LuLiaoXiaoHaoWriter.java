package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.AnaItemValDTO;
import com.cisdi.steel.dto.response.gl.ChargeDTO;
import com.cisdi.steel.dto.response.gl.MaterialExpendDTO;
import com.cisdi.steel.dto.response.gl.TagValueListDTO;
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
 * <p>Description: 8高炉炉料消耗月报表 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/12/23 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class LuLiaoXiaoHaoWriter extends BaseGaoLuWriter {
    // 标记行
    private static int itemRowNum = 4;

    // 获取最新的chargeNo tagname
    private static String tagName = "BF8_L2M_SH_ChargeVariation_evt";

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
        Cell titleCell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(sheet, 0), 0);
        String stringCellValue = titleCell.getStringCellValue();
        String currentMonth = DateFormatUtils.format(new Date(), DateUtil.yyyyMM);
        stringCellValue = stringCellValue.replaceAll("%当前月份%", currentMonth);
        titleCell.setCellValue(stringCellValue);

        return workbook;
    }

    private void mapDataHandler(Sheet sheet, String version) {
        double defaultCellValue = 0.0;
        // 获取excel占位符列
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
        List<CellData> cellDataList = new ArrayList<>();
        List<Date> allDayBeginTimeInCurrentMonth = DateUtil.getAllDayBeginTimeInCurrentMonth(new Date());

        int fixLineCount = 0;
        for (int i = 0; i < allDayBeginTimeInCurrentMonth.size(); i++) {
            // 通过api获取MaterialExpendDTO数据
            Date day = allDayBeginTimeInCurrentMonth.get(i);
            MaterialExpendDTO materialExpendDTO = getMaterialExpendDTO(version, day);
            // 获取批次总数
            DateQuery dateQuery = DateQueryUtil.buildDayWithBeginTimeForBoth(day);
            BigDecimal batchCount = getFirstTagValueByRange(version, dateQuery, batchCountTagName, "day");
            // 计算原料配比总量
            BigDecimal yuanLiaoPeiBiCount = getYuanLiaoPeiBiCount(materialExpendDTO);
            // 获取当天最后一条chargeNo对应的COO
            ChargeDTO latestCOO = getLatestCOO(version, day);

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
                    ExcelWriterUtil.addCellData(cellDataList, row, col, defaultCellValue);
                    switch (itemName) {
                        case "DAY": {
                            ExcelWriterUtil.addCellData(cellDataList, row, col, i + 1);
                            break;
                        }
                        case "IRON,Tfe": {
                            AnaItemValDTO anaChargeValue = getAnaChargeValue(version, day, "IRON", "TFe", "day");
                            if ((null != anaChargeValue)&&(null != anaChargeValue.getData())) {
                                Double val = anaChargeValue.getData() * 100;
                                ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            }
                            break;
                        }
                        case "ALL,Aggl": {
                            AnaItemValDTO anaChargeValue = getAnaChargeValue(version, day, "ALL", "Aggl", "day");
                            if ((null != anaChargeValue)&&(null != anaChargeValue.getData())) {
                                Double val = anaChargeValue.getData() * 100;
                                ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            }
                            break;
                        }
                        case "*烧结矿": {
                            //所以matCname以“烧结矿”结尾的material之和
                            BigDecimal shaoJieKuang = materialExpendDTO.getData().stream()
                                    .filter(p -> StringUtils.endsWith(p.getMatCname(), "烧结矿"))
                                    .map(MaterialExpend::getWetWgt)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            if (Objects.nonNull(shaoJieKuang) && Objects.nonNull(yuanLiaoPeiBiCount) && yuanLiaoPeiBiCount.doubleValue() > 0) {
                                BigDecimal val = shaoJieKuang.divide(yuanLiaoPeiBiCount, 3, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                                ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            }
                            break;
                        }
                        case "OHN-N":
                        case "OIK-N":
                        case "PCQ-N":
                        case "PEQ-N": {
                            // 通过matCode获取MaterialExpend中对应的wetWgt, 再除以总量
                            MaterialExpend materialExpend = materialExpendDTO.getData().stream()
                                    .filter(p -> itemName.equals(p.getMatCode()))
                                    .findAny().orElse(null);
                            if (Objects.nonNull(materialExpend) && Objects.nonNull(materialExpend.getWetWgt()) && Objects.nonNull(yuanLiaoPeiBiCount) && yuanLiaoPeiBiCount.doubleValue() > 0) {
                                BigDecimal val = materialExpend.getWetWgt().divide(yuanLiaoPeiBiCount, 3, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                                ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            }
                            break;
                        }
                        case "回用焦丁批重": {
                            BigDecimal huiYongJiaoDing = getMaterialExpendWetWgt(materialExpendDTO, Arrays.asList("回用焦丁"));
                            BigDecimal val = new BigDecimal(0);
                            if (batchCount.intValue() > 0) {
                                val = huiYongJiaoDing.multiply(new BigDecimal(1000)).divide(batchCount, 0, BigDecimal.ROUND_HALF_UP);
                            }
                            ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            break;
                        }
                        case "小焦批重": {
                            BigDecimal huiYongJiaoDing = getMaterialExpendWetWgt(materialExpendDTO, Arrays.asList("小块焦"));
                            BigDecimal val = new BigDecimal(0);
                            if (batchCount.intValue() > 0) {
                                val = huiYongJiaoDing.multiply(new BigDecimal(1000)).divide(batchCount, 0, BigDecimal.ROUND_HALF_UP);
                            }
                            ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            break;
                        }
                        case "矿石平均批重": {
                            // 计算o总量
                            BigDecimal oCount = getOCount(materialExpendDTO);
                            // 计算回用焦丁
                            BigDecimal huiYongJiaoDing = getMaterialExpendWetWgt(materialExpendDTO, Arrays.asList("回用焦丁"));
                            BigDecimal subtract = oCount.subtract(huiYongJiaoDing);
                            BigDecimal val = new BigDecimal(0);
                            if (batchCount.intValue() > 0) {
                                val = subtract.multiply(new BigDecimal(1000)).divide(batchCount, 0, BigDecimal.ROUND_HALF_UP);
                            }
                            ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            break;
                        }
                        case "焦炭平均批重": {
                            BigDecimal jiaoTanPingJunPiZhong = getMaterialExpendWetWgt(materialExpendDTO, Arrays.asList("小块焦","大块焦"));
                            BigDecimal val = new BigDecimal(0);
                            if (batchCount.intValue() > 0) {
                                val = jiaoTanPingJunPiZhong.multiply(new BigDecimal(1000)).divide(batchCount, 0, BigDecimal.ROUND_HALF_UP);
                            }
                            ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            break;
                        }
                        case "焦碳": {
                            if (Objects.nonNull(latestCOO)) {
                                List<BatchData> data = latestCOO.getData();
                                if (CollectionUtils.isNotEmpty(data)) {
                                    List<BatchDistribution> cDistributions = data.get(0).getDistributions();
                                    String val = getPosition(cDistributions) + "-" + getRoundact(cDistributions);
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                                }
                            }
                            break;
                        }
                        case "矿石": {
                            if (Objects.nonNull(latestCOO)) {
                                List<BatchData> data = latestCOO.getData();
                                if (CollectionUtils.isNotEmpty(data)) {
                                    List<BatchDistribution> o1Distributions = data.get(1).getDistributions();
                                    String val = getPosition(o1Distributions) + "-" + getRoundact(o1Distributions);
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                                }
                            }
                            break;
                        }
                        case "小烧": {
                            if (Objects.nonNull(latestCOO)) {
                                List<BatchData> data = latestCOO.getData();
                                if (CollectionUtils.isNotEmpty(data)) {
                                    List<BatchDistribution> o2Distributions = data.get(2).getDistributions();
                                    String val = getPosition(o2Distributions) + "-" + getRoundact(o2Distributions);
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                                }
                            }
                            break;
                        }
                        case "N/A": {
                            ExcelWriterUtil.addCellData(cellDataList, row, col, defaultCellValue);
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
     * 获取原料配比之和
     * @param materialExpendDTO
     * @return
     */
    private BigDecimal getYuanLiaoPeiBiCount(MaterialExpendDTO materialExpendDTO) {
        List<String> matCodes = Arrays.asList("OHN-N", "OIK-N", "PCQ-N", "PEQ-N");
        BigDecimal yuanLiaoPeibiCount = materialExpendDTO.getData().stream()
                .filter(p -> (matCodes.contains(p.getMatCode()) || StringUtils.endsWith(p.getMatCname(), "烧结矿")))
                .map(MaterialExpend::getWetWgt)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return yuanLiaoPeibiCount;
    }

    /**
     * 获取本次O之和  元数据
     * @param materialExpendDTO
     * @return
     */
    private BigDecimal getOCount(MaterialExpendDTO materialExpendDTO) {
        BigDecimal oCount = materialExpendDTO.getData().stream()
                .map(MaterialExpend::getWetWgt)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return oCount;
    }

    /**
     * 根据参数获取anaCharge  元数据，例如“矿石平均含铁”或“熟料率”
     * @param version
     * @param date
     * @param category
     * @param anaItemName
     * @param granularity
     * @return
     */
    private AnaItemValDTO getAnaChargeValue(String version, Date date, String category, String anaItemName, String granularity) {
        AnaItemValDTO anaItemValDTO = null;
        Map<String, String> queryParam = new HashMap();
        Long dateTime = DateUtil.getDateBeginTime(date).getTime();
        queryParam.put("category",  category);
        queryParam.put("anaItemName",  anaItemName);
        queryParam.put("granularity",  granularity);
        queryParam.put("dateTime",  String.valueOf(dateTime));
        String anaChargeTfeUrl = httpProperties.getGlUrlVersion(version) + "/report/anaCharge/getItemValInTime";
        String anaItemValDTOStr = httpUtil.get(anaChargeTfeUrl, queryParam);
        if (StringUtils.isNotBlank(anaItemValDTOStr)) {
            anaItemValDTO = JSON.parseObject(anaItemValDTOStr, AnaItemValDTO.class);
        } else {
            log.warn(DateFormatUtils.format(date, DateUtil.MMddChineseFormat) + "的 " + anaItemName + " 为空");
        }

        return anaItemValDTO;
    }

    /**
     * 获取某一天最后一个COO数据
     * @param version
     * @param day
     * @return
     */
    private ChargeDTO getLatestCOO(String version, Date day){
        ChargeDTO chargeDTO = null;
        DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(day);
        TagValueListDTO tagValueListDTO = getTagValueListDTO(dateQuery, version, tagName);
        if (Objects.nonNull(tagValueListDTO) && CollectionUtils.isNotEmpty(tagValueListDTO.getData())) {
            TagValue tagValue = tagValueListDTO.getData().get(tagValueListDTO.getData().size() - 1);
            BigDecimal chargeNo = tagValue.getVal();
            chargeDTO = getChargeDTO(version, chargeNo.intValue());
        }

        return chargeDTO;
    }


}
