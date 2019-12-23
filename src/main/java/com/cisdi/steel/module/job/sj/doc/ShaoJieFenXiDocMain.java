package com.cisdi.steel.module.job.sj.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.enums.LanguageEnum;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.tools.ant.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@Component
@Slf4j
@SuppressWarnings("ALL")
/**
 *
 */
public class ShaoJieFenXiDocMain {

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

    private String version = "4.0";

    /**
     * 第一部分点名
     */
    private String[] L1 = null;

    /**
     * doc最后结果
     */
    private HashMap<String, Object> result = null;

    @Scheduled(cron = "0 0 6 * * ?")
    public void mainJob() {
        result = new HashMap<>();
        Date date = new Date();
        mainDeal(version, date);
    }

    public void mainDeal(String version, Date date) {
        dealTagName();
        if (Objects.isNull(date)) {
            date = new Date();
        }
        dealPart1(version, L1, date);

        dealShaoJiePeiBiPart(date);

        dealYuanLiaoZhiLiangPart(date);

        //获取对应的路径
        List<ReportCategoryTemplate> reportCategoryTemplates = reportCategoryTemplateService.selectTemplateInfo(JobEnum.sj_shengchanfenxi4.getCode(), LanguageEnum.cn_zh.getName(), "4烧结");
        if (CollectionUtils.isNotEmpty(reportCategoryTemplates)) {
            String templatePath = reportCategoryTemplates.get(0).getTemplatePath();
            log.debug("烧结生产分析模板路径：" + templatePath);
            comm(version, templatePath);
        }
    }

    /**
     * 通过tag点获取得数据，组装到result
     *
     * @param version
     * @param tagNames
     * @param date
     */
    private void dealPart1(String version, String[] tagNames, Date date) {
        dealTagName();
        JSONObject data = getDataByTag(L1, DateUtil.addDays(date, -1), version);

        // 处理当前时间
        result.put("current_date", DateUtils.format(date, DateUtil.yyyyMMddChineseFormat));

        for (int i = 0; i < L1.length; i++) {
            Double doubleValue = 0d;
            JSONObject valueObject = data.getJSONObject(L1[i]);
            Map<String, Object> map = valueObject.getInnerMap();
            for (Object object : map.values()) {
                doubleValue = Double.parseDouble(object.toString());
            }
            result.put("attr" + String.valueOf(i + 1), doubleValue);
        }
    }

    /**
     * 处理“烧结配比”数据
     * @param date
     */
    private void dealShaoJiePeiBiPart(Date date) {
        String apiPath = "/burdenReport/getBurdenRatio";
        Map<String, String> queryParams = new LinkedHashMap<String, String>();
        queryParams.put("time", String.valueOf(DateUtil.getDateBeginTime(date).getTime()));
        SerializeConfig serializeConfig = new SerializeConfig();
        String results = httpUtil.get(getUrl(version) + apiPath, queryParams);
        if (StringUtils.isNotBlank(results)) {
            JSONObject jsonObject = JSONObject.parseObject(results);
            if (Objects.nonNull(jsonObject)) {
                JSONArray arr = jsonObject.getJSONArray("data");
                if (Objects.nonNull(arr) && arr.size() != 0) {
                    List list = new ArrayList();
                    for (int i = 0; i < arr.size(); i++) {
                        JSONObject data = arr.getJSONObject(i);
                        JSONObject values = data.getJSONObject("values");
                        Map<String, Object> valuesMap = values.getInnerMap();
                        list.add(valuesMap);
                    }
                    result.put("shaojiepeibi", list);
                }
            }
        }
    }

