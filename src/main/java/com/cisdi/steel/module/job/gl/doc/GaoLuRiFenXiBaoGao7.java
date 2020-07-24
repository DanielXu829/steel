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
import com.cisdi.steel.dto.response.gl.CommentDataDTO;
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
public class GaoLuRiFenXiBaoGao7 extends AbstractExportWordJob {
    /**
     * doc最后结果
     */
    private HashMap<String, Object> result = null;
    private Date startTime = null;
    private Date endTime = null;
    private List<String> categoriesList = new ArrayList<>();
    private List<Long> longTimeList = new ArrayList<>();
    private ReportCategoryTemplate currentTemplate;
    private DecimalFormat df2 = new DecimalFormat("0.00");
    private DecimalFormat df3 = new DecimalFormat("0.000");
    private String[] L1 = new String[]{
            "BF7_L2M_BX_HMMass_1d_cur","BF7_L2M_BX_Productivity_1d_cur","BF7_L2M_BX_CokeRate_1d_cur","BF7_L2M_BX_CoalRate_1d_cur",
            "BF7_L2M_BX_FuelRate_1d_cur","BF7_L2C_BD_HotBlastTemp1_1d_avg"
    };
    private String[] L3 = new String[]{
            "BF7_L2C_BD_BH_1d_avg","BF7_L2C_BD_ColdBlastFlow_1d_avg",
            "BF7_L2C_BD_ColdBlastPress_1d_avg","BF7_L2M_PressDiff_1d_avg","BF7_L2C_BD_TopPress_1d_avg",
            "BF7_L2M_HMTemp_1d_avg","BF7_L2M_BX_FuelRate_1d_cur","BF7_L2M_GasUtilization_1d_avg"
    };
    private String[] L4 = new String[]{
            "BF7_L2C_BD_SoftTempDiff_1d_avg"
    };

    private String[] luGang = new String[]{
            "BF7_L2C_BD_ATI261_1d_max", "BF7_L2C_BD_ATI262_1d_max", "BF7_L2C_BD_ATI263_1d_max", "BF7_L2C_BD_ATI264_1d_max",
            "BF7_L2C_BD_ATI265_1d_max", "BF7_L2C_BD_ATI266_1d_max", "BF7_L2C_BD_ATI267_1d_max", "BF7_L2C_BD_ATI268_1d_max",
            "BF7_L2C_BD_ATI361_1d_max", "BF7_L2C_BD_ATI362_1d_max", "BF7_L2C_BD_ATI363_1d_max", "BF7_L2C_BD_ATI364_1d_max",
            "BF7_L2C_BD_ATI365_1d_max", "BF7_L2C_BD_ATI366_1d_max", "BF7_L2C_BD_ATI367_1d_max", "BF7_L2C_BD_ATI368_1d_max",
            "BF7_L2C_BD_ATI461_1d_max", "BF7_L2C_BD_ATI462_1d_max", "BF7_L2C_BD_ATI463_1d_max", "BF7_L2C_BD_ATI464_1d_max",
            "BF7_L2C_BD_ATI465_1d_max", "BF7_L2C_BD_ATI466_1d_max", "BF7_L2C_BD_ATI467_1d_max", "BF7_L2C_BD_ATI468_1d_max"
    };

    private String[] luFu = new String[]{
            "BF7_L2C_BD_ATI601_1d_max", "BF7_L2C_BD_ATI602_1d_max", "BF7_L2C_BD_ATI603_1d_max", "BF7_L2C_BD_ATI604_1d_max",
            "BF7_L2C_BD_ATI611_1d_max", "BF7_L2C_BD_ATI612_1d_max", "BF7_L2C_BD_ATI613_1d_max", "BF7_L2C_BD_ATI614_1d_max",
            "BF7_L2C_BD_ATI615_1d_max", "BF7_L2C_BD_ATI616_1d_max", "BF7_L2C_BD_ATI617_1d_max", "BF7_L2C_BD_ATI618_1d_max",
            "BF7_L2C_BD_ATI619_1d_max", "BF7_L2C_BD_ATI620_1d_max", "BF7_L2C_BD_ATI621_1d_max", "BF7_L2C_BD_ATI622_1d_max"
    };

    private String[] luYao = new String[]{
            "BF7_L2C_BD_ATI701_1d_max", "BF7_L2C_BD_ATI702_1d_max", "BF7_L2C_BD_ATI703_1d_max", "BF7_L2C_BD_ATI711_1d_max",
            "BF7_L2C_BD_ATI712_1d_max", "BF7_L2C_BD_ATI713_1d_max", "BF7_L2C_BD_ATI714_1d_max", "BF7_L2C_BD_ATI715_1d_max",
            "BF7_L2C_BD_ATI716_1d_max", "BF7_L2C_BD_ATI717_1d_max", "BF7_L2C_BD_ATI718_1d_max", "BF7_L2C_BD_ATI719_1d_max",
            "BF7_L2C_BD_ATI720_1d_max", "BF7_L2C_BD_ATI721_1d_max", "BF7_L2C_BD_ATI722_1d_max"
    };

