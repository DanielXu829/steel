package com.cisdi.steel.module.job.a2.writer;

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
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 炉温管控
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Slf4j
@Component
public class LuwenguankongWriter extends AbstractExcelReadWriter {
    /**
     * 当前行数
     */
    private int rowIndex = 1;

    /**
     * 记录重新排序的序列
     */
    private int tempIndex = 1;

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        rowIndex = 1;
        tempIndex = 1;

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
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                int size = dateQueries.size();
                for (int j = 0; j < size; j++) {
                    DateQuery item = dateQueries.get(j);
                    List<CellData> cellDataList = mapDataHandler(this.rowIndex, getUrl(), columns, item);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }
        return workbook;
    }

    protected List<CellData> mapDataHandler(int rowIndex, String url, List<String> columns, DateQuery dateQuery) {

        List<CellData> cellDataList = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        for (String column : columns) {
            if (StringUtils.isNotBlank(column)) {
                builder.append(column).append(",");
            }
        }

        String tagNames = builder.toString().substring(0, builder.toString().length() - 1);

        Map<String, String> queryParam = getQueryParam(dateQuery, tagNames);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isEmpty(result)) {
            return null;
        }

        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.nonNull(data)) {
            int index = 0;
            for (int i = 0; i < columns.size(); i++) {
                String col = columns.get(i);
                if (StringUtils.isNotBlank(col)) {
                    JSONArray jsonArray = data.getJSONArray(col);
                    if (Objects.nonNull(jsonArray) && jsonArray.size() > 0) {
                        for (int j = 0; j < jsonArray.size(); j++) {
                            //3.该点名其他时间值关联上一个时间点值
                            dealPart1(jsonArray, col, cellDataList, this.rowIndex, index, j);
                            this.rowIndex += 1;
                        }
                    }
                    index += 4;
                }
            }
        }

        List<CellData> yeList = new ArrayList<>();
        List<CellData> baiList = new ArrayList<>();
        List<CellData> zhongList = new ArrayList<>();

        List<CellData> dataR = new ArrayList<>();

        for (int i = 0; i < cellDataList.size(); i++) {
            CellData cellData = cellDataList.get(i);
            if ("夜".equals(cellData.getCellValue())) {
                yeList.add(cellData);
            }
            if ("白".equals(cellData.getCellValue())) {
                baiList.add(cellData);
            }
            if ("中".equals(cellData.getCellValue())) {
                zhongList.add(cellData);
            }
        }
        dealList(yeList, cellDataList, dataR);
        dealList(baiList, cellDataList, dataR);
        dealList(zhongList, cellDataList, dataR);


        return dataR;
    }

    /**
     * 处理数据先按时间再按班次排序
     *
     * @param list1 待排序的班次集合
     * @param list2 所有的数据集合
     * @param list3 排序后的数据集合
     */
    private void dealList(List<CellData> list1, List<CellData> list2, List<CellData> list3) {
        for (int j = 0; j < list1.size(); j++) {
            CellData data1 = list1.get(j);
            for (int i = 0; i < list2.size(); i++) {
                CellData data2 = list2.get(i);
                if (data1.getRowIndex().intValue() == data2.getRowIndex().intValue()) {
                    CellData temp = new CellData(this.tempIndex, data2.getColumnIndex(), data2.getCellValue());
                    list3.add(temp);
                }
            }
            this.tempIndex += 1;
        }
    }

    /**
     * 对单元格进行填值
     *
     * @param jsonArray    接口返回的数据数组
     * @param col          当前列名
     * @param cellDataList 数据集合
     * @param rowIndex     当前行
     * @param index        当前列
     * @param row          当前数据数组的角标
     */
    private void dealPart1(JSONArray jsonArray, String col, List<CellData> cellDataList, int rowIndex, int index, int row) {
        //1.获取该点名最早的一个值
        JSONObject child = jsonArray.getJSONObject(row);
        //2.查找该最早时的前一个值
        Date clock = child.getDate("clock");
        Integer before = null;
        //处理每一个点名的第一个数值关联
        if (row == 0) {
            before = dealPart(clock, col);
        } else {
            JSONObject fir = jsonArray.getJSONObject(row - 1);
            if (Objects.nonNull(fir)) {
                before = fir.getInteger("val");
            }
        }
        Integer after = child.getInteger("val");
        //如果调整前没值 就等于调整后的值
        if (Objects.isNull(before)) {
            before = after;
        }
        Object curr = after.intValue() - before.intValue();

        //处理班次
        Integer hh = Integer.valueOf(DateUtil.getFormatDateTime(clock, "HH"));
        String bb = "";
        int shiftNum = 0;
        if (0 <= hh.intValue() && hh.intValue() < 8) {
            bb = "夜";
            shiftNum = 1;
        } else if (8 <= hh.intValue() && hh.intValue() < 16) {
            bb = "白";
            shiftNum = 2;
        } else if (16 <= hh.intValue() && hh.intValue() < 24) {
            bb = "中";
            shiftNum = 3;
        }
        //处理班组
        String bz = "";
        Map<String, String> queryParam2 = getQueryParam2(clock, shiftNum);
        String result = httpUtil.get(getUrl2(), queryParam2);
        if (StringUtils.isNotBlank(result)) {
            JSONArray array = JSONObject.parseArray(result);
            if (Objects.nonNull(array) && array.size() != 0) {
                JSONObject jsonObject = array.getJSONObject(0);
                if (Objects.nonNull(jsonObject)) {
                    bz = jsonObject.getString("workTeam");
                }

            }
        }

        //将时间和班次填在前两列
        ExcelWriterUtil.addCellData(cellDataList, rowIndex, 0, clock);
        ExcelWriterUtil.addCellData(cellDataList, rowIndex, 1, bb);
        ExcelWriterUtil.addCellData(cellDataList, rowIndex, 2, bz);

        ExcelWriterUtil.addCellData(cellDataList, rowIndex, index + 3, clock);
        ExcelWriterUtil.addCellData(cellDataList, rowIndex, index + 4, before);
        ExcelWriterUtil.addCellData(cellDataList, rowIndex, index + 5, after);
        ExcelWriterUtil.addCellData(cellDataList, rowIndex, index + 6, curr);
    }

    /**
     * 查询前一个的处理值
     *
     * @param date
     * @param tagName
     * @return
     */
    private Integer dealPart(Date date, String tagName) {
        Map<String, String> queryParam = getQueryParam1(date, tagName);
        String result = httpUtil.get(getUrl1(), queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }

        Integer val = null;

        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.nonNull(data)) {
            JSONArray jsonArray = data.getJSONArray(tagName);
            if (Objects.nonNull(jsonArray) && jsonArray.size() > 0) {
                JSONObject first = jsonArray.getJSONObject(0);
                val = first.getInteger("val");
            }
        }
        return val;
    }

    protected Map<String, String> getQueryParam(DateQuery dateQuery, String tagNames) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("tagNames", tagNames);
        return result;
    }

    protected Map<String, String> getQueryParam1(Date date, String tagName) {
        Map<String, String> result = new HashMap<>();
        result.put("date", DateUtil.getFormatDateTime(date, "yyyy/MM/dd HH:mm:ss"));
        result.put("tagName", tagName);
        return result;
    }

    protected Map<String, String> getQueryParam2(Date clock, int shiftNum) {
        Map<String, String> result = new HashMap<>();
        result.put("date", DateUtil.getFormatDateTime(DateUtil.getDateBeginTime(clock), "yyyy/MM/dd HH:mm:ss"));
        result.put("shift", shiftNum + "");
        return result;
    }

    protected String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/jhTagValue/getTagValue";
    }

    protected String getUrl1() {
        return httpProperties.getUrlApiJHOne() + "/jhTagValue/getTagValLastestByDate";
    }

    protected String getUrl2() {
        return httpProperties.getUrlApiJHOne() + "/cokeActualPerformance/getCokeActuPerfByDateAndShift";
    }

}
