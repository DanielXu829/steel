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
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
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
import java.util.*;
import java.util.List;

@Component
@Slf4j
@SuppressWarnings("ALL")
public class GaoLuDocMain2 {

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private HttpProperties httpProperties;

    @Autowired
    private JobProperties jobProperties;

    @Autowired
    private ReportIndexMapper reportIndexMapper;

    private String version6 = "6.0";
    private String version7 = "7.0";
    private String version8 = "8.0";

    @Scheduled(cron = "0 31 6 * * ?")
    public void mainTask() {
        result = new HashMap<>();
        mainDeal(version6);
        mainDeal(version7);
        mainDeal(version8);
        log.error("高炉word生成完毕！");
    }

    public void mainDeal(String version) {
        String name = "8";
        if ("6.0".equals(version)) {
            name = "6";
            dealTagName6();
        } else if ("7.0".equals(version)) {
            name = "7";
            dealTagName7();
        } else if ("8.0".equals(version)) {
            name = "8";
            dealTagName8();
        }
        dealPart1(version, L1);
        dealPart2(version, L2);
        dealPart3(version, L3);
        dealPart4(version, L4);
        dealPart5(version, L5);
        dealPart6(version, L6);
        dealPart7(version, L7);
//
//        dealPart8(version, L8);
        dealPart9(version, L9);
        dealPart10(version, L10);
        dealPart11(version, L11);// 已完成
        dealPart12(version, L12);
        dealPart13(version, L13);

        if ("6.0".equals(version) || "7.0".equals(version)) {
            dealPart16_2(version, L16);
        } else if ("8.0".equals(version)) {
            dealPart14(version, L14);
            dealPart15(version, L15);
            dealPart16(version, L16);
        }
        dealPart17(version, L17);
        dealPart18(version, L18);
        dealPart19(version, L19);

        dealPart20(version);
        comm(version, jobProperties.getTemplatePath() + File.separator + "doc" + File.separator + name + "高炉操业会议纪要（实施版）.docx");
    }

    /**
     * doc最后结果
     */
    private HashMap<String, Object> result = null;

    private void dealTagName6() {
        L1 = new String[]{"BF6_L2M_BX_HM_confirmWgt_evt", "BF6_L2M_BX_FuelRate_1d_cur"};
        L2 = new String[]{"BF6_L2M_BX_NTCokeRate_1d_cur", "BF6_L2M_BX_CokeRate_1d_cur", "BF6_L2M_BX_CoalRate_1d_cur"};
        L3 = new String[]{"BF6_L2M_SinterRatio_1d_avg", "BF6_L2M_PelletsRatio_1d_avg", "BF6_L2M_LumporeRatio_1d_avg"};
        L4 = new String[]{"M40", "M10"};
        L5 = new String[]{"CSR", "CRI"};
        L6 = new String[]{"Ad", "S"};
        L7 = new String[]{"TFe", "FeO"};
        L8 = new String[]{"BF8_L2C_BD_HotBlastFlow_1d_avg", "BF8_L2C_BD_ColdBlastPress_1d_avg", "BF8_L2C_BD_Pressdiff_1d_avg"};
        L9 = new String[]{"Ad", "S"};

        L10 = new String[]{"BF6_L2C_BD_ActBlastFlow_1d_avg", "BF6_L2C_BD_OxygenFlow_1d_avg"};
        L11 = new String[]{"BF6_L2C_BD_ColdBlastPress_1d_avg", "BF6_L2C_BD_MaxTopPress_1d_avg"};

        L12 = new String[]{"BF6_L2C_BD_W_1d_avg", "BF6_L2C_BD_Z_1d_avg"};
        L13 = new String[]{"BF6_L2M_CCT_1d_avg", "BF6_L2M_PCT_1d_avg"};
        L14 = new String[]{"BF8_L2C_BD_B1B3_HeatLoad_1d_avg", "BF8_L2C_BD_S1S3_HeatLoad_1d_avg"};

        L15 = new String[]{"BF8_L2C_BD_WT6_Q_1d_avg", "BF8_L2C_BD_S4S6_HeatLoad_1d_avg", "BF8_L2C_BD_R1R3_HeatLoad_1d_avg"};

        L16 = new String[]{"BF6_L2C_BD_WT6_Q_1d_avg", "BF6_L2C_BD_GasUtilization_1d_avg"};

        L17 = new String[]{"BF6_L2C_BD_ActBlastFlow_1d_avg", "BF6_L2C_BD_ColdBlastPress_1d_avg", "BF6_L2C_BD_Pressdiff_1d_avg",
                "BF6_L2C_BD_OxygenFlow_1d_avg", "BF6_L2C_BD_HotBlastTemp_1d_avg", "BF6_L2C_BD_BH_1d_avg",
                "BF6_L2C_BD_MaxTopPress_1d_avg", "BF6_L2M_FuelRate_1d_avg", "BF6_L2M_LumporeRatio_1d_avg",
                "BF6_L2M_PelletsRatio_1d_avg", "BF6_L2M_SinterRatio_1d_avg", "",
                "BF6_L2C_BD_CokeLoad_1d_avg", "BF6_L2C_SH_OreBatchWeight", "BF6_L2C_SH_CokeBatchWeight",
                "BF6_L2M_LAB_slagR2_1d_avg", "BF6_L2M_BatchRate_1d_avg", "BF6_L2C_BD_BlastVelocityAct_1d_avg",
                "BF6_L2C_BD_Ek_1d_avg", "BF6_L2C_BD_FlamTemp_1d_avg"
        };

        L18 = new String[]{"BF6_L2C_HMTemp_1d_avg"};
        L19 = new String[]{"BF6_L2C_AnalysisSiValue_1d_avg", "BF6_L2C_AnalysisSValue_1d_avg"};
    }

