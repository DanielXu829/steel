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
 * <p>Description: 出铁化学成分执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/22 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class ChuTieHuaXueChengFenWriter extends AbstractExcelReadWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        List<DateQuery> dateQueries = null;
        int numberOfNames = workbook.getNumberOfSheets();

        String version = "8.0";
        try{
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        }catch(Exception e){
            log.error("在模板中获取version失败", e);
        }

        for (int i = 0; i < numberOfNames; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                //获取时间策略
               dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
            }
        }

        // 获取第一个sheet,往第一个sheet中填充数据, 第4行 为标记项(在excel中设置为隐藏)
        Sheet sheet = workbook.getSheetAt(0);
        int itemRowNum = 3;
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
        for (DateQuery dateQuery : dateQueries) {
            if (itemNameList != null && !itemNameList.isEmpty()) {
                for (int i = 0; i < itemNameList.size(); i++) {
                    // 执行生铁成分的数据写入
                    if ("ST_Si".equals(itemNameList.get(i))) {
                        String chutTiePrefix = "ST_";
                        // 通过api获取数据
                        String data = getData("HM", dateQuery, version);
                        List<CellData> cellDataList = mapDataHandler(data, chutTiePrefix, itemNameList, itemRowNum, i);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                    // 执行炉渣成分的数据写入
                    if ("LZ_SiO2".equals(itemNameList.get(i))) {
                        String luZhaPrefix = "LZ_";
                        // 通过api获取数据
                        String data = getData("SLAG", dateQuery, version);
                        List<CellData> cellDataList = mapDataHandler(data, luZhaPrefix, itemNameList, itemRowNum, i);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                }
            }
        }

        return workbook;
    }

    /**
     *
     * @param data
     * @param prefix
     * @param itemNameList
     * @param itemRowNum
     * @param itemColNum
     * @return
     */
    protected List<CellData> mapDataHandler(String data, String prefix, List<String> itemNameList, Integer itemRowNum, Integer itemColNum) {
        List<CellData> cellDataList = new ArrayList<>();
        if (StringUtils.isNotBlank(data)) {
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (Objects.nonNull(jsonObject)) {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                if (Objects.nonNull(dataArray) && dataArray.size() != 0) {
                    for (int i = 0; i < dataArray.size(); i++) {
                        // 半个小时一条数据
                        JSONObject dataObj = dataArray.getJSONObject(i);
                        if (Objects.nonNull(dataObj)) {
                            JSONObject valueObj = dataObj.getJSONObject("values");
                            if (Objects.nonNull(valueObj)) {
                                // 遍历标记行所有的单元格
                                for (int j = 0; j < itemNameList.size(); j++) {
                                    // 获取标记项单元格中的值
                                    String itemName = itemNameList.get(j);
                                    if (!StringUtils.isEmpty(itemName)) {
                                        if (itemName.indexOf(prefix) >= 0) {
                                            String itemNameTrue = itemName.substring(prefix.length());
                                            Double cellValue = valueObj.getDouble(itemNameTrue);
                                            Integer row = itemRowNum + 1 + i;
                                            Integer col = j;
                                            ExcelWriterUtil.addCellData(cellDataList, row, col, cellValue);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return cellDataList;
    }

    /**
     * 获取api返回的数据
     * @param brandCode (HM: 生铁成分, SLAG: 炉渣成分)
     * @param dateQuery
     * @param version
     * @return api数据
     */
    public String getData(String brandCode, DateQuery dateQuery, String version) {
        Map<String, String> queryParam = this.getQueryParam(dateQuery);
        queryParam.put("type", "LC");
        queryParam.put("brandcode", brandCode);
        return httpUtil.get(getUrl(version), queryParam);
    }

    /**
     * 获取api的url
     * @param version
     * @return url
     */
    protected String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/analysisValues/clock";
    }
}
