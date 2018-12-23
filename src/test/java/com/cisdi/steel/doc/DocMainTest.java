package com.cisdi.steel.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import com.cisdi.steel.common.util.DateUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileOutputStream;
import java.util.*;

public class DocMainTest {
    private static List list = new ArrayList();

    static {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attr1", "上料量t/h");
        map.put("attr2", "730±10");
        map.put("attr3", "710");
        map.put("attr4", "-20");

        list.add(map);
    }

    public static void main(String[] args) {
        Date date = new Date();
        String date1 = DateUtil.getFormatDateTime(DateUtil.addDays(date, -2), DateUtil.MMddChineseFormat);
        String date2 = DateUtil.getFormatDateTime(DateUtil.addDays(date, -1), DateUtil.MMddChineseFormat);
        String date3 = DateUtil.getFormatDateTime(date, DateUtil.MMddChineseFormat);
        String date4 = DateUtil.getFormatDateTime(DateUtil.addDays(date, 1), DateUtil.MMddChineseFormat);
        String date5 = DateUtil.getFormatDateTime(date, DateUtil.yyyyMMddChineseFormat);
        String date6 = DateUtil.getFormatDateTime(date, "dd日");

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("date1", date1);
        map.put("date2", date2);
        map.put("date3", date3);
        map.put("date4", date4);
        map.put("date5", date5);
        map.put("date6", date6);

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
}
