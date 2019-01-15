package com.cisdi.steel.module.job.a1.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.WordImageEntity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class GaoLuDocMain {

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private HttpProperties httpProperties;

    @Autowired
    private JobProperties jobProperties;

    private String version6 = "6.0";
    private String version8 = "8.0";

    @Scheduled(cron = "0 30 6 * * ?")
    public void mainTask() {
        result = new HashMap<>();
        dealPart1(version8, L1);
        dealPart2(version8, L2);

        dealPart3(version8, L3);
        dealPart5(version8);
        dealPart6(version8, L6);
        dealPart7(version8, L7);

        dealPart8(version8, L8);
        dealPart9(version8, L9);
        dealPart11(version8, L11);
        dealPart13(version8, L13);
        dealPart14(version8);
        dealPart15(version8, L15);
        dealPart17(version8, L17);

        mainDeal(version8);
        log.error("高炉word生成完毕！");
    }

    public void mainDeal(String version) {
        if ("6.0".equals(version)) {
//            comm(jobProperties.getTemplatePath() + File.separator + "doc" + File.separator + "五烧每日操业会-设计版v1.docx");
        } else {
            comm(jobProperties.getTemplatePath() + File.separator + "doc" + File.separator + "八高炉操业会议纪要（实施版）.docx");
        }
    }

    /**
     * doc最后结果
     */
    private HashMap<String, Object> result = null;

    private String[] L1 = new String[]{"BF8_L2C_BD_ProductionSum_1d_cur", "BF8_L2C_BD_CokeRate_1d_avg", "BF8_L2M_FuelRate_1d_avg"};
    private String[] L2 = new String[]{"BF8_L2M_SinterRatio_evt", "BF8_L2M_LumporeRatio_1h_avg", "BF8_L2M_PelletsRatio_1h_avg"};
    private String[] L3 = new String[]{"CSR", "M40", "Ad"};
    private String[] L6 = new String[]{"TFe", "FeO"};
    private String[] L7 = new String[]{"Vdaf", "Ad", "Fcad"};
    private String[] L8 = new String[]{"BF8_L2C_BD_HotBlastFlow_1d_avg", "BF8_L2C_BD_ColdBlastPress_1d_avg", "BF8_L2C_BD_Pressdiff_1d_avg"};
    private String[] L9 = new String[]{"BF8_L2C_BD_W_1d_avg", "BF8_L2C_BD_Z_1d_avg"};
    private String[] L11 = new String[]{"BF8_L2C_BH_T0146_1d_avg", "BF8_L1R_TP_StockLineSetL4_evt"};
    private String[] L13 = new String[]{"BF8_L2C_TP_GasUtilization_1d_avg"};
    private String[] L15 = new String[]{"BF8_L2C_HMTemp_1m_max"};
    private String[] L17 = new String[]{"BF8_L2C_BD_HotBlastFlow_1d_avg", "BF8_L2C_BD_ColdBlastPress_1d_avg", "BF8_L2C_BD_Pressdiff_1d_avg", "BF8_L2C_BD_OxygenRate_1d_avg",
            "BF8_L2C_BD_HotBlastTemp_1d_avg", "BF8_L2C_BD_BH_1d_avg", "BF8_L2C_BD_TopPress_1d_avg", "BF8_L2M_FuelRate_1d_avg", "BF8_L2M_FuelRate_1d_avg", "BF8_L2M_PelletsRatio_1h_avg",
            "BF8_L2M_SinterRatio_evt", "BF8_L2C_BD_CokeLoad_1d_avg", "BF8_L2C_SH_OreBatchWeight", "BF8_L2C_SH_CokeBatchWeight",
            "BF8_L2C_BD_BlastVelocityAct_1d_avg", "BF8_L2C_BD_Ek_1d_avg", "BF8_L2C_BD_FlamTemp_1d_avg"
    };

    private BigDecimal dealType(Object o) {
        BigDecimal v = BigDecimal.ZERO;
        if (o instanceof Integer) {
            v = new BigDecimal((Integer) o);
        } else {
            v = (BigDecimal) o;
        }

        return v;
    }

    List<String> categoriesList = new ArrayList<>();
    List<String> dateList = new ArrayList<>();

    private List<List<Double>> part1(String version, String[] tagNames) {

        Date date = new Date();
        Date beginDate = DateUtil.addDays(date, -30);
        Date start = beginDate;

        List<List<Double>> doubles = new ArrayList<>();


        while (beginDate.before(date)) {
            categoriesList.add(DateUtil.getFormatDateTime(beginDate, "MM月dd日"));
            dateList.add(DateUtil.getFormatDateTime(beginDate, DateUtil.yyyyMMddFormat));
            beginDate = DateUtil.addDays(beginDate, 1);
        }


        JSONObject jsonObject = dataHttp(tagNames, start, date, version);
        if (Objects.nonNull(jsonObject)) {
            for (String tagName : tagNames) {
                List<Double> a = new ArrayList<>();
                JSONObject tagObject = jsonObject.getJSONObject(tagName);
                if (Objects.nonNull(tagObject)) {
                    Map<String, Object> innerMap = tagObject.getInnerMap();
                    Set<String> keySet = innerMap.keySet();
                    Long[] list = new Long[keySet.size()];
                    int k = 0;
                    for (String key : keySet) {
                        list[k] = Long.valueOf(key);
                        k++;
                    }
                    Arrays.sort(list);
                    for (String da : dateList) {
                        Double v = null;
                        for (Long li : list) {
                            Date dd = new Date(Long.valueOf(li));
                            String fomDate = DateUtil.getFormatDateTime(dd, DateUtil.yyyyMMddFormat);
                            if (fomDate.equals(da)) {
                                BigDecimal vv = (BigDecimal) innerMap.get(li + "");
                                v = vv.doubleValue();
                                break;
                            }
                        }
                        a.add(v);
                    }
                }
                doubles.add(a);
            }
        }

        return doubles;

    }

    private List<List<Double>> part2(String version, String[] tagNames) {
        Date date = new Date();
        date = DateUtil.addDays(date, -1);
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        Date dateEndTime = DateUtil.getDateEndTime(date);


        List<List<Double>> doubles = new ArrayList<>();

        JSONObject jsonObject = dataHttp(tagNames, dateBeginTime, dateEndTime, version);


        for (String tagName : tagNames) {
            List<Double> a = new ArrayList<>();
            if (Objects.nonNull(jsonObject)) {
                JSONObject tagObject = jsonObject.getJSONObject(tagName);
                if (Objects.nonNull(tagObject)) {
                    Map<String, Object> innerMap = tagObject.getInnerMap();
                    Set<String> keySet = innerMap.keySet();
                    Long[] list = new Long[keySet.size()];
                    int k = 0;
                    for (String key : keySet) {
                        list[k] = Long.valueOf(key);
                        k++;
                    }
                    Arrays.sort(list);

                    Double v = 0.00;
                    for (Long li : list) {
                        BigDecimal vv = (BigDecimal) innerMap.get(li + "");
                        v += vv.doubleValue();
                    }
                    if (Objects.nonNull(v)) {
                        v = v / list.length;
                    }
                    a.add(v);
                } else {
                    a.add(0.00);
                }
                doubles.add(a);
            } else {
                a.add(0.00);
                doubles.add(a);
            }
        }

        return doubles;

    }

    private List<List<Double>> part3(String version, String[] tagNames, String[] cBrandCodes, String type) {

        Date date = new Date();
        Date beginDate = DateUtil.addDays(date, -30);
        Date start = beginDate;

        List<List<Double>> doubles = new ArrayList<>();


        while (beginDate.before(date)) {
            categoriesList.add(DateUtil.getFormatDateTime(beginDate, "MM月dd日"));
            dateList.add(DateUtil.getFormatDateTime(beginDate, DateUtil.yyyyMMddFormat));
            beginDate = DateUtil.addDays(beginDate, 1);
        }

        for (String cBrandCode : cBrandCodes) {
            List<Double> csrR = new ArrayList<>();
            List<Double> m40R = new ArrayList<>();
            List<Double> adR = new ArrayList<>();
            List<Double> a4R = new ArrayList<>();

            JSONArray jsonArray = dataHttp1(cBrandCode, type, start, date, version);
            for (String da : dateList) {
                List<BigDecimal> csr = new ArrayList<>();
                List<BigDecimal> m40 = new ArrayList<>();
                List<BigDecimal> ad = new ArrayList<>();
                List<BigDecimal> a4 = new ArrayList<>();

                BigDecimal a = BigDecimal.ZERO;
                BigDecimal b = BigDecimal.ZERO;
                BigDecimal c = BigDecimal.ZERO;
                BigDecimal d = BigDecimal.ZERO;
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (Objects.nonNull(jsonObject)) {
                        JSONObject analysis = jsonObject.getJSONObject("analysis");
                        long sampletime = analysis.getLong("sampletime");
                        Date dd = new Date(sampletime);
                        String fomDate = DateUtil.getFormatDateTime(dd, DateUtil.yyyyMMddFormat);

                        if (da.equals(fomDate)) {
                            JSONObject object = jsonObject.getJSONObject("values");
                            Map<String, Object> innerMap = object.getInnerMap();
                            Object o = innerMap.get(tagNames[0]);
                            Object o1 = null;
                            Object o2 = null;
                            Object o3 = null;
                            if (tagNames.length > 1) {
                                o1 = innerMap.get(tagNames[1]);
                            }
                            if (tagNames.length > 2) {
                                o2 = innerMap.get(tagNames[2]);
                            }
                            if (tagNames.length > 3) {
                                o3 = innerMap.get(tagNames[3]);
                            }
                            if (Objects.nonNull(o)) {
                                BigDecimal v = dealType(o);
                                csr.add(v);
                                a = a.add(v);
                            }
                            if (Objects.nonNull(o1)) {
                                BigDecimal v = dealType(o1);
                                m40.add(v);
                                b = b.add(v);
                            }
                            if (Objects.nonNull(o2)) {
                                BigDecimal v = dealType(o2);
                                ad.add(v);
                                c = c.add(v);
                            }
                            if (Objects.nonNull(o3)) {
                                BigDecimal v = dealType(o3);
                                a4.add(v);
                                d = d.add(v);
                            }
                        }
                    }
                }
                if (csr.size() != 0) {
                    a = a.divide(new BigDecimal(csr.size()), 2, BigDecimal.ROUND_HALF_UP);
                }
                a.setScale(2, BigDecimal.ROUND_HALF_UP);
                csrR.add(a.doubleValue());

                if (m40.size() != 0) {
                    b = b.divide(new BigDecimal(m40.size()), 2, BigDecimal.ROUND_HALF_UP);
                }
                b.setScale(2, BigDecimal.ROUND_HALF_UP);
                m40R.add(b.doubleValue());

                if (ad.size() != 0) {
                    c = c.divide(new BigDecimal(ad.size()), 2, BigDecimal.ROUND_HALF_UP);
                }
                c.setScale(2, BigDecimal.ROUND_HALF_UP);
                adR.add(c.doubleValue());

                if (a4.size() != 0) {
                    d = d.divide(new BigDecimal(a4.size()), 2, BigDecimal.ROUND_HALF_UP);
                }
                d.setScale(2, BigDecimal.ROUND_HALF_UP);
                a4R.add(d.doubleValue());
            }
            doubles.add(csrR);
            if (tagNames.length > 1) {
                doubles.add(m40R);
            }
            if (tagNames.length > 2) {
                doubles.add(adR);
            }
            if (tagNames.length > 3) {
                doubles.add(a4R);
            }
        }
        List<List<Double>> result = new ArrayList<>();

        for (int j = 0; j < tagNames.length; j++) {
            List<Double> rd = new ArrayList<>();
            List<Double> doubles1 = doubles.get(j);
            for (int i = 0; i < doubles1.size(); i++) {
                Double aDouble = doubles.get(j).get(i);
                Double bDouble = 0.0;
                if ((j + tagNames.length) < doubles.size()) {
                    bDouble = doubles.get(j + tagNames.length).get(i);
                }
                Double cDouble = 0.0;
                if ((j + 2 * tagNames.length) < doubles.size()) {
                    cDouble = doubles.get(j + 2 * tagNames.length).get(i);
                }
                Double x = (aDouble + bDouble + cDouble) / cBrandCodes.length;
                rd.add(x);
                result.add(rd);
            }
        }

//
//        List<Double> rd = new ArrayList<>();
//        List<Double> doubles1 = doubles.get(0);
//        for (int i = 0; i < doubles1.size(); i++) {
//            Double aDouble = doubles.get(0).get(i);
//            Double bDouble = doubles.get(3).get(i);
//            Double cDouble = doubles.get(6).get(i);
//
//            Double x = (aDouble + bDouble + cDouble) / cBrandCodes.length;
//            rd.add(x);
//        }
//
//        List<Double> rd1 = new ArrayList<>();
//        List<Double> doubles2 = doubles.get(1);
//        for (int i = 0; i < doubles2.size(); i++) {
//            Double aDouble = doubles.get(1).get(i);
//            Double bDouble = doubles.get(4).get(i);
//            Double cDouble = doubles.get(7).get(i);
//
//            Double x = (aDouble + bDouble + cDouble) / cBrandCodes.length;
//            rd1.add(x);
//        }
//
//        List<Double> rd2 = new ArrayList<>();
//        List<Double> doubles3 = doubles.get(2);
//        for (int i = 0; i < doubles3.size(); i++) {
//            Double aDouble = doubles.get(2).get(i);
//            Double bDouble = doubles.get(5).get(i);
//            Double cDouble = doubles.get(8).get(i);
//
//            Double x = (aDouble + bDouble + cDouble) / cBrandCodes.length;
//            rd2.add(x);
//        }
//
//        result.add(rd);
//        result.add(rd1);
//        result.add(rd2);
        return result;

    }

    private List<Map<String, Object>> part4(String version) {

        Date date = new Date();
        Date beginDate = DateUtil.addDays(date, -8);

        List<Map<String, Object>> result = new ArrayList<>();

        int index = 1;
        while (beginDate.before(date)) {
            String dateTime = DateUtil.getFormatDateTime(beginDate, "yyyy-MM-dd 23:00:00");
            String dateTime1 = DateUtil.getFormatDateTime(beginDate, "yyyy-MM-dd 24:00:00");

            Date date1 = DateUtil.strToDate(dateTime, DateUtil.fullFormat);
            Date date2 = DateUtil.strToDate(dateTime1, DateUtil.fullFormat);
            JSONArray jsonArray = dataHttp2(date1, date2, version);

            Map<String, Object> map = new HashMap<>();
            List<BigDecimal> anglesetList = new ArrayList<>();
            String roundsetC = "";
            String roundsetO = "";

            boolean flag1 = false;
            boolean flag2 = false;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (Objects.nonNull(jsonObject)) {
                    JSONObject batchIndex = jsonObject.getJSONObject("batchIndex");
                    if (Objects.nonNull(batchIndex)) {
                        String typ = batchIndex.getString("typ");

                        if (StringUtils.isNotBlank(typ) && "C".equals(typ)) {
                            JSONArray batchDistributions = jsonObject.getJSONArray("batchDistributions");
                            for (int j = 0; j < batchDistributions.size(); j++) {
                                JSONObject object = batchDistributions.getJSONObject(j);
                                BigDecimal roundset = object.getBigDecimal("roundset");
                                if (roundset.intValue() != 0) {
                                    roundsetC += roundset;
                                }

                                BigDecimal angleset = object.getBigDecimal("angleset");
                                anglesetList.add(angleset);
                            }
                            flag1 = true;
                        }
                        if (StringUtils.isNotBlank(typ) && "O".equals(typ)) {
                            JSONArray batchDistributions = jsonObject.getJSONArray("batchDistributions");
                            for (int j = 0; j < batchDistributions.size(); j++) {
                                JSONObject object = batchDistributions.getJSONObject(j);
                                BigDecimal roundset = object.getBigDecimal("roundset");
                                if (roundset.intValue() != 0) {
                                    roundsetO += roundset;
                                }
                            }
                            flag2 = true;
                        }
                    }
                    if (flag1 && flag2) {
                        map.put("roundsetC", roundsetC);
                        map.put("roundsetO", roundsetO);

                        for (int m = 0; m < anglesetList.size(); m++) {
                            map.put("a" + (m + 1), anglesetList.get(m));
                        }
                        break;
                    }
                }
            }
            beginDate = DateUtil.addDays(beginDate, 1);
            map.put("index", index++);
            result.add(map);
        }


        return result;

    }


    private void dealPart1(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        Object[] objects3 = doubles.get(2).toArray();
        List<List<Double>> doubles2 = part2(version, tagNames);
        result.put("part1", doubles2.get(0).get(0));
        result.put("part2", doubles2.get(1).get(0));
        result.put("part3", doubles2.get(2).get(0));

        /**
         * 产量BF8_L2C_BD_ProductionSum_1d_cur 0-8192
         * 焦比BF8_L2C_BD_CokeRate_1d_avg 0-365
         * 燃料比BF8_L2M_FuelRate_1d_avg 0-1409783.0496
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("产量", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("焦比", objects2));

        // 标注类别
        Vector<Serie> series3 = new Vector<Serie>();
        series3.add(new Serie("燃料比", objects3));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);
        vectors.add(series3);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String valueAxisLabel1 = null;


        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 8192, 0, 360, 0, 500, tagNames.length);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg1", image1);
    }

    private void dealPart2(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        Object[] objects3 = doubles.get(2).toArray();
        List<List<Double>> doubles2 = part2(version, tagNames);
        result.put("part4", doubles2.get(0).get(0));
        result.put("part5", doubles2.get(1).get(0));
        result.put("part6", doubles2.get(2).get(0));


        /**
         * A烧结矿BF8_L2M_SinterRatio_evt
         * B块矿 BF8_L2M_LumporeRatio_1h_avg
         * P球团 BF8_L2M_PelletsRatio_1h_avg
         *
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("A烧结矿", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("B块矿", objects2));

        // 标注类别
        Vector<Serie> series3 = new Vector<Serie>();
        series3.add(new Serie("P球团", objects3));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);
        vectors.add(series3);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String valueAxisLabel1 = null;


        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 2, 0, 2, 0, 2, tagNames.length);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg2", image1);
    }

    private void dealPart3(String version, String[] tagNames) {
        String[] cBrandCodes = {"1_2_LYJJ_COKE", "WGYJJT_COKE", "6_7_LYJJ_COKE"};
        List<List<Double>> doubles = part3(version, tagNames, cBrandCodes, "LG");
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        Object[] objects3 = doubles.get(2).toArray();
        /**
         *          CSR
         *         M40
         *         Ad
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("CSR", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("M40", objects2));

        // 标注类别
        Vector<Serie> series3 = new Vector<Serie>();
        series3.add(new Serie("Ad", objects3));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);
        vectors.add(series3);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String valueAxisLabel1 = null;


        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 8192, 0, 360, 0, 500, tagNames.length);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg3", image1);
    }

    private void dealPart5(String version) {
        String[] cBrandCodes = {"6_SJK_SINTER", "6_SJK_SINTER", "6_SJK_SINTER"};
        /**
         *   <5mm GL5
         *         >40mm GF40
         *         5-10mm
         */
        //转鼓
        String[] lp = {"TI"};
        //粒度
        String[] lg = {"LF5", "GF40", "GF5"};
        List<List<Double>> lPList = part3(version, lp, cBrandCodes, "LP");
        List<List<Double>> lGList = part3(version, lg, cBrandCodes, "LG");


        Object[] objects1 = lPList.get(0).toArray();

        Object[] objects2 = lGList.get(0).toArray();
        Object[] objects3 = lGList.get(1).toArray();
        Object[] objects4 = lGList.get(2).toArray();

        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("转鼓", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("<5mm", objects2));

        // 标注类别
        Vector<Serie> series3 = new Vector<Serie>();
        series2.add(new Serie(">40mm", objects3));

        // 标注类别
        Vector<Serie> series4 = new Vector<Serie>();
        series2.add(new Serie("5-10mm", objects4));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);
        vectors.add(series3);
        vectors.add(series4);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String valueAxisLabel1 = null;


        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 8192, 0, 360, 0, 500, 4);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg5", image1);
    }

    private void dealPart6(String version, String[] tagNames) {
        String[] cBrandCodes = {"6_SJK_SINTER", "6_SJK_SINTER", "6_SJK_SINTER"};
        List<List<Double>> doubles = part3(version, tagNames, cBrandCodes, "LC");
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("TFe", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("FeO", objects2));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String valueAxisLabel1 = null;


        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 1, 0, 1, 0, 1, tagNames.length);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg6", image1);
    }

    private void dealPart7(String version, String[] tagNames) {
        String[] cBrandCodes = {"6_SJK_SINTER", "6_SJK_SINTER", "6_SJK_SINTER"};
        List<List<Double>> doubles = part3(version, tagNames, cBrandCodes, "LC");
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        Object[] objects3 = doubles.get(2).toArray();

        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("Vdaf", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("Ad", objects2));

        // 标注类别
        Vector<Serie> series3 = new Vector<Serie>();
        series3.add(new Serie("Fcad", objects3));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);
        vectors.add(series3);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String valueAxisLabel1 = null;


        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 8192, 0, 360, 0, 500, tagNames.length);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg7", image1);
    }

    private void dealPart8(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        Object[] objects3 = doubles.get(2).toArray();
        List<List<Double>> doubles2 = part2(version, tagNames);
        result.put("part7", doubles2.get(0).get(0));
        result.put("part8", doubles2.get(1).get(0));
        result.put("part9", doubles2.get(2).get(0));

        /**
         * 风量BF8_L2C_BD_HotBlastFlow_1d_avg
         * 风压BF8_L2C_BD_ColdBlastPress_1d_avg
         * 压差BF8_L2C_BD_Pressdiff_1d_avg
         *
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("风量", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("风压", objects2));

        // 标注类别
        Vector<Serie> series3 = new Vector<Serie>();
        series3.add(new Serie("压差", objects3));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);
        vectors.add(series3);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String valueAxisLabel1 = null;


        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 5500, 0, 1, 0, 180, tagNames.length);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg8", image1);
    }

    private void dealPart9(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        List<List<Double>> doubles2 = part2(version, tagNames);
        result.put("part10", doubles2.get(0).get(0));
        result.put("part11", doubles2.get(1).get(0));
        /**
         * W BF8_L2C_BD_W_1d_avg
         * Z BF8_L2C_BD_Z_1d_avg
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("W", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("Z", objects2));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String valueAxisLabel1 = null;


        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 1, 0, 8, 0, 180, tagNames.length);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg9", image1);
    }

    private void dealPart11(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        List<List<Double>> doubles2 = part2(version, tagNames);
        result.put("part12", doubles2.get(0).get(0));
        result.put("part13", doubles2.get(1).get(0));

        /**
         * 炉芯BF8_L2C_BH_T0146_1d_avg
         * L4x BF8_L1R_TP_StockLineSetL4_evt
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("炉芯", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("L4x", objects2));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String valueAxisLabel1 = null;


        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 210, 0, 1, 0, 180, tagNames.length);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg11", image1);
    }

    private void dealPart13(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames);
        Object[] objects1 = doubles.get(0).toArray();
        List<List<Double>> doubles2 = part2(version, tagNames);
        result.put("part14", doubles2.get(0).get(0));
        /**
         *  煤气利用率  BF8_L2C_TP_GasUtilization_1d_avg
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("煤气利用率", objects1));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String valueAxisLabel1 = null;


        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 48, 0, 1, 0, 180, tagNames.length);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg13", image1);
    }

    private void dealPart14(String version) {
        List<Map<String, Object>> maps = part4(version);
        result.put("sheet14", maps);
    }

    private void dealPart15(String version, String[] tagNames) {
        String[] cBrandCodes = {"HM"};
        String[] si = {"Si"};
        List<List<Double>> lc = part3(version, si, cBrandCodes, "LC");

        List<List<Double>> tagName = part2(version, tagNames);

        Object[] objects = lc.get(0).toArray();
        Object[] objects1 = tagName.get(0).toArray();

        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("Si", objects));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("PT", objects1));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String valueAxisLabel1 = null;


        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 1, 0, 1, 0, 1, 2);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg15", image1);

    }

    private void dealPart17(String version, String[] tagNames) {
        Map<String, Double> map = new HashMap();
        map.put("cz1", 5750.00);
        map.put("cz2", 385.00);
        map.put("cz3", 170.00);
        map.put("cz4", 1800.00);
        map.put("cz5", 1180.00);
        map.put("cz6", 10.00);
        map.put("cz7", 220.00);
        map.put("cz8", 520.00);
        map.put("cz9", 20.00);
        map.put("cz10", 5.00);
        map.put("cz11", 75.00);
        map.put("cz12", 4.60);
        map.put("cz13", 90.00);
        map.put("cz14", 20.00);
        map.put("cz15", 240.00);
        map.put("cz16", 115.00);
        map.put("cz17", 2200.00);
        List<List<Double>> doubles2 = part2(version, tagNames);
        for (int i = 0; i < doubles2.size(); i++) {
            Double aDouble = doubles2.get(i).get(0);
            BigDecimal bigDecimal = new BigDecimal(aDouble);
            Double aDouble1 = map.get("cz" + (i + 1));
            BigDecimal bigDecimal1 = new BigDecimal(aDouble1);
            bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
            result.put("cz" + (i + 1), bigDecimal);

            BigDecimal subtract = bigDecimal.subtract(bigDecimal1);
            subtract = subtract.setScale(2, BigDecimal.ROUND_HALF_UP);
            result.put("pc" + (i + 1), subtract);
        }
    }

    private JSONObject dataHttp(String[] tagNames, Date beginDate, Date endDate, String version) {
        JSONObject query = new JSONObject();
        query.put("starttime", beginDate.getTime());
        query.put("endtime", endDate.getTime());
        query.put("tagnames", tagNames);
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String results = httpUtil.postJsonParams(getUrl(version), jsonString);
        JSONObject jsonObject = JSONObject.parseObject(results);
        JSONObject data = jsonObject.getJSONObject("data");
        return data;
    }

    private JSONArray dataHttp1(String brandCode, String type, Date beginDate, Date endDate, String version) {
        Map<String, String> map = new HashMap<>();
        map.put("starttime", beginDate.getTime() + "");
        map.put("endtime", endDate.getTime() + "");
        map.put("brandcode", brandCode);
        map.put("type", type);
        String results = httpUtil.get(getUrl1(version), map);
        JSONObject jsonObject = JSONObject.parseObject(results);
        JSONArray data = jsonObject.getJSONArray("data");
        return data;
    }

    private JSONArray dataHttp2(Date beginDate, Date endDate, String version) {
        Map<String, String> map = new HashMap<>();
        map.put("starttime", beginDate.getTime() + "");
        map.put("endtime", endDate.getTime() + "");
        String results = httpUtil.get(getUrl2(version), map);
        JSONObject jsonObject = JSONObject.parseObject(results);
        JSONArray data = jsonObject.getJSONArray("data");
        return data;
    }

    private void comm(String path) {
        //文档所有日期
        dealDate(result);
        try {
            XWPFDocument doc = WordExportUtil.exportWord07(path, result);
            String fileName = "高炉" + DateUtil.getFormatDateTime(new Date(), "yyyyMMdd") + "操业会议纪要（实施版）.docx";
            String filePath = jobProperties.getFilePath() + File.separator + "doc" + File.separator + fileName;
            FileOutputStream fos = new FileOutputStream(filePath);
            doc.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealDate(HashMap<String, Object> map) {
        Date date = new Date();
        String date1 = DateUtil.getFormatDateTime(date, "yyyy年MM月dd日 HH:mm");
        String date2 = DateUtil.getFormatDateTime(date, "yyyy年MM月dd日");

        //文档所有日期
        map.put("date1", date1);
        map.put("date2", date2);
    }

    /**
     * tag点名
     *
     * @param version
     * @return
     */
    private String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }

    /**
     * 检化验
     *
     * @param version
     * @return
     */
    private String getUrl1(String version) {
        return httpProperties.getGlUrlVersion(version) + "/analysisValues/sampletime";
    }

    /**
     * 布料制度
     *
     * @param version
     * @return
     */
    private String getUrl2(String version) {
        return httpProperties.getGlUrlVersion(version) + "/batches/distribution/period";
    }

    private WordImageEntity image(JFreeChart chart) {
        WordImageEntity image = new WordImageEntity();
        try {
            chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            chart.getPlot().setBackgroundAlpha(0.1f);
            chart.getPlot().setNoDataMessage("当前没有有效的数据");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ChartUtilities.writeChartAsJPEG(baos, chart, 600, 300);
            image.setHeight(350);
            image.setWidth(650);
            image.setData(baos.toByteArray());
            image.setType(WordImageEntity.Data);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return image;
    }
}
