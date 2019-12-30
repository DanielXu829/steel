package com.cisdi.steel.module.job.gl.writer;

import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Description: 出铁化学成分执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/12/12 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class ChuTieHuaXueChengFenWriter extends AbstractExcelReadWriter {
    private static Map<Integer, String> colNumToLetterMap = new HashMap<>();
    private static String[] letterArray = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W"};
    // 标记行
    private static int itemRowNum = 3;
    // 平均值计算起始行
    private static int avgBeginRowNum = 4;
    private static String avgValue = "平均";
    private static int leftCellColumn = 1;
    private static int rightCellColumn = 22;

    static {
        if (letterArray.length >= 19) {
            for (int i = 4; i <= 22; i++) {
                colNumToLetterMap.put(i, letterArray[i]);
            }
        }
    }

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = "8.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
            log.error("在模板中获取version失败", e);
        }

        DateQuery date = this.getDateQuery(excelDTO);
        DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());
        // 报表当前记录时间的前一天晚上8点到当前晚上24点，重新生成报表不会改变记录时间
        Date tieLiangEndTime = date.getRecordDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tieLiangEndTime);
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        tieLiangEndTime = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        calendar.set(Calendar.HOUR, 20);
        Date tieLiangStartTime = calendar.getTime();
        String tieLiangData = getTieLiangData(tieLiangStartTime, tieLiangEndTime, version);
        Map<String, Double> tapNoToActWeightMap = handleTieLiangData(tieLiangData);

        Sheet sheet = workbook.getSheetAt(0);
        // 将第4行（标记行）隐藏
        sheet.getRow(itemRowNum).setZeroHeight(true);
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
        // 通过api获取数据
        String shengTieData = getData("HM", dateQuery, version);
        JSONArray shengTieDataArray = convertJsonStringToJsonArray(shengTieData);
        String luZhaData = getData("SLAG", dateQuery, version);
        JSONArray luZhaDataArray = convertJsonStringToJsonArray(luZhaData);
        int shengTieArraySize = 0;
        int luZhaArraySize = 0;
        if (Objects.nonNull(shengTieDataArray)) {
            shengTieArraySize = shengTieDataArray.size();
        }
        if (Objects.nonNull(luZhaDataArray)) {
            luZhaArraySize = shengTieDataArray.size();
        }

        // 获取数据的行数
        int dataSize = shengTieArraySize >= luZhaArraySize ? shengTieArraySize : luZhaArraySize;

        // 重新生成平均值行
        int newAverageRowNum = itemRowNum + dataSize + 1;
        resetAverageRow(workbook, sheet, newAverageRowNum);

        // 写入数据
        if (itemNameList != null && !itemNameList.isEmpty()) {
            for (int i = 0; i < itemNameList.size(); i++) {
                // 执行生铁成分的数据写入
                if ("ST_Si".equals(itemNameList.get(i))) {
                    String shengTiePrefix = "ST_";
                    List<CellData> cellDataList = mapDataHandler(shengTieDataArray, shengTiePrefix, itemNameList, itemRowNum, i, sheet, tapNoToActWeightMap);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
                // 执行炉渣成分的数据写入
                if ("LZ_SiO2".equals(itemNameList.get(i))) {
                    String luZhaPrefix = "LZ_";
                    List<CellData> cellDataList = mapDataHandler(luZhaDataArray, luZhaPrefix, itemNameList, itemRowNum, i, sheet, tapNoToActWeightMap);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }

        return workbook;
    }

    /**
     * 设置边框样式
     */
    private void setBorderStyle(Workbook workbook, Sheet sheet, int newAverageRowNum) {
        //设置每个单元格的四周边框
        CellStyle cellNormalStyle = workbook.createCellStyle();
        cellNormalStyle.setBorderRight(BorderStyle.THIN);
        cellNormalStyle.setBorderBottom(BorderStyle.THIN);
        for (int i = avgBeginRowNum; i <= newAverageRowNum - 1; i++) {
            for (int j = leftCellColumn; j < rightCellColumn; j++) {
                Cell cell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(sheet, i), j);
                cell.setCellStyle(cellNormalStyle);
            }
        }

        // 最左侧列边框
        CellStyle cellLeftStyle = workbook.createCellStyle();
        cellLeftStyle.setBorderLeft(BorderStyle.THICK);
        cellLeftStyle.setBorderBottom(BorderStyle.THIN);
        cellLeftStyle.setBorderRight(BorderStyle.THIN);
        for (int i = avgBeginRowNum; i <= newAverageRowNum; i++) {
            Cell cell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(sheet, i), leftCellColumn);
            cell.setCellStyle(cellLeftStyle);
        }

        // 最右侧边框
        CellStyle cellRightStyle = workbook.createCellStyle();
        cellRightStyle.setBorderRight(BorderStyle.THICK);
        cellRightStyle.setBorderBottom(BorderStyle.THIN);
        for (int i = avgBeginRowNum; i <= newAverageRowNum; i++) {
            Cell cell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(sheet, i), rightCellColumn);
            cell.setCellStyle(cellRightStyle);
        }

        // 最后一行下边框
        CellStyle cellBottomStyle = workbook.createCellStyle();
        cellBottomStyle.setBorderBottom(BorderStyle.THICK);
        cellBottomStyle.setBorderRight(BorderStyle.THIN);
        Cell cell = null;
        for (int i = leftCellColumn; i <= rightCellColumn; i++) {
            cell = ExcelWriterUtil.getCellOrCreate(sheet.getRow(newAverageRowNum), i);
            if (i == leftCellColumn) {
                CellStyle cellBottomLeftStyle = workbook.createCellStyle();
                cellBottomLeftStyle.setBorderLeft(BorderStyle.THICK);
                cellBottomLeftStyle.setBorderBottom(BorderStyle.THICK);
                cellBottomLeftStyle.setBorderRight(BorderStyle.THIN);
                cell.setCellStyle(cellBottomLeftStyle);
                continue;
            }
            if (i == rightCellColumn) {
                CellStyle cellBottomRightStyle = workbook.createCellStyle();
                cellBottomRightStyle.setBorderRight(BorderStyle.THICK);
                cellBottomRightStyle.setBorderBottom(BorderStyle.THICK);
                cell.setCellStyle(cellBottomRightStyle);
                continue;
            }

            cell.setCellStyle(cellBottomStyle);
        }

    }

    /**
     *  移除原本的平均值行，重新设置新的平均值公式
     * @param dataSize
     * @param sheet
     */
    private void resetAverageRow(Workbook workbook, Sheet sheet, int newAverageRowNum) {
        Cell averageCell = PoiCustomUtil.getCellByValue(sheet, avgValue);
        if (Objects.isNull(averageCell)) {
            return;
        }

        Row row = averageCell.getRow();
        int averageRowNum = row.getRowNum();

        // 如果数据量未增多，不用删除平均值的行
        if (averageRowNum >= newAverageRowNum) {
            return;
        }

        // 将公式去除，并移除行
        for (int i = 4; i <= 22; i++) {
            row.getCell(i).setCellType(CellType.BLANK);
        }
        // 清除合并单元格
        PoiCustomUtil.removeMergedRegion(sheet, averageRowNum, 1);
        // 删除平均值行
        PoiCustomUtil.removeRow(sheet, averageRowNum);

        Row newAverageRow = ExcelWriterUtil.getRowOrCreate(sheet, newAverageRowNum);
        Cell avgCell = ExcelWriterUtil.getCellOrCreate(newAverageRow, leftCellColumn);
        PoiCustomUtil.setCellValue(avgCell, avgValue);

        // 设置公式
        for (int i = 4; i <= 22; i++) {
            String letter = colNumToLetterMap.get(i);
            String avgBegin = letter + (avgBeginRowNum + 1);
            String avgEnd = letter + newAverageRowNum;
            String formulaPrefix = "IFERROR(AVERAGE(";
            String formulaSuffix =  "), \"\")";
            String formula = formulaPrefix + avgBegin + ":" + avgEnd + formulaSuffix;
            Cell newAverageCell = ExcelWriterUtil.getCellOrCreate(newAverageRow, i);
            newAverageCell.setCellFormula(formula);
            newAverageCell.setCellType(CellType.FORMULA);
        }

        // TODO "平均" 居中未生效
        PoiMergeCellUtil.addMergedRegion(sheet, newAverageRowNum, newAverageRowNum, leftCellColumn, leftCellColumn + 2);
        CellStyle averageCellStyle = workbook.createCellStyle();
        averageCellStyle.setAlignment(HorizontalAlignment.CENTER);
        avgCell.setCellStyle(averageCellStyle);

        // 设置表格边框样式
        setBorderStyle(workbook, sheet, newAverageRowNum);
    }

    /**
     * 将JSON串转换为JSON数组
     * @param data
     * @return
     */
    private JSONArray convertJsonStringToJsonArray(String data) {
        JSONArray dataArray = null;
        if (StringUtils.isNotBlank(data)) {
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (Objects.nonNull(jsonObject)) {
                dataArray = jsonObject.getJSONArray("data");
            }
        }

        return dataArray;
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
    private List<CellData> mapDataHandler(JSONArray dataArray, String prefix, List<String> itemNameList, Integer itemRowNum, Integer itemColNum, Sheet sheet, Map<String, Double> tapNoToActWeightMap) {
        List<CellData> cellDataList = new ArrayList<>();
        if (Objects.nonNull(dataArray) && dataArray.size() != 0) {
            int arraySize = dataArray.size();
            for (int i = 0; i < arraySize; i++) {
                JSONObject dataObj = dataArray.getJSONObject(i);
                if (Objects.nonNull(dataObj)) {
                    JSONObject valueObj = dataObj.getJSONObject("values");
                    JSONObject analysisObj = dataObj.getJSONObject("analysis");

                    if (Objects.nonNull(valueObj)) {
                        // 遍历标记行所有的单元格
                        for (int j = 0; j < itemNameList.size(); j++) {
                            // 获取标记项单元格中的值
                            String itemName = itemNameList.get(j);
                            if (StringUtils.isNotBlank(itemName)) {
                                if (itemName.indexOf(prefix) >= 0) {
                                    String itemNameTrue = itemName.substring(prefix.length());
                                    Double cellValue;
                                    if ("R".equals(itemNameTrue)) {
                                        cellValue = valueObj.getDouble("B2");
                                    } else if ("R4".equals(itemNameTrue)) {
                                        cellValue = valueObj.getDouble("B4");
                                    } else {
                                        cellValue = valueObj.getDouble(itemNameTrue);
                                        if (cellValue != null) {
                                            BigDecimal b1 = new BigDecimal(cellValue.toString());
                                            BigDecimal value = b1.multiply(new BigDecimal("100"));
                                            cellValue = value.doubleValue();
                                        }
                                    }
                                    // 设置时间
                                    Long timeValue = analysisObj.getLong("clock");
                                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                    String time = sdf.format(new Date(timeValue));
                                    // 设置铁次
                                    String sampleId = analysisObj.getString("sampleid");
                                    String[] tapNoArray = {};
                                    if (StringUtils.isNotBlank(sampleId)) {
                                        tapNoArray = sampleId.split("\\*");
                                    }

                                    String tapNo = null;
                                    if (tapNoArray.length > 0) {
                                        tapNo = tapNoArray[0];
                                    }
                                    Double actWeight = tapNoToActWeightMap.get(tapNo);
                                    Integer row = itemRowNum + arraySize - i;
                                    Integer col = j;
                                    ExcelWriterUtil.addCellData(cellDataList, row, 1, time);
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, cellValue);
                                    ExcelWriterUtil.addCellData(cellDataList, row, 2, tapNo);
                                    ExcelWriterUtil.addCellData(cellDataList, row, 3, actWeight);
                                    if ("LZ_".equals(prefix)) {
                                        calMgAL(row, sheet);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return cellDataList;
    }

    /**
     * 设置美铝比计算公式
     */
    private void calMgAL(int row, Sheet sheet) {
        String formula = "IFERROR(Q" + (row + 1) + "/" + "R" + (row + 1) + ", \"\")";
        Row calRow = ExcelWriterUtil.getRowOrCreate(sheet, row);
        Cell avgCell = ExcelWriterUtil.getCellOrCreate(calRow, 21);
        avgCell.setCellFormula(formula);
        avgCell.setCellType(CellType.FORMULA);
    }

    /**
     * 获取api返回的数据
     * @param brandCode (HM: 生铁成分, SLAG: 炉渣成分)
     * @param dateQuery
     * @param version
     * @return api数据
     */
    private String getData(String brandCode, DateQuery dateQuery, String version) {
        Map<String, String> queryParam = this.getQueryParam(dateQuery);
        queryParam.put("type", "LC");
        queryParam.put("brandcode", brandCode);
        return httpUtil.get(getUrl(version), queryParam);
    }

    private String getTieLiangData(Date tieLiangStartTime, Date tieLiangEndTime, String version) {
        Map<String, String> queryParam = new HashMap();
//        Objects.requireNonNull(getQueryStartTime()).toString()
        queryParam.put("startTime",  Objects.requireNonNull(tieLiangStartTime.getTime()).toString());
        queryParam.put("endTime",  Objects.requireNonNull(tieLiangEndTime.getTime()).toString());
        queryParam.put("pageSize", "24");
        return httpUtil.get(getTapUrl(version), queryParam);
    }

    private Map<String, Double> handleTieLiangData(String data) {
        Map<String, Double> tapNoToActWeightMap = new HashMap();
        JSONArray dataArray = convertJsonStringToJsonArray(data);
        if (Objects.nonNull(dataArray) && dataArray.size() != 0) {
            int arraySize = dataArray.size();
            for (int i = 0; i < arraySize; i++) {
                JSONObject dataObj = dataArray.getJSONObject(i);
                if (Objects.nonNull(dataObj)) {
                    String tapNo = dataObj.getString("tapNo");
                    Double actWeight = dataObj.getDouble("actWeight");
                    tapNoToActWeightMap.put(tapNo, actWeight);
                }
            }
        }

        return tapNoToActWeightMap;
    }

    /**
     * 获取api的url
     * @param version
     * @return url
     */
    private String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/analysisValues/clock";
    }

    private String getTapUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/tap/detail";
    }
}
