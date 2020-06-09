package com.cisdi.steel.module.job.a1.doc;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.*;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Vector;

/**
 * Jfreechart工具类
 * <p>
 * 解决中文乱码问题<br>
 * 用来创建类别图表数据集、创建饼图数据集、时间序列图数据集<br>
 * 用来对柱状图、折线图、饼图、堆积柱状图、时间序列图的样式进行渲染<br>
 * 设置X-Y坐标轴样式
 * <p>
 *
 * @author chenchangwen
 * @since:2014-2-18
 */
@SuppressWarnings("ALL")
public class ChartUtils {
    private static String NO_DATA_MSG = "数据加载失败";
    private static Font FONT = new Font("宋体", Font.PLAIN, 12);
    public static Color[] CHART_COLORS = {new Color(31, 129, 188),
            new Color(92, 92, 97), new Color(144, 237, 125),
            new Color(255, 188, 117), new Color(153, 158, 255),
            new Color(255, 117, 153), new Color(253, 236, 109),
            new Color(128, 133, 232), new Color(158, 90, 102),
            new Color(255, 204, 102)};// 颜色

    static {
        setChartTheme();
    }

    /**
     * 中文主题样式 解决乱码
     */
    public static void setChartTheme() {
        // 设置中文主题样式 解决乱码
        StandardChartTheme chartTheme = new StandardChartTheme("CN");
        // 设置标题字体
        chartTheme.setExtraLargeFont(FONT);
        // 设置图例的字体
        chartTheme.setRegularFont(FONT);
        // 设置轴向的字体
        chartTheme.setLargeFont(FONT);
        chartTheme.setSmallFont(FONT);
        chartTheme.setTitlePaint(new Color(51, 51, 51));
        chartTheme.setSubtitlePaint(new Color(85, 85, 85));

        chartTheme.setLegendBackgroundPaint(Color.WHITE);// 设置标注
        chartTheme.setLegendItemPaint(Color.BLACK);//
        chartTheme.setChartBackgroundPaint(Color.WHITE);
        // 绘制颜色绘制颜色.轮廓供应商
        // paintSequence,outlinePaintSequence,strokeSequence,outlineStrokeSequence,shapeSequence

        Paint[] OUTLINE_PAINT_SEQUENCE = new Paint[]{Color.WHITE};
        // 绘制器颜色源
        DefaultDrawingSupplier drawingSupplier = new DefaultDrawingSupplier(
                CHART_COLORS, CHART_COLORS, OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
        chartTheme.setDrawingSupplier(drawingSupplier);

        chartTheme.setPlotBackgroundPaint(Color.WHITE);// 绘制区域
        chartTheme.setPlotOutlinePaint(Color.WHITE);// 绘制区域外边框
        chartTheme.setLabelLinkPaint(new Color(8, 55, 114));// 链接标签颜色
        chartTheme.setLabelLinkStyle(PieLabelLinkStyle.CUBIC_CURVE);

        chartTheme.setAxisOffset(new RectangleInsets(5, 12, 5, 12));
        chartTheme.setDomainGridlinePaint(new Color(192, 208, 224));// X坐标轴垂直网格颜色
        chartTheme.setRangeGridlinePaint(new Color(192, 192, 192));// Y坐标轴水平网格颜色

        chartTheme.setBaselinePaint(Color.WHITE);
        chartTheme.setCrosshairPaint(Color.BLUE);// 不确定含义
        chartTheme.setAxisLabelPaint(new Color(51, 51, 51));// 坐标轴标题文字颜色
        chartTheme.setTickLabelPaint(new Color(67, 67, 72));// 刻度数字
        chartTheme.setBarPainter(new StandardBarPainter());// 设置柱状图渲染
        chartTheme.setXYBarPainter(new StandardXYBarPainter());// XYBar 渲染

        chartTheme.setItemLabelPaint(Color.black);
        chartTheme.setThermometerPaint(Color.white);// 温度计

        ChartFactory.setChartTheme(chartTheme);
    }

    /**
     * 必须设置文本抗锯齿
     */
    public static void setAntiAlias(JFreeChart chart) {
        chart.setTextAntiAlias(false);

    }

    /**
     * 设置图例无边框，默认黑色边框
     */
    public static void setLegendEmptyBorder(JFreeChart chart) {
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));

    }

    /**
     * 设置标注是否显示
     *
     * @param chart
     * @param show
     */
    public static void setLegendShow(JFreeChart chart, int index, boolean show) {
        chart.getLegend(index).setVisible(show);
        chart.getLegend(index).setPosition(RectangleEdge.TOP);
    }

    /**
     * 创建类别数据集合
     */
    public static DefaultCategoryDataset createDefaultCategoryDataset(
            Vector<Serie> series, Object[] categories) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Serie serie : series) {
            String name = serie.getName();
            Vector<Object> data = serie.getData();
//            if (data != null && categories != null && data.size() == categories.length) {
            for (int index = 0; index < data.size(); index++) {
                String value = data.get(index) == null ? "" : data.get(index).toString();
                if (isPercent(value)) {
                    value = value.substring(0, value.length() - 1);
                    dataset.setValue(Double.parseDouble(value), name,
                            (String) categories[index]);
                } else if (isNumber(value)) {
                    dataset.setValue(Double.parseDouble(value), name,
                            (String) categories[index]);
                } else {
                    dataset.setValue(null, name, (String) categories[index]);
                }
            }
//            }

        }
        return dataset;

    }

    private static void dealRenderer(CategoryItemRenderer renderer, CategoryPlot plot) {
        if (renderer instanceof StackedBarRenderer) {
            renderer = (StackedBarRenderer) plot
                    .getRenderer();
        } else if (renderer instanceof LineAndShapeRenderer) {
            renderer = (LineAndShapeRenderer) plot
                    .getRenderer();
        }
    }

    /**
     * 设置折线图样式
     *
     * @param plot
     */
    @SuppressWarnings("deprecation")
    public static void setLineRender(CategoryPlot plot,
                                     CategoryLabelPositions positions,
                                     double[] rangStarts, double[] rangEnds, int y2,
                                     DefaultCategoryDataset[] datasets,String[] yLabels, int stack[], int[] ystack) {
        CategoryAxis categoryaxis = plot.getDomainAxis();//X轴
        categoryaxis.setCategoryLabelPositions(positions);
        categoryaxis.setMaximumCategoryLabelWidthRatio(5.0f);
        categoryaxis.setMaximumCategoryLabelLines(1);
        categoryaxis.setTickMarksVisible(true);
        categoryaxis.setCategoryLabelPositionOffset(10);
        categoryaxis.setTickLabelFont(com.cisdi.steel.module.job.a1.doc.ChartFactory.getSimsub());
        categoryaxis.setLabelFont(com.cisdi.steel.module.job.a1.doc.ChartFactory.getSimsub());
        categoryaxis.setCategoryMargin(0.4);

        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10, 10, 0, 10), false);

        Color[] colors = {Color.CYAN,Color.BLUE,Color.BLACK,Color.MAGENTA,Color.GREEN};
        for(int i=0; i<y2; i++){
            // 添加第i个Y轴
            NumberAxis axis = new NumberAxis();
            // 修改第i个Y轴的显示效果
            axis.setRange(rangStarts[i], rangEnds[i]);
            axis.setLabel(yLabels[i]);
            axis.setVisible(true);
            plot.setRangeAxis(i, axis);
            plot.setDataset(i, datasets[i]);
            plot.mapDatasetToRangeAxis(i, i);

            plot.getRangeAxis(i).setLabelFont(com.cisdi.steel.module.job.a1.doc.ChartFactory.getSimsub());

            // -- 修改第i条曲线显示效果
            CategoryItemRenderer renderer = new LineAndShapeRenderer();
            if (stack[i] == 2) {
                renderer = new StackedBarRenderer();
            }
            renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            renderer.setBaseItemLabelFont(com.cisdi.steel.module.job.a1.doc.ChartFactory.getSimsub().deriveFont(Font.ROMAN_BASELINE, 15));

            renderer.setSeriesPaint(0, colors[i]);
            renderer.setSeriesPaint(1, colors[i]);
            renderer.setSeriesPaint(2, colors[i]);
            plot.setRenderer(i, renderer);
        }

        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(112, 128, 144));
        plot.setRangeGridlinePaint(new Color(112, 128, 144));
        plot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setOutlineVisible(false);
        setXAixs(plot);
        setYAixs(plot, 0);
    }


    /**
     * 设置折线图样式
     *
     * @param plot
     */
    @SuppressWarnings("deprecation")
    public static void setLineRenderAndyVisible(CategoryPlot plot,
                                                CategoryLabelPositions positions,
                                                double[] rangStarts, double[] rangEnds, int y2, boolean[] isVisible,
                                                DefaultCategoryDataset[] datasets,String[] yLabels, int stack[], int[] ystack) {
        CategoryAxis categoryaxis = plot.getDomainAxis();//X轴
        categoryaxis.setCategoryLabelPositions(positions);
        categoryaxis.setMaximumCategoryLabelWidthRatio(5.0f);
        categoryaxis.setMaximumCategoryLabelLines(1);
        categoryaxis.setTickMarksVisible(true);
        categoryaxis.setCategoryLabelPositionOffset(10);
        categoryaxis.setTickLabelFont(com.cisdi.steel.module.job.a1.doc.ChartFactory.getSimsub());
        categoryaxis.setLabelFont(com.cisdi.steel.module.job.a1.doc.ChartFactory.getSimsub());
        categoryaxis.setCategoryMargin(0.4);

        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10, 10, 0, 10), false);

        Color[] colors = {Color.CYAN,Color.BLUE,Color.BLACK,Color.MAGENTA,Color.GREEN};
        for(int i=0; i<y2; i++){
            // 添加第i个Y轴
            NumberAxis axis = new NumberAxis();
            // 修改第i个Y轴的显示效果
            axis.setRange(rangStarts[i], rangEnds[i]);
            axis.setLabel(yLabels[i]);
            axis.setVisible(isVisible[i]);
            plot.setRangeAxis(i, axis);
            plot.setDataset(i, datasets[i]);
            plot.mapDatasetToRangeAxis(i, i);

            plot.getRangeAxis(i).setLabelFont(com.cisdi.steel.module.job.a1.doc.ChartFactory.getSimsub());

            // -- 修改第i条曲线显示效果
            CategoryItemRenderer renderer = new LineAndShapeRenderer();
            if (stack[i] == 2) {
                renderer = new StackedBarRenderer();
            }
            renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            renderer.setBaseItemLabelFont(com.cisdi.steel.module.job.a1.doc.ChartFactory.getSimsub().deriveFont(Font.ROMAN_BASELINE, 15));

            renderer.setSeriesPaint(0, colors[i]);
            renderer.setSeriesPaint(1, colors[i]);
            renderer.setSeriesPaint(2, colors[i]);
            plot.setRenderer(i, renderer);
        }

        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(112, 128, 144));
        plot.setRangeGridlinePaint(new Color(112, 128, 144));
        plot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setOutlineVisible(false);
        setXAixs(plot);
        setYAixs(plot, 0);
    }

    /**
     * 设置柱状图渲染
     *
     * @param plot
     * @param isShowDataLabels
     */
    public static void setBarRenderer(CategoryPlot plot,
                                      boolean isShowDataLabels) {

        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10, 10, 5, 10));
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setMaximumBarWidth(0.075);// 设置柱子最大宽度

        if (isShowDataLabels) {
            renderer.setBaseItemLabelsVisible(true);
        }

        setXAixs(plot);