    private void dealTagName7() {
        L1 = new String[]{"BF7_L2M_BX_HM_confirmWgt_evt", "BF7_L2M_FuelRate_1d_avg"};
        L2 = new String[]{"BF7_L2M_BX_CokeRate_1d_cur", "BF7_L2M_BX_NTCokeRate_1d_cur", "BF7_L2M_BX_CoalRate_1d_cur"};
        L3 = new String[]{"BF7_L2M_SinterRatio_1d_avg", "BF7_L2M_PelletsRatio_1d_avg", "BF7_L2M_LumporeRatio_1d_avg"};
        L4 = new String[]{"M40", "M10"};
        L5 = new String[]{"CSR", "CRI"};
        L6 = new String[]{"Ad", "S"};
        L7 = new String[]{"TFe", "FeO"};
        L8 = new String[]{"BF7_L2C_BD_HotBlastFlow_1d_avg", "BF7_L2C_BD_ColdBlastPress_1d_avg", "BF7_L2C_BD_Pressdiff_1d_avg"};
        L9 = new String[]{"Ad", "S"};

        L10 = new String[]{"BF7_L2C_BD_ColdBlastFlow_1h_avg", "BF7_L2C_BD_OxygenFlow_1d_avg"};
        L11 = new String[]{"BF7_L2C_BD_ColdWindPress2_1d_avg", "BF7_L2C_TP_TopPress_1d_avg"};

        L12 = new String[]{"BF7_L2C_BD_PeripheralAirflowFinger_1d_avg", "BF7_L2C_BD_CentralAirFinger_1d_avg"};
        L13 = new String[]{"BF7_L2C_BD_CCTCenterTemp_1d_avg", "BF7_L2M_PCT_1d_avg"};
        L14 = new String[]{"BF7_L2C_BD_B1B3_HeatLoad_1d_avg", "BF7_L2C_BD_S1S3_HeatLoad_1d_avg"};

        L15 = new String[]{"BF7_L2C_BD_S4S6_HeatLoad_1d_avg", "BF7_L2C_BD_R1R3_HeatLoad_1d_avg"};

        L16 = new String[]{"BF7_L2C_BD_WT6_Q_1d_avg", "BF7_L2C_BD_GasUtilization_1d_avg"};

        L17 = new String[]{"BF7_L2C_BD_ColdBlastFlow_1d_avg", "BF7_L2C_BD_ColdWindPress2_1d_avg", "BF7_L2M_PressDiff_1d_avg",
                "BF7_L2C_BD_OxygenFlow_1d_avg", "BF7_L2C_BD_HotBlastTemp_1d_avg", "BF7_L2C_BD_BH_1d_avg",
                "BF7_L2C_TP_TopPress_1d_avg", "BF7_L2M_BX_FuelRate_1d_cur", "BF7_L2M_LumporeRatio_1d_avg",
                "BF7_L2M_PelletsRatio_1d_avg", "BF7_L2M_SinterRatio_1d_avg", "",
                "BF7_L2C_SH_OCRate_1d_avg", "BF7_L2C_SH_OreBatchWeight", "BF7_L2C_SH_CokeBatchWeight",
                "BF7_L2M_LAB_slagR2_1d_avg", "BF7_L2C_TP_MaBatch_1d_avg", "BF7_L2C_BD_ActualWindSpeed_1d_avg",
                "BF7_L2M_BlastKineticEnergy_1d_avg", "BF7_L2C_BD_FlamTemp_1d_avg"
        };

        L18 = new String[]{"BF7_L2M_HMTemp_1d_avg"};
        L19 = new String[]{"BF7_L2C_AnalysisSiValue_1d_avg", "BF7_L2C_AnalysisSValue_1d_avg"};
    }

