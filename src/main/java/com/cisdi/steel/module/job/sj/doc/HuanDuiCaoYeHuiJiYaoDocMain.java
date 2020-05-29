package com.cisdi.steel.module.job.sj.doc;

import cn.afterturn.easypoi.util.PoiPublicUtil;
import cn.afterturn.easypoi.util.PoiWordStyleUtil;
import cn.afterturn.easypoi.word.WordExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.tools.ant.util.DateUtils;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
@SuppressWarnings("ALL")
/**
 *
 */
public class HuanDuiCaoYeHuiJiYaoDocMain {

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

    private String version = "4.0";

    /**
     * doc最后结果
     */
    private HashMap<String, Object> result = null;

    @Scheduled(cron = "0 0 0/1 * * ?")
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
            //dealPart1(version, date);
            // part2
            // part3
            // part4
            // part5 操业方针(换堆)
            dealPart5(version, date);

            //获取对应的路径
            List<ReportCategoryTemplate> reportCategoryTemplates = reportCategoryTemplateService.selectTemplateInfo(JobEnum.sj_huanduicaoyehuijiyao.getCode(), LanguageEnum.cn_zh.getName(), "4烧结");
            if (CollectionUtils.isNotEmpty(reportCategoryTemplates)) {
                String templatePath = reportCategoryTemplates.get(0).getTemplatePath();
                XWPFDocument doc = WordExportUtil.exportWord07(templatePath, result);
                log.debug("换堆操业会纪要模板路径：" + templatePath);

                comm(version, templatePath);
            }
        } catch (Exception e) {
            log.error("生成换堆操业会纪要word文档失败", e);
        }
        if (Objects.isNull(date)) {
            date = new Date();
        }
    }

    private void dealPart1(String version, Date date) {
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
            Collections.sort(bleRatioList, new Comparator<BurdenBleRatio>() {
                @Override
                public int compare(BurdenBleRatio o1, BurdenBleRatio o2) {
                    //跟据OrderNum降序
                    return o2.getOrderNum().compareTo(o1.getOrderNum());
                }
            });

            if (bleRatioList.size() < 2) {
                return;
            }
            List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
            // 第一列
            Map<String, String> lm = new HashMap<String, String>();
            lm.put("header", "堆号");
            lm.put("value1", bleRatioList.get(0).getPile_no());
            lm.put("value2", bleRatioList.get(1).getPile_no());
            lm.put("value3", "比较");
            listMap.add(lm);
            List<BurdenMatRatio> burdenMatRatioList1 = bleRatioList.get(0).getRatios();
            List<BurdenMatRatio> burdenMatRatioList2 = bleRatioList.get(1).getRatios();
            for (BurdenMatRatio b1 : burdenMatRatioList1) {
                Map<String, String> map = new HashMap<String, String>();
                Optional<BurdenMatRatio> optional = burdenMatRatioList2.stream().filter(item -> item.getBrandcode().equals(b1.getBrandcode())).findFirst();
                if (optional.isPresent()) {
                    BurdenMatRatio b2 = optional.get();
                    map.put("header", b1.getBrandname());
                    map.put("value1", b1.getRatio().toString());
                    map.put("value2", b2.getRatio().toString());
                    map.put("value3", b2.getRatio().subtract(b1.getRatio()).toString());
                    listMap.add(map);
                }
            }
            Map<String, String> lm2 = new HashMap<String, String>();
            lm2.put("header", "合计(%)");
            lm2.put("value1", bleRatioList.get(0).getSum().toString());
            lm2.put("value2", bleRatioList.get(1).getSum().toString());
            lm2.put("value3", "");
            listMap.add(lm2);
            result.put("part1MapList", listMap);
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
        Collections.sort(rows, new Comparator<ProcessParameter>(){
            @Override
            public int compare(ProcessParameter p1, ProcessParameter p2) {
                if (StringUtils.isBlank(p1.getName()) && StringUtils.isBlank(p2.getName())) {
                    return 0;
                }
                if (p1.getName().compareTo(p2.getName()) > 0){
                    return 1;
                }else if (p1.getName().compareTo(p2.getName()) > 0){
                    return 0;
                }else{
                    return -1;
                }
            }
        });
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
            String fileName = sequence + "_换堆操业会纪要_" + DateUtil.getFormatDateTime(new Date(), "yyyyMMdd") + ".docx";
            String filePath = jobProperties.getFilePath() + File.separator + "doc" + File.separator + fileName;
            FileOutputStream fos = new FileOutputStream(filePath);
            doc.write(fos);
            fos.close();

            ReportIndex reportIndex = new ReportIndex();
            reportIndex.setCreateTime(new Date());
            reportIndex.setUpdateTime(new Date());
            reportIndex.setSequence(sequence);
            reportIndex.setIndexLang("cn_zh");
            reportIndex.setIndexType("report_day");
            reportIndex.setRecordDate(new Date());
            reportIndex.setName(fileName);
            reportIndex.setReportCategoryCode(JobEnum.sj_huanduicaoyehuijiyao.getCode());
            reportIndex.setPath(filePath);
            reportIndexMapper.insert(reportIndex);

            log.info("换堆操业会纪要word文档生成完毕" + filePath);
        } catch (Exception e) {
            log.error("换堆操业会纪要word文档失败", e);
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
