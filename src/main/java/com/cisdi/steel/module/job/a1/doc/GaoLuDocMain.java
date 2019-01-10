package com.cisdi.steel.module.job.a1.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.WordImageEntity;
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

    public void mainTask() {
        mainDeal(version8);
        log.error("高炉word生成完毕！");
    }

    public void mainDeal(String version) {
        part1(version);
        if ("6.0".equals(version)) {
//            comm(jobProperties.getTemplatePath() + File.separator + "doc" + File.separator + "五烧每日操业会-设计版v1.docx");
        } else {
            comm(jobProperties.getTemplatePath() + File.separator + "doc" + File.separator + "八高炉操业会议纪要（实施版）.docx");
        }
    }

    /**
     * doc最后结果
     */
    private static HashMap<String, Object> result = new HashMap<String, Object>();


    private String[] L1 = new String[]{"BF8_L2C_BD_ProductionSum_1d_cur", "BF8_L2C_BD_CokeRate_1d_avg", "BF8_L2M_FuelRate_1d_avg"};

    private void part1(String version) {
        Date date = new Date();
        Date beginDate = DateUtil.addDays(date, -30);
        Date start = beginDate;

        List<List<Double>> doubles = new ArrayList<>();
        List<String> categoriesList = new ArrayList<>();

        List<String> dateList = new ArrayList<>();
        while (beginDate.before(date)) {
            categoriesList.add(DateUtil.getFormatDateTime(beginDate, "MM月dd日"));
            dateList.add(DateUtil.getFormatDateTime(beginDate, DateUtil.yyyyMMddFormat));
            beginDate = DateUtil.addDays(beginDate, 1);
        }


        JSONObject jsonObject = dataHttp(L1, start, date, version);
        if (Objects.nonNull(jsonObject)) {
            for (String tagName : L1) {
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
        Object[] objects1 = doubles.get(0).toArray();
        Object[] objects2 = doubles.get(1).toArray();
        Object[] objects3 = doubles.get(2).toArray();

        /**
         * 产量BF8_L2C_BD_ProductionSum_1d_cur
         * 焦比BF8_L2C_BD_CokeRate_1d_avg
         * 燃料比BF8_L2M_FuelRate_1d_avg
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
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, 450, 650, 160, 220, true);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg1", image1);

//        try {
//            File file = new File("D:\\tetstet.jpeg");
//            int width = 1024;
//            int height = 420;
//            ChartUtilities.saveChartAsJPEG(file, Chart1, width, height);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

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

    private String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
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
