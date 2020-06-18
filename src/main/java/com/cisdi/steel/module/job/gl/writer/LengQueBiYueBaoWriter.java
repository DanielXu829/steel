package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>Description: 8高炉冷却水冷却壁月报 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020/06/18 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class LengQueBiYueBaoWriter extends BaseGaoLuWriter {
    private String prefix = "BF8_L2C_BD_";
    private String[] stufix = new String[]{"_1d_max", "_1d_min", "_1d_avg"};
    private String[] gangZhuan = new String[]{"TI3701", "TI3702", "TI3703", "TI3704", "TI3705", "TI3706", "TI3707",
            "TI3708", "TI3709", "TI3710", "TI3711", "TI3712"
    };

    //第1，13，15段没有
    private String[] duan14 = new String[]{"TI3601", "TI3602", "TI3603", "TI3604", "TI3605", "TI3606", "TI3607", "TI3608",
            "TI3609", "TI3610"
    };

    private String[] duan12 = new String[]{"TI3501" ,"TI3502", "TI3503", "TI3504", "TI3505", "TI3506", "TI3507", "TI3508",
            "TI3509", "TI3510", "TI3511"
    };

    private String[] duan11 = new String[]{"TI3401", "TI3402", "TI3403", "TI3404", "TI3405", "TI3406", "TI3407", "TI3408",
            "TI3409", "TI3410", "TI3411", "TI3412"
    };

    private String[] duan10 = new String[]{"TI3301", "TI3302", "TI3303", "TI3304", "TI3305", "TI3306", "TI3307", "TI3308",
            "TI3309", "TI3310", "TI3311", "TI3312"
    };

    private String[] duan9 = new String[]{"TI3101", "TI3102", "TI3103", "TI3104", "TI3105", "TI3106", "TI3107", "TI3108",
            "TI3109", "TI3110", "TI3111", "TI3112", "TI3113", "TI3201", "TI3202", "TI3203", "TI3204"
    };

    private String[] duan8 = new String[]{"TI2901", "TI2902", "TI2903", "TI2904", "TI3001", "TI3002", "TI3003", "TI3004",
            "TI3005", "TI3006", "TI3007", "TI3008", "TI3009", "TI3010", "TI3011", "TI3012", "TI3013"
    };

    private String[] duan7 = new String[]{"TI2701", "TI2702", "TI2703", "TI2704", "TI2801", "TI2802", "TI2803", "TI2804",
            "TI2805", "TI2806", "TI2807", "TI2808", "TI2809", "TI2810", "TI2811", "TI2812", "TI2813"
    };

    private String[] duan6 = new String[]{"TI2501", "TI2502", "TI2503", "TI2504", "TI2601", "TI2602", "TI2603", "TI2604",
            "TI2605", "TI2606", "TI2607", "TI2608", "TI2609", "TI2610", "TI2611", "TI2612", "TI2613"
    };

    private String[] duan5 = new String[]{"TI2401", "TI2402", "TI2403", "TI2404", "TI2405", "TI2406", "TI2407", "TI2408",
            "TI2409", "TI2410"
    };

    private String[] duan4 = new String[]{"TI2301", "TI2302", "TI2303", "TI2304", "TI2305", "TI2307", "TI2308", "TI2306"
    };

    private String[] duan3 = new String[]{"TI2201", "TI2202", "TI2203", "TI2204", "TI2205", "TI2206", "TI2207", "TI2208"
    };

    private String[] duan2 = new String[]{"TI2101", "TI2102", "TI2103", "TI2104", "TI2105", "TI2106", "TI2107", "TI2108"
    };

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = "8.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
            log.error("在模板中获取version失败", e);
        }
        List<CellData> resultList = new ArrayList<>();
        try {
            int beginRow = 4;
            Sheet sheet = workbook.getSheetAt(0);
            List<Date> allDayBeginTimeInCurrentMonth = DateUtil.getAllDayBeginTimeInCurrentMonthBeforeDays(new Date(), 1);
            int fixLineCount = 0;
            for (int i = 0; i < allDayBeginTimeInCurrentMonth.size(); i++) {
                Date day = allDayBeginTimeInCurrentMonth.get(i);
                // 计算行
                if (i > 0 && i%10 ==0) {
                    fixLineCount++;
                }
                int rowIndex = beginRow + fixLineCount + i;
                handleLengQueShui(version, resultList, rowIndex, day);
                handleLengQueBi(version, resultList, rowIndex, day);
            }
            ExcelWriterUtil.setCellValue(sheet, resultList);
        } catch (Exception e) {
            log.error("处理 冷却水冷却壁月报 时产生错误", e);
            throw e;
        } finally {
            Date date = new Date();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (!Objects.isNull(sheet) && !workbook.isSheetHidden(i)) {
                    // 全局替换 当前日期
                    ExcelWriterUtil.replaceCurrentMonthInTitle(sheet, 0, 0, date);
                    PoiCustomUtil.clearPlaceHolder(sheet);
                }
            }
        }
        return workbook;
    }

    /**
     * 处理一行冷却水数据
     * @param version
     * @param resultList
     * @param rwoIndex
     */
    private void handleLengQueShui(String version, List<CellData> resultList, int rwoIndex, Date date) {
        try {

        } catch (Exception e) {
            log.error("冷却水冷却壁月报处理冷却水时产生错误", e);
        }
    }

    /**
     * 处理一行冷却壁数据
     * @param version
     * @param resultList
     * @param rwoIndex
     */
    private void handleLengQueBi(String version, List<CellData> resultList, int rwoIndex, Date date) {
        try {
            //段+钢砖总共13块，一块中有3列
            int beginColumn = 13;
            List<String[]> list = new ArrayList<String[]>(){{
                add(duan2);
                add(duan3);
                add(duan4);
                add(duan5);
                add(duan6);
                add(duan7);
                add(duan8);
                add(duan9);
                add(duan10);
                add(duan11);
                add(duan12);
                add(duan14);
                add(gangZhuan);
            }};
            for(int i = 0; i < list.size(); i++) {
                dealLengQueBiData(version, list.get(i), resultList, rwoIndex ,beginColumn, date);
                beginColumn = beginColumn + 3;
            }
        } catch (Exception e) {
            log.error("冷却水冷却壁月报处理冷却壁时产生错误", e);
        }
    }

    /**
     * 根据点名取值
     * @param version
     * @param tagNames
     * @param nameStufix
     * @return
     */
    private DoubleSummaryStatistics getLatestTagValues (String version, String[] tagNames, String nameStufix, Date date) {
        List<BigDecimal> list = new ArrayList<>();
        String url = getLatestTagValueUrl(version);
        long time = date.getTime();
        Map<String, String> param = new HashMap<>();
        param.put("time", String.valueOf(time));
        for (String tagName:tagNames) {
            String tageName = prefix + tagName + nameStufix;
            param.put("tagname", tageName);
            String result = httpUtil.get(url, param);
            if (StringUtils.isNotBlank(result)) {
                JSONObject object = JSON.parseObject(result);
                if (Objects.nonNull(object)) {
                    object = object.getJSONObject("data");
                    if (Objects.nonNull(object)) {
                        BigDecimal val = object.getBigDecimal("val");
                        list.add(val);
                    }
                }
            }
        }
        list.removeAll(Collections.singleton(null));
        if (list.size() == 0) {
            return null;
        }
        DoubleSummaryStatistics statistics = list.stream().mapToDouble(Number::doubleValue).summaryStatistics();
        return statistics;
    }

    /**
     * 取最大值
     * @param version
     * @param tagNames
     * @param nameStufix
     * @return
     */
    private Double getMaxValue(String version, String[] tagNames, String nameStufix, Date date) {
        DoubleSummaryStatistics statistics = getLatestTagValues(version, tagNames, nameStufix, date);
        if (Objects.nonNull(statistics)) {
            return statistics.getMax();
        }
        return null;
    }

    /**
     * 取最小值
     * @param version
     * @param tagNames
     * @param nameStufix
     * @return
     */
    private Double getMinValue(String version, String[] tagNames, String nameStufix, Date date) {
        DoubleSummaryStatistics statistics = getLatestTagValues(version, tagNames, nameStufix, date);
        if (Objects.nonNull(statistics)) {
            return statistics.getMin();
        }
        return null;
    }

    /**
     * 取平均值
     * @param version
     * @param tagNames
     * @param nameStufix
     * @return
     */
    private Double getAvgValue(String version, String[] tagNames, String nameStufix, Date date) {
        DoubleSummaryStatistics statistics = getLatestTagValues(version, tagNames, nameStufix, date);
        if (Objects.nonNull(statistics)) {
            return statistics.getAverage();
        }
        return null;
    }

    /**
     * 处理一块冷却壁数据
     * @param version
     * @param tagNames
     * @param resultList
     * @param rowIndex
     * @param columnIndex
     */
    private void dealLengQueBiData(String version, String[] tagNames, List<CellData> resultList, int rowIndex, int columnIndex, Date date) {
        for(int j = 0; j < stufix.length; j ++) {
            //处理一列数据
            Double val = null;
            switch (stufix[j]) {
                case "_1d_max":
                    val = getMaxValue(version, tagNames, stufix[j], date);
                    break;
                case "_1d_min":
                    val = getMinValue(version, tagNames, stufix[j], date);
                    break;
                case "_1d_avg":
                    val = getAvgValue(version, tagNames, stufix[j], date);
                    break;
            }
            ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex + j, val);
        }
    }
}
