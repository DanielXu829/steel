package com.cisdi.steel.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.MyXWPFDocument;
import com.cisdi.steel.common.util.DateUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;

import java.io.FileOutputStream;
import java.util.*;

public class DocMainTest {
    private static List list = new ArrayList();
    private static List list1 = new ArrayList();
    private static List list2 = new ArrayList();
    private static List list3 = new ArrayList();
    private static List list4 = new ArrayList();
    private static List list5 = new ArrayList();


    static {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attr1", "上料量t/h");
        map.put("attr2", "730±10");
        map.put("attr3", "710");
        map.put("attr4", "-20");
        map.put("attr5", "");
        list.add(map);

        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("attr1", "理论产量t");
        map1.put("attr2", "-");
        map1.put("attr3", "10867");
        map1.put("attr4", "11230");
        map1.put("attr5", "363");

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("attr1", "R±0.05合格率%");
        map2.put("attr2", "≥93");
        map2.put("attr3", "88.89");
        map2.put("attr4", "88.89");
        map2.put("attr5", "0");

        list1.add(map1);
        list1.add(map2);

        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("attr1", "燃料配比%");
        map3.put("attr2", "5.3");
        map3.put("attr3", "5.24");
        map3.put("attr4", "-0.06");

        Map<String, Object> map4 = new HashMap<String, Object>();
        map4.put("attr1", "返矿配比%");
        map4.put("attr2", "18.5");
        map4.put("attr3", "18.5");
        map4.put("attr4", "0");

        list2.add(map3);
        list2.add(map4);

        Map<String, Object> map5 = new HashMap<String, Object>();
        map5.put("attr1", "余热发电（kWh/t）");
        map5.put("attr2", "≥14.54");
        map5.put("attr3", "12.79");
        map5.put("attr4", "14.15");
        map5.put("attr5", "-1.8");
        map5.put("attr6", "-0.39");

        Map<String, Object> map6 = new HashMap<String, Object>();
        map6.put("attr1", "固燃比（kg/t ）");
        map6.put("attr2", "≤63.56");
        map6.put("attr3", "64.2");
        map6.put("attr4", "63.6");
        map6.put("attr5", "0.64");
        map6.put("attr6", "0.04");

        list3.add(map5);
        list3.add(map6);


        Map<String, Object> map7 = new HashMap<String, Object>();
        map7.put("attr0", "1");
        map7.put("attr1", "5#烧结高硫脱硫进口");
        map7.put("attr2", "≤50");
        map7.put("attr3", "—");
        map7.put("attr4", "≤300");
        map7.put("attr5", "≤75");
        map7.put("attr6", "—");
        map7.put("attr7", "—");

        Map<String, Object> map8 = new HashMap<String, Object>();
        map8.put("attr0", "2");
        map8.put("attr1", "5#烧结低硫脱硫进口");
        map8.put("attr2", "≤50");
        map8.put("attr3", "—");
        map8.put("attr4", "≤300");
        map8.put("attr5", "≤75");
        map8.put("attr6", "—");
        map8.put("attr7", "—");


        list4.add(map7);
        list4.add(map8);

        Map<String, Object> map9 = new HashMap<String, Object>();
        map9.put("attr1", "混合料水分率波动范围");
        map9.put("attr2", "±0.5％");

        Map<String, Object> map10 = new HashMap<String, Object>();
        map10.put("attr1", "点火温度");
        map10.put("attr2", "1100±100℃");

        Map<String, Object> map11 = new HashMap<String, Object>();
        map11.put("attr1", "南大烟道温度");
        map11.put("attr2", "110±20℃（6号机）\n" +
                "100±20℃（5号机）");

        Map<String, Object> map12 = new HashMap<String, Object>();
        map12.put("attr1", "粉尘配比单次调整幅度");
        map12.put("attr2", "        " + "≤0.5%（6号机）\n" +
                "        " + "≤1t（5号机）");

        list5.add(map9);
        list5.add(map10);
        list5.add(map11);
        list5.add(map12);


    }

    public static void main(String[] args) {
        Date date = new Date();
        String date1 = DateUtil.getFormatDateTime(DateUtil.addDays(date, -2), DateUtil.MMddChineseFormat);
        String date2 = DateUtil.getFormatDateTime(DateUtil.addDays(date, -1), DateUtil.MMddChineseFormat);
        String date3 = DateUtil.getFormatDateTime(date, DateUtil.MMddChineseFormat);
        String date4 = DateUtil.getFormatDateTime(DateUtil.addDays(date, 1), DateUtil.MMddChineseFormat);
        String date5 = DateUtil.getFormatDateTime(date, DateUtil.yyyyMMddChineseFormat);
        String date6 = DateUtil.getFormatDateTime(date, "dd日");

        //doc结果
        HashMap<String, Object> map = new HashMap<String, Object>();

        //文档所有日期
        map.put("date1", date1);
        map.put("date2", date2);
        map.put("date3", date3);
        map.put("date4", date4);
        map.put("date5", date5);
        map.put("date6", date6);

        //文档表格简单数据
        map.put("sheet1", list);


        //第一部分特殊部分
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("attr1", "大烟道温度℃");
        map1.put("attr2", "南100±20");
        map1.put("attr3", "107");
        map1.put("attr4", "7");
        map1.put("attr5", "正常");

        map1.put("attr6", "北140±20");
        map1.put("attr7", "140");
        map1.put("attr8", "0");
        map1.put("attr9", "正常");

        map1.put("attr10", "主抽阀门开度%");
        map1.put("attr11", "南85-97");
        map1.put("attr12", "97.00");
        map1.put("attr13", "-");
        map1.put("attr14", "正常");

        map1.put("attr15", "北85-97");
        map1.put("attr16", "97.00");
        map1.put("attr17", "-");
        map1.put("attr18", "正常");

        map1.put("attr19", "BTP位置");
        map1.put("attr20", "南23.0±1.0");
        map1.put("attr21", "22.93");
        map1.put("attr22", "-0.17");
        map1.put("attr23", "正常");

        map1.put("attr24", "北23.0±1.0");
        map1.put("attr25", "23.07");
        map1.put("attr26", "0.07");
        map1.put("attr27", "正常");

        map.putAll(map1);


        map.put("sheet2", list1);
        map.put("sheet3", list2);
        map.put("sheet4", list3);

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("bttr1", "上料量t/h");
        map2.put("bttr2", "720±10");

        map2.put("bttr3", "返矿配比%");
        map2.put("bttr4", "18±1");

        map2.put("bttr5", "粉尘配比t/h");
        map2.put("bttr6", "12±2");

        map2.put("bttr7", "混合料水分%");
        map2.put("bttr8", "7.2±0.3");


        map2.put("bttr9", "布料参数mm");
        map2.put("bttr10", "620±10");


        map2.put("bttr11", "点火温度℃");
        map2.put("bttr12", "1080±30");


        map2.put("bttr13", "烧结机速m/min");
        map2.put("bttr14", "1.92±0.05");


        map2.put("bttr15", "大烟道温度℃");
        map2.put("bttr16", "南100±20");
        map2.put("bttr17", "北140±20");

        map2.put("bttr18", "主抽阀门开度%");
        map2.put("bttr19", "南85-97");
        map2.put("bttr20", "北85-97");

        map2.put("bttr21", "BTP位置");
        map2.put("bttr22", "南23.0±1.0");
        map2.put("bttr23", "北23.0±1.0");

        map.putAll(map2);

        map.put("sheet5", list4);
        map.put("sheet6", list5);


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
