package com.cisdi.steel.module.job.a1.doc;

import com.cisdi.steel.common.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

/**
 * 生成JFreeChart图表的工厂类<br/>
 * 目的：根据MVC的设计思想，数据与展现分离。调用者只需传入数据，即可生成图表。
 *
 * @author liuyimin
 */
@Slf4j
public class ChartFactory {

    private static Font simsun;

    static{
        try (InputStream is = ChartFactory.class.getClassLoader().getResourceAsStream("config/simsun.ttf");){
            simsun = Font.createFont(Font.TRUETYPE_FONT, is);
            simsun = simsun.deriveFont(Font.ROMAN_BASELINE, 13);
        } catch (Exception e){
            // nothing
            log.error("初始化字体库宋体失败", e);
        }

    }

    public static Font getSimsub(){
        return simsun;
    }


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
     * @param yLabels    y轴标题
     * @param series            数据
     * @param categories        类别
     * @param batch        每隔多少个显示X轴的点
     * @return
     */
    public static JFreeChart createLineChartEachBatch(int batch ,String title,
                                                      String categoryAxisLabel, String[] yLabels,
                                                      List<Vector<Serie>> series, Object[] categories, CategoryLabelPositions positions, boolean show,
                                                      double[] rangStarts, double[] rangEnds, int y2, int[] stack, int[] ystack) {
        // 1：创建数据集合
        DefaultCategoryDataset dataset = ChartUtils
                .createDefaultCategoryDataset(series.get(0), categories);
        DefaultCategoryDataset dataset2 = null;
        DefaultCategoryDataset dataset3 = null;
        DefaultCategoryDataset dataset4 = null;
        DefaultCategoryDataset dataset5 = null;

        JFreeChart chart = org.jfree.chart.ChartFactory.createLineChart(title, categoryAxisLabel, null,null);
        if (stack[0] == 2) {
            chart = org.jfree.chart.ChartFactory.createStackedBarChart(title, categoryAxisLabel, null,null);
        }

        if (y2 >= 2) {
            dataset2 = ChartUtils.createDefaultCategoryDataset(series.get(1), categories);
        }
        if (y2 >= 3) {
            dataset3 = ChartUtils.createDefaultCategoryDataset(series.get(2), categories);
        }
        if (y2 >= 4) {
            dataset4 = ChartUtils.createDefaultCategoryDataset(series.get(3), categories);
        }
        if (y2 >= 5) {
            dataset5 = ChartUtils.createDefaultCategoryDataset(series.get(4), categories);
        }

        LegendTitle legend = chart.getLegend(); // 设置图例的字体
        legend.setItemFont(simsun);

        chart.setBorderVisible(false);
        // 3:设置抗锯齿，防止字体显示不清楚
        ChartUtils.setAntiAlias(chart);// 抗锯齿
        // 4:对柱子进行渲染[[采用不同渲染]]
        //int batch = 2;
        if (categories.length > 1000) {
            batch = 2000;
        }
        for (int i = 0; i < categories.length; i++) {
            Object s = categories[i];
            if (i % batch == 0) {
                chart.getCategoryPlot().getDomainAxis().setTickLabelPaint(s.toString(), Color.black);
            } else {
                // 设置背景色为白色
                chart.getCategoryPlot().getDomainAxis().setTickLabelPaint(s.toString(), Color.white);
            }
        }
        //double[] rangStarts = {rangIndex,rangIndex2,rangIndex3};
        //double[] rangEnds = {rangEnd,rangEnd2,rangEnd3};
        DefaultCategoryDataset[] datasets = {dataset,dataset2,dataset3,dataset4,dataset5};
        ChartUtils.setLineRenderAndyVisible(chart.getCategoryPlot(), positions, rangStarts, rangEnds, y2, new boolean[]{true,false,false,false}, datasets, yLabels, stack, ystack);
        // 5:对其他部分进行渲染
        ChartUtils.setXAixs(chart.getCategoryPlot());// X坐标轴渲染
//        ChartUtils.setYAixs(chart.getCategoryPlot(), 0);// Y坐标轴渲染
        // 设置标注无边框
//        ChartUtils.setLegendEmptyBorder(chart);
        //设置标注不显示
        chart.setTextAntiAlias(false);

        ChartUtils.setLegendShow(chart, 0, show);
        return chart;
    }


