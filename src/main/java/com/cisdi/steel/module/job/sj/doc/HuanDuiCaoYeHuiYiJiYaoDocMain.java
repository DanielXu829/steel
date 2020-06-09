package com.cisdi.steel.module.job.sj.doc;

import cn.afterturn.easypoi.util.PoiPublicUtil;
import cn.afterturn.easypoi.util.PoiWordStyleUtil;
import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.params.ExcelListEntity;
import cn.afterturn.easypoi.word.parse.excel.ExcelEntityParse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.dto.response.sj.*;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.enums.LanguageEnum;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import com.cisdi.steel.module.report.service.ReportIndexService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.afterturn.easypoi.util.PoiElUtil.*;
import static cn.afterturn.easypoi.util.PoiElUtil.eval;
import static java.util.Comparator.comparing;

@Component
@Slf4j
@SuppressWarnings("ALL")
/**
 *
 */
public class HuanDuiCaoYeHuiYiJiYaoDocMain {

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private HttpProperties httpProperties;

    @Autowired
    private JobProperties jobProperties;

    @Autowired
    private ReportIndexMapper reportIndexMapper;

    @Autowired
    private ReportCategoryTemplateService reportCategoryTemplateService;

    @Autowired
    protected ReportIndexService reportIndexService;

    private String version = "4.0";

    /**
     * doc最后结果
     */
    private HashMap<String, Object> result = null;

    @Scheduled(cron = "0 0 23 * * ?")
    public void mainJob() {
        result = new HashMap<>();
        Date date = new Date();
        initDate(date);
        mainDeal(version, date);
    }

    /**
     * 替换时间和日期
     * @param date
     */
    private void initDate (Date date) {
        String currentDate = DateFormatUtils.format(date, DateUtil.MMddChineseFormat);
        String currentDateTime = DateFormatUtils.format(date, DateUtil.yyyyMMddChineseFormat);
        result.put("Date", currentDate);
        result.put("DateTime", currentDateTime);
    }

    public void mainDeal(String version, Date date) {
        try {

            //该文档总共5个部分
            // part1
            dealPart1(version, date);
            // part2
            // part3
            // part4
            // part5 操业方针(换堆)
            dealPart5(version, date);

            //获取对应的路径
            List<ReportCategoryTemplate> reportCategoryTemplates = reportCategoryTemplateService.selectTemplateInfo(JobEnum.sj_huanduicaoyehuiyijiyao.getCode(), LanguageEnum.cn_zh.getName(), "4烧结");
            if (CollectionUtils.isNotEmpty(reportCategoryTemplates)) {
                String templatePath = reportCategoryTemplates.get(0).getTemplatePath();
                XWPFDocument doc = WordExportUtil.exportWord07(templatePath, result);
                log.debug("换堆操业会议纪要模板路径：" + templatePath);

                comm(version, templatePath);
            }
        } catch (Exception e) {
            log.error("生成换堆操业会议纪要word文档失败", e);
        }
        if (Objects.isNull(date)) {
            date = new Date();
        }
    }

    /**
     * 解析参数行,获取参数列表
     * @param currentRow
     * @return
     */
    private static String[] parseCurrentRowGetParams(XWPFTableRow currentRow) {
        List<XWPFTableCell> cells = currentRow.getTableCells();
        String[] params = new String[cells.size()];
        String text;
        for (int i = 0; i < cells.size(); i++) {
            text = cells.get(i).getText();
            params[i] = text == null ? ""
                    : text.trim().replace(START_STR, EMPTY).replace(END_STR, EMPTY);
        }
        return params;
    }

    /**
     * 检查是否需要递归遍历
     * @param cell
     * @param map
     * @return
     * @throws Exception
     */
    private Object checkThisTableIsNeedIterator(XWPFTableCell cell,
                                                Map<String, Object> map) throws Exception {
        String text = cell.getText().trim();
        // 判断是不是迭代输出
        if (text != null && text.contains(FOREACH) && text.startsWith(START_STR)) {
            text = text.replace(FOREACH_NOT_CREATE, EMPTY).replace(FOREACH_AND_SHIFT, EMPTY).replace(FOREACH_COL, EMPTY)
                    .replace(FOREACH, EMPTY).replace(START_STR, EMPTY);
            String[] keys = text.replaceAll("\\s{1,}", " ").trim().split(" ");
            return PoiPublicUtil.getParamsValue(keys[0], map);
        }
        return null;
    }

