package com.cisdi.steel.module.job.a5.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelCellColorUtil;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 柜区风机煤压机时间统计表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GuifengjimeiyajiWriter extends AbstractExcelReadWriter {
    private Map<String, String> maps;

    {
        maps = new HashMap<>();
        // 黄色标记部分
        maps.put("ffff00", "/AreaACMShiftRuntimeTagValues");
        // 红色
        maps.put("ff0000", "/AreaACMRuntimeMonthTagValues");
    }

    /**
     * 默认访问路径
     */
    //绿色
    private static final String DEFAULT_URL = "/AreaACMStoptimeMonthTagValues";

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
                Map<String, List<Cell>> stringListMap = ExcelCellColorUtil.groupByCell(columns, maps, DEFAULT_URL);
                int index = 1;

                if ("_Area1_day_shift".equals(sheetName)) {
                    for (DateQuery item : dateQueries) {
                        Map<String, String> queryParam = this.getQueryParam2(item);
                        List<CellData> cellDataList = this.eachData(stringListMap, queryParam, index);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index++;
                    }
                } else {
                    Map<String, String> queryParam = this.getQueryParam(dateQueries.get(0));
                    List<CellData> cellDataList = this.eachData(stringListMap, queryParam, index);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }

            }
        }
        return workbook;
    }


    /**
     * 遍历每个小时的值
     *
     * @param listMap
     * @param queryParam
     * @param indexs
     * @return
     */
    private List<CellData> eachData(Map<String, List<Cell>> listMap, Map<String, String> queryParam, int indexs) {
        List<CellData> results = new ArrayList<>();

        listMap.forEach((k, v) -> {
            String url = httpProperties.getUrlApiNJOne() + k;
            v.forEach(cell -> {
                String column = PoiCellUtil.getCellValue(cell);
                if (StringUtils.isNotBlank(column)) {
                    queryParam.put("tagname", column);
                    String result = httpUtil.get(url, queryParam);
                    if (StringUtils.isNotBlank(result)) {
                        JSONObject jsonObject = JSONObject.parseObject(result);
                        Object data = jsonObject.get("data");
                        if (Objects.nonNull(data)) {
                            if (data instanceof BigDecimal) {
                                ExcelWriterUtil.addCellData(results, indexs, cell.getColumnIndex(), data);
                            } else if (data instanceof JSONArray) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                if (Objects.nonNull(jsonArray)) {
                                    int size = jsonArray.size();
                                    for (int index = 0; index < size; index++) {
                                        JSONObject obj = jsonArray.getJSONObject(index);
                                        Object val = obj.get("val");
                                        if (Objects.nonNull(val)) {
                                            ExcelWriterUtil.addCellData(results, indexs, cell.getColumnIndex(), val);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
        });
        return results;
    }

    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        map.put("time", calendar.getTime().getTime() + "");
//        map.put("time", "1541088000000");
        return map;
    }

    protected Map<String, String> getQueryParam2(DateQuery dateQuery) {
        Map<String, String> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateQuery.getStartTime());
//        calendar.set(Calendar.HOUR, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
        map.put("time", calendar.getTime().getTime() + "");
//        map.put("time", "1541088000000");
        return map;
    }

}
