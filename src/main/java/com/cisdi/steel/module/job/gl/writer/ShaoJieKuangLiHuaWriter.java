package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>Description: 烧结矿理化指标处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/22 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class ShaoJieKuangLiHuaWriter extends AbstractExcelReadWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());

        // 默认8号高炉，从模板中获取version
        String version ="8.0";
        try{
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        }catch(Exception e){
            log.info("从模板中获取version失败", e);
        }

        // 获取时间策略
        DateQuery date = this.getDateQuery(excelDTO);
        // 获取API url
        String url = getUrl(version);
        Sheet sheet = workbook.getSheetAt(0);
        List<String> columns = PoiCustomUtil.getRowCelVal(sheet, 3);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet dateSheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表 待处理
            String sheetName = dateSheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                int size = dateQueries.size();
                for (int j = 0; j < size; j++) {

                    DateQuery item = dateQueries.get(j);
                    if (item.getRecordDate().before(new Date())) {
                        // 获取查询参数
                        Map<String, String> queryParam = getQueryParam(item);
                        String result = httpUtil.get(url, queryParam);
                        JSONObject jsonObject = JSONObject.parseObject(result);
                        List<CellData> cellDataList = this.mapDataHandler(jsonObject, columns);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    } else {
                        break;
                    }
                }
            }
        }

        return workbook;
    }

    /**
     * 组装List<CellData>
     * @param jsonObject
     * @param columns
     * @return
     */
    protected List<CellData> mapDataHandler(JSONObject jsonObject, List<String> columns) {
        List<CellData> cellDataList = new ArrayList<>();

        if (Objects.nonNull(jsonObject)) {
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");
            if (Objects.nonNull(jsonArray) && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++){
                    // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                    JSONObject json = jsonArray.getJSONObject(i);

                    JSONObject categoriesJson = (JSONObject) json.get("categories");
                    JSONObject sinterJson = (JSONObject) categoriesJson.get("SINTER");
                    JSONObject sinterAnaTypesJson = (JSONObject) sinterJson.get("anaTypes");

                    JSONObject pelletsJson = (JSONObject) categoriesJson.get("PELLETS");
                    JSONObject pelletsAnaTypesJson = (JSONObject) pelletsJson.get("anaTypes");

                    JSONObject lumporeJson = (JSONObject) categoriesJson.get("LUMPORE");
                    JSONObject lumporeAnaTypesJson = (JSONObject) lumporeJson.get("anaTypes");

                    if (Objects.nonNull(columns) && !columns.isEmpty()) {
                        int size = columns.size();
                        for (int j = 0; j < size; j++) {
                            String column = columns.get(j);
                            if (StringUtils.isNotBlank(column)) {
                                String[] columnSplit = column.split("_");
                                Double doubleValue = 0.0d;
                                // 处理模板中sj开头的参数
                                if ("sj".equals(columnSplit[0])) {
                                    String sjColumn = columnSplit[1];
                                    JSONObject lpJson = (JSONObject) sinterAnaTypesJson.get("LP");
                                    JSONObject lcJson = (JSONObject) sinterAnaTypesJson.get("LC");
                                    JSONObject lgJson = (JSONObject) sinterAnaTypesJson.get("LG");
                                    Object valueObject = lpJson.get(sjColumn);
                                    if (Objects.isNull(valueObject)) {
                                        valueObject = lcJson.get(sjColumn);
                                        if (Objects.isNull(valueObject)) {
                                            valueObject = lgJson.get(sjColumn);
                                        }
                                    }
                                    if (Objects.nonNull(valueObject)) {
                                        String value = String.valueOf(valueObject);
                                        doubleValue = Double.parseDouble(value);
                                    }
                                    // 处理模板中qt开头的参数
                                } else if ("qt".equals(columnSplit[0])) {
                                    String qtColumn = columnSplit[1];
                                    JSONObject lcJson = (JSONObject) pelletsAnaTypesJson.get("LC");
                                    JSONObject lgJson = (JSONObject) pelletsAnaTypesJson.get("LG");
                                    Object valueObject = lcJson.get(qtColumn);
                                    if (Objects.isNull(valueObject)) {
                                        valueObject = lgJson.get(qtColumn);
                                    }
                                    if (Objects.nonNull(valueObject)) {
                                        String value = String.valueOf(valueObject);
                                        doubleValue = Double.parseDouble(value);
                                    }
                                    // 处理模板中kk开头的参数
                                } else if ("kk".equals(columnSplit[0])) {
                                    String kkColumn = columnSplit[1];
                                    JSONObject lpJson = (JSONObject) lumporeAnaTypesJson.get("LP");
                                    JSONObject lcJson = (JSONObject) lumporeAnaTypesJson.get("LC");
                                    JSONObject lgJson = (JSONObject) lumporeAnaTypesJson.get("LG");
                                    Object valueObject = lpJson.get(kkColumn);
                                    if (Objects.isNull(valueObject)) {
                                        valueObject = lcJson.get(kkColumn);
                                        if (Objects.isNull(valueObject)) {
                                            valueObject = lgJson.get(kkColumn);
                                        }
                                    }
                                    if (Objects.nonNull(valueObject)) {
                                        String value = String.valueOf(valueObject);
                                        doubleValue = Double.parseDouble(value);
                                    }
                                }
                                ExcelWriterUtil.addCellData(cellDataList, 3 + i, j, doubleValue);
                            }
                        }
                    }
                }
            }
        }

        return cellDataList;
    }

    /**
     * 组装API url
     * @param version
     * @return
     */
    protected String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/analysisCharges";
    }
}
