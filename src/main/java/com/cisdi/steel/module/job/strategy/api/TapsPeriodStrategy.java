package com.cisdi.steel.module.job.strategy.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 出铁作业月报
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/12/20 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
@Slf4j
public class TapsPeriodStrategy extends AbstractApiStrategy {
    @Override
    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (DateQuery dateQuery : queryList) {
            Map<String,Object> requestData = this.requestData(url, dateQuery);
            dataList.add(requestData);
        }
        List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
        List<CellData> cellDataList = this.loopRowData(dataList, columns);
        return SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(cellDataList)
                .build();
    }

    private List<CellData> loopRowData(List<Map<String, Object>> dataList, List<String> columns) {
        int starRow = 1;
        List<CellData> resultData = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            List<CellData> rowData = ExcelWriterUtil.handlerRowData(columns, starRow, data);
            resultData.addAll(rowData);
            starRow += 1;
        }
        return resultData;
    }

    private Map<String,Object> requestData(String urlPre, DateQuery dateQuery) {
        String url = urlPre + "/taps/summary/period";
        Map<String, String> queryParam = dateQuery.getQueryParam();
        queryParam.put("pagenum", "1");
        queryParam.put("pagesize", "1000");
        String result = httpUtil.get(url, queryParam);
        JSONObject jsonObject = JSON.parseObject(result);
        Object total = jsonObject.get("total");
        Map<String,Object> resultMap = new HashMap<>();
        if (Objects.isNull(total) || "0".equals(total.toString())) {
            return resultMap;
        }
        queryParam.put("pagesize", total.toString());
        result = httpUtil.get(url, queryParam);
        JSONObject object = JSONObject.parseObject(result);
        JSONArray jsonArray = object.getJSONArray("data");
        if(Objects.isNull(jsonArray)){
            return resultMap;
        }
        int size = jsonArray.size();
        for (int i = 0; i < size; i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if (Objects.isNull(obj)) {
                continue;
            }
            Integer taphole = obj.getInteger("taphole");
            // 本次出铁量
            Object actweight = obj.get("actweight");
            resultMap.put("actweight"+taphole,actweight);

            // 铁口深度
            Object dephth = obj.get("dephth");
            resultMap.put("dephth"+taphole,dephth);

            // 铁水温度
            Object temp = obj.get("temp");
            resultMap.put("temp"+taphole,temp);


            String starttime = obj.getString("starttime");
            String endtime = obj.getString("endtime");
            if (StringUtils.isNotBlank(starttime) && StringUtils.isNotBlank(endtime)) {
                try {
                    Date startTime = new Date(Long.parseLong(starttime));
                    Date endTime = new Date(Long.parseLong(endtime));
                    Long betweenMin = DateUtil.getBetweenMin(endTime, startTime);
                    resultMap.put("time"+taphole,betweenMin);
                }catch (Exception e){
                    log.error("时间转换错误");
                }
            }
        }
        return resultMap;
    }

    @Override
    public String getKey() {
        return "tapsPeriod";
    }
}
