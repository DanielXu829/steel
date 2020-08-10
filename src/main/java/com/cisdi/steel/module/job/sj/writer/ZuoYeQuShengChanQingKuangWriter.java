package com.cisdi.steel.module.job.sj.writer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.sj.res.AnaQualitySttcs;
import com.cisdi.steel.dto.response.sj.res.AnalysisQuality;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
        dateQuery = this.getDateQueryAheadTwoHourBeforeOneDay(excelDTO);

        // 填充质量指标sheet
        handleZhiLiangZhiBiao(workbook, version);

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

    /**
     * 质量指标统计
     * @param workbook
     * @param version
     */
    private void handleZhiLiangZhiBiao(Workbook workbook, String version) {
        try {
            final String FEO = "FeO";
            final String RO = "RO";
            final String MGO = "MgO";
            // 表头
            Sheet firstSheet = workbook.getSheetAt(0);
            String tableHeadData = getQualityIndexTableHead(version);
            if (StringUtils.isNotBlank(tableHeadData)) {
                JSONObject analysisQualityJsonObject = JSON.parseObject(tableHeadData);
                JSONArray analysisQualityArray = analysisQualityJsonObject.getJSONArray("data");
                List<AnalysisQuality> analysisQualityList = JSON.parseObject(analysisQualityArray.toJSONString(), new TypeReference<List<AnalysisQuality>>() {});
                for (AnalysisQuality item : analysisQualityList) {
                    String itemOs = item.getItemOs();
                    if (RO.equals(itemOs)) {
                        String content1 = itemOs + "±" + item.getRange();
                        String content2 = itemOs + "±" + item.getFirstGrade();
                        Cell cellRo1 = PoiCustomUtil.getCellByValue(firstSheet, "{{RO1}}");
                        if (Objects.nonNull(cellRo1)) {
                            PoiCustomUtil.setCellValue(cellRo1, content1);
                        }
                        Cell cellRo2 = PoiCustomUtil.getCellByValue(firstSheet, "{{RO1}}");
                        if (Objects.nonNull(cellRo2)) {
                            PoiCustomUtil.setCellValue(cellRo2, content2);
                        }
                    } else {
                        String unit = item.getUnit() == null ? "%" : item.getUnit();
                        String content = itemOs + "（" + item.getCenter() + unit + "±" + item.getRange() + "）";
                        Cell cell = PoiCustomUtil.getCellByValue(firstSheet, "{{" + itemOs + "}}");
                        if (Objects.nonNull(cell)) {
                            PoiCustomUtil.setCellValue(cell, content);
                        }
                    }
                }
            }
            // 清除表头占位符
            PoiCustomUtil.clearPlaceHolder(firstSheet);

            // 表体
            Sheet qualityIndexSheet = workbook.getSheet("_zhiliangzhibiao");
            List<CellData> resultList = new ArrayList<>();
            Integer[] workTeams = {1, 2, 3, 4, null};
            String[] workTeamName = {"甲班", "乙班", "丙班", "丁班","作业区"};
            String endTime = DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy-MM-dd 22:00:00");
            Date endDate = DateUtil.strToDate(endTime, "yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < workTeams.length; i++) {
                Integer workTeam = workTeams[i];
                String qualityIndexData = getQualityIndexData(endDate.getTime(), workTeam, version);
                if (StringUtils.isNotBlank(qualityIndexData)) {
                    List<AnaQualitySttcs> anaQualitySttcsList = Optional.ofNullable(JSON.parseObject(qualityIndexData))
                            .map(e -> e.getJSONArray("data")).map(e -> JSONArray.parseObject(e.toJSONString(),
                                    new TypeReference<List<AnaQualitySttcs>>() {})).orElse(null);
                    if (CollectionUtils.isEmpty(anaQualitySttcsList)) {
                        log.warn(String.format("4烧结作业区生产情况-质量指标统计-%s无数据", workTeamName[i]));
                        continue;
                    }
                    for (AnaQualitySttcs anaQualitySttcs : anaQualitySttcsList) {
                        if (FEO.equals(anaQualitySttcs.getItem())) {
                            ExcelWriterUtil.addCellData(resultList, i + 1, 1, anaQualitySttcs.getTotal());
                            ExcelWriterUtil.addCellData(resultList, i + 1, 2, anaQualitySttcs.getUnqualified());
                            ExcelWriterUtil.addCellData(resultList, i + 1, 3, anaQualitySttcs.getQualifiedRate());
                        }
                        if (RO.equals(anaQualitySttcs.getItem())) {
                            ExcelWriterUtil.addCellData(resultList, i + 1, 4, anaQualitySttcs.getUnqualified());
                            ExcelWriterUtil.addCellData(resultList, i + 1, 5, anaQualitySttcs.getQualifiedRate());
                            ExcelWriterUtil.addCellData(resultList, i + 1, 6, anaQualitySttcs.getGradeOneQualifiedRate());
                        }
                        if (MGO.equals(anaQualitySttcs.getItem())) {
                            ExcelWriterUtil.addCellData(resultList, i + 1, 7, anaQualitySttcs.getUnqualified());
                            ExcelWriterUtil.addCellData(resultList, i + 1, 8, anaQualitySttcs.getQualifiedRate());
                        }
                    }
                }
            }

            ExcelWriterUtil.setCellValue(qualityIndexSheet, resultList);
        } catch (Exception e) {
            log.error("处理 4烧结作业区每月生产情况-质量指标统计 出错", e);
        }
    }

    private void handleShengChanQingKuang(Workbook workbook, String version) {
        try {
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
            String startTime = DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy-MM-01 00:00:00");
            Date startDate = DateUtil.strToDate(startTime, "yyyy-MM-dd HH:mm:ss");
            startDate = DateUtil.addHours(startDate, -2);
            startTime = DateUtil.getFormatDateTime(startDate, "yyyy-MM-dd HH:mm:ss");

            String endTime = DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy-MM-dd 22:00:00");
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

            // 可停机
            Cell canStopTimeCell = PoiCustomUtil.getCellByValue(sheet, "可停机");
            int canDowntimeCellColIndex = canStopTimeCell.getColumnIndex();
            String canStopTimeData = getCanStopTime(endDate.getTime(), version);
            Double canStopTimeHour = Optional.ofNullable(JSONObject.parseObject(canStopTimeData))
                    .map(e -> e.getJSONObject("data")).map(e -> e.getDouble("canStopTime")).orElse(null);
            ExcelWriterUtil.addCellData(resultList, 1, canDowntimeCellColIndex, canStopTimeHour);

            ExcelWriterUtil.setCellValue(sheet, resultList);
        } catch (Exception e) {
            log.error("处理 4烧结作业区每月生产情况-生产情况 出错", e);
        }
    }

    private void handleYuanLiaoZhiBiao(Workbook workbook, String version) {
        try {
            Sheet sheet = workbook.getSheet("_yuanliaozhibiao");
            List<CellData> resultList = new ArrayList<>();

            String searchDateStr = DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy-MM-dd 22:00:00");
            Date endDate = DateUtil.strToDate(searchDateStr, "yyyy-MM-dd HH:mm:ss");
            String MATERIAL_CLASS = "materialClass";
            String MATERIAL_TYPE = "materialType";

            writeMatConsume(sheet, resultList, MATERIAL_CLASS, endDate.getTime(), "ore", version);
            writeMatConsume(sheet, resultList, MATERIAL_CLASS, endDate.getTime(), "flux", version);
            writeMatConsume(sheet, resultList, MATERIAL_CLASS, endDate.getTime(), "fuel", version);
            writeMatConsume(sheet, resultList, MATERIAL_TYPE, endDate.getTime(), "limestone", version);
            writeMatConsume(sheet, resultList, MATERIAL_TYPE, endDate.getTime(), "dolomite", version);
            writeMatConsume(sheet, resultList, MATERIAL_TYPE, endDate.getTime(), "quicklime", version);
            writeMatConsume(sheet, resultList, MATERIAL_TYPE, endDate.getTime(), "return_fine", version);

            ExcelWriterUtil.setCellValue(sheet, resultList);
        } catch (Exception e) {
            log.error("处理 4烧结作业区每月生产情况-原料指标完成情况 出错", e);
        }
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
     * 获取质量指标表头数据
     * @param timestamp
     * @param workTeam
     * @param version
     * @return
     */
    private String getQualityIndexTableHead(String version) {
        String url = httpProperties.getSJUrlVersion(version) + "/report/qualitySttcs/tableHead";
        return httpUtil.get(url);
    }

    /**
     * 获取质量指标表体数据
     * @param timestamp
     * @param workTeam
     * @param version
     * @return
     */
    private String getQualityIndexData(Long timestamp, Integer workTeam, String version) {
        Map<String, String> queryParam = new HashMap();
        queryParam.put("clock", Objects.requireNonNull(timestamp.toString()));
        if (Objects.nonNull(workTeam)) {
            queryParam.put("workTeam", Objects.requireNonNull(workTeam.toString()));
        }
        String url = httpProperties.getSJUrlVersion(version) + "/report/qualitySttcs/clock";
        return httpUtil.get(url, queryParam);
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
     * 获取生产情况的可停机时间
     * @param timestamp
     * @param version
     * @return
     */
    private String getCanStopTime(Long timestamp, String version) {
        String url = httpProperties.getSJUrlVersion(version) + "/report/canStopTime";
        Map<String, String> queryParam = new HashMap();
        queryParam.put("clock", Objects.requireNonNull(timestamp.toString()));
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
