package com.cisdi.steel.module.job.a1.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONArray;
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
import javafx.beans.binding.ObjectExpression;
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
//        handlerPart1(workbook, dictionary, excelDTO, url);
        // 料制
//        handlerPart2(workbook, dictionary, excelDTO, url);


        String s = httpUtil.get(url + "/burden/latest/forward");
        if (StringUtils.isBlank(s)) {
            return workbook;
        }
        JSONObject jsonObject = JSONObject.parseObject(s);

        // 料制
        handlerPart3(workbook, dictionary, excelDTO, jsonObject);
        // 炉料结构
        handlerPart4(workbook, dictionary, excelDTO, jsonObject);
        return workbook;
    }

    private void handlerPart4(Workbook workbook, Sheet dictionary, WriterExcelDTO excelDTO, JSONObject jsonObject) {
        int rowNum = 1;
        // 炉料结构
        String sheetName = PoiCellUtil.getCellValue(dictionary, rowNum, 0);
        // 写入数据的行数
        String rowIndexStr = PoiCellUtil.getCellValue(dictionary, rowNum, 4);
        int rowIndex = 3;
        if (StringUtils.isNotBlank(rowIndexStr)) {
            rowIndex = Double.valueOf(rowIndexStr).intValue();
        }

        Sheet sheet = workbook.getSheet(sheetName);
        List<String> rowVals = PoiCustomUtil.getRowCelVal(sheet, 2);
        List<CellData> rowCellDataList = this.changeluliaojiegou(jsonObject, rowIndex, rowVals);
        if (Objects.nonNull(rowCellDataList) && !rowCellDataList.isEmpty()) {
            // 更新写入数据的行数
            updateDictChargeNo(dictionary, rowIndex + 1, rowNum, 4);
        }
        SheetRowCellData build = SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(rowCellDataList)
                .build();
        build.allValueWriteExcel();
    }

    private List<CellData> changeluliaojiegou(JSONObject jsonObject, int rowIndex, List<String> rowVals) {
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject result = new JSONObject();
        handlerDataMethod(result, data, "parameters");
        handlerDataMethod(result, data, "results");
        handlerDataMethod(result, data, "slag");

        JSONArray bookOreRatios = data.getJSONArray("bookOreRatios");
        if (Objects.nonNull(bookOreRatios)) {
            int size = bookOreRatios.size();
            for (int i = 0; i < size; i++) {
                JSONObject bookOreRatio = bookOreRatios.getJSONObject(i);
                if (Objects.nonNull(bookOreRatio)) {
                    String matClass = bookOreRatio.getString("matClass");
                    Object ratio = bookOreRatio.get("ratio");
                    result.put("bookOreRatios/" + matClass + "/ratio", ratio);
                }
            }
        }


        // 获取不包含斜杠的点名
        List<String> collect = rowVals.stream().filter(val -> {
            if ("".equals(val) || "time".equals(val)) {
                return false;
            }
            return !val.contains("/");
        }).collect(Collectors.toList());

        List<String> a = collect.stream().filter(val -> val.contains("_")).collect(Collectors.toList());
        List<String> b = collect.stream().filter(val -> !val.contains("_")).collect(Collectors.toList());

        JSONArray bookMaterials = data.getJSONArray("bookMaterials");
        if (Objects.nonNull(bookMaterials)) {
            for (String key : a) {
                List<Double> resultData = new ArrayList<>();
                for (int i = 0; i < bookMaterials.size(); i++) {
                    JSONObject json = bookMaterials.getJSONObject(i);
                    String matClass = json.getString("brandCode");
                    if (key.equals(matClass)) {
                        Double jsonVal = json.getDouble("weight");
                        if (Objects.nonNull(jsonVal)) {
                            resultData.add(jsonVal);
                        }
                    }
                }
                if (!resultData.isEmpty()) {
                    double sum = resultData.stream().mapToDouble(Double::doubleValue).sum();
                    if (sum != 0) {
                        result.put(key, sum);
                    }
                }
            }

            for (String key : b) {
                List<Double> resultData = new ArrayList<>();
                for (int i = 0; i < bookMaterials.size(); i++) {
                    JSONObject json = bookMaterials.getJSONObject(i);
                    String matClass = json.getString("matClass");
                    if (key.equals(matClass)) {
                        Double jsonVal = json.getDouble("weight");
                        if (Objects.nonNull(jsonVal)) {
                            resultData.add(jsonVal);
                        }
                    }
                }
                if (!resultData.isEmpty()) {
                    double sum = resultData.stream().mapToDouble(Double::doubleValue).sum();
                    if (sum != 0) {
                        result.put(key, sum);
                    }
                }
            }
        }

        result.put("time", new Date());

        List<CellData> resultData = new ArrayList<>();
        int size = rowVals.size();
        for (int i = 0; i < size; i++) {
            String s1 = rowVals.get(i);
            if (StringUtils.isBlank(s1)) {
                continue;
            }
            Object obj = result.get(s1);
            ExcelWriterUtil.addCellData(resultData, rowIndex, i, obj);
        }
        return resultData;
    }

    private void handlerDataMethod(JSONObject result, JSONObject data, String type) {
        JSONObject parameters = data.getJSONObject(type);
        if (Objects.nonNull(parameters)) {
            JSONObject components = parameters.getJSONObject("components");
            if (Objects.nonNull(components)) {
                Set<String> keySet = components.keySet();
                keySet.forEach(key -> {
                    Object o = components.get(key);
                    result.put(type + "/" + key, o);
                });
            }
        }

    }

    private void handlerPart3(Workbook workbook, Sheet dictionary, WriterExcelDTO excelDTO, JSONObject jsonObject) {
        int rowNum = 2;
        // 料制
        String sheetName = PoiCellUtil.getCellValue(dictionary, rowNum, 0);
        // 写入数据的行数
        String rowIndexStr = PoiCellUtil.getCellValue(dictionary, rowNum, 4);
        int rowIndex = 3;
        if (StringUtils.isNotBlank(rowIndexStr)) {
            rowIndex = Double.valueOf(rowIndexStr).intValue();
        }

        Sheet sheet = workbook.getSheet(sheetName);
        List<String> rowVals = PoiCustomUtil.getRowCelVal(sheet, 2);
        List<CellData> rowCellDataList = this.changeLiaozhiData(jsonObject, rowIndex, rowVals);
        if (Objects.nonNull(rowCellDataList) && !rowCellDataList.isEmpty()) {
            // 更新写入数据的行数
            updateDictChargeNo(dictionary, rowIndex + 4, rowNum, 4);
        }
        SheetRowCellData build = SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(rowCellDataList)
                .build();
        build.allValueWriteExcel();

    }

    private List<CellData> changeLiaozhiData(JSONObject jsonObject, int rowIndex, List<String> rowVals) {
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject materialValues = new JSONObject();
        materialValues.put("time", new Date());

        JSONObject parameters = data.getJSONObject("parameters");
        if (Objects.nonNull(parameters)) {
            JSONObject components = parameters.getJSONObject("components");
            if (Objects.nonNull(components)) {
                materialValues.put("OreStock", components.getInteger("OreStock"));
                materialValues.put("CokeStock", components.getInteger("CokeStock"));
                materialValues.put("OreWeight", components.getInteger("OreWeight"));
            }
        }
        JSONObject results = data.getJSONObject("results");
        if (Objects.nonNull(results)) {
            JSONObject components = results.getJSONObject("components");
            if (Objects.nonNull(components)) {
                materialValues.put("AllOCRate", components.getInteger("AllOCRate"));
                materialValues.put("ClinkerRatio", components.getInteger("ClinkerRatio"));
            }
        }

        JSONArray distribution = data.getJSONArray("distribution");
        List<CellData> resultData = new ArrayList<>();

        List<String> collect = rowVals.stream().map(item -> {
            if ("".equals(item)) {
                return item;
            }
            String[] split = item.split("/");
            if (split.length == 1) {
                return item;
            } else {
                return split[split.length - 1];
            }
        }).collect(Collectors.toList());
        int size = collect.size();
        int count = 0;
        int count2 = 0;
        for (int i = 0; i < size; i++) {
            String s1 = collect.get(i);
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
            boolean flag = handlerDistribution2(distribution, "C", Double.valueOf(s1).intValue() + "", rowIndex, i - count, resultData);
            if (!flag) {
                count++;
            }
            boolean flag2 = handlerDistribution2(distribution, "O", Double.valueOf(s1).intValue() + "", rowIndex + 2, i - count2, resultData);
            if (!flag2) {
                count2++;
            }
        }
        return resultData;
    }

    private boolean handlerDistribution2(JSONArray distribution, String type, String value, int rowIndex, int columnIndex, List<CellData> resultData) {
        int size = distribution.size();
        JSONObject jsonObject = null;
        for (int j = 0; j < size; j++) {
            JSONObject data = distribution.getJSONObject(j);
            if (Objects.isNull(data)) {
                continue;
            }
            String typ = data.getString("typ");
            String seq = data.getString("seq");
            if (type.equals(typ) && value.equals(seq)) {
                jsonObject = data;
                break;
            }
        }
        if (Objects.isNull(jsonObject)) {
            return false;
        }
        BigDecimal degreeSet = jsonObject.getBigDecimal("angle");
        BigDecimal roundSet = jsonObject.getBigDecimal("round");
        if (Objects.isNull(degreeSet) || Objects.isNull(roundSet)) {
            return false;
        }
        if (BigDecimal.ZERO.doubleValue() == degreeSet.doubleValue() || BigDecimal.ZERO.doubleValue() == roundSet.doubleValue()) {
            return false;
        }
        ExcelWriterUtil.addCellData(resultData, rowIndex, columnIndex, degreeSet);
        ExcelWriterUtil.addCellData(resultData, rowIndex + 1, columnIndex, roundSet);
        return true;
    }


    private void handlerPart2(Workbook workbook, Sheet dictionary, WriterExcelDTO excelDTO, String url) {
        int rowNum = 2;
        // 料制
        String sheetName = PoiCellUtil.getCellValue(dictionary, rowNum, 0);
        // 策略
        String sheetOption = PoiCellUtil.getCellValue(dictionary, rowNum, 1);
        // 点名
        String tagName = PoiCellUtil.getCellValue(dictionary, rowNum, 2);
        // 最后一个值
        String lastVal = PoiCellUtil.getCellValue(dictionary, rowNum, 3);
        // 写入数据的行数
        String rowIndexStr = PoiCellUtil.getCellValue(dictionary, rowNum, 4);
        int rowIndex = 3;
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
                int count = 0;
                int lastRowIndex = rowIndex;
                for (int i = min; i < max; i++) {
                    Integer chargeNo = indexs.get(i);
                    Integer newRowIndex = rowIndex + count * 4;
                    lastRowIndex = newRowIndex;
                    List<CellData> cellDataList = changeLiaozhiData(url, chargeNo.toString(), times.get(i), newRowIndex, rowVals);
                    if (cellDataList.size() != 0) {
                        count++;
                        rowCellDataList.addAll(cellDataList);
                    }
                }
                if (max > 0) {
                    updateDictChargeNo(dictionary, indexData.get(max - 1), rowNum, 3);
                    updateDictChargeNo(dictionary, lastRowIndex, rowNum, 4);
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
        if (Objects.isNull(data.get("chargeIndexInDay"))) {
            return resultData;
        }
        JSONObject materialValues = data.getJSONObject("materialValues");
        if (Objects.isNull(materialValues)) {
            return resultData;
        }
        materialValues.put("chargeNo", chargeNo);
        materialValues.put("chargeIndexInDay", data.get("chargeIndexInDay"));
        materialValues.put("time", new Date(Long.valueOf(time)));
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
        int count = 0;
        int count2 = 0;
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
            boolean flag = handlerDistribution(distribution, "C", Double.valueOf(s1).intValue() + "", rowIndex, i - count, resultData);
            if (!flag) {
                count++;
            }
            boolean flag2 = handlerDistribution(distribution, "O", Double.valueOf(s1).intValue() + "", rowIndex + 2, i - count2, resultData);
            if (!flag2) {
                count2++;
            }

        }
        return resultData;

    }


    private boolean handlerDistribution(JSONObject distribution,
                                        String type,
                                        String key,
                                        Integer rowIndex,
                                        Integer columnIndex,
                                        List<CellData> resultData
    ) {
        JSONObject obj = distribution.getJSONObject(type);
        JSONObject jsonObject = obj.getJSONObject(key);
        if (Objects.isNull(jsonObject)) {
            return false;
        }
        BigDecimal degreeSet = jsonObject.getBigDecimal("degreeSet");
        BigDecimal roundSet = jsonObject.getBigDecimal("roundSet");
        if (Objects.isNull(degreeSet) || Objects.isNull(roundSet)) {
            return false;
        }
        if (BigDecimal.ZERO.doubleValue() == degreeSet.doubleValue() || BigDecimal.ZERO.doubleValue() == roundSet.doubleValue()) {
            return false;
        }
        ExcelWriterUtil.addCellData(resultData, rowIndex, columnIndex, degreeSet);
        ExcelWriterUtil.addCellData(resultData, rowIndex + 1, columnIndex, roundSet);
        return true;
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
        int rowIndex = 3;
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
                int count = 0;
                for (int i = min; i < max; i++) {
                    Integer chargeNo = indexs.get(i);
                    Integer row = rowIndex + count;
                    List<CellData> cellDataList = changeData(url, chargeNo.toString(), times.get(i), row, rowVals);
                    if (cellDataList.size() != 0) {
                        count++;
                        rowCellDataList.addAll(cellDataList);
                    }
                }
                if (max > 0) {
                    updateDictChargeNo(dictionary, indexData.get(max - 1), 1, 3);
                    updateDictChargeNo(dictionary, count + rowIndex, 1, 4);
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
        if (Objects.isNull(data.get("chargeIndexInDay"))) {
            return resultData;
        }
        JSONObject materialValues = data.getJSONObject("materialValues");
        if (Objects.isNull(materialValues)) {
            return resultData;
        }
        materialValues.put("chargeNo", chargeNo);
        materialValues.put("chargeIndexInDay", data.get("chargeIndexInDay"));
        materialValues.put("time", new Date(Long.valueOf(time)));
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
