package com.cisdi.steel.module.job.a1.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 高炉炉顶布料
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class LudingbuliaoWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        return getMapHandler(getUrl(version), 6, excelDTO);
    }

    @Override
    protected List<CellData> mapDataHandler(String url, List<String> columns, DateQuery dateQuery, int rowBatch) {
        Map<String, String> queryParam = dateQuery.getQueryParam();
        queryParam.put("pagesize", "1");
        String s = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(s)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(s);
        if (Objects.isNull(jsonObject)) {
            return null;
        }
        Integer total = jsonObject.getInteger("total");
        queryParam.put("pagesize", total + "");
        String resultData = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(resultData)) {
            return null;
        }
        JSONObject obj = JSONObject.parseObject(resultData);
        JSONArray data = obj.getJSONArray("data");
        if (Objects.isNull(data)) {
            return null;
        }
        int size = data.size();
        int rowIndex = 1;
        List<CellData> cellDataList = new ArrayList<>();
        for (int i = size - 1; i >= 0; i--) {
            JSONObject map = data.getJSONObject(i);
            int columnIndex = 0;
            // 隐藏的行
            int index = 0;
            for (String column : columns) {
                if (StringUtils.isBlank(column)) {
                    continue;
                }
                String[] columnSplit = column.split("/");
                String key = columnSplit[0];
                String child = columnSplit[0];
                if (columnSplit.length > 1) {
                    child = columnSplit[1];
                }

                if ("batchDistributions".equals(key)) {
                    JSONArray jsonArray = map.getJSONArray(key);
                    if (Objects.nonNull(jsonArray)) {
                        int size1 = jsonArray.size();
                        for (int j = 0; j < size1; j++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                            if (Objects.nonNull(jsonObject1)) {
                                BigDecimal position = jsonObject1.getBigDecimal("position");
                                if (child.equals(position.toString())) {
                                    BigDecimal val1 = jsonObject1.getBigDecimal("angleset");
                                    BigDecimal val2 = jsonObject1.getBigDecimal("angleact");
                                    BigDecimal val3 = jsonObject1.getBigDecimal("roundset");
                                    BigDecimal val4 = jsonObject1.getBigDecimal("roundact");
                                    BigDecimal val5 = jsonObject1.getBigDecimal("weightset");
                                    BigDecimal val6 = jsonObject1.getBigDecimal("weightact");
                                    int row = rowIndex;
                                    if (Objects.nonNull(val3) && val3.intValue() != 0) {
                                        int newColumnIndex = columnIndex - index;
                                        ExcelWriterUtil.addCellData(cellDataList, row++, newColumnIndex, val1);
                                        ExcelWriterUtil.addCellData(cellDataList, row++, newColumnIndex, val2);
                                        ExcelWriterUtil.addCellData(cellDataList, row++, newColumnIndex, val3);
                                        ExcelWriterUtil.addCellData(cellDataList, row++, newColumnIndex, val4);
                                        ExcelWriterUtil.addCellData(cellDataList, row++, newColumnIndex, val5);
                                        ExcelWriterUtil.addCellData(cellDataList, row, newColumnIndex, val6);
                                    } else {
                                        index++;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (columnSplit.length > 1) {
                        JSONObject keyData = map.getJSONObject(key);
                        if (Objects.nonNull(keyData)) {
                            Object value = keyData.get(child);
                            ExcelWriterUtil.addCellData(cellDataList, rowIndex, columnIndex, value);
                        }
                    } else {
                        Object value = map.get(key);
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, columnIndex, value);
                    }
                }
                columnIndex++;
            }
            rowIndex += rowBatch;

        }

        return cellDataList;
    }

    private String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/batches/distribution/period";
    }
}
