package com.cisdi.steel.module.job.a3.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 烧结无纸办公通用执行类
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ShaojieWuzhibangongWriter extends AbstractExcelReadWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        return this.getMapHandler2(excelDTO);
    }

    /**
     * 同样处理 方式
     *
     * @param excelDTO 数据
     * @return 结果
     */
    public Workbook getMapHandler2(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            String version = "5.0";
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                //获取版本
                String main1 = sheetSplit[1];
                if (main1.startsWith("6")) {
                    version = "6.0";
                }
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                for (DateQuery dateQuery : dateQueries) {
                    //请求参数
                    JSONObject query = new JSONObject();
                    String url = getUrl(version);
                    int rowBatch = 1;
                    int flag = 1;
                    //获取对应的请求接口地址
                    //工作流水账
                    if (JobEnum.sj_gongzuoliushuizhang.getCode().equals(excelDTO.getJobEnum().getCode())
                            || JobEnum.sj_gongzuoliushuizhang6.getCode().equals(excelDTO.getJobEnum().getCode())) {
                        url = getUrl(version);
                        //雨季生产作业
                    } else if (JobEnum.sj_yujizuoyequ.getCode().equals(excelDTO.getJobEnum().getCode())
                            || JobEnum.sj_yujizuoyequ6.getCode().equals(excelDTO.getJobEnum().getCode())) {
                        url = getUrl1(version);
                        //烧结混合机加水蒸汽预热温度统计表
                    } else if (JobEnum.sj_hunhejiashuizhengqi5_month.getCode().equals(excelDTO.getJobEnum().getCode())
                            || JobEnum.sj_hunhejiashuizhengqi6_month.getCode().equals(excelDTO.getJobEnum().getCode())) {
                        url = getUrl2(version);
                    } else if (JobEnum.sj_huanliaoqingkuang5_month.getCode().equals(excelDTO.getJobEnum().getCode())
                            || JobEnum.sj_huanliaoqingkuang6_month.getCode().equals(excelDTO.getJobEnum().getCode())) {
                        url = getUrl3(version);
                    } else if (JobEnum.sj_wuzhituoliu_month.getCode().equals(excelDTO.getJobEnum().getCode())) {
                        url = getUrl4(version);
                        rowBatch = 3;
                        flag = 2;
                    } else if (JobEnum.sj_gyijiancha_month.getCode().equals(excelDTO.getJobEnum().getCode())) {
                        url = getUrl5(version);
                        rowBatch = 7;
                        flag = 3;
                    }
                    List<JSONObject> clauses = new ArrayList<>();
                    dealClauses(clauses, "recordDate", ">=", DateUtil.getFormatDateTime(dateQuery.getStartTime(), DateUtil.fullFormat));
                    dealClauses(clauses, "recordDate", "<=", DateUtil.getFormatDateTime(dateQuery.getEndTime(), DateUtil.fullFormat));

                    query.put("clauses", clauses);

                    JSONObject sortMap = new JSONObject();
                    sortMap.put("recordDate", "desc");

                    query.put("sortMap", sortMap);

                    List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                    List<CellData> cellDataList = mapDataHandler(url, query, columns, date.getRecordDate(), rowBatch, flag);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }
        return workbook;
    }

    private void dealClauses(List<JSONObject> clauses, String column, String operation, String value) {
        JSONObject clause = new JSONObject();
        clause.put("column", column);
        clause.put("operation", operation);
        clause.put("value", value);
        clauses.add(clause);
    }

    private List<CellData> mapDataHandler(String url, JSONObject query, List<String> columns, Date date, int rowBatch, int flag) {

        List<CellData> cellDataList = new ArrayList<>();
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String result = httpUtil.postJsonParams(url, jsonString);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                if (flag == 2) {
                    dealByTime(jsonObject, date, columns, cellDataList, rowBatch);
                } else if (flag == 1) {
                    dealNoTime(jsonObject, columns, cellDataList);
                } else if (flag == 3) {
                    dealNoTime2(jsonObject, columns, cellDataList, rowBatch);
                }
            }
        }

        return cellDataList;
    }


    private void dealNoTime(JSONObject jsonObject, List<String> columns, List<CellData> cellDataList) {
        JSONArray rows = jsonObject.getJSONArray("rows");
        if (Objects.nonNull(rows) && rows.size() > 0) {
            for (int i = 0; i < rows.size(); i++) {
                JSONObject jsonObject1 = rows.getJSONObject(i);
                List<CellData> cellData = ExcelWriterUtil.handlerRowData(columns, (i + 1), jsonObject1);
                cellDataList.addAll(cellData);
            }
        }
    }


    private void dealNoTime2(JSONObject jsonObject, List<String> columns, List<CellData> cellDataList, int rowBatch) {
        JSONObject pageInfo = jsonObject.getJSONObject("pageInfo");
        if (Objects.nonNull(pageInfo)) {
            JSONArray list = pageInfo.getJSONArray("list");
            if (Objects.nonNull(list) && list.size() > 0) {
                int rowIndex = 1;
                for (int i = 0; i < list.size(); i++) {
                    JSONObject jsonObject1 = list.getJSONObject(i);
                    JSONObject ploProcessCheck = jsonObject1.getJSONObject("ploProcessCheck");
                    List<CellData> cellData = ExcelWriterUtil.handlerRowData(columns, rowIndex, ploProcessCheck);
                    cellDataList.addAll(cellData);

                    JSONArray items = jsonObject1.getJSONArray("items");
                    if (Objects.nonNull(items) && items.size() > 0) {
                        int chRowIndex = rowIndex;
                        for (int j = 0; j < items.size(); j++) {
                            JSONObject jsonObject2 = items.getJSONObject(j);
                            List<CellData> cellData2 = ExcelWriterUtil.handlerRowData(columns, chRowIndex++, jsonObject2);
                            cellDataList.addAll(cellData2);
                        }
                    }
                    rowIndex += rowBatch;
                }
            }
        }
    }

    private void dealByTime(JSONObject jsonObject, Date date, List<String> columns, List<CellData> cellDataList, int rowbatch) {
        JSONArray rows = jsonObject.getJSONArray("rows");
        if (Objects.nonNull(rows) && rows.size() > 0) {
            List<DateQuery> monthDayEach = DateQueryUtil.buildMonthDayEach(date);
            int rowIndex = 1;
            for (int j = 0; j < monthDayEach.size(); j++) {
                DateQuery dateQuery = monthDayEach.get(j);
                int r = rowIndex;
                for (int i = 0; i < rows.size(); i++) {
                    JSONObject jsonObject1 = rows.getJSONObject(i);
                    if (Objects.nonNull(jsonObject1)) {
                        String recordDate = jsonObject1.getString("recordDate");
                        Date strToDate = DateUtil.strToDate(recordDate, DateUtil.fullFormat);
                        String formatDateTime = DateUtil.getFormatDateTime(strToDate, "yyyy-MM-dd");
                        if (formatDateTime.equals(DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy-MM-dd"))) {
                            for (int m = 0; m < columns.size(); m++) {
                                String s = columns.get(m);
                                if (StringUtils.isNotBlank(s)) {
                                    Object o = jsonObject1.get(s);
                                    ExcelWriterUtil.addCellData(cellDataList, r, m, o);
                                }
                            }
                            r++;
                        }
                    }
                }
                rowIndex += rowbatch;
            }
        }
    }


    /**
     * 不同的版本获取不同的接口地址
     *
     * @param version 版本号
     * @return 结果
     */
    private String getUrl(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/ploWorkNote/select";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/ploWorkNote/select";
        }
    }

    private String getUrl1(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/ploRainyProduce/select";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/ploRainyProduce/select";
        }
    }

    private String getUrl2(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/ploSteamTemp/select";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/ploSteamTemp/select";
        }
    }

    private String getUrl3(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/ploRetardMaterial/select";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/ploRetardMaterial/select";
        }
    }

    //无纸化-脱硫运行记录
    private String getUrl4(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/ploDesulfuration/select";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/ploDesulfuration/select";
        }
    }    //无纸化-烧结机生产工艺检查项目表

    private String getUrl5(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/ploProcessCheck/selectPage";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/ploProcessCheck/selectPage";
        }
    }
}
