package com.cisdi.steel.module.job.sj.writer;

import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.sj.req.SjTagQueryParam;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import com.cisdi.steel.module.report.mapper.TargetManagementOldMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.cisdi.steel.common.constant.Messages.GET_VERSION_FAILED_MESSAGE;

@Component
@SuppressWarnings("ALL")
@Slf4j
public class MeiZhouChanLiangFenXiWriter extends BaseShaoJieWriter {

    private Date dateRun; // job运行时间的前一天

    @Autowired
    private TargetManagementMapper targetManagementMapper;

    @Autowired
    private TargetManagementOldMapper targetManagementOldMapper;

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());

        String version = "4.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch(Exception e){
            log.error(GET_VERSION_FAILED_MESSAGE, e);
        }

        Sheet tagSheet = workbook.getSheet("_mzlcfx");
        Sheet mainSheet = workbook.getSheetAt(0);

        // 第二天运行前一天的数据
        dateRun = DateUtil.addDays(getDateQuery(excelDTO).getRecordDate(), -1);
        List<Date> daysOfWeek = DateUtil.getDaysOfWeek(dateRun);
        List<DateQuery> dateQueryList = new ArrayList<>();
        daysOfWeek.forEach(e -> dateQueryList.add(DateQueryUtil.buildDayAheadTwoHour(e)));

        handleTagSheet(tagSheet, version, dateQueryList);
        handleAverageDataOfLastWeek(mainSheet, version, dateQueryList);
        handleDateOfWeek(mainSheet, daysOfWeek);
        PoiCustomUtil.clearPlaceHolder(mainSheet);
        return workbook;
    }

    private void handleTagSheet(Sheet tagSheet, String version, List<DateQuery> dateQueryList) {
        // 获取本周一到本周日的日期
        try {
            List<String> columns = PoiCustomUtil.getFirstRowCelVal(tagSheet);
            for (int j = 0; j < columns.size(); j++) {
                String tagName = targetManagementMapper.selectTargetFormulaByTargetName(columns.get(j));
                if (StringUtils.isBlank(tagName)) {
                    tagName = targetManagementOldMapper.selectTargetFormulaByTargetName(columns.get(j));
                }
                if (StringUtils.isBlank(tagName)) {
                    columns.set(j, StringUtils.EMPTY);
                } else {
                    columns.set(j, tagName);
                }
            }
            List<CellData> cellDataListOfTagSheet = new ArrayList<>();
            for (int dateQueryIndex = 0, dateQuerySize = dateQueryList.size(); dateQueryIndex < dateQuerySize; dateQueryIndex++) {
                int dayOfWeekDay = DateUtil.getDayOfWeekDay(dateRun);
                if (dateQueryIndex > dayOfWeekDay - 1) {
                    break;
                }
                DateQuery dateAheadTwoHourQuery = dateQueryList.get(dateQueryIndex);
                Long queryStartTime = dateAheadTwoHourQuery.getQueryStartTime();
                Long queryEndTime = dateAheadTwoHourQuery.getQueryEndTime();
                SjTagQueryParam sjTagQueryParam = new SjTagQueryParam(queryStartTime,
                        queryEndTime, columns);
                String tagValueJsonData = getTagValue(version, sjTagQueryParam);
                JSONObject data = Optional.ofNullable(JSONObject.parseObject(tagValueJsonData))
                        .map(e -> e.getJSONObject("data")).orElse(null);
                if (Objects.isNull(data)) {
                    continue;
                }
                for (int columnIndex = 0, columnSize = columns.size(); columnIndex < columnSize; columnIndex++) {
                    String column = columns.get(columnIndex);

                    JSONObject dataOfColumn = data.getJSONObject(column);
                    if (Objects.isNull(dataOfColumn)) {
                        continue;
                    }
                    Object value = dataOfColumn.get(queryEndTime);
                    ExcelWriterUtil.addCellData(cellDataListOfTagSheet, dateQueryIndex + 1, columnIndex, value);
                }
            }
            ExcelWriterUtil.setCellValue(tagSheet, cellDataListOfTagSheet);
        } catch (Exception e) {
            log.error("处理tag点出错！", e);
        }
    }

    /**
     * 处理运转率数据
     * @param mainSheet
     * @param version
     * @param dateQueryList
     */
    private void handleYunZhuanLv(Sheet mainSheet, String version, List<DateQuery> dateQueryList) {
        // 运转率数据
        try {
            Cell yunZhuanLvCell = PoiCustomUtil.getCellByValue(mainSheet, "{运转率}");
            if (Objects.isNull(yunZhuanLvCell)) {
                log.warn("{运转率}占位符不存在！");
                return;
            }
            int yunzhuanLvRowIndex = yunZhuanLvCell.getRowIndex();
            int yunZhuanLvColumnIndex = yunZhuanLvCell.getColumnIndex();
            List<CellData> cellDataListOfMainSheet = new ArrayList<>();
            for (int dateQueryIndex = 0, dateQuerySize = dateQueryList.size(); dateQueryIndex < dateQuerySize; dateQueryIndex++) {
                int dayOfWeekDay = DateUtil.getDayOfWeekDay(dateRun);
                if (dateQueryIndex > dayOfWeekDay - 1) {
                    break;
                }
                DateQuery dateAheadTwoHourQuery = dateQueryList.get(dateQueryIndex);
                Long queryStartTime = dateAheadTwoHourQuery.getQueryStartTime();
                Long queryEndTime = dateAheadTwoHourQuery.getQueryEndTime();
                String yarnRunStatisticsForFullDayData = getYarnRunStatisticsForFullDayData(version, queryEndTime);
                if (StringUtils.isBlank(yarnRunStatisticsForFullDayData)) {
                    continue;
                }
                Double yunZhuanLvData = Optional.ofNullable(JSONObject.parseObject(yarnRunStatisticsForFullDayData))
                        .map(e -> e.getJSONObject("data")).map(e -> e.getDouble("operationRate")).orElse(null);
                if (Objects.isNull(yunZhuanLvData)) {
                    continue;
                }
                ExcelWriterUtil.addCellData(cellDataListOfMainSheet, yunzhuanLvRowIndex,
                        yunZhuanLvColumnIndex + dateQueryIndex, yunZhuanLvData);
            }
            ExcelWriterUtil.setCellValue(mainSheet, cellDataListOfMainSheet);
        } catch (Exception e) {
            log.error("处理运转率数据出错！", e);
        }
    }

    /**
     * 上周均值
     */
    private void handleAverageDataOfLastWeek(Sheet mainSheet, String version, List<DateQuery> dateQueryList) {
        // 上周均值数据 取周任意一天的22点时间戳
        try {
            String analysisOfOutPutData = getAnalysisOfOutPutData(version, dateQueryList.get(0).getQueryEndTime());
            JSONObject avgDataOfLastWeekJsonObject = Optional.ofNullable(JSONObject.parseObject(analysisOfOutPutData))
                    .map(e -> e.getJSONObject("data")).orElse(null);
            if (Objects.isNull(analysisOfOutPutData)) {
                return;
            }
            Map<String, Object> itemToValueMap = new HashMap<>();
            for (Map.Entry entry : avgDataOfLastWeekJsonObject.entrySet()) {
                Object value = entry.getValue();
                // 如果是String 就转为 Double
                if (value instanceof String) {
                    value = Double.valueOf(value.toString());
                }
                itemToValueMap.put(entry.getKey().toString(), value);
            }
            // 写入excel, {map的key} 作为excel中的占位符
            List<CellData> cellDataListOfMainSheet = new ArrayList<>();
            itemToValueMap.forEach((item, value) -> {
                Cell cell = PoiCustomUtil.getCellByValue(mainSheet, "{" + item + "}");
                if (Objects.nonNull(cell)) {
                    int rowIndex = cell.getRowIndex();
                    int columnIndex = cell.getColumnIndex();
                    ExcelWriterUtil.addCellData(cellDataListOfMainSheet, rowIndex, columnIndex, value);
                }
            });
            ExcelWriterUtil.setCellValue(mainSheet, cellDataListOfMainSheet);
        } catch (Exception e) {
            log.error("处理上周平均值出错", e);
        }
    };

    /**
     * 处理首行一周日期显示
     */
    private void handleDateOfWeek(Sheet mainSheet, List<Date> daysOfWeek) {
        try {
            Cell dateCell = PoiCustomUtil.getCellByValue(mainSheet, "{date}");
            if (Objects.isNull(dateCell)) {
                log.error("{date}占位符不存在");
                return;
            }
            int rowIndex = dateCell.getRowIndex();
            int columnIndex = dateCell.getColumnIndex();
            List<CellData> cellDataListOfMainSheet = new ArrayList<>();
            for (int dayIndex = 0, daysSize = daysOfWeek.size(); dayIndex < daysSize; dayIndex++) {
                Date date = daysOfWeek.get(dayIndex);
                String currentDay = DateFormatUtils.format(date, DateUtil.ddChineseFormat);
                ExcelWriterUtil.addCellData(cellDataListOfMainSheet, rowIndex, columnIndex + dayIndex, currentDay);
            }
            ExcelWriterUtil.setCellValue(mainSheet, cellDataListOfMainSheet);
        } catch (Exception e) {
            log.error("处理首行日期出错", e);
        }
    }

}
