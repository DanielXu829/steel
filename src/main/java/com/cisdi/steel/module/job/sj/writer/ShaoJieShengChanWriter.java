package com.cisdi.steel.module.job.sj.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.sj.AnaValueDTO;
import com.cisdi.steel.dto.response.SuccessEntity;
import com.cisdi.steel.dto.response.sj.res.ProdStopRecordInfo;
import com.cisdi.steel.dto.response.sj.res.AnalysisValue;
import com.cisdi.steel.dto.response.sj.res.ProdStopRecord;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
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
    private static final int shaoJieChengPinItemRowNum = 28;
    private static final int yuanLiaoXingNengItemRowNum = 17;
    private Date dateRun;

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

    /**
     * 获取写入数据
     * @param excelDTO
     * @param version
     * @return
     */
    private Workbook getMapHandler1(WriterExcelDTO excelDTO, String version) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        dateRun = this.getDateQuery(excelDTO).getRecordDate();
        int numberOfSheets = workbook.getNumberOfSheets();
        // 处理tag点的数据
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
                List<DateQuery> dateQueries = DateQueryUtil.buildDay12HourEach(dateRun);
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

        //  接口直接写入的数据：烧结成品质量
        Sheet sheet1 = workbook.getSheetAt(0);
        sheet1.getRow(shaoJieChengPinItemRowNum).setZeroHeight(true);
        DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(dateRun);
        List<CellData> cellDataList1  = handleShaoJieChengPinData(sheet1, dateQuery, version);
        ExcelWriterUtil.setCellValue(sheet1, cellDataList1);

        // 接口直接写入的数据：原料性能
        sheet1.getRow(yuanLiaoXingNengItemRowNum).setZeroHeight(true);
        List<CellData> cellDataList2  = handleYuanLiaoXingNengData(sheet1, dateQuery, version);
        ExcelWriterUtil.setCellValue(sheet1, cellDataList2);

        // 接口写入数据：停机记录
        DateQuery dateQuery1 = DateQueryUtil.build24HoursFromEight(dateRun);
        List<CellData> cellDataList3  = handleDownTimeRecordData(sheet1, dateQuery1, version);
        ExcelWriterUtil.setCellValue(sheet1, cellDataList3);

        // 清除标记项(例如:{停机记录.停机起时.夜})
        PoiCustomUtil.clearPlaceHolder(sheet1);

        return workbook;
    }

    /**
     * 处理停机记录数据
     * @param sheet1
     * @param dateQuery
     * @param version
     * @return
     */
    private List<CellData> handleDownTimeRecordData(Sheet sheet, DateQuery dateQuery, String version) {
        List<CellData> cellDataList = new ArrayList();
        String data = getDownTimeRecordData(dateQuery, version);
        Cell nightDownTimeCell = PoiCustomUtil.getCellByValue(sheet, "{停机记录.停机起时.夜}");
        int nightDownTimeBeginRow = nightDownTimeCell.getRowIndex();
        int nightDownTimeBeginColumn = nightDownTimeCell.getColumnIndex();
        Cell dayDownTimeCell = PoiCustomUtil.getCellByValue(sheet, "{停机记录.停机起时.白}");
        int dayDownTimeBeginRow = dayDownTimeCell.getRowIndex();
        int dayDownTimeBeginColumn = dayDownTimeCell.getColumnIndex();
        if (StringUtils.isBlank(data)) {
            return null;
        }
        SuccessEntity<ProdStopRecordInfo> prodStopRecordDTO = JSON.parseObject(data, new TypeReference<SuccessEntity<ProdStopRecordInfo>>(){});
        List<ProdStopRecord> nightProdStopRecords = prodStopRecordDTO.getData().getNightProdStopRecords();
        List<ProdStopRecord> dayProdStopRecords = prodStopRecordDTO.getData().getDayProdStopRecords();
        // excel中只有5行单元格
        int nightMaxSize = 5;
        int dayMaxSize = 5;
        handleWriteDownTimeData(cellDataList, nightDownTimeBeginRow, nightDownTimeBeginColumn, nightProdStopRecords, nightMaxSize);
        handleWriteDownTimeData(cellDataList, dayDownTimeBeginRow, dayDownTimeBeginColumn, dayProdStopRecords, dayMaxSize);

        Cell dayDownTimesCell = PoiCustomUtil.getCellByValue(sheet, "{停机记录.停机次数}");
        int dayDownTimes = nightProdStopRecords.size() + dayProdStopRecords.size();
        ExcelWriterUtil.addCellData(cellDataList, dayDownTimesCell.getRowIndex(), dayDownTimesCell.getColumnIndex(), dayDownTimes);
        return cellDataList;
    }

    private void handleWriteDownTimeData(List<CellData> cellDataList, int BeginRow, int BeginColumn, List<ProdStopRecord> prodStopRecords, int maxSize) {
        if (CollectionUtils.isNotEmpty(prodStopRecords)) {
            int size = prodStopRecords.size();
            int offset;
            if (size > maxSize) {
                offset = size - maxSize;
            } else {
                offset = 0;
            }
            for (int i = offset, j = 0; i < size; i++, j++) {
                ProdStopRecord nightProdStopRecord = prodStopRecords.get(i);
                String startTime = DateUtil.getFormatDateTime(nightProdStopRecord.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                String endTime = DateUtil.getFormatDateTime(nightProdStopRecord.getEndTime(), "yyyy-MM-dd HH:mm:ss");
                Long stopTime = nightProdStopRecord.getStopTime();
                String causeDesc = nightProdStopRecord.getCauseDesc();

                ExcelWriterUtil.addCellData(cellDataList, BeginRow + j, BeginColumn, startTime);
                ExcelWriterUtil.addCellData(cellDataList, BeginRow + j, BeginColumn + 1, endTime);
                ExcelWriterUtil.addCellData(cellDataList, BeginRow + j, BeginColumn + 2, stopTime);
                ExcelWriterUtil.addCellData(cellDataList, BeginRow + j, BeginColumn + 2, stopTime);
                ExcelWriterUtil.addCellData(cellDataList, BeginRow + j, BeginColumn + 3, causeDesc);
            }
        }
    }

    /**
     * 处理原料性能数据
     * @param sheet1
     * @param dateQuery
     * @param version
     * @return
     */
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

        // 获取不同接口的数据(7个接口),并映射到 DTO中
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

        // 获取标记行的数据
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet1, yuanLiaoXingNengItemRowNum);
        for (int j = 0; j < materialTypeList.size(); j++) {
            // 获取excel单元格中项目的名称，来和接口进行对应
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
                                // 标记项以YL开头
                                if (itemName.startsWith("YL")) {
                                    // 将"YL_"截取掉
                                    String itemNameTrue = itemName.substring(3);
                                    List<AnalysisValue> dataList = anaValueDTO.getData();
                                    if (dataList != null && !dataList.isEmpty()) {
                                        int dataSize = dataList.size();
                                        // 如果数据不止一条，就取prodUnitCode并不为空的那一条数据
                                        if (dataSize > 1) {
                                            for (int k = 0; k < dataSize; k++) {
                                                String prodUnitCode = dataList.get(k).getAnalysis().getProdUnitCode();
                                                if (StringUtils.isNotBlank(prodUnitCode)) {
                                                    BigDecimal bigDecimal = dataList.get(k).getValues().get(itemNameTrue);
                                                    ExcelWriterUtil.addCellData(cellDataList, yuanLiaoXingNengItemRowNum + j + 1, i, bigDecimal);
                                                }
                                            }
                                        } else {
                                            // 如果只有一条，就取该条数据
                                            BigDecimal bigDecimal = dataList.get(0).getValues().get(itemNameTrue);
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

    /**
     * 处理烧结成品数据
     * @param sheet1
     * @param dateQuery
     * @param version
     * @return
     */
    private List<CellData> handleShaoJieChengPinData(Sheet sheet1, DateQuery dateQuery, String version) {
        List<CellData> cellDataList = new ArrayList();
        // 根据type不同(LC,LG,LP),调用三个接口
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

        // 根据type不同将数据封装到map中
        Map<String, AnaValueDTO> dtoMap = new HashMap();
        dtoMap.put("LC", anaValueLcDTO);
        dtoMap.put("LG", anaValueLgDTO);
        dtoMap.put("LP", anaValueLpDTO);

        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet1, shaoJieChengPinItemRowNum);

        for (int j = 0; j < 4; j++) {
            String id = PoiCellUtil.getCellValue(sheet1, shaoJieChengPinItemRowNum + j + 1, 1);
            // 去除小数点以及小数点后的数字
            Integer idNumber = Integer.parseInt(id.split("\\.")[0]);
            id = idNumber.toString();
            if (itemNameList != null && !itemNameList.isEmpty()) {
                for (int i = 0; i < itemNameList.size() ; i++) {
                    String itemName = itemNameList.get(i);
                    if (StringUtils.isNotBlank(itemName)) {
                        String prefix = itemName.split("_")[0];
                        AnaValueDTO anaValueDTO = dtoMap.get(prefix);
                        if (Objects.nonNull(anaValueDTO)) {
                            String itemNameTrue = itemName.substring(prefix.length() + 1);
                            for (int k = 0; k < anaValueDTO.getData().size() ; k++) {
                                // sampleId的最后两位(如果小于10,就取最后一位)与excel中的样编号进行对应
                                String sampleId =  anaValueDTO.getData().get(k).getAnalysis().getSampleid();
                                sampleId = sampleId.substring(sampleId.length() - 2);
                                if (sampleId.startsWith("0")) {
                                    sampleId = sampleId.substring(1);
                                }
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

    /**
     * 处理tag点数据
     * @param url
     * @param columns
     * @param dateQuery
     * @return
     */
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

    /**
     * 处理tag点数据
     * @param columns
     * @param jsonObject
     * @param dateQuery
     * @return
     */
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
                    // 按照时间顺序排序
                    Arrays.sort(list);

                    if(column.indexOf("_1d_") > -1) {
                        // 该tag点需要特殊处理, 获取当前运行时间，如果超过下午8点，就写入第一和第二行,如果没超过就只写第一行
                        Date itemTime = DateUtil.addHours(DateUtil.getDateBeginTime(dateRun), 20);
                        if (dateRun.getTime() < itemTime.getTime()) {
                            ExcelWriterUtil.addCellData(cellDataList, 1, columnIndex, data.get(list[0]));
                        } else {
                            ExcelWriterUtil.addCellData(cellDataList, 1, columnIndex, data.get(list[0]));
                            ExcelWriterUtil.addCellData(cellDataList, 2, columnIndex, data.get(list[0]));
                        }
                        continue;
                    }

                    List<DateQuery> dayEach = DateQueryUtil.buildDay12HourEach(dateRun);
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

    /**
     * 调用api获取停机记录
     * @param dateQuery
     * @param version
     * @param materialType
     * @return
     */
    private String getDownTimeRecordData(DateQuery dateQuery, String version) {
        Map<String, String> queryParam = new HashMap();
        queryParam.put("start", Objects.requireNonNull(dateQuery.getQueryStartTime().toString()));
        queryParam.put("end", Objects.requireNonNull(dateQuery.getQueryEndTime().toString()));
        String url = getDownTimeRecordUrl(version);
        return httpUtil.get(url, queryParam);
    }

    /**
     * 调用api获取原料性能数据
     * @param dateQuery
     * @param version
     * @param materialType
     * @return
     */
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

    /**
     * 调用api获取烧结成品数据
     * @param dateQuery
     * @param version
     * @param type
     * @return
     */
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
     * 通过tag点拿数据的API
     * @param version
     * @return
     */
    private String getTagUrl(String version) {
        return httpProperties.getSJUrlVersion(version) + "/tagValues/tagNames";
    }

    /**
     * 原料性能API
     * @param version
     * @return
     */
    private String getMatAnalysisUrl(String version) {
        return httpProperties.getSJUrlVersion(version) + "/burdenMatAnalysisVal?pageSize=10&pageNum=1";
    }

    /**
     * 烧结成品API
     * @param version
     * @return
     */
    private String getAnalysisValuesUrl(String version) {
        return httpProperties.getSJUrlVersion(version) + "/analysisValues/clock";
    }

    /**
     * 停机记录API
     * @param version
     * @return
     */
    private String getDownTimeRecordUrl(String version) {
        return httpProperties.getSJUrlVersion(version) + "/stopRecord/time";
    }

}
