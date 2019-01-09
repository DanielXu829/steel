package com.cisdi.steel.module.job.a1.doc;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PolarAxisLocation;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

import java.awt.*;
import java.util.Vector;

/**
 * 生成JFreeChart图表的工厂类<br/>
 * 目的：根据MVC的设计思想，数据与展现分离。调用者只需传入数据，即可生成图表。
 *
 * @author liuyimin
 */
public class ChartFactory {




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
                                             Vector<Serie> series, Vector<Serie> series2, String[] categories, CategoryLabelPositions positions, boolean show, int rangIndex, int rangEnd, int rangIndex2, int rangEnd2, boolean y2) {
        // 1：创建数据集合
        DefaultCategoryDataset dataset = ChartUtils
                .createDefaultCategoryDataset(series, categories);
        DefaultCategoryDataset dataset2 = null;

        if (y2) {
            dataset2 = ChartUtils
                    .createDefaultCategoryDataset(series2, categories);
        }

        JFreeChart chart = org.jfree.chart.ChartFactory.createLineChart(title, categoryAxisLabel, valueAxisLabel, dataset);

        chart.setBorderVisible(false);
        // 3:设置抗锯齿，防止字体显示不清楚
        ChartUtils.setAntiAlias(chart);// 抗锯齿
        // 4:对柱子进行渲染[[采用不同渲染]]
        ChartUtils.setLineRender(chart.getCategoryPlot(), false, false, positions, rangIndex, rangEnd, rangIndex2, rangEnd2, y2, dataset2);//
        // 5:对其他部分进行渲染
        ChartUtils.setXAixs(chart.getCategoryPlot());// X坐标轴渲染
        ChartUtils.setYAixs(chart.getCategoryPlot(), 0);// Y坐标轴渲染
        // 设置标注无边框
//        ChartUtils.setLegendEmptyBorder(chart);
        //设置标注不显示


        ChartUtils.setLegendShow(chart, 0, show);
        return chart;
    }
}
