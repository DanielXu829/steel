package com.cisdi.steel.module.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cisdi.steel.common.base.service.impl.BaseServiceImpl;
import com.cisdi.steel.common.exception.LeafException;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.report.dto.ReportIndexDTO;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.enums.ReportTemplateTypeEnum;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import com.cisdi.steel.module.report.query.ReportIndexQuery;
import com.cisdi.steel.module.report.service.ReportIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>Description: 报表文件-索引 服务实现类 </p>
 * <P>Date: 2018-10-24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Service
public class ReportIndexServiceImpl extends BaseServiceImpl<ReportIndexMapper, ReportIndex> implements ReportIndexService {

    @Autowired
    private JobProperties jobProperties;

    @Autowired
    private ReportIndexMapper reportIndexMapper;

    @Override
    public ApiResult upload(MultipartFile file) {
        String fileExtension = FileUtils.getFileExtension(file.getOriginalFilename());
        String templatePath = jobProperties.getTempPath();
        String date = DateUtil.getFormatDateTime(new Date(), DateUtil.NO_SEPARATOR);
        String fileName = "demo" + date + "." + fileExtension;

        String savePath = templatePath + File.separator + fileName;
        // 保存文件
        FileUtils.saveFileToDisk(file, savePath);
        return ApiUtil.success(savePath);
    }

    @Override
    public ApiResult updateRecord(ReportIndex record) {
        File file = new File(record.getPath());
        if (Objects.nonNull(file) && file.exists()) {
            LambdaQueryWrapper<ReportIndex> wrapper = new QueryWrapper<ReportIndex>().lambda();
            wrapper.select(ReportIndex::getPath, ReportIndex::getName);
            wrapper.eq(ReportIndex::getId, record.getId());
            ReportIndex one = this.getOne(wrapper);

            if (!one.getPath().equals(record.getPath())) {
                String fileExtension = FileUtils.getFileExtension(file.getName());
                String templatePath = jobProperties.getFilePath();
                String fileName = record.getSequence() + File.separator +
                        ReportTemplateTypeEnum.getType(record.getIndexType()).getName() + File.separator +
                        FileUtils.getFileNameWithoutExtension(one.getName());
                String savePath = FileUtils.getSaveFilePathNoFilePath(templatePath, fileName, record.getIndexLang()) + "." + fileExtension;

                FileUtils.copyFile(record.getPath(), savePath);
                file.delete();

                record.setPath(savePath);
            }
        } else {
            record.setPath(null);
        }
        return super.updateRecord(record);
    }

    @Override
    public ReportIndex queryByPath(String path) {
        LambdaQueryWrapper<ReportIndex> wrapper = new QueryWrapper<ReportIndex>().lambda();
        wrapper.eq(true, ReportIndex::getHidden, "0");
        wrapper.eq(ReportIndex::getPath, path);
        return reportIndexMapper.selectOne(wrapper);
    }

    @Override
    public ApiResult pageList(ReportIndexQuery query) {
        Page<ReportIndex> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        LambdaQueryWrapper<ReportIndex> wrapper = new QueryWrapper<ReportIndex>().lambda();
        wrapper.eq(true, ReportIndex::getHidden, "0");
        wrapper.eq(StringUtils.isNotBlank(query.getReportCategoryCode()), ReportIndex::getReportCategoryCode, query.getReportCategoryCode());
        wrapper.eq(StringUtils.isNotBlank(query.getIndexType()), ReportIndex::getIndexType, query.getIndexType());
        wrapper.eq(StringUtils.isNotBlank(query.getIndexLang()), ReportIndex::getIndexLang, query.getIndexLang());

        if (StringUtils.isNotBlank(query.getSequence()) && query.getSequence().contains("烧结")) {
            wrapper.like(StringUtils.isNotBlank(query.getSequence()), ReportIndex::getSequence, query.getSequence());
        } else {
            if (StringUtils.isNotBlank(query.getReportCategoryCode())
                    && (query.getReportCategoryCode().startsWith("hb_") || query.getReportCategoryCode().startsWith("gl_gaolupenmei"))) {
                query.setSequence(null);
            }
            wrapper.eq(StringUtils.isNotBlank(query.getSequence()), ReportIndex::getSequence, query.getSequence());
        }

        wrapper.likeRight(StringUtils.isNotBlank(query.getName()), ReportIndex::getName, query.getName());
        if (Objects.nonNull(query.getStartTime())) {
            Date dateBeginTime = DateUtil.getDateBeginTime(query.getStartTime());
            Date dateEndTime;
            if (Objects.isNull(query.getEndTime())) {
                dateEndTime = DateUtil.getDateEndTime(new Date());
            } else {
                dateEndTime = DateUtil.getDateEndTime(query.getEndTime());
            }
            wrapper.between(ReportIndex::getCreateTime, dateBeginTime, dateEndTime);
        }
        wrapper.orderBy(true, false, ReportIndex::getCreateTime);
        this.page(page, wrapper);
        return ApiUtil.successPage(page.getTotal(), page.getRecords());
    }