    /**
     * 清除Cell中的内容，保留格式
     * @param cell
     */
    private void clearCellText(XWPFTableCell cell) {
        for (XWPFParagraph paragraph:cell.getParagraphs()) {
            for (XWPFRun run:paragraph.getRuns()) {
                run.setText("", 0);
            }
        }
    }

    /**
     *得到所有需要列遍历的Cell
     * @param row
     * @param cell
     * @param name
     * @return
     */
    private Object[] getAllDataColumns (XWPFTableRow row, XWPFTableCell cell, String name) {
        List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();
        int rowspan = 1, colspan = 1;
        //clearCellText(cell);
        columns.add(new HashMap<String, Object>(){{
            put(name.replace(END_STR, EMPTY).replace(WRAP, EMPTY), cell);
        }});

        if (!name.contains(END_STR)) {
            int index = row.getTableCells().indexOf(cell);
            //保存col 的开始列
            int startIndex = row.getTableCells().indexOf(cell);
            while (index < row.getTableCells().size()) {
                index += colspan;
                XWPFTableCell nextCell = row.getCell(index);
                if (nextCell == null) {
                    if (index >= row.getTableCells().size() && name.contains(WRAP) &&
                            row.getTable().getRows().size() > row.getTable().getRows().indexOf(row) + 1) {
                        index = 0 - colspan;
                        row = row.getTable().getRows().get(row.getTable().getRows().indexOf(row) + 1);
                        rowspan++;
                    }
                    continue;
                }
                String cellVal = nextCell.getText();
                if (StringUtils.isBlank(cellVal)) {
                    //读取是判断,跳过,数据为空,但是不是第一次读这一列,所以可以跳过
                    columns.add(new HashMap<String, Object>(){{
                        put(cellVal, nextCell);
                    }});
                    continue;
                }
                //把读取过的cell 置为空
                //clearCellText(nextCell);
                if (cellVal.contains(END_STR)) {
                    columns.add(new HashMap<String, Object>(){{
                        put(cellVal.replace(END_STR, EMPTY), nextCell);
                    }});
                    colspan ++;
                    break;
                } else if (cellVal.contains(WRAP)) {
                    columns.add(new HashMap<String, Object>(){{
                        put(cellVal.replace(WRAP, EMPTY), nextCell);
                    }});
                    //发现换行符,执行换行操作
                    index = 0 - colspan;
                    row = row.getTable().getRows().get(row.getTable().getRows().indexOf(row) + 1);
                    rowspan++;
                } else {
                    columns.add(new HashMap<String, Object>(){{
                        put(cellVal.replace(WRAP, EMPTY), nextCell);
                    }});
                    colspan ++;
                }
            }
        }
        return new Object[]{rowspan, colspan, columns};
    }

    /**
     * 遍历列（多行遍历）
     * @param row
     * @param cell
     * @param map
     * @param name
     * @param index
     * @throws Exception
     */
    private void foreachCol(XWPFTableRow row, XWPFTableCell cell, Map<String, Object> map, String name, int index) throws Exception {
        boolean isCreate = name.contains(FOREACH_COL_VALUE);
        name = name.replace(FOREACH_COL_VALUE, EMPTY).replace(FOREACH_COL, EMPTY).replace(START_STR,
                EMPTY);
        String[]      keys  = name.replaceAll("\\s{1,}", " ").trim().split(" ");
        Collection<?> datas = (Collection<?>) PoiPublicUtil.getParamsValue(keys[0], map);
        Object[] columnsInfo = getAllDataColumns(row, cell, name.replace(keys[0], EMPTY));
        if (datas == null) {
            return;
        }
        Iterator<?> its     = datas.iterator();
        int         rowspan = (Integer) columnsInfo[0], colspan = (Integer) columnsInfo[1];
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> columns = (List<Map<String, Object>>) columnsInfo[2];
        while (its.hasNext()) {
            Object t = its.next();
            Map<String, Object> tempMap = Maps.newHashMap();
            for (Map<String, Object> column:columns) {
                for (String key:column.keySet()) {
                    cell = (XWPFTableCell)column.get(key);
                    row = cell.getTableRow();
                    tempMap.put("t", t);
                    String[] params = key.split("\\.");
                    Object val = PoiPublicUtil.getParamsValue(params[params.length-1], t);
                    clearCellText(cell);
                    PoiWordStyleUtil.copyCellAndSetValue(row.getTableCells().get(index),
                            cell, val.toString());
                    if (row.getCell(row.getTableCells().indexOf(cell) + 1) == null && its.hasNext()) {
                        cell = row.createCell();
                        column.put(key, cell);
                    }
                }
            }
        }
        if (isCreate) {
            cell = row.getCell(index - 1);
            cell.setText(cell.getText() + END_STR);
        }
    }

