package com.cisdi.steel.doc;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;

public class ChartFrame extends JFrame {

	/** 
     *  
     */
	public ChartFrame() {
		this.setBounds((int) ((1024 - 800) / 2), (int) ((768 - 600) / 2), 800,
				600);
		this.setTitle("JFreeChart Demo");
		this.setVisible(true);// add by taodzh

		JFreeChart Chart = null;
		
		//柱状图
//		 Chart = ChartCreater.createCategoryChart();
		 // 3D柱状图
//		 Chart = ChartCreater.createCategoryChart3D();
		 // 进度条柱状图
//		 Chart = ChartCreater.createBarChart();
		// 3D进度条柱状图
//		 Chart = ChartCreater.createBarChart3D();
		// 面积图
//		 Chart = ChartCreater.createAreaChart();
		// 柱状图
//		 Chart = ChartCreater.createHistogram();
		// 柱状图
//		 Chart = ChartCreater.createLintChart3D();
		// 多重饼图
//		 Chart = ChartCreater.createMultiplePieChart();
		// 3D多重饼图
//		 Chart = ChartCreater.createMultiplePieChart3D();
		// 饼图
//		 Chart = ChartCreater.createPieChart();
		// 3D饼图
//		 Chart = ChartCreater.createPieChart3D();
		// 环形饼图
//		 Chart = ChartCreater.createRingChart();
		// 散点图
//		 Chart = ChartCreater.createScatterPlot();
		// Chart = ChartCreater.createBubbleChart(); //youcuo
		// Chart = ChartCreater.createCandlestickChart(); //youcuo
		// 结合图（柱状图和折线图）
//		 Chart = ChartCreater.createCombinedChart();
		// 甘特图
//		 Chart = ChartCreater.createGanttChart();
		// Chart = ChartCreater.createHighLowChart(); //youcuo
		// 堆叠面积图
//		 Chart = ChartCreater.createStackedAreaChart();
		// 横向堆叠柱状图
//		 Chart = ChartCreater.createStackedBarChart();
		// 3D横向堆叠柱状图
//		 Chart = ChartCreater.createStackedBarChart3D();
		// 堆叠柱状图
//		 Chart = ChartCreater.createStackedCategoryChart();
		// 3D堆叠柱状图
//		 Chart = ChartCreater.createStackedCategoryChart3D();
		// Chart = ChartCreater.createStackedXYAreaChart(); //youcuo
		// 流水图
//		 Chart = ChartCreater.createWaterfallChart();
		// 折线面积图
//		 Chart = ChartCreater.createXYAreaChart();
		// 柱状图
//		 Chart = ChartCreater.createXYBarChart();
		// 折线图
//		 Chart = ChartCreater.createXYLineChart();
		// 面积折线图
//		 Chart = ChartCreater.createXYStepAreaChart();
		// 折线图
//		 Chart = ChartCreater.createXYStepChart();

		Chart.getPlot().setBackgroundAlpha(1.0f);
		Chart.getPlot().setNoDataMessage("当前没有有效的数据");

		Image chart = Chart.createBufferedImage(700, 500);

		JLabel label = new JLabel();

		label.setIcon(new ImageIcon(chart));

		this.getContentPane().add(label, BorderLayout.CENTER);

	}

	public static void main(String[] args) {
		new ChartFrame().setVisible(true);
	}

}
