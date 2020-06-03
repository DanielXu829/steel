package com.cisdi.steel.module.job.gl.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.WordImageEntity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.math.NumberArithmeticUtils;
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
import org.apache.tools.ant.util.DateUtils;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
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
public class GaoLuRiFenXiBaoGao {
    private static String version8 = "1.0";
    /**
     * doc最后结果
     */
    private HashMap<String, Object> result = null;
    private Date startTime = null;
    private Date endTime = null;
    private Date beforeYesterday = null;
    List<String> categoriesList = new ArrayList<>();
    List<String> dateList = new ArrayList<>();
    List<String> longTimeList = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String[] L1 = new String[]{
            "BF8_L2M_HMMassAct_1d_cur","BF8_L2M_Productivity_1d","BF8_L2M_BX_CokeRate_1d_cur","BF8_L2M_BX_CoalRate_1d_cur",
            "BF8_L2M_BX_FuelRate_1d_cur","BF8_L2C_BD_HotBlastTemp1_1d_avg"
    };
    private String[] L2 = new String[]{
            "BF8_L2M_ANA_COKE_H2O_1d_avg",
            "BF8_L2M_ANA_COKE_Vdaf_1d_avg","BF8_L2M_ANA_COKE_Ad_1d_avg","BF8_L2M_ANA_COKE_M40_1d_avg",
            "BF8_L2M_ANA_COKE_M10_1d_avg","BF8_L2M_ANA_COKE_CSR_1d_avg","BF8_L2M_ANA_COKE_CRI_1d_avg",
            "BF8_L2M_ANA_COAL_H2O_1d_avg","BF8_L2M_ANA_COAL_Vdaf_1d_avg","BF8_L2M_ANA_COAL_Fcad_1d_avg",
            "BF8_L2M_ANA_SINTER_TFe_1d_avg","BF8_L2M_ANA_SINTER_FeO_1d_avg","BF8_L2M_ANA_SINTER_CaO_1d_avg",
            "BF8_L2M_ANA_SINTER_SiO2_1d_avg","BF8_L2M_ANA_SINTER_MgO_1d_avg","BF8_L2M_ANA_SINTER_Al203_1d_avg",
            "BF8_L2M_ANA_SINTER_TFe_B2_avg","BF8_L2M_ANA_PELLETS_TFe_1d_avg","BF8_L2M_ANA_PELLETS_CaO_1d_avg",
            "BF8_L2M_ANA_PELLETS_SiO2_1d_avg","BF8_L2M_ANA_LUMPORE_TFe_1d_avg","BF8_L2M_ANA_LUMPORE_SiO2_1d_avg",
            "BF8_L2M_ANA_LUMPORE_Al2O3_1d_avg"
    };
    private String[] L3 = new String[]{
            "BF8_L2C_BD_BH_1d_avg","BF8_L2C_BD_ColdBlastFlow_1d_avg",
            "BF8_L2C_BD_ColdBlastPress_1d_avg","BF8_L2M_PressDiff_1d_avg","BF8_L2C_BD_TopPress_1d_avg",
            "BF8_L2M_HMTemp_1d_avg","BF8_L2M_BX_FuelRate_1d_cur","BF8_L2M_GasUtilization_1d_avg"
    };
    private String[] L4 = new String[]{
            "BF8_L2C_BD_SoftTempDiff_1d_avg"
    };

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

