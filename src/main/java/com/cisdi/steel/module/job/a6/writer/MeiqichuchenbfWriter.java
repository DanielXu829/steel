package com.cisdi.steel.module.job.a6.writer;

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

import java.math.BigDecimal;
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
public class MeiqichuchenbfWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                int size = dateQueries.size();
                if ("tag".equals(sheetSplit[1])) {
                    for (int rowNum = 0; rowNum < size; rowNum++) {
                        DateQuery eachDate = dateQueries.get(rowNum);
                        List<CellData> cellValInfoList = eachData(columns, getUrl(version), eachDate.getQueryParam(), sheetSplit[2]);
                        ExcelWriterUtil.setCellValue(sheet, cellValInfoList);
                    }
                } else if ("maxmin".equals(sheetSplit[1])) {
                    int index = 1;
                    for (int rowNum = 0; rowNum < size; rowNum++) {
                        DateQuery eachDate = dateQueries.get(rowNum);
                        List<CellData> cellValInfoList = mapDataHandler1(columns, getUrl2(version), eachDate, index);
                        ExcelWriterUtil.setCellValue(sheet, cellValInfoList);
                        index++;
                    }
                } else if ("fanchui6".equals(sheetSplit[1])) {
                    int index = 1;
                    for (int rowNum = 0; rowNum < size; rowNum++) {
                        DateQuery eachDate = dateQueries.get(rowNum);
                        List<CellData> cellValInfoList = mapDataHandler3(columns, getUrl(version), eachDate, index, version);
                        ExcelWriterUtil.setCellValue(sheet, cellValInfoList);
                        index++;
                    }
                } else if ("fanchui7".equals(sheetSplit[1])) {
                    int index = 1;
                    for (int rowNum = 0; rowNum < size; rowNum++) {
                        DateQuery eachDate = dateQueries.get(rowNum);
                        List<CellData> cellValInfoList = mapDataHandler4(columns, getUrl(version), eachDate, index, version);
                        ExcelWriterUtil.setCellValue(sheet, cellValInfoList);
                        index++;
                    }
                } else if ("fanchui8".equals(sheetSplit[1])) {
                    int index = 1;
                    for (int rowNum = 0; rowNum < size; rowNum++) {
                        DateQuery eachDate = dateQueries.get(rowNum);
                        List<CellData> cellValInfoList = mapDataHandler2(columns, getUrl(version), eachDate, index, version);
                        ExcelWriterUtil.setCellValue(sheet, cellValInfoList);
                        index++;
                    }
                }
            }
        }
        return workbook;
    }

    public List<CellData> mapDataHandler(List<String> columns, String url, DateQuery dateQuery, int rowBatch) {
        Map<String, String> queryParam = getQueryParam(dateQuery);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return null;
        }
        JSONArray r = data.getJSONArray("particleDistribution");
        if (Objects.isNull(r) || r.size() == 0) {
            return null;
        }
        int startRow = 1;
        return handlerJsonArray(columns, rowBatch, r, startRow);
    }

    private List<CellData> eachData(List<String> cellList, String url, Map<String, String> queryParam, String type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("starttime", queryParam.get("starttime"));
        jsonObject.put("endtime", queryParam.get("endtime"));
        jsonObject.put("tagnames", cellList);

        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        JSONObject obj = JSONObject.parseObject(result);
        obj = obj.getJSONObject("data");
        List<CellData> resultList = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < cellList.size(); columnIndex++) {
            String cell = cellList.get(columnIndex);
            if (StringUtils.isNotBlank(cell)) {
                JSONObject data = obj.getJSONObject(cell);
                if (Objects.nonNull(data)) {
                    Set<String> keys = data.keySet();
                    Long[] list = new Long[keys.size()];
                    int k = 0;
                    for (String key : keys) {
                        list[k] = Long.valueOf(key);
                        k++;
                    }
                    // 按照顺序排序
                    Arrays.sort(list);
                    if (StringUtils.isNotBlank(type)) {
                        if ("day".equals(type)) {
                            int size = list.length;
                            for (int i = 0; i < size; i++) {
                                Long key = list[i];
                                Date date = new Date(key);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                int rowIndex = calendar.get(Calendar.HOUR_OF_DAY);
                                if (rowIndex == 0) {
                                    if (i == size - 1) {
                                        rowIndex = 24;
                                    } else {
                                        continue;
                                    }
                                }
                                Object o = data.get(key + "");
                                ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, o);
                            }
                        } else if ("month".equals(type)) {
                            for (Long key : list) {
                                Date date = new Date(key);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                int rowIndex = calendar.get(Calendar.DATE);
                                Object o = data.get(key + "");
                                ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, o);
                            }
                        }
                    } else {
                        int rowIndex = 1;
                        for (Long key : list) {
                            Object o = data.get(key + "");
                            ExcelWriterUtil.addCellData(resultList, rowIndex++, columnIndex, o);
                        }
                    }

                }
            }
        }
        return resultList;
    }

    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();

        result.put("endtime", dateQuery.getQueryEndTime().toString());
        result.put("starttime", dateQuery.getQueryStartTime().toString());
        return result;
    }

    protected JSONObject getQueryParam(DateQuery dateQuery, List<String> tagNames) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("starttime", dateQuery.getStartTime().getTime());
        jsonObject.put("endtime", dateQuery.getEndTime().getTime());
        jsonObject.put("tagnames", tagNames);
        return jsonObject;
    }

    public List<CellData> mapDataHandler1(List<String> columns, String url, DateQuery dateQuery, int index) {
        List<CellData> resultList = new ArrayList<>();
        Map<String, String> queryParam = getQueryParam(dateQuery);
        queryParam.put("method", "max");
        queryParam.put("tagname", columns.get(0));
        String resultmax = httpUtil.get(url, queryParam);
        queryParam.put("method", "min");
        if (StringUtils.isNotBlank(resultmax)) {
            JSONObject jsonObject1 = JSONObject.parseObject(resultmax);
            if (Objects.nonNull(jsonObject1)) {
                Object v = jsonObject1.get("data");
                ExcelWriterUtil.addCellData(resultList, index, 0, v);
            }
        }
        String resultmin = httpUtil.get(url, queryParam);
        if (StringUtils.isNotBlank(resultmin)) {
            JSONObject jsonObject1 = JSONObject.parseObject(resultmin);
            if (Objects.nonNull(jsonObject1)) {
                Object v = jsonObject1.get("data");
                ExcelWriterUtil.addCellData(resultList, index, 1, v);
            }
        }
        return resultList;
    }

    public List<CellData> mapDataHandler2(List<String> columns, String url, DateQuery dateQuery, int index, String version) {
        List<CellData> resultList = new ArrayList<>();
        List<String> c = new ArrayList<>();
        c.add(columns.get(0));
        c.add(columns.get(1));
        JSONObject jsonObject = getQueryParam(dateQuery, c);

        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        if (StringUtils.isNotBlank(result)) {
            JSONObject obj = JSONObject.parseObject(result);
            obj = obj.getJSONObject("data");

            JSONObject jsonObject1 = obj.getJSONObject(columns.get(0));
            JSONObject jsonObject2 = obj.getJSONObject(columns.get(1));
            if (Objects.nonNull(jsonObject1) && Objects.nonNull(jsonObject2)) {
                Map<String, Object> innerMap1 = jsonObject1.getInnerMap();
                Map<String, Object> innerMap2 = jsonObject2.getInnerMap();
                Set<String> keyset = innerMap1.keySet();
                List<Integer> list1 = new ArrayList<>();
                Long[] dateSort = new Long[keyset.size()];
                int k = 0;
                for (String key : keyset) {
                    Object o = innerMap1.get(key);
                    BigDecimal o1 = (BigDecimal) o;
                    list1.add(o1.intValue());
                    dateSort[k] = Long.valueOf(key);
                    k++;
                }
                // 按照顺序排序
                Arrays.sort(dateSort);

                Set<String> keyset2 = innerMap2.keySet();
                List<Integer> list2 = new ArrayList<>();
                Long[] dateSort2 = new Long[keyset2.size()];
                int l = 0;
                for (String key : keyset2) {
                    Object o = innerMap2.get(key);
                    BigDecimal o1 = (BigDecimal) o;
                    list2.add(o1.intValue());
                    dateSort2[l] = Long.valueOf(key);
                    l++;
                }
                // 按照顺序排序
                Arrays.sort(dateSort2);

                int indexkey = -1;
                for (int i = 0; i < list1.size(); i++) {
                    if (list1.get(i) * list2.get(i) == 1) {
                        indexkey = i;
                        break;
                    }
                }

                String indextime = "";
//                Object[] objects = keyset2.toArray();
                for (int i = 0; i < dateSort2.length; i++) {
                    if (i == indexkey) {
                        indextime = String.valueOf(dateSort2[i]);
                        break;
                    }
                }

                if (StringUtils.isNotBlank(indextime)) {
                    ExcelWriterUtil.addCellData(resultList, index, 1, Long.valueOf(indextime));
                    Object o2 = dealPart1(getUrl3(version), indextime, columns.get(2), -1);
                    Object o3 = dealPart1(getUrl3(version), indextime, columns.get(3), -1);

                    ExcelWriterUtil.addCellData(resultList, index, 2, o2);
                    ExcelWriterUtil.addCellData(resultList, index, 3, o3);

                    int indexkey2 = -1;
                    for (int i = indexkey; i < list1.size(); i++) {
                        if (list1.get(i) * list2.get(i) == 0) {
                            indexkey2 = i;
                            break;
                        }
                    }

                    String indextime2 = "";
                  //  Object[] objects2 = keyset2.toArray();
                    for (int i = 0; i < dateSort2.length; i++) {
                        if (i == indexkey2) {
                            indextime2 = String.valueOf(dateSort2[i]);
                            break;
                        }
                    }

                    if (StringUtils.isNotBlank(indextime2)) {
                        Object o4 = dealPart1(getUrl3(version), indextime, columns.get(4), 1);
                        Object o5 = dealPart1(getUrl3(version), indextime, columns.get(5), 1);

                        ExcelWriterUtil.addCellData(resultList, index, 4, o4);
                        ExcelWriterUtil.addCellData(resultList, index, 5, o5);
                    }
                }

            }

        }

        return resultList;
    }


    public List<CellData> mapDataHandler4(List<String> columns, String url, DateQuery dateQuery, int index, String version) {
        List<CellData> resultList = new ArrayList<>();
        List<String> c = new ArrayList<>();
        c.add(columns.get(0));
        c.add(columns.get(1));
        JSONObject jsonObject = getQueryParam(dateQuery, c);

        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        if (StringUtils.isNotBlank(result)) {
            JSONObject obj = JSONObject.parseObject(result);
            obj = obj.getJSONObject("data");

            JSONObject jsonObject1 = obj.getJSONObject(columns.get(0));
            JSONObject jsonObject2 = obj.getJSONObject(columns.get(1));
            if (Objects.nonNull(jsonObject1) && Objects.nonNull(jsonObject2)) {
                Map<String, Object> innerMap1 = jsonObject1.getInnerMap();
                Map<String, Object> innerMap2 = jsonObject2.getInnerMap();
                Set<String> keyset = innerMap1.keySet();
                List<Integer> list1 = new ArrayList<>();
                Long[] dateSort = new Long[keyset.size()];
                int k = 0;
                for (String key : keyset) {
                    Object o = innerMap1.get(key);
                    BigDecimal o1 = (BigDecimal) o;
                    list1.add(o1.intValue());
                    dateSort[k] = Long.valueOf(key);
                    k++;
                }
                // 按照顺序排序
                Arrays.sort(dateSort);

                Set<String> keyset2 = innerMap2.keySet();
                List<Integer> list2 = new ArrayList<>();
                Long[] dateSort2 = new Long[keyset2.size()];
                int l = 0;
                for (String key : keyset2) {
                    Object o = innerMap2.get(key);
                    BigDecimal o1 = (BigDecimal) o;
                    list2.add(o1.intValue());
                    dateSort2[l] = Long.valueOf(key);
                    l++;
                }
                // 按照顺序排序
                Arrays.sort(dateSort2);

                int indexkey = -1;
                for (int i = 0; i < list1.size(); i++) {
                    if (list1.get(i) * list2.get(i) == 1) {
                        indexkey = i;
                        break;
                    }
                }

                String indextime = "";
//                Object[] objects = keyset2.toArray();
                for (int i = 0; i < dateSort2.length; i++) {
                    if (i == indexkey) {
                        indextime = String.valueOf(dateSort2[i]);
                        break;
                    }
                }

                if (StringUtils.isNotBlank(indextime)) {
                    ExcelWriterUtil.addCellData(resultList, index, 1, Long.valueOf(indextime));
                    Object o2 = dealPart1(getUrl3(version), indextime, columns.get(2), -1);
                    Object o3 = dealPart1(getUrl3(version), indextime, columns.get(3), -1);
                    Object o4 = dealPart1(getUrl3(version), indextime, columns.get(4), -1);
                    Object o5 = dealPart1(getUrl3(version), indextime, columns.get(5), -1);
                    Object o6 = dealPart1(getUrl3(version), indextime, columns.get(6), -1);

                    ExcelWriterUtil.addCellData(resultList, index, 2, o2);
                    ExcelWriterUtil.addCellData(resultList, index, 3, o3);
                    ExcelWriterUtil.addCellData(resultList, index, 4, o4);
                    ExcelWriterUtil.addCellData(resultList, index, 5, o5);
                    ExcelWriterUtil.addCellData(resultList, index, 6, o6);

                    int indexkey2 = -1;
                    for (int i = indexkey; i < list1.size(); i++) {
                        if (list1.get(i) * list2.get(i) == 0) {
                            indexkey2 = i;
                            break;
                        }
                    }

                    String indextime2 = "";
                    //  Object[] objects2 = keyset2.toArray();
                    for (int i = 0; i < dateSort2.length; i++) {
                        if (i == indexkey2) {
                            indextime2 = String.valueOf(dateSort2[i]);
                            break;
                        }
                    }

                    if (StringUtils.isNotBlank(indextime2)) {
                        Object o7 = dealPart1(getUrl3(version), indextime, columns.get(7), 1);
                        Object o8 = dealPart1(getUrl3(version), indextime, columns.get(8), 1);
                        Object o9 = dealPart1(getUrl3(version), indextime, columns.get(9), 1);
                        Object o10 = dealPart1(getUrl3(version), indextime, columns.get(10), 1);
                        Object o11 = dealPart1(getUrl3(version), indextime, columns.get(11), 1);


                        ExcelWriterUtil.addCellData(resultList, index, 7, o7);
                        ExcelWriterUtil.addCellData(resultList, index, 8, o8);
                        ExcelWriterUtil.addCellData(resultList, index, 9, o9);
                        ExcelWriterUtil.addCellData(resultList, index, 10, o10);
                        ExcelWriterUtil.addCellData(resultList, index, 11, o11);
                    }
                }

            }

        }

        return resultList;
    }


    public List<CellData> mapDataHandler3(List<String> columns, String url, DateQuery dateQuery, int index, String version) {
        List<CellData> resultList = new ArrayList<>();
        List<String> c = new ArrayList<>();
        c.add(columns.get(0));
        JSONObject jsonObject = getQueryParam(dateQuery, c);
        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        if (StringUtils.isNotBlank(result)) {
            JSONObject obj = JSONObject.parseObject(result);
            obj = obj.getJSONObject("data");
            JSONObject jsonObject1 = obj.getJSONObject(columns.get(0));
            if (Objects.nonNull(jsonObject1)) {
                Map<String, Object> innerMap1 = jsonObject1.getInnerMap();
                Set<String> keyset = innerMap1.keySet();
                List<Integer> list1 = new ArrayList<>();
                Long[] dateSort = new Long[keyset.size()];
                int k = 0;
                for (String key : keyset) {
                    Object o = innerMap1.get(key);
                    BigDecimal o1 = (BigDecimal) o;
                    list1.add(o1.intValue());
                    dateSort[k] = Long.valueOf(key);
                    k++;
                }
                // 按照顺序排序
                Arrays.sort(dateSort);
                int indexkey = -1;
                for (int i = 0; i < list1.size(); i++) {
                    if (list1.get(i) == 1) {
                        indexkey = i;
                        break;
                    }
                }

                String indextime = "";
              //  Object[] objects = keyset.toArray();
                for (int i = 0; i < dateSort.length; i++) {
                    if (i == indexkey) {
                        indextime = String.valueOf(dateSort[i]);
                        break;
                    }
                }

                if (StringUtils.isNotBlank(indextime)) {
                    ExcelWriterUtil.addCellData(resultList, index, 0, Long.valueOf(indextime));
                    Object o1 = dealPart1(getUrl3(version), indextime, columns.get(1), -1);
                    Object o2 = dealPart1(getUrl3(version), indextime, columns.get(2), -1);
                    ExcelWriterUtil.addCellData(resultList, index, 1, o1);
                    ExcelWriterUtil.addCellData(resultList, index, 2, o2);

                    int indexkey2 = -1;
                    for (int i = indexkey; i < list1.size(); i++) {
                        if (list1.get(i) == 0) {
                            indexkey2 = i;
                            break;
                        }
                    }

                    String indextime2 = "";
                   // Object[] objects2 = keyset.toArray();
                    for (int i = 0; i < dateSort.length; i++) {
                        if (i == indexkey2) {
                            indextime2 = String.valueOf(dateSort[i]);
                            break;
                        }
                    }

                    if (StringUtils.isNotBlank(indextime2)) {
                        Object o3 = dealPart1(getUrl3(version), indextime, columns.get(3), 1);
                        Object o4 = dealPart1(getUrl3(version), indextime, columns.get(4), 1);

                        ExcelWriterUtil.addCellData(resultList, index, 3, o3);
                        ExcelWriterUtil.addCellData(resultList, index, 4, o4);
                    }
                }


            }
        }

        return resultList;
    }

    private Object dealPart1(String url, String indextime, String tagName, int min) {
        long value = Long.valueOf(indextime);
        Date date = new Date(value);
        Date curr = DateUtil.addMinute(date, min);
        Object v = null;
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("time", curr.getTime() + "");
        queryParam.put("tagname", tagName);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    v = data.get("val");
                }
            }
        }
        return v;
    }

    protected String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }

    protected String getUrl2(String version) {
        return httpProperties.getGlUrlVersion(version) + "/tagValueAction";
    }

    protected String getUrl3(String version) {
        return httpProperties.getGlUrlVersion(version) + "/tagValue/latest";
    }
}
