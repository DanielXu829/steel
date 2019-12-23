package com.cisdi.steel.module.job.sj.writer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@SuppressWarnings("ALL")
@Slf4j
public class ShaoJieShengChanWriter extends AbstractExcelReadWriter {

    @Autowired
    private TargetManagementMapper targetManagementMapper;

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());

        String version = "4.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch(Exception e){
            log.error("在模板中获取version失败", e);
        }

        return getMapHandler1(excelDTO, version);
    }

    private Workbook getMapHandler1(WriterExcelDTO excelDTO, String version) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表 待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                // 根据别名获取tag点名
                for (int j = 0; j < columns.size(); j++) {
                    if (columns.get(j).startsWith("ZP")) {
                        String tagName = targetManagementMapper.selectTargetFormulaByTargetName(columns.get(j));
                        if (StringUtils.isBlank(tagName)) {
                            columns.set(j, "");
                        } else {
                            columns.set(j, tagName);
                        }
                    }
                }

                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                for (int k = 0; k < dateQueries.size(); k++) {
                    DateQuery dateQuery = dateQueries.get(k);
                    if (dateQuery.getRecordDate().before(new Date())) {
                        List<CellData> cellDataList = this.mapDataHandler(getUrl(version), columns, dateQuery);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    } else {
                        break;
                    }
                }
            }
        }

        return workbook;
    }

    private List<CellData> mapDataHandler(String url, List<String> columns, DateQuery dateQuery) {
        JSONObject query = new JSONObject();
        Date date = new Date();
        if (dateQuery.getQueryEndTime() > date.getTime()) {
            query.put("end", date.getTime());
        } else {
            query.put("end", dateQuery.getQueryEndTime());
        }

        query.put("start", dateQuery.getQueryStartTime());
        query.put("tagNames", columns);
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String result = httpUtil.postJsonParams(url, jsonString);

        if (StringUtils.isBlank(result)) {
            return null;
        }

        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return null;
        }

        return handlerData(columns, data, dateQuery);
    }

    private List<CellData> handlerData(List<String> columns, JSONObject jsonObject, DateQuery dateQuery) {
        List<CellData> cellDataList = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            String column = columns.get(columnIndex);
            if (StringUtils.isNotBlank(column)) {
                JSONObject data = jsonObject.getJSONObject(column);
                if (Objects.nonNull(data)) {
                    Set<String> keys = data.keySet();
                    Long[] list = new Long[keys.size()];
                    int k = 0;
                    for (String key : keys) {
                        list[k] = Long.valueOf(key);
                        k++;
                    }
                    // 按照顺序排序
                    Arrays.sort(list);
                    List<DateQuery> dayEach = DateQueryUtil.buildDay12HourEach(new Date());
                    int rowIndex = 1;
                    for (int j = 0; j < dayEach.size(); j++) {
                        DateQuery query = dayEach.get(j);
                        Date recordDate = query.getEndTime();
                        for (int i = 0; i < list.length; i++) {
                            Long tempTime = list[i];
                            String formatDateTime = DateUtil.getFormatDateTime(new Date(tempTime), "yyyy-MM-dd HH:00:00");
                            Date date = DateUtil.strToDate(formatDateTime, DateUtil.fullFormat);
                            if (date.getTime() == recordDate.getTime()) {
                                Object o = data.get(tempTime + "");
                                ExcelWriterUtil.addCellData(cellDataList, rowIndex, columnIndex, o);
                                break;
                            }
                        }
                        rowIndex += 1;
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
    private String getUrl(String version) {
        return httpProperties.getSJUrlVersion(version) + "/tagValues/tagNames";
    }
}
