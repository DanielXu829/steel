package com.cisdi.steel.module.job.a4.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/14 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GongliaochejianyichangJobWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        return getMapHandler(getUrl(), 4, excelDTO);
    }


    /**
     * 处理返回的 json格式
     *
     * @param columns  列名
     * @param rowBatch 占用多少行
     * @param data     数据
     * @param startRow 开始行
     * @return 结果
     */
    @Override
    protected List<CellData> handlerJsonArray(List<String> columns, int rowBatch, JSONArray data, int startRow) {
        List<CellData> cellDataList = new ArrayList<>();
        int size = data.size();
        for (int i = 0; i < size; i++) {
            JSONObject map = data.getJSONObject(i);
            if (Objects.nonNull(map)) {
                List<CellData> cellDataList1 = handlerRowData(columns, startRow, map);
                cellDataList.addAll(cellDataList1);
            }
            startRow += rowBatch;
        }
        return cellDataList;
    }


    public static List<CellData> handlerRowData(List<String> columns, int starRow, Map<String, Object> rowData) {
        List<CellData> resultData = new ArrayList<>();
        int size = columns.size();
        // 忽略大小写
        CaseInsensitiveMap<String, Object> rowDataMap = new CaseInsensitiveMap<>(rowData);
        for (int columnIndex = 0; columnIndex < size; columnIndex++) {
            String column = columns.get(columnIndex);
            if (StringUtils.isBlank(column)) {
                continue;
            }
            if (!column.contains("/")) {
                Object value = rowDataMap.get(column);
                ExcelWriterUtil.addCellData(resultData, starRow, columnIndex, value);
            } else {
                String[] split = column.split("/");
                String key = split[0];
                String keyChild = split[1];
                Object o = rowDataMap.get(key);
                if (o instanceof JSONObject) {
                    JSONObject object = (JSONObject) o;
                    CaseInsensitiveMap<String, Object> map = new CaseInsensitiveMap<>(object);
                    Object value = map.get(keyChild);
                    ExcelWriterUtil.addCellData(resultData, starRow, columnIndex, value);
                } else if (o instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) o;
                    int childIndex = starRow;
                    int col = columnIndex;
                    int max = jsonArray.size();
                    for (int i = 0; i < max; i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        CaseInsensitiveMap<String, Object> map = new CaseInsensitiveMap<>(item);
                        Object value = map.get(keyChild);
                        ExcelWriterUtil.addCellData(resultData, childIndex, col, value);
                        if ((i + 1) % 3 != 0) {
                            col += 2;
                        } else {
                            col = columnIndex;
                            childIndex++;
                        }
                    }
                }
            }
        }
        return resultData;
    }


    private String getUrl() {
        return httpProperties.getUrlApiYGLOne() + "/reportManager/getReport7Data";
    }
}