    private String[] luShen = new String[]{
            "BF7_L2C_BD_ATI801_1d_max", "BF7_L2C_BD_ATI802_1d_max", "BF7_L2C_BD_ATI803_1d_max", "BF7_L2C_BD_ATI804_1d_max",
            "BF7_L2C_BD_ATI805_1d_max", "BF7_L2C_BD_ATI806_1d_max", "BF7_L2C_BD_ATI807_1d_max", "BF7_L2C_BD_ATI808_1d_max",
            "BF7_L2C_BD_ATI809_1d_max", "BF7_L2C_BD_ATI810_1d_max", "BF7_L2C_BD_ATI811_1d_max", "BF7_L2C_BD_ATI812_1d_max",
            "BF7_L2C_BD_ATI821_1d_max", "BF7_L2C_BD_ATI822_1d_max", "BF7_L2C_BD_ATI823_1d_max", "BF7_L2C_BD_ATI824_1d_max",
            "BF7_L2C_BD_ATI901_1d_max", "BF7_L2C_BD_ATI902_1d_max", "BF7_L2C_BD_ATI903_1d_max", "BF7_L2C_BD_ATI904_1d_max",
            "BF7_L2C_BD_ATI911_1d_max", "BF7_L2C_BD_ATI912_1d_max", "BF7_L2C_BD_ATI913_1d_max", "BF7_L2C_BD_ATI914_1d_max",
            "BF7_L2C_BD_ATI915_1d_max", "BF7_L2C_BD_ATI916_1d_max", "BF7_L2C_BD_ATI917_1d_max", "BF7_L2C_BD_ATI918_1d_max",
            "BF7_L2C_BD_ATI919_1d_max", "BF7_L2C_BD_ATI920_1d_max", "BF7_L2C_BD_ATI921_1d_max", "BF7_L2C_BD_ATI922_1d_max",
            "BF7_L2C_BD_ATIA01_1d_max", "BF7_L2C_BD_ATIA02_1d_max", "BF7_L2C_BD_ATIA03_1d_max", "BF7_L2C_BD_ATIA04_1d_max",
            "BF7_L2C_BD_ATIA05_1d_max", "BF7_L2C_BD_ATIA06_1d_max", "BF7_L2C_BD_ATIA07_1d_max", "BF7_L2C_BD_ATIA08_1d_max",
            "BF7_L2C_BD_ATIA09_1d_max", "BF7_L2C_BD_ATIA10_1d_max", "BF7_L2C_BD_ATIA11_1d_max", "BF7_L2C_BD_ATIA12_1d_max",
            "BF7_L2C_BD_ATIB01_1d_max", "BF7_L2C_BD_ATIB02_1d_max", "BF7_L2C_BD_ATIB03_1d_max", "BF7_L2C_BD_ATIB04_1d_max",
            "BF7_L2C_BD_ATIB05_1d_max", "BF7_L2C_BD_ATIB06_1d_max", "BF7_L2C_BD_ATIB07_1d_max", "BF7_L2C_BD_ATIB08_1d_max",
            "BF7_L2C_BD_ATIB09_1d_max", "BF7_L2C_BD_ATIB10_1d_max", "BF7_L2C_BD_ATIB11_1d_max", "BF7_L2C_BD_ATIB12_1d_max",
            "BF7_L2C_BD_ATIC01_1d_max", "BF7_L2C_BD_ATIC02_1d_max", "BF7_L2C_BD_ATIC03_1d_max", "BF7_L2C_BD_ATIC04_1d_max",
            "BF7_L2C_BD_ATIC05_1d_max", "BF7_L2C_BD_ATIC06_1d_max", "BF7_L2C_BD_ATIC07_1d_max", "BF7_L2C_BD_ATIC08_1d_max",
            "BF7_L2C_BD_ATIC09_1d_max", "BF7_L2C_BD_ATIC10_1d_max", "BF7_L2C_BD_ATIC11_1d_max", "BF7_L2C_BD_ATIC12_1d_max",
            "BF7_L2C_BD_ATIE01_1d_max", "BF7_L2C_BD_ATIE02_1d_max", "BF7_L2C_BD_ATIE03_1d_max", "BF7_L2C_BD_ATIE04_1d_max",
            "BF7_L2C_BD_ATIE05_1d_max", "BF7_L2C_BD_ATIE06_1d_max", "BF7_L2C_BD_ATIE07_1d_max", "BF7_L2C_BD_ATIE08_1d_max",
            "BF7_L2C_BD_ATIE09_1d_max", "BF7_L2C_BD_ATIE10_1d_max", "BF7_L2C_BD_ATIE11_1d_max", "BF7_L2C_BD_ATIE12_1d_max"
    };

    private String[] luGangA_C = new String[]{
            "BF7_L2C_BD_ATI101A_1d_max", "BF7_L2C_BD_ATI101B_1d_max", "BF7_L2C_BD_ATI101C_1d_max", "BF7_L2C_BD_ATI121A_1d_max",
            "BF7_L2C_BD_ATI121B_1d_max", "BF7_L2C_BD_ATI121C_1d_max", "BF7_L2C_BD_ATI121D_1d_max", "BF7_L2C_BD_ATI201A_1d_max",
            "BF7_L2C_BD_ATI201B_1d_max", "BF7_L2C_BD_ATI201C_1d_max", "BF7_L2C_BD_ATI201D_1d_max", "BF7_L2C_BD_ATI201E_1d_max",
            "BF7_L2C_BD_ATI221A_1d_max", "BF7_L2C_BD_ATI221B_1d_max", "BF7_L2C_BD_ATI221C_1d_max", "BF7_L2C_BD_ATI301A_1d_max",
            "BF7_L2C_BD_ATI301B_1d_max", "BF7_L2C_BD_ATI301C_1d_max", "BF7_L2C_BD_ATI321A_1d_max", "BF7_L2C_BD_ATI321B_1d_max",
            "BF7_L2C_BD_ATI321C_1d_max", "BF7_L2C_BD_ATI341A_1d_max", "BF7_L2C_BD_ATI341B_1d_max", "BF7_L2C_BD_ATI341C_1d_max",
            "BF7_L2C_BD_ATI401A_1d_max", "BF7_L2C_BD_ATI401B_1d_max", "BF7_L2C_BD_ATI102A_1d_max", "BF7_L2C_BD_ATI102B_1d_max",
            "BF7_L2C_BD_ATI102C_1d_max", "BF7_L2C_BD_ATI122A_1d_max", "BF7_L2C_BD_ATI122B_1d_max", "BF7_L2C_BD_ATI122C_1d_max",
            "BF7_L2C_BD_ATI122D_1d_max", "BF7_L2C_BD_ATI202A_1d_max", "BF7_L2C_BD_ATI202B_1d_max", "BF7_L2C_BD_ATI202C_1d_max",
            "BF7_L2C_BD_ATI202D_1d_max", "BF7_L2C_BD_ATI202E_1d_max", "BF7_L2C_BD_ATI222A_1d_max", "BF7_L2C_BD_ATI222B_1d_max",
            "BF7_L2C_BD_ATI222C_1d_max", "BF7_L2C_BD_ATI302A_1d_max", "BF7_L2C_BD_ATI302B_1d_max", "BF7_L2C_BD_ATI302C_1d_max",
            "BF7_L2C_BD_ATI322A_1d_max", "BF7_L2C_BD_ATI322B_1d_max", "BF7_L2C_BD_ATI322C_1d_max", "BF7_L2C_BD_ATI342A_1d_max",
            "BF7_L2C_BD_ATI342B_1d_max", "BF7_L2C_BD_ATI342C_1d_max", "BF7_L2C_BD_ATI402A_1d_max", "BF7_L2C_BD_ATI402B_1d_max",
            "BF7_L2C_BD_ATI103A_1d_max", "BF7_L2C_BD_ATI103B_1d_max", "BF7_L2C_BD_ATI103C_1d_max", "BF7_L2C_BD_ATI123A_1d_max",
            "BF7_L2C_BD_ATI123B_1d_max", "BF7_L2C_BD_ATI123C_1d_max", "BF7_L2C_BD_ATI123D_1d_max", "BF7_L2C_BD_ATI203A_1d_max",
            "BF7_L2C_BD_ATI203B_1d_max", "BF7_L2C_BD_ATI203C_1d_max", "BF7_L2C_BD_ATI203D_1d_max", "BF7_L2C_BD_ATI203E_1d_max",
            "BF7_L2C_BD_ATI223A_1d_max", "BF7_L2C_BD_ATI223B_1d_max", "BF7_L2C_BD_ATI223C_1d_max", "BF7_L2C_BD_ATI303A_1d_max",
            "BF7_L2C_BD_ATI303B_1d_max", "BF7_L2C_BD_ATI303C_1d_max", "BF7_L2C_BD_ATI323A_1d_max", "BF7_L2C_BD_ATI323B_1d_max",
            "BF7_L2C_BD_ATI323C_1d_max", "BF7_L2C_BD_ATI343A_1d_max", "BF7_L2C_BD_ATI343B_1d_max", "BF7_L2C_BD_ATI343C_1d_max",
            "BF7_L2C_BD_ATI403A_1d_max", "BF7_L2C_BD_ATI403B_1d_max"
    };

