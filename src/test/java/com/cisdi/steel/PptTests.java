package com.cisdi.steel;

import com.cisdi.steel.doc.ChartCreater;
import com.mchange.io.FileUtils;
import org.apache.poi.sl.usermodel.*;
import org.apache.poi.xslf.usermodel.*;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class PptTests {

    /**
     * XSLF构建图片
     *
     * @throws Exception
     */
    @Test
    public void test3() throws Exception {
        XMLSlideShow ppt = new XMLSlideShow();


//        JFreeChart Chart0 = ChartCreater.createCategoryChart();
        JFreeChart Chart0 = ChartCreater.createPolarChart();
        JFreeChart Chart00 = ChartCreater.createCategoryChart3D();
        JFreeChart Chart1 = ChartCreater.createBarChart();
        JFreeChart Chart2 = ChartCreater.createBarChart3D();
        JFreeChart Chart3 = ChartCreater.createLintChart();
        JFreeChart Chart4 = ChartCreater.createLintChart3D();
        JFreeChart Chart5 = ChartCreater.createAreaChart();
        JFreeChart Chart6 = ChartCreater.createWaterfallChart();
        JFreeChart Chart7 = ChartCreater.createPieChart();
        JFreeChart Chart8 = ChartCreater.createPieChart3D();
        JFreeChart Chart9 = ChartCreater.createMultiplePieChart();
        JFreeChart Chart10 = ChartCreater.createMultiplePieChart3D();
        JFreeChart Chart11 = ChartCreater.createRingChart();
        JFreeChart Chart12 = ChartCreater.createScatterPlot();
        JFreeChart Chart13 = ChartCreater.createHistogram();
        JFreeChart Chart14 = ChartCreater.createXYStepChart();
        JFreeChart Chart15 = ChartCreater.createStackedCategoryChart();
        JFreeChart Chart16 = ChartCreater.createStackedCategoryChart3D();
        JFreeChart Chart17 = ChartCreater.createStackedBarChart();
        JFreeChart Chart18 = ChartCreater.createStackedBarChart3D();
        ArrayList<JFreeChart> list = new ArrayList<>();
        list.add(Chart0);
        list.add(Chart00);
//        list.add(Chart1);
//        list.add(Chart2);
//        list.add(Chart3);
//        list.add(Chart4);
//        list.add(Chart5);
//        list.add(Chart6);
//        list.add(Chart7);
//        list.add(Chart8);
//        list.add(Chart9);
//        list.add(Chart10);
//        list.add(Chart11);
//        list.add(Chart12);
//        list.add(Chart13);
//        list.add(Chart14);
//        list.add(Chart15);
//        list.add(Chart16);
//        list.add(Chart17);
//        list.add(Chart18);
        int x = 50;//初始化x坐标
        int y = 50;//初始化y坐标
        int w = 300;//初始化图片宽度
        int h = 200;//初始化图片高度

        int csize = 2;//行
        int rsize = 2;//列

        XSLFSlide slide = ppt.createSlide();
        for (int i = 0; i < list.size(); i++) {
            common(ppt, slide, list.get(i), x + (i * w), y, w, h, csize * w, rsize * h);
        }
        FileOutputStream out = new FileOutputStream("C:\\Users\\cj\\Desktop\\test.pptx");
        ppt.write(out);
        out.close();
    }

    private void common(XMLSlideShow ppt, XSLFSlide slide, JFreeChart Chart, int x, int y, int w, int h, int csize, int rsize) throws Exception {
        Chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        Chart.getPlot().setBackgroundAlpha(0.1f);
        Chart.getPlot().setNoDataMessage("当前没有有效的数据");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtilities.writeChartAsJPEG(baos, Chart, csize, rsize);

        File image = new File("C:\\Users\\cj\\Desktop\\20181205100950.jpg");
//        byte[] data = FileUtils.getBytes(image);
        byte[] data = baos.toByteArray();
        XSLFPictureData pictureIndex = ppt.addPicture(data, PictureData.PictureType.JPEG);
        XSLFPictureShape picture = slide.createPicture(pictureIndex);
        picture.setAnchor(new Rectangle2D.Double(x, y, w, h));
    }


    /**
     * XSLF构建图形
     *
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        XMLSlideShow ppt = new XMLSlideShow(new FileInputStream("C:\\Users\\cj\\Desktop\\test.pptx"));
        XSLFSlide slideForLine = ppt.createSlide();
        XSLFSlide slideForTextBox = ppt.createSlide();
        XSLFSlide slideForTrapezoid = ppt.createSlide();
        XSLFSlide slideForShapeGroup = ppt.createSlide();

        //构建直线
        XSLFConnectorShape line = slideForLine.createConnector();
        line.setLineColor(Color.red);
        line.setAnchor(new Rectangle(200, 200, 200, 100));

        //构建一个文本框
        XSLFTextBox textBox = slideForTextBox.createTextBox();
        textBox.setAnchor(new Rectangle(100, 100, 300, 50));
        //设置文本框格式
        XSLFTextParagraph textBoxParagraph = textBox.addNewTextParagraph();
        XSLFTextRun textBoxStyle = textBoxParagraph.addNewTextRun();
        textBoxStyle.setText("new textBox");
        textBoxStyle.setFontSize(32.00);
        textBoxStyle.setUnderlined(true);
        textBoxStyle.setFontColor(Color.blue);

        //构建梯形
        XSLFAutoShape trapezoid = slideForTrapezoid.createAutoShape();
        trapezoid.setShapeType(ShapeType.TRAPEZOID);
        trapezoid.setAnchor(new java.awt.Rectangle(150, 150, 100, 200));
        trapezoid.setFillColor(Color.blue);
        /**
         * 构建组合图形，下面的方式生成的图形不是组合在一起的，目前没找到关于XSLF操作组合图形的例子
         */
        XSLFGroupShape groupShape = slideForShapeGroup.createGroup();
        groupShape.setAnchor(new Rectangle(100, 100, 300, 300));

        XSLFAutoShape autoShape = slideForShapeGroup.createAutoShape();
        autoShape.setAnchor(new Rectangle(100, 150, 200, 200));
        autoShape.setLineWidth(5);
        autoShape.setLineColor(Color.black);

        XSLFConnectorShape lineOfGroup = slideForShapeGroup.createConnector();
        lineOfGroup.setAnchor(new Rectangle(100, 150, 200, 200));
        lineOfGroup.setLineColor(Color.red);
        lineOfGroup.setLineWidth(1);

        FileOutputStream out = new FileOutputStream("C:\\Users\\cj\\Desktop\\test.pptx");
        ppt.write(out);
        out.close();
    }

    /**
     * 表格
     *
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        XMLSlideShow ppt = new XMLSlideShow(new FileInputStream("C:\\Users\\cj\\Desktop\\test.pptx"));
        XSLFSlide slide = ppt.createSlide();

        int COLUMN_NUM = 3;
        int ROW_NUM = 3;
        XSLFTable table = slide.createTable();
        table.setAnchor(new Rectangle2D.Double(50, 50, 450, 300));

        XSLFTableRow headerRow = table.addRow();
        headerRow.setHeight(50);

        for (int i = 0; i < COLUMN_NUM; i++) {
            XSLFTableCell th = headerRow.addCell();

            XSLFTextParagraph textParagraph = th.addNewTextParagraph();
            textParagraph.setTextAlign(TextParagraph.TextAlign.CENTER);

            XSLFTextRun textRun = textParagraph.addNewTextRun();
            textRun.setText("Header" + (i + 1));
            textRun.setBold(true);

            th.setFillColor(new Color(79, 129, 189));
            th.setBottomInset(2.00);
            th.setBorderColor(TableCell.BorderEdge.bottom, Color.white);

            table.setColumnWidth(i, 150);
        }

        for (int rowNum = 0; rowNum < ROW_NUM; rowNum++) {
            XSLFTableRow tr = table.addRow();
            tr.setHeight(50);

            for (int i = 0; i < COLUMN_NUM; i++) {
                XSLFTableCell cell = tr.addCell();
                XSLFTextParagraph textParagraph = cell.addNewTextParagraph();

                XSLFTextRun textRun = textParagraph.addNewTextRun();
                textRun.setText("Cell " + (i + 1));

                if (rowNum % 2 == 0)
                    cell.setFillColor(new Color(208, 216, 232));
                else
                    cell.setFillColor(new Color(233, 247, 244));
            }
        }

        FileOutputStream out = new FileOutputStream("C:\\Users\\cj\\Desktop\\test.pptx");
        ppt.write(out);
        out.close();
    }

    /**
     * 文本
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        XMLSlideShow ppt = new XMLSlideShow();
        XSLFSlide slide = ppt.createSlide();

        //构建一个标题
        XSLFTextShape title = slide.createTextBox();
        title.setPlaceholder(Placeholder.TITLE);
        //如果需要设置样式，使用XSLFTextRun的setTitle方法；使用下面注释的代码，打开生成的ppt时会报错
//        title.setText("Hello");
        title.setAnchor(new Rectangle(50, 50, 400, 100));

        //设置标题格式
        XSLFTextParagraph titleParagraph = title.addNewTextParagraph();
        XSLFTextRun titleStyle = titleParagraph.addNewTextRun();
        titleStyle.setText("Hello");
        titleStyle.setFontColor(Color.red);
        titleStyle.setBold(true);

        //构建一个文本框
        XSLFTextBox textBox = slide.createTextBox();
        //如果需要设置样式，使用XSLFTextRun的setTitle方法；使用下面注释的代码，打开生成的ppt时会报错
//        textBox.setText("new textBox");
        textBox.setAnchor(new Rectangle(100, 100, 300, 50));

        //设置文本框格式
        XSLFTextParagraph textBoxParagraph = textBox.addNewTextParagraph();
        XSLFTextRun textBoxStyle = textBoxParagraph.addNewTextRun();
        textBoxStyle.setText("new textBox");
        textBoxStyle.setFontSize(32.00);
        textBoxStyle.setUnderlined(true);
        textBoxStyle.setFontColor(Color.blue);

        FileOutputStream out = new FileOutputStream("C:\\Users\\cj\\Desktop\\test.pptx");
        ppt.write(out);
        out.close();
    }
}
