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
 * 烧结能源消耗及成本统计
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ShaojieNengyuanxiaohaoWriter extends AbstractExcelReadWriter {

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
                    int rowBatch = 3;
                    //获取对应的请求接口地址
                    String url = getUrl4(version);

                    JSONObject sortMap = new JSONObject();
                    sortMap.put("recordDate", "ASC");

                    List<JSONObject> clauses = new ArrayList<>();
                    dealClauses(clauses, "recordDate", ">=", DateUtil.getFormatDateTime(dateQuery.getStartTime(), DateUtil.fullFormat));
                    dealClauses(clauses, "recordDate", "<=", DateUtil.getFormatDateTime(dateQuery.getEndTime(), DateUtil.fullFormat));

                    query.put("clauses", clauses);
                    query.put("sortMap", sortMap);

                    List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                    List<CellData> cellDataList = mapDataHandler(url, query, columns, date.getRecordDate(), rowBatch);
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

    private List<CellData> mapDataHandler(String url, JSONObject query, List<String> columns, Date date, int rowBatch) {

        List<CellData> cellDataList = new ArrayList<>();
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String result = httpUtil.postJsonParams(url, jsonString);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                dealByTime(jsonObject, date, columns, cellDataList, rowBatch);
            }
        }

        return cellDataList;
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
    //无纸化-脱硫运行记录
    private String getUrl4(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/ploDesulfuration/select";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/ploDesulfuration/select";
        }
    }


}