    @Override
    public void insertReportRecord(ReportIndex reportIndex) {
        if (StringUtils.isBlank(reportIndex.getReportCategoryCode())) {
            return;
        }
        Date now = new Date();
        reportIndex.setUpdateTime(now);
        reportIndex.setHidden("0");
        ReportIndex report = reportIndexMapper.selectIdByParamter(reportIndex);

        if (Objects.isNull(report)
                || JobEnum.jh_zidongpeimei.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.gl_peiliaodan.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.gl_peiliaodan7.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.gl_peiliaodan6.getCode().equals(reportIndex.getReportCategoryCode())) {
            reportIndex.setCreateTime(now);
            this.save(reportIndex);
        } else {
            if (JobEnum.sj_liushaogycanshu.getCode().equals(reportIndex.getReportCategoryCode())
                    || JobEnum.sj_gycanshutotal.getCode().equals(reportIndex.getReportCategoryCode())
            ) {
                boolean f = dealGongyi(report.getRecordDate(), reportIndex.getRecordDate());
                otherHand(f, reportIndex, report, now);
            } else if (JobEnum.jh_zhibiaoguankong.getCode().equals(reportIndex.getReportCategoryCode()) || JobEnum.jh_zhuyaogycs.getCode().equals(reportIndex.getReportCategoryCode())) {
                boolean f = dealZhibiao(report.getRecordDate(), reportIndex.getRecordDate());
                otherHand(f, reportIndex, report, now);
            } else {
                if (!reportIndex.getPath().equals(report.getPath())) {
                    FileUtils.deleteFile(report.getPath());
                }
                reportIndex.setId(report.getId());
                this.updateById(reportIndex);
            }
        }
    }

    private void otherHand(boolean f, ReportIndex reportIndex, ReportIndex report, Date date) {
        if (!f) {
            reportIndex.setCreateTime(date);
            this.save(reportIndex);
        } else {
            if (!reportIndex.getPath().equals(report.getPath())) {
                FileUtils.deleteFile(report.getPath());
            }
            reportIndex.setId(report.getId());
            this.updateById(reportIndex);
        }
    }

    private boolean dealZhibiao(Date date, Date date1) {
        boolean flag = false;
        try {
            int dateTime = Integer.valueOf(DateUtil.getFormatDateTime(date, "HH"));
            int dateTime1 = Integer.valueOf(DateUtil.getFormatDateTime(date1, "HH"));
            if (((dateTime >= 0 && dateTime < 8))
                    && ((dateTime1 >= 0 && dateTime1 < 8))) {
                flag = true;
            } else if ((dateTime < 16 && dateTime >= 8)
                    && (dateTime1 < 16 && dateTime1 >= 8)) {
                flag = true;
            } else if ((dateTime < 24 && dateTime >= 16)
                    && (dateTime1 < 24 && dateTime1 >= 16)) {
                flag = true;
            }
        } catch (Exception e) {
        }

        return flag;
    }

    private boolean dealGongyi(Date date, Date date1) {
        boolean flag = false;
        try {
            int dateTime = Integer.valueOf(DateUtil.getFormatDateTime(date, "HH"));
            int dateTime1 = Integer.valueOf(DateUtil.getFormatDateTime(date1, "HH"));
            if (((dateTime >= 0 && dateTime < 3) && (dateTime1 >= 0 && dateTime1 < 3))
                    || (dateTime == 23 && dateTime1 == 23)) {
                flag = true;
            } else if ((dateTime < 7 && dateTime >= 3)
                    && (dateTime1 < 7 && dateTime1 >= 3)) {
                flag = true;
            } else if ((dateTime < 11 && dateTime >= 7)
                    && (dateTime1 < 11 && dateTime1 >= 7)) {
                flag = true;
            } else if ((dateTime < 15 && dateTime >= 11)
                    && (dateTime1 < 15 && dateTime1 >= 11)) {
                flag = true;
            } else if ((dateTime < 19 && dateTime >= 15)
                    && (dateTime1 < 19 && dateTime1 >= 15)) {
                flag = true;
            } else if ((dateTime < 23 && dateTime >= 19)
                    && (dateTime1 < 23 && dateTime1 >= 19)) {
                flag = true;
            }
        } catch (Exception e) {
        }

        return flag;
    }