    private void dealTagName8() {
        L1 = new String[]{"BF8_L2M_BX_HM_confirmWgt_evt", "BF8_L2M_FuelRate_1d_avg"};
        L2 = new String[]{"BF8_L2M_CokeRate_1d_avg", "BF8_L2M_NTCokeRate_1d_avg", "BF8_L2M_CoalRate_1d_avg"};
        L3 = new String[]{"BF8_L2M_SinterRatio_1d_avg", "BF8_L2M_PelletsRatio_1d_avg", "BF8_L2M_LumporeRatio_1d_avg"};
        L4 = new String[]{"M40", "M10"};
        L5 = new String[]{"CSR", "CRI"};
        L6 = new String[]{"Ad", "S"};
        L7 = new String[]{"TFe", "FeO"};
        L8 = new String[]{"BF8_L2C_BD_HotBlastFlow_1d_avg", "BF8_L2C_BD_ColdBlastPress_1d_avg", "BF8_L2C_BD_Pressdiff_1d_avg"};
        L9 = new String[]{"Ad", "S"};

        L10 = new String[]{"BF8_L2C_BD_HotBlastFlow_1d_avg", "BF8_L2C_BD_OxygenFlow_1d_avg"};
        L11 = new String[]{"BF8_L2C_BD_ColdBlastPress_1d_avg", "BF8_L2C_BD_TopPress2_1d_avg"};

        L12 = new String[]{"BF8_L2C_BD_W_1d_avg", "BF8_L2C_BD_Z_1d_avg"};
        L13 = new String[]{"BF8_L2C_BD_CCT_1d_avg", "BF8_L2M_PCT_1d_avg"};
        L14 = new String[]{"BF8_L2C_BD_B1B3_HeatLoad_1d_avg", "BF8_L2C_BD_S1S3_HeatLoad_1d_avg"};

        L15 = new String[]{"BF8_L2C_BD_S4S6_HeatLoad_1d_avg", "BF8_L2C_BD_R1R3_HeatLoad_1d_avg"};

        L16 = new String[]{"BF8_L2C_BD_WT6_Q_1d_avg", "BF8_L2C_TP_GasUtilization_1d_avg"};

        L17 = new String[]{"BF8_L2C_BD_HotBlastFlow_1d_avg", "BF8_L2C_BD_ColdBlastPress_1d_avg", "BF8_L2C_BD_Pressdiff_1d_avg",
                "BF8_L2C_BD_OxygenFlow_1d_avg", "BF8_L2C_BD_HotBlastTemp_1d_avg", "BF8_L2C_BD_BH_1d_avg",
                "BF8_L2C_BD_TopPress_1d_avg", "BF8_L2M_FuelRate_1d_avg", "BF8_L2M_LumporeRatio_1d_avg",
                "BF8_L2M_PelletsRatio_1d_avg", "BF8_L2M_SinterRatio_1d_avg", "",
                "BF8_L2C_BD_CokeLoad_1d_avg", "BF8_L2C_SH_OreBatchWeight", "BF8_L2C_SH_CokeBatchWeight",
                "BF8_L2M_LAB_slagR2_1d_avg", "BF8_L2M_BatchRate_1d_avg", "BF8_L2C_BD_BlastVelocityAct_1d_avg",
                "BF8_L2C_BD_Ek_1d_avg", "BF8_L2C_BD_FlamTemp_1d_avg"
        };

        L18 = new String[]{"BF8_L2C_HMTemp_1d_avg"};
        L19 = new String[]{"BF8_L2C_AnalysisSiValue_1d_avg", "BF8_L2C_AnalysisSValue_1d_avg"};
    }

