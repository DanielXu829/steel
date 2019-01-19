package com.cisdi.steel.module.job.a1.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.common.util.ValidUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.strategy.date.DateStrategy;
import com.cisdi.steel.module.job.strategy.options.OptionsStrategy;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/14 </P>
 *
 * @author leaf
 * @version 1.0
 */
@SuppressWarnings("Duplicates")
@Component
public class GaolubuliaoWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version);

        Sheet dictionary = workbook.getSheet("_dictionary");
        // 炉料结构
        handlerPart1(workbook, dictionary, excelDTO, url);

        handlerPart2(workbook, dictionary, excelDTO, url);

        return workbook;
    }

    private void handlerPart2(Workbook workbook, Sheet dictionary, WriterExcelDTO excelDTO, String url) {
        int rowNum = 2;
        // 炉料结构
        String sheetName = PoiCellUtil.getCellValue(dictionary, rowNum, 0);
        // 策略
        String sheetOption = PoiCellUtil.getCellValue(dictionary, rowNum, 1);
        // 点名
        String tagName = PoiCellUtil.getCellValue(dictionary, rowNum, 2);
        // 最后一个值
        String lastVal = PoiCellUtil.getCellValue(dictionary, rowNum, 3);
        // 写入数据的行数
        String rowIndexStr = PoiCellUtil.getCellValue(dictionary, rowNum, 4);
        Integer rowIndex = 3;
        if (StringUtils.isNotBlank(rowIndexStr)) {
            rowIndex = Double.valueOf(rowIndexStr).intValue();
        }
        Sheet sheet = workbook.getSheet(sheetName);
        // 最后一个值
        String lastValKey = null;
        if (StringUtils.isNotBlank(lastVal)) {
            lastValKey = lastVal;
        }
        // 指定行的值
        List<String> rowVals = PoiCustomUtil.getRowCelVal(sheet, 2);
        List<CellData> rowCellDataList = new ArrayList<>();
        // 处理后的时间
        List<DateQuery> dateQueries = handlerDate(sheetOption, excelDTO.getDateQuery().getRecordDate());
        for (DateQuery dateQuery : dateQueries) {
            List<String> indexData = eachData(url, dateQuery, tagName);
            if (Objects.nonNull(indexData)) {
                List<Integer> indexs = indexData.stream().map(v -> Integer.parseInt(v.split("/")[0])).collect(Collectors.toList());
                List<String> times = indexData.stream().map(v -> v.split("/")[1]).collect(Collectors.toList());
                int min = 0;
                if (Objects.nonNull(lastValKey) && indexData.contains(lastValKey)) {
                    min = indexData.indexOf(lastValKey) + 1;
                }
                int max = indexData.size();

                for (int i = min; i < max; i++) {
                    Integer chargeNo = indexs.get(i);
                    Integer newRowIndex = rowIndex + i * 4;
                    List<CellData> cellDataList = changeLiaozhiData(url, chargeNo.toString(), times.get(i), newRowIndex, rowVals);
                    rowCellDataList.addAll(cellDataList);
                }
                if (max > 0) {
                    updateDictChargeNo(dictionary, indexData.get(max - 1), rowNum, 3);
                    updateDictChargeNo(dictionary, max * 4 + rowIndex, rowNum, 4);
                }

            }
        }
        SheetRowCellData build = SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(rowCellDataList)
                .build();
        build.allValueWriteExcel();
    }

    private List<CellData> changeLiaozhiData(String url, String chargeNo, String time, int rowIndex, List<String> rowVals) {
        url += "/charge/variation?chargeNo=" + chargeNo;
        String s = httpUtil.get(url);
        List<CellData> resultData = new ArrayList<>();
        if (StringUtils.isBlank(s)) {
            return resultData;
        }
        JSONObject object = JSONObject.parseObject(s);
        JSONObject data = object.getJSONObject("data");
        if (Objects.isNull(data)) {
            return resultData;
        }
//        JSONObject chargeIndexInDay = data.getJSONObject("chargeIndexInDay");
        JSONObject materialValues = data.getJSONObject("materialValues");
        if (Objects.isNull(materialValues)) {
            return resultData;
        }
        materialValues.put("chargeNo", chargeNo);
        materialValues.put("chargeIndexInDay", data.get("chargeIndexInDay"));
        materialValues.put("time", new Date(Long.valueOf(time)));
//        materialValues.put("chargeIndexInDay", chargeIndexInDay.get("value"));
//        materialValues.put("time", new Date(chargeIndexInDay.getLong("datetime")));
        Object oreStockline = materialValues.get("oreStockline");
        if (Objects.nonNull(oreStockline)) {
            BigDecimal multiply = new BigDecimal(oreStockline.toString()).multiply(new BigDecimal(1000));
            materialValues.put("oreStockline", multiply);
        }
        Object cokeStockline = materialValues.get("cokeStockline");
        if (Objects.nonNull(cokeStockline)) {
            BigDecimal multiply = new BigDecimal(cokeStockline.toString()).multiply(new BigDecimal(1000));
            materialValues.put("cokeStockline", multiply);
        }
        int size = rowVals.size();
        JSONObject distribution = data.getJSONObject("distribution");

        for (int i = 0; i < size; i++) {
            String s1 = rowVals.get(i);
            if (StringUtils.isBlank(s1)) {
                continue;
            }
            boolean intege = ValidUtils.isDecmal(s1);
            if (!intege) {
                Object obj = materialValues.get(s1);
                ExcelWriterUtil.addCellData(resultData, rowIndex, i, obj);
                continue;
            }
            if (Double.valueOf(s1).intValue() == 1) {
                ExcelWriterUtil.addCellData(resultData, rowIndex, i - 1, "PWC");
                ExcelWriterUtil.addCellData(resultData, rowIndex + 2, i - 1, "PWO");
            }
            handlerDistribution(distribution, "C", Double.valueOf(s1).intValue() + "", rowIndex, i, resultData);
            handlerDistribution(distribution, "O", Double.valueOf(s1).intValue() + "", rowIndex + 2, i, resultData);

        }
        return resultData;

    }

    private void handlerDistribution(JSONObject distribution,
                                     String type,
                                     String key,
                                     Integer rowIndex,
                                     Integer columnIndex,
                                     List<CellData> resultData
    ) {
        JSONObject obj = distribution.getJSONObject(type);
        JSONObject jsonObject = obj.getJSONObject(key);
        if (Objects.isNull(jsonObject)) {
            return;
        }
        BigDecimal degreeSet = jsonObject.getBigDecimal("degreeSet");
        BigDecimal roundSet = jsonObject.getBigDecimal("roundSet");
        if (Objects.isNull(degreeSet) || Objects.isNull(roundSet)) {
            return;
        }
        if (BigDecimal.ZERO.doubleValue() == degreeSet.doubleValue() || BigDecimal.ZERO.doubleValue() == roundSet.doubleValue()) {
            return;
        }
        ExcelWriterUtil.addCellData(resultData, rowIndex, columnIndex, degreeSet);
        ExcelWriterUtil.addCellData(resultData, rowIndex + 1, columnIndex, roundSet);
    }


    private void handlerPart1(Workbook workbook, Sheet dictionary, WriterExcelDTO excelDTO, String url) {
        // 炉料结构
        String sheetName = PoiCellUtil.getCellValue(dictionary, 1, 0);
        // 策略
        String sheetOption = PoiCellUtil.getCellValue(dictionary, 1, 1);
        // 点名
        String tagName = PoiCellUtil.getCellValue(dictionary, 1, 2);
        // 最后一个值
        String lastVal = PoiCellUtil.getCellValue(dictionary, 1, 3);
        // 写入数据的行数
        String rowIndexStr = PoiCellUtil.getCellValue(dictionary, 1, 4);
        Integer rowIndex = 3;
        if (StringUtils.isNotBlank(rowIndexStr)) {
            rowIndex = Double.valueOf(rowIndexStr).intValue();
        }

        Sheet sheet = workbook.getSheet(sheetName);
        // 最后一个值
        String lastValKey = null;
        if (StringUtils.isNotBlank(lastVal)) {
            lastValKey = lastVal;
        }
        // 指定行的值
        List<String> rowVals = PoiCustomUtil.getRowCelVal(sheet, 2);
        List<CellData> rowCellDataList = new ArrayList<>();
        // 处理后的时间
        List<DateQuery> dateQueries = handlerDate(sheetOption, excelDTO.getDateQuery().getRecordDate());
        for (DateQuery dateQuery : dateQueries) {
            List<String> indexData = eachData(url, dateQuery, tagName);
            if (Objects.nonNull(indexData)) {
                List<Integer> indexs = indexData.stream().map(v -> Integer.parseInt(v.split("/")[0])).collect(Collectors.toList());
                List<String> times = indexData.stream().map(v -> v.split("/")[1]).collect(Collectors.toList());
                int min = 0;
                if (Objects.nonNull(lastValKey) && indexData.contains(lastValKey)) {
                    min = indexData.indexOf(lastValKey) + 1;
                }
                int max = indexs.size();
                for (int i = min; i < max; i++) {
                    Integer chargeNo = indexs.get(i);
                    List<CellData> cellDataList = changeData(url, chargeNo.toString(), times.get(i), rowIndex + i, rowVals);
                    rowCellDataList.addAll(cellDataList);
                }
                if (max > 0) {
                    updateDictChargeNo(dictionary, indexData.get(max - 1), 1, 3);
                    updateDictChargeNo(dictionary, max + rowIndex, 1, 4);
                }

            }
        }
        SheetRowCellData build = SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(rowCellDataList)
                .build();
        build.allValueWriteExcel();
    }

    /**
     * 更新指定行 指定列的值
     *
     * @param dictionary
     * @param obj
     */
    private void updateDictChargeNo(Sheet dictionary, Object obj, Integer rowNum, Integer colnum) {
        Row row = dictionary.getRow(rowNum);
        Cell cell = row.getCell(colnum);
        if (Objects.isNull(cell)) {
            cell = row.createCell(colnum);
        }
        PoiCustomUtil.setCellValue(cell, obj);
    }

    /**
     *
     */
    private List<DateQuery> handlerDate(String sheetOption, Date date) {
        String[] split = sheetOption.split("_");
        DateStrategy dateStrategy = strategyContext.getDate(split[2]);
        DateQuery handlerDate = dateStrategy.handlerDate(date);
        OptionsStrategy option = strategyContext.getOption(split[3]);
        return option.execute(handlerDate);
    }

    /**
     * 数据请求结果
     *
     * @param url
     * @param chargeNo
     * @param rowIndex
     * @param rowVals
     * @return
     */
    private List<CellData> changeData(String url, String chargeNo, String time, Integer rowIndex, List<String> rowVals) {
        url += "/charge/variation?chargeNo=" + chargeNo;
        String s = httpUtil.get(url);
        List<CellData> resultData = new ArrayList<>();
        if (StringUtils.isBlank(s)) {
            return resultData;
        }
        JSONObject object = JSONObject.parseObject(s);
        JSONObject data = object.getJSONObject("data");
        if (Objects.isNull(data)) {
            return resultData;
        }
//        JSONObject chargeIndexInDay = data.getJSONObject("chargeIndexInDay");
        JSONObject materialValues = data.getJSONObject("materialValues");
        if (Objects.isNull(materialValues)) {
            return resultData;
        }
        materialValues.put("chargeNo", chargeNo);
        materialValues.put("chargeIndexInDay", data.get("chargeIndexInDay"));
        materialValues.put("time", new Date(Long.valueOf(time)));
//        materialValues.put("chargeIndexInDay", chargeIndexInDay.get("value"));
//        materialValues.put("time", new Date(chargeIndexInDay.getLong("datetime")));
        int size = rowVals.size();
        for (int i = 0; i < size; i++) {
            String s1 = rowVals.get(i);
            if (StringUtils.isBlank(s1)) {
                continue;
            }
            Object obj = materialValues.get(s1);
            if (Objects.nonNull(obj) && "totalGrade".equals(s1)) {
                BigDecimal multiply = new BigDecimal(obj.toString()).multiply(new BigDecimal(100));
                obj = multiply.setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            if (Objects.nonNull(obj) && "totalGrade".equals(s1)) {
                BigDecimal multiply = new BigDecimal(obj.toString()).multiply(new BigDecimal(100));
                obj = multiply.setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            ExcelWriterUtil.addCellData(resultData, rowIndex, i, obj);

        }
        return resultData;
    }

    /**
     * 获取指定点名的值
     *
     * @param url       url
     * @param dateQuery 时间范围
     * @param tagName   点名
     * @return 结果
     */
    private List<String> eachData(String url, DateQuery dateQuery, String tagName) {
        JSONObject jsonObject = new JSONObject();
        Map<String, String> queryParam = dateQuery.getQueryParam();
        jsonObject.put("starttime", queryParam.get("starttime"));
        jsonObject.put("endtime", queryParam.get("endtime"));
        List<String> tagNames = new ArrayList<>();
        tagNames.add(tagName);
        jsonObject.put("tagnames", tagNames);
        url += "/getTagValues/tagNamesInRange";
        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject obj = JSONObject.parseObject(result);
        obj = obj.getJSONObject("data");
        JSONObject data = obj.getJSONObject(tagName);
        List<String> resultList = new ArrayList<>();
        if (Objects.nonNull(data)) {
            Set<String> keys = data.keySet();
            Long[] list = new Long[keys.size()];
            int k = 0;
            for (String key : keys) {
                list[k] = Long.valueOf(key);
                k++;
            }
            // 按照顺序排序
            Arrays.sort(list);
            for (Long key : list) {
                Integer integer = data.getInteger(key.toString());
                resultList.add(integer.toString() + "/" + key);
            }
        }
        return resultList;
    }

}
