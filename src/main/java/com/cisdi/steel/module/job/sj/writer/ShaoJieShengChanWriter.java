package com.cisdi.steel.module.job.sj.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.TagValueListDTO;
import com.cisdi.steel.dto.response.gl.res.TagValue;
import com.cisdi.steel.dto.response.sj.AnaValueDTO;
import com.cisdi.steel.dto.response.sj.res.AnalysisValue;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
@SuppressWarnings("ALL")
@Slf4j
public class ShaoJieShengChanWriter extends AbstractExcelReadWriter {
    private static int shaoJieChengPinItemRowNum = 28;
    private static int yuanLiaoXingNengItemRowNum = 17;

    @Autowired
    private TargetManagementMapper targetManagementMapper;

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());

        String version = "4.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch(Exception e){
            log.error("在模板中获取version失败", e);
        }

        return getMapHandler1(excelDTO, version);
    }

    private Workbook getMapHandler1(WriterExcelDTO excelDTO, String version) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表 待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                // 根据别名获取tag点名
                for (int j = 0; j < columns.size(); j++) {
                    if (columns.get(j).startsWith("ZP")) {
                        String tagName = targetManagementMapper.selectTargetFormulaByTargetName(columns.get(j));
                        if (StringUtils.isBlank(tagName)) {
                            columns.set(j, "");
                        } else {
                            columns.set(j, tagName);
                        }
                    }
                }

                // 获取两班倒查询策略
                List<DateQuery> dateQueries = DateQueryUtil.buildDay12HourEach(date.getRecordDate());
                for (int k = 0; k < dateQueries.size(); k++) {
                    DateQuery dateQuery = dateQueries.get(k);
                    if (dateQuery.getRecordDate().before(new Date())) {
                        List<CellData> cellDataList = this.mapDataHandler(getTagUrl(version), columns, dateQuery);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    } else {
                        break;
                    }
                }
            }
        }

        Sheet sheet1 = workbook.getSheetAt(0);
        sheet1.getRow(shaoJieChengPinItemRowNum).setZeroHeight(true);
        DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());
        List<CellData> cellDataList1  = handleShaoJieChengPinData(sheet1, dateQuery, version);
        ExcelWriterUtil.setCellValue(sheet1, cellDataList1);

        sheet1.getRow(yuanLiaoXingNengItemRowNum).setZeroHeight(true);
        List<CellData> cellDataList2  = handleYuanLiaoXingNengData(sheet1, dateQuery, version);
        ExcelWriterUtil.setCellValue(sheet1, cellDataList2);

        return workbook;
    }

    private List<CellData> handleYuanLiaoXingNengData(Sheet sheet1, DateQuery dateQuery, String version) {
        List<CellData> cellDataList = new ArrayList();
        List<String> materialTypeList = new ArrayList();
        materialTypeList.add("ore_blending");
        materialTypeList.add("quicklime");
        materialTypeList.add("dust");
        materialTypeList.add("limestone");
        materialTypeList.add("dolomite");
        materialTypeList.add("return_fine");
        materialTypeList.add("coke");
        Map<String, AnaValueDTO> dtoMap = new HashMap();

        for (int i = 0; i < materialTypeList.size(); i++) {
            String data = getYuanLiaoXingNengData(dateQuery, version, materialTypeList.get(i));
            AnaValueDTO anaValueFenDTO = null;
            if (StringUtils.isNotBlank(data)) {
                anaValueFenDTO = JSON.parseObject(data, AnaValueDTO.class);
            }
            dtoMap.put(materialTypeList.get(i), anaValueFenDTO);
        }
        Map<String, String> itemCategoryToName = new HashMap();
        itemCategoryToName.put("混匀粉", "ore_blending");
        itemCategoryToName.put("生石灰(PL8)", "quicklime");
        itemCategoryToName.put("生石灰(PL9)", "dust");
        itemCategoryToName.put("石灰石", "limestone");
        itemCategoryToName.put("白云石", "dolomite");
        itemCategoryToName.put("返矿", "return_fine");
        itemCategoryToName.put("燃料", "coke");

        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet1, yuanLiaoXingNengItemRowNum);
        for (int j = 0; j < materialTypeList.size(); j++) {
            String itemCategory = PoiCellUtil.getCellValue(sheet1, yuanLiaoXingNengItemRowNum + j + 1, 7);
            itemCategory = itemCategory.trim();
            if (StringUtils.isNotBlank(itemCategory)) {
                String materialType = itemCategoryToName.get(itemCategory);
                if (StringUtils.isNotBlank(materialType)) {
                    AnaValueDTO anaValueDTO = dtoMap.get(materialType);
                    if (Objects.isNull(anaValueDTO)) {
                        continue;
                    }
                    if (itemNameList != null && !itemNameList.isEmpty()) {
                        for (int i = 0; i < itemNameList.size() ; i++) {
                            String itemName = itemNameList.get(i);
                            if (StringUtils.isNotBlank(itemName)) {
                                if (itemName.startsWith("YL")) {
                                    String itemNameTrue = itemName.substring(3);
                                    List<AnalysisValue> dataList = anaValueDTO.getData();
                                    if (dataList != null && !dataList.isEmpty()) {
                                        BigDecimal bigDecimal = new BigDecimal(0.0);
                                        int valueNum = 0;
                                        for (int k = 0; k < dataList.size(); k++) {
                                            if (Objects.nonNull(dataList.get(k).getValues().get(itemNameTrue))) {
                                                bigDecimal = bigDecimal.add(dataList.get(k).getValues().get(itemNameTrue));
                                                valueNum += 1;
                                            }
                                        }
                                        if (Objects.nonNull(bigDecimal) && valueNum > 0) {
                                            bigDecimal = bigDecimal.divide(new BigDecimal(dataList.size()),2, BigDecimal.ROUND_DOWN);
                                            ExcelWriterUtil.addCellData(cellDataList, yuanLiaoXingNengItemRowNum + j + 1, i, bigDecimal);
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

    private List<CellData> handleShaoJieChengPinData(Sheet sheet1, DateQuery dateQuery, String version) {
        List<CellData> cellDataList = new ArrayList();

        String shaoJieChengPinLCData = getShaoJieChengPinData(dateQuery, version, "LC");
        AnaValueDTO anaValueLcDTO = null;
        if (StringUtils.isNotBlank(shaoJieChengPinLCData)) {
            anaValueLcDTO = JSON.parseObject(shaoJieChengPinLCData, AnaValueDTO.class);
        }

        String shaoJieChengPinLGData = getShaoJieChengPinData(dateQuery, version, "LG");
        AnaValueDTO anaValueLgDTO = null;
        if (StringUtils.isNotBlank(shaoJieChengPinLGData)) {
            anaValueLgDTO = JSON.parseObject(shaoJieChengPinLGData, AnaValueDTO.class);
        }

        String shaoJieChengPinLPData = getShaoJieChengPinData(dateQuery, version, "LP");
        AnaValueDTO anaValueLpDTO = null;
        if (StringUtils.isNotBlank(shaoJieChengPinLPData)) {
            anaValueLpDTO = JSON.parseObject(shaoJieChengPinLPData, AnaValueDTO.class);
        }

        Map<String, AnaValueDTO> dtoMap = new HashMap();
        dtoMap.put("LC", anaValueLcDTO);
        dtoMap.put("LG", anaValueLgDTO);
        dtoMap.put("LP", anaValueLpDTO);

        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet1, shaoJieChengPinItemRowNum);

        for (int j = 0; j < 4; j++) {
            String id = PoiCellUtil.getCellValue(sheet1, shaoJieChengPinItemRowNum + j + 1, 1);
            // 去除小数点以及小数点后的数字
            Integer id1 = Integer.parseInt(id.split("\\.")[0]);
            id = id1.toString();
            if (itemNameList != null && !itemNameList.isEmpty()) {
                for (int i = 0; i < itemNameList.size() ; i++) {
                    String itemName = itemNameList.get(i);
                    if (StringUtils.isNotBlank(itemName)) {
                        String prefix = itemName.split("_")[0];
                        AnaValueDTO anaValueDTO = dtoMap.get(prefix);
                        if (Objects.nonNull(anaValueDTO)) {
                            String itemNameTrue = itemName.substring(prefix.length() + 1);
                            for (int k = 0; k < anaValueDTO.getData().size() ; k++) {
                                String sampleId =  anaValueDTO.getData().get(k).getAnalysis().getSampleid();
                                sampleId = sampleId.substring(sampleId.length() - 1);
                                if (sampleId.equals(id)) {
                                    BigDecimal cellValue = anaValueDTO.getData().get(k).getValues().get(itemNameTrue);
                                    ExcelWriterUtil.addCellData(cellDataList, shaoJieChengPinItemRowNum + j + 1, i, cellValue);
                                }
                            }
                        }
                    }
                }
            }
        }

        return cellDataList;
    }

    private List<CellData> mapDataHandler(String url, List<String> columns, DateQuery dateQuery) {
        JSONObject query = new JSONObject();
        Date date = new Date();
        if (dateQuery.getQueryEndTime() > date.getTime()) {
            query.put("end", date.getTime());
        } else {
            query.put("end", dateQuery.getQueryEndTime());
        }

        query.put("start", dateQuery.getQueryStartTime());
        query.put("tagNames", columns);
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String result = httpUtil.postJsonParams(url, jsonString);

        if (StringUtils.isBlank(result)) {
            return null;
        }

        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return null;
        }

        return handlerData(columns, data, dateQuery);
    }

    private List<CellData> handlerData(List<String> columns, JSONObject jsonObject, DateQuery dateQuery) {
        List<CellData> cellDataList = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            String column = columns.get(columnIndex);
            if (StringUtils.isNotBlank(column)) {
                JSONObject data = jsonObject.getJSONObject(column);
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
                    List<DateQuery> dayEach = DateQueryUtil.buildDay12HourEach(new Date());
                    int rowIndex = 1;
                    for (int j = 0; j < dayEach.size(); j++) {
                        DateQuery query = dayEach.get(j);
                        Date recordDate = query.getEndTime();
                        for (int i = 0; i < list.length; i++) {
                            Long tempTime = list[i];
                            String formatDateTime = DateUtil.getFormatDateTime(new Date(tempTime), "yyyy-MM-dd HH:00:00");
                            Date date = DateUtil.strToDate(formatDateTime, DateUtil.fullFormat);
                            if (date.getTime() == recordDate.getTime()) {
                                Object o = data.get(tempTime + "");
                                ExcelWriterUtil.addCellData(cellDataList, rowIndex, columnIndex, o);
                                break;
                            }
                        }
                        rowIndex += 1;
                    }
                }
            }
        }

        return cellDataList;
    }

    private String getYuanLiaoXingNengData(DateQuery dateQuery, String version, String materialType) {
        JSONObject query = new JSONObject();
        query.put("materialType", materialType);
        query.put("start", dateQuery.getQueryStartTime());
        query.put("end", dateQuery.getQueryEndTime());
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String url = getMatAnalysisUrl(version);
        return httpUtil.postJsonParams(url, jsonString);
    }

    private String getShaoJieChengPinData(DateQuery dateQuery, String version, String type) {
        Map<String, String> queryParam = new HashMap();
        queryParam.put("startTime", Objects.requireNonNull(dateQuery.getStartTime().getTime()).toString());
        queryParam.put("endTime", Objects.requireNonNull(dateQuery.getEndTime().getTime()).toString());
        queryParam.put("brandCode", "sinter");
        queryParam.put("type", type);
        queryParam.put("pageSize", "10");
        queryParam.put("pageNum", "1");
        String url = getAnalysisValuesUrl(version);
        return httpUtil.get(url, queryParam);
    }
    /**
     * 通过tag点拿数据的API，路径可能会改变
     * @param version
     * @return
     */
    private String getTagUrl(String version) {
        return httpProperties.getSJUrlVersion(version) + "/tagValues/tagNames";
    }

    private String getMatAnalysisUrl(String version) {
        return httpProperties.getSJUrlVersion(version) + "/burdenMatAnalysisVal?pageSize=10&pageNum=1";
    }

    private String getAnalysisValuesUrl(String version) {
        return httpProperties.getSJUrlVersion(version) + "/analysisValues/clock";
    }

}