    @Override
    @Transactional(rollbackFor = LeafException.class)
    public void insertReportRecord(String code, String resultPath, String category, String indexType, String indexLang) {
        if (StringUtils.isBlank(code)) {
            return;
        }
        Date now = new Date();
        ReportIndex reportIndex = new ReportIndex();
        reportIndex.setCreateTime(now);
        reportIndex.setUpdateTime(now);
        reportIndex.setReportCategoryCode(code);
        String fileName = FileUtil.getFileName(resultPath);
        reportIndex.setName(fileName);
        reportIndex.setPath(resultPath);
        reportIndex.setSequence(category);
        reportIndex.setIndexType(indexType);
        reportIndex.setIndexLang(indexLang);
        this.save(reportIndex);
    }


    @Override
    public ApiResult reportIndex(ReportIndexQuery reportIndexQuery) {
        Date today = new Date();
        //1.昨日日报
        Date yestarDay = DateUtil.addDays(today, -1);
        reportIndexQuery.setToDay(DateUtil.getFormatDateTime(yestarDay, DateUtil.yyyyMMddFormat));
        List<ReportIndex> yestarDayList = reportIndexMapper.queryReportToday(reportIndexQuery);
        //2.今日日报
        reportIndexQuery.setToDay(DateUtil.getFormatDateTime(today, DateUtil.yyyyMMddFormat));
        List<ReportIndex> todayList = reportIndexMapper.queryReportToday(reportIndexQuery);
        //3.本月月报
        reportIndexQuery.setToDay(DateUtil.getFormatDateTime(today, DateUtil.yyyyMMFormat));
        List<ReportIndex> monthList = reportIndexMapper.queryReportMonth(reportIndexQuery);
        //4.其他最新报表
        reportIndexQuery.setToDay(DateUtil.getFormatDateTime(today, DateUtil.yyyyMMddFormat));
        List<ReportIndex> otherList = reportIndexMapper.queryReportOther(reportIndexQuery);

        ReportIndexDTO reportIndexDTO = new ReportIndexDTO();
        reportIndexDTO.setYestarDayList(yestarDayList);
        reportIndexDTO.setTodayList(todayList);
        reportIndexDTO.setMonthList(monthList);
        reportIndexDTO.setOtherList(otherList);
        return ApiUtil.success(reportIndexDTO);
    }

    @Override
    public String existTemplate(ReportIndex reportIndex) {
        ReportIndex report = reportIndexMapper.selectIdByParamter(reportIndex);
        // 判断数据库是否存在报表
        if (Objects.isNull(report) || Objects.isNull(report.getPath())
                || JobEnum.jh_zidongpeimei.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.gl_peiliaodan.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.gl_peiliaodan6.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.gl_peiliaodan7.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.sj_liushaogycanshu.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.sj_gycanshutotal.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.jh_luwenguankong.getCode().equals(reportIndex.getReportCategoryCode())
        ) {
            // 不存在，直接返回null
            return null;
        } else {
            // 存在
            File file = new File(report.getPath());
            if (file.exists()) {
                return report.getPath();
            }
        }
        return null;
    }


    @Override
    public ReportIndex existTemplate1(ReportIndex reportIndex) {
        ReportIndex report = null;
        if (JobEnum.nj_qiguidianjianruihua_month.getCode().equals(reportIndex.getReportCategoryCode())) {
            report = reportIndexMapper.selectIdByParamter1(reportIndex);
        } else {
            report = reportIndexMapper.selectIdByParamter(reportIndex);
        }
        // 判断数据库是否存在报表
        if (Objects.isNull(report) || Objects.isNull(report.getPath())) {
            // 不存在，直接返回null
            return null;
        } else {
            // 存在
            File file = new File(report.getPath());
            if (file.exists()) {
                return report;
            }
        }
        return null;
    }

}
