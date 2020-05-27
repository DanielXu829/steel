package com.cisdi.steel.module.job.jh.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.WordImageEntity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.a1.doc.ChartFactory;
import com.cisdi.steel.module.job.a1.doc.Serie;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.enums.LanguageEnum;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.tools.ant.util.DateUtils;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Component
@Slf4j
public class JiaoHuaShengChanZhenDuanBaoGao {
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

    private HashMap<String, Object> result = null;
    private Date startTime = null;
    private Date endTime = null;
    private Date beforeYesterday = null;
    List<String> categoriesList = new ArrayList<>();
    List<String> dateList = new ArrayList<>();
    List<String> longTimeList = new ArrayList<>();
    DecimalFormat df1 = new DecimalFormat("0.0");
    DecimalFormat df2 = new DecimalFormat("0.00");
    DecimalFormat df3 = new DecimalFormat("0.000");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String sequence = "焦化910";

    private String comma = ",";
    private String tag1 = "CK9_10_L2C_CI_211CF151FT_TOTALIZERA_1_PV_1m_avg";
    private String L1 = "CK9_10_MESR_ANA_cokeProduct_1d_avg," +
            "CK9_10_MESR_CI_LJC001_1d_avg," +
            "CK9_10_CDQ_rate_1d_avg," +
            "CK9_10_L2C_CDQ_auxfi309_total_1d_avg," +
            "CK9_10_MESR_ANA_cokeProduct_1month_avg," +
            "CK9_10_MESR_CI_LJC001_1month_avg";
    private String L2 = "CK9_10_MESR_SH_Mt_1d_avg," +
            "CK9_10_MESR_SH_Ad_1d_avg," +
            "CK9_10_MESR_SH_Std_1d_avg," +
            "CK9_10_MESR_SH_G_1d_avg," +
            "CK9_10_MESR_SH_G_Y_1d_avg";
    private String L3 = "CK9_10_MESR_SH_K3_1d_avg," +
            "CK9_10_MESR_SH_9KAVG_1d_avg," +
            "CK9_10_MESR_SH_9KAN_1d_avg," +
            "CK9_10_MESR_SH_10KAVG_1d_avg," +
            "CK9_10_MESR_SH_10KAN_1d_avg," +
            "CK9_10_MESR_SH_9MACH_1d_avg," +
            "CK9_10_MESR_SH_10MACH_1d_avg," +
            "CK9_10_MESR_SH_9FOCAL_1d_avg," +
            "CK9_10_MESR_SH_10FOCAL_1d_avg";
    private String L4 = "CK9_10_CDQ_rate_1d_avg," +
            "CK9_10_L2C_CDQ_C_3TE_25101a_1d_avg," +
            "CK9_10_L2C_CDQ_C_23TE_225101a_1d_avg," +
            "CK9_10_L2C_CDQ_auxfi309_total_1d_avg," +
            "CK9_10_L2C_CDQ_auxfi309_2total_1d_avg," +
            "CK9_10_L2C_CDQ_mr_tag0108_1d_avg," +
            "CK9_10_L2C_CDQ_mr_2tag0108_1d_avg," +
            "CK9_10_L2C_CDQ_EI01AI118_00_1d_avg," +
            "CK9_10_L2C_CDQ_EI01AI118_02_1d_avg," +
            "CK9_10_L2C_CDQ_EI01AI118_03_1d_avg," +
            "CK9_10_L2C_CDQ_EI01AI118_01_1d_avg," +
            "CK9_10_L2C_CDQ_EI01AI118_200_1d_avg," +
            "CK9_10_L2C_CDQ_EI01AI118_201_1d_avg";

