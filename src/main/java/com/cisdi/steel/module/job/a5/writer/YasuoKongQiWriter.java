package com.cisdi.steel.module.job.a5.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 压缩空气生产情况汇总表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class YasuoKongQiWriter extends AbstractExcelReadWriter {
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
                //黄色部分
                if ("_acsReportGas_day_all".equals(sheetName)) {
                    // 获取的对应的策略
                    List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                    List<Cell> columns = PoiCustomUtil.getFirstRowCel(sheet);
                    List<CellData> cellDataList = this.eachData(columns, getUrl(), getQueryParam(dateQueries.get(0)));
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    /*dateQueries.forEach(item -> {
                    });*/
                    //红色部分
                } else if ("_acsReportStrtStp_day_all".equals(sheetName) || "_acsReportRuntime_day_all".equals(sheetName)) {
                    List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                    List<Cell> columns = PoiCustomUtil.getFirstRowCel(sheet);
                    List<CellData> cellDataList = this.eachData(columns, getUrl1(), getQueryParam(dateQueries.get(0)));
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    //绿色部分
                } else if ("_acsReportStrtStp_month_day".equals(sheetName)) {
                    List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                    List<Cell> columns = PoiCustomUtil.getFirstRowCel(sheet);
                    List<CellData> cellDataList = this.eachData(columns, getUrl2(), getQueryParam2(dateQueries.get(0)));
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }

            }
        }
        return workbook;
    }

    protected Map<String, String> getQueryParam2(DateQuery dateQuery) {
        Map<String, String> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        map.put("starttime", calendar.getTime().getTime() + "");
//        map.put("time", "1541088000000");
        return map;
    }

    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> map = new HashMap<>();
        map.put("createDate", String.valueOf(Objects.requireNonNull(dateQuery.getStartTime()).getTime()));
//        map.put("createDate", "1541088000000");
        return map;
    }

    /**
     * 遍历每个小时的值
     *
     * @param cellList   列名
     * @param url        发送请求的地址
     * @param queryParam 查询参数
     * @return 结果
     */
    private List<CellData> eachData(List<Cell> cellList, String url, Map<String, String> queryParam) {
        List<CellData> results = new ArrayList<>();
        for (Cell cell : cellList) {
            String column = PoiCellUtil.getCellValue(cell);
            if (StringUtils.isNotBlank(column)) {
                queryParam.put("tagname", column);
                String result = httpUtil.get(url, queryParam);
                if (StringUtils.isNotBlank(result)) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    Object data = jsonObject.get("data");
                    if (null != data) {
                        int rowIndex = cell.getRowIndex();
                        if (data instanceof BigDecimal) {
                            ExcelWriterUtil.addCellData(results, ++rowIndex, cell.getColumnIndex(), data);
                        } else {
                            JSONObject jsonObjects = jsonObject.getJSONObject("data");
                            Object val = jsonObjects.get("val");
                            ExcelWriterUtil.addCellData(results, ++rowIndex, cell.getColumnIndex(), val);
                        }
                    }
                }
            }
        }
        return results;
    }

    private String getUrl() {
        return httpProperties.getUrlApiNJOne() + "/acsReportDayGasProd";
    }

    private String getUrl1() {
        return httpProperties.getUrlApiNJOne() + "/acsDayTagValues";
    }

    private String getUrl2() {
        return httpProperties.getUrlApiNJOne() + "/acsACMTagStrtstpTimesValues";
    }

}
