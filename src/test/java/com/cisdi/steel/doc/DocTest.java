package com.cisdi.steel.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.WordImageEntity;
import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.common.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;

@Slf4j
public class DocTest extends SteelApplicationTests {
    @Test
    public void test3() throws Exception {
        Date date = new Date();
        String date1 = DateUtil.getFormatDateTime(DateUtil.addDays(date, -2), DateUtil.MMddChineseFormat);
        String date2 = DateUtil.getFormatDateTime(DateUtil.addDays(date, -1), DateUtil.MMddChineseFormat);
        String date3 = DateUtil.getFormatDateTime(date, DateUtil.MMddChineseFormat);
        String date4 = DateUtil.getFormatDateTime(DateUtil.addDays(date, 1), DateUtil.MMddChineseFormat);
        String date5 = DateUtil.getFormatDateTime(date, DateUtil.yyyyMMddChineseFormat);

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("date1", date1);
        map.put("date2", date2);
        map.put("date3", date3);
        map.put("date4", date4);
        map.put("date5", date5);

        String path = "E://五烧20180914每日操业会-设计版v1.docx";
        try {
            XWPFDocument doc = WordExportUtil.exportWord07(path, map);
            String fileName = "五烧" + DateUtil.getFormatDateTime(date, "yyyyMMdd") + "每日操业会 - 设计版v1.docx";
            FileOutputStream fos = new FileOutputStream("D://" + fileName);
            doc.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * easypoi-word+JFreeChart测试
     *
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", "测试");
        map.put("currDate", new Date());

        for (int i = 0; i < 5; i++) {
            map.put("paragraph" + i, "新华社广州6月17日电 2017年金砖国家运动会于6月17日晚在广州开幕。国家主席习近平致贺信，对运动会的召开表示热烈祝贺，向参加运动会的各国嘉宾、运动员、教练员们致以诚挚的欢迎。习近平指出，我们期待着以今年9月举行的金砖国家领导人厦门会议为契机，推动金砖国家人文交流合作取得新成果，为金砖国家合作夯实民意基础。习近平强调，金砖国家体育事业发展各具特色。本届运动会将为提高运动员竞技水平、普及传统体育项目、推动体育事业发展、促进人民友谊发挥积极作用。希望运动员们发扬风格、赛出水平、创造佳绩。");
        }

        List<PayeeEntity> payees = new ArrayList<PayeeEntity>();
        for (int i = 0; i < 10; i++) {
            payees.add(new PayeeEntity("name" + i, "bankAccount" + i, "bankName" + i));
        }
        map.put("payees", payees);

        JFreeChart Chart = ChartCreater.createXYLineChart();

        Chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        Chart.getPlot().setBackgroundAlpha(0.1f);
        Chart.getPlot().setNoDataMessage("当前没有有效的数据");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtilities.writeChartAsJPEG(baos, Chart, 700, 500);

        WordImageEntity image = new WordImageEntity();
        image.setHeight(200);
        image.setWidth(500);
        image.setData(baos.toByteArray());
        image.setType(WordImageEntity.Data);
        map.put("jfreechartImg", image);

        for (int i = 0; i < 3; i++) {
            PayeeEntity payeeEntity = new PayeeEntity("name" + i, "bankAccount" + i, "bankName" + i);

            map.put("payee" + (i + 1), payeeEntity);
        }
//		String path = Class.class.getClass().getResource("/").getPath();
//        String path = System.getProperty("user.dir") + "/测试用freemarker生成word-easypoi.docx";
        String path = "E://测试用freemarker生成word-easypoi.docx";
        System.out.println("---------------path:" + path);
        try {
            XWPFDocument doc = WordExportUtil.exportWord07(path, map);
            FileOutputStream fos = new FileOutputStream(
                    "D://easypoiReport.docx");
            doc.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * word写入图片一
     *
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph p = doc.createParagraph();

        XWPFRun r = p.createRun();
        String[] imgs = {"E:\\TIM图片20181218155911.png"};

        for (String imgFile : imgs) {
            int format;

            if (imgFile.endsWith(".emf")) format = XWPFDocument.PICTURE_TYPE_EMF;
            else if (imgFile.endsWith(".wmf")) format = XWPFDocument.PICTURE_TYPE_WMF;
            else if (imgFile.endsWith(".pict")) format = XWPFDocument.PICTURE_TYPE_PICT;
            else if (imgFile.endsWith(".jpeg") || imgFile.endsWith(".jpg")) format = XWPFDocument.PICTURE_TYPE_JPEG;
            else if (imgFile.endsWith(".png")) format = XWPFDocument.PICTURE_TYPE_PNG;
            else if (imgFile.endsWith(".dib")) format = XWPFDocument.PICTURE_TYPE_DIB;
            else if (imgFile.endsWith(".gif")) format = XWPFDocument.PICTURE_TYPE_GIF;
            else if (imgFile.endsWith(".tiff")) format = XWPFDocument.PICTURE_TYPE_TIFF;
            else if (imgFile.endsWith(".eps")) format = XWPFDocument.PICTURE_TYPE_EPS;
            else if (imgFile.endsWith(".bmp")) format = XWPFDocument.PICTURE_TYPE_BMP;
            else if (imgFile.endsWith(".wpg")) format = XWPFDocument.PICTURE_TYPE_WPG;
            else {
                System.err.println("Unsupported picture: " + imgFile +
                        ". Expected emf|wmf|pict|jpeg|png|dib|gif|tiff|eps|bmp|wpg");
                continue;
            }

            r.setText(imgFile);
            r.addBreak();
            r.addPicture(new FileInputStream(imgFile), format, imgFile, Units.toEMU(200), Units.toEMU(200)); // 200x200 pixels
            r.addBreak(BreakType.PAGE);
        }

        FileOutputStream out = new FileOutputStream("images.docx");
        doc.write(out);
        out.close();
        doc.close();
    }
}
