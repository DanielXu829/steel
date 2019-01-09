package com.cisdi.steel.module.job.a1.doc;

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


    public static void main(String[] args) {
        new GaoLuDocMain().mainTask();
    }

    public void mainTask() {
//        mainDeal(version6);
        mainDeal(version8);
        log.error("高炉word生成完毕！");
    }

    public void mainDeal(String version) {
        //init();

        if ("6.0".equals(version)) {
//            comm(jobProperties.getTemplatePath() + File.separator + "doc" + File.separator + "五烧每日操业会-设计版v1.docx");
        } else {
            comm(jobProperties.getTemplatePath() + File.separator + "doc" + File.separator + "八高炉操业会议纪要（实施版）.docx");
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
                map.put(key2, attr3.doubleValue());
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
            double attr4 = attr3.doubleValue() - can;
            String attr5 = "正常";
            if (attr4 < -pianc) {
                attr5 = "偏低";
            } else if (attr4 > pianc) {
                attr5 = "偏高";
            }
            map.put(key3, attr4);
            map.put(key4, attr5);
        } else {
            String attr5 = "正常";
            double attr4 = attr3.doubleValue();
            if (attr4 < can) {
                attr5 = "偏低";
            } else if (attr4 > pianc) {
                attr5 = "偏高";
            }
            map.put(key3, attr4);
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
                    Object o1 = null;

                    Long[] list = new Long[keySet.size()];
                    int k = 0;
                    for (String key : keySet) {
                        list[k] = Long.valueOf(key);
                        k++;
                    }
                    Arrays.sort(list);

                    for (int m = 0; m < list.length; m++) {
                        o1 = innerMap.get(list[m] + "");
                    }

                    map.put(key3, o1);


                    Map<String, Object> innerMap2 = o_2.getInnerMap();
                    Set<String> keySet2 = innerMap2.keySet();
                    Object o2 = null;

                    list = new Long[keySet2.size()];
                    k = 0;
                    for (String key : keySet2) {
                        list[k] = Long.valueOf(key);
                        k++;
                    }
                    Arrays.sort(list);

                    for (int m = 0; m < list.length; m++) {
                        o2 = innerMap2.get(list[m] + "");
                    }
                    map.put(key4, o2);

                    if (Objects.nonNull(o2) && Objects.nonNull(o1)) {
                        BigDecimal attr5 = BigDecimal.ZERO;
                        BigDecimal b1 = (BigDecimal) o1;
                        BigDecimal b2 = (BigDecimal) o2;
                        attr5 = b2.subtract(b1);

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
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        map.put("attr6", "");
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "固燃比（kg/t）");
        map.put("attr2", "≤63.56");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        map.put("attr6", "");
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "电耗(kwh/t)");
        map.put("attr2", "≤37.77");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        map.put("attr6", "");
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "熔剂成本（元/t）");
        map.put("attr2", "≤25.30");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        map.put("attr6", "");
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "内返矿率（%）");
        map.put("attr2", "≤21");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        map.put("attr6", "");
        list3.add(map);

        map = new HashMap<String, Object>();
        map.put("attr1", "主抽电耗（kWh/t）");
        map.put("attr2", "≤21");
        map.put("attr3", "");
        map.put("attr4", "");
        map.put("attr5", "");
        map.put("attr6", "");
        list3.add(map);
    }

    private List<Map<String, Object>> list = new ArrayList();
    private List<Map<String, Object>> dataList = new ArrayList();

    private List<Map<String, Object>> list1 = new ArrayList();
    private List<Map<String, Object>> list2 = new ArrayList();
    private List<Map<String, Object>> list3 = new ArrayList();
    private List<Map<String, Object>> list4 = new ArrayList();
    private List<Map<String, Object>> list5 = new ArrayList();

    /**
     * doc最后结果
     */
    private static HashMap<String, Object> result = new HashMap<String, Object>();

    private void init() {
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

    private static void dealDate(HashMap<String, Object> map) {
        Date date = new Date();
        String date1 = DateUtil.getFormatDateTime(date,"yyyy年MM月dd日 HH:mm");
        String date2 = DateUtil.getFormatDateTime(date,"yyyy年MM月dd日");

        //文档所有日期
        map.put("date1", date1);
        map.put("date2", date2);
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