    private String[] luGangD_F = new String[]{
            "BF7_L2C_BD_ATI104A_1d_max", "BF7_L2C_BD_ATI104B_1d_max", "BF7_L2C_BD_ATI104C_1d_max", "BF7_L2C_BD_ATI104D_1d_max",
            "BF7_L2C_BD_ATI124A_1d_max", "BF7_L2C_BD_ATI124B_1d_max", "BF7_L2C_BD_ATI124C_1d_max", "BF7_L2C_BD_ATI124D_1d_max",
            "BF7_L2C_BD_ATI124E_1d_max", "BF7_L2C_BD_ATI204A_1d_max", "BF7_L2C_BD_ATI204B_1d_max", "BF7_L2C_BD_ATI204C_1d_max",
            "BF7_L2C_BD_ATI204D_1d_max", "BF7_L2C_BD_ATI204E_1d_max", "BF7_L2C_BD_ATI204F_1d_max", "BF7_L2C_BD_ATI224A_1d_max",
            "BF7_L2C_BD_ATI224B_1d_max", "BF7_L2C_BD_ATI224C_1d_max", "BF7_L2C_BD_ATI304A_1d_max", "BF7_L2C_BD_ATI304B_1d_max",
            "BF7_L2C_BD_ATI304C_1d_max", "BF7_L2C_BD_ATI324A_1d_max", "BF7_L2C_BD_ATI324B_1d_max", "BF7_L2C_BD_ATI324C_1d_max",
            "BF7_L2C_BD_ATI344A_1d_max", "BF7_L2C_BD_ATI344B_1d_max", "BF7_L2C_BD_ATI344C_1d_max", "BF7_L2C_BD_ATI404A_1d_max",
            "BF7_L2C_BD_ATI404B_1d_max", "BF7_L2C_BD_ATI105A_1d_max", "BF7_L2C_BD_ATI105B_1d_max", "BF7_L2C_BD_ATI105C_1d_max",
            "BF7_L2C_BD_ATI125A_1d_max", "BF7_L2C_BD_ATI125B_1d_max", "BF7_L2C_BD_ATI125C_1d_max", "BF7_L2C_BD_ATI125D_1d_max",
            "BF7_L2C_BD_ATI205A_1d_max", "BF7_L2C_BD_ATI205B_1d_max", "BF7_L2C_BD_ATI205C_1d_max", "BF7_L2C_BD_ATI205D_1d_max",
            "BF7_L2C_BD_ATI205E_1d_max", "BF7_L2C_BD_ATI225A_1d_max", "BF7_L2C_BD_ATI225B_1d_max", "BF7_L2C_BD_ATI225C_1d_max",
            "BF7_L2C_BD_ATI305A_1d_max", "BF7_L2C_BD_ATI305B_1d_max", "BF7_L2C_BD_ATI305C_1d_max", "BF7_L2C_BD_ATI325A_1d_max",
            "BF7_L2C_BD_ATI325B_1d_max", "BF7_L2C_BD_ATI325C_1d_max", "BF7_L2C_BD_ATI345A_1d_max", "BF7_L2C_BD_ATI345B_1d_max",
            "BF7_L2C_BD_ATI345C_1d_max", "BF7_L2C_BD_ATI405A_1d_max", "BF7_L2C_BD_ATI405B_1d_max", "BF7_L2C_BD_ATI106A_1d_max",
            "BF7_L2C_BD_ATI106B_1d_max", "BF7_L2C_BD_ATI106C_1d_max", "BF7_L2C_BD_ATI126A_1d_max", "BF7_L2C_BD_ATI126B_1d_max",
            "BF7_L2C_BD_ATI126C_1d_max", "BF7_L2C_BD_ATI126D_1d_max", "BF7_L2C_BD_ATI206A_1d_max", "BF7_L2C_BD_ATI206B_1d_max",
            "BF7_L2C_BD_ATI206C_1d_max", "BF7_L2C_BD_ATI206D_1d_max", "BF7_L2C_BD_ATI206E_1d_max", "BF7_L2C_BD_ATI226A_1d_max",
            "BF7_L2C_BD_ATI226B_1d_max", "BF7_L2C_BD_ATI226C_1d_max", "BF7_L2C_BD_ATI306A_1d_max", "BF7_L2C_BD_ATI306B_1d_max",
            "BF7_L2C_BD_ATI306C_1d_max", "BF7_L2C_BD_ATI326A_1d_max", "BF7_L2C_BD_ATI326B_1d_max", "BF7_L2C_BD_ATI326C_1d_max",
            "BF7_L2C_BD_ATI346A_1d_max", "BF7_L2C_BD_ATI346B_1d_max", "BF7_L2C_BD_ATI346C_1d_max", "BF7_L2C_BD_ATI406A_1d_max",
            "BF7_L2C_BD_ATI406B_1d_max"
    };

