package com.cisdi.steel.module.job.gl.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.WordImageEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.dto.response.SuccessEntity;
import com.cisdi.steel.dto.response.gl.AnalysisValueDTO;
import com.cisdi.steel.dto.response.gl.TagValueListDTO;
import com.cisdi.steel.dto.response.gl.res.*;
import com.cisdi.steel.dto.response.sj.*;
import com.cisdi.steel.module.job.AbstractExportWordJob;
import com.cisdi.steel.module.job.a1.doc.ChartFactory;
import com.cisdi.steel.module.job.a1.doc.Serie;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.GLDataUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.enums.LanguageEnum;
import com.cisdi.steel.module.report.enums.ReportTemplateTypeEnum;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import com.cisdi.steel.module.report.service.ReportIndexService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.tools.ant.util.DateUtils;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import static java.util.Comparator.comparing;

@Component
@Slf4j
public class GaoLuRiFenXiBaoGao extends AbstractExportWordJob {
    /**
     * doc最后结果
     */
    private HashMap<String, Object> result = null;
    private Date startTime = null;
    private Date endTime = null;
    private List<String> categoriesList = new ArrayList<>();
    private List<String> longTimeList = new ArrayList<>();
    private ReportCategoryTemplate currentTemplate;
    private DecimalFormat df2 = new DecimalFormat("0.00");
    private DecimalFormat df3 = new DecimalFormat("0.000");
    private String[] L1 = new String[]{
            "BF8_L2M_HMMassAct_1d_cur","BF8_L2M_Productivity_1d","BF8_L2M_BX_CokeRate_1d_cur","BF8_L2M_BX_CoalRate_1d_cur",
            "BF8_L2M_BX_FuelRate_1d_cur","BF8_L2C_BD_HotBlastTemp1_1d_avg"
    };
    private String[] L3 = new String[]{
            "BF8_L2C_BD_BH_1d_avg","BF8_L2C_BD_ColdBlastFlow_1d_avg",
            "BF8_L2C_BD_ColdBlastPress_1d_avg","BF8_L2M_PressDiff_1d_avg","BF8_L2C_BD_TopPress_1d_avg",
            "BF8_L2M_HMTemp_1d_avg","BF8_L2M_BX_FuelRate_1d_cur","BF8_L2M_GasUtilization_1d_avg"
    };
    private String[] L4 = new String[]{
            "BF8_L2C_BD_SoftTempDiff_1d_avg"
    };

    private String[] luGang = new String[]{
            "BF8_L2C_BD_TI0707C_1d_max", "BF8_L2C_BD_TI0708A_1d_max", "BF8_L2C_BD_TI0708B_1d_max", "BF8_L2C_BD_TI0708C_1d_max", "BF8_L2C_BD_TI0709A_1d_max", "BF8_L2C_BD_TI0709B_1d_max", "BF8_L2C_BD_TI0709C_1d_max", "BF8_L2C_BD_TI0710A_1d_max", "BF8_L2C_BD_TI0710B_1d_max",
            "BF8_L2C_BD_TI0710C_1d_max", "BF8_L2C_BD_TI0711A_1d_max", "BF8_L2C_BD_TI0711B_1d_max", "BF8_L2C_BD_TI0711C_1d_max", "BF8_L2C_BD_TI0712A_1d_max", "BF8_L2C_BD_TI0712B_1d_max", "BF8_L2C_BD_TI0712C_1d_max", "BF8_L2C_BD_TI0713A_1d_max", "BF8_L2C_BD_TI0713B_1d_max", "BF8_L2C_BD_TI0713C_1d_max",
            "BF8_L2C_BD_ATI0801A_1d_max", "BF8_L2C_BD_ATI0801B_1d_max", "BF8_L2C_BD_ATI0801C_1d_max", "BF8_L2C_BD_TI0802A_1d_max", "BF8_L2C_BD_TI0802B_1d_max", "BF8_L2C_BD_TI0802C_1d_max", "BF8_L2C_BD_TI0803A_1d_max", "BF8_L2C_BD_TI0803B_1d_max", "BF8_L2C_BD_TI0803C_1d_max", "BF8_L2C_BD_TI0804A_1d_max",
            "BF8_L2C_BD_TI0804B_1d_max", "BF8_L2C_BD_TI0804C_1d_max", "BF8_L2C_BD_TI0805A_1d_max", "BF8_L2C_BD_TI0805B_1d_max", "BF8_L2C_BD_TI0805C_1d_max", "BF8_L2C_BD_TI0806A_1d_max", "BF8_L2C_BD_TI0806B_1d_max", "BF8_L2C_BD_TI0806C_1d_max", "BF8_L2C_BD_TI0807A_1d_max", "BF8_L2C_BD_TI0807B_1d_max",
            "BF8_L2C_BD_TI0807C_1d_max", "BF8_L2C_BD_TI0808A_1d_max", "BF8_L2C_BD_TI0808B_1d_max", "BF8_L2C_BD_TI0808C_1d_max", "BF8_L2C_BD_TI0809A_1d_max", "BF8_L2C_BD_TI0809B_1d_max", "BF8_L2C_BD_TI0809C_1d_max", "BF8_L2C_BD_TI0810A_1d_max", "BF8_L2C_BD_TI0810B_1d_max", "BF8_L2C_BD_TI0810C_1d_max",
            "BF8_L2C_BD_TI0811A_1d_max", "BF8_L2C_BD_TI0811B_1d_max", "BF8_L2C_BD_TI0811C_1d_max", "BF8_L2C_BD_TI0812A_1d_max", "BF8_L2C_BD_TI0812B_1d_max", "BF8_L2C_BD_TI0812C_1d_max", "BF8_L2C_BD_TI0813A_1d_max", "BF8_L2C_BD_TI0813B_1d_max", "BF8_L2C_BD_TI0813C_1d_max", "BF8_L2C_BD_TI0901A_1d_max",
            "BF8_L2C_BD_TI0901B_1d_max", "BF8_L2C_BD_TI0901C_1d_max", "BF8_L2C_BD_TI0902A_1d_max", "BF8_L2C_BD_TI0902B_1d_max", "BF8_L2C_BD_TI0902C_1d_max", "BF8_L2C_BD_TI0903A_1d_max", "BF8_L2C_BD_TI0903B_1d_max", "BF8_L2C_BD_TI0903C_1d_max", "BF8_L2C_BD_TI0904A_1d_max", "BF8_L2C_BD_TI0904B_1d_max",
            "BF8_L2C_BD_TI0904C_1d_max", "BF8_L2C_BD_TI0905A_1d_max", "BF8_L2C_BD_TI0905B_1d_max", "BF8_L2C_BD_TI0905C_1d_max", "BF8_L2C_BD_TI0906A_1d_max", "BF8_L2C_BD_TI0906B_1d_max", "BF8_L2C_BD_TI0906C_1d_max", "BF8_L2C_BD_TI0907A_1d_max", "BF8_L2C_BD_TI0907B_1d_max", "BF8_L2C_BD_TI0907C_1d_max",
            "BF8_L2C_BD_TI0908A_1d_max", "BF8_L2C_BD_TI0908B_1d_max", "BF8_L2C_BD_TI0908C_1d_max", "BF8_L2C_BD_TI0909A_1d_max", "BF8_L2C_BD_TI0909B_1d_max", "BF8_L2C_BD_TI0909C_1d_max", "BF8_L2C_BD_TI0910A_1d_max", "BF8_L2C_BD_TI0910B_1d_max", "BF8_L2C_BD_TI0910C_1d_max", "BF8_L2C_BD_TI0911A_1d_max",
            "BF8_L2C_BD_TI0911B_1d_max", "BF8_L2C_BD_TI0911C_1d_max", "BF8_L2C_BD_TI0912A_1d_max", "BF8_L2C_BD_TI0912B_1d_max", "BF8_L2C_BD_TI0912C_1d_max", "BF8_L2C_BD_TI0913A_1d_max", "BF8_L2C_BD_TI0913B_1d_max", "BF8_L2C_BD_TI0913C_1d_max", "BF8_L2C_BD_TI1001A_1d_max", "BF8_L2C_BD_TI1001B_1d_max",
            "BF8_L2C_BD_TI1002A_1d_max", "BF8_L2C_BD_TI1002B_1d_max", "BF8_L2C_BD_TI1003A_1d_max", "BF8_L2C_BD_TI1003B_1d_max", "BF8_L2C_BD_TI1004A_1d_max", "BF8_L2C_BD_TI1004B_1d_max", "BF8_L2C_BD_TI1005A_1d_max", "BF8_L2C_BD_TI1005B_1d_max", "BF8_L2C_BD_TI1006A_1d_max", "BF8_L2C_BD_TI1006B_1d_max",
            "BF8_L2C_BD_TI1007A_1d_max", "BF8_L2C_BD_TI1007B_1d_max", "BF8_L2C_BD_TI1008A_1d_max", "BF8_L2C_BD_TI1008B_1d_max", "BF8_L2C_BD_TI1009A_1d_max", "BF8_L2C_BD_TI1009B_1d_max", "BF8_L2C_BD_TI1010A_1d_max", "BF8_L2C_BD_TI1010B_1d_max", "BF8_L2C_BD_ATI1011A_1d_max", "BF8_L2C_BD_ATI1011B_1d_max",
            "BF8_L2C_BD_TI1012A_1d_max", "BF8_L2C_BD_TI1012B_1d_max", "BF8_L2C_BD_TI1013A_1d_max", "BF8_L2C_BD_TI1013B_1d_max", "BF8_L2C_BD_TI1101A_1d_max", "BF8_L2C_BD_TI1101B_1d_max", "BF8_L2C_BD_TI1102A_1d_max", "BF8_L2C_BD_TI1102B_1d_max", "BF8_L2C_BD_TI1103A_1d_max", "BF8_L2C_BD_TI1103B_1d_max",
            "BF8_L2C_BD_TI1104A_1d_max", "BF8_L2C_BD_TI1104B_1d_max", "BF8_L2C_BD_TI1105A_1d_max", "BF8_L2C_BD_TI1105B_1d_max", "BF8_L2C_BD_TI1106A_1d_max", "BF8_L2C_BD_TI1106B_1d_max","BF8_L2C_BD_TI1107A_1d_max", "BF8_L2C_BD_TI1107B_1d_max", "BF8_L2C_BD_TI1108A_1d_max", "BF8_L2C_BD_TI1108B_1d_max",
            "BF8_L2C_BD_TI1109A_1d_max", "BF8_L2C_BD_TI1109B_1d_max", "BF8_L2C_BD_TI1110A_1d_max", "BF8_L2C_BD_TI1110B_1d_max", "BF8_L2C_BD_TI1111A_1d_max", "BF8_L2C_BD_TI1111B_1d_max", "BF8_L2C_BD_TI1112A_1d_max", "BF8_L2C_BD_TI1112B_1d_max", "BF8_L2C_BD_TI1113A_1d_max", "BF8_L2C_BD_TI1113B_1d_max",
            "BF8_L2C_BD_TI2101_1d_max", "BF8_L2C_BD_TI2102_1d_max", "BF8_L2C_BD_TI2103_1d_max", "BF8_L2C_BD_TI2104_1d_max", "BF8_L2C_BD_TI2105_1d_max", "BF8_L2C_BD_TI2106_1d_max", "BF8_L2C_BD_TI2107_1d_max", "BF8_L2C_BD_TI2108_1d_max", "BF8_L2C_BD_TI2201_1d_max", "BF8_L2C_BD_TI2202_1d_max", "BF8_L2C_BD_TI2203_1d_max",
            "BF8_L2C_BD_TI2204_1d_max", "BF8_L2C_BD_TI2205_1d_max", "BF8_L2C_BD_TI2206_1d_max", "BF8_L2C_BD_TI2207_1d_max", "BF8_L2C_BD_TI2208_1d_max", "BF8_L2C_BD_TI2301_1d_max", "BF8_L2C_BD_TI2302_1d_max", "BF8_L2C_BD_TI2303_1d_max", "BF8_L2C_BD_TI2304_1d_max", "BF8_L2C_BD_TI2305_1d_max", "BF8_L2C_BD_TI2307_1d_max",
            "BF8_L2C_BD_TI2308_1d_max", "BF8_L2C_BD_TI2401_1d_max", "BF8_L2C_BD_TI2402_1d_max", "BF8_L2C_BD_TI2403_1d_max", "BF8_L2C_BD_TI2404_1d_max", "BF8_L2C_BD_TI2405_1d_max", "BF8_L2C_BD_TI2406_1d_max", "BF8_L2C_BD_TI2407_1d_max", "BF8_L2C_BD_TI2408_1d_max", "BF8_L2C_BD_TI2409_1d_max", "BF8_L2C_BD_TI2410_1d_max",
            "BF8_L2C_BD_TI2306_1d_max"
    };

