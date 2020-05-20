package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.AnalysisValueDTO;
import com.cisdi.steel.dto.response.gl.TagValueMapDTO;
import com.cisdi.steel.dto.response.gl.TapTPCDTO;
import com.cisdi.steel.dto.response.gl.req.TagQueryParam;
import com.cisdi.steel.dto.response.gl.res.*;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.entity.TargetManagement;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * <p>Description: 8高炉操作管理日记 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/12/28 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class CaoZuoGuanLiRiJiWriter extends BaseGaoLuWriter {

    @Autowired
    protected TargetManagementMapper targetManagementMapper;

    /**
     * @param excelDTO 数据
     * @return
     */
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = "8.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
            log.error("在模板中获取version失败", e);
        }

        try {
            // 正面-小时参数
            handleFacadeXiaoShiCanShu(excelDTO, workbook, version);

            // 正面-风口信息
            handleFengKouXinXi(excelDTO, workbook, version);

            // 正面-变料信息
            handleBianLiaoXinXi(excelDTO, workbook, version);

            // 出铁(反面)
            handleTapData(excelDTO, workbook, version);

            //反面-块矿
            handOreBlockData(excelDTO, workbook, version);

            //反面-焦炭/煤粉
            handAnalysisValue(excelDTO, workbook, version);

            // 反面-烧结矿理化分析
            handAnalysisValues(excelDTO, workbook, version);

        } catch (Exception e) {
            log.error("处理模板数据失败", e);
        } finally {
            Date currentDate = new Date();
            for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet=workbook.getSheetAt(i);
                // 清除标记项(例如:{块矿.矿种})
                if (!Objects.isNull(sheet) && !workbook.isSheetHidden(i)) {
                    // 全局替换 当前日期
                    ExcelWriterUtil.replaceCurrentDateInTitle(sheet, "%当前日期%", currentDate);
                    PoiCustomUtil.clearPlaceHolder(sheet);
                }
            }
        }

        return workbook;
    }

    //开始--------------------正面-小时参数--------------------
    protected void handleFacadeXiaoShiCanShu(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        try {
            String queryUrl = getUrlTagNamesInRange(excelDTO.getTemplate().getSequence(), version);

            // 动态报表生成的模板默认取第二个sheet。
            String sheetName = "_FacadeXiaoShiCanShu_day_hour";
            Sheet sheet = workbook.getSheet(sheetName);
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                DateQuery dateQuery = this.getDateQuery(excelDTO);
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, dateQuery.getRecordDate());
                // evt为后缀的值需要此逻辑，防止取到前一天累计的值。
                DateQuery firstDateQuery = dateQueries.get(0);
                firstDateQuery.setStartTime(DateUtil.getDateBeginTime(firstDateQuery.getEndTime()));
                dateQueries.set(0, firstDateQuery);

                // 直接拿到tag点名, 无需根据别名再去获取tag点名
                List<String> tagNames = PoiCustomUtil.getFirstRowCelVal(sheet);
                for (int rowNum = 0; rowNum < dateQueries.size(); rowNum++) {
                    List<CellData> cellDataList = handleEachRowData(tagNames, queryUrl, dateQueries.get(rowNum), rowNum + 1);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
            // TODO 煤枪数支
            // TODO 齿轮箱水温差℃
        } catch (Exception e) {
            log.error("处理正面-小时参数出错", e);
        }
    }

    /**
     * 处理每行数据
     *
     * @param tagFormulas
     * @param queryUrl
     * @param queryParam
     * @param rowIndex
     * @return
     */
    private List<CellData> handleEachRowData(List<String> tagNames, String queryUrl, DateQuery dateQuery, int rowIndex) {
        List<TargetManagement> targetManagements = targetManagementMapper.selectTargetManagementsByTargetNames(tagNames);
        Map<String, TargetManagement> targetManagementMap = targetManagements.stream().collect(Collectors.toMap(TargetManagement::getTargetName, target -> target));
        List<String> tagFormulas = targetManagements.stream().map(TargetManagement::getTargetFormula).collect(Collectors.toList());
        // 批量查询tag value
        TagQueryParam tagQueryParam = new TagQueryParam(dateQuery.getQueryStartTime(), dateQuery.getQueryEndTime(), tagFormulas);
        String result = httpUtil.postJsonParams(queryUrl, JSON.toJSONString(tagQueryParam));

        List<CellData> resultList = new ArrayList<>();
        if (StringUtils.isNotBlank(result)) {
            TagValueMapDTO tagValueMapDTO = JSON.parseObject(result, TagValueMapDTO.class);
            Map<String, Map<Long, Double>> tagValueMaps = tagValueMapDTO.getData();
            // 获取targetManagement map

            for (int columnIndex = 0; columnIndex < tagNames.size(); columnIndex++) {
                String targetName = tagNames.get(columnIndex);
                if (StringUtils.isNotBlank(targetName)) {
                    TargetManagement targetManagement = targetManagementMap.get(targetName);
                    if (Objects.nonNull(targetManagement)) {
                        Map<Long, Double> tagValueMap = tagValueMaps.get(targetManagement.getTargetFormula());
                        if (Objects.nonNull(tagValueMap)) {
                            TargetManagement tag = targetManagementMap.get(targetName);
                            List<DateQuery> dayEach = DateQueryUtil.buildDayHourOneEach(new Date(dateQuery.getQueryStartTime()), new Date(dateQuery.getQueryEndTime()));
                            handleEachCellData(dayEach, tagValueMap, resultList, rowIndex, columnIndex, tag);
                        }
                    }
                }
            }
        }

        return resultList;
    }

    /**
     * 处理每个单元格数据
     *
     * @param dayEach
     * @param tagValueMap
     * @param resultList
     * @param rowIndex
     * @param columnIndex
     * @param tag
     */
    private void handleEachCellData(List<DateQuery> dayEach, Map<Long, Double> tagValueMap, List<CellData> resultList, int rowIndex, int columnIndex, TargetManagement tag) {
        // 按照时间顺序从老到新排序
        List<Long> clockList = tagValueMap.keySet().stream().sorted().collect(Collectors.toList());
        for (int i = 0; i < dayEach.size(); i++) {
            DateQuery query = dayEach.get(i);
            Date queryStartTime = query.getStartTime();
            Date queryEndTime = query.getEndTime();

            if (tag.getTargetFormula().endsWith("_evt")) {
                //如果是evt结尾的, 取时间范围内最大值
                Double maxVal = 0.0d;
                for (int j = 0; j < clockList.size(); j++) {
                    Long tempTime = clockList.get(j);
                    Date date = new Date(tempTime);
                    if ((date.getTime() >= queryStartTime.getTime()) && (date.getTime() <= queryEndTime.getTime())) {
                        Double defaultVal = tagValueMap.get(tempTime);
                        if (defaultVal > maxVal) {
                            maxVal = defaultVal;
                        }
                    }
                }
                ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, maxVal);
            } else {
                // 其他情况，取时间范围内第一个值。
                for (int j = 0; j < clockList.size(); j++) {
                    Long tempTime = clockList.get(j);
                    Date date = new Date(tempTime);
                    if ((date.getTime() >= queryStartTime.getTime()) && (date.getTime() <= queryEndTime.getTime())) {
                        Double val = tagValueMap.get(tempTime);
                        ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, val);
                        break;
                    }
                }
            }
        }
    }
    //结束--------------------正面-小时参数--------------------

    //开始--------------------正面-风口信息--------------------
    protected void handleFengKouXinXi(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        try {
            Sheet zhengMianSheet = workbook.getSheetAt(0);
            // 获取标志位的坐标
            Cell fengKouZhiJing = PoiCustomUtil.getCellByValue(zhengMianSheet, "风口直径");
            int rowIndex = fengKouZhiJing.getRowIndex();
            int columnIndex = fengKouZhiJing.getColumnIndex();

            // 填充风口信息
            BfBlastMainInfo bfBlastMainInfo = getBfBlastMainInfo(version);
            int fengkouNumMaxIndex = columnIndex + 36;
            List<CellData> cellDataList = new ArrayList<CellData>();
            for (int i = columnIndex + 1; i <= fengkouNumMaxIndex; i++) {
                BfBlastMain bfBlastMain = bfBlastMainInfo.getBfBlastMains().get(i - columnIndex - 1);
                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, bfBlastMain.getBlastDiameter());
            }
            ExcelWriterUtil.setCellValue(zhengMianSheet, cellDataList);

            // TODO 夜班白班

            // 填充其他风口信息
            String queryUrl = getUrlTagNamesInRange(excelDTO.getTemplate().getSequence(), version);
            // 动态报表生成的模板默认取第二个sheet。
            String sheetName = "_FacadeFengKouXinXi_day_hour";
            Sheet sheet = workbook.getSheet(sheetName);
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(new Date());
            // 直接拿到tag点名, 无需根据别名再去获取tag点名
            List<String> tagNames = PoiCustomUtil.getFirstRowCelVal(sheet);
            int rowNum = 1;
            List<CellData> cellData = handleEachRowData(tagNames, queryUrl, dateQuery, rowNum);
            ExcelWriterUtil.setCellValue(sheet, cellData);
        } catch (Exception e) {
            log.error("处理正面-风口信息出错", e);
        }
    }
    //结束--------------------正面-风口信息--------------------


    //开始--------------------正面-变料信息--------------------
    protected void handleBianLiaoXinXi(WriterExcelDTO excelDTO,Workbook workbook,String version){
        try {
            Sheet zhengMianSheet = workbook.getSheetAt(0);
            // 获取标志位的坐标
            Cell piShuCell = PoiCustomUtil.getCellByValue(zhengMianSheet, "{变料.批数}");
            int beginRowIndex = piShuCell.getRowIndex();
            int columnIndex = piShuCell.getColumnIndex();

            Cell qiuTuanCell = PoiCustomUtil.getCellByValue(zhengMianSheet, "{变料.球团}");
            int endRowIndex = qiuTuanCell.getRowIndex();

            // 获取数据并填充
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(new Date());
            List<ChargeVarInfo> chargeVarInfos = getChargeVarInfo(version, dateQuery);
            List<CellData> cellDataList = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(chargeVarInfos)) {
                for (int i = 0; i < chargeVarInfos.size(); i++) {
                    ChargeVarInfo chargeVarInfo = chargeVarInfos.get(i);
                    // 计算相关项
                    List<ChargeVarMaterial> chargeVarMaterial = chargeVarInfo.getChargeVarMaterial();
                    BigDecimal cokeNutWeight = chargeVarMaterial.stream()
                            .filter(p -> ("CokeNut".equals(p.getBrandCode()) && 2 == p.getTyp()))
                            .map(ChargeVarMaterial::getWeight)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);// type=2 的brandcode=CokeNut的weight

                    BigDecimal jiaoPiWeightSum = chargeVarMaterial.stream()
                            .filter(p -> (p.getTyp() != 1 && !"CokeNut".equals(p.getBrandCode())))
                            .map(ChargeVarMaterial::getWeight)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal daShaoWeightSum = chargeVarMaterial.stream()
                            .filter(p -> p.getTyp() == 2)
                            .map(ChargeVarMaterial::getWeight)
                            .reduce(BigDecimal.ZERO, BigDecimal::add).subtract(cokeNutWeight);// （chargeVarMaterial.typ =2   weight 和 ） 减去 （brandcode=CokeNut的weight）

                    BigDecimal xiaoShaoWeightSum = chargeVarMaterial.stream()
                            .filter(p -> p.getTyp() == 3)
                            .map(ChargeVarMaterial::getWeight)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);// chargeVarMaterial.typ =3  weight 和

                    // 循环所有的标志位列
                    for (int j = beginRowIndex; j <= endRowIndex; j++) {
                        Cell flagCell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(zhengMianSheet, j), columnIndex);
                        String itemName = flagCell.getStringCellValue();
                        if (StringUtils.isNotBlank(itemName)) {
                            switch (itemName) {
                                case "{变料.批数}": {
                                    Integer chargeNo = chargeVarInfo.getChargeVarIndex().getChargeNo();
                                    ExcelWriterUtil.addCellData(cellDataList, j, columnIndex + i, chargeNo);
                                    break;
                                }
                                case "{变料.焦批}": {
                                    // chargeVarMaterial.typ =1  weight 和  + type=2 的brandcode=CokeNut的weight
                                    BigDecimal type1WeightSum = chargeVarMaterial.stream()
                                            .filter(p -> p.getTyp() == 1)
                                            .map(ChargeVarMaterial::getWeight)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);// chargeVarMaterial.typ =1  weight 和
                                    BigDecimal val = type1WeightSum.add(cokeNutWeight);
                                    ExcelWriterUtil.addCellData(cellDataList, j, columnIndex + i, val);
                                    break;
                                }
                                case "{变料.矿批}": {
                                    // 大烧 + 小烧
                                    BigDecimal val = daShaoWeightSum.add(xiaoShaoWeightSum);
                                    ExcelWriterUtil.addCellData(cellDataList, j, columnIndex + i, val);
                                    break;
                                }
                                case "{变料.大烧}": {
                                    // （（chargeVarMaterial.typ =2   weight 和 ） 减去 （brandcode=CokeNut的weight））  再除以 （大烧+小烧+球团之和）的百分比
                                    BigDecimal val = daShaoWeightSum.divide(jiaoPiWeightSum, BigDecimal.ROUND_HALF_UP, 4).multiply(new BigDecimal(100));
                                    ExcelWriterUtil.addCellData(cellDataList, j, columnIndex + i, val);
                                    break;
                                }
                                case "{变料.小烧}": {
                                    // （chargeVarMaterial.typ =3   weight 和） 除以 （大烧+小烧+球团之和）的百分比
                                    BigDecimal val = xiaoShaoWeightSum.divide(jiaoPiWeightSum, BigDecimal.ROUND_HALF_UP, 4).multiply(new BigDecimal(100));
                                    ExcelWriterUtil.addCellData(cellDataList, j, columnIndex + i, val);
                                    break;
                                }
                                case "{变料.球团}": {
                                    //（brandCode以PELLETS结尾的 weight之和） 除以 （大烧+小烧+球团之和）的百分比
                                    BigDecimal sum = chargeVarMaterial.stream()
                                            .filter(p -> StringUtils.endsWith(p.getBrandCode(), "PELLETS"))
                                            .map(ChargeVarMaterial::getWeight)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);// （brandCode以PELLETS结尾的 weight之和）
                                    BigDecimal val = sum.divide(jiaoPiWeightSum, BigDecimal.ROUND_HALF_UP, 4).multiply(new BigDecimal(100));
                                    ExcelWriterUtil.addCellData(cellDataList, j, columnIndex + i, val);
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                        }
                    }
                }
                ExcelWriterUtil.setCellValue(zhengMianSheet, cellDataList);
            }
        } catch (Exception e) {
            log.error("处理正面-变料信息出错", e);
        }
    }
    //结束--------------------正面-变料信息--------------------

    /**
     * 通过tag点拿数据的API，根据sequence和version返回不同工序的api地址
     *
     * @param sequence
     * @param version
     * @return
     */
    protected String getUrlTagNamesInRange(String sequence, String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }

    protected void handleFacadeXiaoShiCanShu(Workbook workbook, String version) {
        try {
            log.debug("处理 正面 - 小时参数 部分");
        } catch (Exception e) {
            log.error("处理 正面 - 小时参数 部分产生错误", e);
        }
    }

    // 反面-出铁
    protected void handleTapData(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        // 需要写入的单元格数据对象
        List<CellData> resultList = new ArrayList<>();
        try {
            // 反面sheet
            Sheet sheet = workbook.getSheetAt(1);
            DateQuery date = this.getDateQuery(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());
            // 处理出铁数据
            List<String> tapNoList = handleTapData(sheet, dateQuery, resultList, version);
            // 处理罐号重量数据
            if (CollectionUtils.isNotEmpty(tapNoList)) {
                handleTpcInfoData(sheet, resultList, tapNoList, version);
            }

            ExcelWriterUtil.setCellValue(sheet, resultList);
        } catch (Exception e) {
            log.error("处理反面-出铁参数出错", e);
        }
    }

    /**
     * 处理出铁数据
     * @param sheet
     * @param dateQuery
     * @param resultList
     * @param version
     * @return tapNoList
     */
    private List<String> handleTapData(Sheet sheet, DateQuery dateQuery, List<CellData> resultList, String version) {
        // 铁次(tapNo)list，用于调接口罐号重量接口
        ArrayList<String> tapNoList = new ArrayList<>();
        int itemRowNum = 6;
        try {
            // 隐藏标记行
            sheet.getRow(itemRowNum).setZeroHeight(true);
            // 1、组装出铁数据URL
            Map<String, String> queryParam = this.getQueryParam(dateQuery);
            queryParam.put("pagenum", "1");
            queryParam.put("pagesize", "10");
            // 2、获取数据并反序列化为java对象
            String tapData = httpUtil.get(getTapsInRange(version), queryParam);
            if (StringUtils.isBlank(tapData)) {
                return null;
            }
            PageData<TapSgRow> pageData = JSON.parseObject(tapData, new TypeReference<PageData<TapSgRow>>(){});
            if (Objects.isNull(pageData)) {
                return null;
            }
            List<TapSgRow> tapSgRowData = pageData.getData();
            if (CollectionUtils.isEmpty(tapSgRowData)) {
                return null;
            }
            tapSgRowData.sort(comparing(TapSgRow::getStartTime)); // 按时间先后进行排序
            int dataSize = tapSgRowData.size();
            List<String> itemRow = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
            if (CollectionUtils.isEmpty(itemRow)) {
                return null;
            }
            int itemDataSize = itemRow.size();
            TapSgRow tapSgRow = null;
            // 需要对时间数据进行格式化处理
            String[] timeItemArray = {"startTime", "slagTime", "endTime"};
            List<String> timeItemList = Arrays.asList(timeItemArray);

            // 遍历标记行
            for (int i = 0; i < dataSize; i++) {
                tapSgRow = tapSgRowData.get(i);
                tapNoList.add(tapSgRow.getTapNo());
                // 将对象转为Map,key为对象引用名
                Map<String, Object> stringObjectMap = JSON.parseObject(JSON.toJSONString(tapSgRow), new TypeReference<Map<String, Object>>(){});
                Map<String, Double> tapValues = tapSgRow.getTapValues();
                Map<String, Double> slagAnalysis = tapSgRow.getSlagAnalysis();
                Map<String, Double> hmAnalysis = tapSgRow.getHmAnalysis();

                for (int j = 0; j < itemDataSize; j++) {
                    String itemData = itemRow.get(j);
                    if (StringUtils.isBlank(itemData)) {
                        continue;
                    }
                    String[] itemArray = itemData.split("_");
                    if (itemArray.length == 2 && "item".equals(itemArray[0])) {
                        Object value = stringObjectMap.get(itemArray[1]);
                        // 对时间数据进行格式化处理
                        if (timeItemList.contains(itemArray[1])) {
                            LocalDateTime localDate = Instant.ofEpochMilli(Long.valueOf(String.valueOf(value))).atZone(ZoneId.systemDefault()).toLocalDateTime();
                            value = DateTimeFormatter.ofPattern("HH:mm:ss").format(localDate);
                            // yyyy-MM-dd HH:mm:ss
                        }
                        ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, j, value);
                    } else if (itemArray.length == 3 && "item".equals(itemArray[0])) {
                        switch (itemArray[1]) {
                            case "tapValues":
                                ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, j, tapValues.get(itemArray[2]));
                                break;
                            case "slagAnalysis":
                                ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, j, slagAnalysis.get(itemArray[2]));
                                break;
                            case "hmAnalysis":
                                ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, j, hmAnalysis.get(itemArray[2]));
                                break;
                        }
                    } else {
                        log.warn("excel标记项格式错误:" + itemData);
                    }
                }
            }

            return tapNoList;
        } catch (Exception e) {
            log.error("处理反面-出铁出错", e);
            return tapNoList;
        }
    }

    /**
     * 处理罐号重量数据
     * @param sheet
     * @param resultList
     * @param tapNoList
     * @param version
     */
    private void handleTpcInfoData(Sheet sheet, List<CellData> resultList, List<String> tapNoList, String version) {
        try {
            Cell tpcNoCell = PoiCustomUtil.getCellByValue(sheet, "{罐号}");
            if (Objects.isNull(tpcNoCell)) {
                return;
            }
            int tpcNoBeginRow = tpcNoCell.getRowIndex();
            int tpcNoBeginColumn = tpcNoCell.getColumnIndex();
            // 组装罐号重量数据URL(基于出铁返回的铁次)
            for (int i = 0; i < tapNoList.size(); i++) {
                Map<String, String> queryParam = new HashMap<>();
                queryParam.put("tapNo", tapNoList.get(i));
                String tpcInfoData = httpUtil.get(getTpcInfoByTapNo(version), queryParam);
                if (StringUtils.isBlank(tpcInfoData)) {
                    return;
                }
                TapTPCDTO tapTPCDTO = JSON.parseObject(tpcInfoData, TapTPCDTO.class);
                if (Objects.isNull(tapTPCDTO)) {
                    return;
                }
                List<TapTPC> tapTPCList = tapTPCDTO.getData();
                if (CollectionUtils.isEmpty(tapTPCList)) {
                    return;
                }
                int dataSize = tapTPCList.size();
                for (int j = 0; j < dataSize; j++) {
                    TapTPC tapTPC = tapTPCList.get(j);
                    String tpcNo = tapTPC.getTpcNo();
                    BigDecimal netWt = tapTPC.getNetWt();
                    int writeRow = tpcNoBeginRow + i;
                    int writeColumn = tpcNoBeginColumn + j * 2;
                    ExcelWriterUtil.addCellData(resultList, writeRow, writeColumn, tpcNo);
                    ExcelWriterUtil.addCellData(resultList, writeRow, writeColumn + 1, netWt);
                }
            }
        } catch (Exception e) {
            log.error("处理反面-罐号重量出错", e);
        }
    }

    /**
     * 处理反面-块矿-矿种数据
     *
     * @param sheet
     * @param dateQuery
     * @param resultList
     * @param version
     * @param type
     * @return
     */
    protected void handOreBlockData(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        String brandCodeType = "LUMPORE";
        String oreBlockType = "LC";
        // 待填充的数据
        String[] arr = {"TFe", "Al2O3", "CaO", "MgO", "SiO2", "S" ,"P"};
        List<CellData> cellDataList = new ArrayList<>();
        // 反面sheet
        Sheet sheet = null;
        try {
            sheet = workbook.getSheetAt(1);
            DateQuery date = this.getDateQuery(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());

            // 1. 找到待填充的坐标
            Cell tpcNoCell = PoiCustomUtil.getCellByValue(sheet, "{块矿.矿种}");
            if (Objects.isNull(tpcNoCell)) {
                return;
            }
            // 待填充的行,列
            int tpcNoBeginRow = tpcNoCell.getRowIndex();
            int tpcNoBeginColumn = tpcNoCell.getColumnIndex();
            // 检查表头列是否存在，如果不存在则不填充数据
            checkHeaders(sheet, arr, new HashMap<>(), tpcNoBeginRow-1);
            // 2.获取brandCode
            List<String> brandCodeList = getBrandCodeData(version, dateQuery, brandCodeType);
            String endTime = Objects.requireNonNull(dateQuery.getEndTime().getTime()).toString();
            if (brandCodeList.isEmpty()) {
                return;
            }
            // 2.遍历brandCodeList取出所有数据
            for (int i = 0; i < brandCodeList.size(); i++) {
                // 3. 根据brandCode和 type调接口获取数据
                String url = getAnalysisValueUrl(version) + "/" + endTime;
                Map<String, String> queryParam = new HashMap();
                queryParam.put("brandcode", brandCodeList.get(i));
                queryParam.put("type", oreBlockType);

                setAnalysisValue2Cell(sheet, cellDataList, arr, url, queryParam,
                        tpcNoBeginRow + i, tpcNoBeginColumn);
            }
            ExcelWriterUtil.setCellValue(sheet, cellDataList);
        } catch (Exception e) {
            log.error("处理反面-块矿-矿种出错", e);
        }
    }

    /**
     * 取出AnalysisValue中的data，并给相应的cell赋值
     * @param sheet
     * @param cellDataList
     * @param arr
     * @param url
     * @param queryParam
     * @param tpcNoBeginRow
     * @param tpcNoBeginColumn
     * @return
     */
    private void setAnalysisValue2Cell(Sheet sheet, List<CellData> cellDataList, String[] arr, String url,
                                             Map<String, String> queryParam, int tpcNoBeginRow, int tpcNoBeginColumn) {
        String jsonData = httpUtil.get(url, queryParam);
        // 根据json映射对象DTO
        AnalysisValueDTO analysisValueDTO = null;
        if (StringUtils.isNotBlank(jsonData)) {
            analysisValueDTO = JSON.parseObject(jsonData, AnalysisValueDTO.class);
        }
        if (Objects.isNull(analysisValueDTO)) {
            return;
        }
        List<AnalysisValue> oreBlockList = analysisValueDTO.getData();
        if (oreBlockList == null || oreBlockList.isEmpty()) {
            log.warn(url + " 接口中无数据");
            return;
        }
        int dataSize = arr.length;
        for (AnalysisValue analysisValue : oreBlockList) {
            if (oreBlockList.indexOf(analysisValue) > 2) break;
            if (Objects.isNull(analysisValue)) return;
            for (int j = 0; j < dataSize; j++){
                Map<String, BigDecimal> values = analysisValue.getValues();
                if (Objects.isNull(values)) return;
                // 获取具体AnalysisValue数据
                if (!values.containsKey(arr[j])) continue;
                BigDecimal value = values.get(arr[j]);
                int writeRow = tpcNoBeginRow;
                int writeColumn = tpcNoBeginColumn + j;
                // 填充具体数据
                ExcelWriterUtil.addCellData(cellDataList, writeRow, writeColumn, value);
            }
        }
    }

    /**
     * 构造取回AnalysisValue所需要的参数，然后给cell赋值
     * @param sheet
     * @param cellDataList
     * @param url
     * @param arr
     * @param heatPlaceHolder
     * @param brandCode
     * @param type
     * @param isNeedMapping
     * @return
     */
    private void handAnalysisValue2Cell(Sheet sheet, List<CellData> cellDataList, String url, String[] arr,
                                                  String placeHolder, String brandCode, String type, Map<String,String> map) {
        Map<String, String> queryParam = new HashMap();
        queryParam.put("brandcode", brandCode);
        queryParam.put("type", type);
        // 找到待填充的坐标
        Cell tpcNoCell = PoiCustomUtil.getCellByValue(sheet, placeHolder);
        if (Objects.isNull(tpcNoCell)) {
            return;
        }
        // 待填充的行,列
        int tpcNoBeginRow = tpcNoCell.getRowIndex();
        int tpcNoBeginColumn = tpcNoCell.getColumnIndex();
        // 检查表头列是否存在，如果不存在则不填充数据
        checkHeaders(sheet, arr, map, tpcNoBeginRow-1);

        setAnalysisValue2Cell(sheet, cellDataList, arr, url, queryParam,
                tpcNoBeginRow, tpcNoBeginColumn);
    }

    /**
     * 处理AnalysisValue相关的数据，包含成分分析(焦炭和煤粉)和热力强度
     * @param excelDTO
     * @param workbook
     * @param version
     */
    protected void handAnalysisValue(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        String heatType = "LG";
        String cokeType = "LC";
        String cokeBrandCode = "KM-L_COKE";
        String coalBrandCode = "FBFM-A_COAL";
        String heatPlaceHolder = "{热强度}";
        String cokePlaceHolder = "{焦炭}";
        String coalPlaceHolder = "{煤粉}";
        String[] heatArr = {"M40", "M10", "CSR", "CRI"};
        String[] cokeArr = {"H2O", "Ad", "Vdaf", "C", "S"};
        // 根据需求，取回的煤粉数据中C取Fcad，S取Std
        String[] coalArr = {"H2O", "Ad", "Vdaf", "Fcad", "Std"};
        Map<String,String> map = new HashMap<String, String>(){{
            put("Fcad","C");
            put("Std","S");
        }};
        List<CellData> cellDataList = new ArrayList<>();
        try {
            // 反面sheet
            Sheet sheet = workbook.getSheetAt(1);
            DateQuery date = this.getDateQuery(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());
            String endTime = Objects.requireNonNull(dateQuery.getEndTime().getTime()).toString();
            // 1. 获取AnalysisValue Url前缀
            String url = getAnalysisValueUrl(version) + "/" + endTime;
            //先处理热力强度，因为只有一行数据
            //再处理成分分析，分为煤粉和焦炭
            // 2. 获取热强度数据，填充数据
            handAnalysisValue2Cell(sheet, cellDataList, url, heatArr, heatPlaceHolder, cokeBrandCode, heatType, new HashMap<>());
            // 3. 获取焦炭数据，填充数据
            handAnalysisValue2Cell(sheet, cellDataList, url, cokeArr, cokePlaceHolder, cokeBrandCode, cokeType, new HashMap<>());
            // 4. 获取煤粉数据，填充数据
            handAnalysisValue2Cell(sheet, cellDataList, url, coalArr, coalPlaceHolder, coalBrandCode, cokeType, map);

            ExcelWriterUtil.setCellValue(sheet, cellDataList);
        } catch (Exception e) {
            log.error("处理反面-块矿-矿种出错", e);
        }
    }

    /**
     * 表头节点不存在或者不在指定行，若表头节点被删除则该列不填充数据
     * 某些表头节点名也许不是返回数据节点名，映射关系存放在headerMap中
     * @param sheet
     * @param headers
     * @param headerMap
     * @param rowIndex
     */
    private void checkHeaders(Sheet sheet, String[] headers, Map<String, String> headerMap, int rowIndex) {

        List<String> arrList = new ArrayList<String>(Arrays.asList(headers));
        for (String item :headers) {
            Cell cell = null;
            if (headerMap.containsKey(item)) {
                cell = PoiCustomUtil.getCellByValue(sheet, headerMap.get(item));
            } else {
                cell = PoiCustomUtil.getCellByValue(sheet, item);
            }
            List<String> list = PoiCustomUtil.getRowCelVal(sheet, rowIndex);
            if (Objects.isNull(cell)) {
                arrList.remove(item);
            } else if (list != null && !list.contains(item)) {
                arrList.remove(item);
            }
        }
        headers = arrList.toArray(new String[arrList.size()]);
    }

    /**
     * 处理烧结矿理化数据，时间和编号以LC为主显示，LP没有数据则空着
     * @param sheet
     * @param brandCode
     * @param pageNum
     * @param result
     * @param cellDataList
     */
    private void handAnalysisValuesData(Sheet sheet, String brandCode, String pageNum, String result, List<CellData> cellDataList) {
        String largerType = "LC";
        String smallerType = "LP";
        String placeHolder = "{矿种}";
        String[] largerArr = {"TFe", "FeO", "CaO", "MgO", "SiO2", "S", "B2", "DI"};
        String[] smallerArr = {"S+40", "S25-40", "S16-25", "S10-16", "S5-10", "S-5", "S-10", "抗磨"};
        String[] arr = {"矿种", "clock","sampleid", "槽号"};

        // TODO 矿种 槽号 DI 抗磨
        // 矿种坐标
        Cell cell = PoiCustomUtil.getCellByValue(sheet, placeHolder);
        if (Objects.isNull(cell)) {
            return;
        }
        // 待填充的行,列
        int beginRow = cell.getRowIndex();
        int coalBeginColumn = 1;
        // LC数据起始列
        int largerBeginColumn = coalBeginColumn + 9;
        // LP数据起始列
        int smallerBeginColumn = largerBeginColumn + 16;

        // 表头R对应节点数据B2
        checkHeaders(sheet, largerArr, new HashMap<String, String>(){{
            put("B2","R");
        }}, beginRow-1);
        // 检查表头列是否存在，如果不存在则不填充数据
        checkHeaders(sheet, smallerArr, new HashMap<String, String>(){{
            put("S+40",">40");
            put("S25-40",">40");
            put("S16-25",">40");
            put("S5-10",">40");
            put("S-5",">40");
            put("S-10",">40");
        }}, beginRow-1);

        // 根据json映射对象DTO
        AnalysisValueDTO analysisValueDTO = null;
        if (StringUtils.isNotBlank(result)) {
            analysisValueDTO = JSON.parseObject(result, AnalysisValueDTO.class);
        }
        if (Objects.isNull(analysisValueDTO)) {
            return;
        }
        List<AnalysisValue> lcList = analysisValueDTO.getData().stream().filter(item -> item.getAnalysis().getType().
                equals("LC")).collect(Collectors.toList());
        List<AnalysisValue> lpList = analysisValueDTO.getData().stream().filter(item -> item.getAnalysis().getType().
                equals("LP")).collect(Collectors.toList());
        List<AnalysisValue> largerList = lcList;
        List<AnalysisValue> smallerList =lpList;
        if (largerList == null || largerList.isEmpty()) {
            return;
        }
        // i < 8最多8行数据
        for (int i = 0; i< largerList.size() && i < 8; i++) {
            AnalysisValue analysisValue = largerList.get(i);
            if (Objects.isNull(analysisValue)) continue;
            Analysis analysis = analysisValue.getAnalysis();
            final String sampleid = analysis != null ? analysis.getSampleid() : null;
            String type = analysis != null ? analysis.getType() : null;
            // 处理矿种、记录时间、编号、槽号
            if (!Objects.isNull(analysis)) {
                //TODO 填充矿种数据
                // 记录时间
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Date clock = analysis.getClock();
                String time = sdf.format(clock);
                if (StringUtils.isNotBlank(time)) {
                    ExcelWriterUtil.addCellData(cellDataList, beginRow + i, coalBeginColumn + 3, time);
                }
                // 编号
                if (StringUtils.isNotBlank(sampleid)) {
                    ExcelWriterUtil.addCellData(cellDataList, beginRow + i, coalBeginColumn + 6, sampleid);
                }
                // TODO 槽号
            }
            Map<String, BigDecimal> values = analysisValue.getValues();
            if (Objects.isNull(values)) continue;
            for (int j = 0; j < largerArr.length; j++){
                // 获取具体AnalysisValue数据
                if (!values.containsKey(largerArr[j])) continue;
                BigDecimal value = values.get(largerArr[j]);
                // 合并的单元格占两格
                int writeColumn = largerBeginColumn + j*2;
                if (j > 1 && type.equals("LP")) {
                    // 未合并的单元格
                    writeColumn = largerBeginColumn + j + 2;
                }
                ExcelWriterUtil.addCellData(cellDataList, beginRow + i, writeColumn, value);
            }
            if (smallerList != null && !smallerList.isEmpty() && sampleid != null) {
                Optional<AnalysisValue> optional = smallerList.stream().filter(item ->item.getAnalysis().
                        getSampleid().equals(sampleid)).findFirst();
                // 存在
                if (optional.isPresent()) {
                    AnalysisValue temp =  optional.get();
                    if (Objects.isNull(temp)) continue;
                    Map<String, BigDecimal> tempValues = temp.getValues();
                    type = temp.getAnalysis().getType();
                    if (Objects.isNull(tempValues)) continue;
                    for (int k = 0; k < smallerArr.length; k++) {
                        // 获取具体AnalysisValue数据
                        if (!tempValues.containsKey(smallerArr[k])) continue;
                        BigDecimal tempValue = tempValues.get(smallerArr[k]);
                        // 合并的单元格占两格
                        int column = smallerBeginColumn + k*2;
                        if (k > 1 && type.equals("LP")) {
                            // 未合并的单元格
                            column = smallerBeginColumn + k + 2;
                        }
                        // 填充具体type = LC数据
                        ExcelWriterUtil.addCellData(cellDataList, beginRow + i, column, tempValue);
                    }
                }
            }
        }
    }

    /**
     * 处理烧结矿理化分析数据
     * @param excelDTO
     * @param workbook
     * @param version
     */
    protected void handAnalysisValues(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        String brandCode = "S4_SINTER";
        String pageNum = "1";
        String type = "ALL";
        List<CellData> cellDataList = new ArrayList<>();
        try {
            // 反面sheet
            Sheet sheet = workbook.getSheetAt(1);
            DateQuery date = this.getDateQuery(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());
            // 1. 获取AnalysisValues Url前缀
            String url = getAnalysisValuesUrl(version);
            Map<String, String> queryParam = new HashMap();
            queryParam.put("starttime", Objects.requireNonNull(dateQuery.getStartTime().getTime()).toString());
            queryParam.put("endtime", Objects.requireNonNull(dateQuery.getEndTime().getTime()).toString());
            queryParam.put("brandcode", brandCode);
            queryParam.put("pageNum", pageNum);
            queryParam.put("type", type);
            String result = httpUtil.get(url, queryParam);
            handAnalysisValuesData(sheet, brandCode, pageNum, result, cellDataList);

            ExcelWriterUtil.setCellValue(sheet, cellDataList);
        } catch (Exception e) {
            log.error("处理反面-烧结矿理化分析出错", e);
        }
    }

    /**
     * 查询BrandCode,可能有多个，最多三个
     * @param version
     * @param startTime
     * @param endTime
     * @param type
     * @return 包含BrandCode的JSONArray
     */
    private List<String> getBrandCodeData(String version, DateQuery dateQuery, String type) {
        List<String> brandCodeData = new ArrayList<>();
        Map<String, String> queryParam = new HashMap();
        queryParam.put("startTime", Objects.requireNonNull(dateQuery.getStartTime().getTime()).toString());
        queryParam.put("endTime", Objects.requireNonNull(dateQuery.getEndTime().getTime()).toString());
        queryParam.put("type", type);
        String url = getBrandCodes(version);
        String result = httpUtil.get(url, queryParam);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if(null != jsonObject){
            JSONArray data = jsonObject.getJSONArray("data");
            if(null!= data){
                for (Object o : data) {
                    brandCodeData.add(String.valueOf(o));
                }
            }
        }
        return brandCodeData;
    }

    /**
     * 获取高炉的种类Url
     * @param version
     * @return Url的string
     */
    private String getBrandCodes(String version){
        return httpProperties.getGlUrlVersion(version) + "/brandCodes/getBrandCodes";
    }

    /**
     * 高炉API
     * @param version
     * @return
     */
    private String getAnalysisValueUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/analysisValue/clock";
    }

    /**
     * 烧结矿理化分析API
     * @param version
     * @return
     */
    private String getAnalysisValuesUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/analysisValues/clock";
    }

    /**
     * 出铁数据接口
     * @param version
     * @return
     */
    protected String getTapsInRange(String version) {
        return httpProperties.getGlUrlVersion(version) + "/taps/sg/period";
    }

    /**
     * 罐号重量数据接口
     * @param version
     * @return
     */
    protected String getTpcInfoByTapNo(String version) {
        return httpProperties.getGlUrlVersion(version) + "/report/tap/queryTpcInfoByTapNo";
    }
}
