package com.cisdi.steel.module.job.strategy.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Description:   charge      </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ChargeStrategy extends AbstractApiStrategy {

    @Override
    public String getKey() {
        return "charge";
    }

    @Override
    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version);
        List<CellData> cellDataList = new ArrayList<>();
        for (DateQuery dateQuery : queryList) {
            // 理论上来说一次获取了所有，循环只执行一次
            List<Map<String, Object>> dataList = this.requestApiData(url, dateQuery);
            cellDataList.addAll(this.loopRowData(dataList, columns));
        }
        return SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(cellDataList)
                .build();
    }


    /**
     * 循环遍历数据
     * 每一个 dataList 对应 多行数据
     *
     * @param dataList 数据
     * @param columns  对应的列
     * @return 结果
     */
    private List<CellData> loopRowData(List<Map<String, Object>> dataList, List<String> columns) {
        int starRow = 1;
        List<CellData> resultData = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            // 存储每一个
            List<CellData> rowData = ExcelWriterUtil.handlerRowData(columns, starRow, data);
            resultData.addAll(rowData);
            starRow += 6;
        }
        return resultData;
    }

    /**
     * 请求获取数据
     *
     * @param dateQuery 查询时间段
     * @return 结果
     */
    private List<Map<String, Object>> requestApiData(String urlPre, DateQuery dateQuery) {
        String url = urlPre + "/batches/material/period";
        Map<String, String> queryParam = dateQuery.getQueryParam();
        queryParam.put("pagenum", "1");
        queryParam.put("pagesize", "1");
        String resultstr = httpUtil.get(url, queryParam);
        JSONObject jsonObject = JSON.parseObject(resultstr);
        Object total = jsonObject.get("total");
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (Objects.isNull(total) || "0".equals(total.toString())) {
            return resultList;
        }
        queryParam.put("pagesize", total.toString());
        String s = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(s)) {
            return resultList;
        }
        JSONObject object = JSONObject.parseObject(s);
        JSONArray data = object.getJSONArray("data");
        for (int i = data.size() - 1; i >= 0; i++) {
            JSONObject dataJSONObject = data.getJSONObject(i);
            resultList.add(dataJSONObject);
        }
        return resultList;
    }

}