    private void initialData() {
        result = new HashMap<>();
        startTime = null;
        endTime = null;
        categoriesList = new ArrayList<>();
        dateList = new ArrayList<>();
        longTimeList = new ArrayList<>();
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void mainTask() {
        initialData();
        initDateTime();
        mainDeal();
        log.info("焦化生产诊断报告word生成完毕！");
    }

    public void mainDeal() {
        // 处理当前时间
        result.put("current_date", DateUtil.getFormatDateTime(endTime, DateUtil.yyyyMMddChineseFormat));
        String allTagNames = L1 + comma + L2 + comma + L3 + comma + L4;
        JSONObject data = getDataByTag(allTagNames, startTime, endTime);
        dealPart1(data);
        dealPart2(data);
        dealPart3(data);
        dealPart4(data);
        List<ReportCategoryTemplate> reportCategoryTemplates = reportCategoryTemplateService.selectTemplateInfo(JobEnum.jh_shengchanzhenduanbaogao.getCode(), LanguageEnum.cn_zh.getName(), sequence);
        if (CollectionUtils.isNotEmpty(reportCategoryTemplates)) {
            String templatePath = reportCategoryTemplates.get(0).getTemplatePath();
            log.info("焦化生产诊断报告模板路径：" + templatePath);
            comm(templatePath);
        }
    }

    private void dealPart1(JSONObject data) {
        dealPart(data, "partOne", L1, df2);
        dealMeiQiLiang();
        dealIncreaseYesterday(data, "CK9_10_MESR_ANA_cokeProduct_1d_avg", "textOne1", "countOne1");
        dealIncreaseYesterday(data, "CK9_10_MESR_CI_LJC001_1d_avg", "textOne2", "countOne2");
        dealIncreaseYesterday(data, "CK9_10_CDQ_rate_1d_avg", "textOne3", "countOne3");
        dealIncreaseYesterday(data, "CK9_10_L2C_CDQ_auxfi309_total_1d_avg", "textOne4", "countOne4");
        selectProdItemValue();
        dealChart(data, "CK9_10_MESR_ANA_cokeProduct_1d_avg", "CK9_10_MESR_CI_LJC001_1d_avg", 5750d, 5250d, 6800d, 5800d, "产量", "煤粉", "chartOne1");
        dealChart(data, "CK9_10_CDQ_rate_1d_avg", "CK9_10_L2C_CDQ_auxfi309_total_1d_avg", 5750d, 5250d, 6800d, 5800d, "干熄率", "蒸汽量", "chartOne2");
        result.put("countOne5", df2.format(dealMonthTotal(data, "CK9_10_L2C_CDQ_auxfi309_total_1d_avg", false)));
    }

    private void dealPart2(JSONObject data) {
        dealPart(data, "partTwo", L2, df2);
        result.put("offsetMT", df2.format(dealOffset(7d, 12d, "CK9_10_MESR_SH_Mt_1d_avg", data)));
        result.put("offsetAd", df2.format(dealOffset(0d, 13d, "CK9_10_MESR_SH_Ad_1d_avg", data)));
        result.put("offsetStd", df2.format(dealOffset(0d, 0.85d, "CK9_10_MESR_SH_Std_1d_avg", data)));
        result.put("offsetG", df2.format(dealOffset(70d, Double.MAX_VALUE, "CK9_10_MESR_SH_G_1d_avg", data)));
        result.put("offsetY", df2.format(dealOffset(0d, 21d, "CK9_10_MESR_SH_G_Y_1d_avg", data)));
        getCurrByDateTime(DateUtil.getFormatDateTime(endTime, "yyyy/MM/dd"));
        dealChart(data, "CK9_10_MESR_SH_Ad_1d_avg", "CK9_10_MESR_SH_Std_1d_avg", 5750d, 5250d, 6800d, 5800d, "Ad", "Std", "chartTwo2");
        dealChart(data, "CK9_10_MESR_SH_G_1d_avg", "CK9_10_MESR_SH_G_Y_1d_avg", 5750d, 5250d, 6800d, 5800d, "G", "Y", "chartTwo3");
    }

    private void dealPart3(JSONObject data) {
        dealPart(data, "partThree", L3, df2);
        Double offsetCoke1 = dealOffset(0.75d, Double.MAX_VALUE, "CK9_10_MESR_SH_K3_1d_avg", data);
        Double offsetCoke2 = dealOffset(0.7d, Double.MAX_VALUE, "CK9_10_MESR_SH_9KAVG_1d_avg", data);
        Double offsetCoke3 = dealOffset(0.7d, Double.MAX_VALUE, "CK9_10_MESR_SH_9KAN_1d_avg", data);
        Double offsetCoke4 = dealOffset(0.7d, Double.MAX_VALUE, "CK9_10_MESR_SH_10KAVG_1d_avg", data);
        Double offsetCoke5 = dealOffset(0.7d, Double.MAX_VALUE, "CK9_10_MESR_SH_10KAN_1d_avg", data);
        Double offsetCoke6 = dealOffset(1258d, 1270d, "CK9_10_MESR_SH_9MACH_1d_avg", data);
        Double offsetCoke7 = dealOffset(1258d, 1270d, "CK9_10_MESR_SH_10MACH_1d_avg", data);
        Double offsetCoke8 = dealOffset(1308d, 1322d, "CK9_10_MESR_SH_9FOCAL_1d_avg", data);
        Double offsetCoke9 = dealOffset(1308d, 1322d, "CK9_10_MESR_SH_10FOCAL_1d_avg", data);
        result.put("offsetCoke1", df2.format(offsetCoke1));
        result.put("offsetCoke2", df2.format(offsetCoke2));
        result.put("offsetCoke3", df2.format(offsetCoke3));
        result.put("offsetCoke4", df2.format(offsetCoke4));
        result.put("offsetCoke5", df2.format(offsetCoke5));
        result.put("offsetCoke6", df2.format(offsetCoke6));
        result.put("offsetCoke7", df2.format(offsetCoke7));
        result.put("offsetCoke8", df2.format(offsetCoke8));
        result.put("offsetCoke9", df2.format(offsetCoke9));
        StringBuilder sb = new StringBuilder();
        if (offsetCoke1 > 0d) {
            sb.append("K3结果偏高").append(df2.format(offsetCoke1)).append(", ");
        } else if (offsetCoke1 < 0d) {
            sb.append("K3结果偏低").append(df2.format(Math.abs(offsetCoke1))).append(", ");
        }
        if (offsetCoke2 > 0d) {
            sb.append("9#K均结果偏高").append(df2.format(offsetCoke2)).append(", ");
        } else if (offsetCoke2 < 0d) {
            sb.append("9#K均结果偏低").append(df2.format(Math.abs(offsetCoke2))).append(", ");
        }
        if (offsetCoke3 > 0d) {
            sb.append("9#K安结果偏高").append(df2.format(offsetCoke3)).append(", ");
        } else if (offsetCoke3 < 0d) {
            sb.append("9#K安结果偏低").append(df2.format(Math.abs(offsetCoke3))).append(", ");
        }
        if (offsetCoke4 > 0d) {
            sb.append("10#K均结果偏高").append(df2.format(offsetCoke4)).append(", ");
        } else if (offsetCoke4 < 0d) {
            sb.append("10#K均结果偏低").append(df2.format(Math.abs(offsetCoke4))).append(", ");
        }
        if (offsetCoke5 > 0d) {
            sb.append("10#K安结果偏高").append(df2.format(offsetCoke5)).append(", ");
        } else if (offsetCoke5 < 0d) {
            sb.append("10#K安结果偏低").append(df2.format(Math.abs(offsetCoke5))).append(", ");
        }
        if (offsetCoke6 > 0d) {
            sb.append("9#机侧直行温度结果偏高").append(df2.format(offsetCoke6)).append(", ");
        } else if (offsetCoke6 < 0d) {
            sb.append("9#机侧直行温度结果偏低").append(df2.format(Math.abs(offsetCoke6))).append(", ");
        }
        if (offsetCoke7 > 0d) {
            sb.append("10#机侧直行温度结果偏高").append(df2.format(offsetCoke7)).append(", ");
        } else if (offsetCoke7 < 0d) {
            sb.append("10#机侧直行温度结果偏低").append(df2.format(Math.abs(offsetCoke7))).append(", ");
        }
        if (offsetCoke8 > 0d) {
            sb.append("9#焦侧直行温度结果偏高").append(df2.format(offsetCoke8)).append(", ");
        } else if (offsetCoke8 < 0d) {
            sb.append("9#焦侧直行温度结果偏低").append(df2.format(Math.abs(offsetCoke8))).append(", ");
        }
        if (offsetCoke9 > 0d) {
            sb.append("10#焦侧直行温度结果偏高").append(df2.format(offsetCoke9)).append(", ");
        } else if (offsetCoke9 < 0d) {
            sb.append("10#焦侧直行温度结果偏低").append(df2.format(Math.abs(offsetCoke9))).append(", ");
        }
        result.put("partOverview3", sb.toString());
    }

    private void dealPart4 (JSONObject data) {
        dealPart(data, "partThree", L4, df2);
    }

    /**
     * 通过tag点获取得数据，组装到result
     *
     * @param data
     * @param prefix
     */
    private void dealPart(JSONObject data, String prefix, String tagNames, DecimalFormat df) {
        if (data != null && data.size() > 0) {
            String[] tags = tagNames.split(comma);
            for (int i = 0; i < tags.length; i++) {
                Double doubleValue = getLastByTag(data, tags[i]);
                result.put(prefix + String.valueOf(i + 1), doubleValue == null ? 0d : df.format(doubleValue));
            }
        }
    }

    private void dealIncreaseYesterday(JSONObject data, String tagName, String textAddr, String countAddr) {
        Double increase = 0d;
        List<Double> tagObject = getLast2ByTag(data, tagName);
        Double yesterday = tagObject.get(tagObject.size() - 1);
        Double twoDaysAgo = tagObject.get(tagObject.size() - 2);
        if (yesterday != null) {
            if (twoDaysAgo != null) {
                increase = yesterday - twoDaysAgo;
            }
        }
        if (increase >= 0d) {
            result.put(textAddr, "升高");
        } else {
            result.put(textAddr, "降低");
        }
        result.put(countAddr, df2.format(Math.abs(increase.doubleValue())));
    }

    private Double dealMonthTotal(JSONObject data, String tagName, Boolean isAvg) {
        Double result = 0d;
        Double total = 0d;
        Double count = 0d;
        List<Double> tagObject = getValuesByTag(data, tagName);
        for (Double item : tagObject) {
            if (item != null) {
                total += item;
                count++;
            }
        }
        if (isAvg && count > 0d) {
            result = total / count;
        } else {
            result = total;
        }
        return result;
    }

    private Double getLastByTag(JSONObject data, String tagName) {
        JSONArray tagArray = data.getJSONArray(tagName);
        return getValueByClock(tagArray, endTime);
    }

    private List<Double> getLast2ByTag(JSONObject data, String tagName) {
        JSONArray tagArray = data.getJSONArray(tagName);
        List<Double> vals = new ArrayList<>();
        vals.add(getValueByClock(tagArray, beforeYesterday));
        vals.add(getValueByClock(tagArray, endTime));
        return vals;
    }

    private List<Double> getValuesByTag(JSONObject data, String tagName) {
        JSONArray tagArray = data.getJSONArray(tagName);
        List<Double> vals = new ArrayList<>();
        for (String time : longTimeList) {
            Double val = getValueByClock(tagArray, time);
            vals.add(val);
        }
        return vals;
    }

    private Double getValueByClock(JSONArray tagArray, Date clock) {
        Double result = null;
        try {
            for (int i = 0; i < tagArray.size(); i++) {
                JSONObject o = tagArray.getJSONObject(i);
                String c = o.getString("clock");
                Date d2 = sdf.parse(c);
                if (DateUtil.isSameDay(clock, d2)) {
                    result = new Double(o.getString("val"));
                }
            }
        } catch (ParseException pex) {
            log.error(pex.getMessage());
        }
        return result;
    }

    private Double getValueByClock(JSONArray tagArray, String clock) {
        Double result = null;
        try {
            for (int i = 0; i < tagArray.size(); i++) {
                JSONObject o = tagArray.getJSONObject(i);
                String c = o.getString("clock");
                Long lt = new Long(clock);
                Date d1 = new Date(lt);
                Date d2 = sdf.parse(c);
                if (DateUtil.isSameDay(d1, d2)) {
                    result = new Double(o.getString("val"));
                }
            }
        } catch (ParseException pex) {
            log.error(pex.getMessage());
        }
        return result;
    }

    /**
     * 通过tag点获取数据
     *
     * @param tagNames
     * @param startTime
     * @param endTime
     * @return
     */
    private JSONObject getDataByTag(String tagNames, Date startTime, Date endTime) {
        String apiPath = "/jhTagValue/getNewTagValue";
        Map<String, String> query = new HashMap<String, String>();
        query.put("endDate", String.valueOf(endTime.getTime()));
        query.put("startDate", String.valueOf(startTime.getTime()));
        query.put("tagNames", tagNames);
        String results = httpUtil.get(httpProperties.getUrlApiJHOne() + apiPath, query);
        JSONObject jsonObject = JSONObject.parseObject(results, Feature.OrderedField);
        JSONObject data = jsonObject.getJSONObject("data");

        return data;
    }

    private void dealChart(JSONObject data, String tag1, String tag2, double mx1, double mn1, double mx2, double mn2, String serie1, String serie2, String addr) {
        List<Double> tagObject1 = getValuesByTag(data, tag1);
        List<Double> tagObject2 = getValuesByTag(data, tag2);
        List<Double> tempObject1 = new ArrayList<>();
        tempObject1.addAll(tagObject1);
        List<Double> tempObject2 = new ArrayList<>();
        tempObject2.addAll(tagObject2);
        tempObject1.removeAll(Collections.singleton(null));
        tempObject2.removeAll(Collections.singleton(null));
        Double max1 = tempObject1.size() > 0 ? Collections.max(tempObject1) * 1.2 : mx1;
        Double min1 = tempObject1.size() > 0 ? Collections.min(tempObject1) * 0.8 : mn1;
        Double max2 = tempObject2.size() > 0 ? Collections.max(tempObject2) * 1.2 : mx2;
        Double min2 = tempObject2.size() > 0 ? Collections.min(tempObject2) * 0.8 : mn2;

        if (max1.equals(min1)) {
            max1 = mx1;
            min1 = mn1;
        }
        if (max2.equals(min2)) {
            max2 = mx2;
            min2 = mn2;
        }

        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie(serie1, tagObject1.toArray()));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie(serie2, tagObject2.toArray()));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        String title1 = "";
        String categoryAxisLabel1 = "";
        String[] yLabels = {serie1, serie2};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, 2, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put(addr, image1);
    }

    private void dealMeiQiLiang() {
        Double today = 0d;
        Double abs = 0d;
        Double today1 = selectAddDirct(1, DateUtil.getFormatDateTime(endTime, "yyyy/MM/dd"));
        Double today2 = selectAddDirct(2, DateUtil.getFormatDateTime(endTime, "yyyy/MM/dd"));
        Double yesterday1 = selectAddDirct(1, DateUtil.getFormatDateTime(beforeYesterday, "yyyy/MM/dd"));
        Double yesterday2 = selectAddDirct(2, DateUtil.getFormatDateTime(beforeYesterday, "yyyy/MM/dd"));
        if (today1 != null) {
            today += today1;
        }
        if (today2 != null) {
            today += today2;
        }
        abs = today;
        if (yesterday1 != null) {
            abs -= yesterday1;
        }
        if (yesterday2 != null) {
            abs -= yesterday2;
        }
        result.put("selectAddDirct1", df2.format(today));
        if (abs >= 0d) {
            result.put("selectAddDirct2", "升高");
        } else {
            result.put("selectAddDirct2", "降低");
        }
        result.put("selectAddDirct3", df2.format(Math.abs(abs.doubleValue())));
    }

    private void selectProdItemValue() {
        String apiPath = "/cokingYieldAndNumberHoles/selectProdItemValue";
        Map<String, String> query = new HashMap<String, String>();
        query.put("date", DateUtil.getFormatDateTime(endTime, "yyyy/MM/dd"));
        String results = httpUtil.get(httpProperties.getUrlApiJHOne() + apiPath, query);
        JSONObject jsonObject = JSONObject.parseObject(results, Feature.OrderedField);
        if (jsonObject != null) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                Double avg = data.getDouble("currentYield");
//            double total = 0d;
//            for (int i = 0; i < array.size(); i++) {
//                total += array.getJSONObject(i).getDouble("");
//            }
                result.put("selectProdItemValue1", avg != null ? df2.format(avg) : 0d);
            }
        } else {
            result.put("selectProdItemValue1", 0d);
        }
    }

    private Double selectAddDirct(int shift, String workDate) {
        Double result = null;
        String apiPath = "/adjustingInput/selectAddDirct";
        Map<String, String> query = new HashMap<String, String>();
        query.put("shift", String.valueOf(shift));
        query.put("tagName", tag1);
        query.put("workDate", workDate);
        String results = httpUtil.get(httpProperties.getUrlApiJHOne() + apiPath, query);
        JSONObject jsonObject = JSONObject.parseObject(results, Feature.OrderedField);
        if (jsonObject != null) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                data = data.getJSONObject("val");
                if (data != null) {
                    result = new Double(data.getString(tag1));
                }
            }
        }
        return result;
    }

    private void getCurrByDateTimeOld(String date) {
        String apiPath = "/coalBlendingParameter/getCurrByDateTime";
        Map<String, String> query = new HashMap<String, String>();
        query.put("date", date);
        String results = httpUtil.get(httpProperties.getUrlApiJHOne() + apiPath, query);
        JSONObject jsonObject = JSONObject.parseObject(results, Feature.OrderedField);
        if (jsonObject != null) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                Map<String, Object> map = data.getInnerMap();
                if (map != null) {
                    List<Map<String, Object>> rows = new ArrayList<>();
                    int i = 0;
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (i >= longTimeList.size()) {
                            break;
                        }
                        i++;
                        Map<String, Object> row = new HashMap<>();
                        Long lt = new Long(entry.getKey());
                        row.put("date", DateUtil.getFormatDateTime(new Date(lt), "yyyy/MM/dd"));
                        JSONArray array = JSONArray.class.isInstance(entry.getValue()) ? ((JSONArray) entry.getValue()) : null;
                        if (array != null && array.size() > 0) {
                            for (int j = 1; j <= 8; j++) {
                                JSONObject item = array.getJSONObject(j - 1);
                                if (item != null) {
                                    row.put("name" + j, item.getString("coalTypeDescr"));
                                    row.put("wet" + j, df2.format(item.getDouble("wetMatchRatio")));
                                    row.put("dry" + j, df2.format(item.getDouble("dryMatchRatio")));
                                } else {
                                    row.put("name" + j, "xxx");
                                    row.put("wet" + j, "xxx");
                                    row.put("dry" + j, "xxx");
                                }
                            }
                        }
                        rows.add(row);
                    }
                    result.put("sheet1", rows);
                }
            }
        }
    }

    private void getCurrByDateTime(String date) {
        String apiPath = "/coalBlendingParameter/getCurrByDateTime";
        Map<String, String> query = new HashMap<String, String>();
        query.put("date", date);
        String results = httpUtil.get(httpProperties.getUrlApiJHOne() + apiPath, query);
        JSONObject jsonObject = JSONObject.parseObject(results, Feature.OrderedField);
        if (jsonObject != null) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                Map<String, Object> map = data.getInnerMap();
                if (map != null) {
                    List<Map<String, Object>> rows = new ArrayList<>();
                    int i = 0;
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (i >= longTimeList.size()) {
                            break;
                        }
                        i++;
                        Map<String, Object> row1 = new HashMap<>();
                        Map<String, Object> row2 = new HashMap<>();
                        Map<String, Object> row3 = new HashMap<>();
                        Long lt = new Long(entry.getKey());
                        row1.put("date", DateUtil.getFormatDateTime(new Date(lt), "yyyy/MM/dd"));
                        row2.put("date", "");
                        row3.put("date", "");
                        row1.put("desc", "名称");
                        row2.put("desc", "湿配比");
                        row3.put("desc", "干配比");
                        JSONArray array = JSONArray.class.isInstance(entry.getValue()) ? ((JSONArray) entry.getValue()) : null;
                        if (array != null && array.size() > 0) {
                            for (int j = 1; j <= 8; j++) {
                                JSONObject item = array.getJSONObject(j - 1);
                                if (item != null) {
                                    row1.put("val" + j, item.getString("coalTypeDescr"));
                                    row2.put("val" + j, df2.format(item.getDouble("wetMatchRatio")));
                                    row3.put("val" + j, df2.format(item.getDouble("dryMatchRatio")));
                                } else {
                                    row1.put("val" + j, "xxx");
                                    row2.put("val" + j, "xxx");
                                    row3.put("val" + j, "xxx");
                                }
                            }
                        }
                        rows.add(row1);
                        rows.add(row2);
                        rows.add(row3);
                    }
                    result.put("sheet1", rows);
                }
            }
        }
    }

    private Double dealOffset(Double min, Double max, String tagName, JSONObject data) {
        Double result = 0d;
        Double actual = getLastByTag(data, tagName);
        if (actual != null) {
            if (actual > max) {
                result = actual - max;
            } else if (actual < min) {
                result = actual - min;
            }
        }
        return result;
    }

    private void comm(String path) {
        try {
            XWPFDocument doc = WordExportUtil.exportWord07(path, result);

            // 合并配合煤配比表格的日期单元格
            List<XWPFTable> tt = doc.getTables();
            if (tt != null && tt.size() > 1) {
                for (int i = 1; i + 2 < tt.get(1).getRows().size(); i += 3) {
                    mergeCellsVertically(tt.get(1), 0, i, i + 2);
                }
            }

            String fileName = sequence + "_焦化生产诊断报告_" + DateUtil.getFormatDateTime(endTime, "yyyyMMdd") + ".docx";
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
            reportIndex.setReportCategoryCode(JobEnum.jh_shengchanzhenduanbaogao.getCode());
            reportIndex.setPath(filePath);
            reportIndexMapper.insert(reportIndex);

            log.info("焦化生产诊断报告word文档生成完毕" + filePath);
        } catch (Exception e) {
            log.error("焦化生产诊断报告word文档失败", e);
        }
    }

    // word跨行并单元格
    public void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
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
     * 构造起始时间
     */
    private void initDateTime() {
        // 当前时间点
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        // 昨日零点，作为结束时间点
        endTime = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        beforeYesterday = cal.getTime();
        // 当月第一天，作为开始时间点
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        startTime = startDate;

        while (startDate.before(endTime)) {
            // 拼接x坐标轴
            categoriesList.add(DateUtil.getFormatDateTime(startDate, "MM-dd"));
            dateList.add(DateUtil.getFormatDateTime(startDate, DateUtil.yyyyMMddFormat));
            longTimeList.add(startDate.getTime() + "");

            // 递增日期
            cal.add(Calendar.DAY_OF_MONTH, 1);
            startDate = cal.getTime();
        }

        categoriesList.add(DateUtil.getFormatDateTime(endTime, "MM-dd"));
        dateList.add(DateUtil.getFormatDateTime(endTime, DateUtil.yyyyMMddFormat));
        longTimeList.add(endTime.getTime() + "");
    }

    private WordImageEntity image(JFreeChart chart) {
        WordImageEntity image = new WordImageEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            chart.getPlot().setBackgroundAlpha(0.1f);
            chart.getPlot().setNoDataMessage("当前没有有效的数据");
            ChartUtilities.writeChartAsJPEG(baos, chart, 600, 290);
            image.setHeight(256);
            image.setWidth(554);
            image.setData(baos.toByteArray());
            image.setType(WordImageEntity.Data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return image;
    }
}