    private String[] luFu = new String[]{
            "BF8_L2C_BD_TI2501_1d_max", "BF8_L2C_BD_TI2502_1d_max", "BF8_L2C_BD_TI2503_1d_max", "BF8_L2C_BD_TI2504_1d_max", "BF8_L2C_BD_TI2601_1d_max", "BF8_L2C_BD_TI2602_1d_max", "BF8_L2C_BD_TI2603_1d_max", "BF8_L2C_BD_TI2604_1d_max", "BF8_L2C_BD_TI2605_1d_max", "BF8_L2C_BD_TI2606_1d_max",
            "BF8_L2C_BD_TI2607_1d_max", "BF8_L2C_BD_TI2608_1d_max", "BF8_L2C_BD_TI2609_1d_max", "BF8_L2C_BD_TI2610_1d_max", "BF8_L2C_BD_TI2611_1d_max", "BF8_L2C_BD_TI2612_1d_max", "BF8_L2C_BD_TI2613_1d_max"
    };

    private String[] luYao = new String[]{
            "BF8_L2C_BD_TI2701_1d_max", "BF8_L2C_BD_TI2702_1d_max", "BF8_L2C_BD_TI2703_1d_max", "BF8_L2C_BD_TI2704_1d_max", "BF8_L2C_BD_TI2801_1d_max", "BF8_L2C_BD_TI2802_1d_max", "BF8_L2C_BD_TI2803_1d_max", "BF8_L2C_BD_TI2804_1d_max", "BF8_L2C_BD_TI2805_1d_max", "BF8_L2C_BD_TI2806_1d_max", "BF8_L2C_BD_TI2807_1d_max",
            "BF8_L2C_BD_TI2808_1d_max", "BF8_L2C_BD_TI2809_1d_max", "BF8_L2C_BD_TI2810_1d_max", "BF8_L2C_BD_TI2811_1d_max", "BF8_L2C_BD_TI2812_1d_max", "BF8_L2C_BD_TI2813_1d_max"
    };

    private String[] luShen = new String[]{
            "BF8_L2C_BD_TI2901_1d_max", "BF8_L2C_BD_TI2902_1d_max", "BF8_L2C_BD_TI2903_1d_max", "BF8_L2C_BD_TI2904_1d_max", "BF8_L2C_BD_TI3001_1d_max", "BF8_L2C_BD_TI3002_1d_max", "BF8_L2C_BD_TI3003_1d_max", "BF8_L2C_BD_TI3004_1d_max", "BF8_L2C_BD_TI3005_1d_max", "BF8_L2C_BD_TI3006_1d_max", "BF8_L2C_BD_TI3007_1d_max",
            "BF8_L2C_BD_TI3008_1d_max", "BF8_L2C_BD_TI3009_1d_max", "BF8_L2C_BD_TI3010_1d_max", "BF8_L2C_BD_TI3011_1d_max", "BF8_L2C_BD_TI3012_1d_max", "BF8_L2C_BD_TI3013_1d_max", "BF8_L2C_BD_TI3101_1d_max", "BF8_L2C_BD_TI3102_1d_max", "BF8_L2C_BD_TI3103_1d_max", "BF8_L2C_BD_TI3104_1d_max", "BF8_L2C_BD_TI3105_1d_max",
            "BF8_L2C_BD_TI3106_1d_max", "BF8_L2C_BD_TI3107_1d_max", "BF8_L2C_BD_TI3108_1d_max", "BF8_L2C_BD_TI3109_1d_max", "BF8_L2C_BD_TI3110_1d_max", "BF8_L2C_BD_TI3111_1d_max", "BF8_L2C_BD_TI3112_1d_max", "BF8_L2C_BD_TI3113_1d_max", "BF8_L2C_BD_TI3201_1d_max", "BF8_L2C_BD_TI3202_1d_max", "BF8_L2C_BD_TI3203_1d_max",
            "BF8_L2C_BD_TI3204_1d_max", "BF8_L2C_BD_TI3301_1d_max", "BF8_L2C_BD_TI3302_1d_max", "BF8_L2C_BD_TI3303_1d_max", "BF8_L2C_BD_TI3304_1d_max", "BF8_L2C_BD_TI3305_1d_max", "BF8_L2C_BD_TI3306_1d_max", "BF8_L2C_BD_TI3307_1d_max", "BF8_L2C_BD_TI3308_1d_max", "BF8_L2C_BD_TI3309_1d_max", "BF8_L2C_BD_TI3310_1d_max",
            "BF8_L2C_BD_TI3311_1d_max", "BF8_L2C_BD_TI3312_1d_max", "BF8_L2C_BD_TI3401_1d_max", "BF8_L2C_BD_TI3402_1d_max", "BF8_L2C_BD_TI3403_1d_max", "BF8_L2C_BD_TI3404_1d_max", "BF8_L2C_BD_TI3405_1d_max", "BF8_L2C_BD_TI3406_1d_max", "BF8_L2C_BD_TI3407_1d_max", "BF8_L2C_BD_TI3408_1d_max", "BF8_L2C_BD_TI3409_1d_max",
            "BF8_L2C_BD_TI3410_1d_max", "BF8_L2C_BD_TI3411_1d_max", "BF8_L2C_BD_TI3412_1d_max", "BF8_L2C_BD_TI3501_1d_max", "BF8_L2C_BD_TI3502_1d_max", "BF8_L2C_BD_TI3503_1d_max", "BF8_L2C_BD_TI3504_1d_max", "BF8_L2C_BD_TI3505_1d_max", "BF8_L2C_BD_TI3506_1d_max", "BF8_L2C_BD_TI3507_1d_max", "BF8_L2C_BD_TI3508_1d_max",
            "BF8_L2C_BD_TI3509_1d_max", "BF8_L2C_BD_TI3510_1d_max", "BF8_L2C_BD_TI3511_1d_max", "BF8_L2C_BD_TI3601_1d_max", "BF8_L2C_BD_TI3602_1d_max", "BF8_L2C_BD_TI3603_1d_max", "BF8_L2C_BD_TI3604_1d_max", "BF8_L2C_BD_TI3605_1d_max", "BF8_L2C_BD_TI3606_1d_max", "BF8_L2C_BD_TI3607_1d_max", "BF8_L2C_BD_TI3608_1d_max",
            "BF8_L2C_BD_TI3609_1d_max", "BF8_L2C_BD_TI3610_1d_max"
    };

    private String[] luGangA_C = new String[]{
        "BF8_L2C_BD_TI0601A_1d_max", "BF8_L2C_BD_TI0601B_1d_max", "BF8_L2C_BD_TI0601C_1d_max", "BF8_L2C_BD_TI0602A_1d_max",
        "BF8_L2C_BD_TI0602B_1d_max", "BF8_L2C_BD_TI0602C_1d_max", "BF8_L2C_BD_TI0603A_1d_max", "BF8_L2C_BD_TI0603B_1d_max",
        "BF8_L2C_BD_TI0603C_1d_max", "BF8_L2C_BD_TI0604A_1d_max", "BF8_L2C_BD_TI0604B_1d_max", "BF8_L2C_BD_TI0604C_1d_max",
        "BF8_L2C_BD_TI0605A_1d_max", "BF8_L2C_BD_TI0605B_1d_max", "BF8_L2C_BD_TI0605C_1d_max", "BF8_L2C_BD_TI0606A_1d_max",
        "BF8_L2C_BD_TI0606B_1d_max", "BF8_L2C_BD_TI0606C_1d_max", "BF8_L2C_BD_TI0607A_1d_max", "BF8_L2C_BD_TI0607B_1d_max",
        "BF8_L2C_BD_TI0607C_1d_max", "BF8_L2C_BD_TI0608A_1d_max", "BF8_L2C_BD_TI0608B_1d_max", "BF8_L2C_BD_TI0608C_1d_max",
        "BF8_L2C_BD_TI0609A_1d_max", "BF8_L2C_BD_TI0609B_1d_max", "BF8_L2C_BD_TI0609C_1d_max", "BF8_L2C_BD_TI0610A_1d_max",
        "BF8_L2C_BD_TI0610B_1d_max", "BF8_L2C_BD_TI0610C_1d_max", "BF8_L2C_BD_TI0611A_1d_max", "BF8_L2C_BD_TI0611B_1d_max",
        "BF8_L2C_BD_TI0611C_1d_max", "BF8_L2C_BD_TI0612A_1d_max", "BF8_L2C_BD_TI0612B_1d_max", "BF8_L2C_BD_TI0612C_1d_max",
        "BF8_L2C_BD_TI0613A_1d_max", "BF8_L2C_BD_TI0613B_1d_max", "BF8_L2C_BD_TI0613C_1d_max", "BF8_L2C_BD_TI0701A_1d_max",
        "BF8_L2C_BD_TI0701B_1d_max", "BF8_L2C_BD_TI0701C_1d_max", "BF8_L2C_BD_TI0702A_1d_max", "BF8_L2C_BD_TI0702B_1d_max",
        "BF8_L2C_BD_TI0702C_1d_max", "BF8_L2C_BD_TI0703A_1d_max", "BF8_L2C_BD_TI0703B_1d_max", "BF8_L2C_BD_TI0703C_1d_max"
    };

