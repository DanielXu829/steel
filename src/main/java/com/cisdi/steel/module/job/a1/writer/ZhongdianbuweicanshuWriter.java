package com.cisdi.steel.module.job.a1.writer;

import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.strategy.date.DateStrategy;
import com.cisdi.steel.module.job.strategy.options.OptionsStrategy;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 重点部位参数
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/14 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ZhongdianbuweicanshuWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            if (sheetName.startsWith("_tag")) {
                String[] split = sheetName.split("_");
                DateStrategy dateStrategy = strategyContext.getDate(split[2]);
                DateQuery handlerDate = dateStrategy.handlerDate(excelDTO.getDateQuery().getRecordDate());
                OptionsStrategy option = strategyContext.getOption(split[3]);
                List<DateQuery> dateQueries = option.execute(handlerDate);
                // 3、接口处理
                SheetRowCellData execute = execute(workbook, sheet, 0, 1, dateQueries);
                // 设置所有值
                execute.allValueWriteExcel();
            }
        }
//        Sheet dictionary = workbook.getSheet("_dictionary");
//        String minCell = PoiCellUtil.getCellValue(dictionary, 1, 1);
//        String maxCell = PoiCellUtil.getCellValue(dictionary, 2, 1);
//        int min = Double.valueOf(minCell).intValue();
//        int max = Double.valueOf(maxCell).intValue();
//        for (int i = min; i < max; i++) {
//            String sheetName = PoiCellUtil.getCellValue(dictionary, i, 1);
//            String sheetStrategy = PoiCellUtil.getCellValue(dictionary, i, 2);
//            String rowIndexInit = PoiCellUtil.getCellValue(dictionary, i, 3);
//            String rowTagNameIndex = PoiCellUtil.getCellValue(dictionary, i, 4);
//            List<DateQuery> dateQueries = strategyContext.handlerStrategy(sheetStrategy, excelDTO.getDateQuery().getRecordDate());
//            Sheet sheet = workbook.getSheet(sheetName);
//
//            SheetRowCellData execute = execute(workbook, sheet, Double.valueOf(rowTagNameIndex).intValue(), Double.valueOf(rowIndexInit).intValue(), dateQueries);
//            // 设置所有值
//            execute.allValueWriteExcel();
//        }
        return workbook;
    }

    public SheetRowCellData execute(Workbook workbook, Sheet sheet, Integer rowTagNameIndex, Integer rowIndexInit, List<DateQuery> queryList) {
        List<String> columnCells = PoiCustomUtil.getRowCelVal(sheet, rowTagNameIndex);
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
        List<CellData> rowCellDataList = new ArrayList<>();
        for (DateQuery eachDate : queryList) {
            List<CellData> cellValInfoList = eachData(columnCells, url, eachDate.getQueryParam(), rowIndexInit);
            rowCellDataList.addAll(cellValInfoList);
        }
        return SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(rowCellDataList)
                .build();
    }


    private List<CellData> eachData(List<String> cellList, String url, Map<String, String> queryParam, Integer rowIndexInit) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("starttime", queryParam.get("starttime"));
        jsonObject.put("endtime", queryParam.get("endtime"));
        List<String> collect = cellList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        jsonObject.put("tagnames", collect);

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
                    int size = list.length;
                    for (int i = 0; i < size; i++) {
                        Long key = list[i];
                        Object o = data.get(key + "");
//                        String dateTime = DateUtil.getFormatDateTime(new Date(key), DateUtil.yyyyMMddHHmm);
                        ExcelWriterUtil.addCellData(resultList, i + rowIndexInit, 0, key);
                        ExcelWriterUtil.addCellData(resultList, i + rowIndexInit, columnIndex, o);
                    }

                }
            }
        }
        return resultList;
    }

}
