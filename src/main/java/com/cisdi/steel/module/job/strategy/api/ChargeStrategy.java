package com.cisdi.steel.module.job.strategy.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.resp.ResponseUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.*;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class ChargeStrategy extends AbstractApiStrategy {
    public ChargeStrategy(HttpUtil httpUtil, HttpProperties httpProperties) {
        super(httpUtil, httpProperties);
    }

    @Override
    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
        List<CellData> cellDataList = new ArrayList<>();
        for (DateQuery dateQuery : queryList) {
            // 理论上来说一次获取了所有，循环只执行一次
            List<Map<String, Object>> dataList = this.requestApiData(dateQuery);
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
            // 每一行的数据
            JSONObject jsonObject = (JSONObject) data.get("data");
            // 存储每一个
            List<CellData> rowData = ExcelWriterUtil.handlerRowData(columns, starRow, jsonObject);
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
    private List<Map<String, Object>> requestApiData(DateQuery dateQuery) {
        String url = httpProperties.getUrlApiGLOne() + "/batchenos/period";
        String s = httpUtil.get(url, dateQuery.getQueryParam());
        List<String> list = ResponseUtil.getResponseArray(s, String.class);
        List<Map<String, Object>> result = new ArrayList<>();
        if (Objects.isNull(list) || list.isEmpty()) {
            return result;
        }
        Collections.sort(list);
        for (String batchNo : list) {
            String detail = httpProperties.getUrlApiGLOne() + "/batch/" + batchNo;
            String detailData = httpUtil.get(detail);
            if (StringUtils.isNotBlank(detailData)) {
                Map<String, Object> mapType = JSON.parseObject(detailData, Map.class);
                result.add(mapType);
            }
        }
        return result;
    }

}
