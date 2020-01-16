package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
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
            // 填充

            // 出铁(反面)
            handleTapData(excelDTO, workbook, version);

            // 全局替换 当前日期

            Sheet sheet1 = workbook.getSheetAt(0);
            Date currentDate = new Date();
            ExcelWriterUtil.replaceCurrentDateInTitle(sheet1, "%当前日期%", currentDate);
            Sheet sheet2 = workbook.getSheetAt(1);
            ExcelWriterUtil.replaceCurrentDateInTitle(sheet2, "%当前日期%", currentDate);
        } catch (Exception e) {
            log.error("处理模板数据失败", e);
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
                                case "{变料.大烧}": {
                                    // （（chargeVarMaterial.typ =2   weight 和 ） 减去 （brandcode=CokeNut的weight））  再除以 （大烧+小烧+球团之和）的百分比
                                    BigDecimal type2WeightSum = chargeVarMaterial.stream()
                                            .filter(p -> p.getTyp() == 2)
                                            .map(ChargeVarMaterial::getWeight)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);// chargeVarMaterial.typ =2  weight 和
                                    BigDecimal val = type2WeightSum.subtract(cokeNutWeight).divide(jiaoPiWeightSum, BigDecimal.ROUND_HALF_UP, 4).multiply(new BigDecimal(100));
                                    ExcelWriterUtil.addCellData(cellDataList, j, columnIndex + i, val);
                                    break;
                                }
                                case "{变料.小烧}": {
                                    // （chargeVarMaterial.typ =3   weight 和） 除以 （大烧+小烧+球团之和）的百分比
                                    BigDecimal type3WeightSum = chargeVarMaterial.stream()
                                            .filter(p -> p.getTyp() == 3)
                                            .map(ChargeVarMaterial::getWeight)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);// chargeVarMaterial.typ =3  weight 和
                                    BigDecimal val = type3WeightSum.divide(jiaoPiWeightSum, BigDecimal.ROUND_HALF_UP, 4).multiply(new BigDecimal(100));
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
            // 隐藏标记行
            sheet.getRow(itemRowNum).setZeroHeight(true);
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
