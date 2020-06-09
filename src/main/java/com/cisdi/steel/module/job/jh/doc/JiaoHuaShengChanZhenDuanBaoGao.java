package com.cisdi.steel.module.job.jh.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.WordImageEntity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.math.NumberArithmeticUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.a1.doc.ChartFactory;
import com.cisdi.steel.module.job.a1.doc.Serie;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.enums.LanguageEnum;
import com.cisdi.steel.module.report.enums.ReportTemplateTypeEnum;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import com.cisdi.steel.module.report.service.ReportIndexService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

@Component
@Slf4j
public class JiaoHuaShengChanZhenDuanBaoGao {
    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private HttpProperties httpProperties;

    @Autowired
    private JobProperties jobProperties;

    @Autowired
    private ReportIndexMapper reportIndexMapper;

    @Autowired
    private ReportCategoryTemplateService reportCategoryTemplateService;

    @Autowired
    protected ReportIndexService reportIndexService;

    private HashMap<String, Object> result = null;
    private Date startTime = null;
    private Date endTime = null;
    private Date beforeYesterday = null;
    List<String> categoriesList = new ArrayList<>();
    List<String> dateList = new ArrayList<>();
    List<String> longTimeList = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String sequence = "焦化910";
    private ReportCategoryTemplate currentTemplate;

    private String comma = ",";
    private String L1 = "CK9_10_MESR_ANA_cokeProduct_1d_avg," +
            "CK9_10_MESR_CI_LJC001_1d_avg," +
            "CK9_10_CDQ_rate_1d_avg," +
            "CK9_10_L2C_CDQ_25201_acculat_total_1d_avg," +
            "CK9_10_MESR_ANA_cokeProduct_1month_avg," +
            "CK9_10_MESR_CI_LJC001_1month_avg," +
            "CK9_10_L2C_CDQ_25201_acculat_total_1month_avg";
    private String L2 = "CK9_10_MESR_SH_Vd_1d_avg," +
            "CK9_10_MESR_SH_Mt_1d_avg," +
            "CK9_10_MESR_SH_Ad_1d_avg," +
            "CK9_10_MESR_SH_Std_1d_avg," +
            "CK9_10_MESR_SH_G_1d_avg," +
            "CK9_10_MESR_SH_G_Y_1d_avg";
    private String L3 = "CK9_10_MESR_SH_K3_1d_avg," +
            "CK9_10_MESR_SH_9KAVG_1d_avg," +
            "CK9_10_MESR_SH_9KAN_1d_avg," +
            "CK9_10_MESR_SH_10KAVG_1d_avg," +
            "CK9_10_MESR_SH_10KAN_1d_avg," +
            "CK9_10_MESR_SH_9MACH_1d_avg," +
            "CK9_10_MESR_SH_10MACH_1d_avg," +
            "CK9_10_MESR_SH_9FOCAL_1d_avg," +
            "CK9_10_MESR_SH_10FOCAL_1d_avg";
    private String L4 = "CK9_10_CDQ_rate_1d_avg," +
            "CK9_10_L2C_CDQ_C_3TE_25101a_1d_avg," +
            "CK9_10_L2C_CDQ_C_23TE_225101a_1d_avg," +
            "CK9_10_L2C_CDQ_auxfi309_total_1d_avg," +
            "CK9_10_L2C_CDQ_auxfi309_2total_1d_avg," +
            "CK9_10_L2C_CDQ_mr_tag0108_1d_avg," +
            "CK9_10_L2C_CDQ_mr_2tag0108_1d_avg," +
            "CK9_10_L2C_CDQ_EI01AI118_00_1d_avg," +
            "CK9_10_L2C_CDQ_EI01AI118_02_1d_avg," +
            "CK9_10_L2C_CDQ_EI01AI118_03_1d_avg," +
            "CK9_10_L2C_CDQ_EI01AI118_01_1d_avg," +
            "CK9_10_L2C_CDQ_EI01AI118_200_1d_avg," +
            "CK9_10_L2C_CDQ_EI01AI118_203_1d_avg," +
            "CK9_10_L2C_CDQ_EI01AI118_202_1d_avg," +
            "CK9_10_L2C_CDQ_EI01AI118_201_1d_avg";