    private String[] luGangD_F = new String[]{
            "BF8_L2C_BD_TI0704A_1d_max", "BF8_L2C_BD_TI0704B_1d_max", "BF8_L2C_BD_TI0704C_1d_max", "BF8_L2C_BD_TI0705A_1d_max",
            "BF8_L2C_BD_TI0705B_1d_max", "BF8_L2C_BD_TI0705C_1d_max", "BF8_L2C_BD_TI0706A_1d_max", "BF8_L2C_BD_TI0706B_1d_max",
            "BF8_L2C_BD_TI0706C_1d_max", "BF8_L2C_BD_TI0707A_1d_max", "BF8_L2C_BD_TI0707B_1d_max", "BF8_L2C_BD_TI0707C_1d_max",
            "BF8_L2C_BD_TI0708A_1d_max", "BF8_L2C_BD_TI0708B_1d_max", "BF8_L2C_BD_TI0708C_1d_max", "BF8_L2C_BD_TI0709A_1d_max",
            "BF8_L2C_BD_TI0709B_1d_max", "BF8_L2C_BD_TI0709C_1d_max", "BF8_L2C_BD_TI0710A_1d_max", "BF8_L2C_BD_TI0710B_1d_max",
            "BF8_L2C_BD_TI0710C_1d_max", "BF8_L2C_BD_TI0711A_1d_max", "BF8_L2C_BD_TI0711B_1d_max", "BF8_L2C_BD_TI0711C_1d_max",
            "BF8_L2C_BD_TI0712A_1d_max", "BF8_L2C_BD_TI0712B_1d_max", "BF8_L2C_BD_TI0712C_1d_max", "BF8_L2C_BD_TI0713A_1d_max",
            "BF8_L2C_BD_TI0713B_1d_max", "BF8_L2C_BD_TI0713C_1d_max", "BF8_L2C_BD_ATI0801A_1d_max", "BF8_L2C_BD_ATI0801B_1d_max",
            "BF8_L2C_BD_ATI0801C_1d_max", "BF8_L2C_BD_TI0802A_1d_max", "BF8_L2C_BD_TI0802B_1d_max", "BF8_L2C_BD_TI0802C_1d_max",
            "BF8_L2C_BD_TI0803A_1d_max", "BF8_L2C_BD_TI0803B_1d_max", "BF8_L2C_BD_TI0803C_1d_max", "BF8_L2C_BD_TI0804A_1d_max",
            "BF8_L2C_BD_TI0804B_1d_max", "BF8_L2C_BD_TI0804C_1d_max", "BF8_L2C_BD_TI0805A_1d_max", "BF8_L2C_BD_TI0805B_1d_max",
            "BF8_L2C_BD_TI0805C_1d_max", "BF8_L2C_BD_TI0806A_1d_max", "BF8_L2C_BD_TI0806B_1d_max", "BF8_L2C_BD_TI0806C_1d_max"
    };

    private String[] luGangG_I = new String[]{
            "BF8_L2C_BD_TI0807A_1d_max", "BF8_L2C_BD_TI0807B_1d_max", "BF8_L2C_BD_TI0807C_1d_max", "BF8_L2C_BD_TI0808A_1d_max",
            "BF8_L2C_BD_TI0808B_1d_max", "BF8_L2C_BD_TI0808C_1d_max", "BF8_L2C_BD_TI0809A_1d_max", "BF8_L2C_BD_TI0809B_1d_max",
            "BF8_L2C_BD_TI0809C_1d_max", "BF8_L2C_BD_TI0810A_1d_max", "BF8_L2C_BD_TI0810B_1d_max", "BF8_L2C_BD_TI0810C_1d_max",
            "BF8_L2C_BD_TI0811A_1d_max", "BF8_L2C_BD_TI0811B_1d_max", "BF8_L2C_BD_TI0811C_1d_max", "BF8_L2C_BD_TI0812A_1d_max",
            "BF8_L2C_BD_TI0812B_1d_max", "BF8_L2C_BD_TI0812C_1d_max", "BF8_L2C_BD_TI0813A_1d_max", "BF8_L2C_BD_TI0813B_1d_max",
            "BF8_L2C_BD_TI0813C_1d_max", "BF8_L2C_BD_TI0901A_1d_max", "BF8_L2C_BD_TI0901B_1d_max", "BF8_L2C_BD_TI0901C_1d_max",
            "BF8_L2C_BD_TI0902A_1d_max", "BF8_L2C_BD_TI0902B_1d_max", "BF8_L2C_BD_TI0902C_1d_max", "BF8_L2C_BD_TI0903A_1d_max",
            "BF8_L2C_BD_TI0903B_1d_max", "BF8_L2C_BD_TI0903C_1d_max", "BF8_L2C_BD_TI0904A_1d_max", "BF8_L2C_BD_TI0904B_1d_max",
            "BF8_L2C_BD_TI0904C_1d_max", "BF8_L2C_BD_TI0905A_1d_max", "BF8_L2C_BD_TI0905B_1d_max", "BF8_L2C_BD_TI0905C_1d_max",
            "BF8_L2C_BD_TI0906A_1d_max", "BF8_L2C_BD_TI0906B_1d_max", "BF8_L2C_BD_TI0906C_1d_max", "BF8_L2C_BD_TI0907A_1d_max",
            "BF8_L2C_BD_TI0907B_1d_max", "BF8_L2C_BD_TI0907C_1d_max", "BF8_L2C_BD_TI0908A_1d_max", "BF8_L2C_BD_TI0908B_1d_max",
            "BF8_L2C_BD_TI0908C_1d_max", "BF8_L2C_BD_TI0909A_1d_max", "BF8_L2C_BD_TI0909B_1d_max", "BF8_L2C_BD_TI0909C_1d_max"
    };

    private String[] luGangJ_M = new String[]{
            "BF8_L2C_BD_TI0910A_1d_max", "BF8_L2C_BD_TI0910B_1d_max", "BF8_L2C_BD_TI0910C_1d_max", "BF8_L2C_BD_TI0911A_1d_max",
            "BF8_L2C_BD_TI0911B_1d_max", "BF8_L2C_BD_TI0911C_1d_max", "BF8_L2C_BD_TI0912A_1d_max", "BF8_L2C_BD_TI0912B_1d_max",
            "BF8_L2C_BD_TI0912C_1d_max", "BF8_L2C_BD_TI0913A_1d_max", "BF8_L2C_BD_TI0913B_1d_max", "BF8_L2C_BD_TI0913C_1d_max",
            "BF8_L2C_BD_TI1001A_1d_max", "BF8_L2C_BD_TI1001B_1d_max", "BF8_L2C_BD_TI1002A_1d_max", "BF8_L2C_BD_TI1002B_1d_max",
            "BF8_L2C_BD_TI1003A_1d_max", "BF8_L2C_BD_TI1003B_1d_max", "BF8_L2C_BD_TI1004A_1d_max", "BF8_L2C_BD_TI1004B_1d_max",
            "BF8_L2C_BD_TI1005A_1d_max", "BF8_L2C_BD_TI1005B_1d_max", "BF8_L2C_BD_TI1006A_1d_max", "BF8_L2C_BD_TI1006B_1d_max",
            "BF8_L2C_BD_TI1007A_1d_max", "BF8_L2C_BD_TI1007B_1d_max", "BF8_L2C_BD_TI1008A_1d_max", "BF8_L2C_BD_TI1008B_1d_max",
            "BF8_L2C_BD_TI1009A_1d_max", "BF8_L2C_BD_TI1009B_1d_max", "BF8_L2C_BD_TI1010A_1d_max", "BF8_L2C_BD_TI1010B_1d_max",
            "BF8_L2C_BD_ATI1011A_1d_max", "BF8_L2C_BD_ATI1011B_1d_max", "BF8_L2C_BD_TI1012A_1d_max", "BF8_L2C_BD_TI1012B_1d_max",
            "BF8_L2C_BD_TI1013A_1d_max", "BF8_L2C_BD_TI1013B_1d_max", "BF8_L2C_BD_TI1101A_1d_max", "BF8_L2C_BD_TI1101B_1d_max",
            "BF8_L2C_BD_TI1102A_1d_max", "BF8_L2C_BD_TI1102B_1d_max", "BF8_L2C_BD_TI1103A_1d_max", "BF8_L2C_BD_TI1103B_1d_max",
            "BF8_L2C_BD_TI1104A_1d_max", "BF8_L2C_BD_TI1104B_1d_max", "BF8_L2C_BD_TI1105A_1d_max", "BF8_L2C_BD_TI1105B_1d_max",
            "BF8_L2C_BD_TI1106A_1d_max", "BF8_L2C_BD_TI1106B_1d_max", "BF8_L2C_BD_TI1107A_1d_max", "BF8_L2C_BD_TI1107B_1d_max",
            "BF8_L2C_BD_TI1108A_1d_max", "BF8_L2C_BD_TI1108B_1d_max", "BF8_L2C_BD_TI1109A_1d_max", "BF8_L2C_BD_TI1109B_1d_max",
            "BF8_L2C_BD_TI1110A_1d_max", "BF8_L2C_BD_TI1110B_1d_max", "BF8_L2C_BD_TI1111A_1d_max", "BF8_L2C_BD_TI1111B_1d_max",
            "BF8_L2C_BD_TI1112A_1d_max", "BF8_L2C_BD_TI1112B_1d_max", "BF8_L2C_BD_TI1113A_1d_max", "BF8_L2C_BD_TI1113B_1d_max"
    };

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private GLDataUtil glDataUtil;

