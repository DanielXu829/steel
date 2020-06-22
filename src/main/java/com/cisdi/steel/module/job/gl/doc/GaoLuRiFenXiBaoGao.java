package com.cisdi.steel.module.job.gl.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.WordImageEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.dto.response.gl.res.PageData;
import com.cisdi.steel.dto.response.gl.res.TapSgRow;
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
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
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
    private String[] L2 = new String[]{
            "BF8_L2M_ANA_COKE_H2O_1d_avg",
            "BF8_L2M_ANA_COKE_Vdaf_1d_avg","BF8_L2M_ANA_COKE_Ad_1d_avg","BF8_L2M_ANA_COKE_M40_1d_avg",
            "BF8_L2M_ANA_COKE_M10_1d_avg","BF8_L2M_ANA_COKE_CSR_1d_avg","BF8_L2M_ANA_COKE_CRI_1d_avg",
            "BF8_L2M_ANA_COAL_H2O_1d_avg","BF8_L2M_ANA_COAL_Vdaf_1d_avg","BF8_L2M_ANA_COAL_Fcad_1d_avg",
            "BF8_L2M_ANA_SINTER_TFe_1d_avg","BF8_L2M_ANA_SINTER_FeO_1d_avg","BF8_L2M_ANA_SINTER_CaO_1d_avg",
            "BF8_L2M_ANA_SINTER_SiO2_1d_avg","BF8_L2M_ANA_SINTER_MgO_1d_avg","BF8_L2M_ANA_SINTER_Al203_1d_avg",
            "BF8_L2M_ANA_SINTER_TFe_B2_avg","BF8_L2M_ANA_PELLETS_TFe_1d_avg","BF8_L2M_ANA_PELLETS_CaO_1d_avg",
            "BF8_L2M_ANA_PELLETS_SiO2_1d_avg","BF8_L2M_ANA_LUMPORE_TFe_1d_avg","BF8_L2M_ANA_LUMPORE_SiO2_1d_avg",
            "BF8_L2M_ANA_LUMPORE_Al2O3_1d_avg"
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
            "TI0707C", "TI0708A", "TI0708B", "TI0708C", "TI0709A", "TI0709B", "TI0709C", "TI0710A", "TI0710B",
            "TI0710C", "TI0711A", "TI0711B", "TI0711C", "TI0712A", "TI0712B", "TI0712C", "TI0713A", "TI0713B", "TI0713C",
            "ATI0801A", "ATI0801B", "ATI0801C", "TI0802A", "TI0802B", "TI0802C", "TI0803A", "TI0803B", "TI0803C", "TI0804A",
            "TI0804B", "TI0804C", "TI0805A", "TI0805B", "TI0805C", "TI0806A", "TI0806B", "TI0806C", "TI0807A", "TI0807B",
            "TI0807C", "TI0808A", "TI0808B", "TI0808C", "TI0809A", "TI0809B", "TI0809C", "TI0810A", "TI0810B", "TI0810C",
            "TI0811A", "TI0811B", "TI0811C", "TI0812A", "TI0812B", "TI0812C", "TI0813A", "TI0813B", "TI0813C", "TI0901A",
            "TI0901B", "TI0901C", "TI0902A", "TI0902B", "TI0902C", "TI0903A", "TI0903B", "TI0903C", "TI0904A", "TI0904B",
            "TI0904C", "TI0905A", "TI0905B", "TI0905C", "TI0906A", "TI0906B", "TI0906C", "TI0907A", "TI0907B", "TI0907C",
            "TI0908A", "TI0908B", "TI0908C", "TI0909A", "TI0909B", "TI0909C", "TI0910A", "TI0910B", "TI0910C", "TI0911A",
            "TI0911B", "TI0911C", "TI0912A", "TI0912B", "TI0912C", "TI0913A", "TI0913B", "TI0913C", "TI1001A", "TI1001B",
            "TI1002A", "TI1002B", "TI1003A", "TI1003B", "TI1004A", "TI1004B", "TI1005A", "TI1005B", "TI1006A", "TI1006B",
            "TI1007A", "TI1007B", "TI1008A", "TI1008B", "TI1009A", "TI1009B", "TI1010A", "TI1010B", "ATI1011A", "ATI1011B",
            "TI1012A", "TI1012B", "TI1013A", "TI1013B", "TI1101A", "TI1101B", "TI1102A", "TI1102B", "TI1103A", "TI1103B",
            "TI1104A", "TI1104B", "TI1105A", "TI1105B", "TI1106A", "TI1106B","TI1107A", "TI1107B", "TI1108A", "TI1108B",
            "TI1109A", "TI1109B", "TI1110A", "TI1110B", "TI1111A", "TI1111B", "TI1112A", "TI1112B", "TI1113A", "TI1113B",
            "TI2101", "TI2102", "TI2103", "TI2104", "TI2105", "TI2106", "TI2107", "TI2108", "TI2201", "TI2202", "TI2203",
            "TI2204", "TI2205", "TI2206", "TI2207", "TI2208", "TI2301", "TI2302", "TI2303", "TI2304", "TI2305", "TI2307",
            "TI2308", "TI2401", "TI2402", "TI2403", "TI2404", "TI2405", "TI2406", "TI2407", "TI2408", "TI2409", "TI2410",
            "TI2306"
    };

    private String[] luFu = new String[]{
            "TI2501", "TI2502", "TI2503", "TI2504", "TI2601", "TI2602", "TI2603", "TI2604", "TI2605", "TI2606",
            "TI2607", "TI2608", "TI2609", "TI2610", "TI2611", "TI2612", "TI2613"
    };

    private String[] luYao = new String[]{
            "TI2701", "TI2702", "TI2703", "TI2704", "TI2801", "TI2802", "TI2803", "TI2804", "TI2805", "TI2806", "TI2807",
            "TI2808", "TI2809", "TI2810", "TI2811", "TI2812", "TI2813"
    };

    private String[] luShen = new String[]{
            "TI2901", "TI2902", "TI2903", "TI2904", "TI3001", "TI3002", "TI3003", "TI3004", "TI3005", "TI3006", "TI3007",
            "TI3008", "TI3009", "TI3010", "TI3011", "TI3012", "TI3013", "TI3101", "TI3102", "TI3103", "TI3104", "TI3105",
            "TI3106", "TI3107", "TI3108", "TI3109", "TI3110", "TI3111", "TI3112", "TI3113", "TI3201", "TI3202", "TI3203",
            "TI3204", "TI3301", "TI3302", "TI3303", "TI3304", "TI3305", "TI3306", "TI3307", "TI3308", "TI3309", "TI3310",
            "TI3311", "TI3312", "TI3401", "TI3402", "TI3403", "TI3404", "TI3405", "TI3406", "TI3407", "TI3408", "TI3409",
            "TI3410", "TI3411", "TI3412", "TI3501", "TI3502", "TI3503", "TI3504", "TI3505", "TI3506", "TI3507", "TI3508",
            "TI3509", "TI3510", "TI3511", "TI3601", "TI3602", "TI3603", "TI3604", "TI3605", "TI3606", "TI3607", "TI3608",
            "TI3609", "TI3610"
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

    //@Scheduled(cron = "0 0 23 * * ?")
    //@Scheduled(cron = "0 30/ 0 * * ?")
    @Scheduled(cron = "0 10/30 * * * ?")
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
        String[] allTagNames = ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(L1, L2), L3), L4);
        JSONObject data = getDataByTag(allTagNames, startTime, endTime, version);
        dealPart1(data, version);
        dealPart(data, "partTwo", L2, df2);
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

    private BigDecimal getMaxLuTiTemp(StringBuilder builder, String version, String[] tagNames, int days, String prefix, String suffix) {
        String maxTempTagName = "";
        BigDecimal maxTemp = new BigDecimal(0);
        for (String name : tagNames) {
            String tagName = prefix + name + suffix;
            BigDecimal tagValue = getLatestMaxTagValue(version, tagName, days);
            if (tagValue != null && tagValue.compareTo(maxTemp) > 0) {
                maxTemp = tagValue;
                maxTempTagName = name;
            }
        }
        builder.append(maxTempTagName);
        return maxTemp;
    }

    /**
     * 炉体温度数据
     * @param version
     * @param tagNames
     * @param prefix
     * @param suffix
     * @return
     */
    private Map<String, BigDecimal> getLuTiWenDuData(String version, String[] tagNames, String prefix, String suffix) {
        StringBuilder builder = new StringBuilder();
        BigDecimal maxTemp = getMaxLuTiTemp(builder, version, tagNames, -1, prefix, suffix);
        Map<String, BigDecimal> tempMap = new HashMap<String, BigDecimal>(){};
        tempMap.put(builder.toString(), maxTemp);
        return tempMap;
    }

    /**
     * 获取单个tag的最大温度
     * @param version
     * @param tagName
     * @return
     */
    private BigDecimal getLatestMaxTagValue (String version, String tagName, int days) {
        BigDecimal maxTemp = new BigDecimal(0);
        String url = getUrl(version) + "/tagValue/latest";
        Map<String, String> param = new HashMap<>();
        Date date = DateUtil.addDays(new Date(), days);
        param.put("time", String.valueOf(date.getTime()));
        param.put("tagname", tagName);
        String results = httpUtil.get(url, param);
        JSONObject jsonObject = JSONObject.parseObject(results, Feature.OrderedField);
        if (Objects.nonNull(jsonObject)) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null && data.size() > 0) {
                BigDecimal tagValue  = (BigDecimal) data.get("val");
                if (tagValue != null && tagValue.compareTo(maxTemp) > 0) {
                    maxTemp = tagValue;
                }
            }
        }
        return maxTemp;
    }

    /**
     * 处理炉体温度数据
     * @param map
     * @param suffix
     */
    private void dealLuTiWenDuData (Map<String, BigDecimal> map, BigDecimal yesterdayVal, int suffix,
                                    String text1, String text2, String text3, String text4) {
        result.put(text1+suffix, " ");
        result.put(text2+suffix, " ");
        result.put(text3+suffix, " ");
        result.put(text4+suffix, " ");
        for(Map.Entry<String, BigDecimal> entry : map.entrySet()){
            String mapKey = entry.getKey();
            BigDecimal mapValue = entry.getValue();
            if (StringUtils.isNotBlank(mapKey) && mapValue != null) {
                result.put(text1+suffix, mapKey);
                result.put(text2+suffix, df2.format(mapValue));
                if (yesterdayVal != null) {
                    if (yesterdayVal.compareTo(mapValue) > 0) {
                        result.put(text4+suffix, df2.format(yesterdayVal.subtract(mapValue)));
                        result.put(text3+suffix, "降低");
                    } else {
                        result.put(text4+suffix, df2.format(mapValue.subtract(yesterdayVal)));
                        result.put(text3+suffix, "升高");
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
            Map<String, BigDecimal> luGangMap =  getLuTiWenDuData(version, luGang, prefix, suffix);
            BigDecimal luGangYesterdayMax =  getMaxLuTiTemp(new StringBuilder(), version, luGang, -2, prefix, suffix);
            dealLuTiWenDuData(luGangMap, luGangYesterdayMax, 1, text1, text2, text3, text4);
            //炉腹
            Map<String, BigDecimal> luFuMap =  getLuTiWenDuData(version, luFu, prefix, suffix);
            BigDecimal luFuYesterdayMax =  getMaxLuTiTemp(new StringBuilder(), version, luFu, -2, prefix, suffix);
            dealLuTiWenDuData(luFuMap, luFuYesterdayMax, 2, text1, text2, text3, text4);
            //炉腰 5
            Map<String, BigDecimal> luYaoMap =  getLuTiWenDuData(version, luYao, prefix, suffix);
            BigDecimal luYaoYesterdayMax =  getMaxLuTiTemp(new StringBuilder(), version, luYao, -2, prefix, suffix);
            dealLuTiWenDuData(luYaoMap, luYaoYesterdayMax, 3, text1, text2, text3, text4);
            //炉身
            Map<String, BigDecimal> lushenMap =  getLuTiWenDuData(version, luShen, prefix, suffix);
            BigDecimal lushenYesterdayMax =  getMaxLuTiTemp(new StringBuilder(), version, luShen, -2, prefix, suffix);
            dealLuTiWenDuData(lushenMap, lushenYesterdayMax, 4, text1, text2, text3, text4);

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
    private Map<String, BigDecimal> getMaxTempMap(String version, String[] tagNames, String prefix, String stuffix) {
        StringBuilder builder = new StringBuilder();
        BigDecimal maxTemp = getMaxTemp(builder, -1, version, tagNames);
        Map<String, BigDecimal> tempMap = new HashMap<String, BigDecimal>(){};
        tempMap.put(builder.toString().replace(prefix,"").replace(stuffix,""), maxTemp);
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
            Map<String, BigDecimal> todayACMap = getMaxTempMap(version, luGangA_C, prefix, stuffix);
            //昨日最高温度
            BigDecimal maxACTemp = getMaxTemp(new StringBuilder(), -2, version, luGangA_C);
            dealLuTiWenDuData(todayACMap, maxACTemp, 1, text1, text2, text3, text4);
            Map<String, BigDecimal> todayDFMap = getMaxTempMap(version, luGangD_F, prefix, stuffix);
            BigDecimal maxDFTemp = getMaxTemp(new StringBuilder(), -2, version, luGangD_F);
            dealLuTiWenDuData(todayDFMap, maxDFTemp, 2, text1, text2, text3, text4);
            Map<String, BigDecimal> todayDIMap = getMaxTempMap(version, luGangG_I, prefix, stuffix);
            BigDecimal maxGITemp = getMaxTemp(new StringBuilder(), -2, version, luGangG_I);
            dealLuTiWenDuData(todayDIMap, maxGITemp, 3, text1, text2, text3, text4);
            Map<String, BigDecimal> todayJMMap = getMaxTempMap(version, luGangJ_M, prefix, stuffix);
            BigDecimal maxJMTemp = getMaxTemp(new StringBuilder(), -2, version, luGangJ_M);
            dealLuTiWenDuData(todayJMMap, maxJMTemp, 4, text1, text2, text3, text4);
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
        if (data != null && data.size() > 0) {
            for (int i = 0; i < tagNames.length; i++) {
                Double doubleValue;
                List<Double> valueObject = getValuesByTag(data, tagNames[i]);
                if (valueObject.size() > 0) {
                    doubleValue = valueObject.get(valueObject.size() - 1);
                    result.put(prefix + (i + 1), doubleValue == null ? 0d : df.format(doubleValue));
                }
            }
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
            if (valueObject.size() > 0) {
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
            if (valueObject.size() > 0) {
                doubleValue = valueObject.get(valueObject.size() - 1);
                result.put(addr, doubleValue == null ? 0d : df.format(doubleValue));
            }
        }
    }

    /**
     * [S]达标率
     * @param version
     */
    private void dealSQualifiedRate (String version) {
        Map<String, String> queryParam = new HashMap<>();
        DateQuery dateQueryNoDelay = DateQueryUtil.buildTodayNoDelay(new Date());
        queryParam.put("starttime", Objects.requireNonNull(dateQueryNoDelay.getQueryStartTime()).toString());
        queryParam.put("endtime", Objects.requireNonNull(dateQueryNoDelay.getQueryEndTime()).toString());
        // 2、获取数据并反序列化为java对象
        String tapData = httpUtil.get(getTapsInRange(version), queryParam);
        if (StringUtils.isBlank(tapData)) {
            return;
        }
        com.cisdi.steel.dto.response.gl.res.PageData<TapSgRow> pageData = JSON.parseObject(tapData, new TypeReference<PageData<TapSgRow>>(){});
        if (Objects.isNull(pageData)) {
            return;
        }
        List<TapSgRow> tapSgRowData = pageData.getData();
        if (CollectionUtils.isEmpty(tapSgRowData)) {
            return;
        }
        tapSgRowData.sort(comparing(TapSgRow::getStartTime)); // 按时间先后进行排序
        int dataSize = tapSgRowData.size();
        if (dataSize == 0) {
            return;
        }
        // S合格的数据
        int count = 0;
        int total = 0;
        for (TapSgRow tapSgRow : tapSgRowData) {
            Map<String, Double> hmAnalysis = tapSgRow.getHmAnalysis();
            if (Objects.isNull(hmAnalysis) || hmAnalysis.size() == 0) {
                continue;
            }
            total++;
            Double value = hmAnalysis.get("S");
            if (value != null && value * 100 <= 0.03) {
                count++;
            }
        }
        //[S]达标率
        if (total > 0) {
            result.put("sQualifiedRate", df2.format((count/total)*100));
        } else {
            result.put("sQualifiedRate", " ");
        }
    }

    private void dealPart1(JSONObject data, String version) {
        dealPart(data, "partOne", L1, df2);
        //一级品率
        Date date = DateUtil.addDays(new Date(), -1);
        DateQuery dateQueryNoDelay = DateQueryUtil.buildTodayNoDelay(date);
        BigDecimal firstGradeRateInRange = glDataUtil.getFirstGradeRateInRange("8.0", dateQueryNoDelay);
        if (firstGradeRateInRange != null) {
            result.put("firstGradeRate", df2.format(firstGradeRateInRange));
        } else {
            result.put("firstGradeRate", " ");
        }
        //[S]达标率
        result.put("sQualifiedRate", " ");
        dealSQualifiedRate(version);
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
    }

    private void dealPart3(JSONObject data){
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
    }

    private void dealPart4(JSONObject data) {
        dealPart(data, "partFour", L4, df2);
        String yesterdayData = getDataBeforeDays(data, L4[0], df2);
        if (StringUtils.isNotBlank(yesterdayData)) {
            result.put("yesterday_temp", yesterdayData);
        } else {
            result.put("yesterday_temp", " ");
        }
    }

    private double dealOffset(Double min, Double max, String tagName, JSONObject data) {
        double result = 0d;
        List<Double> tagObject = getValuesByTag(data, tagName);
        Double actual = tagObject.get(tagObject.size() - 1);
        if (actual != null) {
            if (actual > max) {
                result = actual - max;
            } else if (actual < min) {
                result = actual - min;
            }
        }
        return result;
    }

    private double dealIncreaseYesterday (JSONObject data, String tagName) {
        double result = 0d;
        List<Double> tagObject = getValuesByTag(data, tagName);
        Double yesterday = tagObject.get(tagObject.size() - 1);
        Double twoDaysAgo = tagObject.get(tagObject.size() - 2);
        if (yesterday != null) {
            if (twoDaysAgo != null) {
                result = yesterday - twoDaysAgo;
            }
        }
        return result;
    }

    private Double dealMonthTotal (JSONObject data, String tagName, Boolean isAvg) {
        Double result;
        Double total = 0d;
        Double count = 0d;
        List<Double> tagObject = getValuesByTag(data, tagName);
        for (Double item : tagObject) {
            if (item != null) {
                total += item;
                count++;
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
            BigDecimal big = getMaxLuTiTemp(new StringBuilder(), version, array, -1 - i, prefix, suffix);
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
        String apiPath = "/getTagValues/tagNamesInRange";
        JSONObject query = new JSONObject();
        query.put("endtime", endTime.getTime());
        query.put("starttime", startTime.getTime());
        query.put("tagnames", tagNames);
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String results = httpUtil.postJsonParams(getUrl(version) + apiPath, jsonString);
        JSONObject jsonObject = JSONObject.parseObject(results, Feature.OrderedField);
        return jsonObject.getJSONObject("data");
    }

    /**
     * 构造起始时间
     */
    private void initDateTime(){
        // 当前时间点
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        //日报是取前一天的数据
        Date date = DateUtil.addDays(now, -1);
        cal.setTime(date);
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
            categoriesList.add(DateUtil.getFormatDateTime(startDate, DateUtil.MMddChineseFormat));
            longTimeList.add(startDate.getTime()+"");

            // 递增日期
            cal.add(Calendar.DAY_OF_MONTH, 1);
            startDate = cal.getTime();
        }

        categoriesList.add(DateUtil.getFormatDateTime(endDate, DateUtil.MMddChineseFormat));
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