    private String[] L1 = new String[]{"BF8_L2C_BD_ProductionSum_1d_cur", "BF8_L2C_BD_CokeRate_1d_avg", "BF8_L2M_FuelRate_1d_avg"};
    private String[] L2 = new String[]{"BF8_L2M_SinterRatio_evt", "BF8_L2M_LumporeRatio_1h_avg", "BF8_L2M_PelletsRatio_1h_avg"};
    private String[] L3 = new String[]{"CSR", "M40", "Ad"};
    private String[] L4 = new String[]{"CSR", "M40", "Ad"};
    private String[] L5 = new String[]{"CSR", "M40", "Ad"};
    private String[] L6 = new String[]{"TFe", "FeO"};
    private String[] L7 = new String[]{"Vdaf", "Ad", "Fcad"};
    private String[] L8 = new String[]{"BF8_L2C_BD_HotBlastFlow_1d_avg", "BF8_L2C_BD_ColdBlastPress_1d_avg", "BF8_L2C_BD_Pressdiff_1d_avg"};
    private String[] L9 = new String[]{"BF8_L2C_BD_W_1d_avg", "BF8_L2C_BD_Z_1d_avg"};
    private String[] L10 = new String[]{"BF8_L2C_BD_CCT_1d_avg", "BF8_L2M_PCT_1d_avg", "BF8_L2C_BD_AvgTopTemp_1d_avg"};
    private String[] L11 = new String[]{"BF8_L2C_BH_T0146_1d_avg", "BF8_L2C_TP_StockLineSetL4_1d_avg"};
    private String[] L12 = new String[]{"BF8_L2C_BD_WT6_Q_1d_avg", "BF8_L2C_BD_B1B3_HeatLoad_1d_avg", "BF8_L2C_BD_S1S3_HeatLoad_1d_avg", "BF8_L2C_BD_S4S6_HeatLoad_1d_avg", "BF8_L2C_BD_R1R3_HeatLoad_1d_avg"};
    private String[] L13 = new String[]{"BF8_L2C_TP_GasUtilization_1d_avg"};
    private String[] L14 = new String[]{"BF8_L2C_TP_GasUtilization_1d_avg"};
    private String[] L15 = new String[]{"BF8_L2C_BD_WT6_Q_1d_avg", "BF8_L2C_BD_S4S6_HeatLoad_1d_avg", "BF8_L2C_BD_R1R3_HeatLoad_1d_avg"};
    private String[] L16 = new String[]{"BF8_L2C_TP_GasUtilization_1d_avg"};