    @Autowired
    private HttpProperties httpProperties;

    @Autowired
    private JobProperties jobProperties;

    @Autowired
    private ReportIndexService reportIndexService;


    @Autowired
    private ReportCategoryTemplateService reportCategoryTemplateService;

    private void initialData() {
        result = new HashMap<>();
        startTime = null;
        endTime = null;
        categoriesList = new ArrayList<>();
        longTimeList = new ArrayList<>();
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_rishengchanfenxibaogao_day;
    }

    @Scheduled(cron = "0 0 10 * * ?")
    //@Scheduled(cron = "0 30/ 0 * * ?")
    //@Scheduled(cron = "0 10/30 * * * ?")
    public void mainTask() {
        initialData();
        initDateTime();
        String version8 = "1.0";
        mainDeal(version8);
        log.info("高炉日生产分析报告word生成完毕！");
    }

    private void mainDeal(String version) {
        // 处理当前时间
        result.put("current_date", DateUtils.format(DateUtil.addDays(new Date(), -1), DateUtil.yyyyMMddChineseFormat));
        String[] allTagNames = ArrayUtils.addAll(ArrayUtils.addAll(L1, L3), L4);
        JSONObject data = getDataByTag(allTagNames, startTime, endTime, version);
        dealPart1(data, version);
        dealPartTwo(version);
        handleCaoZuoCanShu(version);
        dealPart3(data);
        dealPart4(data);
        handleTapTempture(version);
        handleLuTiWenDu(version);
        List<ReportCategoryTemplate> reportCategoryTemplates = reportCategoryTemplateService.selectTemplateInfo(JobEnum.gl_rishengchanfenxibaogao_day.getCode(), LanguageEnum.cn_zh.getName(), "8高炉");
        if (CollectionUtils.isNotEmpty(reportCategoryTemplates)) {
            currentTemplate = reportCategoryTemplates.get(0);
            String templatePath = currentTemplate.getTemplatePath();
            log.info("高炉日生产分析报告模板路径：" + templatePath);
            comm(templatePath);
        }
    }

    /**
     * 查询BrandCode,可能有多个，最多三个
     * @param version
     * @param dateQuery
     * @param type
     * @return 包含BrandCode的JSONArray
     */
    private List<String> getBrandCodeData(String version, DateQuery dateQuery, String type) {
        List<String> brandCodeData = new ArrayList<>();
        Map<String, String> queryParam = new HashMap();
        queryParam.put("startTime", Objects.requireNonNull(dateQuery.getStartTime().getTime()).toString());
        queryParam.put("endTime", Objects.requireNonNull(dateQuery.getEndTime().getTime()).toString());
        queryParam.put("type", type);
        String url = getUrl(version) + "/brandCodes/getBrandCodes";
        String result = httpUtil.get(url, queryParam);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if(null != jsonObject){
            JSONArray data = jsonObject.getJSONArray("data");
            if(null!= data){
                for (Object o : data) {
                    brandCodeData.add(String.valueOf(o));
                }
            }
        }
        return brandCodeData;
    }

    private String getAnalysisValuesByCodeUrl(String version, long time, String type, String brandCode) {
        return String.format(getUrl(version) + "/analysisValue/clock/%s?type=%s&brandcode=%s", time, type, brandCode);
    }

    private String getAnalysisValuesByTypeUrl(String version) {
        return getUrl(version) + "/analysisValues/rangeByType";
    }

    private String getAnalysisValuesByCodeUrl(String version) {
        return getUrl(version) + "/analysisValues/rangeByCode";
    }

    private List<AnalysisValue> getAnalysisValuesByUrl(String url, DateQuery dateQuery, String name, String brandCode) {
        Map<String, String> queryParam = new HashMap();
        queryParam.put("from", Objects.requireNonNull(dateQuery.getQueryStartTime()).toString());
        queryParam.put("to", Objects.requireNonNull(dateQuery.getQueryEndTime()).toString());
        //materialType
        queryParam.put(name, brandCode);
        String data = httpUtil.get(url, queryParam);
        // 根据json映射对象DTO
        AnalysisValueDTO analysisValueDTO = null;
        if (StringUtils.isNotBlank(data)) {
            analysisValueDTO = JSON.parseObject(data, AnalysisValueDTO.class);
        }
        if (Objects.isNull(analysisValueDTO)) {
            return null;
        }
        return analysisValueDTO.getData();
    }

    private List<AnalysisValue> getAnalysisValuesByType(String version, DateQuery dateQuery, String name, String brandCode) {
        return getAnalysisValuesByUrl(getAnalysisValuesByTypeUrl(version), dateQuery, name, brandCode);
    }

    private List<AnalysisValue> getAnalysisValuesByBrandCode(String version, DateQuery dateQuery, String name, String brandCode) {
        return getAnalysisValuesByUrl(getAnalysisValuesByCodeUrl(version), dateQuery, name, brandCode);
    }

    private List<AnalysisValue> getAnalysisValuesByCode(String version, DateQuery dateQuery, String type, String brandCode) {
        String url = getAnalysisValuesByCodeUrl(version, dateQuery.getQueryEndTime(), type, brandCode);
        String data = httpUtil.get(url);
        // 根据json映射对象DTO
        AnalysisValueDTO analysisValueDTO = null;
        if (StringUtils.isNotBlank(data)) {
            analysisValueDTO = JSON.parseObject(data, AnalysisValueDTO.class);
        }
        if (Objects.isNull(analysisValueDTO)) {
            return null;
        }
        return analysisValueDTO.getData();
    }

    private void handleAnalysisValues(List<AnalysisValue> analysisValues, String[] array, String prefix, List<String> list) {
        if(Objects.isNull(analysisValues) || CollectionUtils.isEmpty(analysisValues)) {
            return;
        }
        for (String item:array) {
            BigDecimal averageValue = analysisValues.stream().map(AnalysisValue::getValues)
                    .map(e -> e.get(item)).filter(e -> e != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(analysisValues.size()), 4, BigDecimal.ROUND_HALF_UP);
            if(!list.contains(item) && Objects.nonNull(averageValue)) {
                averageValue = averageValue.multiply(new BigDecimal(100));
            }
            result.put(prefix+"_"+item, df2.format(averageValue));
        }
    }

    private void dealPartTwo(String version) {
        DateQuery dateQuery = DateQueryUtil.buildDayAheadTwoHour(DateUtil.addDays(new Date(), -1));
        //焦炭
        handleAnalysisValues(getAnalysisValuesByType(version, dateQuery, "materialType", "COKE"), new String[]{"H2O", "Ad", "Vdaf"}, "COKE", Arrays.asList());
        handleAnalysisValues(getAnalysisValuesByCode(version, dateQuery, "LG", "KM-L_COKE"), new String[]{"M40", "M10", "CSR", "CRI"}, "COKE", Arrays.asList("M40", "M10", "CSR", "CRI"));
        //煤粉
        handleAnalysisValues(getAnalysisValuesByType(version, dateQuery, "materialType", "COAL"), new String[]{"H2O", "Vdaf", "Fcad"}, "COAL", Arrays.asList());
        //烧结
        List<AnalysisValue> sinterList = getAnalysisValuesByBrandCode(version, dateQuery, "brandCode", "S4_SINTER");
        if(Objects.isNull(sinterList) || CollectionUtils.isEmpty(sinterList)) {
            sinterList = getAnalysisValuesByBrandCode(version, dateQuery, "brandCode", "S1_SINTER");
        }
        handleAnalysisValues(sinterList, new String[]{"TFe", "FeO", "CaO", "SiO2", "MgO", "Al2O3", "B2"}, "SINTER", Arrays.asList("B2"));
        //球团
        handleAnalysisValues(getAnalysisValuesByType(version, dateQuery, "materialType", "PELLETS"), new String[]{"TFe", "CaO", "SiO2"}, "PELLETS", Arrays.asList());
        //块矿
        String brandCodeType = "LUMPORE";
        String oreBlockType = "LC";
        List<AnalysisValue> allAnalysisValues = new ArrayList<>();
        List<String> list = getBrandCodeData(version, dateQuery, brandCodeType);
        if(Objects.nonNull(list) && CollectionUtils.isNotEmpty(list)) {
            for (String brandCode:list) {
                allAnalysisValues.addAll(getAnalysisValuesByCode(version, dateQuery, oreBlockType, brandCode));
            }
        }
        handleAnalysisValues(allAnalysisValues, new String[]{"TFe", "SiO2", "Al2O3"}, brandCodeType, Arrays.asList());
    }

    private TagValue getLatestMaxTag(String version, String[] tagNames, int days) {
        TagValue tagValue = null;
        Date date = DateUtil.addDays(new Date(), days);
        List<TagValue> list = getLatestTagValueList(date, version, Arrays.asList(tagNames));
        if(Objects.nonNull(list) && CollectionUtils.isNotEmpty(list)) {
            tagValue = list.get(list.size() - 1);
        }
        return tagValue;
    }

    /**
     * 批量获取Latest TagValue
     * @param date
     * @param version
     * @param tagNames
     * @return
     */
    private List<TagValue> getLatestTagValueList(Date date, String version, List<String> tagNames) {
        String url = getUrl(version) + "/tagValues/latest/";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", tagNames);
        String chargeNoData = httpUtil.postJsonParams(url + date.getTime(), jsonObject.toJSONString());
        List<TagValue> list = null;
        TagValueListDTO tagValueListDTO = null;
        if (StringUtils.isNotBlank(chargeNoData)) {
            tagValueListDTO = JSON.parseObject(chargeNoData, TagValueListDTO.class);
            if (Objects.isNull(tagValueListDTO) || CollectionUtils.isEmpty(tagValueListDTO.getData())) {
                log.warn("根据tagName[{}]获取[{}]的latest TagValueList数据为空", tagNames, date);
            } else {
                // 排序
                list = tagValueListDTO.getData();
                Iterator<TagValue> iterator = list.iterator();
                while (iterator.hasNext()) {
                    TagValue value = iterator.next();
                    if(Objects.isNull(value.getVal()) || Objects.isNull(value.getClock()) || value.getClock().
                            before(DateUtil.getDateBeginTime(date))) {
                        iterator.remove();
                    }
                }
                list.sort(Comparator.comparing(TagValue::getVal));
            }
        }
        return list;
    }

