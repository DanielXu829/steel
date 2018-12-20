package com.cisdi.steel.doc;

import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYZDataset;
import org.jfree.util.TableOrder;

public class ChartCreater {
	
	private static void init() {
		//创建主题样式  
        StandardChartTheme standardChartTheme=new StandardChartTheme("CN");  
        //设置标题字体  
        standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20));  
        //设置图例的字体  
        standardChartTheme.setRegularFont(new Font("宋书",Font.PLAIN,15));  
        //设置轴向的字体  
        standardChartTheme.setLargeFont(new Font("宋书",Font.PLAIN,15));  
        //应用主题样式  
        ChartFactory.setChartTheme(standardChartTheme);
	}
	
	/** */  
    /** 
     * 2D柱图 
     *  
     * @return 
     */  
    public static JFreeChart createCategoryChart() {  
    	
    	init();
  
        JFreeChart chart = ChartFactory.createBarChart("Bar2D", // 图表名称  
                "Category", // X轴名称  
                "Value", // Y轴名称  
                ChartDataSet.createCategoryDataset(),  
                PlotOrientation.VERTICAL, // 图表方向  
                true, // 图例  
                true, // Tooltips  
                false); // URL  
  
        CategoryPlot plot = (CategoryPlot) chart.getPlot();  
  
        setCategoryPlot(plot);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 3D柱图 
     *  
     * @return 
     */  
    public static JFreeChart createCategoryChart3D() { 
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createBarChart3D("图片", "Category",  
                "Value", ChartDataSet.createCategoryDataset(),  
                PlotOrientation.VERTICAL, true, true, false);  
  
        CategoryPlot plot = (CategoryPlot) chart.getPlot();  
  
        setCategoryPlot(plot);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 2D条形图 
     *  
     * @return 
     */  
    public static JFreeChart createBarChart() {  
    	
    	init();
  
        JFreeChart chart = ChartFactory.createBarChart("Bar2D", "Category",  
                "Value", ChartDataSet.createCategoryDataset(),  
                PlotOrientation.HORIZONTAL, true, true, false);  
  
        CategoryPlot plot = (CategoryPlot) chart.getPlot();  
  
        setCategoryPlot(plot);  
  
        plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 3D条形图 
     *  
     * @return 
     */  
    public static JFreeChart createBarChart3D() {  
    	
    	init();
  
        JFreeChart chart = ChartFactory.createBarChart3D("Bar2D", "Category",  
                "Value", ChartDataSet.createCategoryDataset(),  
                PlotOrientation.HORIZONTAL, true, true, false);  
  
        CategoryPlot plot = (CategoryPlot) chart.getPlot();  
  
        setCategoryPlot(plot);  
  
        plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 2D线图 
     *  
     * @return 
     */  
    public static JFreeChart createLintChart() {  
  
        JFreeChart chart = ChartFactory.createLineChart("Bar2D", "Category",  
                "Value", ChartDataSet.createCategoryDataset(),  
                PlotOrientation.VERTICAL, true, true, false);  
  
        CategoryPlot plot = (CategoryPlot) chart.getPlot();  
  
        setCategoryPlot(plot);  
  
        LineAndShapeRenderer lineAndShapeRenderer = (LineAndShapeRenderer) plot  
                .getRenderer();  
        lineAndShapeRenderer.setItemLabelsVisible(true);  
        lineAndShapeRenderer.setShapesFilled(true);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 3D线图 
     *  
     * @return 
     */  
    public static JFreeChart createLintChart3D() {  
    	
    	init();
  
        JFreeChart chart = ChartFactory.createBarChart3D("Bar2D", "Category",  
                "Value", ChartDataSet.createCategoryDataset(),  
                PlotOrientation.VERTICAL, true, true, false);  
  
        CategoryPlot plot = (CategoryPlot) chart.getPlot();  
  
        setCategoryPlot(plot);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 面积图 
     *  
     * @return 
     */  
    public static JFreeChart createAreaChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createAreaChart("Area", "Category",  
                "Value", ChartDataSet.createCategoryDataset(),  
                PlotOrientation.VERTICAL, true, true, false);  
  
        CategoryPlot plot = (CategoryPlot) chart.getPlot();  
  
        setCategoryPlot(plot);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 瀑布图 
     *  
     * @return 
     */  
    public static JFreeChart createWaterfallChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createWaterfallChart("WaterfallChart",  
                "Category", "Value", ChartDataSet.createCategoryDataset(),  
                PlotOrientation.VERTICAL, true, true, false);  
  
        CategoryPlot plot = (CategoryPlot) chart.getPlot();  
  
        setCategoryPlot(plot);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 2D饼图 
     *  
     * @return 
     */  
    public static JFreeChart createPieChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createPieChart("PieChart", ChartDataSet  
                .createPieDataset(), true, true, false);  
  
        PiePlot piePlot = (PiePlot) chart.getPlot();  
  
        piePlot.setCircular(false);  
        piePlot.setLabelGap(0.02);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 3D饼图 
     *  
     * @return 
     */  
    public static JFreeChart createPieChart3D() { 
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createPieChart3D("PieChart3D",  
                ChartDataSet.createPieDataset(), true, true, false);  
  
        PiePlot piePlot = (PiePlot) chart.getPlot();  
  
        piePlot.setCircular(false);  
        piePlot.setLabelGap(0.02);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 复合饼图 12 
     */  
    public static JFreeChart createMultiplePieChart() {  
    	
    	init();
    	
        TableOrder order = TableOrder.BY_ROW;  
        // TableOrder order = TableOrder.BY_COLUMN;  
  
        JFreeChart chart = ChartFactory.createMultiplePieChart(  
                "MultiplePieChart", ChartDataSet.createCategoryDataset(),  
                order, true, true, false);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 复合饼图3D 12 
     */  
    public static JFreeChart createMultiplePieChart3D() {  
    	
    	init();
    	
        TableOrder order = TableOrder.BY_ROW;  
        // TableOrder order = TableOrder.BY_COLUMN;  
  
        JFreeChart chart = ChartFactory.createMultiplePieChart3D(  
                "MultiplePieChart3D", ChartDataSet.createCategoryDataset(),  
                order, true, true, false);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 环形图 
     */  
    public static JFreeChart createRingChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createPieChart("RingChart",  
                ChartDataSet.createPieDataset(), true, true, false);  
  
        Plot ringPlot = (Plot) chart.getPlot();  
  
        ringPlot.setBackgroundAlpha(1.0f);  
        ((PiePlot) ringPlot).setCircular(false);  
        ((PiePlot) ringPlot).setLabelGap(0.02);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 散点图 40 
     */  
    public static JFreeChart createScatterPlot() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createScatterPlot("ScatterPlot",  
                "Categary", "Value", ChartDataSet.createXYSeriesCollection(),  
                PlotOrientation.VERTICAL, true, true, false);  
  
        XYPlot xyPlot = chart.getXYPlot();  
  
        setXYSeriesPlot(xyPlot);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 组织图 
     */  
    public static JFreeChart createHistogram() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createHistogram("Histogram",  
                "Categary", "Value", ChartDataSet.createXYSeriesCollection(),  
                PlotOrientation.VERTICAL, true, true, false);  
  
        XYPlot xyPlot = chart.getXYPlot();  
  
        setXYSeriesPlot(xyPlot);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 数据点阶梯图 33 
     */  
    public static JFreeChart createXYStepChart() {  
    	
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createXYStepChart("XYStepChart",  
                "Categary", "Value", ChartDataSet.createXYSeriesCollection(),  
                PlotOrientation.VERTICAL, true, true, false);  
  
        XYPlot xyPlot = chart.getXYPlot();  
  
        setXYSeriesPlot(xyPlot);  
  
        return chart;  
    }  
  
    /**//* 
         * 堆积柱状图 01 
         */  
    public static JFreeChart createStackedCategoryChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createStackedBarChart(  
                "StackedCategoryChar", "Categary", "Value", ChartDataSet  
                        .createCategoryDataset(), PlotOrientation.VERTICAL,  
                true, true, false);  
  
        CategoryPlot plot = (CategoryPlot) chart.getPlot();  
  
        setCategoryPlot(plot);  
  
        return chart;  
    }  
  
    /**//* 
         * 堆积柱状图3D 04 
         */  
    public static JFreeChart createStackedCategoryChart3D() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createStackedBarChart3D(  
                "StackedCategoryChar3D", "Categary", "Value", ChartDataSet  
                        .createCategoryDataset(), PlotOrientation.VERTICAL,  
                true, true, false);  
  
        CategoryPlot plot = (CategoryPlot) chart.getPlot();  
  
        setCategoryPlot(plot);  
  
        return chart;  
    }  
  
    /**//* 
         * 堆积条形图 21 
         */  
    public static JFreeChart createStackedBarChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createStackedBarChart(  
                "StackedBarChart", "Categary", "Value", ChartDataSet  
                        .createCategoryDataset(), PlotOrientation.HORIZONTAL,  
                true, true, false);  
  
        CategoryPlot plot = (CategoryPlot) chart.getPlot();  
  
        setCategoryPlot(plot);  
  
        plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);  
  
        return chart;  
    }  
  
    /**//* 
         * 堆积条形图3D 23 
         */  
    public static JFreeChart createStackedBarChart3D() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createStackedBarChart3D(  
                "StackedBarChart3D", "Categary", "Value", ChartDataSet  
                        .createCategoryDataset(), PlotOrientation.HORIZONTAL,  
                true, true, false);  
  
        CategoryPlot plot = (CategoryPlot) chart.getPlot();  
  
        setCategoryPlot(plot);  
  
        plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);  
  
        return chart;  
    }  
  
    /**//* 
         * 堆积面积图 53 
         */  
    public static JFreeChart createStackedAreaChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createStackedAreaChart(  
                "XX电厂XX机组电量考核曲线", " ", " ", ChartDataSet  
                        .createCategoryDataset(), PlotOrientation.VERTICAL,  
                true, true, false);  
  
        CategoryPlot plot = (CategoryPlot) chart.getPlot();  
  
        setCategoryPlot(plot);  
  
        return chart;  
    }  
  
    /**//* 
         * XY数据点面积图 52 
         */  
    public static JFreeChart createXYAreaChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createXYAreaChart("XYAreaChart",  
                "Categary", "Value", ChartDataSet.createXYSeriesCollection(),  
                PlotOrientation.VERTICAL, true, true, false);  
  
        XYPlot xyPlot = chart.getXYPlot();  
  
        setXYSeriesPlot(xyPlot);  
  
        return chart;  
    }  
  
    /**//* 
         * XY数据点阶梯面积图 51 
         */  
    public static JFreeChart createXYStepAreaChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createXYStepAreaChart(  
                "XYStepAreaChart", "Categary", "Value", ChartDataSet  
                        .createXYSeriesCollection(), PlotOrientation.VERTICAL,  
                true, true, false);  
  
        XYPlot xyPlot = chart.getXYPlot();  
  
        setXYSeriesPlot(xyPlot);  
  
        return chart;  
    }  
  
    /**//* 
         * XY数据点折线图 32 
         */  
    public static JFreeChart createXYLineChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createXYLineChart("XYLineChart",  
                "Categary", "Value", ChartDataSet.createXYSeriesCollection(),  
                PlotOrientation.VERTICAL, true, true, false);  
  
        XYPlot xyPlot = chart.getXYPlot();  
  
        setXYSeriesPlot(xyPlot);  
  
        XYItemRenderer lineAndShapeRenderer = (XYItemRenderer) xyPlot  
                .getRenderer();  
  
        return chart;  
    }  
  
    /**//* 
         * XY数据点柱状图 05 
         */  
    public static JFreeChart createXYBarChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createXYBarChart("XYBarChart",  
                "Categary", true, "Value", ChartDataSet  
                        .createXYSeriesCollection(), PlotOrientation.VERTICAL,  
                true, true, false);  
  
        XYPlot xyPlot = chart.getXYPlot();  
  
        setXYSeriesPlot(xyPlot);  
  
        return chart;  
    }  
  
    /**//* 
         * 气泡图 
         */  
    public static JFreeChart createBubbleChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createBubbleChart("BubbleChart",  
                "Categary", "Value", (XYZDataset) ChartDataSet.createDefaultXYZDataset(),  
                PlotOrientation.VERTICAL, true, true, false);  
  
        return chart;  
    }  
  
    /**//* 
         * 数据点堆积面积图 
         */  
    public static JFreeChart createStackedXYAreaChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createStackedXYAreaChart(  
                "StackedXYAreaChart", "Categary", "Value", ChartDataSet  
                        .createDefaultTableXYDataset(),  
                PlotOrientation.VERTICAL, true, true, false);  
  
        XYPlot xyPlot = chart.getXYPlot();  
  
        setXYSeriesPlot(xyPlot);  
  
        return chart;  
    }  
  
    /**//* 
         * 甘特图 
         */  
    public static JFreeChart createGanttChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createGanttChart("GanttChart",  
                "Categary", "Value", ChartDataSet.createGanttDataset(), true,  
                true, false);  
  
        CategoryPlot plot = (CategoryPlot) chart.getPlot();  
  
        setCategoryPlot(plot);  
  
        return chart;  
    }  
  
    /**//* 
         * 股价图 
         */  
    public static JFreeChart createHighLowChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createHighLowChart("HighLowChart",  
                "Categary", "Value", ChartDataSet.createDefaultOHLCDataset(),  
                false);  
  
        return chart;  
    }  
  
    /**//* 
         * 烛台图 
         */  
    public static JFreeChart createCandlestickChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createCandlestickChart(  
                "CandlestickChart", "Categary", "Value", ChartDataSet  
                        .createDefaultOHLCDataset(), false);  
        return chart;  
    }  
  
    /**//* 
         * 雷达图 
         */  
    public static JFreeChart createPolarChart() {
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createPolarChart("PolarChart",  
                ChartDataSet.createXYSeriesCollection(), true, true, false);  
  
        return chart;  
    }  
  
    /**//* 
         * 2D柱线图 
         */  
    public static JFreeChart createCombinedChart() {  
    	
    	init();
    	
        JFreeChart chart = ChartFactory.createBarChart("CombinedChart",  
                "Categary", "Value", ChartDataSet.createCategoryDataset(),  
                PlotOrientation.VERTICAL, true, true, false);  
  
        CategoryPlot plot = (CategoryPlot) chart.getPlot();  
  
        setCategoryPlot(plot);  
  
        BarRenderer barRenderer = (BarRenderer) plot.getRenderer();  
        barRenderer.setMaximumBarWidth(0.10D);  
        barRenderer.setItemMargin(0.10D);  
  
        NumberAxis numberAxis = new NumberAxis("");  
        plot.setRangeAxis(1, numberAxis);  
        plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);  
        plot.setDataset(1, ChartDataSet.createCategoryDataset());  
        plot.setRenderer(1, new LineAndShapeRenderer());  
        plot.mapDatasetToDomainAxis(1, 0);  
        plot.mapDatasetToRangeAxis(1, 1);  
  
        return chart;  
    }  
  
    /** */  
    /** 
     * 设置CategoryPlot 坐标轴及网格属性设置 
     *  
     * @param plot 
     */  
    private static void setCategoryPlot(CategoryPlot plot) {  
        plot.getDomainAxis().setVisible(true);  
        plot.getDomainAxis().setLabelFont(new Font("宋体", Font.PLAIN, 12));  
        plot.getDomainAxis().setLabelPaint(Color.BLACK);  
        plot.getDomainAxis().setTickLabelFont(new Font("宋体", Font.PLAIN, 12));  
        plot.getDomainAxis().setTickLabelPaint(Color.BLACK);  
        plot.getDomainAxis().setTickLabelsVisible(true);  
  
        plot.getRangeAxis().setVisible(true);  
        plot.getRangeAxis().setLabelFont(new Font("宋体", Font.PLAIN, 12));  
        plot.getRangeAxis().setLabelPaint(Color.BLACK);  
        plot.getRangeAxis().setTickLabelFont(new Font("宋体", Font.PLAIN, 12));  
        plot.getRangeAxis().setTickLabelPaint(Color.BLACK);  
        plot.getRangeAxis().setVerticalTickLabels(false);  
        plot.getRangeAxis().setLabelAngle(0.0D);  
  
        plot.setDomainGridlinesVisible(true);  
        plot.setRangeGridlinesVisible(true);  
    }  
  
    /** */  
    /** 
     * 设置 XYPlot 
     *  
     * @param plot 
     */  
    private static void setXYSeriesPlot(XYPlot plot) {  
        plot.getDomainAxis().setVisible(true);  
        plot.getDomainAxis().setLabelFont(new Font("宋体", Font.PLAIN, 12));  
        plot.getDomainAxis().setLabelPaint(Color.BLACK);  
        plot.getDomainAxis().setTickLabelFont(new Font("宋体", Font.PLAIN, 12));  
        plot.getDomainAxis().setTickLabelPaint(Color.BLACK);  
        plot.getDomainAxis().setTickLabelsVisible(true);  
  
        plot.getRangeAxis().setVisible(true);  
        plot.getRangeAxis().setLabelFont(new Font("宋体", Font.PLAIN, 12));  
        plot.getRangeAxis().setLabelPaint(Color.BLACK);  
        plot.getRangeAxis().setTickLabelFont(new Font("宋体", Font.PLAIN, 12));  
        plot.getRangeAxis().setTickLabelPaint(Color.BLACK);  
        plot.getRangeAxis().setVerticalTickLabels(false);  
        plot.getRangeAxis().setLabelAngle(0.0D);  
  
        plot.setDomainGridlinesVisible(true);  
        plot.setRangeGridlinesVisible(true);  
    }

}
