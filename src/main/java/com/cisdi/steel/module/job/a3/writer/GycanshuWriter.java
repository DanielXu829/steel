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
import com.cisdi.steel.module.job.util.ExcelCellColorUtil;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
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
public class GycanshuWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = PoiCustomUtil.getSheetCell(workbook, "_dictionary", 0, 1);
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                int index = 4;
                for (DateQuery item : dateQueries) {
                    List<CellData> cellDataList = this.mapDataHandler1(columns, item, index, sheetName, version);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    index += 4;
                }

            }
        }
        return workbook;
    }

    protected JSONObject getQueryParam1(DateQuery dateQuery) {
        JSONObject result = new JSONObject();
        result.put("method", "avg");
        result.put("start", DateUtil.getFormatDateTime(DateUtil.addHours(dateQuery.getStartTime(),-1), DateUtil.fullFormat));
        result.put("end", DateUtil.getFormatDateTime(DateUtil.addHours(dateQuery.getEndTime(),-1), DateUtil.fullFormat));
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
        if ("_main2_day_4hour".equals(sheetName)) {
            queryParam.put("method", "min,max");
            url = getUrl2(version);
        } else if ("_main3_day_4hour".equals(sheetName)) {
            queryParam.remove("method");
            queryParam.remove("tagNames");
            queryParam.put("itemNames", columns);
            queryParam.put("brandCode", "sinter");
            url = getUrl3(version);
        }

        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(queryParam, serializeConfig);


        String result = "";
        if ("_main3_day_4hour".equals(sheetName)) {
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
            if ("_main2_day_4hour".equals(sheetName)) {
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
}