    private void initialData() {
        result = new HashMap<>();
        startTime = null;
        endTime = null;
        categoriesList = new ArrayList<>();
        dateList = new ArrayList<>();
        longTimeList = new ArrayList<>();
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void mainTask() {
        initialData();
        initDateTime();
        mainDeal();
        log.info("焦化生产诊断报告word生成完毕！");
    }

    public void mainDeal() {
        // 处理当前时间
        result.put("current_date", DateUtil.getFormatDateTime(endTime, DateUtil.yyyyMMddChineseFormat));
        String allTagNames = L1 + comma + L2 + comma + L3 + comma + L4;
        JSONObject data = getDataByTag(allTagNames, startTime, endTime);

        dealPart1(data);
        dealPart2(data);
        dealPart3(data);
        dealPart4(data);
        List<ReportCategoryTemplate> reportCategoryTemplates = reportCategoryTemplateService.selectTemplateInfo(JobEnum.jh_shengchanzhenduanbaogao.getCode(), LanguageEnum.cn_zh.getName(), sequence);
        if (CollectionUtils.isNotEmpty(reportCategoryTemplates)) {
            currentTemplate = reportCategoryTemplates.get(0);
            String templatePath = currentTemplate.getTemplatePath();
            log.info("焦化生产诊断报告模板路径：" + templatePath);
            comm(templatePath);
        }
    }

    // 技术经济模块
    private void dealPart1(JSONObject data) {
        dealPart(data, "partOne", L1);
        // 煤气量
        dealMeiQiLiang();
        // 昨日产焦较前日
        dealIncreaseYesterday(data, "CK9_10_MESR_ANA_cokeProduct_1d_avg", "textOne1", "countOne1", 0);
        // 昨日配合煤消耗较前日
        dealIncreaseYesterday(data, "CK9_10_MESR_CI_LJC001_1d_avg", "textOne2", "countOne2", 0);
        // 昨日干熄率较前日 保留一位小数
        dealIncreaseYesterday(data, "CK9_10_CDQ_rate_1d_avg", "textOne3", "countOne3", 1);
        // 昨日蒸汽量较前日
        dealIncreaseYesterday(data, "CK9_10_L2C_CDQ_25201_acculat_total_1d_avg", "textOne4", "countOne4", 0);
        // 焦炭平均产量
        selectProdItemValue();
        // 趋势分析 产量和配合煤
        dealChart(data, "CK9_10_MESR_ANA_cokeProduct_1d_avg", "CK9_10_MESR_CI_LJC001_1d_avg", 5750d, 5250d, 6800d, 5800d, "产量(t)", "煤粉(t)", "chartOne1");
        dealChart(data, "CK9_10_CDQ_rate_1d_avg", "CK9_10_L2C_CDQ_25201_acculat_total_1d_avg", 5750d, 5250d, 6800d, 5800d, "干熄率(%)", "蒸汽量(t)", "chartOne2");
        // 蒸汽量累计
        result.put("countOne5", parse(dealMonthTotal(data, "CK9_10_L2C_CDQ_25201_acculat_total_1month_avg", false)));
    }

    // 配煤质量模块
    private void dealPart2(JSONObject data) {
        dealPart(data, "partTwo", L2);
        result.put("offsetVd", parse(dealOffset(20d, 23d, "CK9_10_MESR_SH_Vd_1d_avg", data)));
        result.put("offsetMT", parse(dealOffset(7d, 12d, "CK9_10_MESR_SH_Mt_1d_avg", data)));
        result.put("offsetAd", parse(dealOffset(0d, 13d, "CK9_10_MESR_SH_Ad_1d_avg", data)));
        result.put("offsetStd", parse(dealOffset(0d, 0.85d, "CK9_10_MESR_SH_Std_1d_avg", data)));
        result.put("offsetG", parse(dealOffset(70d, Double.MAX_VALUE, "CK9_10_MESR_SH_G_1d_avg", data)));
        result.put("offsetY", parse(dealOffset(0d, 21d, "CK9_10_MESR_SH_G_Y_1d_avg", data)));
        getCurrByDateTime(DateUtil.getFormatDateTime(endTime, "yyyy/MM/dd"));
        dealChart(data, "CK9_10_MESR_SH_Vd_1d_avg", "CK9_10_MESR_SH_Mt_1d_avg",
                5750d, 5250d, 6800d, 5800d, "Vd(%)", "MT(%)", "chartTwo1");
        dealChart(data, "CK9_10_MESR_SH_Ad_1d_avg", "CK9_10_MESR_SH_Std_1d_avg",
                5750d, 5250d, 6800d, 5800d, "Ad(%)", "Std(%)", "chartTwo2");
        dealChart(data, "CK9_10_MESR_SH_G_1d_avg", "CK9_10_MESR_SH_G_Y_1d_avg",
                5750d, 5250d, 6800d, 5800d, "G", "Y(mm)", "chartTwo3");
        dealIncreaseYesterday(data, "CK9_10_MESR_SH_Mt_1d_avg", "textTwo2", "countTwo2", 2);
        dealIncreaseYesterday(data, "CK9_10_MESR_SH_Ad_1d_avg", "textTwo3", "countTwo3", 2);
        dealIncreaseYesterday(data, "CK9_10_MESR_SH_Std_1d_avg", "textTwo4", "countTwo4", 2);
    }

    // 炼焦状态模块
    private void dealPart3(JSONObject data) {
        dealPart(data, "partThree", L3);
        Double offsetCoke1 = dealOffset(0.75d, Double.MAX_VALUE, "CK9_10_MESR_SH_K3_1d_avg", data);
        Double offsetCoke2 = dealOffset(0.7d, Double.MAX_VALUE, "CK9_10_MESR_SH_9KAVG_1d_avg", data);
        Double offsetCoke3 = dealOffset(0.7d, Double.MAX_VALUE, "CK9_10_MESR_SH_9KAN_1d_avg", data);
        Double offsetCoke4 = dealOffset(0.7d, Double.MAX_VALUE, "CK9_10_MESR_SH_10KAVG_1d_avg", data);
        Double offsetCoke5 = dealOffset(0.7d, Double.MAX_VALUE, "CK9_10_MESR_SH_10KAN_1d_avg", data);
        Double offsetCoke6 = dealOffset(1258d, 1270d, "CK9_10_MESR_SH_9MACH_1d_avg", data);
        Double offsetCoke7 = dealOffset(1258d, 1270d, "CK9_10_MESR_SH_10MACH_1d_avg", data);
        Double offsetCoke8 = dealOffset(1308d, 1322d, "CK9_10_MESR_SH_9FOCAL_1d_avg", data);
        Double offsetCoke9 = dealOffset(1308d, 1322d, "CK9_10_MESR_SH_10FOCAL_1d_avg", data);
        result.put("offsetCoke1", parse(offsetCoke1));
        result.put("offsetCoke2", parse(offsetCoke2));
        result.put("offsetCoke3", parse(offsetCoke3));
        result.put("offsetCoke4", parse(offsetCoke4));
        result.put("offsetCoke5", parse(offsetCoke5));
        result.put("offsetCoke6", parse(offsetCoke6));
        result.put("offsetCoke7", parse(offsetCoke7));
        result.put("offsetCoke8", parse(offsetCoke8));
        result.put("offsetCoke9", parse(offsetCoke9));
        StringBuilder sb = new StringBuilder();
        if (offsetCoke1 != null) {
            if (offsetCoke1 > 0d) {
                sb.append("K3结果偏高").append(parse(offsetCoke1)).append(", ");
            } else if (offsetCoke1 < 0d) {
                sb.append("K3结果偏低").append(parse(Math.abs(offsetCoke1))).append(", ");
            }
        }
        if (offsetCoke2 != null) {
            if (offsetCoke2 > 0d) {
                sb.append("9#K均结果偏高").append(parse(offsetCoke2)).append(", ");
            } else if (offsetCoke2 < 0d) {
                sb.append("9#K均结果偏低").append(parse(Math.abs(offsetCoke2))).append(", ");
            }
        }
        if (offsetCoke3 != null) {
            if (offsetCoke3 > 0d) {
                sb.append("9#K安结果偏高").append(parse(offsetCoke3)).append(", ");
            } else if (offsetCoke3 < 0d) {
                sb.append("9#K安结果偏低").append(parse(Math.abs(offsetCoke3))).append(", ");
            }
        }
        if (offsetCoke4 != null) {
            if (offsetCoke4 > 0d) {
                sb.append("10#K均结果偏高").append(parse(offsetCoke4)).append(", ");
            } else if (offsetCoke4 < 0d) {
                sb.append("10#K均结果偏低").append(parse(Math.abs(offsetCoke4))).append(", ");
            }
        }
        if (offsetCoke5 != null) {
            if (offsetCoke5 > 0d) {
                sb.append("10#K安结果偏高").append(parse(offsetCoke5)).append(", ");
            } else if (offsetCoke5 < 0d) {
                sb.append("10#K安结果偏低").append(parse(Math.abs(offsetCoke5))).append(", ");
            }
        }
        if (offsetCoke6 != null) {
            if (offsetCoke6 > 0d) {
                sb.append("9#机侧直行温度结果偏高").append(parse(offsetCoke6)).append(", ");
            } else if (offsetCoke6 < 0d) {
                sb.append("9#机侧直行温度结果偏低").append(parse(Math.abs(offsetCoke6))).append(", ");
            }
        }
        if (offsetCoke7 != null) {
            if (offsetCoke7 > 0d) {
                sb.append("10#机侧直行温度结果偏高").append(parse(offsetCoke7)).append(", ");
            } else if (offsetCoke7 < 0d) {
                sb.append("10#机侧直行温度结果偏低").append(parse(Math.abs(offsetCoke7))).append(", ");
            }
        }
        if (offsetCoke8 != null) {
            if (offsetCoke8 > 0d) {
                sb.append("9#焦侧直行温度结果偏高").append(parse(offsetCoke8)).append(", ");
            } else if (offsetCoke8 < 0d) {
                sb.append("9#焦侧直行温度结果偏低").append(parse(Math.abs(offsetCoke8))).append(", ");
            }
        }
        if (offsetCoke9 != null) {
            if (offsetCoke9 > 0d) {
                sb.append("10#焦侧直行温度结果偏高").append(parse(offsetCoke9)).append(", ");
            } else if (offsetCoke9 < 0d) {
                sb.append("10#焦侧直行温度结果偏低").append(parse(Math.abs(offsetCoke9))).append(", ");
            }
        }
        if (sb.toString().isEmpty()) {
            sb.append(" ");
        }
        result.put("partOverview3", sb.toString());
    }

    // 干熄状态模块
    private void dealPart4(JSONObject data) {
        // (1)干熄操作
        // 实际值
        dealPart(data, "partFour", L4);
        // 偏差-干熄率
        Double offsetDry1 = dealOffset(90d, Double.MAX_VALUE, "CK9_10_CDQ_rate_1d_avg", data);
        // 偏差-5#排焦温度
        Double offsetDry2 = dealOffset(80d, 90d, "CK9_10_L2C_CDQ_C_3TE_25101a_1d_avg", data);
        // 偏差-6#排焦温度
        Double offsetDry3 = dealOffset(200d, 270d, "CK9_10_L2C_CDQ_C_23TE_225101a_1d_avg", data);
        // 偏差-5#CO
        Double offsetDry8 = dealOffset(0d, 0.5d, "CK9_10_L2C_CDQ_EI01AI118_00_1d_avg", data);
        // 偏差-5#H2
        Double offsetDry9 = dealOffset(0d, 2.5d, "CK9_10_L2C_CDQ_EI01AI118_02_1d_avg", data);
        // 偏差-5#O2
        Double offsetDry10 = dealOffset(0d, 0.8d, "CK9_10_L2C_CDQ_EI01AI118_03_1d_avg", data);
        // 偏差-6#CO
        Double offsetDry12 = dealOffset(0d, 0.5d, "CK9_10_L2C_CDQ_EI01AI118_200_1d_avg", data);
        // 6#H2
        Double offsetDry13 = dealOffset(0d, 2.5d, "CK9_10_L2C_CDQ_EI01AI118_203_1d_avg", data);

        result.put("offsetDry1", parse(offsetDry1));
        result.put("offsetDry2", parse(offsetDry2));
        result.put("offsetDry3", parse(offsetDry3));
        result.put("offsetDry8", parse(offsetDry8));
        result.put("offsetDry9", parse(offsetDry9));
        result.put("offsetDry10", parse(offsetDry10));
        result.put("offsetDry12", parse(offsetDry12));
        result.put("offsetDry13", parse(offsetDry13));

        // (2)趋势分析 5炉
        dealChart(data, "CK9_10_L2C_CDQ_EI01AI118_00_1d_avg", "CK9_10_L2C_CDQ_EI01AI118_02_1d_avg",
                "CK9_10_L2C_CDQ_EI01AI118_03_1d_avg", "CK9_10_L2C_CDQ_EI01AI118_01_1d_avg",
                5750d, 5250d, 6800d, 5800d, 5750d, 5250d, 6800d, 5800d,
                "5#CO", "5#H2", "5#O2", "5#CO2", "chartFour1");
        dealChart(data, "CK9_10_L2C_CDQ_EI01AI118_200_1d_avg", "CK9_10_L2C_CDQ_EI01AI118_203_1d_avg",
                "CK9_10_L2C_CDQ_EI01AI118_202_1d_avg", "CK9_10_L2C_CDQ_EI01AI118_201_1d_avg",
                5750d, 5250d, 6800d, 5800d, 5750d, 5250d, 6800d, 5800d,
                "6#CO", "6#H2", "6#O2", "6#CO2", "chartFour2");

        // (3) 小结的结论
        StringBuilder sb = new StringBuilder();
        if (offsetDry1 != null) {
            if (offsetDry1 > 0d) {
                sb.append("干熄率结果偏高").append(parse(offsetDry1)).append(", ");
            } else if (offsetDry1 < 0d) {
                sb.append("干熄率结果偏低").append(parse(Math.abs(offsetDry1))).append(", ");
            }
        }
        if (offsetDry2 != null) {
            if (offsetDry2 > 0d) {
                sb.append("5#排焦温度结果偏高").append(parse(offsetDry2)).append(", ");
            } else if (offsetDry2 < 0d) {
                sb.append("5#排焦温度结果偏低").append(parse(Math.abs(offsetDry2))).append(", ");
            }
        }
        if (offsetDry3 != null) {
            if (offsetDry3 > 0d) {
                sb.append("6#排焦温度结果偏高").append(parse(offsetDry3)).append(", ");
            } else if (offsetDry3 < 0d) {
                sb.append("6#排焦温度结果偏低").append(parse(Math.abs(offsetDry3))).append(", ");
            }
        }
        if (offsetDry8 != null) {
            if (offsetDry8 > 0d) {
                sb.append("5#CO结果偏高").append(parse(offsetDry8)).append(", ");
            } else if (offsetDry8 < 0d) {
                sb.append("5#CO结果偏低").append(parse(Math.abs(offsetDry8))).append(", ");
            }
        }
        if (offsetDry9 != null) {
            if (offsetDry9 > 0d) {
                sb.append("5#H2结果偏高").append(parse(offsetDry9)).append(", ");
            } else if (offsetDry9 < 0d) {
                sb.append("5#H2结果偏低").append(parse(Math.abs(offsetDry9))).append(", ");
            }
        }
        if (offsetDry10 != null) {
            if (offsetDry10 > 0d) {
                sb.append("5#O2结果偏高").append(parse(offsetDry10)).append(", ");
            } else if (offsetDry10 < 0d) {
                sb.append("5#O2结果偏低").append(parse(Math.abs(offsetDry10))).append(", ");
            }
        }
        if (offsetDry12 != null) {
            if (offsetDry12 > 0d) {
                sb.append("6#CO结果偏高").append(parse(offsetDry12)).append(", ");
            } else if (offsetDry12 < 0d) {
                sb.append("6#CO结果偏低").append(parse(Math.abs(offsetDry12))).append(", ");
            }
        }
        if (offsetDry13 != null) {
            if (offsetDry13 > 0d) {
                sb.append("6#H2结果偏高").append(parse(offsetDry13)).append(", ");
            } else if (offsetDry13 < 0d) {
                sb.append("6#H2结果偏低").append(parse(Math.abs(offsetDry13))).append(", ");
            }
        }
        if (sb.toString().isEmpty()) {
            sb.append(" ");
        }
        result.put("partOverview4", sb.toString());
    }

    /**
     * 通过tag点获取得数据，组装到result
     *
     * @param data
     * @param prefix
     */
    private void dealPart(JSONObject data, String prefix, String tagNames) {
        if (data != null && data.size() > 0) {
            String[] tags = tagNames.split(comma);
            for (int i = 0; i < tags.length; i++) {
                Double doubleValue = getLastByTag(data, tags[i]);
                // 第一部分除了干熄率保留一位小数，其他部分为整数
                if ("partOne".equals(prefix)) {
                    if ("CK9_10_CDQ_rate_1d_avg".equals(tags[i])) {
                        result.put(prefix + String.valueOf(i + 1), parse(doubleValue, 1));
                    } else {
                        result.put(prefix + String.valueOf(i + 1), parseInteger(doubleValue));
                    }
                } else {
                    // TODO 其他部分还未确认，暂时保留两位小数
                    result.put(prefix + String.valueOf(i + 1), parse(doubleValue));
                }
            }
        }
    }

    /**
     *
     * @param data
     * @param tagName
     * @param textAddr
     * @param countAddr
     * @param n 小数点保留位数
     */
    private void dealIncreaseYesterday(JSONObject data, String tagName, String textAddr, String countAddr, int n) {
        Double increase = null;
        List<Double> tagObject = getLast2ByTag(data, tagName);
        Double yesterday = tagObject.get(tagObject.size() - 1);
        Double twoDaysAgo = tagObject.get(tagObject.size() - 2);
        if (yesterday != null) {
            if (twoDaysAgo != null) {
                increase = yesterday - twoDaysAgo;
            }
        }
        if (increase != null && increase >= 0d) {
            result.put(textAddr, "升高");
        } else {
            result.put(textAddr, "降低");
        }
        if (increase != null) {
            if (n == 0) {
                // 保留整数
                result.put(countAddr, parseInteger(Math.abs(increase.doubleValue())));
            } else {
                result.put(countAddr, parse(Math.abs(increase.doubleValue()), n));
            }
        }
        else {
            result.put(countAddr, parse(increase));
        }
    }

    private Double dealMonthTotal(JSONObject data, String tagName, Boolean isAvg) {
        Double result = null;
        Double total = 0d;
        Double count = 0d;
        List<Double> tagObject = getValuesByTag(data, tagName);
        for (Double item : tagObject) {
            if (item != null) {
                total += item;
                count++;
            }
        }
        if (count > 0d) {
            if (isAvg) {
                result = total / count;
            } else {
                result = total;
            }
        }
        return result;
    }

    /**
     * 获取昨日的tag点值
     * @param data
     * @param tagName
     * @return
     */
    private Double getLastByTag(JSONObject data, String tagName) {
        JSONArray tagArray = data.getJSONArray(tagName);
        return getValueByClock(tagArray, endTime);
    }

    /**
     * 获取昨天和前天的tag点数据
     * @param data
     * @param tagName
     * @return
     */
    private List<Double> getLast2ByTag(JSONObject data, String tagName) {
        JSONArray tagArray = data.getJSONArray(tagName);
        List<Double> vals = new ArrayList<>();
        vals.add(getValueByClock(tagArray, beforeYesterday));
        vals.add(getValueByClock(tagArray, endTime));
        return vals;
    }


    /**
     * 取longTimeList天的tag点的value
     * @param data
     * @param tagName
     * @return
     */
    private List<Double> getValuesByTag(JSONObject data, String tagName) {
        JSONArray tagArray = data.getJSONArray(tagName);
        List<Double> vals = new ArrayList<>();
        for (String time : longTimeList) {
            Double val = getValueByClock(tagArray, time);
            vals.add(val);
        }
        return vals;
    }

    /**
     * 取指定某天的value
     * @param tagArray
     * @param clock
     * @return
     */
    private Double getValueByClock(JSONArray tagArray, Date clock) {
        Double result = null;
        if (tagArray != null && tagArray.size() > 0) {
            try {
                for (int i = 0; i < tagArray.size(); i++) {
                    JSONObject o = tagArray.getJSONObject(i);
                    String c = o.getString("clock");
                    Date d2 = sdf.parse(c);
                    if (DateUtil.isSameDay(clock, d2)) {
                        result = new Double(o.getString("val"));
                    }
                }
            } catch (ParseException pex) {
                log.error(pex.getMessage());
            }
        }
        return result;
    }

    /**
     * // 取指定某天的value
     * @param tagArray
     * @param clock
     * @return
     */
    private Double getValueByClock(JSONArray tagArray, String clock) {
        Double result = null;
        if (tagArray != null && tagArray.size() > 0) {
            try {
                for (int i = 0; i < tagArray.size(); i++) {
                    JSONObject o = tagArray.getJSONObject(i);
                    String c = o.getString("clock");
                    Long lt = new Long(clock);
                    Date d1 = new Date(lt);
                    Date d2 = sdf.parse(c);
                    if (DateUtil.isSameDay(d1, d2)) {
                        result = new Double(o.getString("val"));
                    }
                }
            } catch (ParseException pex) {
                log.error(pex.getMessage());
            }
        }
        return result;
    }

    /**
     * 通过tag点获取数据
     *
     * @param tagNames
     * @param startTime
     * @param endTime
     * @return
     */
    private JSONObject getDataByTag(String tagNames, Date startTime, Date endTime) {
        String apiPath = "/jhTagValue/getNewTagValue";
        Map<String, String> query = new HashMap<String, String>();
        query.put("endDate", String.valueOf(endTime.getTime()));
        query.put("startDate", String.valueOf(startTime.getTime()));
        query.put("tagNames", tagNames);
        String results = httpUtil.get(httpProperties.getUrlApiJHOne() + apiPath, query);
        JSONObject jsonObject = JSONObject.parseObject(results, Feature.OrderedField);
        JSONObject data = jsonObject.getJSONObject("data");

        return data;
    }

    private void dealChart(JSONObject data, String tag1, String tag2, double mx1, double mn1, double mx2, double mn2, String serie1, String serie2, String addr) {
        List<Double> tagObject1 = getValuesByTag(data, tag1);
        List<Double> tagObject2 = getValuesByTag(data, tag2);
        List<Double> tempObject1 = new ArrayList<>();
        tempObject1.addAll(tagObject1);
        List<Double> tempObject2 = new ArrayList<>();
        tempObject2.addAll(tagObject2);
        tempObject1.removeAll(Collections.singleton(null));
        tempObject2.removeAll(Collections.singleton(null));

        Double max1 = tempObject1.size() > 0 ? getAxisMaxValue(tempObject1) : mx1;
        Double min1 = tempObject1.size() > 0 ? getAxisMinValue(tempObject1) : mn1;

        Double max2 = tempObject2.size() > 0 ? getAxisMaxValue(tempObject2) : mx2;
        Double min2 = tempObject2.size() > 0 ? getAxisMinValue(tempObject2) : mn2;

        if (max1.equals(min1)) {
            max1 = mx1;
            min1 = mn1;
        }
        if (max2.equals(min2)) {
            max2 = mx2;
            min2 = mn2;
        }

        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie(serie1, tagObject1.toArray()));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie(serie2, tagObject2.toArray()));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);

        String title1 = "";
        String categoryAxisLabel1 = "";
        String[] yLabels = {serie1, serie2};

        int[] stack = {1, 1};
        int[] ystack = {1, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, 2, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put(addr, image1);
    }

    private void dealChart(JSONObject data, String tag1, String tag2, String tag3, String tag4,
                           double mx1, double mn1, double mx2, double mn2, double mx3, double mn3, double mx4, double mn4,
                           String serie1, String serie2, String serie3, String serie4, String addr) {
        List<Double> tagObject1 = getValuesByTag(data, tag1);
        List<Double> tagObject2 = getValuesByTag(data, tag2);
        List<Double> tagObject3 = getValuesByTag(data, tag3);
        List<Double> tagObject4 = getValuesByTag(data, tag4);

        List<Double> tempObject1 = new ArrayList<>();
        tempObject1.addAll(tagObject1);
        List<Double> tempObject2 = new ArrayList<>();
        tempObject2.addAll(tagObject2);
        List<Double> tempObject3 = new ArrayList<>();
        tempObject3.addAll(tagObject3);
        List<Double> tempObject4 = new ArrayList<>();
        tempObject4.addAll(tagObject4);

        tempObject1.removeAll(Collections.singleton(null));
        tempObject2.removeAll(Collections.singleton(null));
        tempObject3.removeAll(Collections.singleton(null));
        tempObject4.removeAll(Collections.singleton(null));

        Double max1 = tempObject1.size() > 0 ? getAxisMaxValue(tempObject1) : mx1;
        Double min1 = tempObject1.size() > 0 ? getAxisMinValue(tempObject1) : mn1;

        Double max2 = tempObject2.size() > 0 ? getAxisMaxValue(tempObject2) : mx2;
        Double min2 = tempObject2.size() > 0 ? getAxisMinValue(tempObject2) : mn2;

        Double max3 = tempObject3.size() > 0 ? getAxisMaxValue(tempObject3) : mx3;
        Double min3 = tempObject3.size() > 0 ? getAxisMinValue(tempObject3) : mn3;

        Double max4 = tempObject4.size() > 0 ? getAxisMaxValue(tempObject4) : mx4;
        Double min4 = tempObject4.size() > 0 ? getAxisMinValue(tempObject4) : mn4;

        if (max1.equals(min1)) {
            max1 = mx1;
            min1 = mn1;
        }
        if (max2.equals(min2)) {
            max2 = mx2;
            min2 = mn2;
        }
        if (max3.equals(min3)) {
            max3 = mx3;
            min3 = mn3;
        }
        if (max4.equals(min4)) {
            max4 = mx4;
            min4 = mn4;
        }

        // 标注类别
        Vector<Serie> series1 = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series1.add(new Serie(serie1, tagObject1.toArray()));

        // 标注类别
        Vector<Serie> series2 = new Vector<Serie>();
        series2.add(new Serie(serie2, tagObject2.toArray()));

        Vector<Serie> series3 = new Vector<Serie>();
        series3.add(new Serie(serie3, tagObject3.toArray()));

        Vector<Serie> series4 = new Vector<Serie>();
        series4.add(new Serie(serie4, tagObject4.toArray()));

        List<Vector<Serie>> vectors = new ArrayList<>();
        vectors.add(series1);
        vectors.add(series2);
        vectors.add(series3);
        vectors.add(series4);

        String title1 = "";
        String categoryAxisLabel1 = "";
        String[] yLabels = {serie1, serie2, serie3, serie4};

        int[] stack = {1, 1, 1, 1};
        int[] ystack = {1, 2, 2, 2};

        JFreeChart Chart1 = ChartFactory.createLineChart(title1,
                categoryAxisLabel1, yLabels, vectors,
                categoriesList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, min3, max3, min4, max4, 4, stack, ystack);
        WordImageEntity image1 = image(Chart1);
        result.put(addr, image1);
    }

    // 煤气量消耗
    private void dealMeiQiLiang() {
        Double today = 0d;
        Double abs = 0d;
        Double today1 = selectAddDirct(1, DateUtil.getFormatDateTime(endTime, "yyyy/MM/dd"), "CK9_10_L2C_CI_211CF151FT_TOTALIZERA_1_PV_1m_avg");
        Double today2 = selectAddDirct(2, DateUtil.getFormatDateTime(endTime, "yyyy/MM/dd"), "CK9_10_L2C_CI_211CF151FT_TOTALIZERA_1_PV_1m_avg");
        Double yesterday1 = selectAddDirct(1, DateUtil.getFormatDateTime(beforeYesterday, "yyyy/MM/dd"), "CK9_10_L2C_CI_211CF151FT_TOTALIZERA_1_PV_1m_avg");
        Double yesterday2 = selectAddDirct(2, DateUtil.getFormatDateTime(beforeYesterday, "yyyy/MM/dd"), "CK9_10_L2C_CI_211CF151FT_TOTALIZERA_1_PV_1m_avg");
        Double today1T = selectAddDirct(1, DateUtil.getFormatDateTime(endTime, "yyyy/MM/dd"), "CK9_10_L2C_CI_211CF151FT_TOTALIZERA_PV_1m_avg");
        Double today2T = selectAddDirct(2, DateUtil.getFormatDateTime(endTime, "yyyy/MM/dd"), "CK9_10_L2C_CI_211CF151FT_TOTALIZERA_PV_1m_avg");
        Double yesterday1T = selectAddDirct(1, DateUtil.getFormatDateTime(beforeYesterday, "yyyy/MM/dd"), "CK9_10_L2C_CI_211CF151FT_TOTALIZERA_PV_1m_avg");
        Double yesterday2T = selectAddDirct(2, DateUtil.getFormatDateTime(beforeYesterday, "yyyy/MM/dd"), "CK9_10_L2C_CI_211CF151FT_TOTALIZERA_PV_1m_avg");
        if (today1 != null) {
            today += today1;
        }
        if (today2 != null) {
            today += today2;
        }
        if (today1T != null) {
            today += today1T;
        }
        if (today2T != null) {
            today += today2T;
        }
        abs = today;
        if (yesterday1 != null) {
            abs -= yesterday1;
        }
        if (yesterday2 != null) {
            abs -= yesterday2;
        }
        if (yesterday1T != null) {
            abs -= yesterday1T;
        }
        if (yesterday2T != null) {
            abs -= yesterday2T;
        }
        result.put("selectAddDirct1", parseInteger(today));
        if (abs >= 0d) {
            result.put("selectAddDirct2", "升高");
        } else {
            result.put("selectAddDirct2", "降低");
        }
        result.put("selectAddDirct3", parseInteger(Math.abs(abs.doubleValue())));
    }

    // 焦炭平均产量
    private void selectProdItemValue() {
        String apiPath = "/cokingYieldAndNumberHoles/selectProdItemValue";
        Map<String, String> query = new HashMap<String, String>();
        query.put("date", DateUtil.getFormatDateTime(endTime, "yyyy/MM/dd"));
        String results = httpUtil.get(httpProperties.getUrlApiJHOne() + apiPath, query);
        JSONObject jsonObject = JSONObject.parseObject(results, Feature.OrderedField);
        if (jsonObject != null) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                Double avg = data.getDouble("yieldAvg");
//            double total = 0d;
//            for (int i = 0; i < array.size(); i++) {
//                total += array.getJSONObject(i).getDouble("");
//            }
                result.put("selectProdItemValue1", parseInteger(avg));
            }
        } else {
            result.put("selectProdItemValue1", parse(null));
        }
    }

    private Double selectAddDirct(int shift, String workDate, String tag) {
        Double result = null;
        String apiPath = "/adjustingInput/selectAddDirct";
        Map<String, String> query = new HashMap<String, String>();
        query.put("shift", String.valueOf(shift));
        query.put("tagName", tag);
        query.put("workDate", workDate);
        String results = httpUtil.get(httpProperties.getUrlApiJHOne() + apiPath, query);
        JSONObject jsonObject = JSONObject.parseObject(results, Feature.OrderedField);
        if (jsonObject != null) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                data = data.getJSONObject("val");
                if (data != null) {
                    result = new Double(data.getString(tag));
                }
            }
        }
        return result;
    }

    private void getCurrByDateTimeOld(String date) {
        String apiPath = "/coalBlendingParameter/getCurrByDateTime";
        Map<String, String> query = new HashMap<String, String>();
        query.put("date", date);
        String results = httpUtil.get(httpProperties.getUrlApiJHOne() + apiPath, query);
        JSONObject jsonObject = JSONObject.parseObject(results, Feature.OrderedField);
        if (jsonObject != null) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                Map<String, Object> map = data.getInnerMap();
                if (map != null) {
                    List<Map<String, Object>> rows = new ArrayList<>();
                    int i = 0;
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (i >= longTimeList.size()) {
                            break;
                        }
                        i++;
                        Map<String, Object> row = new HashMap<>();
                        Long lt = new Long(entry.getKey());
                        row.put("date", DateUtil.getFormatDateTime(new Date(lt), "yyyy/MM/dd"));
                        JSONArray array = JSONArray.class.isInstance(entry.getValue()) ? ((JSONArray) entry.getValue()) : null;
                        if (array != null && array.size() > 0) {
                            for (int j = 1; j <= 8; j++) {
                                JSONObject item = array.getJSONObject(j - 1);
                                if (item != null) {
                                    row.put("name" + j, item.getString("coalTypeDescr"));
                                    row.put("wet" + j, parse(item.getDouble("wetMatchRatio")));
                                    row.put("dry" + j, parse(item.getDouble("dryMatchRatio")));
                                } else {
                                    row.put("name" + j, parse(null));
                                    row.put("wet" + j, parse(null));
                                    row.put("dry" + j, parse(null));
                                }
                            }
                        }
                        rows.add(row);
                    }
                    result.put("sheet1", rows);
                }
            }
        }
    }

    /**
     * 配合煤配比
     * @param date
     */
    private void getCurrByDateTime(String date) {
        String apiPath = "/coalBlendingParameter/getCurrByDateTime";
        Map<String, String> query = new HashMap<String, String>();
        query.put("date", date);
        String results = httpUtil.get(httpProperties.getUrlApiJHOne() + apiPath, query);
        JSONObject jsonObject = JSONObject.parseObject(results, Feature.OrderedField);
        if (jsonObject != null) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                Map<String, Object> map = data.getInnerMap();
                if (map != null) {
                    List<Map<String, Object>> rows = new ArrayList<>();
                    int i = 0;
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (i >= longTimeList.size()) {
                            break;
                        }
                        i++;
                        Map<String, Object> row1 = new HashMap<>();
                        Map<String, Object> row2 = new HashMap<>();
                        Map<String, Object> row3 = new HashMap<>();
                        Long lt = new Long(entry.getKey());
                        row1.put("date", DateUtil.getFormatDateTime(new Date(lt), "yyyy/MM/dd"));
                        row2.put("date", "");
                        row3.put("date", "");
                        row1.put("desc", "名称");
                        row2.put("desc", "湿配比");
                        row3.put("desc", "干配比");
                        JSONArray array = JSONArray.class.isInstance(entry.getValue()) ? ((JSONArray) entry.getValue()) : null;
                        if (array != null && array.size() > 0) {
                            for (int j = 1; j <= 8; j++) {
                                JSONObject item = array.getJSONObject(j - 1);
                                if (item != null) {
                                    row1.put("val" + j, item.getString("coalTypeDescr"));
                                    row2.put("val" + j, parse(item.getDouble("wetMatchRatio")));
                                    row3.put("val" + j, parse(item.getDouble("dryMatchRatio")));
                                } else {
                                    row1.put("val" + j, parse(null));
                                    row2.put("val" + j, parse(null));
                                    row3.put("val" + j, parse(null));
                                }
                            }
                        }
                        rows.add(row1);
                        rows.add(row2);
                        rows.add(row3);
                    }
                    result.put("sheet1", rows);
                }
            }
        }
    }

    // 处理偏差
    private Double dealOffset(Double min, Double max, String tagName, JSONObject data) {
        Double result = null;
        Double actual = getLastByTag(data, tagName);
        if (actual != null) {
            if (actual > max) {
                result = actual - max;
            }
            else if (actual < min) {
                result = actual - min;
            }
            else {
                result = 0d;
            }
        }
        return result;
    }

    private void comm(String path) {
        try {
            XWPFDocument doc = WordExportUtil.exportWord07(path, result);

            // 合并配合煤配比表格的日期单元格
            List<XWPFTable> tt = doc.getTables();
            if (tt != null && tt.size() > 1) {
                for (int i = 1; i + 2 < tt.get(1).getRows().size(); i += 3) {
                    mergeCellsVertically(tt.get(1), 0, i, i + 2);
                }
            }
            String fileName = String.format("%s_%s_%s.docx", sequence, currentTemplate.getTemplateName(), DateUtil.getFormatDateTime(endTime, "yyyyMMdd"));
            String filePath = jobProperties.getFilePath() + File.separator + "doc" + File.separator + fileName;
            FileOutputStream fos = new FileOutputStream(filePath);
            doc.write(fos);
            fos.close();

            ReportIndex reportIndex = new ReportIndex();
            reportIndex.setSequence(sequence)
                    .setReportCategoryCode(JobEnum.jh_shengchanzhenduanbaogao.getCode())
                    .setName(fileName)
                    .setPath(filePath)
                    .setIndexLang(LanguageEnum.getByLang(currentTemplate.getTemplateLang()).getName())
                    .setIndexType(ReportTemplateTypeEnum.getType(currentTemplate.getTemplateType()).getCode())
                    .setRecordDate(new Date());

            reportIndexService.insertReportRecord(reportIndex);
            log.info("焦化生产诊断报告word文档生成完毕" + filePath);
        } catch (Exception e) {
            log.error("焦化生产诊断报告word文档失败", e);
        }
    }

    // word跨行并单元格
    public void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
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
     * 构造起始时间
     */
    private void initDateTime() {
        // 当前时间点
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        // 昨日23点，作为结束时间点
        endTime = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        beforeYesterday = cal.getTime();
        // 当月第一天，作为开始时间点
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        startTime = startDate;

        while (startDate.before(endTime)) {
            // 拼接x坐标轴
            categoriesList.add(DateUtil.getFormatDateTime(startDate, "MM-dd"));
            dateList.add(DateUtil.getFormatDateTime(startDate, DateUtil.yyyyMMddFormat));
            longTimeList.add(startDate.getTime() + "");

            // 递增日期
            cal.add(Calendar.DAY_OF_MONTH, 1);
            startDate = cal.getTime();
        }

        categoriesList.add(DateUtil.getFormatDateTime(endTime, "MM-dd"));
        dateList.add(DateUtil.getFormatDateTime(endTime, DateUtil.yyyyMMddFormat));
        longTimeList.add(endTime.getTime() + "");
    }

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

    // 处理double数据 四舍五入保留2位小数
    private String parse(Double v) {
        if (v == null) {
            return "   ";
        }
        Double value = NumberArithmeticUtils.roundingX(v, 2);
        if (value.equals(0d)) {
            return "0";
        } else {
            return value.toString();
        }
    }

    /**
     * 四舍五入取整
     * @return
     */
    private String parseInteger(Double v) {
        if (v == null) {
            return "   ";
        }
        Long value = Math.round(v);
        return value.toString();
    }

    /**
     *
     * @param v
     * @param n 小数点保留位数
     * @return
     */
    private String parse(Double v, int n) {
        if (v == null) {
            return "   ";
        }
        Double value = NumberArithmeticUtils.roundingX(v, n);
        if (value.equals(0d)) {
            return "0";
        } else {
            return value.toString();
        }
    }

    /**
     * 获取坐标轴最大值
     * @param tempObject1
     * @return
     */
    private Double getAxisMaxValue(List<Double> tempObject1) {
        Double max = Collections.max(tempObject1);
        if (max < 0) {
            return max * 0.8;
        }
        return max * 1.2;
    }

    /**
     * 获取坐标轴最小值
     * @param tempObject1
     * @return
     */
    private Double getAxisMinValue(List<Double> tempObject1) {
        Double min = Collections.min(tempObject1);
        if (min < 0) {
            return min * 1.2;
        }
        return min * 0.8;
    }
}
