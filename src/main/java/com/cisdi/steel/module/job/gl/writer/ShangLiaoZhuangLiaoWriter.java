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
    // 大分类
    // 焦炭
    private static final String COKE = "COKE";
    // 烧结矿
    private static final String SINTER = "SINTER";
    // 球团矿
    private static final String PELLETS = "PELLETS";
    // 块矿
    private static final String LUMPORE = "LUMPORE";
    // 熔剂
    private static final String FLUX = "FLUX";
    // 大烧
    private static final String OL = "Ol";
    // 小烧
    private static final String OS = "Os";
    // 标记项
    private static final String SEQUENCE = "sequence";
    private static final String BATCHINDEX_WEIGHTTIME = "batchIndex.weighttime";
    private static final String BATCHINDEX_MATRIXNO = "batchIndex.matrixno";
    private static final String BATCHINDEX_BATCHNO = "batchIndex.batchno";
    private static final String N_A = "N/A";
    private static final String ORE_TOTAL = "矿石总量";
    private static final String COKE_TOTAL = "焦炭总量";
    private static final String OL_ITEM = "大烧";
    private static final String OS_ITEM = "小烧";

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
        sheet.getRow(itemRowNum).setZeroHeight(true);//隐藏占位符行

        DateQuery date = this.getDateQueryBeforeOneDay(excelDTO);
        DateQuery dateQuery = DateQueryUtil.buildDayAheadTwoHour(date.getRecordDate());

        List<Integer> chargeNos = handleChargeNoData(dateQuery, version);
        List<List<BatchData>> batchDataListList = new ArrayList<>();
        for (Integer chargeNo : chargeNos) {
            ChargeDTO chargeDTO = getChargeDTO(version, chargeNo);
            if (Objects.isNull(chargeDTO) || CollectionUtils.isEmpty(chargeDTO.getData())) {
                continue;
            }
            batchDataListList.add(chargeDTO.getData());
        }

        // 处理动态的小分类
        handleChargeDTOList(sheet, batchDataListList);
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);

        // 写入数据
        int count = 0; // 序号记录
        for (int i = 0; i < batchDataListList.size(); i++) {
            List<BatchData> batchDataList = batchDataListList.get(i);
            // 过滤出22点-22点之间的数据
            batchDataList = batchDataList.stream().filter(batchData -> batchData.getBatchIndex().getWeighttime().getTime() >= dateQuery.getStartTime().getTime()
                    && batchData.getBatchIndex().getWeighttime().getTime() <= dateQuery.getEndTime().getTime()).collect(Collectors.toList());
            if (Objects.nonNull(batchDataList) && CollectionUtils.isNotEmpty(batchDataList)) {
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

        // 替换当前日期
        Date currentDate = DateUtil.addDays(new Date(), -1);
        for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet tempSheet = workbook.getSheetAt(i);
            if (!Objects.isNull(tempSheet) && !workbook.isSheetHidden(i)) {
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
        List<String> typeList = Arrays.asList(COKE, SINTER, PELLETS, LUMPORE, FLUX);
        List<BatchMaterial> allBatchMaterialList = batchDataListList.stream().flatMap(Collection::stream)
                .map(BatchData::getMaterials).flatMap(Collection::stream).collect(Collectors.toList());
        // 大分类对应多个小分类的map
        Map<String, Set<String>> typeNameToList = new HashMap<>();
        typeList.forEach(type -> typeNameToList.put(type, new HashSet<String>()));
        for (BatchMaterial batchMaterial : allBatchMaterialList) {
            typeNameToList.forEach((type, subTypeList) -> {
                if (batchMaterial.getBrandcode().endsWith(type)) {
                    subTypeList.add(batchMaterial.getDescr());
                }
            });
        }
        // 写入新的列
        addNewItem(sheet, typeNameToList, COKE, "焦丁", "回用焦");
        addNewItem(sheet, typeNameToList, PELLETS, "程潮球团", "鄂州球团");
        addNewItem(sheet, typeNameToList, LUMPORE, "阿块", "纽曼混合块矿");
        addNewItem(sheet, typeNameToList, FLUX, "石灰石", "硅石");
    }

    /**
     * 
     * @param sheet sheet
     * @param typeNameToList api数据集中大分类对应小分类的map
     * @param type 大分类
     * @param firstItem 该大分类下的第一项 hardcode写在excel中的第一项
     * @param endItem 该大分类的结束标记 hardcode写在excel中的最后一项
     */
    private void addNewItem(Sheet sheet, Map<String, Set<String>> typeNameToList, String type, String startItem, String endItem) {
        try {
            // 获取接口返回的数据中该分类下的所有小分类
            Set<String> typeList = typeNameToList.get(type);
            Cell startItemCell = PoiCustomUtil.getCellByValue(sheet, startItem);
            Cell endItemCell = PoiCustomUtil.getCellByValue(sheet, endItem);
            int startItemCellColumnIndex = startItemCell.getColumnIndex();
            int endItemCellColumnIndex = endItemCell.getColumnIndex();
            // 大分类下的单元格个数(表示有多少个单元格合并为此大分类)
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
                // 填充完后，如果单元格为空，则隐藏
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
    private List<CellData> mapDataHandler(List<BatchData> data, List<String> itemNameList, Sheet sheet, Integer count) {
        List<CellData> cellDataList = new ArrayList<>();
        if (CollectionUtils.isEmpty(data)) {
            return cellDataList;
        }
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
                        case SEQUENCE: {
                            ExcelWriterUtil.addCellData(cellDataList, row, col, sequence);
                            break;
                        }
                        case BATCHINDEX_WEIGHTTIME: {
                            Long weightTimeInMs = batchIndexDataMap.getWeighttime().getTime();
                            Date weightTime = new Date();
                            weightTime.setTime(weightTimeInMs);
                            String formatWeightTime = DateUtil.getFormatDateTime(weightTime, DateUtil.hhmmFormat);
                            ExcelWriterUtil.addCellData(cellDataList, row, col, formatWeightTime);
                            break;
                        }
                        case BATCHINDEX_MATRIXNO: {
                            ExcelWriterUtil.addCellData(cellDataList, row, col, batchIndexDataMap.getMatrixno());
                            break;
                        }
                        case BATCHINDEX_BATCHNO: {
                            ExcelWriterUtil.addCellData(cellDataList, row, col, batchIndexDataMap.getBatchno());
                            break;
                        }
                        case N_A: {
                            ExcelWriterUtil.addCellData(cellDataList, row, col, "");
                            break;
                        }
                        case ORE_TOTAL : {
                            BigDecimal oreTotal = materialsArray.stream().filter(m -> m.getBrandcode().endsWith(LUMPORE) ||
                                    m.getBrandcode().endsWith(PELLETS) || m.getBrandcode().endsWith(SINTER))
                                    .map(BatchMaterial::getWeightset).reduce(BigDecimal.ZERO, BigDecimal::add);
                            ExcelWriterUtil.addCellData(cellDataList, row, col, oreTotal);
                            break;
                        }
                        case COKE_TOTAL : {
                            BigDecimal cokeTotal = materialsArray.stream().filter(m -> m.getBrandcode().endsWith(COKE))
                                    .map(BatchMaterial::getWeightset).reduce(BigDecimal.ZERO, BigDecimal::add);
                            ExcelWriterUtil.addCellData(cellDataList, row, col, cokeTotal);
                            break;
                        }
                        case OL_ITEM : {
                            // OL中：materials.brandcode后缀是SINTER
                            if (OL.equalsIgnoreCase(batchIndexDataMap.getTyp())) {
                                BigDecimal val = materialsArray.stream()
                                        .filter(m -> m.getBrandcode().endsWith(SINTER))
                                        .map(BatchMaterial::getWeightset).reduce(BigDecimal.ZERO, BigDecimal::add);
                                ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                            }
                            break;
                        }
                        case OS_ITEM : {
                            // OS中：materials.brandcode后缀是SINTER
                            if (OS.equalsIgnoreCase(batchIndexDataMap.getTyp())) {
                                BigDecimal val = materialsArray.stream()
                                        .filter(m -> m.getBrandcode().endsWith(SINTER))
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
