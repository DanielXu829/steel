package com.cisdi.steel.module.job.a3.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.util.DateUtil;
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
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class ShaojieDocMain {

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private HttpProperties httpProperties;

    @Autowired
    private JobProperties jobProperties;

    @Autowired
    private ReportIndexMapper reportIndexMapper;

    private String version4 = "4.0";
    private String version5 = "5.0";

    @Scheduled(cron = "0 40 14 * * ?")
    //    @Scheduled(cron = "0 0/1 * * * ?")
    public void mainJob() {
        Date date = new Date();
//        mainDeal(version5, date);
        mainDeal(version4, date);
        log.error("烧结word生成完毕！");
    }


    public void mainTask(Date date) {
//        mainDeal(version5, date);
        mainDeal(version4, date);
        log.error("烧结word生成完毕！");
    }


    public void mainDeal(String version, Date date) {
        init();
        dealVersion(version);
        if (Objects.isNull(date)) {
            date = new Date();
        }
        JSONObject data = dataHttp(L1, DateUtil.addDays(date, -1), version);
        for (int i = 0; i < L1.length; i++) {
            JSONObject o = data.getJSONObject(L1[i]);
            if (i < list.size()) {
                Map<String, Object> map = list.get(i);
                commPart1(o, map, "attr2", "attr3", "attr4", "attr5");
                dataList.add(map);
            } else {
                // 7
                if (i == 7)
                    commPart1(o, result, "attr2", "attr3", "attr4", "attr5");
                if (i == 8)
                    commPart1(o, result, "attr6", "attr7", "attr8", "attr9");
                if (i == 9)
                    commPart1(o, result, "attr11", "attr12", "attr13", "attr14");
                if (i == 10)
                    commPart1(o, result, "attr15", "attr16", "attr17", "attr18");
                if (i == 11)
                    commPart1(o, result, "attr20", "attr21", "attr22", "attr23");
                if (i == 12)
                    commPart1(o, result, "attr24", "attr25", "attr26", "attr27");
            }
        }

        JSONObject data2_1 = dataHttp(L2, DateUtil.addDays(date, -2), version);
        JSONObject data2_2 = dataHttp(L2, DateUtil.addDays(date, -1), version);
        commPart2(list1, L2, data2_1, data2_2, "attr3", "attr4", "attr5");

        JSONObject data3_1 = dataHttp(L3, DateUtil.addDays(date, -2), version);
        JSONObject data3_2 = dataHttp(L3, DateUtil.addDays(date, -1), version);
        commPart2(list2, L3, data3_1, data3_2, "attr2", "attr3", "attr4");


        if ("5.0".equals(version)) {
            comm(jobProperties.getTemplatePath() + File.separator + "doc" + File.separator + "五烧每日操业会-设计版v1.docx");
        } else {
            comm(jobProperties.getTemplatePath() + File.separator + "doc" + File.separator + "四烧每日操业会-设计版v1.docx");
        }
    }

    private void dealVersion(String version) {

        if ("5.0".equals(version)) {
            /**
             ST5_L1R_OB_SetDelAmt_1d_avg
             ST5_L1R_OB_ColdReturnFineUseP_1d_avg
             ST5_L1R_OB_DustUseP_1d_avg
             ST5_L1R_SIN_MI202_1d_avg
             ST5_L1R_SIN_LI303_1d_avg
             ST5_L1R_SIN_TI350_1d_avg
             ST5_L1R_SIN_SiMaRunVel_1d_avg
             ST5_L1R_SIN_PI300B_1d_avg
             ST5_L1R_SIN_PI300A_1d_avg
             ST5_L1R_SIN_OP424B_1d_avg
             ST5_L1R_SIN_OP424A_1d_avg
             ST5_L1R_SIN_BtpPoN_1d_avg
             ST5_L1R_SIN_BtpPoS_1d_avg
             */
            L1 = new String[]
                    {
                            "ST5_L1R_OB_SetDelAmt_1d_avg",
                            "ST5_L1R_OB_ColdReturnFineUseP_1d_avg",
                            "ST5_L1R_OB_DustUseP_1d_avg",
                            "ST5_L1R_SIN_MI202_1d_avg",
                            "ST5_L1R_SIN_LI303_1d_avg",
                            "ST5_L1R_SIN_TI350_1d_avg",
                            "ST5_L1R_SIN_SiMaRunVel_1d_avg",
                            "ST5_L1R_SIN_PI300B_1d_avg",
                            "ST5_L1R_SIN_PI300A_1d_avg",
                            "ST5_L1R_SIN_OP424B_1d_avg",
                            "ST5_L1R_SIN_OP424A_1d_avg",
                            "ST5_L1R_SIN_BtpPoN_1d_avg",
                            "ST5_L1R_SIN_BtpPoS_1d_avg"
                    };

            /**
             * ST5_MESR_SIN_SinterDayThY_1d_cur
             *
             * ST5_MESR_SIN_SinterTFe_1d_avg
             * ST5_MESR_SIN_SinterFeO_1d_avg
             * ST5_MESR_SIN_SinterMgAlRatio_1d_avg
             * ST5_MESR_SIN_SinterTumIx_1d_avg
             * ST5_MESR_SIN_SinterScrIx_1d_avg
             * ST5_MESR_SIN_Sinter5to10mm_1d_avg
             * ST5_MESR_SIN_Sinter10to40mm_1d_avg
             * ST5_MESR_SIN_SinterH40mm_1d_avg
             * ST5_MESR_SIN_SinterMeanSize_1d_avg
             * ST5_MESR_SIN_SinterRDIH3p15_1d_avg
             */
            L2 = new String[]
                    {
                            "ST5_MESR_SIN_SinterDayThY_1d_cur",
                            "",
                            "ST5_MESR_SIN_SinterTFe_1d_avg",
                            "ST5_MESR_SIN_SinterFeO_1d_avg",
                            "ST5_MESR_SIN_SinterMgAlRatio_1d_avg",
                            "ST5_MESR_SIN_SinterTumIx_1d_avg",
                            "ST5_MESR_SIN_SinterScrIx_1d_avg",
                            "ST5_MESR_SIN_Sinter5to10mm_1d_avg",
                            "ST5_MESR_SIN_Sinter10to40mm_1d_avg",
                            "ST5_MESR_SIN_SinterH40mm_1d_avg",
                            "ST5_MESR_SIN_SinterMeanSize_1d_avg",
                            "ST5_MESR_SIN_SinterRDIH3p15_1d_avg",
                    };


            /**
             * ST5_L1R_OB_FuelUseP_1d_avg
             * ST5_L1R_OB_ColdReturnFineUseP_1d_avg
             *
             * ST5_L1R_SIN_MI202_1d_avg
             * ST5_L1R_SIN_LI303_1d_avg
             * ST5_L1R_SIN_TI350_1d_avg
             * ST5_L1R_SIN_SiMaRunVel_1d_avg
             * ST5_L1R_SIN_VerSinVel_1d_avg
             * ST5_L1R_SIN_PI300B_1d_avg
             * ST5_L1R_SIN_PI300A_1d_avg
             * ST5_L1R_SIN_BtpTeNS_1d_avg
             * ST5_L1R_SIN_BtpPoNS_1d_avg
             */
            L3 = new String[]
                    {
                            "ST5_L1R_OB_FuelUseP_1d_avg",
                            "ST5_L1R_OB_ColdReturnFineUseP_1d_avg",
                            "",
                            "ST5_L1R_SIN_MI202_1d_avg",
                            "ST5_L1R_SIN_LI303_1d_avg",
                            "ST5_L1R_SIN_TI350_1d_avg",
                            "ST5_L1R_SIN_SiMaRunVel_1d_avg",
                            "ST5_L1R_SIN_VerSinVel_1d_avg",
                            "ST5_L1R_SIN_PI300B_1d_avg",
                            "ST5_L1R_SIN_PI300A_1d_avg",
                            "ST5_L1R_SIN_BtpTeNS_1d_avg",
                            "ST5_L1R_SIN_BtpPoNS_1d_avg"
                    };
        } else {
            /**
             * ST4_L1R_SIN_DelAmtUse_1d_avg
             * ST4_L1R_OB_CoReFineUseP_1d_avg
             * ST4_L1R_OB_DustUseP_1d_avg
             * ST4_L1R_SIN_MI202_1d_avg
             * ST4_L1R_SIN_LI3031_1d_avg
             * ST4_L1R_SIN_TIC351PVIN_1d_avg
             * ST4_L1R_SIN_SiMaRunVel_1d_avg
             * ST4_L1R_SIN_TI300B_1d_avg
             * ST4_L1R_SIN_TI300A_1d_avg
             * ST4_L1R_SIN_DL424B_1d_avg
             * ST4_L1R_SIN_DL424A_1d_avg
             * ST4_L1R_SIN_BtpPoS_1d_avg
             * ST4_L1R_SIN_BtpPoN_1d_avg
             */
            L1 = new String[]
                    {
                            "ST4_L1R_SIN_DelAmtUse_1d_avg",
                            "ST4_L1R_OB_CoReFineUseP_1d_avg",
                            "ST4_L1R_OB_DustUseP_1d_avg",
                            "ST4_L1R_SIN_MI202_1d_avg",
                            "ST4_L1R_SIN_LI3031_1d_avg",
                            "ST4_L1R_SIN_TIC351PVIN_1d_avg",
                            "ST4_L1R_SIN_SiMaRunVel_1d_avg",
                            "ST4_L1R_SIN_TI300B_1d_avg",
                            "ST4_L1R_SIN_TI300A_1d_avg",
                            "ST4_L1R_SIN_DL424B_1d_avg",
                            "ST4_L1R_SIN_DL424A_1d_avg",
                            "ST4_L1R_SIN_BtpPoS_1d_avg",
                            "ST4_L1R_SIN_BtpPoN_1d_avg"
                    };
            /**
             * ST4_MESR_SIN_SinterDayThY_1d_cur
             *
             * ST4_MESR_SIN_SinterTFe_1d_avg
             * ST4_MESR_SIN_SinterFeO_1d_avg
             * ST4_MESR_SIN_SinterMgAlRatio_1d_avg
             * ST4_MESR_SIN_SinterTumIx_1d_avg
             * ST4_MESR_SIN_SinterScrIx_1d_avg
             * ST4_MESR_SIN_Sinter5to10mm_1d_avg
             * ST4_MESR_SIN_Sinter10to40mm_1d_avg
             * ST4_MESR_SIN_SinterH40mm_1d_avg
             * ST4_MESR_SIN_SinterMeanSize_1d_avg
             * ST4_MESR_SIN_SinterRDIH3p15_1d_avg
             */
            L2 = new String[]
                    {
                            "ST4_MESR_SIN_SinterDayThY_1d_cur",
                            "",
                            "ST4_MESR_SIN_SinterTFe_1d_avg",
                            "ST4_MESR_SIN_SinterFeO_1d_avg",
                            "ST4_MESR_SIN_SinterMgAlRatio_1d_avg",
                            "ST4_MESR_SIN_SinterTumIx_1d_avg",
                            "ST4_MESR_SIN_SinterScrIx_1d_avg",
                            "ST4_MESR_SIN_Sinter5to10mm_1d_avg",
                            "ST4_MESR_SIN_Sinter10to40mm_1d_avg",
                            "ST4_MESR_SIN_SinterH40mm_1d_avg",
                            "ST4_MESR_SIN_SinterMeanSize_1d_avg",
                            "ST4_MESR_SIN_SinterRDIH3p15_1d_avg",
                    };

            /**
             * ST4_L1R_OB_FuelUseP_1d_avg
             * ST4_L1R_OB_CoReFineUseP_1d_avg
             * ST4_L1R_SIN_13DustInstanAmt_1d_avg
             * ST4_L1R_SIN_MI202_1d_avg
             * ST4_L1R_SIN_LI3031_1d_avg
             * ST4_L1R_SIN_TIC351PVIN_1d_avg
             ST4_L1R_SIN_SiMaRunVel_1d_avg
             ST4_L1R_SIN_VerSinVel_1d_avg
             * ST4_L1R_SIN_TI300B_1d_avg
             * ST4_L1R_SIN_TI300A_1d_avg
             * ST4_L1R_SIN_BtpTeNS_1d_avg
             * ST4_L1R_SIN_BtpPoNS_1d_avg
             */
            L3 = new String[]
                    {
                            "ST4_L1R_OB_FuelUseP_1d_avg",
                            "ST4_L1R_OB_CoReFineUseP_1d_avg",
                            "ST4_L1R_SIN_13DustInstanAmt_1d_avg",
                            "ST4_L1R_SIN_MI202_1d_avg",
                            "ST4_L1R_SIN_LI3031_1d_avg",
                            "ST4_L1R_SIN_TIC351PVIN_1d_avg",
                            "ST4_L1R_SIN_SiMaRunVel_1d_avg",
                            "ST4_L1R_SIN_VerSinVel_1d_avg",
                            "ST4_L1R_SIN_TI300B_1d_avg",
                            "ST4_L1R_SIN_TI300A_1d_avg",
                            "ST4_L1R_SIN_BtpTeNS_1d_avg",
                            "ST4_L1R_SIN_BtpPoNS_1d_avg"
                    };
        }

    }

    /**
     * 第一部分点名
     */
    private String[] L1 = null;

    private void commPart1(JSONObject o, Map<String, Object> map, String key1, String key2, String key3, String key4) {
        if (Objects.nonNull(o)) {
            Map<String, Object> innerMap = o.getInnerMap();
            Set<String> keySet = innerMap.keySet();
            Object o1 = null;
            for (String key : keySet) {
                o1 = innerMap.get(key);
            }
            if (Objects.nonNull(o1)) {
                BigDecimal attr3 = (BigDecimal) o1;
                String attr2 = (String) map.get(key1);
                if (attr2.contains("±")) {
                    matchDeal(map, attr2, attr3, key3, key4, "±");
                } else if (attr2.contains("-")) {
                    matchDeal(map, attr2, attr3, key3, key4, "-");
                }
                attr3 = attr3.setScale(2, BigDecimal.ROUND_HALF_UP);
                map.put(key2, attr3);
            }
        }
    }

    private void matchDeal(Map<String, Object> map, String attr2, BigDecimal attr3, String key3, String key4, String match) {
        // 去除中文
        Pattern pat = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher mat = pat.matcher(attr2);
        attr2 = mat.replaceAll("");
        String[] split = attr2.split(match);
        double can = Double.valueOf(split[0]);
        double pianc = Double.valueOf(split[1]);

        if ("±".equals(match)) {
            BigDecimal subtract = attr3.subtract(new BigDecimal(can));
            double attr4 = subtract.doubleValue();
            String attr5 = "正常";
            if (attr4 < -pianc) {
                attr5 = "偏低";
            } else if (attr4 > pianc) {
                attr5 = "偏高";
            }
            subtract = subtract.setScale(2, BigDecimal.ROUND_HALF_UP);
            map.put(key3, subtract);
            map.put(key4, attr5);
        } else {
            String attr5 = "正常";
            double attr4 = attr3.doubleValue();
            if (attr4 < can) {
                attr5 = "偏低";
            } else if (attr4 > pianc) {
                attr5 = "偏高";
            }
            attr3 = attr3.setScale(2, BigDecimal.ROUND_HALF_UP);
            map.put(key3, attr3);
            map.put(key4, attr5);
        }
    }

    private void part1Data() {
        //文档第一部分
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attr1", "上料量t/h");
        map.put("attr2", "730±10");
        map.put("attr3", "710");
        map.put("attr4", "-20");
        map.put("attr5", "正常");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "返矿配比%");
        map.put("attr2", "18±1");
        map.put("attr3", "18.5");
        map.put("attr4", "0.5");
        map.put("attr5", "正常");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "粉尘配比t/h");
        map.put("attr2", "12±2");
        map.put("attr3", "10.91");
        map.put("attr4", "-1.09");
        map.put("attr5", "正常");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "混合料水分%");
        map.put("attr2", "7.2±0.3");
        map.put("attr3", "7.17");
        map.put("attr4", "-0.03");
        map.put("attr5", "正常");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "布料参数mm");
        map.put("attr2", "620±10");
        map.put("attr3", "620");
        map.put("attr4", "0");
        map.put("attr5", "正常");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "点火温度℃");
        map.put("attr2", "1080±30");
        map.put("attr3", "1096");
        map.put("attr4", "16");
        map.put("attr5", "正常");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "烧结机速m/min");
        map.put("attr2", "1.90±0.05");
        map.put("attr3", "1.94");
        map.put("attr4", "0.04");
        map.put("attr5", "正常");
        list.add(map);

        //文档第一部分 下
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("attr1", "大烟道温度℃");
        map1.put("attr2", "南100±20");
        map1.put("attr3", "107");
        map1.put("attr4", "7");
        map1.put("attr5", "正常");

        map1.put("attr6", "北140±20");
        map1.put("attr7", "140");
        map1.put("attr8", "0");
        map1.put("attr9", "正常");

        map1.put("attr10", "主抽阀门开度%");
        map1.put("attr11", "南85-97");
        map1.put("attr12", "97.00");
        map1.put("attr13", "-");
        map1.put("attr14", "正常");

        map1.put("attr15", "北85-97");
        map1.put("attr16", "97.00");
        map1.put("attr17", "-");
        map1.put("attr18", "正常");

        map1.put("attr19", "BTP位置");
        map1.put("attr20", "南23.0±1.0");
        map1.put("attr21", "22.93");
        map1.put("attr22", "-0.17");
        map1.put("attr23", "正常");

        map1.put("attr24", "北23.0±1.0");
        map1.put("attr25", "23.07");
        map1.put("attr26", "0.07");
        map1.put("attr27", "正常");

        result.putAll(map1);
    }

    /**
     * 第二部分点名
     */
    private String[] L2 = null;

    private void part2Data() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attr1", "理论产量t");
        map.put("attr2", "-");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        list1.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "R±0.05合格率%");
        map.put("attr2", "≥93");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        list1.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "TFe%");
        map.put("attr2", "-");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        list1.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "FeO%");
        map.put("attr2", "-");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        list1.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "镁铝比");
        map.put("attr2", "-");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        list1.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "转鼓%");
        map.put("attr2", "≥76");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        list1.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "筛分%");
        map.put("attr2", "≤6");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        list1.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "5~10mm比例%");
        map.put("attr2", "≤20");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        list1.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "10~40mm比例%");
        map.put("attr2", "≥58.48");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        list1.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "＞40mm比例%");
        map.put("attr2", "-");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        list1.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "平均粒径mm");
        map.put("attr2", "≥20.5");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        list1.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "RDI%");
        map.put("attr2", "≥60");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        list1.add(map);

    }


    private void commPart2(List<Map<String, Object>> listData, String[] point, JSONObject data2_1, JSONObject data2_2, String key3, String key4, String key5) {
        for (int i = 0; i < point.length; i++) {
            //昨天数据
            JSONObject o_1 = data2_1.getJSONObject(point[i]);

            //今天数据
            JSONObject o_2 = data2_2.getJSONObject(point[i]);

            if (i < listData.size()) {
                Map<String, Object> map = listData.get(i);
                if (Objects.nonNull(o_1)) {
                    Map<String, Object> innerMap = o_1.getInnerMap();
                    Set<String> keySet = innerMap.keySet();
                    BigDecimal o1 = BigDecimal.ZERO;

                    Long[] list = new Long[keySet.size()];
                    int k = 0;
                    for (String key : keySet) {
                        list[k] = Long.valueOf(key);
                        k++;
                    }
                    Arrays.sort(list);

                    for (int m = 0; m < list.length; m++) {
                        o1 = (BigDecimal) innerMap.get(list[m] + "");
                    }
                    o1 = o1.setScale(2, BigDecimal.ROUND_HALF_UP);

                    map.put(key3, o1);


                    Map<String, Object> innerMap2 = o_2.getInnerMap();
                    Set<String> keySet2 = innerMap2.keySet();
                    BigDecimal o2 = BigDecimal.ZERO;

                    list = new Long[keySet2.size()];
                    k = 0;
                    for (String key : keySet2) {
                        list[k] = Long.valueOf(key);
                        k++;
                    }
                    Arrays.sort(list);

                    for (int m = 0; m < list.length; m++) {
                        o2 = (BigDecimal) innerMap2.get(list[m] + "");
                    }
                    o2 = o2.setScale(2, BigDecimal.ROUND_HALF_UP);
                    map.put(key4, o2);

                    if (Objects.nonNull(o2) && Objects.nonNull(o1)) {
                        BigDecimal attr5 = o2.subtract(o1);
                        attr5 = attr5.setScale(2, BigDecimal.ROUND_HALF_UP);
                        map.put(key5, attr5);
                    }

                }
            }
        }
    }

    /**
     * 第三部分点名
     */
    private String[] L3 = null;

    private void part3Data() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attr1", "燃料配比%");
        map.put("attr2", "");
        map.put("attr3", "");
        map.put("attr4", "");
        list2.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "返矿配比%");
        map.put("attr2", "");
        map.put("attr3", "");
        map.put("attr4", "");
        list2.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "粉尘配加量t/h");
        map.put("attr2", "");
        map.put("attr3", "");
        map.put("attr4", "");
        list2.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "混合料水分%");
        map.put("attr2", "");
        map.put("attr3", "");
        map.put("attr4", "");
        list2.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "布料参数mm");
        map.put("attr2", "");
        map.put("attr3", "");
        map.put("attr4", "");
        list2.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "点火温度℃");
        map.put("attr2", "");
        map.put("attr3", "");
        map.put("attr4", "");
        list2.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "烧结机速度m/min");
        map.put("attr2", "");
        map.put("attr3", "");
        map.put("attr4", "");
        list2.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "垂直烧结速度mm/min");
        map.put("attr2", "");
        map.put("attr3", "");
        map.put("attr4", "");
        list2.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "烟道温度℃（南）");
        map.put("attr2", "");
        map.put("attr3", "");
        map.put("attr4", "");
        list2.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "烟道温度℃（北）");
        map.put("attr2", "");
        map.put("attr3", "");
        map.put("attr4", "");
        list2.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "BTP温度℃");
        map.put("attr2", "");
        map.put("attr3", "");
        map.put("attr4", "");
        list2.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "BTP位置");
        map.put("attr2", "");
        map.put("attr3", "");
        map.put("attr4", "");
        list2.add(map);

    }

    private void part4Data() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attr1", "余热发电（kWh/t）");
        map.put("attr2", "≥14.54");
        map.put("attr3", "12.79");
        map.put("attr4", "14.15");
        map.put("attr5", "-1.8");
        map.put("attr6", "-0.39");
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "固燃比（kg/t）");
        map.put("attr2", "≤63.56");
        map.put("attr3", "4.2");
        map.put("attr4", "63.6");
        map.put("attr5", "0.64");
        map.put("attr6", "0.04");
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "电耗(kwh/t)");
        map.put("attr2", "≤37.77");
        map.put("attr3", "38.46");
        map.put("attr4", "36.74");
        map.put("attr5", "0.69");
        map.put("attr6", "-1.03");
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "熔剂成本（元/t）");
        map.put("attr2", "≤25.30");
        map.put("attr3", "26.41");
        map.put("attr4", "25.74");
        map.put("attr5", "1.11");
        map.put("attr6", "0.44");
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "内返矿率（%）");
        map.put("attr2", "≤21");
        map.put("attr3", "18.28");
        map.put("attr4", "17.68");
        map.put("attr5", "-2.7");
        map.put("attr6", "-3.32");
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "主抽电耗（kWh/t）");
        map.put("attr2", "≤21");
        map.put("attr3", "21.40");
        map.put("attr4", "20.33");
        map.put("attr5", "0.4");
        map.put("attr6", "-0.67");
        list3.add(map);
    }

    private List<Map<String, Object>> list = null;
    private List<Map<String, Object>> dataList = null;

    private List<Map<String, Object>> list1 = null;
    private List<Map<String, Object>> list2 = null;
    private List<Map<String, Object>> list3 = null;
    private List<Map<String, Object>> list4 = null;
    private List<Map<String, Object>> list5 = null;

    /**
     * doc最后结果
     */
    private HashMap<String, Object> result = null;

    private void init() {
        result = new HashMap<String, Object>();
        list = new ArrayList();
        dataList = new ArrayList();
        list1 = new ArrayList();
        list2 = new ArrayList();
        list3 = new ArrayList();
        list4 = new ArrayList();
        list5 = new ArrayList();

        part1Data();
        part2Data();
        part3Data();
        part4Data();


        Map<String, Object> map7 = new HashMap<String, Object>();
        map7.put("attr0", "1");
        map7.put("attr1", "5#烧结高硫脱硫进口");
        map7.put("attr2", "≤50");
        map7.put("attr3", "—");
        map7.put("attr4", "≤300");
        map7.put("attr5", "≤75");
        map7.put("attr6", "—");
        map7.put("attr7", "—");

        Map<String, Object> map8 = new HashMap<String, Object>();
        map8.put("attr0", "2");
        map8.put("attr1", "5#烧结低硫脱硫进口");
        map8.put("attr2", "≤50");
        map8.put("attr3", "—");
        map8.put("attr4", "≤300");
        map8.put("attr5", "≤75");
        map8.put("attr6", "—");
        map8.put("attr7", "—");


        list4.add(map7);
        list4.add(map8);

        Map<String, Object> map9 = new HashMap<String, Object>();
        map9.put("attr1", "混合料水分率波动范围");
        map9.put("attr2", "±0.5％");

        Map<String, Object> map10 = new HashMap<String, Object>();
        map10.put("attr1", "点火温度");
        map10.put("attr2", "1100±100℃");

        Map<String, Object> map11 = new HashMap<String, Object>();
        map11.put("attr1", "南大烟道温度");
        map11.put("attr2", "110±20℃（6号机）\n" +
                "100±20℃（5号机）");

        Map<String, Object> map12 = new HashMap<String, Object>();
        map12.put("attr1", "粉尘配比单次调整幅度");
        map12.put("attr2", "        " + "≤0.5%（6号机）\n" +
                "        " + "≤1t（5号机）");

        list5.add(map9);
        list5.add(map10);
        list5.add(map11);
        list5.add(map12);


    }

    private JSONObject dataHttp(String[] tagNames, Date date, String version) {
        DateQuery dateQuery = DateQueryUtil.buildToday(date);
        JSONObject query = new JSONObject();
        query.put("start", dateQuery.getQueryStartTime());
        query.put("end", dateQuery.getQueryEndTime());
        query.put("tagNames", tagNames);
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String results = httpUtil.postJsonParams(getUrl(version), jsonString);
        JSONObject jsonObject = JSONObject.parseObject(results);
        JSONObject data = jsonObject.getJSONObject("data");
        return data;
    }

    private void comm(String path) {
        //文档所有日期
        dealDate(result);

        //文档第一部分 上
        result.put("sheet1", dataList);


        result.put("sheet2", list1);
        result.put("sheet3", list2);
        result.put("sheet4", list3);

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("bttr1", "上料量t/h");
        map2.put("bttr2", "720±10");

        map2.put("bttr3", "返矿配比%");
        map2.put("bttr4", "18±1");

        map2.put("bttr5", "粉尘配比t/h");
        map2.put("bttr6", "12±2");

        map2.put("bttr7", "混合料水分%");
        map2.put("bttr8", "7.2±0.3");


        map2.put("bttr9", "布料参数mm");
        map2.put("bttr10", "620±10");


        map2.put("bttr11", "点火温度℃");
        map2.put("bttr12", "1080±30");


        map2.put("bttr13", "烧结机速m/min");
        map2.put("bttr14", "1.92±0.05");


        map2.put("bttr15", "大烟道温度℃");
        map2.put("bttr16", "南100±20");
        map2.put("bttr17", "北140±20");

        map2.put("bttr18", "主抽阀门开度%");
        map2.put("bttr19", "南85-97");
        map2.put("bttr20", "北85-97");

        map2.put("bttr21", "BTP位置");
        map2.put("bttr22", "南23.0±1.0");
        map2.put("bttr23", "北23.0±1.0");

        result.putAll(map2);

        result.put("sheet5", list4);
        result.put("sheet6", list5);

        try {
            String name = "四烧";
            String sqquence = "4烧结";
            if (path.contains("五烧")) {
                name = "五烧";
                sqquence = "5烧结";
            }
            log.info(path);

            XWPFDocument doc = WordExportUtil.exportWord07(path, result);
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                List<XWPFRun> runs = paragraph.getRuns();
                for (XWPFRun run : runs) {
                    run.setFontFamily("微软雅黑", XWPFRun.FontCharRange.cs);//字体，范围----效果不详
                }
            }
            String fileName = name + DateUtil.getFormatDateTime(new Date(), "yyyyMMdd") + "每日操业会 - 设计版v1.docx";
            String filePath = jobProperties.getFilePath() + File.separator + "doc" + File.separator + fileName;
            FileOutputStream fos = new FileOutputStream(filePath);
            log.info(filePath);
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
            reportIndex.setReportCategoryCode(JobEnum.sj_caoyehui_day.getCode());
            reportIndex.setPath(filePath);
            reportIndexMapper.insert(reportIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealDate(HashMap<String, Object> map) {
        Date date = new Date();
        String date1 = DateUtil.getFormatDateTime(DateUtil.addDays(date, -2), DateUtil.MMddChineseFormat);
        String date2 = DateUtil.getFormatDateTime(DateUtil.addDays(date, -1), DateUtil.MMddChineseFormat);
        String date3 = DateUtil.getFormatDateTime(date, DateUtil.MMddChineseFormat);
        String date4 = DateUtil.getFormatDateTime(DateUtil.addDays(date, 1), DateUtil.MMddChineseFormat);
        String date5 = DateUtil.getFormatDateTime(date, DateUtil.yyyyMMddChineseFormat);
        String date6 = DateUtil.getFormatDateTime(date, "dd日");

        //文档所有日期
        map.put("date1", date1);
        map.put("date2", date2);
        map.put("date3", date3);
        map.put("date4", date4);
        map.put("date5", date5);
        map.put("date6", date6);
    }

    private String getUrl(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/tagValues/tagNames";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/tagValues/tagNames";
        }
    }
}