    private BigDecimal getMaxLuTiTemp(StringBuilder builder, String version, String[] tagNames, int days) {
        BigDecimal maxTemp = null;
        TagValue tagValue = getLatestMaxTag(version, tagNames, days);
        if (Objects.nonNull(tagValue)) {
            maxTemp = tagValue.getVal();
            builder.append(tagValue.getName());
        }
        return maxTemp;
    }

    /**
     * 炉体温度数据
     * @param version
     * @param tagNames
     * @return
     */
    private Map<String, BigDecimal> getLuTiWenDuData(String version, String[] tagNames) {
        StringBuilder builder = new StringBuilder();
        BigDecimal maxTemp = getMaxLuTiTemp(builder, version, tagNames, 0);
        Map<String, BigDecimal> tempMap = new HashMap<String, BigDecimal>(){};
        tempMap.put(builder.toString(), maxTemp);
        return tempMap;
    }

    /**
     * 处理炉体温度数据
     * @param map
     * @param num
     */
    private void dealLuTiWenDuData (Map<String, BigDecimal> map, BigDecimal yesterdayVal, int num,
                                    String text1, String text2, String text3, String text4, String prefix, String stuffix) {
        result.put(text1+ num, " ");
        result.put(text2+ num, " ");
        result.put(text3+ num, " ");
        result.put(text4+ num, " ");
        for(Map.Entry<String, BigDecimal> entry : map.entrySet()){
            String mapKey = entry.getKey();
            BigDecimal mapValue = entry.getValue();
            if (StringUtils.isNotBlank(mapKey) && mapValue != null) {
                result.put(text1+ num, mapKey.replace(prefix, "").replace(stuffix, ""));
                result.put(text2+ num, df2.format(mapValue));
                if (yesterdayVal != null) {
                    if (yesterdayVal.compareTo(mapValue) > 0) {
                        result.put(text4+ num, df2.format(yesterdayVal.subtract(mapValue)));
                        result.put(text3+ num, "降低");
                    } else {
                        result.put(text4+ num, df2.format(mapValue.subtract(yesterdayVal)));
                        result.put(text3+ num, "升高");
                    }
                }
            }
        }
    }

    /**
     * 炉体温度
     * @param version
     */
    private void handleLuTiWenDu (String version) {
        try {
            String text1 = "luti_text";
            String text2 = "luti_temp";
            String text3 = "luti_differ_text";
            String text4 = "luti_temp_differ";
            String prefix = "BF8_L2C_BD_";
            String suffix = "_1d_max";
            //炉缸
            Map<String, BigDecimal> luGangMap =  getLuTiWenDuData(version, luGang);
            BigDecimal luGangYesterdayMax =  getMaxLuTiTemp(new StringBuilder(), version, luGangMap.keySet().toArray(new String[luGangMap.keySet().size()]), -1);
            dealLuTiWenDuData(luGangMap, luGangYesterdayMax, 1, text1, text2, text3, text4, prefix, suffix);
            //炉腹
            Map<String, BigDecimal> luFuMap =  getLuTiWenDuData(version, luFu);
            BigDecimal luFuYesterdayMax =  getMaxLuTiTemp(new StringBuilder(), version, luFuMap.keySet().toArray(new String[luFuMap.keySet().size()]), -1);
            dealLuTiWenDuData(luFuMap, luFuYesterdayMax, 2, text1, text2, text3, text4, prefix, suffix);
            //炉腰 5
            Map<String, BigDecimal> luYaoMap =  getLuTiWenDuData(version, luYao);
            BigDecimal luYaoYesterdayMax =  getMaxLuTiTemp(new StringBuilder(), version, luYaoMap.keySet().toArray(new String[luYaoMap.keySet().size()]), -1);
            dealLuTiWenDuData(luYaoMap, luYaoYesterdayMax, 3, text1, text2, text3, text4, prefix, suffix);
            //炉身
            Map<String, BigDecimal> lushenMap =  getLuTiWenDuData(version, luShen);
            BigDecimal lushenYesterdayMax =  getMaxLuTiTemp(new StringBuilder(), version, lushenMap.keySet().toArray(new String[lushenMap.keySet().size()]), -1);
            dealLuTiWenDuData(lushenMap, lushenYesterdayMax, 4, text1, text2, text3, text4, prefix, suffix);

            dealChart3(version);
            dealChart4(version);
            dealChart5(version);
        } catch (Exception e) {
            log.error("处理炉体温度失败", e);
        }

    }

    /**
     * 获取某天最高温度
     * @param daysBefore
     * @param version
     * @param tagNames
     * @return
     */
    private BigDecimal getMaxTemp(StringBuilder builder, int daysBefore, String version, String[] tagNames) {
        String name = "";
        BigDecimal tempMax = new BigDecimal(0);
        Date date = DateUtil.addDays(new Date(), daysBefore);
        DateQuery dateQueryNoDelay = DateQueryUtil.buildTodayNoDelay(date);
        JSONObject jsonObject = getDataByTag(tagNames, dateQueryNoDelay.getStartTime(), dateQueryNoDelay.getEndTime(), version);
        if (jsonObject != null && jsonObject.size() > 0) {
            for (String tagName:tagNames) {
                JSONObject tagObject = jsonObject.getJSONObject(tagName);
                Map<String, Object> innerMap = tagObject == null ? null : tagObject.getInnerMap();
                if (Objects.nonNull(innerMap)) {
                    Set<String> keySet = innerMap.keySet();
                    for (String key:keySet) {
                        BigDecimal tagValue = (BigDecimal) innerMap.get(key);
                        if (tagValue == null) {
                            tempMax = null;
                        } else if (Objects.nonNull(tempMax) && tagValue.compareTo(tempMax) > 0) {
                            tempMax = tagValue;
                            name = tagName;
                        }
                    }
                }
            }
        }
        builder.append(name);
        return tempMax;
    }

    /**
     * 获取某天最高温度
     * @param version
     * @param tagNames
     * @return
     */
    private Map<String, BigDecimal> getMaxTempMap(String version, String[] tagNames) {
        StringBuilder builder = new StringBuilder();
        BigDecimal maxTemp = getMaxTemp(builder, 0, version, tagNames);
        Map<String, BigDecimal> tempMap = new HashMap<String, BigDecimal>(){};
        tempMap.put(builder.toString(), maxTemp);
        return tempMap;
    }

    /**
     * 处理铁口温度
     * @param version
     */
    private void handleTapTempture (String version) {
        try {
            String text1 = "tap_text";
            String text2 = "tap_temp";
            String text3 = "tap_temp_text";
            String text4 = "tap_temp_differ";

            String prefix = "BF8_L2C_BD_";
            String stuffix = "_1d_max";
            //默认值，防止接口中没有数据
            for(int i = 0; i < 4; i++) {
                result.put(text1 + (i + 1), " ");
                result.put(text2 + (i + 1), " ");
                result.put(text3 + (i + 1), " ");
                result.put(text4 + (i + 1), " ");
            }
            //今日最高温度
            Map<String, BigDecimal> todayACMap = getMaxTempMap(version, luGangA_C);
            //昨日最高温度
            BigDecimal maxACTemp = getMaxTemp(new StringBuilder(), -1, version, todayACMap.keySet().toArray(new String[todayACMap.keySet().size()]));
            dealLuTiWenDuData(todayACMap, maxACTemp, 1, text1, text2, text3, text4, prefix, stuffix);
            Map<String, BigDecimal> todayDFMap = getMaxTempMap(version, luGangD_F);
            BigDecimal maxDFTemp = getMaxTemp(new StringBuilder(), -1, version, todayDFMap.keySet().toArray(new String[todayDFMap.keySet().size()]));
            dealLuTiWenDuData(todayDFMap, maxDFTemp, 2, text1, text2, text3, text4, prefix, stuffix);
            Map<String, BigDecimal> todayGIMap = getMaxTempMap(version, luGangG_I);
            BigDecimal maxGITemp = getMaxTemp(new StringBuilder(), -1, version, todayGIMap.keySet().toArray(new String[todayGIMap.keySet().size()]));
            dealLuTiWenDuData(todayGIMap, maxGITemp, 3, text1, text2, text3, text4, prefix, stuffix);
            Map<String, BigDecimal> todayJMMap = getMaxTempMap(version, luGangJ_M);
            BigDecimal maxJMTemp = getMaxTemp(new StringBuilder(), -1, version, todayJMMap.keySet().toArray(new String[todayJMMap.keySet().size()]));
            dealLuTiWenDuData(todayJMMap, maxJMTemp, 4, text1, text2, text3, text4, prefix, stuffix);
        } catch (Exception e) {
            log.error("处理铁口温度失败", e);
        }
    }

    /**
     * 获取第一列对应的数据
     * @param version
     * @return
     */
    private List<ProcessCardDto> getVaues(String version) {
        String url = getUrl(version) + "/processCard/selectLatest";
        String jsonData = httpUtil.get(url);
        JsonListResult<ProcessCardDto> listResult = null;
        if (StringUtils.isNotBlank(jsonData)) {
            listResult = JSON.parseObject(jsonData, new TypeReference<JsonListResult<ProcessCardDto>>(){});
        }
        if (Objects.isNull(listResult)) {
            return null;
        }
        List<ProcessCardDto> rows = listResult.getRows();
        if (Objects.isNull(rows)) {
            return rows;
        }
        return rows;
    }

