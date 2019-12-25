package com.cisdi.steel.module.job.gl.writer;

import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.ChargeDTO;
import com.cisdi.steel.dto.response.gl.TagValueListDTO;
import com.cisdi.steel.dto.response.gl.res.BatchData;
import com.cisdi.steel.dto.response.gl.res.BatchDistribution;
import com.cisdi.steel.dto.response.gl.res.BatchMaterial;
import com.cisdi.steel.dto.response.gl.res.TagValue;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>Description: 8高炉布料制度变动记载 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/12/23 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class BuLiaoZhiDuBianDongJiZaiWriter extends BaseShangLiaoBuLiaoWriter {
    // 标记行
    private static int itemRowNum = 4;

    // 获取chargeNo tagname
    private static String tagName = "BF8_L2M_SH_ChargeVariation_evt";

    // 批次tagname
    private static String piCiTagName = "BF8_L2M_SH_L1ChargeVarNo_evt";

    // 基准尺 tagName
    private static String jiZhunChiTagName = "BF8_L2C_TP_MainSetLine_1m_cur";

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
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
        sheet.getRow(itemRowNum).setZeroHeight(true);//隐藏占位符行

        DateQuery date = this.getDateQuery(excelDTO);
        DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());
        TagValueListDTO tagValueListDTO = getTagValueListDTO(dateQuery, version, tagName);

        int count = 0;
        for (int i = 0; i < tagValueListDTO.getData().size(); i++) {
            TagValue tagValue = tagValueListDTO.getData().get(i);
            BigDecimal chargeNo = tagValue.getVal();
            Date clock = tagValue.getClock();

            // 通过api获取chargeDTO数据
            ChargeDTO chargeDTO = getChargeDTO(version, chargeNo.intValue());

            if (Objects.nonNull(chargeDTO)) {
                // 写入数据
                List<CellData> cellDataList = mapDataHandler(chargeDTO, itemNameList, sheet, count, clock, version);
                ExcelWriterUtil.setCellValue(sheet, cellDataList);
                count = count + 2;
            }
        }

        // TODO 设置动态边框样式
