package com.cisdi.steel.module.job.a3.writer;

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

            int picTime = -1;
            int currDateTime = Integer.valueOf(DateUtil.getFormatDateTime(date.getRecordDate(), "HH"));
            picTime = currDateTime;
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);

                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                int index = 4;
                Map<String, Object> map = dealFourDate(currDateTime, dateQueries);
                Object dateQ = map.get("dateQ");
                if (Objects.nonNull(dateQ)) {
                    DateQuery item = (DateQuery) dateQ;
                    Object bc = map.get("bc");
                    if (Objects.nonNull(item)) {
                        List<CellData> cellDataList = this.mapDataHandler2(columns, item, index, sheetName, version);
                        if ("_main4_day_4hour".equals(sheetName)) {
                            String time = DateUtil.getFormatDateTime(DateUtil.addHours(item.getStartTime(), -1), "HH:mm:ss");
                            ExcelWriterUtil.addCellData(cellDataList, 1, 9, time);
                            ExcelWriterUtil.addCellData(cellDataList, 2, 9, bc);
                        }
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);

                    }
                }
            }

            if ("main".equals(sheetName)) {
                dealPicLocation(version, date, sheetName, workbook, picTime + "", sheet);
            }

        }
        return workbook;
    }

    protected List<CellData> mapDataHandler2(List<String> columns, DateQuery dateQuery, int index, String sheetName, String version) {
        List<CellData> cellDataList = new ArrayList<>();
        int rowIndex = (index - 4) + 1;
        String reportCode = "";
        if ("_main4_day_4hour".equals(sheetName)) {
            reportCode = "process_param_4h";
        } else {
            reportCode = "sinter_quality_4h";
        }
        String url = getUrl7(version, reportCode);
        String result = httpUtil.get(url);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject obj = JSONObject.parseObject(result);
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

                        String val4 = "";
                        //状态评价（-1偏低，0正常，1偏高）
                        if (Objects.nonNull(val3) && Objects.nonNull(str1) && Objects.nonNull(str2)) {
                            if (val3.doubleValue() < str1.doubleValue()) {
                                val4 = "偏低";
                            } else if (val3.doubleValue() >= str1.doubleValue() && val3.doubleValue() <= str2.doubleValue()) {
                                val4 = "正常";
                            } else if (val3.doubleValue() > str2.doubleValue()) {
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
    private void dealPicLocation(String version, DateQuery date, String sheetName, Workbook workbook, String picTime, Sheet sheet) {
        byte[] inputStream = dealPicture(version, date.getRecordDate(), "1", picTime, sheet);
        if (Objects.nonNull(inputStream)) {
            ExcelWriterUtil.setImg(workbook, inputStream, sheetName, 18, 25, 3, 4);
        }

        byte[] inputStream2 = dealPicture(version, date.getRecordDate(), "6", picTime, sheet);
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
    private byte[] dealPicture(String version, Date picDate, String picLocation, String picTime, Sheet sheet) {
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("picDate", DateUtil.getFormatDateTime(picDate, DateUtil.yyyyMMddFormat));
//        queryParam.put("picDate", "2019-05-24");
        queryParam.put("picLocation", picLocation);
        queryParam.put("picTime", picTime);
        String s = httpUtil.get(getUrl5(version), queryParam);
        String picUrl = null;
        String picEvaluate = "";
        if (StringUtils.isNotBlank(s)) {
            JSONObject jsonObject = JSONObject.parseObject(s);
            if (Objects.nonNull(jsonObject)) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (Objects.nonNull(jsonArray) && jsonArray.size() > 0) {
                    JSONObject object = jsonArray.getJSONObject(0);
                    picUrl = object.getString("picUrl");
                    if (StringUtils.isNotBlank(picUrl)) {
                        String[] split = picUrl.split("/");
                        picUrl = split[split.length - 1];

                    }
                    picEvaluate = object.getString("picEvaluate");
                }
            }
        }
        if (StringUtils.isNotBlank(picEvaluate)) {
            List<CellData> results = new ArrayList<>();
            int c = 2;
            if ("1".equals(picLocation)) {
                c = 2;
            } else if ("6".equals(picLocation)) {
                c = 7;
            }
            ExcelWriterUtil.addCellData(results, 25, c, picEvaluate);
            ExcelWriterUtil.setCellValue(sheet, results);
        }

        byte[] strem = httpUtil.getStrem(getUrl6(version) + picUrl, null);
        return strem;
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

    private String getUrl7(String version, String reportCode) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/tagReport/" + reportCode;
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/tagReport/" + reportCode;
        }
    }
}
