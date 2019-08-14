package com.cisdi.steel.module.job.a2.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 焦炉加热
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
public class JiaolujiareWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        String version ="67.0";
        try{
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        }catch(Exception e){
        }
        String jhNo = PoiCustomUtil.getSheetCell(workbook, "_jhNo", 0, 1);
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                List<DateQuery> dateQueries = null;
                // 获取的对应的策略
                dateQueries = new ArrayList<>();
                Calendar cal = Calendar.getInstance();
                // 以查询时间为基准
                cal.setTime(date.getRecordDate());
                // 清零秒
                cal.set(Calendar.SECOND,0);
                // 当天23:59
                cal.set(Calendar.HOUR_OF_DAY,23);
                cal.set(Calendar.MINUTE,59);
                Date endTime = cal.getTime();
                // 前一天23:30
                cal.add(Calendar.DAY_OF_MONTH,-1);
                cal.set(Calendar.HOUR_OF_DAY,23);
                cal.set(Calendar.MINUTE,30);
                Date beginTime = cal.getTime();

                // 归零
                Date huanxiang = getHuanXiangDate(version,jhNo);
                cal.set(Calendar.MINUTE,huanxiang.getMinutes());
                while(cal.getTime().after(beginTime)) {
                    cal.add(Calendar.MINUTE,-30);
                }
                // 轮询计算
                while(cal.getTime().before(endTime)) {
                    Date recordDate = cal.getTime();
                    dateQueries.add(new DateQuery(null,null,recordDate));
                    cal.add(Calendar.MINUTE,30);
                }

                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                int size = dateQueries.size();
                for (int j = 0; j < size; j++) {
                    DateQuery item = dateQueries.get(j);
                    if (item.getRecordDate().before(new Date())) {
                        int rowIndex = j + 1;
                        List<CellData> cellDataList = this.mapDataHandler(rowIndex, getUrl(version), columns, item,jhNo);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    } else {
                        break;
                    }
                }
            }
        }
        return workbook;
    }

    protected List<CellData> mapDataHandler(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery,String jhNo) {
        Map<String, String> queryParam = getQueryParam(dateQuery,jhNo);
        int size = columns.size();
        List<CellData> cellDataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            String[] s = column.split("_");
            if("cur".equals(s[s.length-1])){
                queryParam = getQueryParam(dateQuery,"6.0");
            }
            if (StringUtils.isNotBlank(column)) {
                queryParam.put("tagNames", column);
                String result = httpUtil.get(url, queryParam);
                if (StringUtils.isNotBlank(result)) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (Objects.nonNull(jsonObject)) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (Objects.nonNull(data)) {
                            JSONArray arr = data.getJSONArray(column);
                            if (Objects.nonNull(arr)&& arr.size()!=0) {
                                JSONObject jsonObject1 = arr.getJSONObject(arr.size() - 1);
                                Double val = jsonObject1.getDouble("val");
                                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
                            }
                        }
                    }
                }
            }
        }
        return cellDataList;
    }

    protected Map<String, String> getQueryParam(DateQuery dateQuery,String jhNo) {
        Map<String, String> result = new HashMap<>();
        Date date = dateQuery.getRecordDate();
        result.put("startDate", DateUtil.getFormatDateTime(DateUtil.addMinute(date,-1),"yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(date,"yyyy/MM/dd HH:mm:ss"));
       return result;
    }

    private Date getHuanXiangDate(String version, String jhNo){
        String url = getHuanXiangUrl(version,jhNo);
        String result = httpUtil.get(url, null);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (null != jsonObject) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (null != data) {
                    JSONArray arr = data.getJSONArray("huangDate");
                    if ((null != arr)&& arr.size()!=0) {
                        JSONObject jsonObject1 = arr.getJSONObject(0);
                        Date val = jsonObject1.getDate("shijian");
                        return val;
                    }
                }
            }
        }
        return null;
    }

    protected String getUrl(String version) {
        return httpProperties.getJHUrlVersion(version) + "/jhTagValue/getTagValue";
    }

    protected String getHuanXiangUrl(String version, String jhNo) {
        String url =  httpProperties.getJHUrlVersion(version) + "/huanxiangxinhao/selectHuanXiangDate";
        String param = "?num=";
        switch (jhNo){
            case "1.0":
                param += "0";
                break;
            case "2.0":
                param += "1";
                break;
            case "4.0":
                param += "0";
                break;
            case "5.0":
                param += "1";
                break;
            case "6.0":
                param += "0";
                break;
            case "7.0":
                param += "1";
                break;
        }
        url += param;
        return url;
    }

}
