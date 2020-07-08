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
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description: 上料装料 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/12/23 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class ShangLiaoZhuangLiaoWriter extends BaseGaoLuWriter {
    // 标记行
    private static int itemRowNum = 3;
    // 数据开始行

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = "8.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
            log.error("在模板中获取version失败", e);
        }

        DateQuery date = this.getDateQueryBeforeOneDay(excelDTO);
        DateQuery dateQuery = DateQueryUtil.buildDayAheadTwoHour(date.getRecordDate());

        List<Integer> chargeNos = handleChargeNoData(dateQuery, version);
        List<List<BatchData>> batchDataListList = new ArrayList<>();
        List<ChargeDTO> chargeDTOList = new ArrayList<>();
        for (Integer chargeNo : chargeNos) {
            ChargeDTO chargeDTO = getChargeDTO(version, chargeNo);
            if (Objects.isNull(chargeDTO) || CollectionUtils.isEmpty(chargeDTO.getData())) {
                continue;
            }
            chargeDTOList.add(chargeDTO);
            batchDataListList.add(chargeDTO.getData());
        }
        Sheet sheet = workbook.getSheetAt(0);
        // 处理动态的小分类
        handleChargeDTOList(sheet, batchDataListList);
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
        sheet.getRow(itemRowNum).setZeroHeight(true);//隐藏占位符行
        int count = 0;
        for (int i = 0; i < batchDataListList.size(); i++) {
            List<BatchData> batchDataList = batchDataListList.get(i);
            // 通过api获取数据
            // String chargeRawDataStr = getChargeRawData(version, chargeNo);
            // ChargeDTO chargeDTO = getChargeDTO(version, chargeNo);
            //JSONArray shangLiaoDataArray = FastJSONUtil.convertJsonStringToJsonArray(chargeRawDataStr);
            if (Objects.nonNull(batchDataList) && CollectionUtils.isNotEmpty(batchDataList)) {
                // 写入数据
                //List<CellData> cellDataList = mapDataHandler(shangLiaoDataArray, itemNameList, sheet, count);
                List<CellData> cellDataList = mapDataHandler(batchDataList, itemNameList, sheet, count);
                ExcelWriterUtil.setCellValue(sheet, cellDataList);
                count = count + batchDataList.size();
            }
        }

        //设置动态边框样式
        int beginRowNum = itemRowNum + 1;
        int lastRowNum = itemRowNum + count;
        int beginColumnNum = 1;
        int endColumnNum = itemNameList.size() + beginColumnNum - 2;
        int lastRowNumOld = sheet.getLastRowNum();
        // 如果数据超出原始模板的行数，则需要设置样式
        if (lastRowNum >= lastRowNumOld) {
            ExcelWriterUtil.setBorderStyle(workbook, sheet, beginRowNum, lastRowNum, beginColumnNum, endColumnNum, BorderStyle.MEDIUM);
        }

        Date currentDate = DateUtil.addDays(new Date(), -1);
        for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet tempSheet = workbook.getSheetAt(i);
            // 清除标记项(例如:{块矿.矿种})
            if (!Objects.isNull(tempSheet) && !workbook.isSheetHidden(i)) {
                // 全局替换 当前日期
                ExcelWriterUtil.replaceCurrentDateInTitle(tempSheet, 0, 1, currentDate);
                PoiCustomUtil.clearPlaceHolder(tempSheet);
            }
        }

        return workbook;
    }

    private void handleChargeDTOList(Sheet sheet, List<List<BatchData>> batchDataListList) {
        if (CollectionUtils.isEmpty(batchDataListList)) {
            return;
        }
        // 小分类的brandCode的后缀，根据后缀判断属于哪个大分类
        List<String> suffixList = Arrays.asList("COKE", "SINTER", "PELLETS", "LUMPORE", "FLUX");
        // 大分类
        List<String> typeList = Arrays.asList("焦炭", "烧结矿", "球团矿", "块矿", "熔剂");
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
        addNewItem(sheet, typeNameToList, "焦炭", "焦丁", "回用焦");
        addNewItem(sheet, typeNameToList,"球团矿", "程潮球团", "鄂州球团");
        addNewItem(sheet, typeNameToList, "块矿", "阿块", "纽曼混合块矿");
        addNewItem(sheet, typeNameToList, "熔剂", "石灰石", "硅石");
    }

    /**
     * 
     * @param sheet sheet
     * @param typeNameToList api数据集中大分类对应小分类的map
     * @param type 大分类
     * @param firstItem 该大分类下的第一项
     * @param endItem 该大分类的结束标记
     */
    private void addNewItem(Sheet sheet, Map<String, Set<String>> typeNameToList, String type, String startItem, String endItem) {
        try {
            // 获取接口返回的数据中该分类下的所有小分类
            Set<String> typeList = typeNameToList.get(type);
            Cell startItemCell = PoiCustomUtil.getCellByValue(sheet, startItem);
            Cell endItemCell = PoiCustomUtil.getCellByValue(sheet, endItem);
            int startItemCellColumnIndex = startItemCell.getColumnIndex();
            int endItemCellColumnIndex = endItemCell.getColumnIndex();
            // 大分类下的单元格个数(有多少个单元格合并为此大分类)
            int excelColumnSize = endItemCellColumnIndex - startItemCellColumnIndex + 1;
            // 已存在的小分类
            List<String> existType = new ArrayList<>();
            int rowIndex = startItemCell.getRowIndex();
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
                        if (StringUtils.isBlank(cellValue) || cellValue.endsWith("start}") || cellValue.endsWith("end}")) {
                            Cell cell = sheet.getRow(rowIndex).getCell(columnNum);
                            Cell itemCell = sheet.getRow(itemRowNum).getCell(columnNum);
                            if (i < newCokeList.size()) {
                                PoiCustomUtil.setCellValue(cell, newCokeList.get(i));
                                PoiCustomUtil.setCellValue(itemCell, newCokeList.get(i));
                                i++;
                            }
                        }
                    }
                }
            }
            for (int columnNum = startItemCellColumnIndex; columnNum < endItemCellColumnIndex; columnNum++) {
                String cellValue = PoiCellUtil.getCellValue(sheet, rowIndex, columnNum);
                // 此单元格值为空，可以写入新的小分类, 同时写入标记行
                if (StringUtils.isBlank(cellValue)) {
                    sheet.setColumnHidden(columnNum, true);
                }
            }
        } catch (Exception e) {
            log.error("动态新增列出错", e);
        }
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
    @Deprecated
    private List<CellData> mapDataHandler(JSONArray dataArray, List<String> itemNameList, Sheet sheet, Integer count) {
        List<CellData> cellDataList = new ArrayList<>();
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject dataObj = dataArray.getJSONObject(i);
            if (Objects.nonNull(dataObj)) {
                JSONObject batchIndexObj = dataObj.getJSONObject("batchIndex");
                JSONArray materialsArray = dataObj.getJSONArray("materials");
                Map<String, Object> batchIndexDataMap = batchIndexObj.getInnerMap();
                int sequence = count + i + 1;
                int row = sequence + itemRowNum;
                // 遍历标记行所有的单元格
                for (int j = 0; j < itemNameList.size(); j++) {
                    // 获取标记项单元格中的值
                    String itemName = itemNameList.get(j);
                    int col = j;
                    if (StringUtils.isNotBlank(itemName)) {
                        switch (itemName) {
                            case "sequence": {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, sequence);
                                break;
                            }
                            case "batchIndex.weighttime": {
                                Long weightTimeInMs = (Long) batchIndexDataMap.get("weighttime");
                                Date weightTime = new Date();
                                weightTime.setTime(weightTimeInMs);
                                String formatWeightTime = DateUtil.getFormatDateTime(weightTime, DateUtil.hhmmFormat);
                                ExcelWriterUtil.addCellData(cellDataList, row, col, formatWeightTime);
                                break;
                            }
                            case "batchIndex.matrixno": {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, batchIndexDataMap.get("matrixno"));
                                break;
                            }
                            case "batchIndex.batchno": {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, batchIndexDataMap.get("batchno"));
                                break;
                            }
                            case "N/A": {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, "");
                                break;
                            }
                            case "materials.brandcode" : {
                                //c中：materials.brandcode后缀是COKE
                                if (i == 0) {
                                    String val = getMaterialValue(materialsArray, "brandcode", "COKE", "weightset");
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                                }
                                break;
                            }
                            case "矿石总量" : {
                                //本次o之和 - 回用焦丁
                                if (i > 0) {
                                    double kuangShiZongLiang = 0;
                                    for (int k = 0; k < materialsArray.size(); k++) {
                                        JSONObject material = materialsArray.getJSONObject(k);
                                        if ("回用焦丁".equals(material.getString("descr"))) {
                                            kuangShiZongLiang = kuangShiZongLiang - material.getDoubleValue("weightset");
                                        } else {
                                            kuangShiZongLiang = kuangShiZongLiang + material.getDoubleValue("weightset");
                                        }
                                    }
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, kuangShiZongLiang);
                                }
                                break;
                            }
                            case "大烧" : {
                                //第一个o中：materials.brandcode后缀是SINTER
                                if (i == 1) {
                                    String val = getMaterialValue(materialsArray, "brandcode", "SINTER", "weightset");
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                                }
                                break;
                            }
                            case "小烧" : {
                                //第二个o中：materials.brandcode后缀是SINTER
                                if (i == 2) {
                                    String val = getMaterialValue(materialsArray, "brandcode", "SINTER", "weightset");
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                                }
                                break;
                            }
                            default: {
                                //默认，materials中descr对应表头
                                if (CollectionUtils.isNotEmpty(materialsArray)) {
                                    for (int k = 0; k < materialsArray.size(); k++) {
                                        JSONObject material = materialsArray.getJSONObject(k);
                                        if (itemName.equals(material.getString("descr"))){
                                            ExcelWriterUtil.addCellData(cellDataList, row, col, material.getString("weightset") );
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        return cellDataList;
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
                        case "sequence": {
                            ExcelWriterUtil.addCellData(cellDataList, row, col, sequence);
                            break;
                        }
                        case "batchIndex.weighttime": {
                            Long weightTimeInMs = batchIndexDataMap.getWeighttime().getTime();
                            Date weightTime = new Date();
                            weightTime.setTime(weightTimeInMs);
                            String formatWeightTime = DateUtil.getFormatDateTime(weightTime, DateUtil.hhmmFormat);
                            ExcelWriterUtil.addCellData(cellDataList, row, col, formatWeightTime);
                            break;
                        }
                        case "batchIndex.matrixno": {
                            ExcelWriterUtil.addCellData(cellDataList, row, col, batchIndexDataMap.getMatrixno());
                            break;
                        }
                        case "batchIndex.batchno": {
                            ExcelWriterUtil.addCellData(cellDataList, row, col, batchIndexDataMap.getBatchno());
                            break;
                        }
                        case "N/A": {
                            ExcelWriterUtil.addCellData(cellDataList, row, col, "");
                            break;
                        }
                        case "materials.brandcode" : {
                            //c中：materials.brandcode后缀是COKE
                            if ("C".equals(batchIndexDataMap.getTyp())) {
                                BigDecimal val = materialsArray.stream()
                                        .filter(m -> m.getBrandcode().endsWith("COKE"))
                                        .map(BatchMaterial::getWeightset).reduce(BigDecimal.ZERO, BigDecimal::add);
                                ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            }
                            break;
                        }
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
