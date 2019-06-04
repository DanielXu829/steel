package com.cisdi.steel.module.job.a4.writer;

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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * 供料准时化横班统计报表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GongLiaoZhunShiHuaWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
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
                DateQuery dateQuery = dateQueries.get(0);
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                if ("sinter".equals(sheetSplit[1])) {
                    List<CellData> cellDataList = mapDataHandler(getUrl(), dateQuery, columns);
                    Double fengPrice = handlerPrice(getUrl3());
                    setSheetValue(sheet,1,25,fengPrice);
                    Double pinPrcie = handlerPrice(getUrl4());
                    setSheetValue(sheet,1,26,pinPrcie);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else if ("coke".equals(sheetSplit[1])) {
                    List<CellData> cellDataList = mapDataHandler(getUrl1(), dateQuery, columns);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else if ("lumpore".equals(sheetSplit[1])) {
                    List<CellData> cellDataList = mapDataHandler(getUrl2(), dateQuery, columns);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }
        return workbook;
    }

    private void setSheetValue(Sheet sheet, Integer rowNum, Integer columnNum, Object obj) {
        Row row = sheet.getRow(rowNum);
        if (Objects.isNull(row)) {
            row = sheet.createRow(rowNum);
        }
        Cell cell = row.getCell(columnNum);
        if (Objects.isNull(cell)) {
            cell = row.createCell(columnNum);
        }
        PoiCustomUtil.setCellValue(cell, obj);
    }

    protected Double handlerPrice(String url) {
        String result = httpUtil.get(url);
        Double data = 0.0;
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                data = jsonObject.getDouble("data");
            }
        }
        return data;
    }


    protected List<CellData> mapDataHandler(String url, DateQuery dateQuery, List<String> columns) {
        Map<String, String> queryParam = this.getQueryParam(dateQuery);
        String result = httpUtil.get(url, queryParam);
        List<CellData> cellData = new ArrayList<>();
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONArray data = jsonObject.getJSONArray("data");
                if (Objects.nonNull(data) && data.size() > 0) {
                    LinkedHashMap<String, List<JSONObject>> maps = new LinkedHashMap<>();
                    for (int i = 0; i < data.size(); i++) {
                        JSONObject object = data.getJSONObject(i);
                        List<JSONObject> list = new ArrayList();
                        String key = object.getString("SHIFT_DAY") + object.getString("SHIFT_NO");
                        if (Objects.isNull(maps.get(key))) {
                            list.add(object);
                            maps.put(key, list);
                        } else {
                            list.addAll(maps.get(key));
                            list.add(object);
                            maps.put(key, list);
                        }
                    }
                    Set<Map.Entry<String, List<JSONObject>>> set = maps.entrySet();
                    Iterator<Map.Entry<String, List<JSONObject>>> iterator = set.iterator();
                    int num = 1;
                    while (iterator.hasNext()) {
                        Map.Entry<String, List<JSONObject>> next = iterator.next();
                        List<JSONObject> value = next.getValue();
                        Map<String, Object> mapValue = getMapValue(value);
                        List<CellData> cellDataList = ExcelWriterUtil.handlerRowData(columns, num, mapValue);
                        cellData.addAll(cellDataList);
                        num += 1;
                    }
                }
            }
        }

        return cellData;
    }

    private Map<String, Object> getMapValue(List<JSONObject> value) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < value.size(); i++) {
            JSONObject object = value.get(i);
            String sub = StringUtils.substring(object.getString("END_ADDRESS"), 0, 1);
            if ("6".equals(sub)) {
                map.put("shiftDay", object.getString("SHIFT_DAY"));
                map.put("shiftNo", object.getInteger("SHIFT_NO"));
                map.put("shiftTeam", object.getString("SHIFT_TEAM"));
                map.put("jhDianhao6", object.getDouble("JHDIANHAO"));
                map.put("sjDianhao6", object.getDouble("SJDIANHAO"));
                map.put("slTime6", object.getDouble("SLTIME"));
                map.put("slWgt6", object.getDouble("SLWGT"));
                map.put("eleNum6", object.getDouble("ELENUM"));
                if (Objects.isNull(map.get("fengTime"))) {
                    Integer fengtime = object.getInteger("FENGTIME");
                    if (Objects.isNull(fengtime)) {
                        fengtime = 0;
                    }
                    map.put("fengTime", fengtime);
                }
                if (Objects.isNull(map.get("pinTime"))) {
                    Integer pinTime = object.getInteger("PINTIME");
                    if (Objects.isNull(pinTime)) {
                        pinTime = 0;
                    }
                    map.put("pinTime", pinTime);
                }
                if (Objects.isNull(map.get("guTime"))) {
                    Integer guTime = object.getInteger("GUTIME");
                    if (Objects.isNull(guTime)) {
                        guTime = 0;
                    }
                    map.put("guTime", guTime);
                }
                if (Objects.isNull(map.get("fengDianhao"))) {
                    Double fengDianhao = object.getDouble("FENGDH");
                    if (Objects.isNull(fengDianhao)) {
                        fengDianhao = 0.0;
                    }
                    map.put("fengDianhao", fengDianhao);
                }
                if (Objects.isNull(map.get("pinDianhao"))) {
                    Double pinDianhao = object.getDouble("PINDH");
                    if (Objects.isNull(pinDianhao)) {
                        pinDianhao = 0.0;
                    }
                    map.put("pinDianhao", pinDianhao);
                }
                if (Objects.isNull(map.get("guDianhao"))) {
                    Double guDianhao = object.getDouble("GUDH");
                    if (Objects.isNull(guDianhao)) {
                        guDianhao = 0.0;
                    }
                    map.put("guDianhao", guDianhao);
                }
            } else if ("7".equals(sub)) {
                map.put("jhDianhao7", object.getDouble("JHDIANHAO"));
                map.put("sjDianhao7", object.getDouble("SJDIANHAO"));
                map.put("slTime7", object.getDouble("SLTIME"));
                map.put("slWgt7", object.getDouble("SLWGT"));
                map.put("eleNum7", object.getDouble("ELENUM"));
                if (Objects.nonNull(map.get("fengTime"))) {
                    Integer fengtime = object.getInteger("FENGTIME");
                    if (Objects.isNull(fengtime)) {
                        fengtime = 0;
                    }
                    Integer v = (Integer) map.get("fengTime") + fengtime;
                    map.put("fengTime", v);
                }
                if (Objects.nonNull(map.get("pinTime"))) {
                    Integer pinTime = object.getInteger("PINTIME");
                    if (Objects.isNull(pinTime)) {
                        pinTime = 0;
                    }
                    Integer v = (Integer) map.get("pinTime") + pinTime;
                    map.put("pinTime", v);
                }
                if (Objects.nonNull(map.get("guTime"))) {
                    Integer gutime = object.getInteger("GUTIME");
                    if (Objects.isNull(gutime)) {
                        gutime = 0;
                    }
                    Integer v = (Integer) map.get("guTime") + gutime;
                    map.put("guTime", v);
                }
                if (Objects.nonNull(map.get("fengDianhao"))) {
                    Double fengdh = object.getDouble("FENGDH");
                    if (Objects.isNull(fengdh)) {
                        fengdh = 0.0;
                    }
                    Double v = (Double) map.get("fengDianhao") + fengdh;
                    map.put("fengDianhao", v);
                }
                if (Objects.nonNull(map.get("pinDianhao"))) {
                    Double pindh = object.getDouble("PINDH");
                    if (Objects.isNull(pindh)) {
                        pindh = 0.0;
                    }
                    Double v = (Double) map.get("pinDianhao") + pindh;
                    map.put("pinDianhao", v);
                }
                if (Objects.nonNull(map.get("guDianhao"))) {
                    Double gudh = object.getDouble("GUDH");
                    if (Objects.isNull(gudh)) {
                        gudh = 0.0;
                    }
                    Double v = (Double) map.get("guDianhao") + gudh;
                    map.put("guDianhao", v);
                }
            } else if ("8".equals(sub)) {
                map.put("jhDianhao8", object.getDouble("JHDIANHAO"));
                map.put("sjDianhao8", object.getDouble("SJDIANHAO"));
                map.put("slTime8", object.getDouble("SLTIME"));
                map.put("slWgt8", object.getDouble("SLWGT"));
                map.put("eleNum8", object.getDouble("ELENUM"));
                if (Objects.nonNull(map.get("fengTime"))) {
                    Integer fengtime = object.getInteger("FENGTIME");
                    if (Objects.isNull(fengtime)) {
                        fengtime = 0;
                    }
                    Integer v = (Integer) map.get("fengTime") + fengtime;
                    map.put("fengTime", v);
                }
                if (Objects.nonNull(map.get("pinTime"))) {
                    Integer pinTime = object.getInteger("PINTIME");
                    if (Objects.isNull(pinTime)) {
                        pinTime = 0;
                    }
                    Integer v = (Integer) map.get("pinTime") + pinTime;
                    map.put("pinTime", v);
                }
                if (Objects.nonNull(map.get("guTime"))) {
                    Integer gutime = object.getInteger("GUTIME");
                    if (Objects.isNull(gutime)) {
                        gutime = 0;
                    }
                    Integer v = (Integer) map.get("guTime") + gutime;
                    map.put("guTime", v);
                }
                if (Objects.nonNull(map.get("fengDianhao"))) {
                    Double fengdh = object.getDouble("FENGDH");
                    if (Objects.isNull(fengdh)) {
                        fengdh = 0.0;
                    }
                    Double v = (Double) map.get("fengDianhao") + fengdh;
                    map.put("fengDianhao", v);
                }
                if (Objects.nonNull(map.get("pinDianhao"))) {
                    Double pindh = object.getDouble("PINDH");
                    if (Objects.isNull(pindh)) {
                        pindh = 0.0;
                    }
                    Double v = (Double) map.get("pinDianhao") + pindh;
                    map.put("pinDianhao", v);
                }
                if (Objects.nonNull(map.get("guDianhao"))) {
                    Double gudh = object.getDouble("GUDH");
                    if (Objects.isNull(gudh)) {
                        gudh = 0.0;
                    }
                    Double v = (Double) map.get("guDianhao") + gudh;
                    map.put("guDianhao", v);
                }
            }
        }
        return map;
    }

    @Override
    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("start", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyyMMdd"));
        result.put("end", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyyMMdd"));
        return result;
    }


    private String getUrl() {
        return httpProperties.getUrlApiYGLOne() + "/reportManager/feedSinter";
    }

    private String getUrl1() {
        return httpProperties.getUrlApiYGLOne() + "/reportManager/feedCoke";
    }

    private String getUrl2() {
        return httpProperties.getUrlApiYGLOne() + "/reportManager/feedLumpOre";
    }

    private String getUrl3() {
        return httpProperties.getUrlApiYGLOne() + "/reportManager/feedLFengPrice";
    }

    private String getUrl4() {
        return httpProperties.getUrlApiYGLOne() + "/reportManager/feedLPinPrice";
    }

}
