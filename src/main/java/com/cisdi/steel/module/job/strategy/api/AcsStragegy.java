package com.cisdi.steel.module.job.strategy.api;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class AcsStragegy extends AbstractApiStrategy {
    public AcsStragegy(HttpUtil httpUtil, HttpProperties httpProperties) {
        super(httpUtil, httpProperties);
    }

    @Override
    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        List<Cell> columnCells = PoiCustomUtil.getFirstRowCel(sheet);
        String url = httpProperties.getUrlApiNJOne() + "/AcsTagValues";
        List<CellData> cellDataList = new ArrayList<>();
        DateQuery query = queryList.get(0);
        if (Objects.nonNull(query)) {
            cellDataList = eachData(columnCells, url, query.getQueryParam());
        }
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
    private List<CellData> eachData(List<Cell> cellList, String url, Map<String, String> queryParam) {
        List<CellData> results = new ArrayList<>();
        for (Cell cell : cellList) {
            String column = PoiCellUtil.getCellValue(cell);
            if (StringUtils.isNotBlank(column)) {
                queryParam.put("tagname", column);
                String result = httpUtil.get(url, queryParam);
                if (StringUtils.isNotBlank(result)) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (Objects.nonNull(jsonArray)) {
                        int size = jsonArray.size();
                        int rowIndex = cell.getRowIndex();
                        for (int index = 0; index < size; index++) {
                            JSONObject obj = jsonArray.getJSONObject(index);
                            Object val = obj.get("val");
                            ExcelWriterUtil.addCellData(results, ++rowIndex, cell.getColumnIndex(), val);
                        }
                    }
                }
            }
        }
        return results;
    }
}
