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
public class ShengChanFenXiZhouBaoWriter extends BaseShaoJieWriter{
    private Date dateRun; // job运行时间的前一天

    @Autowired
    private TargetManagementMapper targetManagementMapper;

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());

        String version = "4.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch(Exception e){
            log.error(GET_VERSION_FAILED_MESSAGE, e);
        }

        Sheet tagSheet = workbook.getSheet("_scfxzb");
        Sheet mainSheet = workbook.getSheetAt(0);

        // 第二天运行前一天的数据
        dateRun = DateUtil.addDays(getDateQuery(excelDTO).getRecordDate(), -1);
        List<Date> daysOfWeek = DateUtil.getDaysOfWeek(dateRun);

        handleTagSheet(tagSheet, version, daysOfWeek);
        handleClassOfWeek(mainSheet, version, daysOfWeek);
        handleDateOfWeek(mainSheet, daysOfWeek);
        PoiCustomUtil.clearPlaceHolder(mainSheet);
        return workbook;
    }

    /**
     * 写入tag点数据
     * @param tagSheet
     * @param version
     * @param daysOfWeek
     */
    private void handleTagSheet(Sheet tagSheet, String version, List<Date> daysOfWeek) {
        try {
            List<String> columns = PoiCustomUtil.getRowCelVal(tagSheet, 1);
            List<String> targetFormulas = targetManagementMapper.selectTargetFormulasByTargetNames(columns);
            List<CellData> cellDataListOfTagSheet = new ArrayList<>();
            for (int dateIndex = 0, dateSize = daysOfWeek.size(); dateIndex < dateSize; dateIndex++) {
                int dayOfWeekDay = DateUtil.getDayOfWeekDay(dateRun);
                if (dateIndex > dayOfWeekDay - 1) {
                    break;
                }
                Date date = daysOfWeek.get(dateIndex);
                // 22-22
                DateQuery dateQueryAllDay = DateQueryUtil.buildDayAheadTwoHour(date);
                // 22-10 10-22
                List<DateQuery> dateQueries = DateQueryUtil.buildDay12HourAheadTwoHour(date);
                // 22-10
                DateQuery dateQuery = dateQueries.get(0);
                Long query10hTime = dateQuery.getQueryEndTime();
                Long query22hStartTime = dateQueryAllDay.getQueryStartTime();
                Long query22hEndTime = dateQueryAllDay.getQueryEndTime();
                SjTagQueryParam sjTagQueryParam = new SjTagQueryParam(query22hStartTime,
                        query22hEndTime, targetFormulas);
                String tagValueJsonData = getTagValue(version, sjTagQueryParam);
                JSONObject data = Optional.ofNullable(JSONObject.parseObject(tagValueJsonData))
                        .map(e -> e.getJSONObject("data")).orElse(null);
                if (Objects.isNull(data)) {
                    continue;
                }
                for (int columnIndex = 0, columnSize = targetFormulas.size(); columnIndex < columnSize; columnIndex++) {
                    String column = targetFormulas.get(columnIndex);

                    JSONObject dataOfColumn = data.getJSONObject(column);
                    if (Objects.isNull(dataOfColumn)) {
                        continue;
                    }
                    // 天的点位查询开始 时间
                    if (column.contains("_1d_")) {
                        Object valueOf22Start = dataOfColumn.get(query22hStartTime);
                        ExcelWriterUtil.addCellData(cellDataListOfTagSheet, dateIndex + 2, columnIndex, valueOf22Start);
                    } else {
                        Object valueOf10 = dataOfColumn.get(query10hTime);
                        Object valueOf22End = dataOfColumn.get(query22hEndTime);
                        ExcelWriterUtil.addCellData(cellDataListOfTagSheet, dateIndex * 2 + 2, columnIndex, valueOf10);
                        ExcelWriterUtil.addCellData(cellDataListOfTagSheet, dateIndex * 2 + 3, columnIndex, valueOf22End);
                    }
                }
            }
            ExcelWriterUtil.setCellValue(tagSheet, cellDataListOfTagSheet);
        } catch (Exception e) {
            log.error("处理tag点出错！", e);
        }
    }

    /**
     * 处理一周日期显示
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
                List<DateQuery> dateQueries = DateQueryUtil.buildDay12HourAheadTwoHour(date);
                DateQuery dateQuery = dateQueries.get(1);
                Long queryStartTime = dateQuery.getQueryStartTime();
                Long queryEndTime = dateQuery.getQueryEndTime();
                String startDate = DateFormatUtils.format(new Date(queryStartTime), DateUtil.yyyyMMddHHmm);
                String endDate = DateFormatUtils.format(new Date(queryEndTime), DateUtil.yyyyMMddHHmm);
                ExcelWriterUtil.addCellData(cellDataListOfMainSheet, 3 * dayIndex + rowIndex, columnIndex, startDate);
                ExcelWriterUtil.addCellData(cellDataListOfMainSheet, 3 * dayIndex + rowIndex + 1, columnIndex, endDate);
                ExcelWriterUtil.addCellData(cellDataListOfMainSheet, 3 * dayIndex + rowIndex + 2, columnIndex, endDate);
            }
            ExcelWriterUtil.setCellValue(mainSheet, cellDataListOfMainSheet);
        } catch (Exception e) {
            log.error("处理日期出错", e);
        }
    }

    /**
     * 处理班组显示
     * @param mainSheet
     * @param version
     * @param daysOfWeek
     */
    private void handleClassOfWeek(Sheet mainSheet, String version, List<Date> daysOfWeek) {
        try {
            Cell dateCell = PoiCustomUtil.getCellByValue(mainSheet, "{班}");
            if (Objects.isNull(dateCell)) {
                log.error("{班}占位符不存在");
                return;
            }
            int rowIndex = dateCell.getRowIndex();
            int columnIndex = dateCell.getColumnIndex();
            List<CellData> cellDataListOfMainSheet = new ArrayList<>();
            for (int dayIndex = 0, daysSize = daysOfWeek.size(); dayIndex < daysSize; dayIndex++) {
                int dayOfWeekDay = DateUtil.getDayOfWeekDay(dateRun);
                if (dayIndex > dayOfWeekDay - 1) {
                    break;
                }
                Date date = daysOfWeek.get(dayIndex);
                List<DateQuery> dateQueries = DateQueryUtil.buildDay12HourAheadTwoHour(date);
                DateQuery dateQuery = dateQueries.get(1);
                Long queryStartTime = dateQuery.getQueryStartTime();
                Long queryEndTime = dateQuery.getQueryEndTime();
                String workTeam1 = Optional.ofNullable(JSONObject.parseObject(getWorkTeam(version, queryStartTime)))
                        .map(e -> e.getString("data")).orElse(null);
                String workTeam2 = Optional.ofNullable(JSONObject.parseObject(getWorkTeam(version, queryEndTime)))
                        .map(e -> e.getString("data")).orElse(null);
                String startDate = DateFormatUtils.format(new Date(queryStartTime), DateUtil.yyyyMMddHHmm);
                String endDate = DateFormatUtils.format(new Date(queryEndTime), DateUtil.yyyyMMddHHmm);
                ExcelWriterUtil.addCellData(cellDataListOfMainSheet, 3 * dayIndex + rowIndex, columnIndex, workTeam1);
                ExcelWriterUtil.addCellData(cellDataListOfMainSheet, 3 * dayIndex + rowIndex + 1, columnIndex, workTeam2);
                ExcelWriterUtil.addCellData(cellDataListOfMainSheet, 3 * dayIndex + rowIndex + 2, columnIndex, "作业区");
            }
            ExcelWriterUtil.setCellValue(mainSheet, cellDataListOfMainSheet);
        } catch (Exception e) {
            log.error("处理班组出错", e);
        }
    }
}