    private String[] luGangG_I = new String[]{
            "BF7_L2C_BD_ATI107A_1d_max", "BF7_L2C_BD_ATI107B_1d_max", "BF7_L2C_BD_ATI107C_1d_max", "BF7_L2C_BD_ATI127A_1d_max",
            "BF7_L2C_BD_ATI127B_1d_max", "BF7_L2C_BD_ATI127C_1d_max", "BF7_L2C_BD_ATI127D_1d_max", "BF7_L2C_BD_ATI207A_1d_max",
            "BF7_L2C_BD_ATI207B_1d_max", "BF7_L2C_BD_ATI207C_1d_max", "BF7_L2C_BD_ATI207D_1d_max", "BF7_L2C_BD_ATI207E_1d_max",
            "BF7_L2C_BD_ATI227A_1d_max", "BF7_L2C_BD_ATI227B_1d_max", "BF7_L2C_BD_ATI227C_1d_max", "BF7_L2C_BD_ATI307A_1d_max",
            "BF7_L2C_BD_ATI307B_1d_max", "BF7_L2C_BD_ATI307C_1d_max", "BF7_L2C_BD_ATI327A_1d_max", "BF7_L2C_BD_ATI327B_1d_max",
            "BF7_L2C_BD_ATI327C_1d_max", "BF7_L2C_BD_ATI347A_1d_max", "BF7_L2C_BD_ATI347B_1d_max", "BF7_L2C_BD_ATI347C_1d_max",
            "BF7_L2C_BD_ATI407A_1d_max", "BF7_L2C_BD_ATI407B_1d_max", "BF7_L2C_BD_ATI108A_1d_max", "BF7_L2C_BD_ATI108B_1d_max",
            "BF7_L2C_BD_ATI108C_1d_max", "BF7_L2C_BD_ATI128A_1d_max", "BF7_L2C_BD_ATI128B_1d_max", "BF7_L2C_BD_ATI128C_1d_max",
            "BF7_L2C_BD_ATI128D_1d_max", "BF7_L2C_BD_ATI208A_1d_max", "BF7_L2C_BD_ATI208B_1d_max", "BF7_L2C_BD_ATI208C_1d_max",
            "BF7_L2C_BD_ATI208D_1d_max", "BF7_L2C_BD_ATI208E_1d_max", "BF7_L2C_BD_ATI228A_1d_max", "BF7_L2C_BD_ATI228B_1d_max",
            "BF7_L2C_BD_ATI228C_1d_max", "BF7_L2C_BD_ATI308A_1d_max", "BF7_L2C_BD_ATI308B_1d_max", "BF7_L2C_BD_ATI308C_1d_max",
            "BF7_L2C_BD_ATI328A_1d_max", "BF7_L2C_BD_ATI328B_1d_max", "BF7_L2C_BD_ATI328C_1d_max", "BF7_L2C_BD_ATI348A_1d_max",
            "BF7_L2C_BD_ATI348B_1d_max", "BF7_L2C_BD_ATI348C_1d_max", "BF7_L2C_BD_ATI408A_1d_max", "BF7_L2C_BD_ATI408B_1d_max",
            "BF7_L2C_BD_ATI109A_1d_max", "BF7_L2C_BD_ATI109B_1d_max", "BF7_L2C_BD_ATI109C_1d_max", "BF7_L2C_BD_ATI129A_1d_max",
            "BF7_L2C_BD_ATI129B_1d_max", "BF7_L2C_BD_ATI129C_1d_max", "BF7_L2C_BD_ATI129D_1d_max", "BF7_L2C_BD_ATI209A_1d_max",
            "BF7_L2C_BD_ATI209B_1d_max", "BF7_L2C_BD_ATI209C_1d_max", "BF7_L2C_BD_ATI209D_1d_max", "BF7_L2C_BD_ATI209E_1d_max",
            "BF7_L2C_BD_ATI229A_1d_max", "BF7_L2C_BD_ATI229B_1d_max", "BF7_L2C_BD_ATI229C_1d_max", "BF7_L2C_BD_ATI309A_1d_max",
            "BF7_L2C_BD_ATI309B_1d_max", "BF7_L2C_BD_ATI309C_1d_max", "BF7_L2C_BD_ATI329A_1d_max", "BF7_L2C_BD_ATI329B_1d_max",
            "BF7_L2C_BD_ATI329C_1d_max", "BF7_L2C_BD_ATI349A_1d_max", "BF7_L2C_BD_ATI349B_1d_max", "BF7_L2C_BD_ATI349C_1d_max",
            "BF7_L2C_BD_ATI409A_1d_max", "BF7_L2C_BD_ATI409B_1d_max"
    };

