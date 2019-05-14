package com.cisdi.steel.module.job.a5.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
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
 * 煤气柜作业区混合煤气情况表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class MeiqihunhemeiWriter extends AbstractExcelReadWriter {
    private Map<String, String> maps;

    {
        maps = new HashMap<>();
        // 黄色标记部分
        maps.put("ffff00", "/AreaDayTagValues");
        // 红色
        maps.put("ff0000", "/AreaMonthTagValues");
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
                if("_Area3_day_all".equals(sheetName)||"_Area4_day_all".equals(sheetName)){
                    stringListMap.put("/AreaDayTagValues",columns);
                }else{
                    stringListMap.put("/AreaMonthTagValues",columns);
                }
//                Map<String, List<Cell>> stringListMap = ExcelCellColorUtil.groupByCell(columns, maps, DEFAULT_URL);
                int index = 1;

                Map<String, String> queryParam = DateQueryUtil.getQueryParam(dateQueries.get(0), 0, 0, 10);
                List<CellData> cellDataList = this.eachData(stringListMap, queryParam, index);
                ExcelWriterUtil.setCellValue(sheet, cellDataList);
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
                            ExcelWriterUtil.addCellData(results, indexs, cell.getColumnIndex(), data);
                        }

                    }
                }
            });
        });
        return results;
    }
}
