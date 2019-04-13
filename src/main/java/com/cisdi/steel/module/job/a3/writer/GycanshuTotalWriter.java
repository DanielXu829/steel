package com.cisdi.steel.module.job.a3.writer;

import com.alibaba.fastjson.JSONArray;
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

import java.util.*;

/**
 * 五、六号烧结机主要工艺参数及实物质量情况
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GycanshuTotalWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        //默认5烧结
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            String version = "5.0";
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                String main1 = sheetSplit[1];
                String lastExt = sheetSplit[3];
                if (main1.startsWith("6")) {
                    version = "6.0";
                }
                // 获取的对应的策略
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);

                if ("4hour".equals(lastExt)) {
                    List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                    int currDateTime = Integer.valueOf(DateUtil.getFormatDateTime(date.getRecordDate(), "HH"));
                    int index = 4;
                    Map<String, Object> map = dealFourDate(currDateTime, dateQueries);
                    Object dateQ = map.get("dateQ");
                    if (Objects.nonNull(dateQ)) {
                        DateQuery item = (DateQuery) dateQ;
                        Object bc = map.get("bc");
                        if (Objects.nonNull(item)) {
                            List<CellData> cellDataList = this.mapDataHandler1(columns, item, index, sheetName, version);
                            if ("_5main1_day_4hour".equals(sheetName)) {
                                String time = DateUtil.getFormatDateTime(DateUtil.addHours(item.getStartTime(), -1), "HH:mm:ss");
                                ExcelWriterUtil.addCellData(cellDataList, 1, 9, time);
                                ExcelWriterUtil.addCellData(cellDataList, 2, 9, bc);
                            }

                            ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        }
                    }
                } else if ("all".equals(lastExt)) {
                    List<DateQuery> dateQueries = new ArrayList<>();
                    Date curr = date.getRecordDate();
                    //当前时间往前推一周
                    Date beforeDate = DateUtil.addDays(curr, -6);
                    while (beforeDate.before(curr)) {
                        DateQuery buildToday = DateQueryUtil.buildToday(beforeDate);
                        dateQueries.add(buildToday);
                        beforeDate = DateUtil.addDays(beforeDate, 1);
                    }
                    dateQueries.add(DateQueryUtil.buildToday(curr));
                    int index = 1;
                    for (int j = 0; j < dateQueries.size(); j++) {
                        DateQuery query = dateQueries.get(j);
                        List<CellData> cellDataList = this.mapDataHandler2(columns, query, index, sheetName, version);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index++;
                    }
                } else if ("each".equals(lastExt)) {
                    List<DateQuery> dateQueries = new ArrayList<>();
                    Date curr = date.getRecordDate();
                    //当前时间往前推24小时
                    Date beforeDate = DateUtil.addHours(curr, -23);
                    while (beforeDate.before(curr)) {
                        DateQuery buildToday = new DateQuery(beforeDate, beforeDate, beforeDate);
                        dateQueries.add(buildToday);
                        beforeDate = DateUtil.addHours(beforeDate, 1);
                    }
                    dateQueries.add(date);
                    int index = 1;
                    for (int j = 0; j < dateQueries.size(); j++) {
                        DateQuery query = dateQueries.get(j);
                        List<CellData> cellDataList = this.mapDataHandler3(columns, query, index, sheetName, version);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index++;
                    }
                }
            }
        }
        return workbook;
    }

    /**
     * 23-3
     * 3-7
     * 7-11
     * 11-15
     * 15-19
     * 19-23
     */
    private Map dealFourDate(int dateTime, List<DateQuery> dateQueries) {
        Map<String, Object> map = new HashMap<>();
        if ((dateTime > 0 && dateTime < 3) || dateTime == 23) {
            map.put("bc", "夜班");
            map.put("dateQ", dateQueries.get(0));
        } else if (dateTime < 7 && dateTime >= 3) {
            map.put("bc", "夜班");
            map.put("dateQ", dateQueries.get(1));
        } else if (dateTime < 11 && dateTime >= 7) {
            map.put("bc", "白班");
            map.put("dateQ", dateQueries.get(2));
        } else if (dateTime < 15 && dateTime >= 11) {
            map.put("bc", "白班");
            map.put("dateQ", dateQueries.get(3));
        } else if (dateTime < 19 && dateTime >= 15) {
            map.put("bc", "中班");
            map.put("dateQ", dateQueries.get(4));
        } else if (dateTime < 23 && dateTime >= 19) {
            map.put("bc", "中班");
            map.put("dateQ", dateQueries.get(5));
        }
        return map;
    }

    protected JSONObject getQueryParam1(DateQuery dateQuery) {
        JSONObject result = new JSONObject();
        result.put("method", "avg");
        result.put("start", DateUtil.getFormatDateTime(DateUtil.addHours(dateQuery.getStartTime(), -1), DateUtil.fullFormat));
        result.put("end", DateUtil.getFormatDateTime(DateUtil.addHours(dateQuery.getEndTime(), -1), DateUtil.fullFormat));
        return result;
    }

    protected List<CellData> mapDataHandler1(List<String> columns, DateQuery dateQuery, int index, String sheetName, String version) {
        List<CellData> cellDataList = new ArrayList<>();
        int rowIndex = (index - 4) + 1;
        JSONObject queryParam = this.getQueryParam1(dateQuery);
        Map<String, String> queryParam2 = new HashMap<>();
        String url = getUrl(version);
        queryParam.put("tagNames", columns);
        queryParam2.put("brandCode", "sinter");
        if ("_5main2_day_4hour".equals(sheetName) || "_6main5_day_4hour".equals(sheetName)) {
            queryParam.put("method", "min,max");
            url = getUrl2(version);
        } else if ("_5main3_day_4hour".equals(sheetName) || "_6main6_day_4hour".equals(sheetName)) {
            queryParam.remove("method");
            queryParam.remove("tagNames");
            queryParam.put("itemNames", columns);
            queryParam.put("brandCode", "sinter");
            queryParam.put("type", "LC");
            url = getUrl3(version);
        }

        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(queryParam, serializeConfig);


        String result = "";
        if ("_5main3_day_4hour".equals(sheetName) || "_6main6_day_4hour".equals(sheetName)) {
            int columnIndex = 0;
            for (String column : columns) {
                if (StringUtils.isBlank(column)) {
                    continue;
                }
                int row = rowIndex;
                queryParam2.remove("type");
                queryParam2.put("anaItemName", column);
                result = httpUtil.get(url, queryParam2);
                JSONObject obj = JSONObject.parseObject(result);

                queryParam2.remove("anaItemName");
                queryParam2.put("type", "ALL");

                String url1 = getUrl4(version);
                if ("RDI+3.15".equals(column)) {
                    queryParam.put("type", "LM");
                    jsonString = JSONObject.toJSONString(queryParam, serializeConfig);
                }
                String result1 = httpUtil.postJsonParams(url1, jsonString);
                JSONObject obj1 = JSONObject.parseObject(result1);

                JSONObject data = obj.getJSONObject("data");
                JSONObject data1 = obj1.getJSONObject("data");

                if (Objects.isNull(data)) {
                    continue;
                }

                String str1 = data.getString("lowerlimit");
                String str2 = data.getString("upperlimit");

                String val1 = "";
                if (Objects.isNull(str1) && Objects.nonNull(str2)) {
                    val1 = "<" + str2;
                } else if (Objects.nonNull(str1) && Objects.isNull(str2)) {
                    val1 = ">" + str1;
                } else if (Objects.nonNull(str1) && Objects.nonNull(str2)) {
                    val1 = str1 + "～" + str2;
                }


                String val2 = "";
                String val3 = "正常";

                if (Objects.nonNull(data1) && !data1.isEmpty()) {
                    double a1 = 0;
                    double a2 = 0;
                    if (StringUtils.isNotBlank(str1)) {
                        a1 = Double.valueOf(str1);
                    }
                    if (StringUtils.isNotBlank(str2)) {
                        a2 = Double.valueOf(str2);

                    }

                    if (data1.containsKey(column)) {
                        double o = data1.getDouble(column);
                        val2 = o + "";
                        if (o < a1) {
                            val3 = "偏低";
                        } else if (o > a2) {
                            val3 = "偏高";
                        }
                    }
                }

                ExcelWriterUtil.addCellData(cellDataList, row++, columnIndex, val1);
                ExcelWriterUtil.addCellData(cellDataList, row++, columnIndex, val2);
                ExcelWriterUtil.addCellData(cellDataList, row++, columnIndex, val3);

                columnIndex++;
            }
            return cellDataList;
        } else {
            result = httpUtil.postJsonParams(url, jsonString);
        }

        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject obj = JSONObject.parseObject(result);
        Object data1 = obj.get("data");

        if (data1 instanceof JSONObject) {
            if ("_5main2_day_4hour".equals(sheetName) || "_6main5_day_4hour".equals(sheetName)) {
                JSONObject data = obj.getJSONObject("data");
                if (Objects.isNull(data)) {
                    return null;
                }
                int columnIndex = 0;
                for (String column : columns) {
                    if (StringUtils.isBlank(column)) {
                        continue;
                    }
                    JSONObject jsonObject = data.getJSONObject(column);
                    if (Objects.isNull(jsonObject)) {
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, columnIndex++, "");
                    } else {
                        Double min = jsonObject.getDouble("min");
                        Double max = jsonObject.getDouble("max");
                        String val = min.doubleValue() + "～" + max.doubleValue();
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, columnIndex++, val);
                    }
                }
            }
        } else {
            JSONArray data = obj.getJSONArray("data");
            if (Objects.isNull(data)) {
                return null;
            }
            int size = data.size();
            for (int i = 0; i < size; i++) {
                JSONObject map = data.getJSONObject(i);
                int columnIndex = 0;
                for (String column : columns) {
                    if (StringUtils.isBlank(column)) {
                        continue;
                    }
                    if (Objects.nonNull(map)) {
                        String tagName = map.getString("tagName");
                        if (tagName.equals(column)) {
                            String val1 = map.getString("unit");

                            Double str1 = map.getDouble("low");
                            Double str2 = map.getDouble("up");

                            String val2 = "";
                            if (Objects.isNull(str1) && Objects.nonNull(str2)) {
                                val2 = "<" + str2;
                            } else if (Objects.nonNull(str1) && Objects.isNull(str2)) {
                                val2 = ">" + str1;
                            } else if (Objects.nonNull(str1) && Objects.nonNull(str2)) {
                                val2 = str1 + "～" + str2;
                            }


                            Double val3 = map.getDouble("val");

                            Integer str4 = map.getInteger("status");
                            String val4 = "";
                            //状态评价（-1偏低，0正常，1偏高）
                            if (Objects.nonNull(str4)) {
                                if (str4.intValue() == -1) {
                                    val4 = "偏低";
                                } else if (str4.intValue() == 0) {
                                    val4 = "正常";
                                } else if (str4.intValue() == 1) {
                                    val4 = "偏高";
                                }
                            }
                            int row = rowIndex;
                            ExcelWriterUtil.addCellData(cellDataList, row++, columnIndex, val1);
                            ExcelWriterUtil.addCellData(cellDataList, row++, columnIndex, val2);
                            ExcelWriterUtil.addCellData(cellDataList, row++, columnIndex, val3);
                            ExcelWriterUtil.addCellData(cellDataList, row++, columnIndex, val4);
                        }
                    }
                    columnIndex++;
                }
            }
        }
        return cellDataList;
    }

    protected List<CellData> mapDataHandler2(List<String> columns, DateQuery dateQuery, int index, String sheetName, String version) {
        List<CellData> cellDataList = new ArrayList<>();
        JSONObject queryParam = new JSONObject();
        queryParam.put("start", dateQuery.getQueryStartTime());
        queryParam.put("end", dateQuery.getQueryEndTime());
        queryParam.put("tagNames", columns);

        String url = getUrl7(version);

        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(queryParam, serializeConfig);

        String result1 = httpUtil.postJsonParams(url, jsonString);
        JSONObject obj1 = JSONObject.parseObject(result1);
        if (Objects.nonNull(obj1)) {
            int columnIndex = 0;
            for (String column : columns) {
                Object value = "";
                if (StringUtils.isNotBlank(column)) {
                    JSONObject data = obj1.getJSONObject("data");
                    if (Objects.nonNull(data)) {
                        JSONObject jsonObject = data.getJSONObject(column);
                        if (Objects.nonNull(jsonObject)) {
                            value = jsonObject.getBigDecimal(dateQuery.getQueryStartTime().toString());
                        }
                    }
                }
                ExcelWriterUtil.addCellData(cellDataList, index, columnIndex++, value);
            }
            ExcelWriterUtil.addCellData(cellDataList, index, 0, DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd"));
        }


        return cellDataList;
    }

    protected List<CellData> mapDataHandler3(List<String> columns, DateQuery dateQuery, int index, String sheetName, String version) {
        List<CellData> cellDataList = new ArrayList<>();

        int columnIndex = 0;
        for (String column : columns) {
            Object value = "";
            if (StringUtils.isNotBlank(column)) {
                Map<String, String> queryParam = new HashMap<>();
                String url = getUrl8(version, column);
                queryParam.put("time", dateQuery.getRecordDate().getTime() + "");
                String result = httpUtil.get(url, queryParam);
                JSONObject obj = JSONObject.parseObject(result);

                if (Objects.nonNull(obj)) {
                    JSONArray jsonArray = obj.getJSONArray("data");
                    if (Objects.nonNull(jsonArray) && jsonArray.size() > 0) {
                        JSONObject object = jsonArray.getJSONObject(0);
                        if (Objects.nonNull(object)) {
                            value = object.get("val");
                        }
                    }
                }
            }
            ExcelWriterUtil.addCellData(cellDataList, index, columnIndex++, value);
        }
        ExcelWriterUtil.addCellData(cellDataList, index, 0, dateQuery.getRecordDate().getTime());


        return cellDataList;
    }

    /**
     * 将图片写入到对应位置
     *
     * @param version
     * @param date
     * @param sheetName
     * @param workbook
     * @param picTime
     */
    private void dealPicLocation(String version, DateQuery date, String sheetName, Workbook workbook, String picTime) {
        byte[] inputStream = dealPicture(version, date.getRecordDate(), "1", picTime);
        if (Objects.nonNull(inputStream)) {
            ExcelWriterUtil.setImg(workbook, inputStream, sheetName, 18, 25, 3, 4);
        }

        byte[] inputStream2 = dealPicture(version, date.getRecordDate(), "6", picTime);
        if (Objects.nonNull(inputStream2)) {
            ExcelWriterUtil.setImg(workbook, inputStream2, sheetName, 18, 25, 8, 9);
        }
    }

    /**
     * 处理图片参数
     *
     * @param version
     * @param picDate
     * @param picLocation
     * @param picTime
     * @return
     */
    private byte[] dealPicture(String version, Date picDate, String picLocation, String picTime) {
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("picDate", DateUtil.getFormatDateTime(picDate, DateUtil.yyyyMMddFormat));
//        queryParam.put("picDate", "2019-04-09");
        queryParam.put("picLocation", picLocation);
        queryParam.put("picTime", picTime);
        String s = httpUtil.get(getUrl5(version), queryParam);
        String picUrl = null;
        if (StringUtils.isNotBlank(s)) {
            JSONObject jsonObject = JSONObject.parseObject(s);
            if (Objects.nonNull(jsonObject)) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (Objects.nonNull(jsonArray) && jsonArray.size() > 0) {
                    JSONObject object = jsonArray.getJSONObject(0);
                    picUrl = object.getString("picUrl");
                }
            }
        }

        byte[] strem = httpUtil.getStrem(getUrl6(version) + picUrl, null);
        return strem;
    }

    private String getUrl(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/tagValues/latestAndStatus";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/tagValues/latestAndStatus";
        }
    }

    private String getUrl2(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/tagValueActions";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/tagValueActions";
        }
    }

    private String getUrl3(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/analysisMapsByCodeAndItem";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/analysisMapsByCodeAndItem";
        }
    }

    private String getUrl4(String version) {
        if ("5.0".equals(version)) {
//            return httpProperties.getUrlApiSJOne() + "/analysis/anaItemKeyVal";
            return httpProperties.getUrlApiSJOne() + "/anaLatestValue/sampleTime";
        } else {
            // "6.0".equals(version) 默认
//            return httpProperties.getUrlApiSJTwo() + "/analysis/anaItemKeyVal";
            return httpProperties.getUrlApiSJTwo() + "/anaLatestValue/sampleTime";
        }
    }

    private String getUrl6(String version) {
        if ("5.0".equals(version)) {
            // http://10.11.11.27:9001/getPictures?picDate=2019-04-09&picLocation=1&picTime=15
            return "http://10.11.11.27/images/";
        } else {
            // "6.0".equals(version) 默认
            return "http://10.11.11.28/images/";
        }
    }

    private String getUrl5(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/getPictures";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/getPictures";
        }
    }

    private String getUrl7(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/tagValues/tagNames";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/tagValues/tagNames";
        }
    }

    private String getUrl8(String version, String tagName) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/tagValue/" + tagName;
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/tagValue/" + tagName;
        }
    }
}
