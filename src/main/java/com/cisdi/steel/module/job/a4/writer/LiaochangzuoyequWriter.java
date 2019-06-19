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
import com.sun.javafx.collections.MappingChange;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;


/**
 * 料场作业区
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class LiaochangzuoyequWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        return getMapHandler1(getUrl(), excelDTO);
    }

    protected Workbook getMapHandler1(String url, WriterExcelDTO excelDTO) {
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
                if ("tag".equals(sheetSplit[1])) {
                    List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                    List<CellData> cellDataList = mapDataHandler(url, date, columns);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else {
                    List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                    List<CellData> cellDataList = mapDataHandler2(getUrl1(), columns);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }
        return workbook;
    }

    protected List<CellData> mapDataHandler(String url, DateQuery dateQuery, List<String> columns) {
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
        return handlerJsonArray(data, columns);
    }

    protected List<CellData> mapDataHandler2(String url, List<String> columns) {
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("typename", "1");
        String result = httpUtil.get(url, queryParam);
        List<CellData> cellDataList = new ArrayList<>();
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONArray arr = jsonObject.getJSONArray("data");
                if (Objects.nonNull(arr) && arr.size() > 0) {
                    for (int i = 0; i < arr.size(); i++) {
                        JSONObject data = arr.getJSONObject(i);
                        List<CellData> cellDataList1 = ExcelWriterUtil.handlerRowData(columns, i + 1, data);
                        cellDataList.addAll(cellDataList1);
                    }
                }
            }
        }
        return cellDataList;
    }

    protected List<CellData> handlerJsonArray(JSONObject data, List<String> columns) {
        List<CellData> cellDataList = new ArrayList<>();
        //主要设备运行情况 0 1 2 3
        dealardDev(cellDataList, data, columns);

        //单种配料料仓 5 6 7
        dealBinLevelTrBH(cellDataList, data);

        //烧结混匀料仓 9 10
        dealBinLevelTrST(cellDataList, data);

        //6＃高炉料仓 12 13 14
        dealBinLevelTrJT6(cellDataList, data, "binLevelTrJT6", 12);
        //7＃高炉料仓 16 17 18
        dealBinLevelTrJT6(cellDataList, data, "binLevelTrJT7", 16);
        //8＃高炉料仓 20 21 22
        dealBinLevelTrJT6(cellDataList, data, "binLevelTrJT8", 20);

        //物料输送作业 23-29
        dealFpRun(cellDataList, data);

        //堆料作业 31 32 /  34 35
        dealBlendPileShift(cellDataList, data);
        //发料作业 37 38 /  40 41
        dealBlendSendShift(cellDataList, data);
        return cellDataList;
    }

    /**
     * 发料作业
     *
     * @param cellDataList
     * @param data
     */
    private void dealBlendSendShift(List<CellData> cellDataList, JSONObject data) {
        JSONArray binLevelTrBH = data.getJSONArray("blendSendShift");
        if (Objects.nonNull(binLevelTrBH) && binLevelTrBH.size() > 0) {
            //37
            int index5 = 0;
            int index6 = 0;
            for (int i = 0; i < binLevelTrBH.size(); i++) {
                JSONObject object = binLevelTrBH.getJSONObject(i);
                //单元号
                String unitNo = object.getString("unitNo");
                //堆号
                String pileCode = object.getString("pileCode");
                //当班发料湿吨
                String shiftSendWetWgt = object.getString("shiftSendWetWgt");
                //累计发料湿吨
                String pileSendWetWgt = object.getString("pileSendWetWgt");
                //当班发料时间
                Double shiftSendTime = object.getDouble("shiftSendTime");
                //累计发料时间
                Double pileSendTime = object.getDouble("pileSendTime");
                //预计发堆班次
                String shiftNo = object.getString("shiftNo");
                //预计封堆班数
                String closureShiftCount = object.getString("closureShiftCount");
                //预计发料班数
                String sendShiftCount = object.getString("sendShiftCount");
                //大堆或直拨
                String sendTag = object.getString("sendTag");

                if (StringUtils.isNotBlank(unitNo)) {
                    if ("YL5".equals(unitNo)) {
                        int c = index5 + 37;
                        ExcelWriterUtil.addCellData(cellDataList, 0, c, pileCode);
                        ExcelWriterUtil.addCellData(cellDataList, 1, c, shiftSendWetWgt);
                        ExcelWriterUtil.addCellData(cellDataList, 2, c, pileSendWetWgt);
                        ExcelWriterUtil.addCellData(cellDataList, 3, c, shiftSendTime);
                        ExcelWriterUtil.addCellData(cellDataList, 4, c, pileSendTime);
                        ExcelWriterUtil.addCellData(cellDataList, 5, c, shiftNo);
                        ExcelWriterUtil.addCellData(cellDataList, 6, c, closureShiftCount);
                        ExcelWriterUtil.addCellData(cellDataList, 7, c, sendShiftCount);
                        ExcelWriterUtil.addCellData(cellDataList, 8, c, sendTag);
                        index5++;
                    } else if ("YL6".equals(unitNo)) {
                        int c = index6 + 40;
                        ExcelWriterUtil.addCellData(cellDataList, 0, c, pileCode);
                        ExcelWriterUtil.addCellData(cellDataList, 1, c, shiftSendWetWgt);
                        ExcelWriterUtil.addCellData(cellDataList, 2, c, pileSendWetWgt);
                        ExcelWriterUtil.addCellData(cellDataList, 3, c, shiftSendTime);
                        ExcelWriterUtil.addCellData(cellDataList, 4, c, pileSendTime);
                        ExcelWriterUtil.addCellData(cellDataList, 5, c, shiftNo);
                        ExcelWriterUtil.addCellData(cellDataList, 6, c, closureShiftCount);
                        ExcelWriterUtil.addCellData(cellDataList, 7, c, sendShiftCount);
                        ExcelWriterUtil.addCellData(cellDataList, 8, c, sendTag);
                        index6++;
                    }
                }
            }
        }
    }

    /**
     * 堆料作业
     *
     * @param cellDataList
     * @param data
     */
    private void dealBlendPileShift(List<CellData> cellDataList, JSONObject data) {
        JSONArray binLevelTrBH = data.getJSONArray("blendPileShift");
        if (Objects.nonNull(binLevelTrBH) && binLevelTrBH.size() > 0) {
            //31
            int index5 = 0;
            int index6 = 0;
            for (int i = 0; i < binLevelTrBH.size(); i++) {
                JSONObject object = binLevelTrBH.getJSONObject(i);
                //单元号
                String unitNo = object.getString("unitNo");
                //堆号
                String pileCode = object.getString("pileCode");
                //堆计划量干吨
                String pileDriedPlanWgt = object.getString("pileDriedPlanWgt");
                //当班堆料干吨
                String shiftDriedFactWgt = object.getString("shiftDriedFactWgt");
                //当班堆料湿吨
                String shiftWetFactWgt = object.getString("shiftWetFactWgt");
                //本堆累计干吨
                String pileDriedFactWgt = object.getString("pileDriedFactWgt");
                //本堆累计湿吨
                String pileWetFactWgt = object.getString("pileWetFactWgt");
                //当班堆料层数
                String shiftFactLayer = object.getString("shiftFactLayer");
                //本堆累计层数
                String pileFactLayer = object.getString("pileFactLayer");
                //当班堆料时间
                BigDecimal shiftDate = object.getBigDecimal("shiftRunTime");
                //累计堆料时间
                BigDecimal pileDate = object.getBigDecimal("pileRunTime");
                //预计封堆班次
                String shiftNo = object.getString("shiftNo");

                if (StringUtils.isNotBlank(unitNo)) {
                    if ("YL5".equals(unitNo)) {
                        int c = index5 + 31;
                        ExcelWriterUtil.addCellData(cellDataList, 0, c, pileCode);
                        ExcelWriterUtil.addCellData(cellDataList, 1, c, pileDriedPlanWgt);
                        ExcelWriterUtil.addCellData(cellDataList, 2, c, shiftDriedFactWgt);
                        ExcelWriterUtil.addCellData(cellDataList, 3, c, shiftWetFactWgt);
                        ExcelWriterUtil.addCellData(cellDataList, 4, c, pileDriedFactWgt);
                        ExcelWriterUtil.addCellData(cellDataList, 5, c, pileWetFactWgt);
                        ExcelWriterUtil.addCellData(cellDataList, 6, c, shiftFactLayer);
                        ExcelWriterUtil.addCellData(cellDataList, 7, c, pileFactLayer);
                        ExcelWriterUtil.addCellData(cellDataList, 8, c, shiftDate);
                        ExcelWriterUtil.addCellData(cellDataList, 9, c, pileDate);
                        ExcelWriterUtil.addCellData(cellDataList, 10, c, shiftNo);
                        index5++;
                    } else if ("YL6".equals(unitNo)) {
                        int c = index6 + 34;
                        ExcelWriterUtil.addCellData(cellDataList, 0, c, pileCode);
                        ExcelWriterUtil.addCellData(cellDataList, 1, c, pileDriedPlanWgt);
                        ExcelWriterUtil.addCellData(cellDataList, 2, c, shiftDriedFactWgt);
                        ExcelWriterUtil.addCellData(cellDataList, 3, c, shiftWetFactWgt);
                        ExcelWriterUtil.addCellData(cellDataList, 4, c, pileDriedFactWgt);
                        ExcelWriterUtil.addCellData(cellDataList, 5, c, pileWetFactWgt);
                        ExcelWriterUtil.addCellData(cellDataList, 6, c, shiftFactLayer);
                        ExcelWriterUtil.addCellData(cellDataList, 7, c, pileFactLayer);
                        ExcelWriterUtil.addCellData(cellDataList, 8, c, shiftDate);
                        ExcelWriterUtil.addCellData(cellDataList, 9, c, pileDate);
                        ExcelWriterUtil.addCellData(cellDataList, 10, c, shiftNo);
                        index6++;
                    }
                }
            }
        }

    }


    /**
     * 物料输送作业
     *
     * @param cellDataList
     * @param data
     */
    private void dealFpRun(List<CellData> cellDataList, JSONObject data) {
        JSONArray binLevelTrBH = data.getJSONArray("fpRun");
        if (Objects.nonNull(binLevelTrBH) && binLevelTrBH.size() > 0) {
            for (int i = 0; i < binLevelTrBH.size(); i++) {
                JSONObject object = binLevelTrBH.getJSONObject(i);
                String endMatCode = object.getString("endMatCode");
                String endMatName = object.getString("endMatName");
                String startAddress = object.getString("startAddress");
                String startDevice = object.getString("startDevice");
                String endAddress = object.getString("endAddress");
                String endDevice = object.getString("endDevice");
                String runNetTime = object.getString("runNetTime");

                int r = 23;
                //物料编码
                ExcelWriterUtil.addCellData(cellDataList, i, r, endMatCode);
                //物料名称
                ExcelWriterUtil.addCellData(cellDataList, i, r + 1, endMatName);
                //起点地址
                ExcelWriterUtil.addCellData(cellDataList, i, r + 2, startAddress);
                //起点设备
                ExcelWriterUtil.addCellData(cellDataList, i, r + 3, startDevice);
                //终点地址
                ExcelWriterUtil.addCellData(cellDataList, i, r + 4, endAddress);
                //终点设备
                ExcelWriterUtil.addCellData(cellDataList, i, r + 5, endDevice);
                //作业时间
                ExcelWriterUtil.addCellData(cellDataList, i, r + 6, runNetTime);
            }
        }

    }

    /**
     * 高炉料仓
     *
     * @param cellDataList
     * @param data
     * @param key
     */
    private void dealBinLevelTrJT6(List<CellData> cellDataList, JSONObject data, String key, int index) {
        JSONArray binLevelTrBH = data.getJSONArray(key);
        if (Objects.nonNull(binLevelTrBH) && binLevelTrBH.size() > 0) {
            for (int i = 0; i < binLevelTrBH.size(); i++) {
                JSONObject object = binLevelTrBH.getJSONObject(i);
                String binNo = object.getString("binNo");
                String matName = object.getString("matName");
                String binLevelValue = object.getString("binLevelValue");

                //高炉料仓
                ExcelWriterUtil.addCellData(cellDataList, i, index, binNo);
                //物料名称
                ExcelWriterUtil.addCellData(cellDataList, i, index + 1, matName);
                //料位情况
                ExcelWriterUtil.addCellData(cellDataList, i, index + 2, binLevelValue);
            }
        }

    }

    /**
     * 烧结混匀料仓
     *
     * @param cellDataList
     * @param data
     */
    private void dealBinLevelTrST(List<CellData> cellDataList, JSONObject data) {
        JSONArray binLevelTrBH = data.getJSONArray("binLevelTrST");
        if (Objects.nonNull(binLevelTrBH) && binLevelTrBH.size() > 0) {
            for (int i = 0; i < binLevelTrBH.size(); i++) {
                JSONObject object = binLevelTrBH.getJSONObject(i);
                String matName = object.getString("matName");
                String binLevelValue = object.getString("binLevelValue");

                //物料名称
                ExcelWriterUtil.addCellData(cellDataList, i, 9, matName);
                //料位情况
                ExcelWriterUtil.addCellData(cellDataList, i, 10, binLevelValue);
            }
        }

    }

    /**
     * 单种配料料仓
     *
     * @param cellDataList
     * @param data
     */
    private void dealBinLevelTrBH(List<CellData> cellDataList, JSONObject data) {
        JSONArray binLevelTrBH = data.getJSONArray("binLevelTrBH");
        if (Objects.nonNull(binLevelTrBH) && binLevelTrBH.size() > 0) {
            for (int i = 0; i < binLevelTrBH.size(); i++) {
                JSONObject object = binLevelTrBH.getJSONObject(i);
                String binSeq = object.getString("binSeq");
                String matName = object.getString("matName");
                String binLevelValue = object.getString("binLevelValue");

                //单种配料料仓
                ExcelWriterUtil.addCellData(cellDataList, i, 5, binSeq);
                //物料名称
                ExcelWriterUtil.addCellData(cellDataList, i, 6, matName);
                //料位情况
                ExcelWriterUtil.addCellData(cellDataList, i, 7, binLevelValue);
            }
        }

    }

    /**
     * 主要设备运行情况
     *
     * @param cellDataList
     * @param data
     */
    private void dealardDev(List<CellData> cellDataList, JSONObject data, List<String> columns) {
        JSONArray yardDev = data.getJSONArray("yardDev");
        if (Objects.nonNull(yardDev) && yardDev.size() > 0) {
            Map<String, Map> key = new HashMap();
            Map<String, String> val = new HashMap();
            for (int i = 0; i < yardDev.size(); i++) {
                JSONObject object = yardDev.getJSONObject(i);
                //设备名
                String devName = object.getString("devName");
                //堆料S,取料R,直拨D
                String workType = object.getString("workType");
                String netTime = object.getString("netTime");
                if (key.containsKey(devName)) {
                    Map<String, String> map = key.get(devName);
                    if (!map.containsKey(workType)) {
                        map.put(workType, netTime);
                        key.put(devName, map);
                    }
                } else {
                    val = new HashMap();
                    val.put(workType, netTime);
                    key.put(devName, val);
                }
            }

            Set<String> keySet = key.keySet();
            int r = 0;
            for (String col : columns) {
                if (StringUtils.isNotBlank(col)) {
                    //设备名称
                    ExcelWriterUtil.addCellData(cellDataList, r, 0, col);
                    Map<String, String> map = key.get(col);
                    if (Objects.nonNull(map)) {
                        //堆料时间(分)
                        ExcelWriterUtil.addCellData(cellDataList, r, 1, map.get("S"));
                        //直拨时间(分)
                        ExcelWriterUtil.addCellData(cellDataList, r, 2, map.get("D"));
                        //取料时间(分)
                        ExcelWriterUtil.addCellData(cellDataList, r, 3, map.get("R"));
                    }
                    r++;
                }
            }
        }
    }


    @Override
    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("shiftNo", dealShiftNo(dateQuery.getRecordDate()));
//        result.put("shiftNo", "1");
        result.put("shiftDay", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyyMMdd"));
//        result.put("shiftDay", "20190610");
        return result;
    }

    /**
     * 处理班次
     *
     * @param date
     * @return
     */
    private String dealShiftNo(Date date) {
        String shiftNo = "1";
        String formatDateTime = DateUtil.getFormatDateTime(date, "HH");
        int hour = Integer.valueOf(formatDateTime);
        if (0 < hour && hour <= 8) {
            shiftNo = "1";
        } else if (8 < hour && hour <= 16) {
            shiftNo = "2";
        } else if (16 < hour && hour <= 24) {
            shiftNo = "3";
        }
        return shiftNo;
    }

    private String getUrl() {
        return httpProperties.getUrlApiYGLOne() + "/reportManager/materialYardShiftReport";
    }

    private String getUrl1() {
        return httpProperties.getUrlApiYGLOne() + "/materialmanagercontroller/findbyshiftday";
    }

}
