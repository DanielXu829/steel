package com.cisdi.steel.module.job.sj.doc;

import cn.afterturn.easypoi.word.WordExportUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.enums.SequenceEnum;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@Component
@Slf4j
@SuppressWarnings("ALL")
public class ShaoJieFenXiDocMain {

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private HttpProperties httpProperties;

    @Autowired
    private JobProperties jobProperties;

    @Autowired
    private ReportIndexMapper reportIndexMapper;

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
        log.info("烧结word生成完毕！");
    }

    public void mainDeal(String version, Date date) {
        dealTagName();
        if (Objects.isNull(date)) {
            date = new Date();
        }
        dealPart1(version, L1, date);
        comm(version, jobProperties.getTemplatePath() + File.separator + "doc" + File.separator + "烧结生产分析模板.docx");
    }

    /**
     * 通过tag点获取得数据，组装到result
     * @param version
     * @param tagNames
     * @param date
     */
    private void dealPart1(String version, String[] tagNames, Date date) {
        dealTagName();
        JSONObject data = dataHttp(L1, DateUtil.addDays(date, -1), version);
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
     * 生产word文档
     * @param version
     * @param path
     */
    private void comm(String version, String path) {
        //文档所有日期
        dealDate(result);
        try {
            String sequence = "4烧结";
            XWPFDocument doc = WordExportUtil.exportWord07(path, result);
            String fileName = sequence + DateUtil.getFormatDateTime(new Date(), "yyyyMMdd") + "烧结生产分析.docx";
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
        } catch (Exception e) {
            log.error("生产烧结生产分析word文档失败", e);
        }
    }

    /**
     * 处理文档时间
     * @param map
     */
    private void dealDate(HashMap<String, Object> map) {
        Date date = new Date();
        String date1 = DateUtil.getFormatDateTime(date, "yyyy年MM月dd日 HH:mm");
        String date2 = DateUtil.getFormatDateTime(date, "yyyy年MM月dd日");

        //文档所有日期
        map.put("date1", date1);
        map.put("date2", date2);
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
     * @param tagNames
     * @param date
     * @param version
     * @return
     */
    private JSONObject dataHttp(String[] tagNames, Date date, String version) {
        DateQuery dateQuery = DateQueryUtil.buildToday(date);
        JSONObject query = new JSONObject();
        query.put("start", dateQuery.getQueryStartTime());
        query.put("end", dateQuery.getQueryEndTime());
        query.put("tagNames", tagNames);
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String results = httpUtil.postJsonParams(getUrl(version), jsonString);
        JSONObject jsonObject = JSONObject.parseObject(results);
        JSONObject data = jsonObject.getJSONObject("data");

        return data;
    }

    /**
     * 4号烧结，默认返回4号高炉
     * @param version
     * @return
     */
    private String getUrl(String version) {
        if ("4.0".equals(version)) {
            return httpProperties.getUrlApiSJThree() + "/tagValues/tagNames";
        } else {
            return httpProperties.getUrlApiSJThree() + "/tagValues/tagNames";
        }
    }
}