    private String[] luGangJ_L = new String[]{
            "BF7_L2C_BD_ATI110A_1d_max", "BF7_L2C_BD_ATI110B_1d_max", "BF7_L2C_BD_ATI110C_1d_max", "BF7_L2C_BD_ATI130A_1d_max",
            "BF7_L2C_BD_ATI130B_1d_max", "BF7_L2C_BD_ATI130C_1d_max", "BF7_L2C_BD_ATI130D_1d_max", "BF7_L2C_BD_ATI210A_1d_max",
            "BF7_L2C_BD_ATI210B_1d_max", "BF7_L2C_BD_ATI210C_1d_max", "BF7_L2C_BD_ATI210D_1d_max", "BF7_L2C_BD_ATI210E_1d_max",
            "BF7_L2C_BD_ATI230A_1d_max", "BF7_L2C_BD_ATI230B_1d_max", "BF7_L2C_BD_ATI230C_1d_max", "BF7_L2C_BD_ATI310A_1d_max",
            "BF7_L2C_BD_ATI310B_1d_max", "BF7_L2C_BD_ATI310C_1d_max", "BF7_L2C_BD_ATI330A_1d_max", "BF7_L2C_BD_ATI330B_1d_max",
            "BF7_L2C_BD_ATI330C_1d_max", "BF7_L2C_BD_ATI350A_1d_max", "BF7_L2C_BD_ATI350B_1d_max", "BF7_L2C_BD_ATI350C_1d_max",
            "BF7_L2C_BD_ATI410A_1d_max", "BF7_L2C_BD_ATI410B_1d_max", "BF7_L2C_BD_ATI111A_1d_max", "BF7_L2C_BD_ATI111B_1d_max",
            "BF7_L2C_BD_ATI111C_1d_max", "BF7_L2C_BD_ATI131A_1d_max", "BF7_L2C_BD_ATI131B_1d_max", "BF7_L2C_BD_ATI131C_1d_max",
            "BF7_L2C_BD_ATI131D_1d_max", "BF7_L2C_BD_ATI211A_1d_max", "BF7_L2C_BD_ATI211B_1d_max", "BF7_L2C_BD_ATI211C_1d_max",
            "BF7_L2C_BD_ATI211D_1d_max", "BF7_L2C_BD_ATI211E_1d_max", "BF7_L2C_BD_ATI231A_1d_max", "BF7_L2C_BD_ATI231B_1d_max",
            "BF7_L2C_BD_ATI231C_1d_max", "BF7_L2C_BD_ATI311A_1d_max", "BF7_L2C_BD_ATI311B_1d_max", "BF7_L2C_BD_ATI311C_1d_max",
            "BF7_L2C_BD_ATI331A_1d_max", "BF7_L2C_BD_ATI331B_1d_max", "BF7_L2C_BD_ATI331C_1d_max", "BF7_L2C_BD_ATI351A_1d_max",
            "BF7_L2C_BD_ATI351B_1d_max", "BF7_L2C_BD_ATI351C_1d_max", "BF7_L2C_BD_ATI411A_1d_max", "BF7_L2C_BD_ATI411B_1d_max",
            "BF7_L2C_BD_ATI112A_1d_max", "BF7_L2C_BD_ATI112B_1d_max", "BF7_L2C_BD_ATI112C_1d_max", "BF7_L2C_BD_ATI132A_1d_max",
            "BF7_L2C_BD_ATI132B_1d_max", "BF7_L2C_BD_ATI132C_1d_max", "BF7_L2C_BD_ATI132D_1d_max", "BF7_L2C_BD_ATI212A_1d_max",
            "BF7_L2C_BD_ATI212B_1d_max", "BF7_L2C_BD_ATI212C_1d_max", "BF7_L2C_BD_ATI212D_1d_max", "BF7_L2C_BD_ATI212E_1d_max",
            "BF7_L2C_BD_ATI232A_1d_max", "BF7_L2C_BD_ATI232B_1d_max", "BF7_L2C_BD_ATI232C_1d_max", "BF7_L2C_BD_ATI312A_1d_max",
            "BF7_L2C_BD_ATI312B_1d_max", "BF7_L2C_BD_ATI312C_1d_max", "BF7_L2C_BD_ATI332A_1d_max", "BF7_L2C_BD_ATI332B_1d_max",
            "BF7_L2C_BD_ATI332C_1d_max", "BF7_L2C_BD_ATI352A_1d_max", "BF7_L2C_BD_ATI352B_1d_max", "BF7_L2C_BD_ATI352C_1d_max",
            "BF7_L2C_BD_ATI412A_1d_max", "BF7_L2C_BD_ATI412B_1d_max"
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
        return JobEnum.gl_rishengchanfenxibaogao_day7;
    }

    @Scheduled(cron = "0 10 10 * * ?")
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
        dealChart3(data);
        handleTapTempture(version);
        handleLuTiWenDu(version);
        List<ReportCategoryTemplate> reportCategoryTemplates = reportCategoryTemplateService.selectTemplateInfo(JobEnum.gl_rishengchanfenxibaogao_day7.getCode(), LanguageEnum.cn_zh.getName(), "7高炉");
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

    private String getAnalysisValuesByCodeUrl(String version, DateQuery dateQuery, String name, String brandCode) {
        return String.format(getUrl(version) + "/analysisValues/rangeByCode?from=%s&to=%s&%s=%s", dateQuery.getQueryStartTime(),
                dateQuery.getQueryEndTime(), name, brandCode);
    }

    private String getAnalysisValueUrl(String version, long time, String type, String brandCode) {
        return String.format(getUrl(version) + "/analysisValue/clock/%s?type=%s&brandcode=%s", time, type, brandCode);
    }

    private List<AnalysisValue> getAnalysisValueList(String version, DateQuery dateQuery, String type, String brandCodeType) {
        List<AnalysisValue> allAnalysisValues = new ArrayList<>();
        List<String> list = getBrandCodeData(version, dateQuery, brandCodeType);
        if(Objects.nonNull(list) && CollectionUtils.isNotEmpty(list)) {
            for (String brandCode:list) {
                allAnalysisValues.addAll(getAnalysisValuesByUrl(getAnalysisValueUrl(version, dateQuery.getQueryEndTime(), type, brandCode)));
            }
        }
        return allAnalysisValues;
    }

