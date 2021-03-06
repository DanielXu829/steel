package com.cisdi.steel.module.job.jh.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.common.util.math.NumberArithmeticUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.entity.TargetManagement;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import com.cisdi.steel.module.report.mapper.TargetManagementOldMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>Description: 干熄焦执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/05 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class GxjShengChanWriter extends AbstractExcelReadWriter {

    @Autowired
    private TargetManagementMapper targetManagementMapper;

    @Autowired
    private TargetManagementOldMapper targetManagementOldMapper;

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        // 调用父类AbstractExcelReadWriter 的方法，获得workbook对象
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        // 调用父类AbstractExcelReadWriter 的方法， 获得DateQuery对象
        DateQuery date = this.getDateQuery(excelDTO);
        // 获取sheet的数量
        int numberOfSheets = workbook.getNumberOfSheets();
        // 从模板中获取version
        String version ="910.0";
        try{
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        }catch(Exception e){
            log.error("在模板中获取version失败", e);
        }
        // 循环所有sheet，操作以下划线开头，并且以下划线分割为4个字符串的sheet
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表 待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 调用父类AbstractExcelReadWriter 的方法， 获取的对应的时间策略。
                // 有需求，可以自己组装dateQueries
                //List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                //List<DateQuery>dateQueries=this.getHandlerData(sheetSplit,date.getRecordDate());
                List<DateQuery> dateQueries = DateQueryUtil.buildDay2HourFromYesEighteen(date.getRecordDate());
                // 拿到tag点别名
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                // 拼装cellDataList，是直接调用，或者是重写父类AbstractExcelReadWriter 的 mapDataHandler 方法
                // 主要是取决于获取数据的API所需要的参数
                int size = dateQueries.size();
                for (int j = 0; j < size; j++) {
                    DateQuery item = dateQueries.get(j);
                    if (item.getRecordDate().before(new Date())) {
                        int rowIndex = j + 1;
                        List<CellData> cellDataList = this.mapDataHandler(rowIndex, getUrl(version), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    } else {
                        continue;
                    }
                }
            }
            if (i == 0) {
                List<DateQuery> dateQueries = DateQueryUtil.buildDay2HourFromYesEighteen(date.getRecordDate());
                List<CellData> cellDataList = this.handleWgShiftTeams(sheet, getWgShiftTeamUrl(version), dateQueries);
                ExcelWriterUtil.setCellValue(sheet, cellDataList);
                // 清除去占位符
                PoiCustomUtil.clearPlaceHolder(sheet);
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
    protected List<CellData> mapDataHandler(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery) {
        // 调用父类AbstractExcelReadWriter 的方法，获得queryParam，有需求，可以直接重写
        //Map<String, String> queryParam = this.getQueryParam(dateQuery);
        Map<String, String> queryParam = new HashMap<String, String>();
        queryParam.put("startDate", String.valueOf(dateQuery.getQueryStartTime()));
        queryParam.put("endDate", String.valueOf(dateQuery.getQueryEndTime()));
        List<CellData> cellDataList = new ArrayList<>();
        if (Objects.nonNull(columns)) {
            int size = columns.size();
            for (int i = 0; i < size; i++) {
                String column = columns.get(i);
                if (StringUtils.isNotBlank(column)) {
                    // 获取别名对应的tag点
                    TargetManagement targetManagement = targetManagementMapper.selectTargetByTargetName(column);
                    if (targetManagement == null) {
                        targetManagement = targetManagementOldMapper.selectTargetByTargetName(column);
                    }
                    column = targetManagement.getTargetFormula();
                    Integer scale = targetManagement.getScale();
                    // 可能是处理方法加tag点的组合。 e.g: max,tag1,tag2 需要根据最前面的方式做特殊处理
                    String[] columnSplit = column.split(",");
                    if (Objects.nonNull(columnSplit) && columnSplit.length > 2) {
                        List<Double> specialValues = new ArrayList<Double>();
                        // 获取处理方式
                        String executeWay = columnSplit[0];
                        int columnSplitSize = columnSplit.length;
                        for (int k = 1; k < columnSplitSize; k++) {
                            //queryParam.put("tagname", columnSplit[k]);
                            queryParam.put("tagNames", columnSplit[k]);
                            String result = httpUtil.get(url, queryParam);
                            if (StringUtils.isNotBlank(result)) {
                                JSONObject jsonObject = JSONObject.parseObject(result);
                                if (Objects.nonNull(jsonObject)) {
                                    JSONObject dataObject = jsonObject.getJSONObject("data");
                                    if (Objects.nonNull(dataObject)) {
                                        JSONArray arr = dataObject.getJSONArray(columnSplit[k]);
                                        if (Objects.nonNull(arr) && arr.size() != 0) {
                                            Double val = getLatestNonZeroValue(arr);
                                            // 处理小数位
                                            if (scale != null) {
                                                val = NumberArithmeticUtils.roundingX(val, scale);
                                            }
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
                        queryParam.put("tagNames", column);
                        String result = httpUtil.get(url, queryParam);
                        if (StringUtils.isNotBlank(result)) {
                            JSONObject jsonObject = JSONObject.parseObject(result);
                            if (Objects.nonNull(jsonObject)) {
                                JSONObject dataObject = jsonObject.getJSONObject("data");
                                if (Objects.nonNull(dataObject)) {
                                    JSONArray arr = dataObject.getJSONArray(column);
                                    if (Objects.nonNull(arr) && arr.size() != 0) {
                                        Double val = getLatestNonZeroValue(arr);
                                        // 处理小数位
                                        if (scale != null) {
                                            val = NumberArithmeticUtils.roundingX(val, scale);
                                        }
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
     * 处理班组
     * @param sheet
     * @param url
     * @param dateQueries
     * @return
     */
    protected List<CellData> handleWgShiftTeams(Sheet sheet, String url, List<DateQuery> dateQueries) {
        List<CellData> cellDataList = new ArrayList<>();
        Map<String, String> teamCodeToNameMap = new HashMap<String, String>() {
            {
                put("A", "甲");
                put("B", "乙");
                put("C", "丙");
                put("D", "丁");
            }
        };
        this.handleWgShiftTeam(cellDataList, sheet, url, dateQueries.get(0).getQueryEndTime(), "{班组1}", teamCodeToNameMap);
        this.handleWgShiftTeam(cellDataList, sheet, url, dateQueries.get(dateQueries.size() - 1).getQueryStartTime(), "{班组2}", teamCodeToNameMap);
        return cellDataList;
    }

    protected void handleWgShiftTeam(List<CellData> cellDataList, Sheet sheet, String url, Long time, String teamPlaceHolder, Map<String, String> teamCodeToNameMap) {
        try {
            Cell classGroupOne = PoiCustomUtil.getCellByValue(sheet, teamPlaceHolder);
            if (Objects.isNull(classGroupOne)) {
                log.error(String.format("模板中%s占位符不存在", teamPlaceHolder));
                return;
            }
            Map<String, String> queryParam = new HashMap<String, String>();
            queryParam.put("workDate", String.valueOf(time));
            String classGroupOneResult = httpUtil.get(url, queryParam);
            if (StringUtils.isNotBlank(classGroupOneResult)) {
                JSONObject jsonObject = JSONObject.parseObject(classGroupOneResult);
                if (Objects.nonNull(jsonObject)) {
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    if (Objects.nonNull(dataObject)) {
                        String teamCode = dataObject.getString("team");
                        String teamName = teamCodeToNameMap.get(teamCode);
                        ExcelWriterUtil.addCellData(cellDataList, classGroupOne.getRowIndex(), classGroupOne.getColumnIndex(), teamName);
                    }
                }
            }
        } catch (Exception e) {
            log.error("处理班组出错", e);
        }
    }

    /**
     * 通过tag点拿数据的API，路径可能会改变
     * @param version
     * @return
     */
    protected String getUrl(String version) {
        return httpProperties.getJHUrlVersion(version) + "/jhTagValue/getNewTagValue";
    }

    /**
     * 获取班组的API
     * @param version
     * @return
     */
    protected String getWgShiftTeamUrl(String version) {
        return httpProperties.getJHUrlVersion(version) + "/cokeActualPerformance/selectWgShiftAndTeam";
    }

}
