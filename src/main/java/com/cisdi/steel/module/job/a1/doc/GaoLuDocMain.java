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

    @Autowired
    private ReportIndexMapper reportIndexMapper;

    private String version6 = "6.0";
    private String version8 = "8.0";

    @Scheduled(cron = "0 0 12 * * ?")
    public void mainTask() {
        result = new HashMap<>();
//        mainDeal(version6);
        mainDeal(version8);
        log.error("高炉word生成完毕！");
    }

    public void mainDeal(String version) {
        String name = "8";
        if ("6.0".equals(version)) {
            name = "6";
            dealTagName6();
        } else if ("8.0".equals(version)) {
            name = "8";
            dealTagName8();
        }
        dealPart1(version, L1);
        dealPart2(version, L2);

        dealPart3(version, L3);
        dealPart5(version);
        dealPart6(version, L6);
        dealPart7(version, L7);

        dealPart8(version, L8);
        dealPart9(version, L9);
        dealPart11(version, L11);
        dealPart13(version, L13);
        dealPart14(version);
        dealPart15(version, L15);
        dealPart17(version, L17);
        comm(version, jobProperties.getTemplatePath() + File.separator + "doc" + File.separator + name + "高炉操业会议纪要（实施版）.docx");
    }

    /**
     * doc最后结果
     */
    private HashMap<String, Object> result = null;

    private void dealTagName6() {
        L1 = new String[]{"BF6_L2C_BD_ProductionSum_1d_cur", "BF6_L2M_CokeRate_1d_avg", "BF6_L2M_FuelRate_1d_avg"};
        L2 = new String[]{"BF6_L2M_SinterRatio_evt", "BF6_L2M_LumporeRatio_1h_avg", "BF6_L2M_PelletsRatio_1h_avg"};
        L3 = new String[]{"CSR", "M40", "Ad"};
        L6 = new String[]{"TFe", "FeO"};
        L7 = new String[]{"Vdaf", "Ad", "Fcad"};
        L8 = new String[]{"BF6_L2C_BD_HotBlastFlow_1d_avg", "BF6_L2C_BD_ColdBlastPress_1d_avg", "BF6_L2C_BD_Pressdiff_1d_avg"};
        L9 = new String[]{"BF8_L2C_BD_W_1d_avg", "BF8_L2C_BD_Z_1d_avg"};
        L11 = new String[]{"BF8_L2C_BH_T0146_1d_avg", "BF8_L2C_TP_StockLineSetL4_1d_avg"};
        L13 = new String[]{"BF8_L2C_TP_GasUtilization_1d_avg"};
        L15 = new String[]{"BF8_L2C_AnalysisSiValue_1d_avg", "BF8_L2C_HMTemp_1d_avg"};
        L17 = new String[]{"BF8_L2C_BD_HotBlastFlow_1d_avg", "BF8_L2C_BD_ColdBlastPress_1d_avg", "BF8_L2C_BD_Pressdiff_1d_avg", "BF8_L2C_BD_OxygenRate_1d_avg",
                "BF8_L2C_BD_HotBlastTemp_1d_avg", "BF8_L2C_BD_BH_1d_avg", "BF8_L2C_BD_TopPress_1d_avg", "BF8_L2M_FuelRate_1d_avg", "BF8_L2M_FuelRate_1d_avg", "BF8_L2M_PelletsRatio_1h_avg",
                "BF8_L2M_SinterRatio_evt", "BF8_L2C_BD_CokeLoad_1d_avg", "BF8_L2C_SH_OreBatchWeight", "BF8_L2C_SH_CokeBatchWeight",
                "BF8_L2C_BD_BlastVelocityAct_1d_avg", "BF8_L2C_BD_Ek_1d_avg", "BF8_L2C_BD_FlamTemp_1d_avg"
        };
    }

    private void dealTagName8() {
        L1 = new String[]{"BF8_L2C_BD_ProductionSum_1d_cur", "BF8_L2C_BD_CokeRate_1d_avg", "BF8_L2M_FuelRate_1d_avg"};
        L2 = new String[]{"BF8_L2M_SinterRatio_evt", "BF8_L2M_LumporeRatio_1h_avg", "BF8_L2M_PelletsRatio_1h_avg"};
        L3 = new String[]{"CSR", "M40", "Ad"};
        L6 = new String[]{"TFe", "FeO"};
        L7 = new String[]{"Vdaf", "Ad", "Fcad"};
        L8 = new String[]{"BF8_L2C_BD_HotBlastFlow_1d_avg", "BF8_L2C_BD_ColdBlastPress_1d_avg", "BF8_L2C_BD_Pressdiff_1d_avg"};
        L9 = new String[]{"BF8_L2C_BD_W_1d_avg", "BF8_L2C_BD_Z_1d_avg"};
        L11 = new String[]{"BF8_L2C_BH_T0146_1d_avg", "BF8_L2C_TP_StockLineSetL4_1d_avg"};
        L13 = new String[]{"BF8_L2C_TP_GasUtilization_1d_avg"};
        L15 = new String[]{"BF8_L2C_AnalysisSiValue_1d_avg", "BF8_L2C_HMTemp_1d_avg"};
        L17 = new String[]{"BF8_L2C_BD_HotBlastFlow_1d_avg", "BF8_L2C_BD_ColdBlastPress_1d_avg", "BF8_L2C_BD_Pressdiff_1d_avg", "BF8_L2C_BD_OxygenRate_1d_avg",
                "BF8_L2C_BD_HotBlastTemp_1d_avg", "BF8_L2C_BD_BH_1d_avg", "BF8_L2C_BD_TopPress_1d_avg", "BF8_L2M_FuelRate_1d_avg", "BF8_L2M_FuelRate_1d_avg", "BF8_L2M_PelletsRatio_1h_avg",
                "BF8_L2M_SinterRatio_evt", "BF8_L2C_BD_CokeLoad_1d_avg", "BF8_L2C_SH_OreBatchWeight", "BF8_L2C_SH_CokeBatchWeight",
                "BF8_L2C_BD_BlastVelocityAct_1d_avg", "BF8_L2C_BD_Ek_1d_avg", "BF8_L2C_BD_FlamTemp_1d_avg"
        };
    }

    private String[] L1 = new String[]{"BF8_L2C_BD_ProductionSum_1d_cur", "BF8_L2C_BD_CokeRate_1d_avg", "BF8_L2M_FuelRate_1d_avg"};
    private String[] L2 = new String[]{"BF8_L2M_SinterRatio_evt", "BF8_L2M_LumporeRatio_1h_avg", "BF8_L2M_PelletsRatio_1h_avg"};
    private String[] L3 = new String[]{"CSR", "M40", "Ad"};
    private String[] L6 = new String[]{"TFe", "FeO"};
    private String[] L7 = new String[]{"Vdaf", "Ad", "Fcad"};
    private String[] L8 = new String[]{"BF8_L2C_BD_HotBlastFlow_1d_avg", "BF8_L2C_BD_ColdBlastPress_1d_avg", "BF8_L2C_BD_Pressdiff_1d_avg"};
    private String[] L9 = new String[]{"BF8_L2C_BD_W_1d_avg", "BF8_L2C_BD_Z_1d_avg"};
    private String[] L11 = new String[]{"BF8_L2C_BH_T0146_1d_avg", "BF8_L2C_TP_StockLineSetL4_1d_avg"};
    private String[] L13 = new String[]{"BF8_L2C_TP_GasUtilization_1d_avg"};
    private String[] L15 = new String[]{"BF8_L2C_AnalysisSiValue_1d_avg", "BF8_L2C_HMTemp_1d_avg"};
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

    private List<List<Double>> part1(String version, String[] tagNames, int scale) {

        categoriesList.clear();
        dateList.clear();

        Date date = new Date();
        Date date1 = DateUtil.addDays(date, 1);
        Date beginDate = DateUtil.addDays(date1, -30);
        Date start = beginDate;

        List<List<Double>> doubles = new ArrayList<>();


        while (beginDate.before(date1)) {
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
                        if (Objects.nonNull(v)) {
                            a.add(v * scale);
                        } else {
                            a.add(v);
                        }
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

    private List<List<Double>> part3(String version, String[] tagNames, String[] cBrandCodes, String type, int scale) {
        categoriesList.clear();
        dateList.clear();

        Date date = new Date();
        Date date1 = DateUtil.addDays(date, 1);
        Date beginDate = DateUtil.addDays(date1, -30);
        Date start = beginDate;

        List<List<Double>> doubles = new ArrayList<>();


        while (beginDate.before(date1)) {
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
                int size = 1;
                Double aDouble = doubles.get(j).get(i);
                Double bDouble = 0.0;
                if ((j + tagNames.length) < doubles.size()) {
                    bDouble = doubles.get(j + tagNames.length).get(i);
                }
                Double cDouble = 0.0;
                if ((j + 2 * tagNames.length) < doubles.size()) {
                    cDouble = doubles.get(j + 2 * tagNames.length).get(i);
                }

                if (bDouble.doubleValue() != 0.0) {
                    size++;
                }
                if (cDouble.doubleValue() != 0.0) {
                    size++;
                }
                Double x = (aDouble + bDouble + cDouble) / size;
                if (Objects.nonNull(x)) {
                    rd.add(x * scale);
                } else {
                    rd.add(x);
                }
            }
            result.add(rd);
        }

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

    private List<Double> part5(String version) {
        List<Double> list = new ArrayList<>();
        Date date = new Date();
        date = DateUtil.addDays(date, -1);
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        Date dateEndTime = DateUtil.getDateEndTime(date);
        JSONArray jsonArray = dataHttp3(dateBeginTime, dateEndTime, version);
        if (Objects.nonNull(jsonArray) && jsonArray.size() > 0) {
            JSONObject object = jsonArray.getJSONObject(0);
            JSONObject values = object.getJSONObject("values");
            BigDecimal aggl = values.getBigDecimal("Aggl");
            BigDecimal r = values.getBigDecimal("R");

            list.add(aggl.doubleValue());
            list.add(r.doubleValue());
        }

        return list;
    }


    private void dealPart1(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        Object[] objects3 = doubles.get(2).toArray();
        List<List<Double>> doubles2 = part2(version, tagNames);
        result.put("part1", doubles2.get(0).get(0).intValue());
        result.put("part2", doubles2.get(1).get(0).intValue());
        result.put("part3", doubles2.get(2).get(0).intValue());

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

        int[] stack = {2, 1, 1};
        int[] ystack = {1, 2, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 6000, 8500, 300, 600, 300, 600, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg1", image1);
    }

    private void dealPart2(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 100);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        Object[] objects3 = doubles.get(2).toArray();
        List<List<Double>> doubles2 = part2(version, tagNames);
        double aa = doubles2.get(0).get(0);
        BigDecimal a = new BigDecimal(aa);
        aa = a.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        double bb = doubles2.get(1).get(0);
        BigDecimal b = new BigDecimal(bb);
        bb = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        double cc = doubles2.get(2).get(0);
        BigDecimal c = new BigDecimal(cc);
        cc = c.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        result.put("part4", aa * 100);
        result.put("part5", bb * 100);
        result.put("part6", cc * 100);


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

        int[] stack = {2, 2, 1};
        int[] ystack = {1, 1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 100, 0, 100, 0, 40, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg2", image1);
    }

    private void dealPart3(String version, String[] tagNames) {
        String[] cBrandCodes = {"1_2_LYJJ_COKE",
                "WGYJJT_COKE",
                "6_7_LYJJ_COKE",
                "4_5_LYJJ_COKE"};
        List<List<Double>> doubles = part3(version, tagNames, cBrandCodes, "ALL", 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        Object[] objects3 = doubles.get(2).toArray();

        for (int i = 0; i < objects3.length; i++) {
            Object o = objects3[i];
            if (Objects.nonNull(o)) {
                Double v = (Double) o;
                objects3[i] = v * 100;
            }
        }

        result.put("part7", objects1[objects1.length - 2]);
        result.put("part8", objects2[objects2.length - 2]);
        result.put("part9", objects3[objects3.length - 2]);

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
        vectors.add(series2);
        vectors.add(series1);
        vectors.add(series3);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String valueAxisLabel1 = null;

        int[] stack = {2, 2, 1};
        int[] ystack = {1, 1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 100, 0, 100, 0, 20, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg3", image1);
    }

    private void dealPart5(String version) {
        String[] cBrandCodes = {"5_SJK_SINTER", "6_SJK_SINTER"};
        /**
         *         <5mm GL5
         *         >40mm GF40
         *         5-10mm
         */
        //转鼓
        String[] lp = {"Drum"};
        //粒度
        String[] lg = {"GS5", "GL40", "G0510"};
        List<List<Double>> lPList = part3(version, lp, cBrandCodes, "ALL", 100);
        List<List<Double>> lGList = part3(version, lg, cBrandCodes, "ALL", 1);


        Object[] objects1 = lPList.get(0).toArray();

        Object[] objects2 = lGList.get(0).toArray();
        Object[] objects3 = lGList.get(1).toArray();
        Object[] objects4 = lGList.get(2).toArray();


        result.put("part10", objects2[objects2.length - 2]);
        result.put("part11", objects4[objects4.length - 2]);

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

        int[] stack = {1, 1, 1, 1};
        int[] ystack = {1, 1, 1, 1};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 8192, 0, 360, 0, 500, 4, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg5", image1);
    }

    private void dealPart6(String version, String[] tagNames) {
        String[] cBrandCodes = {"5_SJK_SINTER", "6_SJK_SINTER"};
        List<List<Double>> doubles = part3(version, tagNames, cBrandCodes, "ALL", 100);
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

        int[] stack = {2, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 50, 65, 0, 15, 0, 1, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg6", image1);
    }

    private void dealPart7(String version, String[] tagNames) {
        String[] cBrandCodes = {"4_ZMHPCM_COAL"};
        List<List<Double>> doubles = part3(version, tagNames, cBrandCodes, "ALL", 100);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        Object[] objects3 = doubles.get(2).toArray();

        result.put("part12", objects1[objects1.length - 2]);
        result.put("part13", objects2[objects2.length - 2]);
        result.put("part14", objects3[objects3.length - 2]);

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
        vectors.add(series3);
        vectors.add(series1);
        vectors.add(series2);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String valueAxisLabel1 = null;

        int[] stack = {2, 1, 1};
        int[] ystack = {2, 1, 1};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 50, 90, 0, 20, 0, 20, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg7", image1);
    }

    private Object[] dealData(Object[] old, int bs) {
        Object[] objects4 = new Object[old.length];
        for (int i = 0; i < old.length; i++) {
            Object o = old[i];
            if (Objects.nonNull(o)) {
                double a = (Double) o;
                o = a * bs;
            }
            objects4[i] = o;
        }
        return objects4;
    }

    private void dealPart8(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        Object[] objects3 = doubles.get(2).toArray();
        Object[] objects4 = dealData(objects2, 1000);

        List<List<Double>> doubles2 = part2(version, tagNames);
        result.put("part15", doubles2.get(0).get(0).intValue());
        result.put("part16", Double.valueOf(doubles2.get(1).get(0) * 1000).intValue());
        result.put("part17", doubles2.get(2).get(0).intValue());

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
        series2.add(new Serie("风压", objects4));

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

        int[] stack = {2, 1, 1};
        int[] ystack = {1, 2, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 5000, 6000, 100, 450, 100, 450, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg8", image1);
    }

    private void dealPart9(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        List<List<Double>> doubles2 = part2(version, tagNames);

        double a = doubles2.get(0).get(0);
        BigDecimal b = new BigDecimal(a);
        a = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        double c = doubles2.get(0).get(0);
        BigDecimal d = new BigDecimal(c);
        c = d.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        result.put("part18", a);
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

        int[] stack = {1, 1};
        int[] ystack = {2, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 1, 1, 15, 0, 180, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg9", image1);
    }

    private void dealPart11(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        List<List<Double>> doubles2 = part2(version, tagNames);
        result.put("part20", doubles2.get(0).get(0).intValue());

        /**
         * 炉芯BF8_L2C_BH_T0146_1d_avg
         * L4x BF8_L2C_TP_StockLineSetL4_1d_avg
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

        int[] stack = {1, 1};
        int[] ystack = {2, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 0, 500, 0, 500, 0, 180, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg11", image1);
    }

    private void dealPart13(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        List<List<Double>> doubles2 = part2(version, tagNames);

        double aDouble = doubles2.get(0).get(0);
        BigDecimal b = new BigDecimal(aDouble);
        aDouble = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        result.put("part21", aDouble);
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

        int[] stack = {1};
        int[] ystack = {2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 40, 55, 0, 1, 0, 180, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg13", image1);
    }

    private void dealPart14(String version) {
        List<Map<String, Object>> maps = part4(version);
        result.put("sheet14", maps);
    }

    private void dealPart15(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        Object[] objects3 = dealData(objects1, 100);
        if (Objects.nonNull(objects2) && objects2.length > 1) {
            result.put("part22", objects2[objects2.length - 2]);
        } else {
            result.put("part22", 0);
        }


//"BF8_L2C_HMTemp_1d_avg" "BF8_L2M_HMTempTargetRatio_1d_cur"
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("PT", objects2));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("Si", objects3));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String valueAxisLabel1 = null;

        int[] stack = {2, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, valueAxisLabel1, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 1300, 1600, 0, 100, 0, 1, tagNames.length, stack, ystack);
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

        //熟料比 R2 料批 未完成
//        /anaChargeValue/range?category=ALL&endtime=1551110400000&granularity=day&starttime=1551024000000&type=LC

        List<Double> part5 = part5(version);
        Double a = part5.get(0).doubleValue() * 100;
        Double b = part5.get(1).doubleValue();
        result.put("cz18", a.intValue() + "%");
        result.put("pc18", (a.intValue() - 80) + "%");

        BigDecimal decimal = new BigDecimal(b);
        BigDecimal subtract1 = decimal.subtract(new BigDecimal(1.25));
        decimal = decimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        subtract1 = subtract1.setScale(2, BigDecimal.ROUND_HALF_UP);
        result.put("cz19", decimal);
        result.put("pc19", subtract1);


        List<List<Double>> doubles2 = part2(version, tagNames);
        for (int i = 0; i < doubles2.size(); i++) {
            Double aDouble = doubles2.get(i).get(0);
            BigDecimal bigDecimal = new BigDecimal(aDouble);
            Double aDouble1 = map.get("cz" + (i + 1));
            BigDecimal bigDecimal1 = new BigDecimal(aDouble1);
            BigDecimal subtract = bigDecimal.subtract(bigDecimal1);
            if (i == 0 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7 || i == 14) {
                bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
                result.put("cz" + (i + 1), bigDecimal.intValue());

                subtract = subtract.setScale(2, BigDecimal.ROUND_HALF_UP);
                result.put("pc" + (i + 1), subtract.intValue());
            }

            if (i == 11 || i == 12 || i == 13 || i == 15 || i == 16) {
                bigDecimal = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP);
                result.put("cz" + (i + 1), bigDecimal);

                subtract = subtract.setScale(1, BigDecimal.ROUND_HALF_UP);
                result.put("pc" + (i + 1), subtract);
            }

            if (i == 1) {
                bigDecimal = bigDecimal.multiply(new BigDecimal(1000)).setScale(2, BigDecimal.ROUND_HALF_UP);
                result.put("cz" + (i + 1), bigDecimal.intValue());

                subtract = bigDecimal1.subtract(bigDecimal);
                result.put("pc" + (i + 1), subtract.intValue());
            }

            if (i == 8 || i == 9 || i == 10) {
                bigDecimal = bigDecimal.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                result.put("cz" + (i + 1), bigDecimal.intValue() + "%");
            }
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

    private JSONArray dataHttp3(Date beginDate, Date endDate, String version) {
        Map<String, String> map = new HashMap<>();
        map.put("starttime", beginDate.getTime() + "");
        map.put("endtime", endDate.getTime() + "");
        map.put("category", "ALL");
        map.put("granularity", "day");
        map.put("type", "LC");
        String results = httpUtil.get(getUrl3(version), map);
        JSONObject jsonObject = JSONObject.parseObject(results);
        JSONArray data = jsonObject.getJSONArray("data");
        return data;
    }

    private void comm(String version, String path) {
        //文档所有日期
        dealDate(result);
        try {
            String sqquence = "8高炉";
            if ("6.0".equals(version)) {
                sqquence = "6高炉";
            }

            XWPFDocument doc = WordExportUtil.exportWord07(path, result);
            String fileName = sqquence + DateUtil.getFormatDateTime(new Date(), "yyyyMMdd") + "操业会议纪要（实施版）.docx";
            String filePath = jobProperties.getFilePath() + File.separator + "doc" + File.separator + fileName;
            FileOutputStream fos = new FileOutputStream(filePath);
            doc.write(fos);
            fos.close();

            ReportIndex reportIndex = new ReportIndex();
            reportIndex.setCreateTime(new Date());
            reportIndex.setUpdateTime(new Date());
            reportIndex.setSequence(sqquence);
            reportIndex.setIndexLang("cn_zh");
            reportIndex.setIndexType("report_day");
            reportIndex.setRecordDate(new Date());
            reportIndex.setName(fileName);
            reportIndex.setReportCategoryCode(JobEnum.gl_caoyehui_day.getCode());
            reportIndex.setPath(filePath);
            reportIndexMapper.insert(reportIndex);
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

    /**
     * 检化验
     *
     * @param version
     * @return
     */
    private String getUrl3(String version) {
        return httpProperties.getGlUrlVersion(version) + "/anaChargeValue/range";
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
