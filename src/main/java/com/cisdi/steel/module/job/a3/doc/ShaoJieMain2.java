package com.cisdi.steel.module.job.a3.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.WordImageEntity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.a1.doc.ChartFactory;
import com.cisdi.steel.module.job.a1.doc.Serie;
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
public class ShaoJieMain2 {

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private HttpProperties httpProperties;

    @Autowired
    private JobProperties jobProperties;

    private String version6 = "5.0";

    public void mainTask() {
        result = new HashMap<>();
        mainDeal(version6);
        log.error("烧结word2生成完毕！");
    }

    public void mainDeal(String version) {
        String name = "5";
        dealTagName6();
        dealPart1(version, L1);
        comm(version, jobProperties.getTemplatePath() + File.separator + "doc" + File.separator + name + "烧结DCS曲线.docx");
    }

    /**
     * doc最后结果
     */
    private HashMap<String, Object> result = null;

    private void dealTagName6() {
        L1 = new String[]{"ST5_L1R_DeSN_LsSpUpTraffic_1m_avg"};
    }


    private String[] L1 = new String[]{"ST5_L1R_DeSN_LsSpUpTraffic_1m_avg"};


    List<String> categoriesList = new ArrayList<>();
    List<String> dateList = new ArrayList<>();

    private List<List<Double>> part1(String version, String[] tagNames, int scale) {

        categoriesList.clear();
        dateList.clear();

        Date date = new Date();
        date = DateUtil.strToDate("2019-05-31", "yyyy-MM-dd");

        Date date1 = DateUtil.addDays(date, 1);
        Date beginDate = DateUtil.addDays(date1, -7);
        Date start = beginDate;

        List<List<Double>> doubles = new ArrayList<>();

        while (beginDate.before(date1)) {
            categoriesList.add(DateUtil.getFormatDateTime(beginDate, "yyyy年MM月dd日HH时mm分"));
            dateList.add(DateUtil.getFormatDateTime(beginDate, "yyyy-MM-dd HH:mm:00"));
            beginDate = DateUtil.addMinute(beginDate, 1);
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
                            String fomDate = DateUtil.getFormatDateTime(dd, "yyyy-MM-dd HH:mm:00");
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

    private void dealPart1(String version, String[] tagNames) {
        List<List<Double>> doubles = part1(version, tagNames, 1);
        Object[] objects1 = doubles.get(0).toArray();
        result.put("part1", getLastVal(objects1));
        List<Double> data = dealList(objects1);
        Double max1 = data.get(0) * 1.01;
        Double min1 = data.get(1) * 0.999;

        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("低硫上层流量", objects1));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);

        String title1 = "";
        String categoryAxisLabel1 = null;
        String[] yLabels = {"",""};

        int[] stack = {1};
        int[] ystack = {1};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, 0, 1, 300, 600, tagNames.length, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg1", image1);
    }

    private JSONObject dataHttp(String[] tagNames, Date beginDate, Date endDate, String version) {
        JSONObject query = new JSONObject();
        query.put("start", beginDate.getTime());
        query.put("end", endDate.getTime());
        query.put("tagNames", tagNames);
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String results = httpUtil.postJsonParams(getUrl(version), jsonString);
        JSONObject jsonObject = JSONObject.parseObject(results);
        JSONObject data = jsonObject.getJSONObject("data");
        return data;
    }

    private void comm(String version, String path) {
        //文档所有日期
        dealDate(result);
        try {
            String sqquence = "5烧结";
            XWPFDocument doc = WordExportUtil.exportWord07(path, result);
            String fileName = sqquence + DateUtil.getFormatDateTime(new Date(), "yyyyMMdd") + "DCS曲线.docx";
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

    /**
     * tag点名
     *
     * @param version
     * @return
     */
    private String getUrl(String version) {
        String s = httpProperties.getUrlApiSJOne() + "/tagValues/tagNames";
        if ("5.0".equals(version)) {
            s = httpProperties.getUrlApiSJOne() + "/tagValues/tagNames";
        }
        return s;
    }

    private WordImageEntity image(JFreeChart chart) {
        WordImageEntity image = new WordImageEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            chart.getPlot().setBackgroundAlpha(0.1f);
            chart.getPlot().setNoDataMessage("当前没有有效的数据");
            ChartUtilities.writeChartAsJPEG(baos, chart, 900, 300);
            image.setHeight(350);
            image.setWidth(950);
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
