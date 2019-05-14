package com.cisdi.steel.module.job.a1.doc;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * 生成JFreeChart图表的工厂类<br/>
 * 目的：根据MVC的设计思想，数据与展现分离。调用者只需传入数据，即可生成图表。
 *
 * @author liuyimin
 */
public class ChartFactory {


    public static JFreeChart createBarChart(String title,
                                            String categoryAxisLabel, String valueAxisLabel,
                                            Vector<Serie> series, String[] categories) {
        // 1：创建数据集合
        DefaultCategoryDataset dataset = ChartUtils
                .createDefaultCategoryDataset(series, categories);
        JFreeChart chart = org.jfree.chart.ChartFactory.createBarChart(title,
                categoryAxisLabel, valueAxisLabel, dataset);
        // 3:设置抗锯齿，防止字体显示不清楚
        ChartUtils.setAntiAlias(chart);// 抗锯齿
        // 4:对柱子进行渲染
        ChartUtils.setBarRenderer(chart.getCategoryPlot(), false);//
        // 5:对其他部分进行渲染
        ChartUtils.setXAixs(chart.getCategoryPlot());// X坐标轴渲染
        ChartUtils.setYAixs(chart.getCategoryPlot(), 0);// Y坐标轴渲染
        // 设置标注无边框
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
        return chart;
    }


    /**
     * 生成折线图
     *
     * @param title             折线图的标题
     * @param categoryAxisLabel x轴标题
     * @param valueAxisLabel    y轴标题
     * @param series            数据
     * @param categories        类别
     * @return
     */
    public static JFreeChart createLineChart(String title,
                                             String categoryAxisLabel, String valueAxisLabel,
                                             List<Vector<Serie>> series, Object[] categories, CategoryLabelPositions positions, boolean show,
                                             double rangIndex, double rangEnd, double rangIndex2, double rangEnd2, double rangIndex3, double rangEnd3, int y2, int[] stack, int[] ystack) {
        // 1：创建数据集合
        DefaultCategoryDataset dataset = ChartUtils
                .createDefaultCategoryDataset(series.get(0), categories);
        DefaultCategoryDataset dataset2 = null;
        DefaultCategoryDataset dataset3 = null;
        DefaultCategoryDataset dataset4 = null;
        DefaultCategoryDataset dataset5 = null;


        JFreeChart chart = org.jfree.chart.ChartFactory.createLineChart(title, categoryAxisLabel, valueAxisLabel, dataset);
        if (stack[0] == 2) {
            chart = org.jfree.chart.ChartFactory.createStackedBarChart(title, categoryAxisLabel, valueAxisLabel, dataset);
        }

        if (y2 == 2) {
            dataset2 = ChartUtils
                    .createDefaultCategoryDataset(series.get(1), categories);
        } else if (y2 == 3) {
            dataset2 = ChartUtils
                    .createDefaultCategoryDataset(series.get(1), categories);
            dataset3 = ChartUtils
                    .createDefaultCategoryDataset(series.get(2), categories);
        } else if (y2 == 4) {
            dataset2 = ChartUtils
                    .createDefaultCategoryDataset(series.get(1), categories);
            dataset3 = ChartUtils
                    .createDefaultCategoryDataset(series.get(2), categories);

            dataset4 = ChartUtils
                    .createDefaultCategoryDataset(series.get(3), categories);
        } else if (y2 == 5) {
            dataset2 = ChartUtils
                    .createDefaultCategoryDataset(series.get(1), categories);
            dataset3 = ChartUtils
                    .createDefaultCategoryDataset(series.get(2), categories);

            dataset4 = ChartUtils
                    .createDefaultCategoryDataset(series.get(3), categories);

            dataset5 = ChartUtils
                    .createDefaultCategoryDataset(series.get(4), categories);
        }

        LegendTitle legend = chart.getLegend(); // 设置图例的字体
        legend.setItemFont(new Font("宋体", Font.ROMAN_BASELINE, 13));

        chart.setBorderVisible(false);
        // 3:设置抗锯齿，防止字体显示不清楚
        ChartUtils.setAntiAlias(chart);// 抗锯齿
        // 4:对柱子进行渲染[[采用不同渲染]]
        for (int i = 0; i < categories.length; i++) {
            Object s = categories[i];
            if (i % 2 == 0) {
                chart.getCategoryPlot().getDomainAxis().setTickLabelPaint(s.toString(), Color.black);
            } else {
                // 设置背景色为白色
                chart.getCategoryPlot().getDomainAxis().setTickLabelPaint(s.toString(), Color.white);
            }
        }
        ChartUtils.setLineRender(chart.getCategoryPlot(), false, false, positions, rangIndex, rangEnd, rangIndex2, rangEnd2, rangIndex3, rangEnd3, y2, dataset2, dataset3, dataset4, dataset5, stack, ystack);//
        // 5:对其他部分进行渲染
        ChartUtils.setXAixs(chart.getCategoryPlot());// X坐标轴渲染
        ChartUtils.setYAixs(chart.getCategoryPlot(), 0);// Y坐标轴渲染
        // 设置标注无边框
//        ChartUtils.setLegendEmptyBorder(chart);
        //设置标注不显示
        chart.setTextAntiAlias(false);

        ChartUtils.setLegendShow(chart, 0, show);
        return chart;
    }
}