    private String[] L17 = new String[]{"BF8_L2C_BD_HotBlastFlow_1d_avg", "BF8_L2C_BD_ColdBlastPress_1d_avg", "BF8_L2C_BD_Pressdiff_1d_avg", "BF8_L2C_BD_OxygenFlow_1d_avg",
            "BF8_L2C_BD_HotBlastTemp_1d_avg", "BF8_L2C_BD_BH_1d_avg", "BF8_L2C_BD_TopPress_1d_avg", "BF8_L2M_FuelRate_1d_avg", "BF8_L2M_LumporeRatio_1d_avg", "BF8_L2M_PelletsRatio_1d_avg",
            "BF8_L2M_SinterRatio_1d_avg", "BF8_L2C_BD_CokeLoad_1d_avg", "BF8_L2C_SH_OreBatchWeight", "BF8_L2C_SH_CokeBatchWeight",
            "BF8_L2C_BD_BlastVelocityAct_1d_avg", "BF8_L2C_BD_Ek_1d_avg", "BF8_L2C_BD_FlamTemp_1d_avg"
    };
    private String[] L18 = new String[]{"BF8_L2C_HMTemp_1d_avg"};
    private String[] L19 = new String[]{"BF8_L2C_AnalysisSiValue_1d_avg", "BF8_L2C_AnalysisSValue_1d_avg"};

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
                                if (v.compareTo(BigDecimal.ZERO) > 0) {
                                    csr.add(v);
                                    a = a.add(v);
                                }
                            }
                            if (Objects.nonNull(o1)) {
                                BigDecimal v = dealType(o1);
                                if (v.compareTo(BigDecimal.ZERO) > 0) {
                                    m40.add(v);
                                    b = b.add(v);
                                }
                            }
                            if (Objects.nonNull(o2)) {
                                BigDecimal v = dealType(o2);
                                if (v.compareTo(BigDecimal.ZERO) > 0) {
                                    ad.add(v);
                                    c = c.add(v);
                                }
                            }
                            if (Objects.nonNull(o3)) {
                                BigDecimal v = dealType(o3);
                                if (v.compareTo(BigDecimal.ZERO) > 0) {
                                    a4.add(v);
                                    d = d.add(v);
                                }
                            }
                        }
                    }
                }
                if (csr.size() != 0) {
                    a = a.divide(new BigDecimal(csr.size()), 6, BigDecimal.ROUND_HALF_UP);
                }
                a.setScale(6, BigDecimal.ROUND_HALF_UP);
                csrR.add(a.doubleValue());

                if (m40.size() != 0) {
                    b = b.divide(new BigDecimal(m40.size()), 6, BigDecimal.ROUND_HALF_UP);
                }
                b.setScale(6, BigDecimal.ROUND_HALF_UP);
                m40R.add(b.doubleValue());

                if (ad.size() != 0) {
                    c = c.divide(new BigDecimal(ad.size()), 6, BigDecimal.ROUND_HALF_UP);
                }
                c.setScale(6, BigDecimal.ROUND_HALF_UP);
                adR.add(c.doubleValue());

                if (a4.size() != 0) {
                    d = d.divide(new BigDecimal(a4.size()), 6, BigDecimal.ROUND_HALF_UP);
                }
                d.setScale(6, BigDecimal.ROUND_HALF_UP);
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

                if (Objects.nonNull(x) && x.doubleValue() != 0.0) {
                    rd.add(x * scale);
                } else {
                    if (i < dateList.size()) {
                        dateList.remove(i);
                    }
                }
            }
            result.add(rd);
        }

        return result;

    }

    private List<Map<String, Object>> part4(String version) {

        Date date = new Date();
        Date beginDate = DateUtil.addDays(date, -1);

        List<Map<String, Object>> result = new ArrayList<>();
        while (beginDate.before(date)) {
            String dateTime = DateUtil.getFormatDateTime(beginDate, "yyyy-MM-dd 00:00:00");
            String dateTime1 = DateUtil.getFormatDateTime(beginDate, "yyyy-MM-dd 24:00:00");

            Date date1 = DateUtil.strToDate(dateTime, DateUtil.fullFormat);
            Date date2 = DateUtil.strToDate(dateTime1, DateUtil.fullFormat);
            JSONObject data = dataHttp2(date1, date2, version);

            if (Objects.nonNull(data)) {
                Map<String, Object> innerMap = data.getInnerMap();
                Set<String> keys = innerMap.keySet();

                Long[] list = new Long[keys.size()];
                int k = 0;
                for (String key : keys) {
                    list[k] = Long.valueOf(key);
                    k++;
                }
                Arrays.sort(list);


                for (int i = 0; i < list.length; i++) {
                    Map<String, Object> cAngle = new HashMap<>();
                    Map<String, Object> cRound = new HashMap<>();
                    Map<String, Object> oAngle = new HashMap<>();
                    Map<String, Object> oRound = new HashMap<>();

                    int index = 1;
                    int useCIndex = 1;
                    int useOIndex = 1;

                    JSONArray jsonArray = (JSONArray) innerMap.get(list[i] + "");
                    for (int j = 0; j < jsonArray.size(); j++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                        String typ = jsonObject.getString("typ");
                        String angle = jsonObject.getString("angle");
                        String round = jsonObject.getString("round");
                        String formatDateTime = DateUtil.getFormatDateTime(new Date(list[i]), "yyyy-MM-dd HH:mm");

                        if (!"0.0".equals(angle) && !"0.0".equals(round)) {
                            if ("C".equals(typ)) {
                                if (index % 4 == 0) {
                                    cAngle.put("a0", formatDateTime);
                                }
                                cAngle.put("a" + useCIndex, angle);
                                cRound.put("a" + useCIndex, round);
                                cAngle.put("type", typ);
                                cRound.put("a0", "");
                                cRound.put("type", typ);
                                useCIndex++;
                            } else if ("O".equals(typ)) {
                                oAngle.put("a" + useOIndex, angle);
                                oRound.put("a" + useOIndex, round);
                                oAngle.put("a0", "");
                                oAngle.put("type", typ);
                                oRound.put("a0", "");
                                oRound.put("type", typ);
                                useOIndex++;
                            }
                        }
                        index++;
                    }

                    for (int m = useCIndex; m < 12; m++) {
                        cAngle.put("a" + m, "");
                        cRound.put("a" + m, "");
                    }

                    for (int n = useOIndex; n < 12; n++) {
                        oAngle.put("a" + n, "");
                        oRound.put("a" + n, "");
                    }

                    result.add(cAngle);
                    result.add(cRound);
                    result.add(oAngle);
                    result.add(oRound);
                }

            }
            beginDate = DateUtil.addDays(beginDate, 1);
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


    private List<Double> dealList(Object[] objects) {

        List data = new ArrayList();

        double max = 0;
        double min = 0;
        List<Double> useList = new ArrayList<>();
        for (int i = 0; i < objects.length; i++) {
            Object o = objects[i];
            if (Objects.nonNull(o)) {
                double v = (double) objects[i];
                useList.add(v);
            }
        }

        for (int i = 0; i < useList.size(); i++) {
            if (i == 0) {
                max = useList.get(0);
                min = useList.get(0);
            }
            max = useList.get(i) > max ? useList.get(i) : max;
            min = useList.get(i) < min ? useList.get(i) : min;
        }
        if (max == 0) {
            max = 1;
        }
        data.add(max);
        data.add(min);

        return data;
    }

    private void  dealPart1(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects3 = doubles.get(1).toArray();

        result.put("part1", getLastVal(objects1));
        result.put("part3", getLastVal(objects3));

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects3);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        /**
         * 产量
         * 燃料比
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("产量", objects1));

        // 标注类别
        Vector<Serie> series3 = new Vector<Serie>();
        series3.add(new Serie("燃料比", objects3));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series3);

        String title1 = "";
        String categoryAxisLabel1 = "";
        String[] yLabels = {"产量(t)","燃料比(kg/t)"};


        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 300, 600, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg1", image1);
    }

    private void dealPart2(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        Object[] objects = new Object[objects1.length];

        for (int i = 0; i < objects1.length; i++) {
            Object o1 = objects1[i];
            Object o2 = objects2[i];
            Double v1 = null;
            Double v2 = null;
            if (Objects.nonNull(o1) && Objects.nonNull(o2)) {
                v1 = (Double) o1;
                v2 = (Double) o2;
                objects[i] = v1 + v2;
            }

        }

        Object[] objects3 = doubles.get(2).toArray();

        result.put("part2", getLastVal(objects));
        result.put("part4", getLastVal(objects3));


        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("焦比", objects));

        // 标注类别
        Vector<Serie> series3 = new Vector<Serie>();
        series3.add(new Serie("煤比", objects3));

        List<Double> data = dealList(objects);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects3);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series3);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"焦比(kg/t)","煤比(kg/t)"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 300, 600, tagNames.length - 1, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg2", image1);
    }

    private void dealPart3(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 100);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        Object[] objects3 = doubles.get(2).toArray();

        result.put("part5", getLastVal(objects1));
        result.put("part6", getLastVal(objects2));
        result.put("part7", getLastVal(objects3));

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        List<Double> data3 = dealList(objects3);
        Double max3 = data3.get(0) * 1.2;
        Double min3 = data3.get(1) * 0.8;


        /**
         * A烧结矿
         * B块矿
         * P球团
         *
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("A烧结矿", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("P球团", objects2));

        // 标注类别
        Vector<Serie> series3 = new Vector<Serie>();
        series3.add(new Serie("B块矿", objects3));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);
        vectors.add(series3);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"A烧结矿(%)","P球团(%)","B块矿(%)"};

        int[] stack = {1, 1, 1};
        int[] ystack = {1, 2, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, min3, max3, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg3", image1);
    }

    private void dealPart4(String version, String[] tagNames) {
        String[] cBrandCodes = {"1_2_LYJJ_COKE",
                "WGYJJT_COKE",
                "6_7_LYJJ_COKE",
                "4_5_LYJJ_COKE"};
        List<List<Double>> doubles = part3(version, tagNames, cBrandCodes, "ALL", 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        result.put("part8", getLastVal(objects1));
        result.put("part9", getLastVal(objects2));

        /**
         *   M40
         *   M10
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("M40", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("M10", objects2));


        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);


        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"M40","M10"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 10, 15, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg4", image1);
    }

    private void dealPart5(String version, String[] tagNames) {
        String[] cBrandCodes = {"1_2_LYJJ_COKE",
                "WGYJJT_COKE",
                "6_7_LYJJ_COKE",
                "4_5_LYJJ_COKE"};
        List<List<Double>> doubles = part3(version, tagNames, cBrandCodes, "ALL", 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();


//        result.put("part7", objects1[objects1.length - 2]);
//        result.put("part8", objects2[objects2.length - 2]);
        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        /**
         *   CSR
         *   CRI
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("CSR", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("CRI", objects2));


        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        result.put("part10", getLastVal(objects1));
        result.put("part11", getLastVal(objects2));

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"CSR","CRI"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 10, 15, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg5", image1);
    }

    private void dealPart6(String version, String[] tagNames) {
        String[] cBrandCodes = {"1_2_LYJJ_COKE",
                "WGYJJT_COKE",
                "6_7_LYJJ_COKE",
                "4_5_LYJJ_COKE"};
        List<List<Double>> doubles = part3(version, tagNames, cBrandCodes, "ALL", 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();


//        result.put("part7", objects1[objects1.length - 2]);
//        result.put("part8", objects2[objects2.length - 2]);
        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.1;
        Double min2 = data2.get(1) * 0.9;


        /**
         *   Ad
         *   S
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("Ad", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("S", objects2));


        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        result.put("part12", getLastDoubleVal(objects1));
        result.put("part13", getLastDoubleVal(objects2));

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"Ad(%)","S(%)"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 10, 15, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg6", image1);
    }

    private void dealPart7(String version, String[] tagNames) {
        String[] cBrandCodes = {"5_SJK_SINTER", "6_SJK_SINTER"};
        List<List<Double>> doubles = part3(version, tagNames, cBrandCodes, "ALL", 100);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.06;
        Double min1 = data.get(1) * 0.94;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

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

        result.put("part14", getLastVal(objects1));
        result.put("part15", getLastVal(objects2));

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"TFe(%)","FeO(%)"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 1, tagNames.length, stack, ystack);
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
        String[] yLabels = {"风量(m3/min)","风压(kpa)","压差(kpa)"};

        int[] stack = {1, 1, 2};
        int[] ystack = {1, 2, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 5000, 6000, 100, 450, 100, 450, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg8", image1);
    }

    private void dealPart9(String version, String[] tagNames) {
        String[] cBrandCodes = {"4_ZMHPCM_COAL"};
        List<List<Double>> doubles = part3(version, tagNames, cBrandCodes, "ALL", 100);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

//        result.put("part12", objects1[objects1.length - 2]);
//        result.put("part13", objects2[objects2.length - 2]);
//        result.put("part14", objects3[objects3.length - 2]);

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;


        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("Ad", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("S", objects2));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        result.put("part18", getLastDoubleVal(objects1));
        result.put("part19", getLastDoubleVal(objects2));

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"Ad(%)","S(%)"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 1, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg9", image1);
    }

    private void dealPart10(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();


        result.put("part20", getLastVal(objects1));
        result.put("part21", getLastVal(objects2));

        /**
         * 风量
         * 氧量
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("风量", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("氧量", objects2));

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.1;
        Double min1 = data.get(1) * 0.9;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"风量(m3/min)","氧量(m3/h)"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 300, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg10", image1);
    }

    private void dealPart11(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        int xc = 1;
        if ("8.0".equals(version)) {
            xc = 1000;
        }
        objects1 = dealData(objects1, xc);

        result.put("part22", getLastVal(objects1));
        result.put("part23", getLastVal(objects2));

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.055;
        Double min1 = data.get(1) * 0.975;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.055;
        Double min2 = data2.get(1) * 0.975;

        /**
         * 风压
         * 顶压
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("风压", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("顶压", objects2));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"风压(kpa)","顶压(kpa)"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 180, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg11", image1);
    }

    private void dealPart12(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        objects1 = dealData(objects1, 100);
        /**
         * W
         * Z
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

        result.put("part24", getLastVal(objects1));
        result.put("part25", getLastVal(objects2));

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"",""};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 1000, 5000, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg12", image1);
    }

    private void dealPart13(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        /**
         * CCT1
         * W4
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("CCT1", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series2.add(new Serie("W4", objects2));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        result.put("part26", getLastVal(objects1));
        result.put("part27", getLastVal(objects2));

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"",""};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 180, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg13", image1);
    }

    private void dealPart14(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        /**
         * B1-B3
         * S1-S3
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("B1-B3", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series2.add(new Serie("S1-S3", objects2));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        result.put("part28", getLastVal(objects1));
        result.put("part29", getLastVal(objects2));

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"",""};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 180, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg14", image1);
    }

    private void dealPart15(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();


        /**
         * S4-S6
         * R1-R3
         */

        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        series1.add(new Serie("S4-S6", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("R1-R3", objects2));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);


        result.put("part30", getLastVal(objects1));
        result.put("part31", getLastVal(objects2));

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"",""};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 1, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg15", image1);

    }

    private void dealPart16(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        /**
         * 总热负荷
         * 煤气利用率
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("总热负荷", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series2.add(new Serie("煤气利用率", objects2));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        result.put("part32", getLastVal(objects1));
        result.put("part33", getLastVal(objects2));

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.1;
        Double min1 = data.get(1) * 0.9;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.085;
        Double min2 = data2.get(1) * 0.95;

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"",""};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 1, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg16", image1);

    }

    private void dealPart16_2(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        int xc = 1;
        if ("7.0".equals(version)) {
            xc = 100;
        }
        objects2 = dealData(objects2, xc);

        /**总热负荷
         * 煤气利用率
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("总热负荷", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series2.add(new Serie("煤气利用率", objects2));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        result.put("part33", getLastVal(objects1));
        result.put("part37", getLastVal(objects2));

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"",""};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 1, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg16", image1);

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
        map.put("cz12", 80.00);
        map.put("cz13", 4.60);
        map.put("cz14", 90.00);
        map.put("cz15", 20.00);
        map.put("cz16", 1.25);
        map.put("cz17", 144.00);
        map.put("cz18", 240.00);
        map.put("cz19", 115.00);
        map.put("cz20", 2200.00);


        List<Double> part5 = part5(version);
        Double a = 0.0;
        Double b = 0.0;
        if (part5.size() > 0) {
            a = part5.get(0).doubleValue() * 100;
            b = part5.get(1).doubleValue();
        }
        result.put("cz12", a.intValue() + "%");
        result.put("pc12", (a.intValue() - 80) + "%");

        BigDecimal decimal = new BigDecimal(b);
        BigDecimal subtract1 = decimal.subtract(new BigDecimal(1.25));
        decimal = decimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        subtract1 = subtract1.setScale(2, BigDecimal.ROUND_HALF_UP);
        result.put("cz16", decimal);
        result.put("pc16", subtract1);


        List<List<Double>> doubles2 = part2(version, tagNames);
        for (int i = 0; i < doubles2.size(); i++) {
            Double aDouble = doubles2.get(i).get(0);
            BigDecimal bigDecimal = new BigDecimal(aDouble);
            Double aDouble1 = map.get("cz" + (i + 1));
            BigDecimal bigDecimal1 = new BigDecimal(aDouble1);
            BigDecimal subtract = bigDecimal.subtract(bigDecimal1);
            if (i == 0 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7 || i == 17 || i == 19) {
                bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
                result.put("cz" + (i + 1), bigDecimal.intValue());

                subtract = subtract.setScale(2, BigDecimal.ROUND_HALF_UP);
                result.put("pc" + (i + 1), subtract.intValue());
            }

            if (i == 12 || i == 13 || i == 14 || i == 18) {
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

            if (i == 8 || i == 9 || i == 10 || i == 16) {
                bigDecimal = bigDecimal.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                result.put("cz" + (i + 1), bigDecimal.intValue() + "%");

                subtract = bigDecimal1.subtract(bigDecimal);
                result.put("pc" + (i + 1), subtract.intValue() + "%");
            }
        }
    }

    private void dealPart18(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();

        /**
         * PT
         */
        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("PT", objects1));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.1;
        Double min1 = data.get(1) * 0.9;

        result.put("part36", getLastVal(objects1));

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"",""};

        int[] stack = {1};
        int[] ystack = {1};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, 0, 100, 0, 1, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg18", image1);

    }

    private void dealPart19(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        /**
         * Si
         * S
         */

        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("Si", objects1));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie("S", objects2));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.1;
        Double min2 = data2.get(1) * 0.9;

        result.put("part34", getLastVal(objects1));
        result.put("part35", getLastVal(objects2));


        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"",""};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 1, 2, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg17", image1);

    }

    private void dealPart20(String version) {
        List<Map<String, Object>> maps = part4(version);
        result.put("sheet14", maps);
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

    private JSONObject dataHttp2(Date beginDate, Date endDate, String version) {
        JSONObject data = null;
        Map<String, String> map = new HashMap<>();
        map.put("startTime", beginDate.getTime() + "");
        map.put("endTime", endDate.getTime() + "");
        map.put("calcModel", "forward");
        String results = httpUtil.get(getUrl2(version), map);
        if (StringUtils.isNotBlank(results)) {
            JSONObject jsonObject = JSONObject.parseObject(results);
            data = jsonObject.getJSONObject("data");
        }
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
            } else if ("7.0".equals(version)) {
                sqquence = "7高炉";
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

    private Object getLastVal(Object[] objects1) {
        if (Objects.isNull(objects1) || objects1.length == 0) {
            return null;
        }
        Object o = objects1[objects1.length - 2];
        Object v1 = 0.0;
        if (Objects.nonNull(o)) {
            Double v = (Double) o;
            v1 = v.intValue();
        }
        return v1;
    }

    private Object getLastDoubleVal(Object[] objects1) {
        if (Objects.isNull(objects1) || objects1.length == 0) {
            return null;
        }
        Object o = objects1[objects1.length - 2];
        Object v1 = 0.0;
        if (Objects.nonNull(o)) {
            Double v = (Double) o;
            v1 = v.doubleValue();
        }
        return v1;
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
        return httpProperties.getGlUrlVersion(version) + "/burden/round/range";
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            chart.getPlot().setBackgroundAlpha(0.1f);
            chart.getPlot().setNoDataMessage("当前没有有效的数据");
            ChartUtilities.writeChartAsJPEG(baos, chart, 600, 300);
            image.setHeight(350);
            image.setWidth(650);
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
