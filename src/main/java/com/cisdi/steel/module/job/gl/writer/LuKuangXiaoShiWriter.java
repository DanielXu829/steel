package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>Description: 小时炉况执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/13 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class LuKuangXiaoShiWriter extends AbstractExcelReadWriter {

    @Autowired
    private TargetManagementMapper targetManagementMapper;

    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        // 调用父类AbstractExcelReadWriter 的方法，获得workbook对象
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        // 调用父类AbstractExcelReadWriter 的方法， 获得DateQuery对象
        DateQuery date = this.getDateQuery(excelDTO);
        // 获取sheet的数量
        int numberOfSheets = workbook.getNumberOfSheets();
        // 从模板中获取version
        String version ="67.0";
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
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                // 拿到tag点别名
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                // 拿到别名对应的tag点
                List<String> tagColumns = targetManagementMapper.selectTargetFormulasByTargetNames(columns);
                // 拼装cellDataList，是直接调用，或者是重写父类AbstractExcelReadWriter 的 mapDataHandler 方法
                // 主要是取决于获取数据的API所需要的参数
                int size = dateQueries.size();
                for (int j = 0; j < size; j++) {
                    DateQuery item = dateQueries.get(j);
                    if (item.getRecordDate().before(new Date())) {
                        int rowIndex = j + 1;
                        List<CellData> cellDataList = this.mapDataHandler(rowIndex, getUrl(version), columns, tagColumns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    } else {
                        break;
                    }
                }
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
    protected List<CellData> mapDataHandler(Integer rowIndex, String url, List<String> columns, List<String> tagColumns, DateQuery dateQuery) {
        // 调用父类AbstractExcelReadWriter 的方法，获得queryParam，有需求，可以直接重写
        Map<String, String> queryParam = this.getQueryParam(dateQuery);
        List<CellData> cellDataList = new ArrayList<>();
        if (Objects.nonNull(columns)) {
            int size = columns.size();
            for (int i = 0; i < size; i++) {
                String column = columns.get(i);
                if (StringUtils.isNotBlank(column)) {
                    column = ExcelWriterUtil.getMatchTagName(column, tagColumns);
                    queryParam.put("tagNames", column);
                    String result = httpUtil.get(url, queryParam);
                    if (StringUtils.isNotBlank(result)) {
                        JSONObject jsonObject = JSONObject.parseObject(result);
                        if (Objects.nonNull(jsonObject)) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (Objects.nonNull(data)) {
                                JSONArray arr = data.getJSONArray(column);
                                if (Objects.nonNull(arr) && arr.size() !=0) {
                                    JSONObject jsonObject1 = arr.getJSONObject(arr.size() - 1);
                                    Double val = jsonObject1.getDouble("val");
                                    ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
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
     * 通过tag点拿数据的API，路径可能会改变
     * @param version
     * @return
     */
    protected String getUrl(String version) {
        return httpProperties.getJHUrlVersion(version) + "/glTagValue/getTagValue";
    }
}
