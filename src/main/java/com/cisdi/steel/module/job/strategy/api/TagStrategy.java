package com.cisdi.steel.module.job.strategy.api;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.dto.CellValInfo;
import com.cisdi.steel.module.job.dto.RowCellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.date.DateQuery;
import lombok.Data;
import org.apache.commons.lang3.RandomUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 全部同一个接口 策略
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class TagStrategy extends AbstractApiStrategy {

    public TagStrategy(HttpUtil httpUtil, HttpProperties httpProperties) {
        super(httpUtil, httpProperties);
    }

    @Override
    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        List<Cell> columnCells = PoiCustomUtil.getFirstRowCel(sheet);
        String url = httpProperties.getUrlApiGLOne() + "/tagValueAction";
        List<RowCellData> rowCellDataList = new ArrayList<>();
        int size = queryList.size();
        for (int rowNum = 0; rowNum < size; rowNum++) {
            DateQuery eachDate = queryList.get(rowNum);
            // 每一行数据
            Integer rowIndex = rowNum + 1;
            RowCellData rowCellData = RowCellData.builder().rowIndex(rowIndex).build();
            List<CellValInfo> cellValInfoList = eachData(columnCells, url, eachDate.getQueryParam());
            rowCellData.setValues(cellValInfoList);
            rowCellDataList.add(rowCellData);
        }
        return SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .rowCellDataList(rowCellDataList)
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
    private List<CellValInfo> eachData(List<Cell> cellList, String url, Map<String, String> queryParam) {
        List<CellValInfo> results = new ArrayList<>();
        for (Cell cell : cellList) {
            String column = PoiCellUtil.getCellValue(cell);
            if (StringUtils.isNotBlank(column)) {
                String[] columnSplit = column.split("/");
                String method = "avg";
                String tagName = columnSplit[0];
                if (columnSplit.length > 2) {
                    method = columnSplit[1];
                }
                queryParam.put("method", method);
                queryParam.put("tagname", tagName);
                // TODO: 暂时模拟数据
//                String result = httpUtil.get(url, queryParam);
                JSONObject mock = new JSONObject();
                mock.put("data", RandomUtils.nextInt(1, 999));
                String result = mock.toJSONString();
                if (StringUtils.isNotBlank(result)) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    CellValInfo cellValInfo = CellValInfo.builder()
                            .columnIndex(cell.getColumnIndex())
                            .cellValue(jsonObject.get("data")).build();
                    results.add(cellValInfo);
                }
            }
        }
        return results;
    }
}