    /**
     * 生成折线图
     *
     * @param title             折线图的标题
     * @param categoryAxisLabel x轴标题
     * @param yLabels    y轴标题
     * @param series            数据
     * @param categories        类别
     * @return
     */
    public static JFreeChart createLineChart(String title,
                                             String categoryAxisLabel, String[] yLabels,
                                             List<Vector<Serie>> series, Object[] categories, CategoryLabelPositions positions, boolean show,
                                             double rangIndex, double rangEnd, double rangIndex2, double rangEnd2, double rangIndex3, double rangEnd3, int y2, int[] stack, int[] ystack) {
        // 1：创建数据集合
        DefaultCategoryDataset dataset = ChartUtils
                .createDefaultCategoryDataset(series.get(0), categories);
        DefaultCategoryDataset dataset2 = null;
        DefaultCategoryDataset dataset3 = null;
        DefaultCategoryDataset dataset4 = null;
        DefaultCategoryDataset dataset5 = null;

        JFreeChart chart = org.jfree.chart.ChartFactory.createLineChart(title, categoryAxisLabel, null,null);
        if (stack[0] == 2) {
            chart = org.jfree.chart.ChartFactory.createStackedBarChart(title, categoryAxisLabel, null,null);
        }

        if (y2 >= 2) {
            dataset2 = ChartUtils.createDefaultCategoryDataset(series.get(1), categories);
        }
        if (y2 >= 3) {
            dataset3 = ChartUtils.createDefaultCategoryDataset(series.get(2), categories);
        }
        if (y2 >= 4) {
            dataset4 = ChartUtils.createDefaultCategoryDataset(series.get(3), categories);
        }
        if (y2 >= 5) {
            dataset5 = ChartUtils.createDefaultCategoryDataset(series.get(4), categories);
        }

        LegendTitle legend = chart.getLegend(); // 设置图例的字体
        legend.setItemFont(simsun);

        chart.setBorderVisible(false);
        // 3:设置抗锯齿，防止字体显示不清楚
        ChartUtils.setAntiAlias(chart);// 抗锯齿
        // 4:对柱子进行渲染[[采用不同渲染]]
        int batch = 2;
        if (categories.length > 1000) {
            batch = 2000;
        }
        for (int i = 0; i < categories.length; i++) {
            Object s = categories[i];
            if (i % batch == 0) {
                chart.getCategoryPlot().getDomainAxis().setTickLabelPaint(s.toString(), Color.black);
            } else {
                // 设置背景色为白色
                chart.getCategoryPlot().getDomainAxis().setTickLabelPaint(s.toString(), Color.white);
            }
        }
        double[] rangStarts = {rangIndex,rangIndex2,rangIndex3};
        double[] rangEnds = {rangEnd,rangEnd2,rangEnd3};
        DefaultCategoryDataset[] datasets = {dataset,dataset2,dataset3,dataset4,dataset5};
        ChartUtils.setLineRender(chart.getCategoryPlot(), positions, rangStarts, rangEnds, y2, datasets, yLabels, stack, ystack);
        // 5:对其他部分进行渲染
        ChartUtils.setXAixs(chart.getCategoryPlot());// X坐标轴渲染
//        ChartUtils.setYAixs(chart.getCategoryPlot(), 0);// Y坐标轴渲染
        // 设置标注无边框
//        ChartUtils.setLegendEmptyBorder(chart);
        //设置标注不显示
        chart.setTextAntiAlias(false);

        ChartUtils.setLegendShow(chart, 0, show);
        return chart;
    }

    /**
     * 生成折线图
     *
     * @param title             折线图的标题
     * @param categoryAxisLabel x轴标题
     * @param yLabels    y轴标题
     * @param series            数据
     * @param categories        类别
     * @return
     */
    public static JFreeChart createLineChart(String title,
                                             String categoryAxisLabel, String[] yLabels,
                                             List<Vector<Serie>> series, Object[] categories, CategoryLabelPositions positions, boolean show,
                                             double rangIndex, double rangEnd, double rangIndex2, double rangEnd2, double rangIndex3, double rangEnd3, double rangIndex4, double rangEnd4, int y2, int[] stack, int[] ystack) {
        // 1：创建数据集合
        DefaultCategoryDataset dataset = ChartUtils
                .createDefaultCategoryDataset(series.get(0), categories);
        DefaultCategoryDataset dataset2 = null;
        DefaultCategoryDataset dataset3 = null;
        DefaultCategoryDataset dataset4 = null;
        DefaultCategoryDataset dataset5 = null;

        JFreeChart chart = org.jfree.chart.ChartFactory.createLineChart(title, categoryAxisLabel, null,null);
        if (stack[0] == 2) {
            chart = org.jfree.chart.ChartFactory.createStackedBarChart(title, categoryAxisLabel, null,null);
        }

        if (y2 >= 2) {
            dataset2 = ChartUtils.createDefaultCategoryDataset(series.get(1), categories);
        }
        if (y2 >= 3) {
            dataset3 = ChartUtils.createDefaultCategoryDataset(series.get(2), categories);
        }
        if (y2 >= 4) {
            dataset4 = ChartUtils.createDefaultCategoryDataset(series.get(3), categories);
        }
        if (y2 >= 5) {
            dataset5 = ChartUtils.createDefaultCategoryDataset(series.get(4), categories);
        }

        LegendTitle legend = chart.getLegend(); // 设置图例的字体
        legend.setItemFont(simsun);

        chart.setBorderVisible(false);
        // 3:设置抗锯齿，防止字体显示不清楚
        ChartUtils.setAntiAlias(chart);// 抗锯齿
        // 4:对柱子进行渲染[[采用不同渲染]]
        int batch = 2;
        if (categories.length > 1000) {
            batch = 2000;
        }
        for (int i = 0; i < categories.length; i++) {
            Object s = categories[i];
            if (i % batch == 0) {
                chart.getCategoryPlot().getDomainAxis().setTickLabelPaint(s.toString(), Color.white);
            } else {
                // 设置背景色为白色
                chart.getCategoryPlot().getDomainAxis().setTickLabelPaint(s.toString(), Color.black);
            }
        }
        double[] rangStarts = {rangIndex,rangIndex2,rangIndex3,rangIndex4};
        double[] rangEnds = {rangEnd,rangEnd2,rangEnd3,rangEnd4};
        DefaultCategoryDataset[] datasets = {dataset,dataset2,dataset3,dataset4,dataset5};
        ChartUtils.setLineRender(chart.getCategoryPlot(), positions, rangStarts, rangEnds, y2, datasets, yLabels, stack, ystack);
        // 5:对其他部分进行渲染
        ChartUtils.setXAixs(chart.getCategoryPlot());// X坐标轴渲染
//        ChartUtils.setYAixs(chart.getCategoryPlot(), 0);// Y坐标轴渲染
        // 设置标注无边框
//        ChartUtils.setLegendEmptyBorder(chart);
        //设置标注不显示
        chart.setTextAntiAlias(false);

        ChartUtils.setLegendShow(chart, 0, show);
        return chart;
    }
}
