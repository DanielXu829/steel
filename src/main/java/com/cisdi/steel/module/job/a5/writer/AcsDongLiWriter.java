package com.cisdi.steel.module.job.a5.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 动力分厂
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class AcsDongLiWriter extends AbstractExcelReadWriter {
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
                List<Cell> columns = PoiCustomUtil.getFirstRowCel(sheet);
                int rowIndex = 1;
                for (DateQuery dateQuery : dateQueries) {
                    List<CellData> cellDataList = this.eachData(columns, getUrl(), getQueryParam(dateQuery), rowIndex);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }
        return workbook;
    }

    /**
     * 单独处理
     *
     * @param dateQuery 查询时间
     * @return
     */
    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateQuery.getStartTime());
        calendar.add(Calendar.SECOND, 1);
        map.put("starttime", calendar.getTime().getTime() + "");
        map.put("endtime", dateQuery.getEndTime().getTime() + "");
        return map;
    }

    /**
     * 遍历每个小时的值
     *
     * @param cellList   列名
     * @param url        发送请求的地址
     * @param queryParam 查询参数
     * @return 结果
     */
    private List<CellData> eachData(List<Cell> cellList, String url, Map<String, String> queryParam, int rowIndexs) {
        List<CellData> results = new ArrayList<>();
        for (int j = 0; j < cellList.size(); j++) {
            Cell cell = cellList.get(j);
            String column = PoiCellUtil.getCellValue(cell);
            if (StringUtils.isNotBlank(column)) {
                queryParam.put("tagname", column);
                String result = httpUtil.get(url, queryParam);
                if (StringUtils.isNotBlank(result)) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (Objects.nonNull(jsonArray) && jsonArray.size() > 0) {
                        int size = jsonArray.size();
                        int rowIndex = rowIndexs;
                        for (int index = 0; index < size; index++) {
                            JSONObject obj = jsonArray.getJSONObject(index);
                            Object val = obj.get("timestamp");
                            if (Objects.nonNull(val)) {
                                ExcelWriterUtil.addCellData(results, rowIndex++, j, val);
                            }
                        }

                    }
                }
            }
        }
        return results;
    }

    private String getUrl() {
        return httpProperties.getUrlApiNJOne() + "/AcsStrtTimeTagValues";
    }

}