//        setYAixs(plot, 0);
    }


    /**
     * 设置类别图表(CategoryPlot) X坐标轴线条颜色和样式
     */
    public static void setXAixs(CategoryPlot plot) {
        Color lineColor = new Color(31, 121, 170);
        plot.getDomainAxis().setAxisLinePaint(lineColor);// X坐标轴颜色
        plot.getDomainAxis().setTickMarkPaint(lineColor);// X坐标轴标记|竖线颜色
    }

    /**
     * 设置类别图表(CategoryPlot) Y坐标轴线条颜色和样式 同时防止数据无法显示
     */
    public static void setYAixs(CategoryPlot plot, int index) {
        Color lineColor = new Color(31, 121, 170);
        ValueAxis axis = plot.getRangeAxis(index);
        axis.setAxisLinePaint(lineColor);// Y坐标轴颜色
        axis.setTickMarkPaint(lineColor);// Y坐标轴标记|竖线颜色
        // 隐藏Y刻度
        axis.setAxisLineVisible(true);
        axis.setTickMarksVisible(true);
        // Y轴网格线条
        plot.setRangeGridlinePaint(new Color(192, 192, 192));
        plot.setRangeGridlineStroke(new BasicStroke(1));

//        plot.getRangeAxis().setUpperMargin(0.1);// 设置顶部Y坐标轴间距,防止数据无法显示
//        plot.getRangeAxis().setLowerMargin(0.1);// 设置底部Y坐标轴间距

    }


    /**
     * 是不是一个%形式的百分比
     *
     * @param str
     * @return
     */
    public static boolean isPercent(String str) {
        return str != null ? str.endsWith("%")
                && isNumber(str.substring(0, str.length() - 1)) : false;
    }

    /**
     * 是不是一个数字
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        return str != null ? str.matches("^[-+]?(([0-9]+)((([.]{0})([0-9]*))|(([.]{1})([0-9]+))))$") : false;
    }

}
