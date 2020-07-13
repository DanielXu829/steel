package com.cisdi.steel.module.job.gl.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.ChargeDTO;
import com.cisdi.steel.dto.response.gl.res.BatchData;
import com.cisdi.steel.dto.response.gl.res.BatchIndex;
import com.cisdi.steel.dto.response.gl.res.BatchMaterial;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.FastJSONUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description: 8高炉班产燃料比 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020/07/09 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class BanChanRanLiaoBiWriter extends BaseGaoLuWriter {
    // 标记行
    private static int itemRowNum = 3;
    // 数据开始行

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = "8.0";
        Date current = new Date();
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
            log.error("在模板中获取version失败", e);
        }

        try {
            DateQuery dateQuery = getDateQuery(excelDTO);
            List<Date> allDayBeginTimeInCurrentMonth = DateUtil.getAllDayBeginTimeInCurrentMonthBeforeDays(dateQuery.getRecordDate(), 1);
            if(allDayBeginTimeInCurrentMonth.size() > 0) {
                current = allDayBeginTimeInCurrentMonth.get(0);
            }
            handleTagValueData(workbook, version, allDayBeginTimeInCurrentMonth);
            handleWeight(workbook, version, allDayBeginTimeInCurrentMonth);
        } catch (Exception e) {
            log.error("处理班产燃料比月报时产生错误", e);
            throw e;
        } finally {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (!Objects.isNull(sheet) && !workbook.isSheetHidden(i)) {
                    // 全局替换 当前日期
                    ExcelWriterUtil.replaceCurrentMonthInTitle(sheet, 0, 0, current);
                    PoiCustomUtil.clearPlaceHolder(sheet);
                }
            }
        }
        return workbook;
    }

    private void handleTagValueData(Workbook workbook, String version, List<Date> allDayBeginTimeInCurrentMonth) {
        Sheet sheet = workbook.getSheet("_tagNameDatas");
        List<String> tagNames = PoiCustomUtil.getFirstRowCelVal(sheet);
        List<CellData> cellDataList = new ArrayList<>();

        for(int i = 0; i < allDayBeginTimeInCurrentMonth.size(); i ++) {
            List<DateQuery> class8DateQueries = DateQueryUtil.buildDay12HourEach(allDayBeginTimeInCurrentMonth.get(i));
            List<DateQuery> class10DateQueries = DateQueryUtil.buildDay12HourAheadTwoHour(allDayBeginTimeInCurrentMonth.get(i));
            for(int k = 0; k < tagNames.size(); k++) {
                String tagName = tagNames.get(k);
                if(StringUtils.isBlank(tagName)) continue;
                for(int j =1; j <= 2; j++) {
                    DateQuery dateQuery = null;
                    //i*2+j
                    if(tagName.contains("_L2M_BX_")) {
                        dateQuery = class10DateQueries.get(j-1);
                    } else {
                        dateQuery = class8DateQueries.get(j-1);
                    }
                    BigDecimal value = getLatestTagValue(version, tagName, dateQuery.getEndTime());
                    if(Objects.nonNull(value)) {
                        if(tagName.contains("_ShiftSchedule_evt")) {
                            int shift = value.intValue();
                            switch (shift) {
                                case 1:
                                    ExcelWriterUtil.addCellData(cellDataList, i*2+j, k, "甲");
                                    break;
                                case 2:
                                    ExcelWriterUtil.addCellData(cellDataList, i*2+j, k, "乙");
                                    break;
                                case 3:
                                    ExcelWriterUtil.addCellData(cellDataList, i*2+j, k, "丙");
                                    break;
                                case 4:
                                    ExcelWriterUtil.addCellData(cellDataList, i*2+j, k, "丁");
                                    break;
                            }
                        } else {
                            ExcelWriterUtil.addCellData(cellDataList, i*2+j, k, value);
                        }
                    }
                }

            }

        }
        ExcelWriterUtil.setCellValue(sheet, cellDataList);
    }

    private void handleWeight(Workbook workbook, String version, List<Date> allDayBeginTimeInCurrentMonth) {
        Sheet sheet = workbook.getSheetAt(0);
        List<CellData> cellDataList = new ArrayList<>();
        for(int i = 0; i < allDayBeginTimeInCurrentMonth.size(); i ++) {
            List<DateQuery> classDateQueries = DateQueryUtil.buildDay12HourEach(allDayBeginTimeInCurrentMonth.get(i));
            for(int j =1; j <= 2; j++)  {
                List<Integer> chargeNos = handleChargeNoData(classDateQueries.get(j-1), version);
                List<BatchData> list = new ArrayList<>();
                List<List<BatchData>> batchDataListList = new ArrayList<>();
                List<ChargeDTO> chargeDTOList = new ArrayList<>();
                for (Integer chargeNo : chargeNos) {
                    ChargeDTO chargeDTO = getChargeDTO(version, chargeNo);
                    if (Objects.isNull(chargeDTO) || CollectionUtils.isEmpty(chargeDTO.getData())) {
                        continue;
                    }
                    chargeDTOList.add(chargeDTO);
                    batchDataListList.add(chargeDTO.getData());
                    list.addAll(chargeDTO.getData());
                }
                // 处理动态的小分类
                handleChargeDTOList(sheet, batchDataListList);
                List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
                sheet.getRow(itemRowNum).setZeroHeight(true);//隐藏占位符行
                List<String> cellValue = PoiCustomUtil.getRowCelVal(sheet, 3);
                for (int columnNum = 0; columnNum < cellValue.size(); columnNum++) {
                    if (StringUtils.isBlank(cellValue.get(columnNum))) {
                        continue;
                    }
                    BigDecimal value = null;
                    switch (cellValue.get(columnNum)) {
                        case "大烧":
                            value = getWeightsetByType(list, "SINTER","Ol");
                            break;
                        case "小烧":
                            value = getWeightsetByType(list, "SINTER","Os");
                            break;
                        default:
                            value = getWeightsetByDescr(list, cellValue.get(columnNum));
                            break;
                    }
                    ExcelWriterUtil.addCellData(cellDataList, i*2+j+3, columnNum, value);
                }
            }
        }
        ExcelWriterUtil.setCellValue(sheet, cellDataList);
        //隐藏空白的列
        int rowIndex = 1;
        List<String> cellValue = PoiCustomUtil.getRowCelVal(sheet, rowIndex);
        for (int columnNum = 0; columnNum < cellValue.size(); columnNum++) {
            if (StringUtils.isBlank(cellValue.get(columnNum))) {
                sheet.setColumnHidden(columnNum, true);
            } else {
                sheet.setColumnHidden(columnNum, false);
            }
            if (columnNum==16) {
                sheet.setColumnHidden(columnNum, false);
            }
        }
    }

    private void handleChargeDTOList(Sheet sheet, List<List<BatchData>> batchDataListList) {
        if (CollectionUtils.isEmpty(batchDataListList)) {
            return;
        }
        // 小分类的brandCode的后缀，根据后缀判断属于哪个大分类
        List<String> suffixList = Arrays.asList("PELLETS", "LUMPORE", "FLUX");
        // 大分类
        List<String> typeList = Arrays.asList("球团矿", "块矿", "熔剂");
        Map<String, String> suffixToTypeMap = suffixList.stream().collect(Collectors
                .toMap(key -> key, key -> typeList.get(suffixList.indexOf(key))));
        List<BatchMaterial> allBatchMaterialList = batchDataListList.stream().flatMap(Collection::stream)
                .map(BatchData::getMaterials).flatMap(Collection::stream).collect(Collectors.toList());
        Map<String, Set<String>> typeNameToList = new HashMap<>();
        suffixToTypeMap.forEach((suffix, type) -> {
            typeNameToList.put(type, new HashSet<String>());
        });
        for (BatchMaterial batchMaterial : allBatchMaterialList) {
            suffixToTypeMap.forEach((suffix, type) -> {
                if (batchMaterial.getBrandcode().endsWith(suffix)) {
                    typeNameToList.get(type).add(batchMaterial.getDescr());
                }
            });
        }
        // 写入新的列
        //5~10
        addNewItem(sheet, typeNameToList,"球团矿", 5, 21);
        //11~16
        addNewItem(sheet, typeNameToList, "块矿", 5, 21);
        //17~21
        addNewItem(sheet, typeNameToList, "熔剂", 5, 21);
    }

    /**
     *
     * @param sheet sheet
     * @param typeNameToList api数据集中大分类对应小分类的map
     * @param type 大分类
     * @param firstItem 该大分类下的第一项
     * @param endItem 该大分类的结束标记
     */
    private void addNewItem(Sheet sheet, Map<String, Set<String>> typeNameToList, String type, int startItemCellColumnIndex, int endItemCellColumnIndex) {
        try {
            // 获取接口返回的数据中该分类下的所有小分类
            Set<String> typeList = typeNameToList.get(type);
            int rowIndex = 1;
            // 大分类下的单元格个数(有多少个单元格合并为此大分类)
            int excelColumnSize = endItemCellColumnIndex - startItemCellColumnIndex + 1;
            // 已存在的小分类
            List<String> existType = new ArrayList<>();
            for (int columnNum = startItemCellColumnIndex; columnNum <= endItemCellColumnIndex; columnNum++) {
                String cellValue = PoiCellUtil.getCellValue(sheet, rowIndex, columnNum);
                if (StringUtils.isNotBlank(cellValue)) {
                    existType.add(cellValue);
                }
            }
            // 过滤掉excel中已存在的小分类
            if (CollectionUtils.isNotEmpty(typeList)) {
                List<String> newCokeList = typeList.stream().filter(item -> !existType.contains(item)).collect(Collectors.toList());
                // 如果还存在空位，写入新的小分类
                if (existType.size() < excelColumnSize) {
                    int i = 0;
                    for (int columnNum = startItemCellColumnIndex; columnNum <= endItemCellColumnIndex; columnNum++) {
                        String cellValue = PoiCellUtil.getCellValue(sheet, rowIndex, columnNum);
                        // 此单元格值为空，可以写入新的小分类, 同时写入标记行
                        if (StringUtils.isBlank(cellValue)) {
                            Cell cell = sheet.getRow(rowIndex).getCell(columnNum);
                            Cell itemCell = sheet.getRow(itemRowNum).getCell(columnNum);
                            Cell cellWater = sheet.getRow(rowIndex).getCell(columnNum+11);
                            Cell itemCellWater = sheet.getRow(itemRowNum).getCell(columnNum+11);
                            if (i < newCokeList.size()) {
                                PoiCustomUtil.setCellValue(cell, newCokeList.get(i));
                                PoiCustomUtil.setCellValue(itemCell, newCokeList.get(i));
                                PoiCustomUtil.setCellValue(cellWater, newCokeList.get(i));
                                PoiCustomUtil.setCellValue(itemCellWater, newCokeList.get(i));
                                i++;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("动态新增列出错", e);
        }
    }

    private BigDecimal getWeightsetByDescr(List<BatchData> data, String descr) {
        BigDecimal oCount = new BigDecimal(0);
        for (int i = 0; i < data.size(); i++) {
            BigDecimal reduce = data.get(i).getMaterials().stream()
                    .filter(m -> m.getDescr().equals(descr))
                    .map(BatchMaterial::getWeightset).reduce(BigDecimal.ZERO, BigDecimal::add);
            oCount = oCount.add(reduce);
        }
        return oCount;
    }

    private BigDecimal getWeightsetByType(List<BatchData> data, String name, String type) {
        BigDecimal oCount = new BigDecimal(0);
        List<BatchData> oCollect = data.stream()
                .filter(p -> (type.equalsIgnoreCase(p.getBatchIndex().getTyp())))
                .collect(Collectors.toList());
        for (int i = 0; i < oCollect.size(); i++) {
            BigDecimal reduce = oCollect.get(i).getMaterials().stream()
                    .filter(m -> m.getBrandcode().endsWith(name))
                    .map(BatchMaterial::getWeightset).reduce(BigDecimal.ZERO, BigDecimal::add);
            oCount = oCount.add(reduce);
        }
        return oCount;
    }

    /**
     * 解析JSON数组，封装写入数据
     * @param data
     * @param prefix
     * @param itemNameList
     * @param itemRowNum
     * @param itemColNum
     * @return
     */
    private List<CellData> mapDataHandler(List<BatchData> data, List<String> itemNameList, Sheet sheet, Integer count) {
        List<CellData> cellDataList = new ArrayList<>();
        if (CollectionUtils.isEmpty(data)) {
            return cellDataList;
        }
//        Ol大烧
//        Os小烧
//        冶金焦是  大焦  焦丁是小焦   回用焦丁是回用焦丁
        // 计算本次o之和。
        BigDecimal oCount = new BigDecimal(0);
        List<BatchData> oCollect = data.stream()
                .filter(p -> ("Ol".equalsIgnoreCase(p.getBatchIndex().getTyp()) || "Os".equalsIgnoreCase(p.getBatchIndex().getTyp())))
                .collect(Collectors.toList());
        for (int i = 0; i < oCollect.size(); i++) {
            BigDecimal reduce = oCollect.get(i).getMaterials().stream()
                    .map(BatchMaterial::getWeightset).reduce(BigDecimal.ZERO, BigDecimal::add);
            oCount = oCount.add(reduce);
        }
        // 第一个C中的回用焦丁
        BigDecimal c1HuiYongJiaoDing = data.get(0).getMaterials().stream()
                .filter(m -> m.getDescr().endsWith("回用焦丁"))
                .map(BatchMaterial::getWeightset).reduce(BigDecimal.ZERO, BigDecimal::add);

        for (int i = 0; i < data.size(); i++) {
            BatchData batchData = data.get(i);
            if (Objects.isNull(batchData) || Objects.isNull(batchData.getBatchIndex()) || Objects.isNull(batchData.getMaterials())) {
                continue;
            }
            BatchIndex batchIndexDataMap = batchData.getBatchIndex();
            List<BatchMaterial> materialsArray = batchData.getMaterials();
            int sequence = count + i + 1;
            int row = sequence + itemRowNum;
            // 遍历标记行所有的单元格
            for (int j = 0; j < itemNameList.size(); j++) {
                // 获取标记项单元格中的值
                String itemName = itemNameList.get(j);
                int col = j;
                if (StringUtils.isNotBlank(itemName)) {
                    switch (itemName) {
                        case "矿石总量" : {
                            //本次o之和 - 第一个C中的回用焦丁
                            if ("C".equals(batchIndexDataMap.getTyp())) {
                                BigDecimal val = oCount.add(c1HuiYongJiaoDing);
                                ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            } else {
                                BigDecimal val = oCount.subtract(c1HuiYongJiaoDing);
                                ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            }
                            break;
                        }
                        case "大烧" : {
                            //第一个o中：materials.brandcode后缀是SINTER
                            if ("O1".equals(batchIndexDataMap.getTyp())) {
                                BigDecimal val = materialsArray.stream()
                                        .filter(m -> m.getBrandcode().endsWith("SINTER"))
                                        .map(BatchMaterial::getWeightset).reduce(BigDecimal.ZERO, BigDecimal::add);
                                ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            }
                            break;
                        }
                        case "小烧" : {
                            //第二个o中：materials.brandcode后缀是SINTER
                            if ("Os".equals(batchIndexDataMap.getTyp())) {
                                BigDecimal val = materialsArray.stream()
                                        .filter(m -> m.getBrandcode().endsWith("SINTER"))
                                        .map(BatchMaterial::getWeightset).reduce(BigDecimal.ZERO, BigDecimal::add);
                                ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            }
                            break;
                        }
                        default: {
                            //默认，materials中descr对应表头
                            BigDecimal val = materialsArray.stream()
                                    .filter(m -> itemName.equals(m.getDescr()))
                                    .map(BatchMaterial::getWeightset).reduce(BigDecimal.ZERO, BigDecimal::add);
                            if (val != null && val.doubleValue() > 0) {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return cellDataList;
    }

    /**
     * 根据chargeNo获取charge raw data
     * @param version
     * @param chargeNo
     * @return api数据
     */
    private String getChargeRawData(String version, Integer chargeNo) {
        return httpUtil.get(getPrimaryUrl(version, chargeNo));
    }

    /**
     * 处理chargeNo 列表
     * @param data
     * @return
     */
    private List<Integer> handleChargeNoData(DateQuery query, String version) {
        Map<String, String> queryParam = new HashMap();
        queryParam.put("starttime",  Objects.requireNonNull(query.getStartTime().getTime()).toString());
        queryParam.put("endtime",  Objects.requireNonNull(query.getEndTime().getTime()).toString());
        queryParam.put("tagname", "BF8_L2M_AnaChargeEnd_evt");
        String chargeNoData = httpUtil.get(getChargeNoUrl(version), queryParam);

        List<Integer> chargeNos = new ArrayList<Integer>();
        JSONArray dataArray = FastJSONUtil.convertJsonStringToJsonArray(chargeNoData);
        if (Objects.nonNull(dataArray) && dataArray.size() != 0) {
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject dataObj = dataArray.getJSONObject(i);
                if (Objects.nonNull(dataObj) && StringUtils.isNotBlank(dataObj.getString("val"))) {
                    chargeNos.add(dataObj.getInteger("val"));
                }
            }
        }
        // 排序
        chargeNos.sort(Integer::compareTo);
        return chargeNos;
    }
}
