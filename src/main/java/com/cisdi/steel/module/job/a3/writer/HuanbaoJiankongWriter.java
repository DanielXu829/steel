package com.cisdi.steel.module.job.a3.writer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 烧结公辅环保设施运行情况及在线监测数据发布
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class HuanbaoJiankongWriter extends AbstractExcelReadWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        return this.getMapHandler2(excelDTO);
    }

    /**
     * 同样处理 方式
     *
     * @param excelDTO 数据
     * @return 结果
     */
    public Workbook getMapHandler2(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            String version = "5.0";
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                //获取版本
                String main1 = sheetSplit[1];
                if (main1.startsWith("6")) {
                    version = "6.0";
                }
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                for (DateQuery dateQuery : dateQueries) {
                    List<CellData> cellDataList = mapDataHandler(getUrl(version), columns, dateQuery);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }

            }
        }
        return workbook;
    }

    private List<CellData> mapDataHandler(String url, List<String> columns, DateQuery dateQuerys) {
        List<CellData> cellDataList = new ArrayList<>();
        //构建从昨天17：00到今天16:00时间段
        Date startTime = DateUtil.addHours(dateQuerys.getStartTime(), -7);
        Date endTime = DateUtil.addHours(dateQuerys.getEndTime(), -8);

        JSONObject query = new JSONObject();
        query.put("start", startTime.getTime());
        query.put("end", endTime.getTime());
        query.put("tagNames", columns);
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String result = httpUtil.postJsonParams(url, jsonString);

        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return null;
        }
        List<Long> timeList = dealDate(startTime, endTime);
        deal(cellDataList, columns, data, timeList);
        return cellDataList;
    }


    private void deal(List<CellData> cellDataList, List<String> columns, JSONObject data, List<Long> timeList) {
        for (int m = 0; m < columns.size(); m++) {
            String column = columns.get(m);
            if (StringUtils.isNotBlank(column)) {
                JSONObject jsonObject = data.getJSONObject(column);
                if (Objects.nonNull(jsonObject)) {
                    Map<String, Object> innerMap = jsonObject.getInnerMap();
                    Set<String> keys = innerMap.keySet();

                    Long[] list = new Long[keys.size()];
                    int k = 0;
                    for (String key : keys) {
                        list[k] = Long.valueOf(key);
                        k++;
                    }
                    Arrays.sort(list);

                    int size = list.length;
                    int rowIndex = 1;
                    for (int j = 0; j < timeList.size(); j++) {
                        Long time1 = timeList.get(j);
                        Object o = null;
                        for (int i = 0; i < size; i++) {
                            Long key = list[i];
                            if (time1.longValue() == key.longValue()) {
                                o = innerMap.get(key + "");
                                break;
                            }
                        }
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, 0, time1);
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, m + 1, o);
                        rowIndex++;
                    }
                }
            }
        }
    }


    private List<Long> dealDate(Date startTime, Date endTime) {
        List<Long> dataList = new ArrayList<>();
        Date tempDate = startTime;
        while (tempDate.before(endTime)) {
            dataList.add(tempDate.getTime());
            tempDate = DateUtil.addHours(tempDate, 1);
        }
        dataList.add(endTime.getTime());
        return dataList;
    }

    /**
     * 不同的版本获取不同的接口地址
     *
     * @param version 版本号
     * @return 结果
     */
    private String getUrl(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/tagValues/tagNames";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/tagValues/tagNames";
        }
    }
}