    /**
     * 对#fe:的处理（多行遍历的处理，这里采用parseDataByRow方式
     * 目前easypoi对word的#fe:处理不了，针对第一个表格这里写了两种处理方式
     * 1. foreachCol: 使用单个#fe:，用]]作为换行符，多行纵向遍历，数据源只需要单个List<Map<String, String>>
     * 2. parseDataByRow： 每行使用一个#fe:，逐行使用纵向遍历，每行需要一个数据源
     * @param table
     * @param map
     * @throws Exception
     */
    private void parseThisTable(XWPFTable table, Map<String, Object> map) throws Exception {
        XWPFTableRow row;
        List<XWPFTableCell> cells;
        Object listobj;
        for (int i = 0; i < table.getRows().size(); i++) {
            row = table.getRow(i);
            cells = row.getTableCells();

            for (int j=0; j<cells.size();j++) {
                XWPFTableCell cell = cells.get(j);
                String text = cell.getText();
                if (text.contains(FOREACH_COL) || text.contains(FOREACH_COL_VALUE)) {
                    foreachCol(row, cell, map, text, cells.indexOf(cell));
                }
            }

//            listobj = checkThisTableIsNeedIterator(cells.get(0), map);
//            if (listobj == null) {
//                return;
//            } else {
//                parseDataByRow(table, i, (List) listobj);
//            }
        }
    }

    /**
     * 解析下一行,并且生成更多的行
     * @param table
     * @param index
     * @param list
     */
    private void parseDataByRow(XWPFTable table, int index,
                                List<Object> list) throws Exception {
        XWPFTableRow currentRow = table.getRow(index);
        String[] params = parseCurrentRowGetParams(currentRow);
        String listname = params[0];
        boolean isCreate = !listname.contains(FOREACH_NOT_CREATE) && !listname.contains(FOREACH_COL);
        listname = listname.replace(FOREACH_NOT_CREATE, EMPTY).replace(FOREACH_AND_SHIFT, EMPTY).replace(FOREACH_COL, EMPTY)
                .replace(FOREACH, EMPTY).replace(START_STR, EMPTY);
        String[] keys = listname.replaceAll("\\s{1,}", " ").trim().split(" ");
        params[0] = keys[1];
        //保存这一行的样式是-后面好统一设置
        List<XWPFTableCell> tempCellList = new ArrayList<XWPFTableCell>();
        tempCellList.addAll(table.getRow(index).getTableCells());
        int templateInde = index;
        for (XWPFParagraph paragraph:currentRow.getCell(0).getParagraphs()) {
            for (XWPFRun run:paragraph.getRuns()) {
                run.setText("", 0);
            }
        }
        int cellIndex = 0;// 创建完成对象一行好像多了一个cell
        for (; cellIndex < params.length; cellIndex++) {
            Map<String, Object> tempMap = Maps.newHashMap();
            int colIndex = 0;
            for (Object obj : list) {
                currentRow = isCreate ? table.insertNewTableRow(index++) : table.getRow(index);
                tempMap.put("t", obj);
                String val = eval(params[cellIndex], tempMap).toString();
                PoiWordStyleUtil.copyCellAndSetValue(tempCellList.get(cellIndex),
                        currentRow.getTableCells().get(colIndex++), val);
                if (list.indexOf(obj) != list.size() - 1) {
                    XWPFTableCell newCell = currentRow.createCell();
                }
            }
        }
    }

    /**
     * 处理得到第一个表的数据
     * @param version
     * @param date
     */
    private void dealPart1(String version, Date date) {
        handPart1Data(version, date);
    }