    /**
     * 处理列数据
     * @param version
     */
    private void dealColumns(String version) {
        String url = getUrl(version) + "/processCard/paramList";
        Map<String, String> queryParam = new HashMap();
        queryParam.put("type", "1");
        String jsonData = httpUtil.get(url, queryParam);
        JsonListResult<ProcessParameter> listResult = null;
        if (StringUtils.isNotBlank(jsonData)) {
            listResult = JSON.parseObject(jsonData, new TypeReference<JsonListResult<ProcessParameter>>(){});
        }
        if (Objects.isNull(listResult)) {
            return;
        }
        List<ProcessParameter> rows = listResult.getRows();
        if (Objects.isNull(rows)) {
            return;
        }
        // 排序，保证可以合并的单元格时连续的，方便合并
        rows.sort(comparing(ProcessParameter::getName));
        //获取第二列
        List<ProcessCardDto> processCardDtoList = getVaues(version);
        ProcessCardDto processCardDto = null;
        if (processCardDtoList != null && !CollectionUtils.isEmpty(processCardDtoList)) {
            processCardDto = processCardDtoList.get(0);
        }
        if (Objects.isNull(processCardDto)) {
            return;
        }
        List<ProcessGoal> goals = processCardDto.getGoals();

        List<Map<String, String>> listMap = new ArrayList<>();
        for (int i =0; i < rows.size(); i++) {
            ProcessParameter row = rows.get(i);
            Map<String, String> lm = new HashMap<>();
            String name = row.getName();
            if(!row.getUnit().equals("-"))
                lm.put("name", name + " " + row.getUnit());
            else
                lm.put("name", name);
            String tagName = row.getTagName();
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(new Date());
            JSONObject data = getDataByTag(new String[]{tagName}, dateQuery.getStartTime(), dateQuery.getEndTime(), version);
            BigDecimal total = new BigDecimal("0.00");
            BigDecimal realVal = null;
            if (Objects.nonNull(data) && data.size() > 0) {
                JSONObject object = data.getJSONObject(tagName);
                for (String key:object.keySet()) {
                    BigDecimal tagValue = object.getBigDecimal(key);
                    total = total.add(tagValue);
                }
                realVal = total.divide(new BigDecimal(object.size()), 2, BigDecimal.ROUND_HALF_UP);
                lm.put("real", realVal.toString());
            }
            Optional<ProcessGoal> optional = goals.stream().filter(item -> item.getParamId().equals(row.getId())).findFirst();
            if (optional.isPresent()) {
                ProcessGoal goal = optional.get();
                String avg;
                BigDecimal low = goal.getLow();
                BigDecimal up = goal.getUp();
                if (low != null && up != null) {
                    avg = low + "-" + up;
                    if (realVal != null) {
                        if (realVal.compareTo(up) > 0) {
                            lm.put("gap", realVal.subtract(up).toString());
                        } else if (realVal.compareTo(low) < 0) {
                            lm.put("gap", low.subtract(realVal).toString());
                        } else {
                            lm.put("gap", 0+"");
                        }
                    }
                } else if (low == null && up == null) {
                    avg = "";
                } else if (low == null) {
                    avg = ">" + up;
                    if (realVal != null) {
                        if (realVal.compareTo(up) > 0) {
                            lm.put("gap", realVal.subtract(up).toString());
                        } else {
                            lm.put("gap", 0+"");
                        }
                    }
                } else {
                    avg = "<" + low;
                    if (realVal != null) {
                        if (realVal.compareTo(low) < 0) {
                            lm.put("gap", low.subtract(realVal).toString());
                        } else {
                            lm.put("gap", 0+"");
                        }
                    }
                }
                lm.put("target", avg);
                if (i == 0) {
                    ProcessCard card = processCardDto.getProcessCard();
                    String note = "";
                    if (!Objects.isNull(card)) {
                        note = card.getNote();
                    }
                    lm.put("note", note);
                }
            }

            listMap.add(lm);
        }
        result.put("partThreeList", listMap);
    }

    /**
     * 动态生成操作参数表格
     * @param version
     */
    private void handleCaoZuoCanShu(String version) {
        try {
            dealColumns(version);
        } catch (Exception e) {
            log.error("生成操作参数表格失败", e);
        }

    }

    /**
     * 通过tag点获取得数据，组装到result
     *
     * @param data
     * @param prefix
     */
    private void dealPart(JSONObject data, String prefix, String[] tagNames, DecimalFormat df) {
        try {
            if (data != null && data.size() > 0) {
                for (int i = 0; i < tagNames.length; i++) {
                    Double doubleValue;
                    List<Double> valueObject = getValuesByTag(data, tagNames[i]);
                    if (Objects.nonNull(valueObject) && valueObject.size() > 0) {
                        doubleValue = valueObject.get(valueObject.size() - 1);
                        if((prefix + (i + 1)).equals("partTwo17")) {
                            Double partTwo13 = Double.valueOf((String)result.get("partTwo13"));
                            Double partTwo14 = Double.valueOf((String)result.get("partTwo14"));
                            doubleValue = partTwo13/partTwo14;
                        }
                        result.put(prefix + (i + 1), doubleValue == null ? 0d : df.format(doubleValue));
                    }
                }
            }
        } catch (Exception e) {
            log.error("高炉日生产分析报告处理数据失败", e);
        }
    }

    /**
     * 根据tagName从data中获取某天的数据，例如获取前一天的数据，beforeDays = 1
     * @param data
     * @param tagName
     * @param df
     * @return
     */
    private String getDataBeforeDays(JSONObject data, String tagName, DecimalFormat df) {
        String result = "";
        if (data != null && data.size() > 0) {
            Double doubleValue = 0d;
            List<Double> valueObject = getValuesByTag(data, tagName);
            if (Objects.nonNull(valueObject) && valueObject.size() > 0) {
                doubleValue = valueObject.get(valueObject.size() - 1 - 1);
                result = doubleValue == null ? "" : df.format(doubleValue);
            }
        }
        return result;
    }

    private void dealSingle(JSONObject data, String addr, String tagName, DecimalFormat df) {
        if (data != null && data.size() > 0) {
            Double doubleValue;
            List<Double> valueObject = getValuesByTag(data, tagName);
            if (Objects.nonNull(valueObject) && valueObject.size() > 0) {
                doubleValue = valueObject.get(valueObject.size() - 1);
                result.put(addr, doubleValue == null ? 0d : df.format(doubleValue));
            }
        }
    }

    /**
     * 获取精益信息
     * @param version
     * @return api数据
     */
    protected TapJyDTO getTapJyDTO(String version, DateQuery date, String dataType) {
        TapJyDTO tapJyDTO = null;
        Map<String, String> queryParam = new HashMap();
        queryParam.put("startTime",  String.valueOf(date.getStartTime().getTime()));
        queryParam.put("endTime",  String.valueOf(date.getEndTime().getTime()));
        queryParam.put("dataType",  dataType);
        queryParam.put("workShift",  "day");

        String tapJyDTOUrl = getUrl(version) + "/report/query/jygl";
        String tapJyDTOStr = httpUtil.get(tapJyDTOUrl, queryParam);
        if (StringUtils.isNotBlank(tapJyDTOStr)) {
            SuccessEntity<TapJyDTO> successEntity = JSON.parseObject(tapJyDTOStr, new TypeReference<SuccessEntity<TapJyDTO>>() {});
            if (Objects.isNull(successEntity) || Objects.isNull(successEntity.getData())) {
                log.warn("根据时间[{}]获取的tapJyDTO数据为空", date.getStartTime());
            } else {
                tapJyDTO = successEntity.getData();
            }
        }
        return tapJyDTO;
    }

    /**
     * 达标率
     * @param version
     */
    private void dealQualifiedRate(String version, DateQuery dateQuery, String dataType) {
        TapJyDTO tapJyDTO = getTapJyDTO(version, dateQuery, dataType);
        if (Objects.nonNull(tapJyDTO)) {
            int fz = tapJyDTO.getFz();
            int fm = tapJyDTO.getFm();
            if (Objects.nonNull(fm) && Objects.nonNull(fz) && fm != 0) {
                result.put("QualifiedRate_" + dataType, df2.format(((float)fz/fm)*100));
            }
        } else {
            result.put("QualifiedRate_" + dataType, " ");
        }
    }

    private void dealPart1(JSONObject data, String version) {
        try {
            dealPart(data, "partOne", L1, df2);
            //一级品率
            Date date = DateUtil.addDays(new Date(), -1);
            DateQuery dateQueryNoDelay = DateQueryUtil.buildTodayNoDelay(date);
            //合格率
            String[] arr = new String[]{"lw", "lz", "ts", "gl"};
            for (String dataType:arr) {
                dealQualifiedRate(version, dateQueryNoDelay, dataType);
            }
            dealChart1(data);
            dealChart2(data);

            double increase = dealIncreaseYesterday(data, "BF8_L2M_HMMassAct_1d_cur");
            if (increase >= 0d) {
                result.put("textOne1", "升高");
            } else {
                result.put("textOne1", "降低");
            }
            result.put("countOne1", df2.format(Math.abs(increase)));

            increase = dealIncreaseYesterday(data, "BF8_L2M_BX_CokeRate_1d_cur");
            if (increase >= 0d) {
                result.put("textOne2", "升高");
            } else {
                result.put("textOne2", "降低");
            }
            result.put("countOne2", df2.format(Math.abs(increase)));

            increase = dealIncreaseYesterday(data, "BF8_L2M_BX_CoalRate_1d_cur");
            if (increase >= 0d) {
                result.put("textOne3", "升高");
            } else {
                result.put("textOne3", "降低");
            }
            result.put("countOne3", df2.format(Math.abs(increase)));

            result.put("countOne4", df2.format(dealMonthTotal(data, "BF8_L2M_HMMassAct_1d_cur", false)));
            result.put("countOne5", df2.format(dealMonthTotal(data, "BF8_L2M_BX_CokeRate_1d_cur", true)));
            result.put("countOne6", df2.format(dealMonthTotal(data, "BF8_L2M_BX_CoalRate_1d_cur", true)));
            result.put("countOne7", df2.format(dealMonthTotal(data, "BF8_L2M_BX_FuelRate_1d_cur", true)));
        } catch (Exception e) {
            log.error("高炉日生产分析报告处理part1失败", e);
        }
    }