    /**
     * 处理“原料质量”数据
     * @param date
     */
    private void dealYuanLiaoZhiLiangPart(Date date) {
        String apiPath = "/burdenReport/getBurdenAvg";
        Map<String, String> queryParams = new LinkedHashMap<String, String>();
        DateQuery dateQuery = DateQueryUtil.buildToday(DateUtil.getDateBeginTime(date));
        JSONObject query = new JSONObject();
        query.put("start", dateQuery.getQueryStartTime());
        List<String> itemNames = new ArrayList<>();
        itemNames.add("CaO");
        itemNames.add("SiO2");
        itemNames.add("MgO");
        itemNames.add("TFe");
        itemNames.add("S");
        itemNames.add("Mt");
        itemNames.add("H2O");
        itemNames.add("SMS");
        itemNames.add("AOL");
        query.put("itemNames", itemNames);
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String results = httpUtil.postJsonParams(getUrl(version) + apiPath, jsonString);
        if (StringUtils.isNotBlank(results)) {
            JSONObject jsonObject = JSONObject.parseObject(results);
            if (Objects.nonNull(jsonObject)) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                if (Objects.nonNull(dataObject)) {
                    List list = new ArrayList();
                    Map<String, Object> dataMap = dataObject.getInnerMap();

                    dataMap.keySet().forEach(key -> {
                        Map<String, Object> valuesMap = new LinkedHashMap<>();
                        valuesMap.put("key", key);
                        valuesMap.putAll(dataObject.getJSONObject(key).getInnerMap());

                        //对于“水分”，优先去H2O，取不到再取Mt
                        Object h2O = valuesMap.get("H2O");
                        if (Objects.isNull(h2O)) {
                            valuesMap.put("H2O", valuesMap.get("Mt"));
                        }

                        list.add(valuesMap);
                    });

                    result.put("yuanliaozhiliang", list);
                }
            }
        }
    }

    /**
     * 生产word文档
     *
     * @param version
     * @param path
     */
    private void comm(String version, String path) {
        try {
            String sequence = "4烧结";
            XWPFDocument doc = WordExportUtil.exportWord07(path, result);

            List<XWPFTable> tt = doc.getTables();
            //insertShaojiePeibi(doc);

            String fileName = sequence + "_烧结生产分析_" + DateUtil.getFormatDateTime(new Date(), "yyyyMMdd") + ".docx";
            String filePath = jobProperties.getFilePath() + File.separator + "doc" + File.separator + fileName;
            FileOutputStream fos = new FileOutputStream(filePath);
            doc.write(fos);
            fos.close();

            ReportIndex reportIndex = new ReportIndex();
            reportIndex.setCreateTime(new Date());
            reportIndex.setUpdateTime(new Date());
            reportIndex.setSequence(sequence);
            reportIndex.setIndexLang("cn_zh");
            reportIndex.setIndexType("report_day");
            reportIndex.setRecordDate(new Date());
            reportIndex.setName(fileName);
            reportIndex.setReportCategoryCode(JobEnum.sj_shengchanfenxi4.getCode());
            reportIndex.setPath(filePath);
            reportIndexMapper.insert(reportIndex);

            log.info("生产烧结生产分析word文档生成完毕" + filePath);
        } catch (Exception e) {
            log.error("生产烧结生产分析word文档失败", e);
        }
    }

    /**
     * 定义L1
     */
    private void dealTagName() {
        L1 = new String[]
                {
                        "ST4_L1R_SIN_SiMaRunVel_1d_avg",
                        "ST4_L1R_SIN_DelAmtUse_1d_avg",
                        "ST4_L1R_SIN_1MainChiFlTe_1d_avg",
                        "ST4_L1R_SIN_1MainChiFlPI_1d_avg",
                        "ST4_L1R_SIN_2MainChiFlTe_1d_avg",
                        "ST4_L1R_SIN_2MainChiFlPI_1d_avg",
                        "ST4_L1R_SIN_1stMixAddActFl_1d_avg",
                        "ST4_L1R_SIN_2ndMixAddActFl_1d_avg"
                };
    }

    /**
     * 通过tag点获取数据
     *
     * @param tagNames
     * @param date
     * @param version
     * @return
     */
    private JSONObject getDataByTag(String[] tagNames, Date date, String version) {
        String apiPath = "/tagValues/tagNames";
        DateQuery dateQuery = DateQueryUtil.buildToday(date);
        JSONObject query = new JSONObject();
        query.put("start", dateQuery.getQueryStartTime());
        query.put("end", dateQuery.getQueryEndTime());
        query.put("tagNames", tagNames);
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String results = httpUtil.postJsonParams(getUrl(version) + apiPath, jsonString);
        JSONObject jsonObject = JSONObject.parseObject(results);
        JSONObject data = jsonObject.getJSONObject("data");

        return data;
    }

    /**
     * 4号烧结，默认返回4号高炉
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
        return httpProperties.getUrlApiSJThree();
    }

}
