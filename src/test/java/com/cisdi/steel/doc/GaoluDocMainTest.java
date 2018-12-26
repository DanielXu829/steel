package com.cisdi.steel.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.WordImageEntity;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.jfreechart.ChartFactory;
import com.cisdi.steel.jfreechart.Serie;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;

public class GaoluDocMainTest {
    private static List list = new ArrayList();
    private static List list1 = new ArrayList();
    private static List list2 = new ArrayList();
    private static List list3 = new ArrayList();
    private static List list4 = new ArrayList();
    private static List list5 = new ArrayList();


    static {


    }

    public static void main(String[] args) throws Exception {
        Date date = new Date();
        String date1 = DateUtil.getFormatDateTime(DateUtil.addDays(date, -2), DateUtil.MMddChineseFormat);
        String date2 = DateUtil.getFormatDateTime(DateUtil.addDays(date, -1), DateUtil.MMddChineseFormat);
        String date3 = DateUtil.getFormatDateTime(date, DateUtil.MMddChineseFormat);
        String date4 = DateUtil.getFormatDateTime(DateUtil.addDays(date, 1), DateUtil.MMddChineseFormat);
        String date5 = DateUtil.getFormatDateTime(date, DateUtil.yyyyMMddChineseFormat);
        String date6 = DateUtil.getFormatDateTime(date, "dd日");

        //doc结果
        HashMap<String, Object> map = new HashMap<String, Object>();

        //文档所有日期
        map.put("date1", date1);
        map.put("date2", date2);
        map.put("date3", date3);
        map.put("date4", date4);
        map.put("date5", date5);
        map.put("date6", date6);

        //文档表格简单数据
        map.put("sheet1", list);


        ////////////////////////////////////////////////////////////////
        List<DateQuery> dateQueries = DateQueryUtil.buildMonthDayEach(new Date());
        String[] categories = new String[dateQueries.size()];
        for (int i = 0; i < dateQueries.size(); i++) {
            categories[i] = (DateUtil.getFormatDateTime(dateQueries.get(i).getStartTime(), "yyyy/MM/dd"));
        }
        // 标注类别
        Vector<Serie> series = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series.add(new Serie("", new Double[]{
                12500.0, 12500.0, 7000.0, 12200.0,
                12600.0, 11800.0, 6000.0, null,
                null, 3000.0, 11900.0, 11980.0,
                9000.0, 12100.0, 12300.0, 8500.0,
                12100.0, 11900.0, 12000.0, 11900.0,
                11800.0, 12000.0, 11500.0, 11000.0,
                0.0, 0.0
        }));
        String title = "铁水产量";
        String categoryAxisLabel = null;
        String valueAxisLabel = null;
        JFreeChart Chart = ChartFactory.createLineChart(title,
                categoryAxisLabel, valueAxisLabel, series, categories);

        ///////////////////////////////////////////////////////////////
        Chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        Chart.getPlot().setBackgroundAlpha(0.1f);
        Chart.getPlot().setNoDataMessage("当前没有有效的数据");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtilities.writeChartAsJPEG(baos, Chart, 600, 300);

        WordImageEntity image = new WordImageEntity();
        image.setHeight(350);
        image.setWidth(650);
        image.setData(baos.toByteArray());
        image.setType(WordImageEntity.Data);
        map.put("jfreechartImg", image);


        String path = "E://8#高炉8月智能化诊断报告-模板v1.docx";
        try {
            XWPFDocument doc = WordExportUtil.exportWord07(path, map);
            String fileName = "8#高炉" + DateUtil.getFormatDateTime(date, "MM月") + "智能化诊断报告-模板v1.docx";
            FileOutputStream fos = new FileOutputStream("D://" + fileName);
            doc.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
