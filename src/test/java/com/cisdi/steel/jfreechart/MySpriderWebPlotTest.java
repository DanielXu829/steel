package com.cisdi.steel.jfreechart;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.Range;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MySpriderWebPlotTest {
    public static JFreeChart createDemoPanel() {
        JFreeChart localJFreeChart = createChart(createDataset());
//        localJFreeChart.setBackgroundPaint(Color.BLUE);// 修改顶层的背景色
//        localJFreeChart.getPlot().setBackgroundPaint(Color.RED);// 修改前景色
        return localJFreeChart;
    }


    public static JFreeChart createChart(DefaultCategoryDataset dataset) {
        MySpiderWebPlot spiderwebplot = new MySpiderWebPlot(dataset);
        JFreeChart jfreechart = new JFreeChart("前三个季度水果销售报告", TextTitle.DEFAULT_FONT, spiderwebplot, false);
        LegendTitle legendtitle = new LegendTitle(spiderwebplot);
        legendtitle.setPosition(RectangleEdge.BOTTOM);
        jfreechart.addSubtitle(legendtitle);
        MySpiderWebPlot plot = (MySpiderWebPlot) jfreechart.getPlot();
        plot.setBackgroundPaint(Color.white);

        return jfreechart;
    }

    public static DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String group1 = "苹果 ";

        dataset.addValue(5, group1, "一月份");
        dataset.addValue(6, group1, "二月份");
        dataset.addValue(4, group1, "三月份");
        dataset.addValue(2, group1, "四月份");
        dataset.addValue(5, group1, "五月份");
        dataset.addValue(5, group1, "六月份");
        dataset.addValue(5, group1, "七月份");
        dataset.addValue(8, group1, "八月份");

//        String group2 = "橙子";
//        dataset.addValue(3, group2, "一月份");
//        dataset.addValue(3, group2, "二月份");
//        dataset.addValue(4, group2, "三月份");
//        dataset.addValue(7, group2, "四月份");
//        dataset.addValue(4, group2, "五月份");
//        dataset.addValue(5, group2, "六月份");
//        dataset.addValue(3, group2, "七月份");
//        dataset.addValue(3, group2, "八月份");
//
//        String group3 = "香蕉";
//        dataset.addValue(4, group3, "一月份");
//        dataset.addValue(5, group3, "二月份");
//        dataset.addValue(2, group3, "三月份");
//        dataset.addValue(5, group3, "四月份");
//        dataset.addValue(6, group3, "五月份");
//        dataset.addValue(6, group3, "六月份");
//        dataset.addValue(4, group3, "七月份");
//        dataset.addValue(4, group3, "八月份");
        return dataset;
    }
}
