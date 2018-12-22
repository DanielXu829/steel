package com.cisdi.steel.module.job.strategy.api;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.ExcelCellColorUtil;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 能介接口
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class AcsStrategy extends AbstractApiStrategy {

    private Map<String, String> maps;

    {
        maps = new HashMap<>();
        // 黄色标记部分
        maps.put("ffff00", "/AcsCurTagValues");
        // 红色
        maps.put("ff0000", "/AcsACMMonthRuntimeTagValues");
        //
    }

    /**
     * 默认访问路径
     */
    private static final String DEFAULT_URL = "/AcsTagValues";

    @Override
    public String getKey() {
        return "acsReport";
    }

    @Override
    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        List<Cell> columnCells = PoiCustomUtil.getFirstRowCel(sheet);

        Map<String, List<Cell>> listMap = ExcelCellColorUtil.groupByCell(columnCells, maps, DEFAULT_URL);
        final List<CellData> cellDataList = new ArrayList<>();
        listMap.forEach((k, v) -> {
            String url = httpProperties.getUrlApiNJOne() + k;
            DateQuery query = queryList.get(0);
            if (Objects.nonNull(query)) {
                cellDataList.addAll(eachData(v, url, query.getQueryParam(), query));
            }
        });
        return SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(cellDataList)
                .build();
    }

    /**
     * 遍历每个小时的值
     *
     * @param cellList   列名
     * @param url        发送请求的地址
     * @param queryParam 查询参数
     * @return 结果
     */
    private List<CellData> eachData(List<Cell> cellList, String url, Map<String, String> queryParam, DateQuery dateQuery) {
        List<CellData> results = new ArrayList<>();
        for (Cell cell : cellList) {
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
                            int rowIndex = cell.getRowIndex();
                            List<DateQuery> dateQueries = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
                            for (DateQuery date : dateQueries) {
                                Object val = "";
                                for (int index = 0; index < size; index++) {
                                    JSONObject obj = jsonArray.getJSONObject(index);
                                    Long timestamp = (Long) obj.get("timestamp");
                                    if (date.getStartTime().getTime() == timestamp.longValue()) {
                                        val = obj.get("val");
                                        break;
                                    }
                                }
                                ExcelWriterUtil.addCellData(results, ++rowIndex, cell.getColumnIndex(), val);
                            }


//                            for (int index = 0; index < size; index++) {
//                                JSONObject obj = jsonArray.getJSONObject(index);
//                                Object val = obj.get("val");
//                                ExcelWriterUtil.addCellData(results, ++rowIndex, cell.getColumnIndex(), val);
//                            }
                        }
                    } else {
                        ExcelWriterUtil.addCellData(results, 1, cell.getColumnIndex(), data);
                    }

                }
            }
        }
        return results;
    }
}
