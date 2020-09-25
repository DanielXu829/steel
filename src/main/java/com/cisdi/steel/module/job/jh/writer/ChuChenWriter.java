package com.cisdi.steel.module.job.jh.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import com.cisdi.steel.module.report.mapper.TargetManagementOldMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@SuppressWarnings("ALL")
@Slf4j
public class ChuChenWriter extends AbstractExcelReadWriter {
    @Autowired
    private TargetManagementMapper targetManagementMapper;

    @Autowired
    private TargetManagementOldMapper targetManagementOldMapper;

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        // 从模板中获取version
        String version ="910.0";
        try{
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        }catch(Exception e){
            log.error("在模板中获取version失败", e);
        }
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表 待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 有需求，可以自己组装dateQueries
                List<DateQuery> dateQueries = DateQueryUtil.buildDay2HourFromYesEighteen(date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                StringBuffer buffer = new StringBuffer();
                for (int j = 0; j < columns.size(); j++) {
                    String tagName = targetManagementMapper.selectTargetFormulaByTargetName(columns.get(j));
                    if (StringUtils.isBlank(tagName)) {
                        tagName = targetManagementOldMapper.selectTargetFormulaByTargetName(columns.get(j));
                    }
                    if (StringUtils.isBlank(tagName)) {
                        columns.set(j, "");
                    } else {
                        columns.set(j, tagName);
                        buffer = buffer.append(columns.get(j).concat(","));
                    }
                }
                String searchParam = buffer.toString();
                searchParam = searchParam.substring(0, searchParam.length() - 1);
                int size = dateQueries.size();

                for (int j = 0; j < size; j++) {
                    DateQuery item = dateQueries.get(j);
                    if (item.getRecordDate().before(new Date())) {
                        int rowIndex = j + 1;
                        List<CellData> cellDataList = this.mapDataHandler(rowIndex, getUrl(version), searchParam, columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    } else {
                        continue;
                    }
                }
            }
        }
        Date currentDate = new Date();
        for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet=workbook.getSheetAt(i);
            if (!Objects.isNull(sheet) && !workbook.isSheetHidden(i)) {
                // 全局替换 当前日期
                ExcelWriterUtil.replaceCurrentDateInTitle(sheet, "%当前日期%", currentDate);
            }
        }
        return workbook;
    }

    /**
     * 通过API拿数据，并且按传入行数和列名来组装List<CellData>
     * @param rowIndex 行数
     * @param url
     * @param columns
     * @param tagColumns
     * @param dateQuery
     * @return List<CellData>
     */
    protected List<CellData> mapDataHandler(Integer rowIndex, String url, String searchParam, List<String> columnCells, DateQuery dateQuery) {
        Map<String, String> queryParam = new HashMap<String, String>();
        queryParam.put("startDate", String.valueOf(dateQuery.getQueryStartTime()));
        queryParam.put("endDate", String.valueOf(dateQuery.getQueryEndTime()));
        List<CellData> cellDataList = new ArrayList<>();

        if (Objects.nonNull(columnCells)) {
            queryParam.put("tagNames", searchParam);
            String result = httpUtil.get(url, queryParam);
            for (int i = 0; i < columnCells.size(); i++) {
                String column = columnCells.get(i);
                if (StringUtils.isNotBlank(column)) {
                    // 可能是处理方法加tag点的组合。 e.g: max,tag1,tag2 需要根据最前面的方式做特殊处理
                    String[] columnSplit = column.split(",");
                    // TODO if中可以删掉
                    if (Objects.nonNull(columnSplit) && columnSplit.length > 2) {
                        List<Double> specialValues = new ArrayList<Double>();
                        // 获取处理方式
                        String executeWay = columnSplit[0];
                        int columnSplitSize = columnSplit.length;
                        for (int k = 1; k < columnSplitSize; k++) {
                            if (StringUtils.isNotBlank(result)) {
                                JSONObject jsonObject = JSONObject.parseObject(result);
                                if (Objects.nonNull(jsonObject)) {
                                    JSONObject dataObject = jsonObject.getJSONObject("data");
                                    if (Objects.nonNull(dataObject)) {
                                        JSONArray arr = dataObject.getJSONArray(columnSplit[k]);
                                        if (Objects.nonNull(arr) && arr.size() != 0) {
                                            Double val = getLatestNonZeroValue(arr);
                                            specialValues.add(val);
                                        }
                                    }
                                }
                            }
                        }
                        // list中的值经过处理
                        Double executeVal = ExcelWriterUtil.executeSpecialList(executeWay, specialValues);
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, executeVal);
                    } else {
                        if (StringUtils.isNotBlank(result)) {
                            JSONObject jsonObject = JSONObject.parseObject(result);
                            if (Objects.nonNull(jsonObject)) {
                                JSONObject dataObject = jsonObject.getJSONObject("data");
                                if (Objects.nonNull(dataObject)) {
                                    JSONArray arr = dataObject.getJSONArray(column);
                                    if (Objects.nonNull(arr) && arr.size() != 0) {
                                        Double val = getLatestNonZeroValue(arr);
                                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
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
     * 通过tag点拿数据的API，路径可能会改变
     * @param version
     * @return
     */
    protected String getUrl(String version) {
        return httpProperties.getJHUrlVersion(version) + "/jhTagValue/getNewTagValue";
    }
}