    private List<AnalysisValue> getAnalysisValuesByUrl(String url) {
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

    private List<AnalysisValue> getAnalysisValuesByBrandCode(String version, DateQuery dateQuery, String name, String brandCode) {
        return getAnalysisValuesByUrl(getAnalysisValuesByCodeUrl(version, dateQuery, name, brandCode));
    }

    private void handleAnalysisValues(List<AnalysisValue> analysisValues, String[] array, String prefix, List<String> list) {
        if(Objects.isNull(analysisValues) || CollectionUtils.isEmpty(analysisValues)) {
            for (String item:array) {
                result.put(prefix+"_"+item, " ");
            }
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

    private List<AnalysisValue> getAnalysisValueList(String version, DateQuery dateQuery, String brandCodeType) {
        List<AnalysisValue> allAnalysisValues = new ArrayList<>();
        List<String> list = getBrandCodeData(version, dateQuery, brandCodeType);
        if(Objects.nonNull(list) && CollectionUtils.isNotEmpty(list)) {
            for (String brandCode:list) {
                allAnalysisValues.addAll(getAnalysisValuesByBrandCode(version, dateQuery, "brandCode", brandCode));
            }
        }
        return allAnalysisValues;
    }

    private void dealPartTwo(String version) {
        DateQuery dateQuery = DateQueryUtil.buildDayAheadTwoHour(DateUtil.addDays(new Date(), -1));
        //焦炭
        handleAnalysisValues(getAnalysisValueList(version, dateQuery, "COKE"), new String[]{"H2O", "Ad", "Vdaf", "M40", "M10", "CSR", "CRI"}, "COKE", Arrays.asList("M40", "M10", "CSR", "CRI"));
        //煤粉
        handleAnalysisValues(getAnalysisValuesByBrandCode(version, dateQuery, "brandCode", "FBFM-A_COAL"), new String[]{"H2O", "Vdaf", "Fcad"}, "COAL", Arrays.asList());
        //烧结
        handleAnalysisValues(getAnalysisValueList(version, dateQuery, "SINTER"), new String[]{"TFe", "FeO", "CaO", "SiO2", "MgO", "Al2O3", "B2"}, "SINTER", Arrays.asList("B2"));
        //球团
        handleAnalysisValues(getAnalysisValueList(version, dateQuery, "PELLETS"), new String[]{"TFe", "CaO", "SiO2"}, "PELLETS", Arrays.asList());
        //块矿
        handleAnalysisValues(getAnalysisValueList(version, dateQuery, "LC", "LUMPORE"), new String[]{"TFe", "SiO2", "Al2O3"}, "LUMPORE", Arrays.asList());
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
        String chargeNoData = httpUtil.postJsonParams(url + DateUtil.getDateEndTime(date).getTime(), jsonObject.toJSONString());
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
        BigDecimal maxTemp = new BigDecimal(0);
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
        BigDecimal maxTemp = getMaxLuTiTemp(builder, version, tagNames, -1);
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
            String prefix = "BF7_L2C_BD_";
            String suffix = "_1d_max";
            //炉缸
            Map<String, BigDecimal> luGangMap =  getLuTiWenDuData(version, luGang);
            BigDecimal luGangYesterdayMax =  getMaxLuTiTemp(new StringBuilder(), version, luGangMap.keySet().toArray(new String[luGangMap.keySet().size()]), -2);
            dealLuTiWenDuData(luGangMap, luGangYesterdayMax, 1, text1, text2, text3, text4, prefix, suffix);
            //炉腹
            Map<String, BigDecimal> luFuMap =  getLuTiWenDuData(version, luFu);
            BigDecimal luFuYesterdayMax =  getMaxLuTiTemp(new StringBuilder(), version, luFuMap.keySet().toArray(new String[luFuMap.keySet().size()]), -2);
            dealLuTiWenDuData(luFuMap, luFuYesterdayMax, 2, text1, text2, text3, text4, prefix, suffix);
            //炉腰 5
            Map<String, BigDecimal> luYaoMap =  getLuTiWenDuData(version, luYao);
            BigDecimal luYaoYesterdayMax =  getMaxLuTiTemp(new StringBuilder(), version, luYaoMap.keySet().toArray(new String[luYaoMap.keySet().size()]), -2);
            dealLuTiWenDuData(luYaoMap, luYaoYesterdayMax, 3, text1, text2, text3, text4, prefix, suffix);
            //炉身
            Map<String, BigDecimal> lushenMap =  getLuTiWenDuData(version, luShen);
            BigDecimal lushenYesterdayMax =  getMaxLuTiTemp(new StringBuilder(), version, lushenMap.keySet().toArray(new String[lushenMap.keySet().size()]), -2);
            dealLuTiWenDuData(lushenMap, lushenYesterdayMax, 4, text1, text2, text3, text4, prefix, suffix);

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
        BigDecimal maxTemp = getMaxTemp(builder, -1, version, tagNames);
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

            String prefix = "BF7_L2C_BD_";
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
            BigDecimal maxACTemp = getMaxTemp(new StringBuilder(), -2, version, todayACMap.keySet().toArray(new String[todayACMap.keySet().size()]));
            dealLuTiWenDuData(todayACMap, maxACTemp, 1, text1, text2, text3, text4, prefix, stuffix);
            Map<String, BigDecimal> todayDFMap = getMaxTempMap(version, luGangD_F);
            BigDecimal maxDFTemp = getMaxTemp(new StringBuilder(), -2, version, todayDFMap.keySet().toArray(new String[todayDFMap.keySet().size()]));
            dealLuTiWenDuData(todayDFMap, maxDFTemp, 2, text1, text2, text3, text4, prefix, stuffix);
            Map<String, BigDecimal> todayGIMap = getMaxTempMap(version, luGangG_I);
            BigDecimal maxGITemp = getMaxTemp(new StringBuilder(), -2, version, todayGIMap.keySet().toArray(new String[todayGIMap.keySet().size()]));
            dealLuTiWenDuData(todayGIMap, maxGITemp, 3, text1, text2, text3, text4, prefix, stuffix);
            Map<String, BigDecimal> todayJMMap = getMaxTempMap(version, luGangJ_L);
            BigDecimal maxJMTemp = getMaxTemp(new StringBuilder(), -2, version, todayJMMap.keySet().toArray(new String[todayJMMap.keySet().size()]));
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
                            lm.put("gap", realVal.subtract(low).toString());
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
                            lm.put("gap", realVal.subtract(low).toString());
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
                        result.put(prefix + (i + 1), doubleValue == null ? df.format(0d):df.format(doubleValue));
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
    private Double getDataBeforeDays(JSONObject data, String tagName, DecimalFormat df) {
        Double result = 0d;
        if (data != null && data.size() > 0) {
            Double doubleValue = 0d;
            List<Double> valueObject = getValuesByTag(data, tagName);
            if (Objects.nonNull(valueObject) && valueObject.size() > 0) {
                doubleValue = valueObject.get(valueObject.size() - 1 - 1);
                result = doubleValue == null ? 0d : doubleValue;
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
        queryParam.put("endTime",  String.valueOf(date.getStartTime().getTime()));
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

    private String getCommitInfo(String version, long date, int id) {
        String commit = "";
        String url = getUrl(version) + "/comment/info";
        Map<String, String> queryParam = new HashMap();
        queryParam.put("date", Objects.requireNonNull(date).toString());
        queryParam.put("model", "REPORT");
        queryParam.put("id", String.valueOf(id));
        String result = httpUtil.get(url, queryParam);
        if(StringUtils.isNotBlank(result)) {
            CommentDataDTO commentDataDTO = JSON.parseObject(result, CommentDataDTO.class);
            if(Objects.nonNull(commentDataDTO)) {
                CommentData data = commentDataDTO.getData();
                if(Objects.nonNull(data)) {
                    commit = data.getRemark();
                }
            }
        }
        return commit;
    }

    private void dealPart1(JSONObject data, String version) {
        try {
            dealPart(data, "partOne", L1, df2);
            Date date = DateUtil.addDays(new Date(), -1);
            DateQuery dateQueryNoDelay = DateQueryUtil.buildTodayNoDelay(date);
            //合格率
            String[] arr = new String[]{"lw", "lz", "ts", "gl"};
            for (String dataType:arr) {
                dealQualifiedRate(version, dateQueryNoDelay, dataType);
            }
            //发电量
            String commit = getCommitInfo(version, dateQueryNoDelay.getQueryStartTime(), 6);
            result.put("commit_day", " ");
            result.put("commit_tap", " ");
            if (StringUtils.isNotBlank(commit)) {
                Double commitDay = Double.parseDouble(commit);
                if (Objects.nonNull(commitDay)) {
                    result.put("commit_day", df2.format(commitDay));
                    //吨铁发电量=发电量*10000/日产量
                    List<Double> valueObject = getValuesByTag(data, "BF7_L2M_BX_HMMass_1d_cur");
                    if (Objects.nonNull(valueObject) && valueObject.size() > 0) {
                        Double total = valueObject.get(valueObject.size() - 1);
                        if (Objects.nonNull(total) && total != 0d) {
                            result.put("commit_tap", df2.format((commitDay*10000)/total));
                        }
                    }
                }

            }
            dealChart1(data);
            dealChart2(data);

            double increase = dealIncreaseYesterday(data, "BF7_L2M_BX_HMMass_1d_cur");
            if (increase >= 0d) {
                result.put("textOne1", "升高");
            } else {
                result.put("textOne1", "降低");
            }
            result.put("countOne1", df2.format(Math.abs(increase)));

            increase = dealIncreaseYesterday(data, "BF7_L2M_BX_CokeRate_1d_cur");
            if (increase >= 0d) {
                result.put("textOne2", "升高");
            } else {
                result.put("textOne2", "降低");
            }
            result.put("countOne2", df2.format(Math.abs(increase)));

            increase = dealIncreaseYesterday(data, "BF7_L2M_BX_CoalRate_1d_cur");
            if (increase >= 0d) {
                result.put("textOne3", "升高");
            } else {
                result.put("textOne3", "降低");
            }
            result.put("countOne3", df2.format(Math.abs(increase)));

            result.put("countOne4", df2.format(dealMonthTotal(data, "BF7_L2M_BX_HMMass_1d_cur")));
            result.put("countOne5", df2.format(dealMonthTotalAvg(data, "BF7_L2M_BX_CokeRate_1d_cur")));
            result.put("countOne6", df2.format(dealMonthTotalAvg(data, "BF7_L2M_BX_CoalRate_1d_cur")));
            result.put("countOne7", df2.format(dealMonthTotalAvg(data, "BF7_L2M_BX_FuelRate_1d_cur")));
        } catch (Exception e) {
            log.error("高炉日生产分析报告处理part1失败", e);
        }
    }

    private void dealPart3(JSONObject data){
        try {
            dealPart(data, "partThree", L3, df2);
            dealSingle(data, "partThree3", "BF7_L2C_BD_ColdBlastPress_1d_avg", df3);
            dealSingle(data, "partThree4", "BF7_L2M_PressDiff_1d_avg", df3);
            dealSingle(data, "partThree5", "BF7_L2C_BD_TopPress_1d_avg", df3);
            result.put("offsetWet", df2.format(dealOffset(5d,10d,"BF7_L2C_BD_BH_1d_avg",data)));
            result.put("offsetBV", df2.format(dealOffset(5700d,5900d,"BF7_L2C_BD_ColdBlastFlow_1d_avg",data)));
            result.put("offsetBP", df3.format(dealOffset(0d,0.42d,"BF7_L2C_BD_ColdBlastPress_1d_avg",data)));
            result.put("offsetP", df3.format(dealOffset(0d,0.183d,"BF7_L2M_PressDiff_1d_avg",data)));
            result.put("offsetTP", df3.format(dealOffset(0.228d,0.236d,"BF7_L2C_BD_TopPress_1d_avg",data)));
            result.put("offsetPT", df2.format(dealOffset(1505d,1530d,"BF7_L2M_HMTemp_1d_avg",data)));
            result.put("offsetFR", df2.format(dealOffset(515d,525d,"BF7_L2M_BX_FuelRate_1d_cur",data)));
            result.put("offsetCO", df2.format(dealOffset(47d,52d,"BF7_L2M_GasUtilization_1d_avg",data)));
        } catch (Exception e) {
            log.error("高炉日生产分析报告处理part3失败", e);
        }
    }

    private void dealPart4(JSONObject data) {
        try {
            dealPart(data, "partFour", L4, df2);
            Double yesterdayData = getDataBeforeDays(data, L4[0], df2);
            result.put("yesterday_temp", df2.format(yesterdayData));
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

    private Double dealMonthTotalAvg (JSONObject data, String tagName) {
        String tagRiChanLiang = "BF7_L2M_BX_HMMass_1d_cur";
        Double result;
        Double total = 0d;
        Double count = 0d;
        List<Date> list = DateUtil.getAllDayBeginTimeInCurrentMonthBeforeDays(new Date(), 0);
        //3日燃料比累计=（1日燃料比*1日产量+2日燃料比*2日产量+3日燃料比*3日产量）/（1日产量+2日产量+3日产量）
        List<Double> tag1Object = getValuesByTag(data, tagName);
        //日产量
        List<Double> tag2Object = getValuesByTag(data, tagRiChanLiang);
        if(Objects.nonNull(tag1Object) && Objects.nonNull(list)&& tag1Object.size() > list.size() ) {
            for(int i = tag1Object.size() - list.size() + 1; i < tag1Object.size(); i ++) {
                Double item1 = tag1Object.get(i);
                Double item2 = tag2Object.get(i);
                if (item1 != null && item2 != null) {
                    total += item1*item2;
                    count += item2;
                }
            }
        }
        if (count > 0d) {
            result = total / count;
        }
        else {
            result = 0d;
        }
        return result;
    }

    private Double dealMonthTotal (JSONObject data, String tagName) {
        Double total = 0d;
        Double count = 0d;
        List<Date> list = DateUtil.getAllDayBeginTimeInCurrentMonthBeforeDays(new Date(), 0);
        List<Double> tagObject = getValuesByTag(data, tagName);
        if(Objects.nonNull(tagObject) && Objects.nonNull(list)&& tagObject.size() > list.size() ) {
            for(int i = tagObject.size() - list.size() + 1; i < tagObject.size(); i ++) {
                Double item = tagObject.get(i);
                if (item != null) {
                    total += item;
                }
            }
        }
        return total;
    }

    private void dealChart1(JSONObject data) {
        List<Double> tagObject1 = getValuesByTag(data, "BF7_L2M_BX_HMMass_1d_cur");
        List<Double> tagObject2 = getValuesByTag(data, "BF7_L2M_BX_FuelRate_1d_cur");
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
        List<Double> tagObject1 = getValuesByTag(data, "BF7_L2M_BX_CokeRate_1d_cur");
        List<Double> tagObject2 = getValuesByTag(data, "BF7_L2M_BX_CoalRate_1d_cur");
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

    private void dealChart3(JSONObject data) {
        String tagName = "BF7_L2C_BD_SoftTempDiff_1d_avg";
        List<Double> tagObject1 = getValuesByTag(data, tagName);
        tagObject1.removeAll(Collections.singleton(null));
        double max1 = tagObject1.size() > 0 ? Collections.max(tagObject1) * 1.2 : 6.0;
        double min1 = tagObject1.size() > 0 ? Collections.min(tagObject1) * 0.8 : 0.0;
        if (max1 == min1) {
            return;
        }
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
        List<Double> luGangJ_MList = getMaxLuGangList(version, luGangJ_L);

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
        if (max == min) {
            return;
        }
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
        series4.add(new Serie("J-L", luGangJ_MList.toArray()));

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
        String prefix = "BF7_L2C_BD_";
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
        if (max == min) {
            return;
        }
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
        for (Long time : longTimeList) {
            Double val = 0d;
            if (innerMap != null) {
                //所有L2M_BX的点都是 写的0 其实9点出来  其余点都是22点出来
                if(!tagName.contains("_L2M_BX_")) {
                    //减去22小时，取L2M_BX数据
                    time = time - 7200000;
                }
                BigDecimal big = (BigDecimal) innerMap.get(String.valueOf(time));
                if (null != big) {
                    val = big.doubleValue();
                    if (val < 0) {
                        val = 0d;
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
        cal.add(Calendar.MONTH, -1);
        Date startDate = cal.getTime();

        startTime = startDate;
        endTime = endDate;
        while (!DateUtil.isSameDay(startDate, endDate)) {
            // 拼接x坐标轴
            categoriesList.add(DateUtil.getFormatDateTime(startDate, DateUtil.MMddChineseFormat));
            // 递增日期
            cal.add(Calendar.DAY_OF_MONTH, 1);
            startDate = cal.getTime();
            longTimeList.add(startDate.getTime());
        }
    }

    /**
     * 8号高炉
     *
     * @param version
     * @return
     */
    private String getUrl(String version) {
        return httpProperties.getUrlApiGLThree();
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
            String sequence = "7高炉";
            XWPFDocument doc = WordExportUtil.exportWord07(path, result);

            List<XWPFTable> tables = doc.getTables();

            mergeCellsVertically(tables.get(tables.size()-1), 4, 1, tables.get(tables.size()-1).getRows().size()-1);
            XWPFTableCell targetCell = tables.get(tables.size()-1).getRow(1).getCell(4);
            // 处理换行符
            addBreakInCell(targetCell);
            Date date = DateUtil.addDays(new Date(), -1);
            String fileName = String.format("%s_%s.docx", currentTemplate.getTemplateName(), DateUtil.getFormatDateTime(date, "yyyyMMdd"));
            String filePath = jobProperties.getFilePath() + File.separator + "doc" + File.separator + fileName;
            FileOutputStream fos = new FileOutputStream(filePath);
            doc.write(fos);
            fos.close();

            ReportIndex reportIndex = new ReportIndex();
            reportIndex.setSequence(sequence)
                    .setReportCategoryCode(JobEnum.gl_rishengchanfenxibaogao_day7.getCode())
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
