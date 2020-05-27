package com.cisdi.steel.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.WordImageEntity;
import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.job.a1.doc.GaoLuDocMain2;
import com.cisdi.steel.module.job.a3.doc.ShaoJieMain2;
import com.cisdi.steel.module.job.a3.doc.ShaojieDocMain;
import com.cisdi.steel.module.job.gl.doc.GaoLuRiFenXiBaoGao;
import com.cisdi.steel.module.job.jh.doc.JiaoHuaShengChanZhenDuanBaoGao;
import com.cisdi.steel.module.job.sj.doc.ShaoJieFenXiDocMain;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;

@Slf4j
public class DocTest extends SteelApplicationTests {

//    @Autowired
//    private GaoLuDocMain gaoLuDocMain;
//
//    @Test
//    public void test4(){
//        gaoLuDocMain.mainTask();
//    }

    @Autowired
    private GaoLuDocMain2 gaoLuDocMain2;

    @Test
    public void test5() {
        gaoLuDocMain2.mainTask();
    }


    @Autowired
    private ShaoJieMain2 shaoJieMain2;

    @Test
    public void test6() {
        long start = System.currentTimeMillis();
        shaoJieMain2.mainTask();
        long end = System.currentTimeMillis();

        System.out.println(end - start);
    }

    @Autowired
    private ShaojieDocMain shaojieDocMain;
    @Test
    public void test7() {
        long start = System.currentTimeMillis();
        shaojieDocMain.mainJob();
        long end = System.currentTimeMillis();

        System.out.println(end - start);
    }


    /**
     * 测试“烧结生产分析模板”
     */
    @Autowired
    private ShaoJieFenXiDocMain shaojieFenXiDocMain;
    @Test
    public void testShaoJieFenXi() {
        long start = System.currentTimeMillis();
        shaojieFenXiDocMain.mainJob();
        long end = System.currentTimeMillis();

        System.out.println(end - start);
    }

    /**
     * 测试“高炉日生产分析报告”
     */
    @Autowired
    private GaoLuRiFenXiBaoGao gaoLuRiFenXiBaoGao;
    @Test
    public void testGaoLuRiFenXiBaoGao() {
        long start = System.currentTimeMillis();
        gaoLuRiFenXiBaoGao.mainTask();
        long end = System.currentTimeMillis();

        System.out.println(end - start);
    }

    @Autowired
    private JiaoHuaShengChanZhenDuanBaoGao jhShengChanZhenDuanBaoGao;

    @Test
    public void testJhShengChanZhenDuanBaoGao() {
        jhShengChanZhenDuanBaoGao.mainTask();
    }
}
