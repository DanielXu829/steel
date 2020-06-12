package com.cisdi.steel.module.job.sj.writer;

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
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@SuppressWarnings("ALL")
@Slf4j
public class ZuoYeQuShengChanQingKuangWriter extends AbstractExcelReadWriter {
    private DateQuery dateQuery;

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());

        String version = "4.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch(Exception e){
            log.error("在模板中获取version失败", e);
        }

        try {
            getMapHandler1(excelDTO, workbook, version);
        } catch (Exception e) {
            log.error("处理模板数据失败", e);
        }

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
        dateQuery = this.getDateQueryBeforeOneDay(excelDTO);

        // 填充质量指标sheet
        // handleZhiLiangZhiBiao(workbook, version);

        // 填充生产情况sheet
        handleShengChanQingKuang(workbook, version);

        // 填充原料指标sheet
        handleYuanLiaoZhiBiao(workbook, version);

        // 设置报表标题
        Sheet sheet0 = workbook.getSheetAt(0);
        Date currentDate = dateQuery.getRecordDate();
        ExcelWriterUtil.replaceCurrentMonthInTitle(sheet0, 0, 0, currentDate);
        ExcelWriterUtil.replaceCurrentMonthInTitle(sheet0, 11, 0, currentDate);
        ExcelWriterUtil.replaceCurrentDateInTitle(sheet0, "%当日数%", currentDate, DateUtil.ddFormat, 3);
    }

    private void handleZhiLiangZhiBiao(Workbook workbook, String version) {
        Sheet sheet = workbook.getSheet("_zhiliangzhibiao");
        List<CellData> resultList = new ArrayList<>();

        ExcelWriterUtil.setCellValue(sheet, resultList);
    }

    private void handleShengChanQingKuang(Workbook workbook, String version) {
        Sheet sheet = workbook.getSheet("_shengchanqingkuang");
        List<CellData> resultList = new ArrayList<>();

        Cell planOutputCell = PoiCustomUtil.getCellByValue(sheet, "PLAN_OUTPUT");
        int planOutputColIndex = planOutputCell.getColumnIndex();
        Cell actOutputCell = PoiCustomUtil.getCellByValue(sheet, "ACT_OUTPUT");
        int actOutputColIndex = actOutputCell.getColumnIndex();
        Cell sinterDayConfirmYCell = PoiCustomUtil.getCellByValue(sheet, "ST4_MESR_SIN_SinterDayConfirmY_1d_cur");
        int sinterDayConfirmYColIndex = sinterDayConfirmYCell.getColumnIndex();
        Cell productPerHourAvgCell = PoiCustomUtil.getCellByValue(sheet, "ST4_L1R_SIN_ProductPerHour_1d");
        int productPerHourAvgColIndex = productPerHourAvgCell.getColumnIndex();
        Cell productRatioCell = PoiCustomUtil.getCellByValue(sheet, "ST4_L2R_SIN_ProductRatio_1d_cur");
        int productRatioColIndex = productRatioCell.getColumnIndex();
        Cell productPerHourCell = PoiCustomUtil.getCellByValue(sheet, "ST4_L1R_SIN_ProductPerHour_1d_avg");
        int productPerHourColIndex = productPerHourCell.getColumnIndex();

        // yyyy/MM/dd HH:mm:ss
        String startTime = DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy-MM-01 00:00:00");
        String endTime = DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy-MM-dd 23:59:59");
        Date endDate = DateUtil.strToDate(endTime, "yyyy-MM-dd HH:mm:ss");
        Integer[] workTeams = {1, 2, 3, 4};
        for (Integer workTeam : workTeams) {
            String productInfo = getProductInfo(endDate.getTime(), workTeam, version);
            if (StringUtils.isNotBlank(productInfo)) {
                JSONObject productInfoObj = JSONObject.parseObject(productInfo);
                JSONObject productInfoData = productInfoObj.getJSONObject("data");
                Float planOutput = productInfoData.getFloat("PLAN_OUTPUT");
                Float actOutput = productInfoData.getFloat("ACT_OUTPUT");
                ExcelWriterUtil.addCellData(resultList, workTeam, actOutputColIndex, actOutput);
                ExcelWriterUtil.addCellData(resultList, workTeam, planOutputColIndex, planOutput);
            }
        }

        String[] tags1 = {"ST4_MESR_SIN_SinterDayConfirmY_1d_cur", "ST4_L1R_SIN_ProductPerHour_1d_avg", "ST4_L2R_SIN_ProductRatio_1d_cur"};
        String tagValueResults = getTagValueAction(startTime, endTime, tags1, version);
        if (StringUtils.isNotBlank(tagValueResults)) {
            JSONObject tagValueResultsObj = JSONObject.parseObject(tagValueResults);
            JSONObject tagValueResultsData = tagValueResultsObj.getJSONObject("data");
            Float sinterDayConfirmY = tagValueResultsData.getFloat(tags1[0]);
            Float productPerHour = tagValueResultsData.getFloat(tags1[1]);
            Float productRatio = tagValueResultsData.getFloat(tags1[2])/100;
            ExcelWriterUtil.addCellData(resultList, 1, sinterDayConfirmYColIndex, sinterDayConfirmY);
            ExcelWriterUtil.addCellData(resultList, 1, productPerHourAvgColIndex, productPerHour);
            ExcelWriterUtil.addCellData(resultList, 1, productRatioColIndex, productRatio);
        }

        String[] tags2 = {"ST4_L1R_SIN_ProductPerHour_1d_avg"};
        String productPerHourOfWorkTeamResults = getProductPerHourOfWorkTeam(startTime, endTime, tags2, version);
        if (StringUtils.isNotBlank(productPerHourOfWorkTeamResults)) {
            JSONObject productPerHourOfWorkTeamResultsObj = JSONObject.parseObject(productPerHourOfWorkTeamResults);
            JSONObject productPerHourOfWorkTeamResultsData = productPerHourOfWorkTeamResultsObj.getJSONObject("data");
            for (Integer workTeam : workTeams) {
                Float productPerHour = productPerHourOfWorkTeamResultsData.getFloat(workTeam.toString());
                ExcelWriterUtil.addCellData(resultList, workTeam, productPerHourColIndex, productPerHour);
            }
        }

        ExcelWriterUtil.setCellValue(sheet, resultList);
    }

    private void handleYuanLiaoZhiBiao(Workbook workbook, String version) {
        Sheet sheet = workbook.getSheet("_yuanliaozhibiao");
        List<CellData> resultList = new ArrayList<>();

        String searchDateStr = DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy-MM-dd 23:59:59");
        Date endDate = DateUtil.strToDate(searchDateStr, "yyyy-MM-dd HH:mm:ss");
        String MATERIAL_CLASS = "materialClass";
        String MATERIAL_TYPE = "materialType";

        writeMatConsume(sheet, resultList, MATERIAL_CLASS, endDate.getTime(), "ore", version);
        writeMatConsume(sheet, resultList, MATERIAL_CLASS, endDate.getTime(), "flux", version);
        writeMatConsume(sheet, resultList, MATERIAL_CLASS, endDate.getTime(), "fuel", version);
        writeMatConsume(sheet, resultList, MATERIAL_TYPE, endDate.getTime(), "limestone", version);

        ExcelWriterUtil.setCellValue(sheet, resultList);
    }

    private void writeMatConsume(Sheet sheet, List<CellData> resultList, String category, Long timestamp, String code, String version) {
        String results = getMatConsume(category, timestamp, code, version);
        if (StringUtils.isNotBlank(results)) {
            JSONObject resultsObj = JSONObject.parseObject(results);
            JSONArray resultsData = resultsObj.getJSONArray("data");
            Cell matConsumeCell = PoiCustomUtil.getCellByValue(sheet, code);
            int columnIndex = matConsumeCell.getColumnIndex();
            for(Object obj: resultsData) {
                JSONObject item = (JSONObject) obj;
                if (item != null) {
                    Integer workTeam = item.getInteger("workTeam");
                    Float comsume = item.getFloat("comsume");
                    ExcelWriterUtil.addCellData(resultList, workTeam, columnIndex, comsume);
                }
            }
        }
    }

    /**
     * 获取生产完成情况表格中的产量-目标值，产量-已完成，产量-应完成
     * @param timestamp
     * @param workTeam
     * @param version
     * @return
     */
    private String getProductInfo(Long timestamp, Integer workTeam, String version) {
        Map<String, String> queryParam = new HashMap();
        queryParam.put("clock", Objects.requireNonNull(timestamp.toString()));
        queryParam.put("workTeam", Objects.requireNonNull(workTeam.toString()));
        String url = httpProperties.getSJUrlVersion(version) + "/report/productInfo";
        return httpUtil.get(url, queryParam);
    }

    /**
     * 获取生产完成情况表格中的每日产量，每日台时，运转率
     * @param startTime
     * @param endTime
     * @param tags
     * @param version
     * @return
     */
    private String getTagValueAction(String startTime, String endTime, String[] tags, String version) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("start", startTime);
        jsonObject.put("end", endTime);
        jsonObject.put("method", "avg");
        jsonObject.put("tagNames", tags);
        String url = httpProperties.getSJUrlVersion(version) + "/tagValueAction";
        return httpUtil.postJsonParams(url, jsonObject.toJSONString());
    }

    /**
     * 获取生产完成情况表格中的台时
     * @param startTime
     * @param endTime
     * @param tags
     * @param version
     * @return
     */
    private String getProductPerHourOfWorkTeam(String startTime, String endTime, String[] tags, String version) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("start", startTime);
        jsonObject.put("end", endTime);
        jsonObject.put("tagNames", tags);
        String url = httpProperties.getSJUrlVersion(version) + "/report/productPerHourOfWorkTeam";
        return httpUtil.postJsonParams(url, jsonObject.toJSONString());
    }

    /**
     * 获取原料指标完成情况
     * @param category
     * @param timestamp
     * @param code
     * @param version
     * @return
     */
    private String getMatConsume(String category, Long timestamp, String code, String version) {
        Map<String, String> queryParam = new HashMap();
        queryParam.put("clock", Objects.requireNonNull(timestamp.toString()));
        queryParam.put("category", Objects.requireNonNull(category));
        queryParam.put("code", Objects.requireNonNull(code));
        String url = httpProperties.getSJUrlVersion(version) + "/report/matConsume";
        return httpUtil.get(url, queryParam);
    }
}
