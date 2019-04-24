package com.cisdi.steel.module.job.a3.writer;

import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 精益生产管控系统
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JingyishengchanguankongWriter extends AbstractExcelReadWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        return this.getMapHandler1(excelDTO);
    }

    protected Workbook getMapHandler1(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = PoiCustomUtil.getSheetCell(workbook, "_dictionary", 0, 1);
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                int index = 1;
                for (DateQuery item : dateQueries) {
                    List<CellData> cellDataList = mapDataHandler(columns, item, index, version);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    index += 24;
                }
            }
        }
        return workbook;
    }

    protected List<CellData> mapDataHandler(List<String> columns, DateQuery dateQuery,
                                            int index, String version) {
        Map<String, String> queryParam = dateQuery.getQueryParam();
        String result = getTagValues(queryParam, columns, version);
        List<CellData> resultList = new ArrayList<>();
        if (StringUtils.isNotBlank(result)) {
            JSONObject obj = JSONObject.parseObject(result);
            obj = obj.getJSONObject("data");
            if (Objects.nonNull(obj)) {
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    int indexs = index;
                    String cell = columns.get(columnIndex);
                    if (StringUtils.isNotBlank(cell)) {
                        JSONObject data = obj.getJSONObject(cell);
                        List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
                        for (int i = 0; i < dayHourEach.size(); i++) {
                            Object v = "";
                            if (Objects.nonNull(data)) {
                                Map<String, Object> innerMap = data.getInnerMap();
                                Set<String> keySet = innerMap.keySet();
                                Long[] list = new Long[keySet.size()];
                                int k = 0;
                                for (String key : keySet) {
                                    list[k] = Long.valueOf(key);
                                    k++;
                                }
                                Arrays.sort(list);
                                Date startTime = dayHourEach.get(i).getStartTime();

                                for (int j = 0; j < list.length; j++) {
                                    Long tempTime = list[j];
                                    String formatDateTime = DateUtil.getFormatDateTime(new Date(tempTime), "yyyy-MM-dd HH:00:00");
                                    Date date = DateUtil.strToDate(formatDateTime, DateUtil.fullFormat);
                                    if (date.getTime() == startTime.getTime()) {
                                        v = data.get(tempTime + "");
                                        break;
                                    }
                                }
                            }
                            ExcelWriterUtil.addCellData(resultList, indexs++, columnIndex, v);
                        }
                    }
                }
            }
        }
        return resultList;
    }

    private String getTagValues(Map<String, String> param, List<String> col, String version) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("start", param.get("starttime"));
        jsonObject.put("end", param.get("endtime"));
        jsonObject.put("tagNames", col);
        String re1 = httpUtil.postJsonParams(getUrl(version), jsonObject.toJSONString());
        return re1;
    }

    /**
     * 不同的版本获取不同的接口地址
     *
     * @param version 版本号
     * @return 结果
     */
    private String getUrl(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/tagValues/tagNames";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/tagValues/tagNames";
        }
    }
}