    /**
     * 获取第一个表的数据
     * @param version
     * @param date
     */
    private void handPart1Data(String version, Date date) {
        //第一个表
        String url = getUrl(version) + "/report/burdenChangeInfo";
        String results = httpGetData(version, date, url);
        if (StringUtils.isBlank(results)) {
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(results);
        if (Objects.nonNull(jsonObject)) {
            String data = jsonObject.getString("data");
            List<BurdenBleRatio> bleRatioList = JSON.parseObject(data, new TypeReference<List<BurdenBleRatio>>(){});
            if (Objects.isNull(bleRatioList)) {
                return;
            }
            // 跟据OrderNum降序
            bleRatioList.sort((o1, o2) -> o2.getOrderNum().compareTo(o1.getOrderNum()));

            if (bleRatioList.size() < 2) {
                return;
            }
            List<Map<String, String>> headreListMap = new ArrayList<Map<String, String>>();
//            List<Map<String, String>> value1ListMap = new ArrayList<Map<String, String>>();
//            List<Map<String, String>> value2ListMap = new ArrayList<Map<String, String>>();
//            List<Map<String, String>> value3ListMap = new ArrayList<Map<String, String>>();
            // 第一列
            headreListMap.add(new HashMap<String, String>(){{
                put("header", "堆号");
                put("value1", bleRatioList.get(0).getPile_no());
                put("value2", bleRatioList.get(1).getPile_no());
                put("value3", "比较");
            }});
//            value1ListMap.add(new HashMap<String, String>(){{
//                put("value1", bleRatioList.get(0).getPile_no());
//            }});
//            value2ListMap.add(new HashMap<String, String>(){{
//                put("value2", bleRatioList.get(1).getPile_no());
//            }});
//            value3ListMap.add(new HashMap<String, String>(){{
//                put("value3", "比较");
//            }});
            List<BurdenMatRatio> burdenMatRatioList1 = bleRatioList.get(0).getRatios();
            List<BurdenMatRatio> burdenMatRatioList2 = bleRatioList.get(1).getRatios();
            for (BurdenMatRatio b1 : burdenMatRatioList1) {
                Optional<BurdenMatRatio> optional = burdenMatRatioList2.stream().filter(item -> item.getBrandcode().equals(b1.getBrandcode())).findFirst();
                if (optional.isPresent()) {
                    BurdenMatRatio b2 = optional.get();
                    headreListMap.add(new HashMap<String, String>(){{
                        put("header", b1.getBrandname());
                        put("value1", b1.getRatio().toString());
                        put("value2", b2.getRatio().toString());
                        put("value3", b2.getRatio().subtract(b1.getRatio()).toString());
                    }});
//                    value1ListMap.add(new HashMap<String, String>(){{
//                        put("value1", b1.getRatio().toString());
//                    }});
//                    value2ListMap.add(new HashMap<String, String>(){{
//                        put("value2", b2.getRatio().toString());
//                    }});
//                    value3ListMap.add(new HashMap<String, String>(){{
//                        put("value3", b2.getRatio().subtract(b1.getRatio()).toString());
//                    }});
                }
            }
            headreListMap.add(new HashMap<String, String>(){{
                put("header", "合计(%)");
                put("value1", bleRatioList.get(0).getSum().toString());
                put("value2", bleRatioList.get(1).getSum().toString());
                put("value3", "");
            }});
//            value1ListMap.add(new HashMap<String, String>(){{
//                put("value1", bleRatioList.get(0).getSum().toString());
//            }});
//            value2ListMap.add(new HashMap<String, String>(){{
//                put("value2", bleRatioList.get(1).getSum().toString());
//            }});
//            value3ListMap.add(new HashMap<String, String>(){{
//                put("value3", "");
//            }});
            result.put("headers", headreListMap);
//            result.put("list1", value1ListMap);
//            result.put("list2", value2ListMap);
//            result.put("list3", value3ListMap);
        }

        //TODO 第二个表
    }

    /**
     * 获取堆号
     *
     * @param version
     * @param date
     */
    private String httpGetData(String version, Date date, String url) {
        DateQuery dateQuery = DateQueryUtil.buildToday(date);
        Map<String, String> queryParam = new HashMap();
        queryParam.put("clock", Objects.requireNonNull(dateQuery.getQueryEndTime()).toString());
        String results = httpUtil.get(url, queryParam);

        return results;
    }

    /**
     * 获取第一列对应的数据
     * @param version
     * @return
     */
    private List<ProcessCardDto> getVaues(String version) {
        String url = getUrl(version) + "/processCard/selectLatest";
        String jsonData = httpUtil.get(url);
        JsonListResult<ProcessCardDto> listResult = null;
        if (StringUtils.isNotBlank(jsonData)) {
            listResult = JSON.parseObject(jsonData, new TypeReference<JsonListResult<ProcessCardDto>>(){});
        }
        if (Objects.isNull(listResult)) {
            return null;
        }
        List<ProcessCardDto> rows = listResult.getRows();
        if (Objects.isNull(rows)) {
            return rows;
        }
        return rows;
    }

    /**
     * 处理列数据
     * @param version
     */
    private void dealColumns(String version) {
        String url = getUrl(version) + "/processCard/paramList";
        String paramName = "paramName";
        Map<String, String> queryParam = new HashMap();
        queryParam.put("type", "1");
        String jsonData = httpUtil.get(url, queryParam);
        JsonListResult<ProcessParameter> listResult = null;
        if (StringUtils.isNotBlank(jsonData)) {
            listResult = JSON.parseObject(jsonData, new TypeReference<JsonListResult<ProcessParameter>>(){});
        }
        if (Objects.isNull(listResult)) {
            return;
        }
        List<ProcessParameter> rows = listResult.getRows();
        if (Objects.isNull(rows)) {
            return;
        }
        // 排序，保证可以合并的单元格时连续的，方便合并
        rows.sort(comparing(ProcessParameter::getName));
        //获取第二列
        List<ProcessCardDto> processCardDtoList = getVaues(version);
        ProcessCardDto processCardDto = null;
        if (processCardDtoList != null && !CollectionUtils.isEmpty(processCardDtoList)) {
            processCardDto = processCardDtoList.get(0);
        }
        if (Objects.isNull(processCardDto)) {
            return;
        }
        List<ProcessGoal> goals = processCardDto.getGoals();

        List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
        for (int i =0; i < rows.size(); i++) {
            ProcessParameter row = rows.get(i);
            String preFx = "";
            Map<String, String> lm = new HashMap<String, String>();
            String name = row.getName();
            if (name.length() > 1) {
                String namePrefx = name.substring(0, name.length() - 1);
                List<ProcessParameter> pList = rows.stream().filter(item -> item.getName().substring(0,
                        item.getName().length() - 1).equals(namePrefx)).collect(Collectors.toList());
                if (pList.size() > 1) {
                    preFx = name.substring(name.length() - 1, name.length());
                    name = namePrefx;
                }
            }

            if(!row.getUnit().equals("-"))
                lm.put("name", name + " " + row.getUnit());
            else
                lm.put("name", name);
            Optional<ProcessGoal> optional = goals.stream().filter(item -> item.getParamId()==row.getId()).findFirst();
            if (optional.isPresent()) {
                ProcessGoal goal = optional.get();
                String avg = "";
                String gap = "";
                if (!Objects.isNull(goal)) {
                    BigDecimal low = goal.getLow();
                    BigDecimal up = goal.getUp();

                    if (low != null && up != null) {
                        avg = (low.add(up)).divide(new BigDecimal(2),2,BigDecimal.ROUND_HALF_UP).toString();
                        gap = up.subtract(new BigDecimal(avg)).toString();
                        avg = avg + "±" + gap;
                    } else if (low == null || up == null) {
                        avg = (low == null) ? ((up == null) ? "" : up.toString()) : low.toString();
                    }
                }
                lm.put("target", preFx+avg);
                lm.put("controlAction", goal.getControlAction());
                if (i == 0) {
                    ProcessCard card = processCardDto.getProcessCard();
                    String note = "";
                    if (!Objects.isNull(card)) {
                        note = card.getNote();
                    }
                    lm.put("note", note);
                }
            }

            listMap.add(lm);
        }
        result.put("part5MapList", listMap);
    }

    /**
     * 处理第五部分-操业方针(换堆)
     *
     * @param version
     * @param date
     */
    private void dealPart5(String version, Date date) {
        // 堆号
        String url = getUrl(version) + "/report/pileNo";
        String results = httpGetData(version, date, url);
        if (StringUtils.isBlank(results)) {
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(results);
        if (Objects.nonNull(jsonObject)) {
            JSONObject data = jsonObject.getJSONObject("data");
            result.put("pileNo", data.getString("BM_PILE_NO"));
        }
        // 第一列
        dealColumns(version);
    }

    // word跨行并单元格
    private void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            if (rowIndex == fromRow) {
                // The first merged cell is set with RESTART merge value
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
            } else {
                // Cells which join (merge) the first one, are set with CONTINUE
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    /**
     * 添加换行符，word中表格中直接替换或者替换字符串，如果包含换行符则会有问题
     * @param cell
     */
    private  void addBreakInCell(XWPFTableCell cell) {
        if(cell.getText() != null && cell.getText().contains("\n")) {
            for (XWPFParagraph p : cell.getParagraphs()) {
                for (XWPFRun run : p.getRuns()) {//XWPFRun对象定义具有一组公共属性的文本区域
                    if(run.getText(0)!= null && run.getText(0).contains("\n")) {
                        String[] lines = run.getText(0).split("\n");
                        if(lines.length > 0) {
                            run.setText(lines[0], 0); // set first line into XWPFRun
                            for(int i=1;i<lines.length;i++){
                                // add break and insert new text
                                run.addBreak();//中断
                                //run.addCarriageReturn();//回车符，但是不起作用
                                run.setText(lines[i]);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 生产word文档
     *
     * @param version
     * @param path
     */
    private void comm(String version, String path) {
        try {
            String sequence = "4烧结";
            XWPFDocument doc = WordExportUtil.exportWord07(path, result);
            List<XWPFTable> tables = doc.getTables();
            parseThisTable(tables.get(0), result);

            mergeCellsVertically(tables.get(tables.size()-1), 3, 1, tables.get(tables.size()-1).getRows().size()-1);
            XWPFTableCell targetCell = tables.get(tables.size()-1).getRow(1).getCell(3);
            // 处理换行符
            addBreakInCell(targetCell);

            // 截取name最后一个字，遍历整个list
            Map<Integer, List<XWPFTableRow>> mergeRows = new HashMap<Integer, List<XWPFTableRow>>();
            List<XWPFTableRow> rows = tables.get(tables.size()-1).getRows();
            for (int rowIndex = 1; rowIndex < rows.size(); rowIndex++) {
                XWPFTableRow row = rows.get(rowIndex);
                String namePrefx = row.getCell(0).getText().split(" ")[0];
                List<XWPFTableRow> mergeRow = new ArrayList<>();
                for (int i = rowIndex; i <  rows.size(); i ++) {
                    String str = rows.get(i).getCell(0).getText().split(" ")[0];
                    if (str.equals(namePrefx)) {
                        mergeRow.add(rows.get(i));
                    }
                }
                if (mergeRow.size() > 1) {
                    mergeRows.put(rowIndex, mergeRow);
                }
            }
            // 合并单元格并赋值
            for (int key : mergeRows.keySet()) {
                List<XWPFTableRow> tableRows = mergeRows.get(key);
                mergeCellsVertically(tables.get(tables.size()-1), 0, key, key + tableRows.size() - 1);
            }
            String fileName = sequence + "_换堆操业会议纪要_" + DateUtil.getFormatDateTime(new Date(), "yyyyMMdd") + ".docx";
            String filePath = jobProperties.getFilePath() + File.separator + "doc" + File.separator + fileName;
            FileOutputStream fos = new FileOutputStream(filePath);
            doc.write(fos);
            fos.close();

            ReportIndex reportIndex = new ReportIndex();
            reportIndex.setSequence(sequence);
            reportIndex.setIndexLang("cn_zh");
            reportIndex.setIndexType("report_day");
            reportIndex.setRecordDate(new Date());
            reportIndex.setName(fileName);
            reportIndex.setReportCategoryCode(JobEnum.sj_huanduicaoyehuiyijiyao.getCode());
            reportIndex.setPath(filePath);

            reportIndexService.insertReportRecord(reportIndex);
            log.info("换堆操业会议纪要word文档生成完毕" + filePath);
        } catch (Exception e) {
            log.error("换堆操业会议纪要word文档失败", e);
        }
    }

    /**
     * 4号烧结，默认返回4号高炉
     *
     * @param version
     * @return
     */
    private String getUrl(String version) {
        return httpProperties.getUrlApiSJThree();
    }
}
