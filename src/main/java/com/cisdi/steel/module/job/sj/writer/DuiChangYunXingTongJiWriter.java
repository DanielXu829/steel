package com.cisdi.steel.module.job.sj.writer;

import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.sj.YardRunInfoDTO;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@SuppressWarnings("ALL")
@Slf4j
public class DuiChangYunXingTongJiWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());

        String version = "4.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch(Exception e){
            log.error("在模板中获取version失败", e);
        }

        Date searchDate = this.getDateQueryBeforeOneDay(excelDTO).getRecordDate();
        Sheet sheet1 = workbook.getSheetAt(0);
        ExcelWriterUtil.replaceCurrentMonthInTitle(sheet1, 0, 0, searchDate);
        ExcelWriterUtil.replaceCurrentDateInTitle(sheet1, "%当日数%", searchDate, DateUtil.ddFormat);
        getMapHandler1(excelDTO, workbook, version);

        return workbook;
    }

    /**
     * 获取写入数据
     * @param excelDTO
     * @param workbook
     * @param version
     * @return
     */
    private void getMapHandler1(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        try {
            String sheetName = "_data";
            Sheet sheet = workbook.getSheet(sheetName);
            List<CellData> resultList = new ArrayList<>();

            Cell pushMatCountCell = PoiCustomUtil.getCellByValue(sheet, "PUSHMATCOUNT");
            int pushMatCountColIndex = pushMatCountCell.getColumnIndex();
            Cell pushMatTimeCell = PoiCustomUtil.getCellByValue(sheet, "PUSHMATTIME");
            int pushMatTimeColIndex = pushMatTimeCell.getColumnIndex();
            Cell pushMatCell = PoiCustomUtil.getCellByValue(sheet, "PUSHMAT");
            int pushMatColIndex = pushMatCell.getColumnIndex();
            Cell pullMatCountCell = PoiCustomUtil.getCellByValue(sheet, "PULLMATCOUNT");
            int pullMatCountColIndex = pullMatCountCell.getColumnIndex();
            Cell pullMatTimeCell = PoiCustomUtil.getCellByValue(sheet, "PULLMATTIME");
            int pullMatTimeColIndex = pullMatTimeCell.getColumnIndex();
            Cell pullMatCell = PoiCustomUtil.getCellByValue(sheet, "PULLMAT");
            int pullMatColIndex = pullMatCell.getColumnIndex();

            // 当天早上运行，查询截至前一天23:59:59
            Date searchDate = this.getDateQueryBeforeOneDay(excelDTO).getRecordDate();
            String searchDateStr = DateUtil.getFormatDateTime(searchDate, "yyyy-MM-dd 22:00:00");
            searchDate = DateUtil.strToDate(searchDateStr, "yyyy-MM-dd HH:mm:ss");
            Integer[] workTeams = {1, 2, 3, 4};

            for (Integer workTeam : workTeams) {
                String result = getDuiChangeYunXingTongJi(searchDate.getTime(), workTeam, version);
                if (StringUtils.isNotBlank(result)) {
                    YardRunInfoDTO yardRunInfoDTO = JSON.parseObject(result, YardRunInfoDTO.class);
                    if (yardRunInfoDTO == null || yardRunInfoDTO.getData() == null) {
                        return;
                    }
                    ExcelWriterUtil.addCellData(resultList, workTeam, pushMatCountColIndex, yardRunInfoDTO.getData().getPushMatCount());
                    ExcelWriterUtil.addCellData(resultList, workTeam, pushMatTimeColIndex, yardRunInfoDTO.getData().getPushMatTime());
                    ExcelWriterUtil.addCellData(resultList, workTeam, pushMatColIndex, yardRunInfoDTO.getData().getPushMat());
                    ExcelWriterUtil.addCellData(resultList, workTeam, pullMatCountColIndex, yardRunInfoDTO.getData().getPullMatCount());
                    ExcelWriterUtil.addCellData(resultList, workTeam, pullMatTimeColIndex, yardRunInfoDTO.getData().getPullMatTime());
                    ExcelWriterUtil.addCellData(resultList, workTeam, pullMatColIndex, yardRunInfoDTO.getData().getPullMat());
                }
            }

            ExcelWriterUtil.setCellValue(sheet, resultList);
        } catch (Exception e) {
            log.error("处理模板数据失败", e);
        }
    }

    /**
     * 调用api获取烧结堆场运行统计数据
     * @param timestamp
     * @param workTeam
     * @param version
     * @return
     */
    private String getDuiChangeYunXingTongJi(Long timestamp, Integer workTeam, String version) {
        Map<String, String> queryParam = new HashMap();
        queryParam.put("clock", Objects.requireNonNull(timestamp.toString()));
        queryParam.put("workTeam", Objects.requireNonNull(workTeam.toString()));
        String url = httpProperties.getSJUrlVersion(version) + "/report/yarnRunStatistics";
        return httpUtil.get(url, queryParam);
    }
}