//        int beginRowNum = itemRowNum + 1;
//        int lastRowNum = sheet.getLastRowNum();
//        int beginColumnNum = 1;
//        int endColumnNum = itemNameList.size() + beginColumnNum - 2;
//        ExcelWriterUtil.setBorderStyle(workbook, sheet,  beginRowNum, lastRowNum, beginColumnNum, endColumnNum);

        // TODO 动态创建、合并单元格。

        return workbook;
    }

    private List<CellData> mapDataHandler(ChargeDTO chargeDTO, List<String> itemNameList, Sheet sheet, int count, Date clock, String version) {
        List<BatchData> data = chargeDTO.getData();
        List<CellData> cellDataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(data)) {
            List<BatchMaterial> cMaterials = data.get(0).getMaterials();
            List<BatchMaterial> o1Materials = data.get(1).getMaterials();
            List<BatchMaterial> o2Materials = data.get(2).getMaterials();

            List<BatchDistribution> cDistributions = data.get(0).getDistributions();
            List<BatchDistribution> o1Distributions = data.get(1).getDistributions();
            List<BatchDistribution> o2Distributions = data.get(2).getDistributions();

            int row = 1 + count + itemRowNum;
            // 遍历标记行所有的单元格
            for (int j = 0; j < itemNameList.size(); j++) {
                // 获取标记项单元格中的值
                String itemName = itemNameList.get(j);
                int col = j;
                if (StringUtils.isNotBlank(itemName)) {
                    switch (itemName) {
                        case "批次": {
                            //批次：BF8_L2M_SH_L1ChargeVarNo_evt+触发点前后一分钟（触发点返回值得clock前后一分钟）
                            DateQuery dateQuery = DateQueryUtil.buildBeforeAfter1Minute(clock);
                            TagValueListDTO tagValueListDTO = this.getTagValueListDTO(dateQuery, version, piCiTagName);
                            if (CollectionUtils.isNotEmpty(tagValueListDTO.getData())) {
                                BigDecimal val = tagValueListDTO.getData().get(0).getVal();
                                ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            }
                            break;
                        }
                        case "C":
                        case "O":
                        case "L":
                        case "S": {
                            ExcelWriterUtil.addCellData(cellDataList, row, col, itemName);
                            break;
                        }
                        case "N/A": {
                            ExcelWriterUtil.addCellData(cellDataList, row, col, "");
                            break;
                        }
                        case "c.position": {
                            String roundact = getRoundact(cDistributions);
                            String position = getPosition(cDistributions);
                            ExcelWriterUtil.addCellData(cellDataList, row, col, position);
                            ExcelWriterUtil.addCellData(cellDataList, row + 1, col, roundact);
                            break;
                        }
                        case "o1.position": {
                            String roundact = getRoundact(o1Distributions);
                            String position = getPosition(o1Distributions);
                            ExcelWriterUtil.addCellData(cellDataList, row, col, position);
                            ExcelWriterUtil.addCellData(cellDataList, row + 1, col, roundact);
                            break;
                        }
                        case "o2.position": {
                            String roundact = getRoundact(o2Distributions);
                            String position = getPosition(o2Distributions);
                            ExcelWriterUtil.addCellData(cellDataList, row, col, position);
                            ExcelWriterUtil.addCellData(cellDataList, row + 1, col, roundact);
                            break;
                        }
                        case "materials.brandcode" : {
                            //c中：materials.brandcode后缀是COKE + 第一个O中的“回用焦丁”
                            BigDecimal coke = cMaterials.stream()
                                    .filter(p -> StringUtils.endsWith(p.getBrandcode(),"COKE"))
                                    .collect(Collectors.toList()).get(0).getWeightset();
                            // 获取第一个o中的“回用焦丁”
                            BigDecimal huiYongJiaoDing = o1Materials.stream()
                                    .filter(p -> "回用焦丁".equals(p.getDescr()))
                                    .collect(Collectors.toList()).get(0).getWeightset();

                            double jiaoDing = coke.add(huiYongJiaoDing).doubleValue();

                            ExcelWriterUtil.addCellData(cellDataList, row, col, jiaoDing);
                            break;
                        }
                        case "矿石总量" : {
                            //本次o之和 - 回用焦丁
                            BigDecimal kuangShiZongLiang = o1Materials.stream()
                                    .filter(p -> !"回用焦丁".equals(p.getDescr()))
                                    .map(BatchMaterial::getWeightset)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            ExcelWriterUtil.addCellData(cellDataList, row, col, kuangShiZongLiang.doubleValue());
                            break;
                        }
                        case "小烧" : {
                            //第二个o中：materials.brandcode后缀是SINTER
                            BatchMaterial batchMaterial = o2Materials.stream()
                                    .filter(p -> StringUtils.endsWith(p.getBrandcode(),"SINTER"))
                                    .collect(Collectors.toList()).get(0);
                            ExcelWriterUtil.addCellData(cellDataList, row, col, batchMaterial.getWeightset());
                            break;
                        }
                        case "基准尺": {
                            //基准尺：BF8_L2C_TP_MainSetLine_1m_cur++触发点clock选取分钟，不含秒，starttime和endtime保持一致
                            DateQuery dateQuery = DateQueryUtil.buildDateWithoutSeconds(clock);
                            TagValueListDTO tagValueListDTO = this.getTagValueListDTO(dateQuery, version, jiZhunChiTagName);
                            if (CollectionUtils.isNotEmpty(tagValueListDTO.getData())) {
                                BigDecimal val = tagValueListDTO.getData().get(0).getVal();
                                ExcelWriterUtil.addCellData(cellDataList, row, col, val);
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
        return cellDataList;
    }

}
