package com.cisdi.steel.module.job.a3.writer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.poi.PoiCustomUtil;
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

import java.util.*;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class TuoliuWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = PoiCustomUtil.getSheetCell(workbook, "_dictionary", 0, 1);
        return getMapHandler1(excelDTO, version);
    }

    private Workbook getMapHandler1(WriterExcelDTO excelDTO, String version) {
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
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);

                if ("_6tuoliutuoxiaomax_day_hour".equals(sheetName) || "_6tuoliutuoxiaosum_day_hour".equals(sheetName)) {
                    int  rowBatch=1;
                    for (DateQuery dateQuery : dateQueries) {
                        List<CellData> cellDataList = this.mapDataHandler(getUrl1(version), columns, dateQuery, rowBatch, sheetName);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        rowBatch++;
                    }
                } else {
                    int  rowBatch=1;
                    DateQuery dateQuery = DateQueryUtil.buildToday(date.getRecordDate());
                    List<CellData> cellDataList = this.mapDataHandler(getUrl(version), columns, dateQuery, rowBatch, sheetName);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }
        return workbook;
    }

    protected List<CellData> mapDataHandler(String url, List<String> columns, DateQuery dateQuery, int rowBatch, String sheetName) {
        JSONObject query = new JSONObject();
        query.put("start", dateQuery.getQueryStartTime());
        query.put("end", dateQuery.getQueryEndTime());
        query.put("tagNames", columns);

        if ("_6tuoliutuoxiaomax_day_hour".equals(sheetName)) {
            query.put("method", "max,min");
        } else if ("_6tuoliutuoxiaosum_day_hour".equals(sheetName)) {
            query.put("method", "sum");
        }
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String result = httpUtil.postJsonParams(url, jsonString);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return null;
        }
        if ("_6tuoliutuoxiaosum_day_hour".equals(sheetName)) {
            return handlerJsonArray1(columns, data, rowBatch, sheetName);
        } else if ("_6tuoliutuoxiaomax_day_hour".equals(sheetName)) {
            return handlerJsonArray2(columns, data, rowBatch, sheetName);
        } else {
            return handlerJsonArray(columns, data, rowBatch);
        }
    }


    private List<CellData> handlerJsonArray2(List<String> columns, JSONObject data, int rowBatch, String sheetName) {
        List<CellData> cellDataList = new ArrayList<>();
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            int rowIndex = rowBatch;
            if (StringUtils.isNotBlank(column)) {
                JSONObject jsonObject = data.getJSONObject(column);
                Object max = "";
                Object min = "";
                if (Objects.nonNull(jsonObject)) {
                    if ("_6tuoliutuoxiaomax_day_hour".equals(sheetName)) {
                        max = jsonObject.getDouble("max");
                        min = jsonObject.getDouble("min");
                    }
                }
                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, min);
                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i + 1, max);
                rowIndex++;
            }
        }
        return cellDataList;
    }

    private List<CellData> handlerJsonArray1(List<String> columns, JSONObject data, int rowBatch, String sheetName) {
        List<CellData> cellDataList = new ArrayList<>();
        int size = columns.size();
        int rowIndex = rowBatch;
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            if (StringUtils.isNotBlank(column)) {
                JSONObject jsonObject = data.getJSONObject(column);
                Object v = "";
                if (Objects.nonNull(jsonObject)) {
                    if ("_6tuoliutuoxiaomax_day_hour".equals(sheetName)) {
                        double max = jsonObject.getDouble("max");
                        double min = jsonObject.getDouble("min");
                        v = max + "/" + min;
                    } else if ("_6tuoliutuoxiaosum_day_hour".equals(sheetName)) {
                        double sum = jsonObject.getDouble("sum");
                        v = sum;
                    }

                }
                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, v);
                rowIndex++;
            }
        }
        return cellDataList;
    }

    private List<CellData> handlerJsonArray(List<String> columns, JSONObject data, int rowBatch) {
        List<CellData> cellDataList = new ArrayList<>();
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            int rowIndex = 1;
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

                    List<DateQuery> dateQueries = DateQueryUtil.buildDayHourEach(new Date());

                    for (int j = 0; j < dateQueries.size(); j++) {
                        long time = dateQueries.get(j).getStartTime().getTime();
                        Object v = "";
                        for (int m = 0; m < list.length; m++) {
                            if (time == list[m].longValue()) {
                                Object o = innerMap.get(String.valueOf(list[m]));
                                v = o;
                                break;
                            }
                        }
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, v);
                        rowIndex++;
                    }
                }
            }
        }
        return cellDataList;
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

    private String getUrl1(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/tagValueActions";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/tagValueActions";
        }
    }

    /**
     * {
     *   "data": {
     *     "ST6_L1R_DeSN_HsCLiL_1h_avg": {
     *       "max": 2066.64,
     *       "min": 1786.37
     *     }
     *   }
     * }
     */
}
