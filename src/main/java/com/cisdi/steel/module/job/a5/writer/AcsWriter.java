package com.cisdi.steel.module.job.a5.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
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
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 运行记录
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class AcsWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
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
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<Cell> columns = PoiCustomUtil.getFirstRowCel(sheet);
                Map<String, List<Cell>> stringListMap = new HashMap<>();
                if ("_acsReport_day_each".equals(sheetName)) {
                    stringListMap.put("/AcsTagValues", columns);
                } else if ("_acsReport1_day_each".equals(sheetName)) {
                    stringListMap.put("/AcsCurTagValues", columns);
                } else {
                    stringListMap.put("/AcsACMMonthRuntimeTagValues", columns);
                }
                for (DateQuery item : dateQueries) {
                    final List<CellData> cellDataList = new ArrayList<>();
                    stringListMap.forEach((k, v) -> {
                        String url = httpProperties.getUrlApiNJOne() + k;
                        eachData(v, url, item.getQueryParam(), item, cellDataList);
                    });
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }
        return workbook;
    }

    /**
     * 遍历每个小时的值
     *
     * @param cellList   列名
     * @param url        发送请求的地址
     * @param queryParam 查询参数
     * @return 结果
     */
    private List<CellData> eachData(List<Cell> cellList, String url, Map<String, String> queryParam, DateQuery dateQuery, List<CellData> cellDataList) {
        for (Cell cell : cellList) {
            int rowIndex = cell.getRowIndex();
            String column = PoiCellUtil.getCellValue(cell);
            if (StringUtils.isNotBlank(column)) {
                queryParam.put("tagname", column);
                String result = httpUtil.get(url, queryParam);
                if (StringUtils.isNotBlank(result)) {
                    JSONObject jsonObject = JSONObject.parseObject(result);

                    Object data = jsonObject.get("data");
                    if (data instanceof JSONArray) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (Objects.nonNull(jsonArray)) {
                            int size = jsonArray.size();
                            List<DateQuery> dateQueries = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
                            for (DateQuery date : dateQueries) {
                                Object val = "";
                                for (int index = 0; index < size; index++) {
                                    JSONObject obj = jsonArray.getJSONObject(index);
                                    Long timestamp = (Long) obj.get("timestamp");
                                    Date zero = DateUtil.handleToZero(timestamp, 0, 0);
                                    if (date.getStartTime().getTime() == zero.getTime()) {
                                        val = obj.get("val");
                                        break;
                                    }
                                }
                                ExcelWriterUtil.addCellData(cellDataList, ++rowIndex, cell.getColumnIndex(), val);
                            }
                        }
                    } else {
                        ExcelWriterUtil.addCellData(cellDataList, 1, cell.getColumnIndex(), data);
                    }

                }
            }
        }
        return cellDataList;
    }
}
