package com.cisdi.steel.module.job.a4.writer;

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
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * 料角煤
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class LiaojiaomeiDayWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        return getMapHandler1(getUrl(), 1, excelDTO);
    }

    protected List<CellData> mapDataHandler(String url, List<String> columns, DateQuery dateQuery, int rowBatch) {
        Map<String, String> queryParam = this.getQueryParam(dateQuery);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return null;
        }
        int startRow = 1;
        return handlerJsonArray(columns, rowBatch, data, startRow);
    }

    protected Workbook getMapHandler1(String url, Integer rowBatch, WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                List<CellData> cellDataList = mapDataHandler(url, columns, date, rowBatch);
                ExcelWriterUtil.setCellValue(sheet, cellDataList);
            }
        }
        return workbook;
    }

    protected List<CellData> handlerJsonArray(List<String> columns, int rowBatch, JSONObject data, int startRow) {
        List<CellData> cellDataList = new ArrayList<>();

        String shiftDay = data.getString("shiftDay");
        cellDataList.add(new CellData(1, 25, shiftDay));

        JSONArray report8Entitys = data.getJSONArray("report8Entitys");
        int size = report8Entitys.size();
        for (int i = 0; i < size; i++) {
            JSONObject map = report8Entitys.getJSONObject(i);
            if (Objects.nonNull(map)) {
                List<CellData> cellDataList1 = ExcelWriterUtil.handlerRowData(columns, startRow, map);
                cellDataList.addAll(cellDataList1);
            }
            startRow += rowBatch;
        }
        return cellDataList;
    }

    @Override
    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("shiftday", dateQuery.getRecordDate().getTime() + "");
        return result;
    }

    private String getUrl() {
        return httpProperties.getUrlApiYGLOne() + "/reportManager/getReport8Data";
    }

}
