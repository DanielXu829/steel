package com.cisdi.steel.module.job.sj.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.SuccessEntity;
import com.cisdi.steel.dto.response.sj.AnaValueDTO;
import com.cisdi.steel.dto.response.sj.YardRunInfoDTO;
import com.cisdi.steel.dto.response.sj.res.*;
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
import java.util.stream.Collectors;

@Component
@SuppressWarnings("ALL")
@Slf4j
public class ShaoJieShengChanWriter extends AbstractExcelReadWriter {
    // 需要乘以12的点位
    private static final List<String> TAG_FORMUALS_NEED_TO_MUTIPLY_12 = Arrays.asList("ST4_L1R_SIN_103ASinInstanFl_12h_cur", "ST4_L1R_SIN_103BSinInstanFl_12h_cur",
            "ST4_L1R_SIN_BF2CRFInstanFl_12h_cur", "ST4_L1R_SIN_CRF104InstanFl_12h_cur", "ST4_L1R_SIN_Bed103BedMatInsFl_12h_cur");
    private static final List<String> TAG_FORMUALS_NEED_TO_divide_100 = Arrays.asList("ST4_L1R_SIN_1To5OreBldUseP_12h_avg", "ST4_L1R_SIN_8ATo8BQuLimeUseP_12h_avg",
            "ST4_L1R_SIN_13To14LimeUseP_12h_avg", "ST4_L1R_SIN_15To16DoloUseP_12h_avg", "ST4_L1R_SIN_10To12CoReFineUseP_12h_avg", "ST4_L1R_SIN_6To7FuelUseP_12h_avg");
    // 取开始时间的点位
    private static final List<String> TAG_FORMUALS_GET_DATA_BY_BEGIN_TIME = Arrays.asList("ST4_MESR_SIN_SinterDayConfirmY_1d_cur",
            "ST4_L1R_SIN_ProductPerHour_1d_avg", "ST4_MESR_SIN_SinterUF_1d_cur", "ST4_L2R_SIN_ProductRatio_1d_cur");
    // 点位需要自己减前一个时间点的数据
    private static final List<String> TAG_FORMUALS_NEED_TO_SUBTRACT_BEFORE = Arrays.asList("ST4_L1R_SIN_103ASinAccFl_12h_cur", "ST4_L1R_SIN_103BSinAccFl_12h_cur",
            "ST4_L1R_SIN_CRF104AccFl_12h_cur", "ST4_L1R_SIN_BF2CRFAccFl_12h_cur", "ST4_L1R_SIN_Bed103BedMatInsAcc_12h_cur",
            "ST4_L1R_SIN_StkPilAccAmt_12h_cur", "ST4_L1R_SIN_ReclAccAct_12h_cur");
    private static int shaojieChengPinItemRowNum = 8;
    private static int yuanRanLiaoXingNengItemRowNum = 36;
    private static final String GET_VERSION_FAILED_MESSAGE = "在模板中获取version失败";
    private static final String TargetName_PREFIX = "ZP";
    public static final String END = "end";
    public static final String START = "start";
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
            log.error(GET_VERSION_FAILED_MESSAGE, e);
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
        // 当天运行前一天的报表
        dateRun = DateUtil.addDays(getDateQuery(excelDTO).getRecordDate(), -1);
        int numberOfSheets = workbook.getNumberOfSheets();
        // 获取两班倒查询策略
        List<DateQuery> dateQueries = DateQueryUtil.buildDay12HourAheadTwoHour(dateRun);
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
                    String tagName = targetManagementMapper.selectTargetFormulaByTargetName(columns.get(j));
                    if (StringUtils.isBlank(tagName)) {
                        columns.set(j, StringUtils.EMPTY);
                    } else {
                        columns.set(j, tagName);
                    }
                }
                // 特殊处理累计作业率
                handleCumulativeOperationRate(workbook, dateQueries, version);
                for (int k = 0; k < dateQueries.size(); k++) {
                    DateQuery dateQuery = dateQueries.get(k);
                    if (dateQuery.getRecordDate().before(new Date())) {
                        List<CellData> cellDataList = this.mapDataHandler(getTagUrl(version), columns, dateQuery, k + 1);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    } else {
                        break;
                    }
                }
            }
        }

        //  接口直接写入的数据：烧结成品质量
        Sheet sheet1 = workbook.getSheetAt(0);
        // 标记行
        shaojieChengPinItemRowNum = Optional.ofNullable(PoiCustomUtil.getCellByValue(sheet1, "LC_TFe"))
                .map(Cell::getRowIndex).orElse(shaojieChengPinItemRowNum);
        sheet1.getRow(shaojieChengPinItemRowNum).setZeroHeight(true);
        DateQuery dateQuery = DateQueryUtil.build24HoursFromTen(dateRun);
        List<CellData> cellDataList1  = handleShaoJieChengPinData(sheet1, dateQuery, version);
        ExcelWriterUtil.setCellValue(sheet1, cellDataList1);

        // 接口直接写入的数据：原料性能
        // 标记行
        yuanRanLiaoXingNengItemRowNum = Optional.ofNullable(PoiCustomUtil.getCellByValue(sheet1, "coke_FCd"))
                .map(Cell::getRowIndex).orElse(yuanRanLiaoXingNengItemRowNum);
        sheet1.getRow(yuanRanLiaoXingNengItemRowNum).setZeroHeight(true);
        List<CellData> cellDataList2  = handleYuanLiaoXingNengData(sheet1, dateQuery, version);
        ExcelWriterUtil.setCellValue(sheet1, cellDataList2);

        // 接口写入数据：停机记录
        List<CellData> cellDataList3  = handleDownTimeRecordData(sheet1, dateQuery, version);
        ExcelWriterUtil.setCellValue(sheet1, cellDataList3);

        // 接口写入 堆料时间、取料时间
        handleYardRunInfo(sheet1, version);

        // 接口写入 堆2、堆6、堆场库存
        handleYardRunInfoOf26B(sheet1, version);

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
        try {
            String data = getDownTimeRecordData(dateQuery, version);
            if (StringUtils.isBlank(data)) {
                return null;
            }
            SuccessEntity<ProdStopRecordInfo> prodStopRecordDTO = JSON.parseObject(data, new TypeReference<SuccessEntity<ProdStopRecordInfo>>(){});
            ProdStopRecordInfo prodStopRecordInfo = Optional.ofNullable(prodStopRecordDTO)
                    .map(SuccessEntity::getData).orElse(null);
            if (Objects.isNull(prodStopRecordInfo)) {
                return null;
            }

            try {
                // 处理夜班停机记录
                List<ProdStopRecord> nightProdStopRecords = prodStopRecordInfo.getNightProdStopRecords();
                // excel中只有5行单元格
                int nightMaxSize = 8;
                if (CollectionUtils.isNotEmpty(nightProdStopRecords)) {
                    Cell nightDownTimeBeginCell = PoiCustomUtil.getCellByValue(sheet, "{停机记录.停机起时.夜}");
                    if (Objects.nonNull(nightDownTimeBeginCell)) {
                        int nightDownTimeBeginRow = nightDownTimeBeginCell.getRowIndex();
                        int nightDownTimeBeginColumn = nightDownTimeBeginCell.getColumnIndex();
                        handleWriteDownTimeData(cellDataList, nightDownTimeBeginRow, nightDownTimeBeginColumn, nightProdStopRecords, nightMaxSize);
                    } else {
                        log.error("模板中{停机记录.停机起时.夜}占位符不存在");
                    }
                }

                Cell nightDownTimeTotalCell = PoiCustomUtil.getCellByValue(sheet, "{停机记录.共停.夜}");
                if (Objects.nonNull(nightDownTimeTotalCell)) {
                    int nightDownTimeTotalCellRow = nightDownTimeTotalCell.getRowIndex();
                    int nightDownTimeTotalCellColumn = nightDownTimeTotalCell.getColumnIndex();
                    ExcelWriterUtil.addCellData(cellDataList, nightDownTimeTotalCellRow, nightDownTimeTotalCellColumn, prodStopRecordInfo.getTotalStopTimeOfNight());
                } else {
                    log.error("模板中{停机记录.共停.夜}占位符不存在");
                }

                Cell nightDownTimesCell = PoiCustomUtil.getCellByValue(sheet, "{停机记录.停机次数.夜}");
                if (Objects.nonNull(nightDownTimesCell)) {
                    int nightDownTimesCellRow = nightDownTimesCell.getRowIndex();
                    int nightDownTimesCellColumn = nightDownTimesCell.getColumnIndex();
                    String value = "停机次数：" + prodStopRecordInfo.getTimeOfNight() + "次";
                    ExcelWriterUtil.addCellData(cellDataList, nightDownTimesCellRow, nightDownTimesCellColumn, value);
                } else {
                    log.error("模板中{停机记录.停机次数.夜}占位符不存在");
                }
            } catch (Exception e) {
                log.error("处理夜班停机记录出错", e);
            }

            try {
                // 处理白班停机记录
                List<ProdStopRecord> dayProdStopRecords = prodStopRecordInfo.getDayProdStopRecords();
                Cell dayDownTimeCell = PoiCustomUtil.getCellByValue(sheet, "{停机记录.停机起时.白}");
                // excel中只有5行单元格
                int dayMaxSize = 10;
                if (CollectionUtils.isNotEmpty(dayProdStopRecords)) {
                    if (Objects.nonNull(dayDownTimeCell)) {
                        int dayDownTimeBeginRow = dayDownTimeCell.getRowIndex();
                        int dayDownTimeBeginColumn = dayDownTimeCell.getColumnIndex();
                        handleWriteDownTimeData(cellDataList, dayDownTimeBeginRow, dayDownTimeBeginColumn, dayProdStopRecords, dayMaxSize);
                    } else {
                        log.error("模板中{停机记录.停机起时.白}占位符不存在");
                    }
                }
                Cell dayDownTimeTotalCell = PoiCustomUtil.getCellByValue(sheet, "{停机记录.共停.白}");
                if (Objects.nonNull(dayDownTimeTotalCell)) {
                    int dayDownTimeTotalCellRow = dayDownTimeTotalCell.getRowIndex();
                    int dayDownTimeTotalCellColumn = dayDownTimeTotalCell.getColumnIndex();
                    ExcelWriterUtil.addCellData(cellDataList, dayDownTimeTotalCellRow, dayDownTimeTotalCellColumn, prodStopRecordInfo.getTotalStopTimeOfDay());
                } else {
                    log.error("模板中{停机记录.共停.白}占位符不存在");
                }

                Cell dayDownTimesCell = PoiCustomUtil.getCellByValue(sheet, "{停机记录.停机次数.白}");
                if (Objects.nonNull(dayDownTimesCell)) {
                    int dayDownTimesCellRow = dayDownTimesCell.getRowIndex();
                    int dayDownTimesCellColumn = dayDownTimesCell.getColumnIndex();
                    String value = "停机次数：" + prodStopRecordInfo.getTimeOfDay() + "次";
                    ExcelWriterUtil.addCellData(cellDataList, dayDownTimesCellRow, dayDownTimesCellColumn, value);
                } else {
                    log.error("模板中{停机记录.停机次数.白}占位符不存在");
                }
            } catch (Exception e) {
                log.error("处理白班停机记录出错", e);
            }

            Cell totalDownTimeCell = PoiCustomUtil.getCellByValue(sheet, "{停机记录.共停}");
            if (Objects.nonNull(totalDownTimeCell)) {
                int totalDownTime = prodStopRecordInfo.getTotalStopTime();
                ExcelWriterUtil.addCellData(cellDataList, totalDownTimeCell.getRowIndex(), totalDownTimeCell.getColumnIndex(), totalDownTime);
            } else {
                log.error("模板中{停机记录.共停}占位符不存在");
            }

            Cell totalDownTimesCell = PoiCustomUtil.getCellByValue(sheet, "{停机记录.停机次数.共停}");
            if (Objects.nonNull(totalDownTimesCell)) {
                int totalDownTimes = prodStopRecordInfo.getTime();
                String value = "停机次数：" + totalDownTimes + "次";
                ExcelWriterUtil.addCellData(cellDataList, totalDownTimesCell.getRowIndex(), totalDownTimesCell.getColumnIndex(), value);
            } else {
                log.error("模板中{停机记录.停机次数.共停}占位符不存在");
            }

            return cellDataList;
        } catch (Exception e) {
            log.error("处理停机记录出错", e);

            return cellDataList;
        }
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
        List<String> itemCategoryList = Arrays.asList("混匀粉", "生石灰PL8", "生石灰PL9", "石灰石", "白云石", "返矿", "煤粉");
        List<String> materialTypeList = Arrays.asList("ore_blending", "quicklime", "dust",
                "limestone", "dolomite", "return_fine", "coke");
        Map<String, String> itemCategoryToNameMap = itemCategoryList.stream().collect(Collectors
                .toMap(key -> key, key -> materialTypeList.get(itemCategoryList.indexOf(key))));

        // 获取不同接口的数据(7个接口),并映射到 DTO中
        Map<String, AnaValueDTO> dtoMap = new HashMap();
        for (int i = 0; i < materialTypeList.size(); i++) {
            String data = getYuanLiaoXingNengData(dateQuery, version, materialTypeList.get(i));
            AnaValueDTO anaValueFenDTO = null;
            if (StringUtils.isNotBlank(data)) {
                anaValueFenDTO = JSON.parseObject(data, AnaValueDTO.class);
            }
            dtoMap.put(materialTypeList.get(i), anaValueFenDTO);
        }
        // 获取标记行的数据
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet1, yuanRanLiaoXingNengItemRowNum);
        for (int j = 0; j < materialTypeList.size(); j++) {
            // 获取excel单元格中项目的名称，来和接口进行对应
            String itemCategory = PoiCellUtil.getCellValue(sheet1, yuanRanLiaoXingNengItemRowNum + j + 1, 15);
            itemCategory = itemCategory.trim();
            String materialType = itemCategoryToNameMap.get(itemCategory);
            List<AnalysisValue> dataList =
                    Optional.ofNullable(dtoMap.get(materialType)).map(AnaValueDTO::getData).orElse(null);
            if (CollectionUtils.isEmpty(dataList)) {
                continue;
            }
            for (int i = 0; i < itemNameList.size(); i++) {
                String itemName = itemNameList.get(i);
                // 标记项以YL开头
                if (StringUtils.isBlank(itemName) || itemName.split("_").length <= 1) {
                    continue;
                }
                String itemNamePrefix = itemName.split("_")[0];
                String itemNameTrue = itemName.split("_")[1];
                // 如果数据不止一条，就取prodUnitCode并不为空的那一条数据
                AnalysisValue analysisValue = dataList.stream().filter(e -> StringUtils
                        .isNotBlank(e.getAnalysis().getProdUnitCode())).findFirst().orElse(null);
                BigDecimal bigDecimal;
                if (Objects.nonNull(analysisValue)) {
                    bigDecimal = analysisValue.getValues().get(itemNameTrue);
                } else {
                    // 如果只有一条，就取该条数据
                    bigDecimal = dataList.get(0).getValues().get(itemNameTrue);
                }
                int rowIndex;
                int columnIndex = i;
                if ("coke".equals(itemNamePrefix) && "coke".equals(materialType)) {
                    rowIndex = yuanRanLiaoXingNengItemRowNum + 1;
                } else {
                    rowIndex = yuanRanLiaoXingNengItemRowNum + j + 1;
                }
                ExcelWriterUtil.addCellData(cellDataList, rowIndex, columnIndex, bigDecimal);
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
        List<CellData> cellDataList = null;
        try {
            cellDataList = new ArrayList();
            // 根据type不同(LC,LG,LP),调用三个接口  根据type不同将数据封装到map中
            Map<String, AnaValueDTO> dtoMap = new HashMap();
            Arrays.asList("LC", "LG", "LP").forEach(type -> {
                String shaoJieChengPinData = getShaoJieChengPinData(dateQuery, version, type);
                if (StringUtils.isNotBlank(shaoJieChengPinData)) {
                    AnaValueDTO anaValueDTO = JSON.parseObject(shaoJieChengPinData, AnaValueDTO.class);
                    dtoMap.put(type, anaValueDTO);
                }
            });

            List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet1, shaojieChengPinItemRowNum);
            if (CollectionUtils.isEmpty(itemNameList)) {
                return cellDataList;
            }
            for (int j = 0; j < 4; j++) {
                String id = PoiCellUtil.getCellValue(sheet1, shaojieChengPinItemRowNum + j + 1, 9);
                // 去除小数点以及小数点后的数字 excel取出来的数值会带小数点
                Integer idNumber = Integer.parseInt(id.split("\\.")[0]);
                id = idNumber.toString();

                for (int i = 0, size = itemNameList.size(); i < size; i++) {
                    String itemName = itemNameList.get(i);
                    if (StringUtils.isBlank(itemName)) {
                        continue;
                    }
                    String prefix = itemName.split("_")[0];
                    AnaValueDTO anaValueDTO = dtoMap.get(prefix);
                    if (Objects.isNull(anaValueDTO)) {
                        continue;
                    }
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
                            ExcelWriterUtil.addCellData(cellDataList, shaojieChengPinItemRowNum + j + 1, i, cellValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return cellDataList;
        }
    }

    private void handleYardRunInfo(Sheet sheet, String version) {
        try {
            List<CellData> cellDataList = new ArrayList<>();
            List<DateQuery> dateQueries = DateQueryUtil.buildDay12HourAheadTwoHour(dateRun);
            YardRunInfo yarnRunStatisticOfNight = getYarnRunStatisticData(version, dateQueries.get(0).getQueryEndTime(), 2);
            YardRunInfo yarnRunStatisticOfDay = getYarnRunStatisticData(version,dateQueries.get(1).getQueryEndTime(), 1);

            Cell pushMatTimeNightCell = PoiCustomUtil.getCellByValue(sheet, "{堆料时间.夜}");
            int pushMatTimeNightCellRowIndex = pushMatTimeNightCell.getRowIndex();
            int pushMatTimeNightCellColumnIndex = pushMatTimeNightCell.getColumnIndex();
            ExcelWriterUtil.addCellData(cellDataList, pushMatTimeNightCellRowIndex, pushMatTimeNightCellColumnIndex, yarnRunStatisticOfNight.getPushMatTime());
            ExcelWriterUtil.addCellData(cellDataList, pushMatTimeNightCellRowIndex + 1, pushMatTimeNightCellColumnIndex, yarnRunStatisticOfDay.getPushMatTime());

            Cell pullMatTimeNightCell = PoiCustomUtil.getCellByValue(sheet, "{取料时间.夜}");
            int pullMatTimeNightCellRowIndex = pullMatTimeNightCell.getRowIndex();
            int pullMatTimeNightCellColumnIndex = pullMatTimeNightCell.getColumnIndex();
            ExcelWriterUtil.addCellData(cellDataList, pullMatTimeNightCellRowIndex, pullMatTimeNightCellColumnIndex, yarnRunStatisticOfNight.getPullMatTime());
            ExcelWriterUtil.addCellData(cellDataList, pullMatTimeNightCellRowIndex + 1, pullMatTimeNightCellColumnIndex, yarnRunStatisticOfDay.getPullMatTime());

            ExcelWriterUtil.setCellValue(sheet, cellDataList);
        } catch (Exception e) {
            log.error("处理堆料时间、取料时间出错", e);
        }
    }

    private void handleYardRunInfoOf26B(Sheet sheet, String version) {
        try {
            List<CellData> cellDataList = new ArrayList<>();
            DateQuery dateQuery = DateQueryUtil.build24HoursFromTen(dateRun);
            String url = String.format("%s?clock=%s", getYarnRunStatisticsFor26BUrl(version), dateQuery.getQueryEndTime());
            String jsonData = httpUtil.get(url);
            JSONObject jsonObject = Optional.ofNullable(JSONObject.parseObject(jsonData))
                    .map(e -> e.getJSONObject("data")).orElse(null);
            if (Objects.isNull(jsonObject)) {
                return;
            }
            // jsonObject转为Map
            Map<String, Object> itemToValueMap = new HashMap<>();
            for (Map.Entry entry : jsonObject.entrySet()) {
                itemToValueMap.put(entry.getKey().toString(), entry.getValue());
            }
            // 写入excel, {map的key} 作为excel中的占位符
            itemToValueMap.forEach((item, value) -> {
                Cell cell = PoiCustomUtil.getCellByValue(sheet, "{" + item + "}");
                int rowIndex = cell.getRowIndex();
                int columnIndex = cell.getColumnIndex();
                ExcelWriterUtil.addCellData(cellDataList, rowIndex, columnIndex, value);
            });
            ExcelWriterUtil.setCellValue(sheet, cellDataList);
        } catch (Exception e) {
            log.error("处理堆2、堆6、库存出错", e);
        }
    }
    /**
     * 处理tag点数据
     * @param url
     * @param columns tag点
     * @param dateQuery
     * @return
     */
    protected List<CellData> mapDataHandler(String url, List<String> columns, DateQuery dateQuery, int k) {
        JSONObject query = new JSONObject();
        Date date = new Date();
        if (dateQuery.getQueryEndTime() > date.getTime()) {
            query.put(END, date.getTime());
        } else {
            query.put(END, dateQuery.getQueryEndTime());
        }
        query.put(START, dateQuery.getQueryStartTime());
        // 将需用分号拼接的tag点拆分，放入columns中，
        // 例如 "ST4_L1R_SIN_1OreBldBunkLvl_12h_cur;ST4_L1R_SIN_2OreBldBunkLvl_12h_cur" 拆开
        List<String> extraTagNames = columns.stream().filter(e -> e.contains(";")).map(e -> Arrays.asList(e.split(";")))
                .flatMap(Collection::stream).collect(Collectors.toList());
        // 用于查询接口的tag点list-columnsForQuery
        List<String> columnsForQuery = new ArrayList<>();
        columnsForQuery.addAll(columns);
        columnsForQuery.addAll(extraTagNames);
        query.put("tagNames", columnsForQuery);
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String result = httpUtil.postJsonParams(url, jsonString);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject data = Optional.ofNullable(JSONObject.parseObject(result))
                .map(e -> e.getJSONObject("data")).orElse(null);
        if (Objects.isNull(data)) {
            return null;
        }

        return handlerData(columns, data, dateQuery, k);
    }

    /**
     * 处理tag点数据
     * @param columns
     * @param jsonObject
     * @param dateQuery
     * @return
     */
    private List<CellData> handlerData(List<String> columns, JSONObject jsonObject, DateQuery dateQuery, int k) {
        List<CellData> cellDataList = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            String column = columns.get(columnIndex);
            if (StringUtils.isBlank(column)) {
                continue;
            }
            // 需要拆分的单元格，拆分完之后从jsonObject中取出来
            if (column.contains(";")) {
                try {
                    String[] columnSplit = column.split(";");
                    String executeWay = columnSplit[0];
                    List<String> tagNames = new ArrayList<>(Arrays.asList(columnSplit));
                    tagNames.remove(0);
                    final int index = columnIndex;
                    List<CellData> cellDataListOfSpecialValues = tagNames.stream().map(tagName -> handleColumn(tagName, index, jsonObject.getJSONObject(tagName), dateQuery, k))
                            .filter(CollectionUtils::isNotEmpty).flatMap(Collection::stream).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(cellDataListOfSpecialValues)) {
                        continue;
                    }
                    // 有可能返回两行的数据 进行分组
                    Map<Integer, List<CellData>> rowIndexToCellDataListMap = cellDataListOfSpecialValues.stream()
                            .collect(Collectors.groupingBy(CellData::getRowIndex));
                    rowIndexToCellDataListMap.forEach((rowIndex, cellDataListOfRow) -> {
                        List<Double> specialValues = cellDataListOfRow.stream().map(CellData::getCellValue)
                                .filter(Objects::nonNull).map(e -> Double.parseDouble(e.toString())).collect(Collectors.toList());
                        double value = ExcelWriterUtil.executeSpecialList(executeWay, specialValues);
                        cellDataList.add(new CellData(rowIndex, index, value));
                    });
                } catch (Exception e) {
                    log.error(column + " 特殊计算tag点出错", e);
                }
            } else {
                JSONObject data = jsonObject.getJSONObject(column);
                // 处理正常一个tag点的单元格
                List<CellData> singleCellDataList = handleColumn(column, columnIndex, data, dateQuery, k);
                if (CollectionUtils.isNotEmpty(singleCellDataList)) {
                    cellDataList.addAll(singleCellDataList);
                }
            }
        }
        return cellDataList;
    }

    private List<CellData> handleColumn(String column, int columnIndex, JSONObject data, DateQuery dateQuery, int k) {
        List<CellData> cellDataList = new ArrayList<>();
        if (Objects.isNull(data)) {
            return cellDataList;
        }
        Set<String> keys = data.keySet();
        List<Long> timestampList = data.keySet().stream().map(Long::valueOf).collect(Collectors.toList());
        // 按照时间顺序排序
        timestampList.sort(Comparator.comparing((Long::longValue)));

        // 处理一天的tag点
        if (column.contains("_1d_")) {
            // dateRun的前一天22点到22点
            DateQuery dateAheadTwoHourQuery = DateQueryUtil.buildDayAheadTwoHour(dateRun);
            Long dayStartTimeAheadTwoHour = dateAheadTwoHourQuery.getQueryStartTime();
            Long dayEndTimeAheadTwoHour = dateAheadTwoHourQuery.getQueryEndTime();
            Long queryTime = TAG_FORMUALS_GET_DATA_BY_BEGIN_TIME.contains(column) ? dayStartTimeAheadTwoHour : dayEndTimeAheadTwoHour;
            // 如果结果中没有查询所需的时间戳 则返回
            if (timestampList.stream().noneMatch(e -> queryTime.equals(e))) {
                return cellDataList;
            }
            Object value = data.get(queryTime);
            cellDataList.add(new CellData(1, columnIndex, value));
            cellDataList.add(new CellData(2, columnIndex, value));

            return cellDataList;
        }

        // 处理12h的tag点
        Long queryStartTime = dateQuery.getQueryStartTime();
        Long queryEndTime = dateQuery.getQueryEndTime();
        if (timestampList.contains(queryEndTime)) {
            BigDecimal queryStartValue = (BigDecimal) data.get(String.valueOf(queryStartTime));
            BigDecimal queryEndValue = (BigDecimal) data.get(String.valueOf(queryEndTime));
            BigDecimal value;
            if (timestampList.contains(queryStartTime) && TAG_FORMUALS_NEED_TO_SUBTRACT_BEFORE.contains(column)) {
                value = queryEndValue.subtract(queryStartValue);
            } else {
                value = queryEndValue;
                if (TAG_FORMUALS_NEED_TO_MUTIPLY_12.contains(column)) {
                    value = value.multiply(BigDecimal.valueOf(12));
                } else if (TAG_FORMUALS_NEED_TO_divide_100.contains(column)) {
                    value = value.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
                }
            }
            cellDataList.add(new CellData(k, columnIndex, value));
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
        queryParam.put(START, Objects.requireNonNull(dateQuery.getQueryStartTime().toString()));
        queryParam.put(END, Objects.requireNonNull(dateQuery.getQueryEndTime().toString()));
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
        query.put(START, dateQuery.getQueryStartTime());
        query.put(END, dateQuery.getQueryEndTime());
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

    private void handleCumulativeOperationRate(Workbook workbook, List<DateQuery> dateQueries, String version) {
        try {
            List<Double> valueList = new ArrayList<>();
            for (DateQuery dateQuery : dateQueries) {
                JSONObject queryJsonObject = new JSONObject();
                String endDateStr = DateUtil.getFormatDateTime(dateQuery.getEndTime(), DateUtil.fullFormat);
                Date startDate = DateUtil.getAllDayBeginTimeInCurrentMonthBeforeDays(dateQuery.getRecordDate(), 0).get(0);
                String startDateStr = DateUtil.getFormatDateTime(startDate, DateUtil.fullFormat);
                queryJsonObject.put(START, startDateStr);
                queryJsonObject.put(END, endDateStr);
                queryJsonObject.put("method", "avg");
                String [] tagArr = {"ST4_L2R_SIN_ProductRatio_1d_cur"};
                queryJsonObject.put("tagNames", tagArr);
                String data = httpUtil.postJsonParams(getTagValueActionsUrl(version), queryJsonObject.toJSONString());
                JSONObject jsonObject = JSONObject.parseObject(data);
                Double doubleValue = Optional.ofNullable(jsonObject).map(e -> e.getJSONObject("data"))
                        .map(e -> e.getJSONObject("ST4_L2R_SIN_ProductRatio_1d_cur"))
                        .map(e -> e.getDouble("AVG")).orElse(null);
                valueList.add(doubleValue);
            }

            Sheet sheet = workbook.getSheetAt(0);
            Cell cellByValue1 = PoiCustomUtil.getCellByValue(sheet, "{累计作业率1}");
            Cell cellByValue2 = PoiCustomUtil.getCellByValue(sheet, "{累计作业率2}");
            if (Objects.isNull(cellByValue1) || Objects.isNull(cellByValue2)) {
                return;
            }
            Date itemTime = DateUtil.addHours(DateUtil.getDateBeginTime(dateRun), 20);
            List<CellData> cellDataList = new ArrayList<>();
            if (dateRun.getTime() < itemTime.getTime()) {
                ExcelWriterUtil.addCellData(cellDataList, cellByValue1.getRowIndex(), cellByValue1.getColumnIndex(), valueList.get(0));
            } else {
                ExcelWriterUtil.addCellData(cellDataList, cellByValue1.getRowIndex(), cellByValue1.getColumnIndex(), valueList.get(0));
                ExcelWriterUtil.addCellData(cellDataList, cellByValue2.getRowIndex(), cellByValue2.getColumnIndex(), valueList.get(1));
            }
            ExcelWriterUtil.setCellValue(sheet, cellDataList);
        } catch (Exception e) {
            log.error("处理累计作业率出错", e);
        }
    }

    private String getTagValueActionsUrl(String version) {
        return httpProperties.getSJUrlVersion(version) + "/tagValueActions";
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

    private String getYarnRunStatisticsUrl(String version) {
        return httpProperties.getSJUrlVersion(version) + "/customReport/yarnRunStatisticsForDay";
    }

    private YardRunInfo getYarnRunStatisticData(String version, Long timestamp, Integer workType) {
        String url = String.format("%s?clock=%s&workType=%s",getYarnRunStatisticsUrl(version), timestamp, workType);
        String jsonData = httpUtil.get(url);
        YardRunInfoDTO yardRunInfoDTO = JSON.parseObject(jsonData, YardRunInfoDTO.class);
        return Optional.ofNullable(yardRunInfoDTO)
                .map(YardRunInfoDTO::getData).orElse(new YardRunInfo());
    }

    private String getYarnRunStatisticsFor26BUrl(String version) {
        return httpProperties.getSJUrlVersion(version) + "/customReport/yarnRunStatisticsFor26B";
    }

    private YardRunInfoOf26B getYarnRunStatisticsFor26BData(String version, Long timestamp) {
        String url = String.format("%s?clock=%s", getYarnRunStatisticsFor26BUrl(version), timestamp);
        String jsonData = httpUtil.get(url);
        SuccessEntity<YardRunInfoOf26B> successEntity = JSON.parseObject(jsonData,
                new TypeReference<SuccessEntity<YardRunInfoOf26B>>() {});
        return Optional.ofNullable(successEntity).map(SuccessEntity::getData)
                .orElse(new YardRunInfoOf26B());
    }
}
