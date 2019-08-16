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
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
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

    private static String version6 = "6.0";
    private static String version7 = "7.0";
    private static String version8 = "8.0";

    private static Map<String,Map<String,Map<String,String>>> TAG_NAMES = new HashMap<>();
    private static String MUBIAO = "mubiao";
    private static String ZHONGDIAN = "zhongdian";
    private static String ZHATIE = "zhatie";
    private static String ZHATIEFANWEI = "zhatiefanwei";
    private static String CAOYEZHUNQUELV = "caoyezhunquelv";
    private static Map<String,String> caoyefanwei = new HashMap<>();
    private static char[] sanjiaoP = {916,80};
    private static char[] co = {951,67,79};
    private static final String sjPStr = new String(sanjiaoP);
    private static final String coStr = new String(co);

    static{
        // 8高炉
        Map<String,Map<String,String>> tg8 = new HashMap<>();
        Map<String,String> mubiao8 = new HashMap<>();
        mubiao8.put("班料批","BF8_L2C_BT_ChargeCount_8h");
        mubiao8.put("湿度","BF8_L2C_BD_BH_1d_avg");
        mubiao8.put("BV","BF8_L2C_BD_HotBlastFlow_1d_avg");
        mubiao8.put("BP","BF8_L2C_BD_ColdBlastPress_1d_avg");
        mubiao8.put(sjPStr,"BF8_L2C_BD_Pressdiff_1d_avg");
        mubiao8.put("TP","BF8_L2C_BD_TopPress_1d_avg");
        mubiao8.put("PT","BF8_L2C_HMTemp_1d_avg");
        mubiao8.put("FR","BF8_L2M_BX_FuelRate_1d_cur");
        tg8.put(MUBIAO,mubiao8);

        Map<String,String> zhongdian8 = new HashMap<>();
        zhongdian8.put("透气性","BF8_L2C_BD_K_1d_avg");
        zhongdian8.put("Q值","BF8_L2C_BD_WT6_Q_1d_avg");
        zhongdian8.put("CCT2","BF8_L2C_BD_CCT_1d_avg");
        zhongdian8.put("W4","BF8_L2M_PCT_1d_avg");
        zhongdian8.put("料速","BF8_L2M_ChargeRate_1d_avg");
        zhongdian8.put("铁口深度","BF8_L2C_CH_TapDepth");
        zhongdian8.put("PT","BF8_L2C_HMTemp_1d_avg");
        zhongdian8.put(sjPStr,"BF8_L2C_BD_Pressdiff_1d_avg");
        zhongdian8.put(coStr,"BF8_L2C_TP_GasUtilization_1d_avg");
        tg8.put(ZHONGDIAN,zhongdian8);

        Map<String,String> zhatie8 = new HashMap<>();
        zhatie8.put("铁口深度","BF8_L2C_CH_TapDepth");
        zhatie8.put("见渣率","BF8_L2C_CH_SlagPercent");
        zhatie8.put("铁水温度","BF8_L2C_HMTemp_1d_avg");
        zhatie8.put("铁水Si","BF8_L2C_AnalysisSiValue_1d_avg");
        zhatie8.put("铁水S","BF8_L2C_AnalysisSValue_1d_avg");
        zhatie8.put("炉渣碱度","BF8_L2C_AnalysisRValue_1d_avg");
        tg8.put(ZHATIE,zhatie8);

        Map<String,String> zhatiefanwei8 = new HashMap<>();
        zhatiefanwei8.put("铁口深度","3400-3800");
        zhatiefanwei8.put("见渣率","90-100");
        zhatiefanwei8.put("铁水温度","1500-1525");
        zhatiefanwei8.put("铁水Si","0.25-0.55");
        zhatiefanwei8.put("铁水S","0.008-0.02");
        zhatiefanwei8.put("炉渣碱度","1.10-1.20");
        tg8.put(ZHATIEFANWEI,zhatiefanwei8);

        Map<String,String> caoye8 = new HashMap<>();
        caoye8.put("产量","BF8_L2M_BX_HM_confirmWgt_evt");
        caoye8.put("燃料比","BF8_L2M_BX_FuelRate_1d_cur");
        caoye8.put("煤气利用率","BF8_L2C_TP_GasUtilization_1d_avg");
        caoye8.put("压差","BF8_L2C_BD_Pressdiff_1d_avg");
        tg8.put(CAOYEZHUNQUELV,caoye8);

        // 7高炉
        Map<String,Map<String,String>> tg7 = new HashMap<>();
        Map<String,String> mubiao7 = new HashMap<>();
        mubiao7.put("班料批","BF7_L2C_BT_ChargeCount_8h");
        mubiao7.put("湿度","BF7_L2C_BD_BH_1d_avg");
        mubiao7.put("BV","BF7_L2C_BD_ColdBlastFlow_1d_avg");
        mubiao7.put("BP","BF7_L2C_BD_ColdWindPress2_1d_avg");
        mubiao7.put(sjPStr,"BF7_L2M_PressDiff_1d_avg");
        mubiao7.put("TP","BF7_L2C_TP_TopPress_1d_avg");
        mubiao7.put("PT","BF7_L2M_HMTemp_1d_avg");
        mubiao7.put("FR","BF7_L2M_BX_FuelRate_1d_cur");
        tg7.put(MUBIAO,mubiao7);

        Map<String,String> zhongdian7 = new HashMap<>();
        zhongdian7.put("透气性","BF7_L2C_BD_K_1d_avg");
        zhongdian7.put("Q值","BF7_L2C_BD_WT6_Q_1d_avg");
        zhongdian7.put("CCT2","BF7_L2C_BD_CCTCenterTemp_1d_avg");
        zhongdian7.put("W4","BF7_L2M_PCT_1d_avg");
        zhongdian7.put("料速","BF7_L2M_ChargeRate_1d_avg");
        zhongdian7.put("铁口深度","BF7_L2C_CH_TapDepth");
        zhongdian7.put("PT","BF7_L2M_HMTemp_1d_avg");
        zhongdian7.put(sjPStr,"BF7_L2M_PressDiff_1d_avg");
        zhongdian7.put(coStr,"BF7_L2M_GasUtilization_1d_avg");
        tg7.put(ZHONGDIAN,zhongdian7);

        Map<String,String> zhatie7 = new HashMap<>();
        zhatie7.put("铁口深度","BF7_L2C_CH_TapDepth");
        zhatie7.put("见渣率","BF7_L2C_CH_SlagPercent");
        zhatie7.put("铁水温度","BF7_L2M_HMTemp_1d_avg");
        zhatie7.put("铁水Si","BF7_L2C_AnalysisSiValue_1d_avg");
        zhatie7.put("铁水S","BF7_L2C_AnalysisSValue_1d_avg");
        zhatie7.put("炉渣碱度","BF7_L2C_AnalysisRValue_1d_avg");
        tg7.put(ZHATIE,zhatie7);

        Map<String,String> zhatiefanwei7 = new HashMap<>();
        zhatiefanwei7.put("铁口深度","3100-3400");
        zhatiefanwei7.put("见渣率","90-100");
        zhatiefanwei7.put("铁水温度","1490-1520");
        zhatiefanwei7.put("铁水Si","0.25-0.55");
        zhatiefanwei7.put("铁水S","0.008-0.02");
        zhatiefanwei7.put("炉渣碱度","1.15-1.35");
        tg7.put(ZHATIEFANWEI,zhatiefanwei7);

        Map<String,String> caoye7 = new HashMap<>();
        caoye7.put("产量","BF7_L2M_BX_HM_confirmWgt_evt");
        caoye7.put("燃料比","BF7_L2M_BX_FuelRate_1d_cur");
        caoye7.put("煤气利用率","BF7_L2M_GasUtilization_1d_avg");
        caoye7.put("压差","BF7_L2M_PressDiff_1d_avg");
        tg7.put(CAOYEZHUNQUELV,caoye7);

        // 6高炉
        Map<String,Map<String,String>> tg6 = new HashMap<>();
        Map<String,String> mubiao6 = new HashMap<>();
        mubiao6.put("班料批","BF6_L2C_BT_ChargeCount_8h");
        mubiao6.put("湿度","BF6_L2C_BD_BH_1d_avg");
        mubiao6.put("BV","BF6_L2C_BD_ActBlastFlow_1d_avg");
        mubiao6.put("BP","BF6_L2C_BD_ColdBlastPress_1d_avg");
        mubiao6.put(sjPStr,"BF6_L2C_BD_Pressdiff_1d_avg");
        mubiao6.put("TP","BF6_L2C_BD_MaxTopPress_1d_avg");
        mubiao6.put("PT","BF6_L2C_HMTemp_1d_avg");
        mubiao6.put("FR","BF6_L2M_BX_FuelRate_1d_cur");
        tg6.put(MUBIAO,mubiao6);

        Map<String,String> zhongdian6 = new HashMap<>();
        zhongdian6.put("透气性","BF6_L2C_BD_K_1d_avg");
        zhongdian6.put("Q值","BF6_L2C_BD_WT6_Q_1d_avg");
        zhongdian6.put("CCT2","BF6_L2M_CCT_1d_avg");
        zhongdian6.put("W4","BF6_L2M_PCT_1d_avg");
        zhongdian6.put("料速","BF6_L2M_ChargeRate_1d_avg");
        zhongdian6.put("铁口深度","BF6_L2C_CH_TapDepth");
        zhongdian6.put("PT","BF6_L2C_HMTemp_1d_avg");
        zhongdian6.put(sjPStr,"BF6_L2C_BD_Pressdiff_1d_avg");
        zhongdian6.put(coStr,"BF6_L2C_BD_GasUtilization_1d_avg");
        tg6.put(ZHONGDIAN,zhongdian6);

        Map<String,String> zhatie6 = new HashMap<>();
        zhatie6.put("铁口深度","BF6_L2C_CH_TapDepth");
        zhatie6.put("见渣率","BF6_L2C_CH_SlagPercent");
        zhatie6.put("铁水温度","BF6_L2C_HMTemp_1d_avg");
        zhatie6.put("铁水Si","BF6_L2C_AnalysisSiValue_1d_avg");
        zhatie6.put("铁水S","BF6_L2C_AnalysisSValue_1d_avg");
        zhatie6.put("炉渣碱度","BF6_L2C_AnalysisRValue_1d_avg");
        tg6.put(ZHATIE,zhatie6);

        Map<String,String> zhatiefanwei6 = new HashMap<>();
        zhatiefanwei6.put("铁口深度","2700-2900");
        zhatiefanwei6.put("见渣率","85-100");
        zhatiefanwei6.put("铁水温度","1460-1490");
        zhatiefanwei6.put("铁水Si","0.25-0.55");
        zhatiefanwei6.put("铁水S","0.015-0.03");
        zhatiefanwei6.put("炉渣碱度","1.10-1.30");
        tg6.put(ZHATIEFANWEI,zhatiefanwei6);

        Map<String,String> caoye6 = new HashMap<>();
        caoye6.put("产量","BF6_L2M_BX_HM_confirmWgt_evt");
        caoye6.put("燃料比","BF6_L2M_BX_FuelRate_1d_cur");
        caoye6.put("煤气利用率","BF6_L2C_BD_GasUtilization_1d_avg");
        caoye6.put("压差","BF6_L2C_BD_Pressdiff_1d_avg");
        tg6.put(CAOYEZHUNQUELV,caoye6);

        TAG_NAMES.put(version8,tg8);
        TAG_NAMES.put(version7,tg7);
        TAG_NAMES.put(version6,tg6);
    }

    @Scheduled(cron = "0 31 6 * * ?")
    public void mainTask() {
        result = new HashMap<>();
        initDateTime();
        mainDeal(version6);
        mainDeal(version7);
        mainDeal(version8);
        log.error("高炉word生成完毕！");
    }

    public void mainDeal(String version) {
        caoyefanwei.clear();
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
        dealPart456(version, L4);
        dealPart7(version, L7);
        dealPart9(version, L9);
        dealPart10(version, L10);
        dealPart11(version, L11);
        dealPart12(version, L12);
        dealPart13(version, L13);

        if ("6.0".equals(version) || "7.0".equals(version)) {
            dealPart16_2(version, L16);
        } else if ("8.0".equals(version)) {
            dealPart16(version, L16);
        }
        dealPart18(version, L18);
        dealPart19(version, L19);

        dealPart20(version);
        dealCaoZuoFangZhen(version);
        dealZhaTie(version);
        dealCaoYeZhunQueLv(version);
        dealTodayCaoZuoFangZhen(version);

        comm(version, jobProperties.getTemplatePath() + File.separator + "doc" + File.separator + name + "高炉操业会议纪要（实施版）.docx");
    }

    /**
     * doc最后结果
     */
    private HashMap<String, Object> result = null;

    private void dealTagName6() {
        L1 = new String[]{"BF6_L2M_BX_HM_confirmWgt_evt", "BF6_L2C_MES_CON_FR_1d_avg"};
        L2 = new String[]{"BF6_L2C_MES_CR_1d_avg", "BF6_L2C_MES_CON_PCI_1d_avg"};
        L3 = new String[]{"BF6_L2M_SinterRatio_1d_avg", "BF6_L2M_PelletsRatio_1d_avg", "BF6_L2M_LumporeRatio_1d_avg"};
        L4 = new String[]{"M40", "M10", "CSR", "CRI", "Ad", "S"};
        L7 = new String[]{"TFe", "FeO"};
        L9 = new String[]{"Ad", "S"};
        L10 = new String[]{"BF6_L2C_BD_ActBlastFlow_1d_avg", "BF6_L2C_BD_OxygenFlow_1d_avg"};
        L11 = new String[]{"BF6_L2C_BD_ColdBlastPress_1d_avg", "BF6_L2C_BD_MaxTopPress_1d_avg"};
        L12 = new String[]{"BF6_L2C_BD_W_1d_avg", "BF6_L2C_BD_Z_1d_avg"};
        L13 = new String[]{"BF6_L2M_CCT_1d_avg", "BF6_L2M_PCT_1d_avg"};
        L16 = new String[]{"BF6_L2C_BD_WT6_Q_1d_avg", "BF6_L2C_BD_GasUtilization_1d_avg"};
        L18 = new String[]{"BF6_L2M_HMTemp_1d_avg"};
        L19 = new String[]{"BF6_L2C_AnalysisSiValue_1d_avg", "BF6_L2C_AnalysisSValue_1d_avg"};
    }

    private void dealTagName7() {
        L1 = new String[]{"BF7_L2M_BX_HM_confirmWgt_evt", "BF7_L2C_MES_CON_FR_1d_avg"};
        L2 = new String[]{"BF7_L2C_MES_CR_1d_avg", "BF7_L2C_MES_CON_PCI_1d_avg"};
        L3 = new String[]{"BF7_L2M_SinterRatio_1d_avg", "BF7_L2M_PelletsRatio_1d_avg", "BF7_L2M_LumporeRatio_1d_avg"};
        L4 = new String[]{"M40", "M10", "CSR", "CRI", "Ad", "S"};
        L7 = new String[]{"TFe", "FeO"};
        L9 = new String[]{"Ad", "S"};
        L10 = new String[]{"BF7_L2C_BD_ColdBlastFlow_1h_avg", "BF7_L2C_BD_OxygenFlow1_1d_avg"};
        L11 = new String[]{"BF7_L2C_BD_ColdWindPress2_1d_avg", "BF7_L2C_TP_TopPress_1d_avg"};
        L12 = new String[]{"BF7_L2C_BD_PeripheralAirflowFinger_1d_avg", "BF7_L2C_BD_CentralAirFinger_1d_avg"};
        L13 = new String[]{"BF7_L2C_BD_CCTCenterTemp_1d_avg", "BF7_L2M_PCT_1d_avg"};
        L16 = new String[]{"BF7_L2C_BD_WT6_Q_1d_avg", "BF7_L2M_GasUtilization_1d_avg"};
        L18 = new String[]{"BF7_L2M_HMTemp_1d_avg"};
        L19 = new String[]{"BF7_L2C_AnalysisSiValue_1d_avg", "BF7_L2C_AnalysisSValue_1d_avg"};
    }

    private void dealTagName8() {
        L1 = new String[]{"BF8_L2M_BX_HM_confirmWgt_evt", "BF8_L2C_MES_CON_FR_1d_avg"};
        L2 = new String[]{"BF8_L2C_MES_CR_1d_avg", "BF8_L2C_MES_CON_PCI_1d_avg"};
        L3 = new String[]{"BF8_L2M_SinterRatio_1d_avg", "BF8_L2M_PelletsRatio_1d_avg", "BF8_L2M_LumporeRatio_1d_avg"};
        L4 = new String[]{"M40", "M10", "CSR", "CRI", "Ad", "S"};
        L7 = new String[]{"TFe", "FeO"};
        L9 = new String[]{"Ad", "S"};
        L10 = new String[]{"BF8_L2C_BD_HotBlastFlow_1d_avg", "BF8_L2C_BD_OxygenFlow_1d_avg"};
        L11 = new String[]{"BF8_L2C_BD_ColdBlastPress_1d_avg", "BF8_L2C_BD_TopPress_1d_avg"};
        L12 = new String[]{"BF8_L2C_BD_W_1d_avg", "BF8_L2C_BD_Z_1d_avg"};
        L13 = new String[]{"BF8_L2C_BD_CCT_1d_avg", "BF8_L2M_PCT_1d_avg"};
        L16 = new String[]{"BF8_L2C_BD_WT6_Q_1d_avg", "BF8_L2C_TP_GasUtilization_1d_avg"};
        L18 = new String[]{"BF8_L2M_HMTemp_1d_avg"};
        L19 = new String[]{"BF8_L2C_AnalysisSiValue_1d_avg", "BF8_L2C_AnalysisSValue_1d_avg"};
    }

    /**
     * 1：产量
     * 2：燃料比
     */
    private String[] L1 = null;
    /**
     * 1：焦比
     * 2：煤比
     */
    private String[] L2 = null;
    private String[] L3 = null;
    private String[] L4 = null;
    private String[] L7 = null;
    private String[] L9 = null;
    private String[] L10 = null;
    private String[] L11 = null;
    private String[] L12 = null;
    private String[] L13 = null;
    private String[] L16 = null;
    private String[] L18 = null;
    private String[] L19 = null;

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
    List<String> longTimeList = new ArrayList<>();
    private Date startTime = null;
    private Date endTime = null;


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
        // 今日零点，作为结束时间点
        Date endDate = cal.getTime();
        // 前推30天，作为开始时间点
        cal.add(Calendar.DAY_OF_MONTH,-30);
        Date startDate = cal.getTime();

        startTime = startDate;
        endTime = endDate;

        // 轮询
        while (startDate.before(endDate)) {
            // 拼接x坐标轴
            categoriesList.add(DateUtil.getFormatDateTime(startDate, "MM月dd日"));
            dateList.add(DateUtil.getFormatDateTime(startDate, DateUtil.yyyyMMddFormat));
            longTimeList.add(startDate.getTime()+"");

            // 递增日期
            cal.add(Calendar.DAY_OF_MONTH, 1);
            startDate = cal.getTime();
        }
    }

    private List<List<Double>> part1(String version, String[] tagNames, int scale) {
        List<List<Double>> doubles = new ArrayList<>();

        JSONObject jsonObject = dataHttp(tagNames, startTime, endTime, version);
        if (Objects.nonNull(jsonObject)) {
            for (String tagName : tagNames) {
                List<Double> vals = new ArrayList<>();
                JSONObject tagObject = jsonObject.getJSONObject(tagName);
                if (null != tagObject) {
                    Map<String, Object> innerMap = tagObject.getInnerMap();
                    for (String time : longTimeList) {
                        Double val = null;
                        BigDecimal big = (BigDecimal) innerMap.get(time);
                        if(null != big){
                            val = big.doubleValue();
                            if(val <0){
                                val = 0.0;
                            }
                        }
                        vals.add(val);
                    }
                }
                doubles.add(vals);
            }
        }
        return doubles;
    }

    private Map<String, Double> partTagValueLatest(String version, String className, Map<String,Integer> decimalMap) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,30);
        cal.set(Calendar.SECOND,0);
        date = cal.getTime();

        Map<String,String> tagNames = TAG_NAMES.get(version).get(className);
        Map<String, Double> vals = new HashMap<>();
        for (Map.Entry<String, String> kv : tagNames.entrySet()) {
            JSONObject jsonObject = dataHttp(kv.getValue(), date, version);
            if (Objects.nonNull(jsonObject)) {
                Double val = jsonObject.getDouble("val");
                int decimal = 1;
                if(null != decimalMap){
                    decimal = decimalMap.getOrDefault(kv.getKey(),1);
                }
                val = decimalDouble(val,decimal);
                vals.put(kv.getKey(), val);
            }
        }
        return vals;
    }

    private List<List<Double>> partJHY(String version, String[] tagNames, String type, int[] scales) {
        List<List<Double>> doubles = new ArrayList<>();

        Date startDate = startTime;
        Date endDate = endTime;

        // 上料记录
        List<ShangLiaoDTO> sl = new ArrayList<>();

        // 是否只用了一种物料
        boolean isOnlyOne = false;
        List<String> brandCodes = dataHttpBrandCode(version,startDate,endDate,"COKE");
        if(brandCodes.size() == 1){
            isOnlyOne = true;
            sl.add(new ShangLiaoDTO(brandCodes.get(0),startDate,endDate));
        }

        // 轮询
        Date index = startDate;
        Calendar cal = Calendar.getInstance();
        cal.setTime(index);
        String brandCode = null;
        while (index.before(endDate)) {
            // 递增日期
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date next = cal.getTime();

            if(!isOnlyOne){ // 上了多种料
                // 轮询每天的焦炭种类
                List<String> todayBrandCodes = dataHttpBrandCode(version,index,next,"COKE");
                String nextBrandCode = todayBrandCodes.get(todayBrandCodes.size()-1);
                if(null == brandCode){ // 第一天的物料代码
                    brandCode = nextBrandCode;
                }else if(!brandCode.equals(nextBrandCode)){ // 说明换料了
                    sl.add(new ShangLiaoDTO(brandCode,startDate,next));
                    startDate = next;
                    brandCode = null;
                }
            }
            index = next;
        }
        if(!isOnlyOne){
            sl.add(new ShangLiaoDTO(brandCode,startDate,endDate));
        }

        // <tagName,<日期，具体值list>>
        Map<String,Map<String,List<Double>>> tagMap = new HashMap<>();
        for (String tagName : tagNames) {
            tagMap.put(tagName,new HashMap<>());
        }

        // 开始查询
        for (ShangLiaoDTO slDTO : sl) {
            JSONArray jsonArray = dataHttp1(slDTO.getBrandCode(), type, slDTO.getStartDate(), slDTO.getEndDate(), version);

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (null != jsonObject) {
                    JSONObject analysis = jsonObject.getJSONObject("analysis");
                    long sampletime = analysis.getLong("sampletime");
                    Date dd = new Date(sampletime);
                    String sampleDate = DateUtil.getFormatDateTime(dd, DateUtil.yyyyMMddFormat);
                    JSONObject values = jsonObject.getJSONObject("values");
                    Map<String, Object> innerMap = values.getInnerMap();
                    for (String tagName : tagNames) {
                        if(innerMap.containsKey(tagName)){
                            Double val = null;
                            Object v = innerMap.get(tagName);
                            if(v instanceof BigDecimal){
                                val = ((BigDecimal)v).doubleValue();
                            }else if(v instanceof Integer){
                                val = ((Integer)v).doubleValue();
                            }

                            if(null != val){
                                Map<String,List<Double>> valMap = tagMap.get(tagName);
                                List<Double> valList = valMap.get(sampleDate);
                                if(null == valList){
                                    valList = new ArrayList<>();
                                    valMap.put(sampleDate,valList);
                                }
                                valList.add(val);
                            }
                        }
                    }
                }
            }
        }

        // 计算最终值
        int i = 0;
        for (String tagName : tagNames) {
            List<Double> list = new ArrayList<>();
            Map<String,List<Double>> valMap = tagMap.get(tagName);
            for (String date : dateList) {
                List<Double> valList = valMap.get(date);
                if((null == valList)||valList.isEmpty()){
                    list.add(0.0);
                    continue;
                }
                Double sum = 0.0;
                for (Double val : valList) {
                    sum += val;
                }
                list.add(sum*scales[i]/valList.size());
            }
            doubles.add(list);
            i++;
        }
        return doubles;
    }

    private List<List<Double>> part3(String version, String[] tagNames, String[] cBrandCodes, String type, int scale) {
        List<String> dateListTmp = new ArrayList<>();
        dateListTmp.addAll(dateList);

        List<List<Double>> doubles = new ArrayList<>();
        for (String cBrandCode : cBrandCodes) {
            List<Double> csrR = new ArrayList<>();
            List<Double> m40R = new ArrayList<>();
            List<Double> adR = new ArrayList<>();
            List<Double> a4R = new ArrayList<>();

            JSONArray jsonArray = dataHttp1(cBrandCode, type, startTime, endTime, version);
            for (String da : dateListTmp) {
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
//                    if (i < dateListTmp.size()) {
//                        dateListTmp.remove(i);
//                    }
                }
            }
            result.add(rd);
        }
        return result;
    }

    private static final int MINUS = 60*1000;

    private List<Map<String, Object>> part4(String version) {
        List<Map<String, Object>> result = new ArrayList<>();

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        // 今天零点
        Date dayEnd = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH,-1);
        // 昨天零点
        Date daySatrt = cal.getTime();

        // 报表code码
        String code = "";
        switch (version){
            case "6.0":
                code = JobEnum.gl_peiliaodan6.getCode();
                break;
            case "7.0":
                code = JobEnum.gl_peiliaodan7.getCode();
                break;
            case "8.0":
                code = JobEnum.gl_peiliaodan.getCode();
                break;
        }
        // 昨天的配料单
        Long startTime = daySatrt.getTime()/1000;
        Long endTime = dayEnd.getTime()/1000;
        List<ReportIndex> reportList = reportIndexMapper.queryReport(code,startTime,endTime);
        for (ReportIndex report : reportList) {
            // 配料单的生成时间往前推，最近一条布料详情
            JSONArray arr = getLastRound(report.getCreateTime(), version);
            if ((null != arr)&&(arr.size() != 0)) {
                // C的角度和圈数
                Map<String, Object> cAngle = new HashMap<>();
                Map<String, Object> cRound = new HashMap<>();
                // O的角度和圈数
                Map<String, Object> oAngle = new HashMap<>();
                Map<String, Object> oRound = new HashMap<>();
                // 默认值填充
                String formatDateTime = DateUtil.getFormatDateTime(report.getCreateTime(), "yyyy-MM-dd HH:mm");
                cAngle.put("a0", formatDateTime);
                cRound.put("a0", "");
                oAngle.put("a0", "");
                oRound.put("a0", "");
                cAngle.put("type", "C");
                cRound.put("type", "C");
                oAngle.put("type", "O");
                oRound.put("type", "O");
                // c有值的索引值
                int c = 1;
                // o有值的索引值
                int o = 1;
                for (int i = 0; i<arr.size() ;i++) {
                    JSONObject jsonObject = arr.getJSONObject(i);
                    // 圈数为0的角度，不显示
                    Double round = jsonObject.getDouble("round");
                    if(round == 0){
                        continue;
                    }

                    String typ = jsonObject.getString("typ");
                    Double angle = jsonObject.getDouble("angle");
                    if ("C".equals(typ)) {
                        cAngle.put("a"+c, angle);
                        cRound.put("a"+c, round);
                        c++;
                    } else if ("O".equals(typ)) {
                        oAngle.put("a"+o, angle);
                        oRound.put("a"+o, round);
                        o++;
                    }
                }
                result.add(cAngle);
                result.add(cRound);
                result.add(oAngle);
                result.add(oRound);
            }
        }
        return result;
    }

    private Double decimalDouble(Double v, Integer decimal){
        if(null == v){
            return null;
        }
        if(null == decimal){
            return v;
        }
        int scale = 1;
        int m = decimal;
        for(;m>0;m--){
            scale *= 10;
        }
        return (double) Math.round(v * scale) / scale;
    }

    private void partCaoZuoFangZhen(String version) {
        List<Map<String, Object>> sheet15 = new ArrayList<>();
        List<Map<String, Object>> sheet16 = new ArrayList<>();

        //填充实际值
        Map<String, Double> mubiao = partTagValueLatest(version,MUBIAO,null);
        Map<String, Double> zhongdian = partTagValueLatest(version,ZHONGDIAN,null);

        // 填充前半部分，并计算偏差
        JSONObject data = dataHttpCaoZuoFangZhen(version);
        if (Objects.nonNull(data)) {
            JSONArray goalParameters = data.getJSONArray("goalParameters");
            if (Objects.nonNull(goalParameters) && goalParameters.size() > 0) {
                for (int i = 0; i < goalParameters.size(); i++) {
                    JSONObject jsonObject = goalParameters.getJSONObject(i);
                    if (Objects.nonNull(jsonObject)) {
                        Map<String, Object> tmp = new HashMap<>();
                        // 参数
                        String goalParaName = jsonObject.getString("goalParaName");
                        // 目标值
                        String goalParaValue = jsonObject.getString("goalParaValue");
                        if("FR".equals(goalParaName)){
                            caoyefanwei.put("燃料比fw",goalParaValue);
                        } else if(sjPStr.equals(goalParaName)){
                            caoyefanwei.put("压差fw",goalParaValue);
                        }
                        //--、-、±、<、
                        Double[] vals = new Double[2];
                        if(goalParaValue.contains("--")){
                            String[] strs = goalParaValue.split("--");
                            for (int j=0; j<2; j++) {
                                try{
                                    vals[j] = Double.valueOf(strs[j]);
                                }catch (Exception e){
                                    vals[j] = 0.0;
                                }
                            }
                        } else if(goalParaValue.contains("-")){
                            String[] strs = goalParaValue.split("-");
                            for (int j=0; j<2; j++) {
                                try{
                                    vals[j] = Double.valueOf(strs[j]);
                                }catch (Exception e){
                                    vals[j] = 0.0;
                                }
                            }
                        } else if(goalParaValue.contains("±")){
                            String[] strs = goalParaValue.split("±");
                            for (int j=0; j<2; j++) {
                                try{
                                    vals[j] = Double.valueOf(strs[j]);
                                }catch (Exception e){
                                    vals[j] = 0.0;
                                }
                            }
                            double min = vals[0]-vals[1];
                            double max = vals[0]+vals[1];
                            vals[0] = min;
                            vals[1] = max;
                        } else if(goalParaValue.contains("<")){
                            String[] strs = goalParaValue.split("<");
                            try{
                                vals[1] = Double.valueOf(strs[1]);
                            }catch (Exception e){
                                vals[1] = 0.0;
                            }
                            vals[0] = vals[1];
                        }else{
                            try{
                                vals[1] = Double.valueOf(goalParaValue);
                            }catch (Exception e){
                                vals[1] = 0.0;
                            }
                            vals[0] = vals[1];
                        }
                        // 实际值
                        Double realValue = mubiao.get(goalParaName);
                        if("8.0".equals(version)&&"BP".equals(goalParaName)){
                            realValue *= 100;
                        }
                        // 偏差
                        Double pc = null;

                        if(null != realValue){
                            double min = vals[0];
                            double max = vals[1];
                            if(realValue>max){
                                pc = realValue-max;
                            } else if(realValue<min){
                                pc = realValue-min;
                            }else{
                                pc = 0.0;
                            }
                        }
                        tmp.put("goalParaName", goalParaName);
                        tmp.put("goalParaValue", goalParaValue);
                        tmp.put("realValue", realValue);
                        pc = decimalDouble(pc,3);
                        tmp.put("pc", pc);

                        sheet15.add(tmp);
                    }
                }
            }

            JSONArray importantParameters = data.getJSONArray("importantParameters");
            if (Objects.nonNull(importantParameters) && importantParameters.size() > 0) {
                for (int i = 0; i < importantParameters.size(); i++) {
                    JSONObject jsonObject = importantParameters.getJSONObject(i);
                    if (Objects.nonNull(jsonObject)) {
                        Map<String, Object> tmp = new HashMap<>();
                        // 参数
                        String importantParaName = jsonObject.getString("importantParaName");
                        // 下限
                        Double textMin = jsonObject.getDouble("textMin");
                        // 上限
                        Double textMax = jsonObject.getDouble("textMax");
                        if(coStr.equals(importantParaName)){
                            String fw = "";
                            if((null != textMin)&&(null != textMax)){
                                fw = textMin+"-"+textMax;
                            }else{
                                if(null != textMin){
                                    fw = textMin+"";
                                }else{
                                    fw = textMax+"";
                                }
                            }
                            caoyefanwei.put("煤气利用率fw",fw);
                        }
                        // 实际值
                        Double realValue = zhongdian.get(importantParaName);
                        // 偏差
                        Double pc = null;

                        if((null == textMin)&&(null != textMax)){
                            textMin = textMax;
                        }
                        if((null == textMax)&&(null != textMin)){
                            textMax = textMin;
                        }

                        if((null != textMin)&&(null != textMax)&&(null != realValue)){
                            double min = textMin;
                            double max = textMax;
                            if(realValue>max){
                                pc = realValue-max;
                            } else if(realValue<min){
                                pc = realValue-min;
                            }else{
                                pc = 0.0;
                            }
                        }
                        tmp.put("importantParaName", importantParaName);
                        tmp.put("textMin", textMin);
                        tmp.put("textMax", textMax);
                        tmp.put("realValue", realValue);
                        pc = decimalDouble(pc,3);
                        tmp.put("pc", pc);
                        sheet16.add(tmp);
                    }
                }
            }

            JSONObject tagetParameters = data.getJSONObject("tagetParameters");
            if(null != tagetParameters){
                String remark = tagetParameters.getString("remarks");
                remark = remark.replaceAll("</p>","")
                        .replaceAll("<p>","")
                        .replaceAll("</ol>","")
                        .replaceAll("<ol>","")
                        .replaceAll("</li>","")
                        .replaceAll("<li>","");
                result.put("caoyeRemark", remark);
            }
        }


        result.put("sheet15", sheet15);
        result.put("sheet16", sheet16);
    }

    private void partZhaTie(String version) {
        List<Map<String, Object>> sheet17 = new ArrayList<>();
        Map<String,Integer> decimalMap = new HashMap<>();
        decimalMap.put("铁水S",3);
        decimalMap.put("铁水Si",2);
        Map<String, Double> zhatie = partTagValueLatest(version,ZHATIE,decimalMap);
        Map<String,String> zhatiefanwei = TAG_NAMES.get(version).get(ZHATIEFANWEI);
        for (Map.Entry<String, String> kv : zhatiefanwei.entrySet()) {
            Map<String, Object> tmp = new HashMap<>();
            // 参数
            String cs = kv.getKey();
            // 目标值
            String mbz = kv.getValue();
            //-
            Double[] vals = new Double[2];
            if(mbz.contains("-")){
                String[] strs = mbz.split("-");
                for (int j=0; j<2; j++) {
                    try{
                        vals[j] = Double.valueOf(strs[j]);
                    }catch (Exception e){
                        vals[j] = 0.0;
                    }
                }
            }
            // 实际值
            Double realValue = zhatie.get(cs);
            // 偏差
            Double pc = null;

            if(null != realValue){
                double min = vals[0];
                double max = vals[1];
                if(realValue>max){
                    pc = realValue-max;
                } else if(realValue<min){
                    pc = realValue-min;
                }else{
                    pc = 0.0;
                }
            }
            tmp.put("cs", cs);
            tmp.put("mbz", mbz);
            tmp.put("realValue", realValue);
            pc = decimalDouble(pc,3);
            tmp.put("pc", pc);
            sheet17.add(tmp);
        }
        result.put("sheet17", sheet17);
    }

    private void partCaoYeZhunQueLv(String version) {
        //填充实际值
        Map<String, Double> caoye = partTagValueLatest(version,CAOYEZHUNQUELV,null);
        result.putAll(caoyefanwei);
        result.putAll(caoye);
    }

    private void partTodayCaoZuoFangZhen(String version) {
        List<Map<String, Object>> sheet18 = new ArrayList<>();
        List<Map<String, Object>> sheet19 = new ArrayList<>();

        JSONObject data = dataHttpCaoZuoFangZhen(version);
        if (Objects.nonNull(data)) {
            JSONArray goalParameters = data.getJSONArray("goalParameters");
            if (Objects.nonNull(goalParameters) && goalParameters.size() > 0) {
                for (int i = 0; i < goalParameters.size(); i++) {
                    JSONObject jsonObject = goalParameters.getJSONObject(i);
                    if (Objects.nonNull(jsonObject)) {
                        Map<String, Object> tmp = new HashMap<>();
                        // 参数
                        String goalParaName = jsonObject.getString("goalParaName");
                        // 单位
                        String goalCompany = jsonObject.getString("goalCompany");
                        // 目标值
                        String goalParaValue = jsonObject.getString("goalParaValue");
                        tmp.put("goalParaName", goalParaName);
                        tmp.put("goalCompany", goalCompany);
                        tmp.put("goalParaValue", goalParaValue);
                        sheet18.add(tmp);
                    }
                }
            }

            JSONArray importantParameters = data.getJSONArray("importantParameters");
            if (Objects.nonNull(importantParameters) && importantParameters.size() > 0) {
                for (int i = 0; i < importantParameters.size(); i++) {
                    JSONObject jsonObject = importantParameters.getJSONObject(i);
                    if (Objects.nonNull(jsonObject)) {
                        Map<String, Object> tmp = new HashMap<>();
                        // 参数
                        Object importantParaName = jsonObject.get("importantParaName");
                        // 下限
                        Object textMin = jsonObject.get("textMin");
                        // 上限
                        Object textMax = jsonObject.get("textMax");
                        // 动作量
                        Object actionVolume = jsonObject.get("actionVolume");
                        tmp.put("importantParaName", importantParaName);
                        tmp.put("textMin", textMin);
                        tmp.put("textMax", textMax);
                        tmp.put("ttt", actionVolume);
                        sheet19.add(tmp);
                    }
                }
            }
        }

        result.put("sheet18", sheet18);
        result.put("sheet19", sheet19);
    }

    /**
     * 提取最小最大值
     * @param objects
     * @return
     */
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

        switch (version){
            case "6.0":
                min1 = 0.0;
                max1 = 5000.0;
                min2 = 300.0;
                max2 = 800.0;
                break;
            case "7.0":
                min1 = 0.0;
                max1 = 9000.0;
                min2 = 350.0;
                max2 = 800.0;
                break;
            case "8.0":
                min1 = 0.0;
                max1 = 10000.0;
                min2 = 400.0;
                max2 = 600.0;
                break;
            default:
        }

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
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg1", image1);
    }

    private void dealPart2(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects3 = doubles.get(1).toArray();

        result.put("part2", getLastVal(objects1));
        result.put("part4", getLastVal(objects3));


        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("焦比", objects1));

        // 标注类别
        Vector<Serie> series3 = new Vector<Serie>();
        series3.add(new Serie("煤比", objects3));

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects3);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        switch (version){
            case "6.0":
                min1 = 200.0;
                max1 = 650.0;
                min2 = 100.0;
                max2 = 190.0;
                break;
            case "7.0":
                min1 = 200.0;
                max1 = 470.0;
                min2 = 100.0;
                max2 = 190.0;
                break;
            case "8.0":
                min1 = 200.0;
                max1 = 470.0;
                min2 = 100.0;
                max2 = 190.0;
                break;
            default:
        }

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
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg2", image1);
    }

    private void dealPart3(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 100);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        Object[] objects3 = doubles.get(2).toArray();

        objects1 = getDoubleVal(objects1,null,2);
        objects2 = getDoubleVal(objects2,null,2);
        objects3 = getDoubleVal(objects3,null,2);

        result.put("part5", getLastDoubleVal(objects1));
        result.put("part6", getLastDoubleVal(objects3));
        result.put("part7", getLastDoubleVal(objects2));

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        List<Double> data3 = dealList(objects3);
        Double max3 = data3.get(0) * 1.2;
        Double min3 = data3.get(1) * 0.8;

        switch (version){
            case "6.0":
                min1 = 45.0;
                max1 = 85.0;
                min2 = 0.0;
                max2 = 10.0;
                min3 = 15.0;
                max3 = 40.0;
                break;
            case "7.0":
                min1 = 45.0;
                max1 = 85.0;
                min2 = 0.0;
                max2 = 10.0;
                min3 = 10.0;
                max3 = 30.0;
                break;
            case "8.0":
                min1 = 45.0;
                max1 = 85.0;
                min2 = 6.0;
                max2 = 16.0;
                min3 = 8.0;
                max3 = 20.0;
                break;
            default:
        }

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
        series2.add(new Serie("B球团", objects2));

        // 标注类别
        Vector<Serie> series3 = new Vector<Serie>();
        series3.add(new Serie("P块矿", objects3));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);
        vectors.add(series3);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"A烧结矿(%)","B球团(%)","P块矿(%)"};

        int[] stack = {1, 1, 1};
        int[] ystack = {1, 2, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, min3, max3, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg3", image1);
    }

    private void dealPart456(String version, String[] tagNames){
        int[] scales = {1,1,1,1,1,1};
        List<List<Double>> doubles = partJHY(version, tagNames, "ALL", scales);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        Object[] objects3 = doubles.get(2).toArray();
        Object[] objects4 = doubles.get(3).toArray();
        Object[] objects5 = doubles.get(4).toArray();
        Object[] objects6 = doubles.get(5).toArray();
        dealPart4(objects1,objects2);
        dealPart5(objects3,objects4);
        dealPart6(objects5,objects6);
    }

    private void dealPart4(Object[] objects1, Object[] objects2) {
        objects1 = getDoubleVal(objects1,null,1);
        objects2 = getDoubleVal(objects2,null,1);

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        result.put("part8", getLastDoubleVal(objects1));
        result.put("part9", getLastDoubleVal(objects2));

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
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, yLabels.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg4", image1);
    }

    private void dealPart5(Object[] objects1, Object[] objects2) {
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
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, yLabels.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg5", image1);
    }

    private void dealPart6(Object[] objects1, Object[] objects2) {
        objects1 = getDoubleVal(objects1,100,2);
        objects2 = getDoubleVal(objects2,100,2);

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
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, yLabels.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg6", image1);
    }

    private void dealPart7(String version, String[] tagNames) {
        String[] cBrandCodes = null;
        if(version.equals("8.0")){
            cBrandCodes = new String[]{"6_SJK_SINTER"};
        }else{
            cBrandCodes = new String[]{"5_SJK_SINTER"};
        }
        List<List<Double>> doubles = part3(version, tagNames, cBrandCodes, "ALL", 100);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        objects1 = getDoubleVal(objects1,null,2);
        objects2 = getDoubleVal(objects2,null,2);

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.06;
        Double min1 = data.get(1) * 0.94;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        switch (version){
            case "6.0":
                min1 = 55.0;
                max1 = 58.0;
                min2 = 7.5;
                max2 = 10.5;
                break;
            case "7.0":
                min1 = 55.0;
                max1 = 58.0;
                min2 = 7.5;
                max2 = 10.5;
                break;
            case "8.0":
                min1 = 55.0;
                max1 = 58.0;
                min2 = 7.5;
                max2 = 10.5;
                break;
            default:
        }

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

        result.put("part14", getLastDoubleVal(objects1));
        result.put("part15", getLastDoubleVal(objects2));

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"TFe(%)","FeO(%)"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, tagNames.length, stack, ystack);
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

    private void dealPart9(String version, String[] tagNames) {
        String[] cBrandCodes = null;
        if(version.equals("6.0")){
            cBrandCodes = new String[]{"2_ZMHPCM_COAL"};
        }else if(version.equals("7.0")){
            cBrandCodes = new String[]{"3_ZMHPCM_COAL"};
        }else if(version.equals("8.0")){
            cBrandCodes = new String[]{"4_ZMHPCM_COAL"};
        }
        List<List<Double>> doubles = part3(version, tagNames, cBrandCodes, "ALL", 100);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        objects1 = getDoubleVal(objects1,null,2);
        objects2 = getDoubleVal(objects2,null,2);
        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        switch (version){
            case "6.0":
                min1 = 9.0;
                max1 = 12.5;
                min2 = 0.3;
                max2 = 0.65;
                break;
            case "7.0":
                min1 = 7.5;
                max1 = 11.0;
                min2 = 0.3;
                max2 = 0.65;
                break;
            case "8.0":
                min1 = 7.5;
                max1 = 11.0;
                min2 = 0.3;
                max2 = 0.65;
                break;
            default:
        }


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
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg9", image1);
    }

    private void dealPart10(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        if(version.equals("7.0")){
            objects2 = getDoubleVal(objects2,10000,0);
        }

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
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, tagNames.length, stack, ystack);
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
        Double max1 = data.get(0) * 1.035;
        Double min1 = data.get(1) * 0.975;

        List<Double> data2 = dealList(objects2);
        Double max2 = 260.0;
        Double min2 = data2.get(1)* 0.975;

        switch (version){
            case "6.0":
                min1 = 0.0;
                max1 = 450.0;
                min2 = 0.0;
                max2 = 180.0;
                break;
            case "7.0":
                min1 = 0.0;
                max1 = 500.0;
                min2 = 0.0;
                max2 = 500.0;
                break;
            case "8.0":
                min1 = 0.0;
                max1 = 500.0;
                min2 = 0.0;
                max2 = 500.0;
                break;
            default:
        }

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
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg11", image1);
    }

    private void dealPart12(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        objects1 = getDoubleVal(objects1,null,2);
        objects2 = getDoubleVal(objects2,null,2);

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

        result.put("part24", getLastDoubleVal(objects1));
        result.put("part25", getLastDoubleVal(objects2));

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"W","Z"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, tagNames.length, stack, ystack);
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
        String[] yLabels = {"CCT1(℃)","W4(℃)"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg13", image1);
    }

    private void dealPart16(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        objects2 = getDoubleVal(objects2,null,2);

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
        result.put("part33", getLastDoubleVal(objects2));

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.1;
        Double min1 = data.get(1) * 0.9;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.085;
        Double min2 = data2.get(1) * 0.95;

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"总热负荷(MJ*10/h)","煤气利用率(%)"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg16", image1);

    }

    private void dealPart16_2(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        objects2 = getDoubleVal(objects2,null,2);

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
        result.put("part37", getLastDoubleVal(objects2));

        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.2;
        Double min1 = data.get(1) * 0.8;

        List<Double> data2 = dealList(objects2);
        Double max2 = data2.get(0) * 1.2;
        Double min2 = data2.get(1) * 0.8;

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"总热负荷(MJ*10/h)","煤气利用率(%)"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg16", image1);

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
        String[] yLabels = {"PT(℃)",""};

        int[] stack = {1};
        int[] ystack = {1};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, 0, 0, 0, 0, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg18", image1);

    }

    private void dealPart19(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();

        objects1 = getDoubleVal(objects1,null,2);
        objects2 = getDoubleVal(objects2,null,3);

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

        result.put("part34", getLastDoubleVal(objects1));
        result.put("part35", getLastDoubleVal(objects2));

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"Si(%)","S(%)"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg17", image1);

    }

    private void dealPart20(String version) {
        List<Map<String, Object>> maps = part4(version);
        result.put("sheet14", maps);
    }

    private void dealCaoZuoFangZhen(String version) {
        partCaoZuoFangZhen(version);
    }

    private void dealZhaTie(String version) {
        partZhaTie(version);
    }

    private void dealCaoYeZhunQueLv(String version) {
        partCaoYeZhunQueLv(version);
    }

    private void dealTodayCaoZuoFangZhen(String version) {
        partTodayCaoZuoFangZhen(version);
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

    private JSONObject dataHttp(String tagName, Date date, String version) {
        Map<String, String> map = new HashMap<>();
        map.put("time", date.getTime()+"");
        map.put("tagname", tagName);
        String results = httpUtil.get(getUrlTagValueLatest(version), map);
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

    private JSONArray getLastRound(Date time, String version) {
        JSONArray data = null;
        Map<String, String> map = new HashMap<>();
        map.put("time", time.getTime() + "");
        map.put("calcModel", "forward");
        String results = httpUtil.get(getUrl2(version), map);
        if (StringUtils.isNotBlank(results)) {
            JSONObject jsonObject = JSONObject.parseObject(results);
            data = jsonObject.getJSONArray("data");
        }
        return data;
    }

    /**
     * 查询每天的用料类型（一天可能有多种，但只返回第一种）
     * @param version
     * @param startTime
     * @param endTime
     * @param type
     * @return
     */
    private List<String> dataHttpBrandCode(String version,Date startTime, Date endTime,String type) {
        List<String> result = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("startTime", DateUtil.getFormatDateTime(startTime, DateUtil.fullFormat));
        map.put("endTime", DateUtil.getFormatDateTime(endTime, DateUtil.fullFormat));
        map.put("type", type);
        String results = httpUtil.get(getBrandCodes(version), map);
        JSONObject jsonObject = JSONObject.parseObject(results);
        if(null != jsonObject){
            JSONArray data = jsonObject.getJSONArray("data");
            if(null!= data){
                for (Object o : data) {
                    result.add(String.valueOf(o));
                }
            }
        }
        return result;
    }

    /**
     * 查询操作方针数据
     * @param version
     * @return
     */
    private JSONObject dataHttpCaoZuoFangZhen(String version) {
        String url = getUrl4(version);
        String s = httpUtil.get(url, null);
        if (StringUtils.isBlank(s)) {
            return null;
        }
        JSONObject object = JSONObject.parseObject(s);
        JSONObject data = object.getJSONObject("data");
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
            List<XWPFTable> tt = doc.getTables();
            String[] needMerges = {"sheet15","sheet16"};
            for (String needMerge : needMerges) {
                for (XWPFTable table : tt) {
                    if(table.getText().contains(needMerge)){
                        mergeCellsVertically(table,table.getRow(0).getTableCells().size()-1,1,table.getRows().size()-1);
                    }
                }
            }
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

    /***
     * 跨列合并
     * @param table
     * @param row 所合并的行
     * @param fromCell  起始列
     * @param toCell   终止列
     */
    private void mergeCellsHorizontal(XWPFTable table, int row, int fromCell, int toCell) {
        for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {
            XWPFTableCell cell = table.getRow(row).getCell(cellIndex);
            if (cellIndex == fromCell) {
                // The first merged cell is set with RESTART merge value
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            } else {
                // Cells which join (merge) the first one, are set with CONTINUE
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    /***
     *  跨行合并
     * @param table
     * @param col  合并列
     * @param fromRow 起始行
     * @param toRow   终止行
     */
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
     * 文档的日期字符串
     * @param map
     */
    private void dealDate(HashMap<String, Object> map) {
        Date date = new Date();
        String date1 = DateUtil.getFormatDateTime(date, "yyyy年MM月dd日 HH:mm");
        String date2 = DateUtil.getFormatDateTime(date, "yyyy年MM月dd日");

        //文档所有日期
        map.put("date1", date1);
        map.put("date2", date2);
    }

    /**
     * 提取int类型的val
     * @param objects1
     * @return
     */
    private Object getLastVal(Object[] objects1) {
        if (Objects.isNull(objects1) || objects1.length == 0) {
            return null;
        }
        Object o = objects1[objects1.length - 1];
        int v1 = 0;
        if (Objects.nonNull(o)) {
            Double v = (Double) o;
            v1 = v.intValue();
        }
        if(0 == v1){
            o = objects1[objects1.length - 2];
            if (Objects.nonNull(o)) {
                Double v = (Double) o;
                v1 = v.intValue();
            }
        }
        return v1;
    }

    /**
     * 提取double类型的val
     * @param objects1
     * @return
     */
    private Object getLastDoubleVal(Object[] objects1) {
        if (Objects.isNull(objects1) || objects1.length == 0) {
            return null;
        }
        Object o = objects1[objects1.length - 1];
        Double v1 = 0.0;
        if (Objects.nonNull(o)) {
            Double v = (Double) o;
            v1 = v.doubleValue();
        }
        if(0.0 == v1){
            o = objects1[objects1.length - 2];
            if (Objects.nonNull(o)) {
                Double v = (Double) o;
                v1 = v;
            }
        }
        return v1;
    }

    /**
     * 提取double类型的objects[]
     * @param objects1
     * @param multiple 倍数
     * @param decimal 小数点位数
     * @return
     */
    private Object[] getDoubleVal(Object[] objects1, Integer multiple, Integer decimal) {
        if (Objects.isNull(objects1) || objects1.length == 0) {
            return null;
        }
        Object[] result = new Object[objects1.length];
        int i = 0;

        int scale = 1;
        if(null != decimal){
            int m = decimal;
            for(;m>0;m--){
                scale *= 10;
            }
        }
        for (Object o : objects1) {
            Double v = null;
            if (null != o) {
                v = (Double) o;
                if(null != multiple){
                    v = v*multiple;
                }
                if(null != decimal){
                    v = (double) Math.round(v * scale) / scale;
                }
            }
            result[i++] = v;
        }
        return result;
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
        return httpProperties.getGlUrlVersion(version) + "/burden/round/last";
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

    /**
     * 操作方针
     *
     * @param version
     * @return
     */
    private String getUrl4(String version) {
        return httpProperties.getGlUrlVersion(version) + "/process/findNewestFormData";
    }

    /**
     * tag点最新值
     *
     * @param version
     * @return
     */
    private String getUrlTagValueLatest(String version) {
        return httpProperties.getGlUrlVersion(version) + "/tagValue/latest";
    }

    /**
     * 获取高炉焦炭或者烧结上料的种类
     * @param version
     * @return
     */
    private String getBrandCodes(String version){
        return httpProperties.getGlUrlVersion(version) + "/brandCodes/getBrandCodes";
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
            image.setHeight(300);
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
