package com.cisdi.steel.module.job.a3.writer;

import com.alibaba.fastjson.JSONArray;
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
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 5#、6#烧结机生产日报
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JiejiWriter extends AbstractExcelReadWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = PoiCustomUtil.getSheetCell(workbook, "_dictionary", 0, 1);
        return getMapHandler2(1, excelDTO, version);
    }


    private Workbook getMapHandler2(Integer rowBatch, WriterExcelDTO excelDTO, String version) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String url = getUrl(version);
            if ("_sjmain2_day_shift".equals(sheetName)) {
                url = getUrl2(version);
            } else if ("_sjmain3_day_shift".equals(sheetName)) {
                url = getUrl3(version);
            } else if ("_sjmain4_day_shift".equals(sheetName)) {
                url = getUrl4(version);
            } else if ("_lilunshengchan_day_shift".equals(sheetName)) {
                url = getUrl5(version);
            }
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                int indexRow = 1;
                if ("_sjmain4_day_shift".equals(sheetName)) {
                    List<CellData> cellDataList = this.mapDataHandler(url, columns, dateQueries.get(0), rowBatch, sheetName, indexRow);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else if ("_sjgc_day_hour".equals(sheetName) || "_sjfx_day_hour".equals(sheetName) || "_sjmain1_day_shift".equals(sheetName)) {
                    DateQuery dateQuery = DateQueryUtil.buildToday(new Date());
                    List<CellData> cellDataList = this.mapDataHandler(url, columns, dateQuery, rowBatch, sheetName, indexRow);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else {
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = this.mapDataHandler(url, columns, item, rowBatch, sheetName, indexRow);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        indexRow++;
                    }
                }
            }
        }
        return workbook;
    }

    private List<CellData> mapDataHandler(String url, List<String> columns, DateQuery dateQuery, int rowBatch, String sheetName, int idexRow) {
        JSONObject query = new JSONObject();
        query.put("start", dateQuery.getQueryStartTime());
        query.put("end", dateQuery.getQueryEndTime());

        Map<String, String> map = new HashMap<>();

        if ("_sjmain3_day_shift".equals(sheetName)) {
            query.put("brandCode", "sinter");
            query.put("itemNames", columns);
        } else if ("_sjmain4_day_shift".equals(sheetName)) {
            map.put("brandCode", "sinter");
            map.put("timeType", "sampleTime");
            map.put("pageSize", Integer.MAX_VALUE + "");
            map.put("startTime", dateQuery.getQueryStartTime().toString());
            map.put("endTime", new Date().getTime() + "");
        } else if ("_lilunshengchan_day_shift".equals(sheetName)) {
            map.put("date", dateQuery.getStartTime().getTime() + "");
        } else {
            query.put("tagNames", columns);
        }

        String result = null;
        if ("_sjmain4_day_shift".equals(sheetName) || "_lilunshengchan_day_shift".equals(sheetName)) {
            result = httpUtil.get(url, map);
        } else {
            SerializeConfig serializeConfig = new SerializeConfig();
            String jsonString = JSONObject.toJSONString(query, serializeConfig);
            result = httpUtil.postJsonParams(url, jsonString);
        }

        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        if ("_sjmain2_day_shift".equals(sheetName)) {
            JSONArray data2 = jsonObject.getJSONArray("data");
            if (Objects.isNull(data2) || data2.size() == 0) {
                return null;
            }
            return this.handlerJsonArray(columns, rowBatch, data2, idexRow);
        } else if ("_sjmain3_day_shift".equals(sheetName)) {
            JSONArray data2 = jsonObject.getJSONArray("data");
            if (Objects.isNull(data2) || data2.size() == 0) {
                return null;
            }
            return this.handlerJsonArray3(columns, rowBatch, data2, idexRow);
        } else if ("_sjmain4_day_shift".equals(sheetName)) {
            JSONArray data2 = jsonObject.getJSONArray("data");
            if (Objects.isNull(data2) || data2.size() == 0) {
                return null;
            }
            return this.handlerJsonArray2(columns, rowBatch, data2, idexRow);
        } else if ("_lilunshengchan_day_shift".equals(sheetName)) {
            JSONArray data2 = jsonObject.getJSONArray("data");
            if (Objects.isNull(data2) || data2.size() == 0) {
                return null;
            }
            return this.handlerJsonArray4(columns, rowBatch, data2, idexRow);
        } else {
            JSONObject data = jsonObject.getJSONObject("data");
            if (Objects.isNull(data)) {
                return null;
            }
            return this.handlerJsonArray(columns, data, sheetName);
        }
    }

    protected List<CellData> handlerJsonArray4(List<String> columns, int rowBatch, JSONArray data, int startRow) {
        List<CellData> cellDataList = new ArrayList<>();
        for (int j = 0; j < data.size(); j++) {
            JSONObject o1 = data.getJSONObject(j);
            if (Objects.nonNull(o1)) {
                List<CellData> cellDataList1 = ExcelWriterUtil.handlerRowData(columns, startRow++, o1);
                cellDataList.addAll(cellDataList1);
            }
        }

        return cellDataList;
    }

    protected List<CellData> handlerJsonArray2(List<String> columns, int rowBatch, JSONArray data, int startRow) {
        List<CellData> cellDataList = new ArrayList<>();
        int size = columns.size();
       /* for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            if (StringUtils.isNotBlank(column)) {*/
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject o = data.getJSONObject(i);
            if (Objects.nonNull(o)) {
                JSONObject analysis = o.getJSONObject("analysis");
                if ("LC".equals(analysis.get("type")) || "LP".equals(analysis.get("type"))) {
                    list.add(o);
                }
            }
        }

        for (int j = 0; j < list.size(); j++) {
            JSONObject o1 = list.get(j);
            if (Objects.nonNull(o1)) {
                o1.put("total", list.size());
                List<CellData> cellDataList1 = ExcelWriterUtil.handlerRowData(columns, startRow++, o1);
                cellDataList.addAll(cellDataList1);
            }
              /*  }
            }*/
        }
        return cellDataList;
    }

    protected List<CellData> handlerJsonArray3(List<String> columns, int rowBatch, JSONArray data, int startRow) {
        List<CellData> cellDataList = new ArrayList<>();
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            int rowIndex = 1;
            if (StringUtils.isNotBlank(column)) {
                for (int j = 0; j < data.size(); j++) {
                    JSONObject o1 = data.getJSONObject(j);
                    JSONObject val = o1.getJSONObject("values");
                    if (Objects.nonNull(o1)) {
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val.get(column));
                        rowIndex++;
                    }
                }
            }
        }
        return cellDataList;
    }

    private List<CellData> handlerJsonArray(List<String> columns, JSONObject data, String sheetName) {
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

                    List<DateQuery> all = new ArrayList<>();

                    if ("_sjmain1_day_shift".equals(sheetName)) {
                        DateQuery dateQuery = DateQueryUtil.buildToday(new Date());
                        List<DateQuery> dateQueries8 = DateQueryUtil.buildDay8HourEach(dateQuery.getEndTime());
                        all.addAll(dateQueries8);
                    } else {
                        List<DateQuery> dateQueries = DateQueryUtil.buildDayHourEach(new Date());
                        all.addAll(dateQueries);
                    }

                    for (int j = 0; j < all.size(); j++) {
                        long time = all.get(j).getStartTime().getTime();
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


//                    for (String key : keys) {
//                        Object o = innerMap.get(key);
//                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, o);
//                        rowIndex++;
//                    }
                }
            }
        }
        return cellDataList;
    }

    @Override
    protected List<CellData> handlerJsonArray(List<String> columns, int rowBatch, JSONArray data, int startRow) {
        List<CellData> cellDataList = new ArrayList<>();
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            int rowIndex = startRow;
            if (StringUtils.isNotBlank(column)) {
                JSONObject o1 = data.getJSONObject(i);
                Object val = o1.get("val");
                if (Objects.nonNull(o1)) {
                    ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
                    rowIndex++;
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

    private String getUrl2(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/tagValues/latest";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/tagValues/latest";
        }
    }

    private String getUrl3(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/analysis/anaItemValues2";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/analysis/anaItemValues2";
        }
    }

    private String getUrl4(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/analysisValues/sampleTime";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/analysisValues/sampleTime";
        }
    }

    private String getUrl5(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/report/theoryYield";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/report/theoryYield";
        }
    }
}
