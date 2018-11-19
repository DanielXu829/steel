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
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 原料车间生产运行记录
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class YuanliaochejianyunxingjiluWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        return this.getMapHandler2(getUrl(), 1, excelDTO);
    }

    private Workbook getMapHandler2(String url, Integer rowBatch, WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                DateQuery item = new DateQuery(new Date());
                item.setStartTime(DateUtil.getTodayBeginTime());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                List<CellData> cellDataList = this.mapDataHandler(url, sheetName, columns, item, rowBatch);
                ExcelWriterUtil.setCellValue(sheet, cellDataList);
            }
        }
        return workbook;
    }


    private List<CellData> mapDataHandler(String url, String sheetName, List<String> columns, DateQuery dateQuery, int rowBatch) {
        Map<String, String> queryParam = this.getQueryParam(dateQuery);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return null;
        }

        if ("_yglmain_day_shift".equals(sheetName)) {
            data.remove("a2");
            data.remove("a3");
            data.remove("a4");
            data.remove("a5");
            data.remove("a6");
            data.remove("a7");
        } else if ("_ygl1_day_shift".equals(sheetName)) {
            data.remove("a1");
            data.remove("a3");
            data.remove("a4");
            data.remove("a5");
            data.remove("a6");
            data.remove("a7");
        } else if ("_ygl2_day_shift".equals(sheetName)) {
            data.remove("a1");
            data.remove("a2");
            data.remove("a4");
            data.remove("a5");
            data.remove("a6");
            data.remove("a7");
        }else if ("_ygl3_day_shift".equals(sheetName)) {
            data.remove("a1");
            data.remove("a2");
            data.remove("a3");
            data.remove("a5");
            data.remove("a6");
            data.remove("a7");
        }else if ("_ygl4_day_shift".equals(sheetName)) {
            data.remove("a1");
            data.remove("a2");
            data.remove("a3");
            data.remove("a4");
            data.remove("a6");
            data.remove("a7");
        }else if ("_ygl5_day_shift".equals(sheetName)) {
            data.remove("a1");
            data.remove("a2");
            data.remove("a3");
            data.remove("a4");
            data.remove("a5");
            data.remove("a7");
        }else if ("_ygl6_day_shift".equals(sheetName)) {
            data.remove("a1");
            data.remove("a2");
            data.remove("a3");
            data.remove("a4");
            data.remove("a5");
            data.remove("a6");
        }

        int startRow = 1;
        return this.handlerJsonArray(columns, rowBatch, data, startRow);
    }

    protected List<CellData> handlerJsonArray(List<String> columns, int rowBatch, JSONObject data, int startRow) {
        List<CellData> cellDataList = new ArrayList<>();
        Set<String> keySet = data.keySet();
        for (String k : keySet) {
            JSONArray map = data.getJSONArray(k);
            for (int i = 0; i < map.size(); i++) {
                if (Objects.nonNull(map)) {
                    List<CellData> cellDataList1 = ExcelWriterUtil.handlerRowData(columns, startRow, map.getJSONObject(i));
                    cellDataList.addAll(cellDataList1);
                }
                startRow += rowBatch;
            }
        }

        return cellDataList;
    }

    public Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> map = new HashMap<>();
//        map.put("shiftday", String.valueOf(Objects.requireNonNull(dateQuery.getStartTime()).getTime()));
        map.put("shiftday", "1542012527000");
        return map;
    }

    private String getUrl() {
        return httpProperties.getUrlApiYGLOne() + "/reportManager/getReport12Data";
    }
}