    private void initialData() {
        result = new HashMap<>();
        startTime = null;
        endTime = null;
        categoriesList = new ArrayList<>();
        dateList = new ArrayList<>();
        longTimeList = new ArrayList<>();
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void mainTask() {
        initialData();
        initDateTime();
        mainDeal(version8);
        log.info("高炉日生产分析报告word生成完毕！");
    }

    public void mainDeal(String version) {
        // 处理当前时间
        result.put("current_date", DateUtils.format(endTime, DateUtil.yyyyMMddChineseFormat));
        String[] allTagNames = ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(L1, L2), L3), L4);
        JSONObject data = getDataByTag(allTagNames, startTime, endTime, version);
        dealPart1(data);
        dealPart(data, "partTwo", L2);
        dealPart3(data);
        dealPart(data, "partFour", L4);
        List<ReportCategoryTemplate> reportCategoryTemplates = reportCategoryTemplateService.selectTemplateInfo(JobEnum.gl_rishengchanfenxibaogao_day.getCode(), LanguageEnum.cn_zh.getName(), "8高炉");
        if (CollectionUtils.isNotEmpty(reportCategoryTemplates)) {
            String templatePath = reportCategoryTemplates.get(0).getTemplatePath();
            log.info("高炉日生产分析报告模板路径：" + templatePath);
            comm(version, templatePath);
        }
    }

    /**
     * 通过tag点获取得数据，组装到result
     *
     * @param data
     * @param prefix
     */
    private void dealPart(JSONObject data, String prefix, String[] tagNames) {
        if (data != null && data.size() > 0) {
            for (int i = 0; i < tagNames.length; i++) {
                Double doubleValue = 0d;
                List<Double> valueObject = getValuesByTag(data, tagNames[i]);
                if (valueObject != null && valueObject.size() > 0) {
                    doubleValue = valueObject.get(valueObject.size() - 1);
                    result.put(prefix + String.valueOf(i + 1), parse(doubleValue));
                }
            }
        }
    }

    private void dealSingle(JSONObject data, String addr, String tagName) {
        if (data != null && data.size() > 0) {
            Double doubleValue = 0d;
            List<Double> valueObject = getValuesByTag(data, tagName);
            if (valueObject != null && valueObject.size() > 0) {
                doubleValue = valueObject.get(valueObject.size() - 1);
                result.put(addr, parse(3, doubleValue));
            }
        }
    }

    private void dealPart1(JSONObject data) {
        dealPart(data, "partOne", L1);
        dealChart1(data);
        dealChart2(data);

        Double increase = dealIncreaseYesterday(data, "BF8_L2M_HMMassAct_1d_cur");
        if (increase != null && increase >= 0d) {
            result.put("textOne1", "升高");
        } else {
            result.put("textOne1", "降低");
        }
        if (increase != null) {
            result.put("countOne1", parse(Math.abs(increase.doubleValue())));
        }
        else {
            result.put("countOne1", parse(increase));
        }

        increase = dealIncreaseYesterday(data, "BF8_L2M_BX_CokeRate_1d_cur");
        if (increase != null && increase >= 0d) {
            result.put("textOne2", "升高");
        } else {
            result.put("textOne2", "降低");
        }
        if (increase != null) {
            result.put("countOne2", parse(Math.abs(increase.doubleValue())));
        }
        else {
            result.put("countOne2", parse(increase));
        }

        increase = dealIncreaseYesterday(data, "BF8_L2M_BX_CoalRate_1d_cur");
        if (increase != null && increase >= 0d) {
            result.put("textOne3", "升高");
        } else {
            result.put("textOne3", "降低");
        }
        if (increase != null) {
            result.put("countOne3", parse(Math.abs(increase.doubleValue())));
        }
        else {
            result.put("countOne3", parse(increase));
        }

        result.put("countOne4", parse(dealMonthTotal(data, "BF8_L2M_HMMassAct_1d_cur", false)));
        result.put("countOne5", parse(dealMonthTotal(data, "BF8_L2M_BX_CokeRate_1d_cur", true)));
        result.put("countOne6", parse(dealMonthTotal(data, "BF8_L2M_BX_CoalRate_1d_cur", true)));
        result.put("countOne7", parse(dealMonthTotal(data, "BF8_L2M_BX_FuelRate_1d_cur", true)));
    }

    private void dealPart3(JSONObject data){
        dealPart(data, "partThree", L3);
        dealSingle(data, "partThree3", "BF8_L2C_BD_ColdBlastPress_1d_avg");
        dealSingle(data, "partThree4", "BF8_L2M_PressDiff_1d_avg");
        dealSingle(data, "partThree5", "BF8_L2C_BD_TopPress_1d_avg");
        result.put("offsetWet", parse(dealOffset(5d,10d,"BF8_L2C_BD_BH_1d_avg",data)));
        result.put("offsetBV", parse(dealOffset(5700d,5900d,"BF8_L2C_BD_ColdBlastFlow_1d_avg",data)));
        result.put("offsetBP", parse(3, dealOffset(0d,0.42d,"BF8_L2C_BD_ColdBlastPress_1d_avg",data)));
        result.put("offsetP", parse(3, dealOffset(0d,0.183d,"BF8_L2M_PressDiff_1d_avg",data)));
        result.put("offsetTP", parse(3, dealOffset(0.228d,0.236d,"BF8_L2C_BD_TopPress_1d_avg",data)));
        result.put("offsetPT", parse(dealOffset(1505d,1530d,"BF8_L2M_HMTemp_1d_avg",data)));
        result.put("offsetFR", parse(dealOffset(515d,525d,"BF8_L2M_BX_FuelRate_1d_cur",data)));
        result.put("offsetCO", parse(dealOffset(47d,52d,"BF8_L2M_GasUtilization_1d_avg",data)));
    }

    private Double dealOffset(Double min, Double max, String tagName, JSONObject data) {
        Double result = null;
        List<Double> tagObject = getValuesByTag(data, tagName);
        Double actual = tagObject.get(tagObject.size() - 1);
        if (actual != null) {
            if (actual > max) {
                result = actual - max;
            }
            else if (actual < min) {
                result = actual - min;
            }
            else {
                result = 0d;
            }
        }
        return result;
    }

    private Double dealIncreaseYesterday (JSONObject data, String tagName) {
        Double result = null;
        List<Double> tagObject = getValuesByTag(data, tagName);
        Double yesterday = tagObject.get(tagObject.size() - 1);
        Double twoDaysAgo = tagObject.get(tagObject.size() - 2);
        if (yesterday != null) {
            if (twoDaysAgo != null) {
                result = yesterday - twoDaysAgo;
            }
        }
        return result;
    }

    private Double dealMonthTotal (JSONObject data, String tagName, Boolean isAvg) {
        Double result = null;
        Double total = 0d;
        Double count = 0d;
        List<Double> tagObject = getValuesByTag(data, tagName);
        for (Double item : tagObject) {
            if (item != null) {
                total += item;
                count++;
            }
        }
        if (count > 0d) {
            if (isAvg) {
                result = total / count;
            } else {
                result = total;
            }
        }
        return result;
    }

    private void dealChart1(JSONObject data) {
        List<Double> tagObject1 = getValuesByTag(data, "BF8_L2M_HMMassAct_1d_cur");
        List<Double> tagObject2 = getValuesByTag(data, "BF8_L2M_BX_FuelRate_1d_cur");
        List<Double> tempObject1 = new ArrayList<>();
        tempObject1.addAll(tagObject1);
        List<Double> tempObject2 = new ArrayList<>();
        tempObject2.addAll(tagObject2);
        tempObject1.removeAll(Collections.singleton(null));
        tempObject2.removeAll(Collections.singleton(null));
        Double max1 = tempObject1.size() > 0 ? Collections.max(tempObject1) * 1.2 : 10000.0;
        Double min1 = tempObject1.size() > 0 ? Collections.min(tempObject1) * 0.8 : 0.0;
        Double max2 = tempObject2.size() > 0 ? Collections.max(tempObject2) * 1.2 : 600.0;
        Double min2 = tempObject2.size() > 0 ? Collections.min(tempObject2) * 0.8 : 400.0;

//        Double min1 = 0.0;
//        Double max1 = 10000.0;
//        Double min2 = 400.0;
//        Double max2 = 600.0;

        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("产量", tagObject1.toArray()));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("燃料比", tagObject2.toArray()));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        String title1 = "";
        String categoryAxisLabel1 = "";
        String[] yLabels = {"产量(t)", "燃料比(kg/t)"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, 2, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg1", image1);
    }

    private void dealChart2(JSONObject data) {
        List<Double> tagObject1 = getValuesByTag(data, "BF8_L2M_BX_CokeRate_1d_cur");
        List<Double> tagObject2 = getValuesByTag(data, "BF8_L2M_BX_CoalRate_1d_cur");
        List<Double> tempObject1 = new ArrayList<>();
        tempObject1.addAll(tagObject1);
        List<Double> tempObject2 = new ArrayList<>();
        tempObject2.addAll(tagObject2);
        tempObject1.removeAll(Collections.singleton(null));
        tempObject2.removeAll(Collections.singleton(null));
        Double max1 = tempObject1.size() > 0 ? Collections.max(tempObject1) * 1.2 : 10000.0;
        Double min1 = tempObject1.size() > 0 ? Collections.min(tempObject1) * 0.8 : 0.0;
        Double max2 = tempObject2.size() > 0 ? Collections.max(tempObject2) * 1.2 : 600.0;
        Double min2 = tempObject2.size() > 0 ? Collections.min(tempObject2) * 0.8 : 400.0;

//        Double min1 = 0.0;
//        Double max1 = 10000.0;
//        Double min2 = 400.0;
//        Double max2 = 600.0;

        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("焦比", tagObject1.toArray()));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("煤比", tagObject2.toArray()));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        String title1 = "";
        String categoryAxisLabel1 = "";
        String[] yLabels = {"焦比(kg/t)", "煤比(kg/t)"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, 2, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg2", image1);
    }

    private List<Double> getValuesByTag (JSONObject data, String tagName) {
        JSONObject tagObject = data.getJSONObject(tagName);
        List<Double> vals = new ArrayList<>();
        Map<String, Object> innerMap = tagObject == null ? null : tagObject.getInnerMap();
        for (String time : longTimeList) {
            Double val = null;
            if (innerMap != null) {
                BigDecimal big = (BigDecimal) innerMap.get(time);
                if (null != big) {
                    val = big.doubleValue();
                    if (val < 0) {
                        val = null;
                    }
                }
            }
            vals.add(val);
        }
        return vals;
    }

    /**
     * 通过tag点获取数据
     *
     * @param tagNames
     * @param startTime
     * @param endTime
     * @param version
     * @return
     */
    private JSONObject getDataByTag(String[] tagNames, Date startTime, Date endTime, String version) {
        String apiPath = "/getTagValues/tagNamesInRange";
        JSONObject query = new JSONObject();
        query.put("endtime", endTime.getTime());
        query.put("starttime", startTime.getTime());
        query.put("tagnames", tagNames);
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String results = httpUtil.postJsonParams(getUrl(version) + apiPath, jsonString);
        JSONObject jsonObject = JSONObject.parseObject(results, Feature.OrderedField);
        JSONObject data = jsonObject.getJSONObject("data");

        return data;
    }

    /**
     * 构造起始时间
     */
    private void initDateTime(){
        // 当前时间点
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        // 昨日零点，作为结束时间点
        endTime = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH,-1);
        beforeYesterday = cal.getTime();
        // 当月第一天，作为开始时间点
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        startTime = startDate;

        while (startDate.before(endTime)) {
            // 拼接x坐标轴
            categoriesList.add(DateUtil.getFormatDateTime(startDate, "MM-dd"));
            dateList.add(DateUtil.getFormatDateTime(startDate, DateUtil.yyyyMMddFormat));
            longTimeList.add(startDate.getTime()+"");

            // 递增日期
            cal.add(Calendar.DAY_OF_MONTH, 1);
            startDate = cal.getTime();
        }

        categoriesList.add(DateUtil.getFormatDateTime(endTime, "MM-dd"));
        dateList.add(DateUtil.getFormatDateTime(endTime, DateUtil.yyyyMMddFormat));
        longTimeList.add(endTime.getTime()+"");
    }

    /**
     * 8号高炉
     *
     * @param version
     * @return
     */
    private String getUrl(String version) {
//        if ("4.0".equals(version)) {
//            return httpProperties.getUrlApiSJThree();
//        } else {
//            return httpProperties.getUrlApiSJThree();
//        }
        return httpProperties.getUrlApiGLTwo();
    }

    /**
     * 生产word文档
     *
     * @param version
     * @param path
     */
    private void comm(String version, String path) {
        try {
            String sequence = "8高炉";
            XWPFDocument doc = WordExportUtil.exportWord07(path, result);

            List<XWPFTable> tt = doc.getTables();

            String fileName = sequence + "_高炉日生产分析报告_" + DateUtil.getFormatDateTime(endTime, "yyyyMMdd") + ".docx";
            String filePath = jobProperties.getFilePath() + File.separator + "doc" + File.separator + fileName;
            FileOutputStream fos = new FileOutputStream(filePath);
            doc.write(fos);
            fos.close();

            ReportIndex reportIndex = new ReportIndex();
            reportIndex.setCreateTime(endTime);
            reportIndex.setUpdateTime(endTime);
            reportIndex.setSequence(sequence);
            reportIndex.setIndexLang("cn_zh");
            reportIndex.setIndexType("report_day");
            reportIndex.setRecordDate(endTime);
            reportIndex.setName(fileName);
            reportIndex.setReportCategoryCode(JobEnum.gl_rishengchanfenxibaogao_day.getCode());
            reportIndex.setPath(filePath);
            reportIndexMapper.insert(reportIndex);

            log.info("高炉日生产分析报告word文档生成完毕" + filePath);
        } catch (Exception e) {
            log.error("高炉日生产分析报告word文档失败", e);
        }
    }

    /**
     * 生成图片
     * @param chart
     * @return
     */
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

    private String parse(Double v) {
        if (v == null) {
            return "   ";
        }
        Double value = NumberArithmeticUtils.roundingX(v, 2);
        if (value.equals(0d)) {
            return "0";
        } else {
            return value.toString();
        }
    }

    private String parse(int scale, Double v) {
        if (v == null) {
            return "   ";
        }
        Double value = NumberArithmeticUtils.roundingX(v, scale);
        if (value.equals(0d)) {
            return "0";
        } else {
            return value.toString();
        }
    }
}