    private void dealPart3(JSONObject data){
        try {
            dealPart(data, "partThree", L3, df2);
            dealSingle(data, "partThree3", "BF8_L2C_BD_ColdBlastPress_1d_avg", df3);
            dealSingle(data, "partThree4", "BF8_L2M_PressDiff_1d_avg", df3);
            dealSingle(data, "partThree5", "BF8_L2C_BD_TopPress_1d_avg", df3);
            result.put("offsetWet", df2.format(dealOffset(5d,10d,"BF8_L2C_BD_BH_1d_avg",data)));
            result.put("offsetBV", df2.format(dealOffset(5700d,5900d,"BF8_L2C_BD_ColdBlastFlow_1d_avg",data)));
            result.put("offsetBP", df3.format(dealOffset(0d,0.42d,"BF8_L2C_BD_ColdBlastPress_1d_avg",data)));
            result.put("offsetP", df3.format(dealOffset(0d,0.183d,"BF8_L2M_PressDiff_1d_avg",data)));
            result.put("offsetTP", df3.format(dealOffset(0.228d,0.236d,"BF8_L2C_BD_TopPress_1d_avg",data)));
            result.put("offsetPT", df2.format(dealOffset(1505d,1530d,"BF8_L2M_HMTemp_1d_avg",data)));
            result.put("offsetFR", df2.format(dealOffset(515d,525d,"BF8_L2M_BX_FuelRate_1d_cur",data)));
            result.put("offsetCO", df2.format(dealOffset(47d,52d,"BF8_L2M_GasUtilization_1d_avg",data)));
        } catch (Exception e) {
            log.error("高炉日生产分析报告处理part3失败", e);
        }
    }

    private void dealPart4(JSONObject data) {
        try {
            dealPart(data, "partFour", L4, df2);
            String yesterdayData = getDataBeforeDays(data, L4[0], df2);
            if (StringUtils.isNotBlank(yesterdayData)) {
                result.put("yesterday_temp", yesterdayData);
            } else {
                result.put("yesterday_temp", " ");
            }
        } catch (Exception e) {
            log.error("高炉日生产分析报告处理part4失败", e);
        }
    }

    private double dealOffset(Double min, Double max, String tagName, JSONObject data) {
        double result = 0d;
        List<Double> tagObject = getValuesByTag(data, tagName);
        if(Objects.nonNull(tagObject) && tagObject.size() > 0) {
            Double actual = tagObject.get(tagObject.size() - 1);
            if (actual != null) {
                if (actual > max) {
                    result = actual - max;
                } else if (actual < min) {
                    result = actual - min;
                }
            }
        }
        return result;
    }

    private double dealIncreaseYesterday (JSONObject data, String tagName) {
        double result = 0d;
        List<Double> tagObject = getValuesByTag(data, tagName);
        if(Objects.nonNull(tagObject) && tagObject.size() > 0) {
            Double yesterday = tagObject.get(tagObject.size() - 1);
            Double twoDaysAgo = tagObject.get(tagObject.size() - 2);
            if (yesterday != null) {
                if (twoDaysAgo != null) {
                    result = yesterday - twoDaysAgo;
                }
            }
        }
        return result;
    }

    private Double dealMonthTotal (JSONObject data, String tagName, Boolean isAvg) {
        Double result;
        Double total = 0d;
        Double count = 0d;
        List<Double> tagObject = getValuesByTag(data, tagName);
        if(Objects.nonNull(tagObject) && tagObject.size() > 0) {
            for (Double item : tagObject) {
                if (item != null) {
                    total += item;
                    count++;
                }
            }
        }
        if (isAvg && count > 0d) {
            result = total / count;
        }
        else {
            result = total;
        }
        return result;
    }

    private void dealChart1(JSONObject data) {
        List<Double> tagObject1 = getValuesByTag(data, "BF8_L2M_HMMassAct_1d_cur");
        List<Double> tagObject2 = getValuesByTag(data, "BF8_L2M_BX_FuelRate_1d_cur");
        List<Double> tempObject1 = new ArrayList<>(tagObject1);
        List<Double> tempObject2 = new ArrayList<>(tagObject2);
        tempObject1.removeAll(Collections.singleton(null));
        tempObject2.removeAll(Collections.singleton(null));
        double max1 = tempObject1.size() > 0 ? Collections.max(tempObject1) * 1.2 : 10000.0;
        double min1 = tempObject1.size() > 0 ? Collections.min(tempObject1) * 0.8 : 0.0;
        double max2 = tempObject2.size() > 0 ? Collections.max(tempObject2) * 1.2 : 600.0;
        double min2 = tempObject2.size() > 0 ? Collections.min(tempObject2) * 0.8 : 400.0;

        // 标注类别
        Vector<Serie> series1 = new Vector<>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("产量", tagObject1.toArray()));

        // 标注类别
        Vector<Serie> series2 = new Vector<>();
        series2.add(new Serie("燃料比", tagObject2.toArray()));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        String title1 = "";
        String categoryAxisLabel1 = "";
        String[] yLabels = {"产量(t)", "燃料比(kg/t)"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, 2, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg1", image1);
    }

    private void dealChart2(JSONObject data) {
        List<Double> tagObject1 = getValuesByTag(data, "BF8_L2M_BX_CokeRate_1d_cur");
        List<Double> tagObject2 = getValuesByTag(data, "BF8_L2M_BX_CoalRate_1d_cur");
        List<Double> tempObject1 = new ArrayList<>(tagObject1);
        List<Double> tempObject2 = new ArrayList<>(tagObject2);
        tempObject1.removeAll(Collections.singleton(null));
        tempObject2.removeAll(Collections.singleton(null));
        double max1 = tempObject1.size() > 0 ? Collections.max(tempObject1) * 1.2 : 10000.0;
        double min1 = tempObject1.size() > 0 ? Collections.min(tempObject1) * 0.8 : 0.0;
        double max2 = tempObject2.size() > 0 ? Collections.max(tempObject2) * 1.2 : 600.0;
        double min2 = tempObject2.size() > 0 ? Collections.min(tempObject2) * 0.8 : 400.0;

        // 标注类别
        Vector<Serie> series1 = new Vector<>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("焦比", tagObject1.toArray()));

