package com.cisdi.steel.module.job.sj.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.dto.response.sj.AnaValueDTO;
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
import java.math.BigDecimal;
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
        List list = new ArrayList();
        Arrays.asList("ore_blending", "coke", "quicklime", "limestone", "dolomite").stream().forEach(type -> {
            AnaValueDTO anaValueDTO = getYuanLiaoZhiLiang(date, type);
            if (Objects.nonNull(anaValueDTO) && CollectionUtils.isNotEmpty(anaValueDTO.getData())) {
                Map<String, BigDecimal> values = anaValueDTO.getData().get(0).getValues();
                list.add(values);
            }
        });

        result.put("yuanliaozhiliang", list);

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

    private AnaValueDTO getYuanLiaoZhiLiang(Date date, String materialType) {
        String apiPath = "/burdenMatAnalysisVal?pageSize=1&pageNum=1";
        DateQuery dateQuery = DateQueryUtil.buildToday(date);
        JSONObject query = new JSONObject();
        query.put("start", dateQuery.getStartTime());
        query.put("end", dateQuery.getEndTime());
        query.put("materialType", materialType);
        String results = httpUtil.postJsonParams(getUrl(version) + apiPath, JSONObject.toJSONString(query));

        JSONObject jsonObject = JSONObject.parseObject(results);
        JSONObject data = jsonObject.getJSONObject("data");

        return null;
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
