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

import java.util.*;

/**
 * 三、四柜区运行记录表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ThreeFourKongWriter extends AbstractExcelReadWriter {
    private Map<String, String> maps;

    {
        maps = new HashMap<>();
        // 黄色标记部分
//        maps.put("ffff00", "/AcsCurTagValues");
        // 红色
//        maps.put("ff0000", "/AreaCurrentTagValues");
        maps.put("ff0000", "/AreaStrtstpTimeTagValues");
        //
    }

    /**
     * 默认访问路径
     */
    private static final String DEFAULT_URL = "/AreaTagValues";

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
                Map<String, List<Cell>> stringListMap=new HashMap<>();
                if("_Area3_day_hour".equals(sheetName)||"_Area4_day_hour".equals(sheetName)){
                    stringListMap.put("/AreaTagValues",columns);
                }else{
                    stringListMap.put("/AreaStrtstpTimeTagValues",columns);
                }
//                Map<String, List<Cell>> stringListMap = ExcelCellColorUtil.groupByCell(columns, maps, DEFAULT_URL);
                int index = 1;
                for (DateQuery item : dateQueries) {
                    System.out.println(item);
                    List<CellData> cellDataList = this.eachData(stringListMap, item.getQueryParam(), index,sheetName);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    if("_Area34_day_shift".equals(sheetName)){
                        index += 4;
                    }else{
                        index += 1;
                    }
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
    private List<CellData> eachData(Map<String, List<Cell>> listMap, Map<String, String> queryParam, int indexs,String sheetName) {
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
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (Objects.nonNull(jsonArray)) {
                            int size = jsonArray.size();
                            for (int index = 0; index < size; index++) {
                                JSONObject obj = jsonArray.getJSONObject(index);
                                if("_Area34_day_shift".equals(sheetName)){
                                    Object val = obj.get("timestamp");
                                    ExcelWriterUtil.addCellData(results, indexs, cell.getColumnIndex(), val);
                                }else{
                                    Object val = obj.get("val");
                                    ExcelWriterUtil.addCellData(results, indexs, cell.getColumnIndex(), val);
                                }
                            }
                        }
                    }
                }
            });
        });
        return results;
    }

    private String getUrl() {
        return httpProperties.getUrlApiNJOne() + "/AreaTagValues";
    }

}
