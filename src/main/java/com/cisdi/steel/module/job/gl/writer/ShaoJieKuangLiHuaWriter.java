package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Description: 烧结矿理化指标处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/22 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class ShaoJieKuangLiHuaWriter extends AbstractExcelReadWriter {
    // 标记行
    private static int itemRowNum = 3;
    // 平均值计算起始行
    private static int beginRowNum = 4;
    private static int leftCellColumn = 1;
    private static int rightCellColumn = 28;

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());

        // 默认8号高炉，从模板中获取version
        String version ="8.0";
        try{
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        }catch(Exception e){
            log.info("从模板中获取version失败", e);
        }

        // 获取时间策略
        DateQuery date = this.getDateQuery(excelDTO);
        // 获取API url
        String url = getUrl(version);
        Sheet sheet = workbook.getSheetAt(0);
        List<String> columns = PoiCustomUtil.getRowCelVal(sheet, 3);
        sheet.getRow(3).setZeroHeight(true);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet dateSheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表 待处理
            String sheetName = dateSheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                int size = dateQueries.size();
                for (int j = 0; j < size; j++) {

                    DateQuery item = dateQueries.get(j);
                    if (item.getRecordDate().before(new Date())) {
                        // 获取查询参数
                        Map<String, String> queryParam = getQueryParam(item);
                        String result = httpUtil.get(url, queryParam);
//                        JSONObject jsonObject = JSONObject.parseObject(result);
                        JSONArray dataArray = convertJsonStringToJsonArray(result);
                        int dataArraySize = 0;
                        if (Objects.nonNull(dataArray)) {
                            dataArraySize = dataArray.size();
                        }
                        // 表格最后一行
                        int lastRowNum = itemRowNum + dataArraySize;
                        setBorderStyle(workbook, sheet, lastRowNum);
                        List<CellData> cellDataList = this.mapDataHandler(dataArray, columns);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    } else {
                        break;
                    }
                }
            }
        }

        return workbook;
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
     * 组装List<CellData>
     * @param jsonObject
     * @param columns
     * @return
     */
    protected List<CellData> mapDataHandler(JSONArray jsonArray, List<String> columns) {
        List<CellData> cellDataList = new ArrayList<>();
            if (Objects.nonNull(jsonArray) && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                    JSONObject json = jsonArray.getJSONObject(i);
                    JSONObject sinterAnaTypesJson = null;
                    JSONObject pelletsAnaTypesJson = null;
                    JSONObject lumporeAnaTypesJson = null;

                    if (Objects.nonNull(json)) {
                        Long timeValue = json.getLong("clock");
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String time = sdf.format(new Date(timeValue));
                        if (StringUtils.isNotBlank(time)) {
                            ExcelWriterUtil.addCellData(cellDataList, 4 + i, 1, time);
                        }
                        JSONObject categoriesJson = (JSONObject) json.get("categories");
                        if (Objects.nonNull(categoriesJson)) {
                            JSONObject sinterJson = (JSONObject) categoriesJson.get("SINTER");
                            if (Objects.nonNull(sinterJson)) {
                                sinterAnaTypesJson = (JSONObject) sinterJson.get("anaTypes");
                            }

                            JSONObject pelletsJson = (JSONObject) categoriesJson.get("PELLETS");
                            if (Objects.nonNull(pelletsJson)) {
                                pelletsAnaTypesJson = (JSONObject) pelletsJson.get("anaTypes");
                            }

                            JSONObject lumporeJson = (JSONObject) categoriesJson.get("LUMPORE");
                            if (Objects.nonNull(lumporeJson)) {
                                lumporeAnaTypesJson = (JSONObject) lumporeJson.get("anaTypes");
                            }
                        }
                    }

                    if (Objects.nonNull(columns) && !columns.isEmpty()) {
                        int size = columns.size();
                        for (int j = 0; j < size; j++) {
                            String column = columns.get(j);
                            if (StringUtils.isNotBlank(column)) {
                                String[] columnSplit = column.split("_");
                                Double doubleValue = 0.0d;
                                // 处理模板中sj开头的参数
                                if ("sj".equals(columnSplit[0])) {
                                    String sjColumn = columnSplit[1];
                                    if (Objects.nonNull(sinterAnaTypesJson)) {
                                        JSONObject lpJson = (JSONObject) sinterAnaTypesJson.get("LP");
                                        JSONObject lcJson = (JSONObject) sinterAnaTypesJson.get("LC");
                                        JSONObject lgJson = (JSONObject) sinterAnaTypesJson.get("LG");
                                        Object valueObject = null;
                                        if (Objects.nonNull(lpJson)) {
                                            valueObject = lpJson.get(sjColumn);
                                        }
                                        if (Objects.isNull(valueObject)) {
                                            if (Objects.nonNull(lcJson)) {
                                                valueObject = lcJson.get(sjColumn);
                                                if (Objects.isNull(valueObject)) {
                                                    if (Objects.nonNull(lgJson)) {
                                                        valueObject = lgJson.get(sjColumn);
                                                    }
                                                }
                                            }
                                        }
                                        if (Objects.nonNull(valueObject)) {
                                            String value = String.valueOf(valueObject);
                                            doubleValue = Double.parseDouble(value);
                                        }
                                    }
                                    // 处理模板中qt开头的参数
                                } else if ("qt".equals(columnSplit[0])) {
                                    String qtColumn = columnSplit[1];
                                    if (Objects.nonNull(pelletsAnaTypesJson)) {
                                        JSONObject lcJson = (JSONObject) pelletsAnaTypesJson.get("LC");
                                        JSONObject lgJson = (JSONObject) pelletsAnaTypesJson.get("LG");
                                        Object valueObject = lcJson.get(qtColumn);
                                        if (Objects.isNull(valueObject)) {
                                            if (Objects.nonNull(lgJson)) {
                                                valueObject = lgJson.get(qtColumn);
                                            }
                                        }
                                        if (Objects.nonNull(valueObject)) {
                                            String value = String.valueOf(valueObject);
                                            doubleValue = Double.parseDouble(value);
                                        }
                                    }
                                    // 处理模板中kk开头的参数
                                } else if ("kk".equals(columnSplit[0])) {
                                    String kkColumn = columnSplit[1];
                                    if (Objects.nonNull(lumporeAnaTypesJson)) {
                                        JSONObject lpJson = (JSONObject) lumporeAnaTypesJson.get("LP");
                                        JSONObject lcJson = (JSONObject) lumporeAnaTypesJson.get("LC");
                                        JSONObject lgJson = (JSONObject) lumporeAnaTypesJson.get("LG");
                                        Object valueObject = null;
                                        if (Objects.nonNull(lpJson)) {
                                            valueObject = lpJson.get(kkColumn);
                                        }
                                        if (Objects.isNull(valueObject)) {
                                            if (Objects.nonNull(lcJson)) {
                                                valueObject = lcJson.get(kkColumn);
                                            }

                                            if (Objects.isNull(valueObject)) {
                                                if (Objects.nonNull(lgJson)) {
                                                    valueObject = lgJson.get(kkColumn);
                                                }
                                            }
                                        }
                                        if (Objects.nonNull(valueObject)) {
                                            String value = String.valueOf(valueObject);
                                            doubleValue = Double.parseDouble(value);
                                        }
                                    }
                                }
                                ExcelWriterUtil.addCellData(cellDataList, 4 + i, j, doubleValue);
                            }
                        }
                    }

                }
            }

        return cellDataList;
    }

    /**
     *
     */
    private void setBorderStyle(Workbook workbook, Sheet sheet, int lastRowNum) {
        //设置每个单元格的四周边框
        CellStyle cellNormalStyle = workbook.createCellStyle();
        cellNormalStyle.setBorderRight(BorderStyle.THIN);
        cellNormalStyle.setBorderBottom(BorderStyle.THIN);
        for (int i = beginRowNum; i <= lastRowNum - 1; i++) {
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
        for (int i = beginRowNum; i <= lastRowNum; i++) {
            Cell cell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(sheet, i), leftCellColumn);
            cell.setCellStyle(cellLeftStyle);
        }

        // 最右侧边框
        CellStyle cellRightStyle = workbook.createCellStyle();
        cellRightStyle.setBorderRight(BorderStyle.THICK);
        cellRightStyle.setBorderBottom(BorderStyle.THIN);
        for (int i = beginRowNum; i <= lastRowNum; i++) {
            Cell cell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(sheet, i), rightCellColumn);
            cell.setCellStyle(cellRightStyle);
        }

        // 最后一行下边框
        CellStyle cellBottomStyle = workbook.createCellStyle();
        cellBottomStyle.setBorderBottom(BorderStyle.THICK);
        cellBottomStyle.setBorderRight(BorderStyle.THIN);
        Cell cell = null;
        for (int i = leftCellColumn; i <= rightCellColumn; i++) {
            cell = ExcelWriterUtil.getCellOrCreate(sheet.getRow(lastRowNum), i);
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
     * 组装API url
     * @param version
     * @return
     */
    protected String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/analysisCharges";
    }
}