        // 标注类别
        Vector<Serie> series2 = new Vector<>();
        series2.add(new Serie("煤比", tagObject2.toArray()));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        String title1 = "";
        String categoryAxisLabel1 = "";
        String[] yLabels = {"焦比(kg/t)", "煤比(kg/t)"};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, 2, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg2", image1);
    }

    private void dealChart3(String version) {
        String tagName = "BF8_L2C_BD_SoftTempDiff_1d_avg";
        JSONObject data = getDataByTag(new String[]{tagName}, startTime, endTime, version);
        List<Double> tagObject1 = getValuesByTag(data, tagName);
        List<Double> tempObject1 = new ArrayList<>(tagObject1);
        tempObject1.removeAll(Collections.singleton(null));
        double max1 = tempObject1.size() > 0 ? Collections.max(tempObject1) * 1.2 : 6.0;
        double min1 = tempObject1.size() > 0 ? Collections.min(tempObject1) * 0.8 : 0.0;
        double[] rangStarts = {min1};
        double[] rangEnds = {max1};
        // 标注类别
        Vector<Serie> series1 = new Vector<>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("水温差", tagObject1.toArray()));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);

        String title1 = "";
        String categoryAxisLabel1 = "";
        String[] yLabels = {""};

        int[] stack = {1, 1};
        int[] ystack = {1, 1};

        JFreeChart Chart1 = ChartFactory.createLineChartEachBatch(2,title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, rangStarts, rangEnds, 1, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg3", image1);
    }

    private List<Double> getMaxLuGangList(String version, String[] array) {
        List<Double> value = new ArrayList<>();
        for (int i = 29; i > -1; i--) {
            BigDecimal big = getMaxTemp(new StringBuilder(), -1 - i, version, array);
            Double val = null;
            if (null != big) {
                val = big.doubleValue();
                if (val < 0) {
                    val = null;
                }
            }
            value.add(val);
        }
        return value;
    }

    private void dealChart4(String version) {
        List<Double> luGangA_CList = getMaxLuGangList(version, luGangA_C);
        List<Double> luGangD_FList = getMaxLuGangList(version, luGangD_F);
        List<Double> luGangG_IList = getMaxLuGangList(version, luGangG_I);
        List<Double> luGangJ_MList = getMaxLuGangList(version, luGangJ_M);

        luGangA_CList.removeAll(Collections.singleton(null));
        luGangD_FList.removeAll(Collections.singleton(null));
        luGangG_IList.removeAll(Collections.singleton(null));
        luGangJ_MList.removeAll(Collections.singleton(null));
        double max1 = luGangA_CList.size() > 0 ? (Collections.max(luGangA_CList) * 1.2 == 0.0 ? 100.0: Collections.max(luGangA_CList) * 1.2) : 10000.0;
        double min1 = luGangA_CList.size() > 0 ? Collections.min(luGangA_CList) * 0.8 : 0.0;
        double max2 = luGangD_FList.size() > 0 ? (Collections.max(luGangD_FList) * 1.2 == 0.0 ? 100.0: Collections.max(luGangD_FList) * 1.2) : 10000.0;
        double min2 = luGangD_FList.size() > 0 ? Collections.min(luGangD_FList) * 0.8 : 0.0;
        double max3 = luGangG_IList.size() > 0 ? (Collections.max(luGangG_IList) * 1.2 == 0.0 ? 100.0: Collections.max(luGangG_IList) * 1.2) : 10000.0;
        double min3 = luGangG_IList.size() > 0 ? Collections.min(luGangG_IList) * 0.8 : 0.0;
        double max4 = luGangJ_MList.size() > 0 ? (Collections.max(luGangJ_MList) * 1.2 == 0.0 ? 100.0: Collections.max(luGangJ_MList) * 1.2) : 10000.0;
        double min4 = luGangJ_MList.size() > 0 ? Collections.min(luGangJ_MList) * 0.8 : 0.0;
        double max = Math.max((Math.max((Math.max(max1, max2)), max3)), max4);
        double min = Math.min((Math.min((Math.min(min1, min2)), min3)), min4);
        double[] rangStarts = {min,min,min,min};
        double[] rangEnds = {max,max,max,max};
        // 标注类别
        Vector<Serie> series1 = new Vector<>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("A-C", luGangA_CList.toArray()));
        // 标注类别
        Vector<Serie> series2 = new Vector<>();
        series2.add(new Serie("D-F", luGangD_FList.toArray()));
        // 标注类别
        Vector<Serie> series3 = new Vector<>();
        series3.add(new Serie("G-I", luGangG_IList.toArray()));
        // 标注类别
        Vector<Serie> series4 = new Vector<>();
        series4.add(new Serie("J-M", luGangJ_MList.toArray()));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);
        vectors.add(series3);
        vectors.add(series4);

        String title1 = "";
        String categoryAxisLabel1 = "";
        String[] yLabels = {"", "", "", ""};

        int[] stack = {1, 1, 1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChartEachBatch(2, title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, rangStarts, rangEnds, 4, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg4", image1);
    }

    private List<Double> getMaxLuTiWenDuList(String version, String[] array, String prefix, String suffix) {
        List<Double> value = new ArrayList<>();
        for (int i = 29; i > -1; i--) {
            //炉缸
            BigDecimal big = getMaxLuTiTemp(new StringBuilder(), version, array, -1 - i);
            Double val = 0d;;
            if (null != big) {
                val = big.doubleValue();
            }
            value.add(val);
        }
        return value;
    }

    private void dealChart5(String version) {
        String prefix = "BF8_L2C_BD_";
        String suffix = "_1d_max";
        List<Double> luGangList = getMaxLuTiWenDuList(version, luGang, prefix, suffix);
        List<Double> luFuList = getMaxLuTiWenDuList(version, luFu, prefix, suffix);
        List<Double> luYaoList = getMaxLuTiWenDuList(version, luYao, prefix, suffix);
        List<Double> luShenList = getMaxLuTiWenDuList(version, luShen, prefix, suffix);

        luGangList.removeAll(Collections.singleton(null));
        luFuList.removeAll(Collections.singleton(null));
        luYaoList.removeAll(Collections.singleton(null));
        luShenList.removeAll(Collections.singleton(null));
        double max1 = luGangList.size() > 0 ? (Collections.max(luGangList) * 1.2 == 0.0 ? 100.0: Collections.max(luGangList) * 1.2) : 10000.0;
        double min1 = luGangList.size() > 0 ? Collections.min(luGangList) * 0.8 : 0.0;
        double max2 = luFuList.size() > 0 ? (Collections.max(luFuList) * 1.2 == 0.0 ? 100.0: Collections.max(luFuList) * 1.2) : 10000.0;
        double min2 = luFuList.size() > 0 ? Collections.min(luFuList) * 0.8 : 0.0;
        double max3 = luYaoList.size() > 0 ? (Collections.max(luYaoList) * 1.2 == 0.0 ? 100.0: Collections.max(luYaoList) * 1.2) : 10000.0;
        double min3 = luYaoList.size() > 0 ? Collections.min(luYaoList) * 0.8 : 0.0;
        double max4 = luShenList.size() > 0 ? (Collections.max(luShenList) * 1.2 == 0.0 ? 100.0: Collections.max(luShenList) * 1.2) : 10000.0;
        double min4 = luShenList.size() > 0 ? Collections.min(luShenList) * 0.8 : 0.0;
        double max = Math.max((Math.max((Math.max(max1, max2)), max3)), max4);
        double min = Math.min((Math.min((Math.min(min1, min2)), min3)), min4);
        double[] rangStarts = {min,min,min,min};
        double[] rangEnds = {max,max,max,max};
        // 标注类别
        Vector<Serie> series1 = new Vector<>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie("炉缸", luGangList.toArray()));
        // 标注类别
        Vector<Serie> series2 = new Vector<>();
        series2.add(new Serie("炉腹", luFuList.toArray()));
        // 标注类别
        Vector<Serie> series3 = new Vector<>();
        series3.add(new Serie("炉腰", luYaoList.toArray()));
        // 标注类别
        Vector<Serie> series4 = new Vector<>();
        series4.add(new Serie("炉身", luShenList.toArray()));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);
        vectors.add(series3);
        vectors.add(series4);

        String title1 = "";
        String categoryAxisLabel1 = "";
        String[] yLabels = {"", "", "", ""};

        int[] stack = {1, 1, 1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChartEachBatch(2, title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, rangStarts, rangEnds, 4, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put("jfreechartImg5", image1);
    }

    private List<Double> getValuesByTag (JSONObject data, String tagName) {
        if(Objects.isNull(data)) {
            return new ArrayList<Double>();
        }
        JSONObject tagObject = data.getJSONObject(tagName);
        List<Double> vals = new ArrayList<>();
        Map<String, Object> innerMap = tagObject == null ? null : tagObject.getInnerMap();
        for (String time : longTimeList) {
            Double val = null;
            if (innerMap != null) {
                BigDecimal big = (BigDecimal) innerMap.get(time);
                if (null != big) {
                    val = big.doubleValue();
                    if (val < 0) {
                        val = null;
                    }
                }
            }
            vals.add(val);
        }
        return vals;
    }

    /**
     * 通过tag点获取数据
     *
     * @param tagNames
     * @param startTime
     * @param endTime
     * @param version
     * @return
     */
    private JSONObject getDataByTag(String[] tagNames, Date startTime, Date endTime, String version) {
        JSONObject object = null;
        try {
            String apiPath = "/getTagValues/tagNamesInRange";
            JSONObject query = new JSONObject();
            query.put("endtime", endTime.getTime());
            query.put("starttime", startTime.getTime());
            query.put("tagnames", tagNames);
            SerializeConfig serializeConfig = new SerializeConfig();
            String jsonString = JSONObject.toJSONString(query, serializeConfig);
            String results = httpUtil.postJsonParams(getUrl(version) + apiPath, jsonString);
            JSONObject jsonObject = JSONObject.parseObject(results, Feature.OrderedField);
            if(Objects.nonNull(jsonObject)) {
                object = jsonObject.getJSONObject("data");
            }
        } catch (Exception e) {
            log.error("高炉日生产分析报告获取数据失败", e);
        }
        return object;
    }

    /**
     * 构造起始时间
     */
    private void initDateTime(){
        // 当前时间点
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        //日报是取前一天的数据
        //Date date = DateUtil.addDays(now, -1);
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        // test
        // cal.add(Calendar.DAY_OF_MONTH,-3);
        // 今日零点，作为结束时间点
        Date endDate = cal.getTime();
        // 前推30天，作为开始时间点
        cal.add(Calendar.DAY_OF_MONTH,-29);
        Date startDate = cal.getTime();

        startTime = startDate;
        endTime = endDate;

        while (startDate.before(endDate)) {
            // 拼接x坐标轴
            categoriesList.add(DateUtil.getFormatDateTime(DateUtil.addDays(startDate, -1), DateUtil.MMddChineseFormat));
            longTimeList.add(startDate.getTime()+"");
            // 递增日期
            cal.add(Calendar.DAY_OF_MONTH, 1);
            startDate = cal.getTime();
        }

        categoriesList.add(DateUtil.getFormatDateTime(DateUtil.addDays(endDate, -1), DateUtil.MMddChineseFormat));
        longTimeList.add(endDate.getTime()+"");
    }

    /**
     * 出铁数据接口
     * @param version
     * @return
     */
    private String getTapsInRange(String version) {
        return getUrl(version) + "/taps/sg/period";
    }

    /**
     * 8号高炉
     *
     * @param version
     * @return
     */
    private String getUrl(String version) {
//        if ("4.0".equals(version)) {
//            return httpProperties.getUrlApiSJThree();
//        } else {
//            return httpProperties.getUrlApiSJThree();
//        }
        return httpProperties.getUrlApiGLTwo();
    }

    // word跨行并单元格
    private void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            if (rowIndex == fromRow) {
                // The first merged cell is set with RESTART merge value
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
            } else {
                // Cells which join (merge) the first one, are set with CONTINUE
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    /**
     * 添加换行符，word中表格中直接替换或者替换字符串，如果包含换行符则会有问题
     * @param cell
     */
    private  void addBreakInCell(XWPFTableCell cell) {
        if(cell.getText() != null && cell.getText().contains("\n")) {
            for (XWPFParagraph p : cell.getParagraphs()) {
                for (XWPFRun run : p.getRuns()) {//XWPFRun对象定义具有一组公共属性的文本区域
                    if(run.getText(0)!= null && run.getText(0).contains("\n")) {
                        String[] lines = run.getText(0).split("\n");
                        if(lines.length > 0) {
                            run.setText(lines[0], 0); // set first line into XWPFRun
                            for(int i=1;i<lines.length;i++){
                                // add break and insert new text
                                run.addBreak();//中断
                                //run.addCarriageReturn();//回车符，但是不起作用
                                run.setText(lines[i]);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 生产word文档
     *
     * @param path
     */
    private void comm(String path) {
        try {
            String sequence = "8高炉";
            XWPFDocument doc = WordExportUtil.exportWord07(path, result);

            List<XWPFTable> tables = doc.getTables();

            mergeCellsVertically(tables.get(tables.size()-1), 4, 1, tables.get(tables.size()-1).getRows().size()-1);
            XWPFTableCell targetCell = tables.get(tables.size()-1).getRow(1).getCell(4);
            // 处理换行符
            addBreakInCell(targetCell);
            Date date = DateUtil.addDays(new Date(), -1);
            String fileName = String.format("%s_%s_%s.docx", sequence, currentTemplate.getTemplateName(), DateUtil.getFormatDateTime(date, "yyyyMMdd"));
            String filePath = jobProperties.getFilePath() + File.separator + "doc" + File.separator + fileName;
            FileOutputStream fos = new FileOutputStream(filePath);
            doc.write(fos);
            fos.close();

            ReportIndex reportIndex = new ReportIndex();
            reportIndex.setSequence(sequence)
                    .setReportCategoryCode(JobEnum.gl_rishengchanfenxibaogao_day.getCode())
                    .setName(fileName)
                    .setPath(filePath)
                    .setIndexLang(LanguageEnum.getByLang(currentTemplate.getTemplateLang()).getName())
                    .setIndexType(ReportTemplateTypeEnum.getType(currentTemplate.getTemplateType()).getCode())
                    .setRecordDate(new Date());

            reportIndexService.insertReportRecord(reportIndex);

            log.info("高炉日生产分析报告word文档生成完毕" + filePath);
        } catch (Exception e) {
            log.error("高炉日生产分析报告word文档失败", e);
        }
    }

    /**
     * 生成图片
     * @param chart
     * @return
     */
    private WordImageEntity image(JFreeChart chart) {
        WordImageEntity image = new WordImageEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            chart.getPlot().setBackgroundAlpha(0.1f);
            chart.getPlot().setNoDataMessage("当前没有有效的数据");
            ChartUtilities.writeChartAsJPEG(baos, chart, 600, 290);
            image.setHeight(256);
            image.setWidth(554);
            image.setData(baos.toByteArray());
            image.setType(WordImageEntity.Data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return image;
    }
}
