package com.cisdi.steel.module.job.a1.readwriter;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellValInfo;
import com.cisdi.steel.module.job.dto.RowCellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class BentiwenduDayReadWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        SheetRowCellData sheetRowCellData = this.requestData(excelDTO.getTemplate(), excelDTO.getDateQuery());
        ExcelWriterUtil.setSheetRowCelData(sheetRowCellData);
        return sheetRowCellData.getWorkbook();
    }

    /**
     * 请求数据
     *
     * @param template  模板
     * @param dateQuery 查询时间
     * @return 结果
     */
    private SheetRowCellData requestData(ReportCategoryTemplate template, DateQuery dateQuery) {
        Workbook workbook = this.getWorkbook(template.getTemplatePath());
        // 第一个sheet值
        Sheet sheet = this.getSheet(workbook, "_tag_hour_each", template.getTemplatePath());
        List<Cell> columnCells = PoiCustomUtil.getFirstRowCel(sheet);
        String url = httpProperties.getUrlApiGLOne() + "/tagValueAction";
        List<RowCellData> rowCellDataList = new ArrayList<>();
        List<DateQuery> list = DateQueryUtil.buildHourEach(dateQuery.getStartTime());
        int size = list.size();
        for (int rowNum = 0; rowNum < size; rowNum++) {
            DateQuery eachDate = list.get(rowNum);
            // 每一行数据
            RowCellData rowCellData = RowCellData.builder().rowIndex(rowNum).build();
            List<CellValInfo> cellValInfoList = eachHour(columnCells, url, eachDate.getQueryParam());
            rowCellData.setValues(cellValInfoList);
            rowCellDataList.add(rowCellData);
        }
        return SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .rowCellDataList(rowCellDataList).build();
    }

    /**
     * 遍历每个小时的值
     *
     * @param cellList   列名
     * @param url        发送请求的地址
     * @param queryParam 查询参数
     * @return 结果
     */
    private List<CellValInfo> eachHour(List<Cell> cellList, String url, Map<String, String> queryParam) {
        List<CellValInfo> results = new ArrayList<>();
        for (Cell cell : cellList) {
            String column = PoiCellUtil.getCellValue(cell);
            if (StringUtils.isNotBlank(column)) {
                String[] columnSplit = column.split("/");
                String method = "avg";
                String tagName = columnSplit[0];
                if (columnSplit.length > 2) {
                    method = columnSplit[1];
                }
                queryParam.put("method", method);
                queryParam.put("tagname", tagName);
                String result = httpUtil.get(url, queryParam);
                if (StringUtils.isNotBlank(result)) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    CellValInfo cellValInfo = CellValInfo.builder()
                            .columnIndex(cell.getColumnIndex())
                            .cellValue(jsonObject.get("data")).build();
                    results.add(cellValInfo);
                }
            }
        }
        return results;
    }
}
