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
import com.cisdi.steel.module.sys.mapper.SysConfigMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Calendar;
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

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public ApiResult upload(MultipartFile file) {
        String savePath = StringUtils.EMPTY;
        if (Objects.nonNull(file)) {
            String templatePath = jobProperties.getTempPath();
            String fileNames = file.getOriginalFilename();
            savePath = templatePath + File.separator + fileNames;

            // 如果有相同路径的文件存在，直接删除原文件
            File oldFile = new File(savePath);
            if (oldFile.exists()) {
                oldFile.delete();
            }

            // 保存文件
            FileUtils.saveFileToDisk(file, savePath);
        }
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
        ReportIndex report;
        ReportTemplateTypeEnum reportTypeEnum = ReportTemplateTypeEnum.getType(reportIndex.getIndexType());
        // 月报表、周报表、年报表特殊处理
        switch (reportTypeEnum) {
            case report_month:
                report = getCurrentMonthReport(reportIndex);
                break;
            case report_week:
                report = getCurrentWeekReport(reportIndex);
                break;
            case report_year:
                report = getCurrentYearReport(reportIndex);
                break;
            default:
                report = reportIndexMapper.selectIdByParamter(reportIndex);
                break;
        }

        if (Objects.isNull(report)
                || (Objects.isNull(reportIndex.getId()) && JobEnum.jh_zidongpeimei.getCode().equals(reportIndex.getReportCategoryCode()))
                || (Objects.isNull(reportIndex.getId()) && JobEnum.jh_ck12zidongpeimeinew.getCode().equals(reportIndex.getReportCategoryCode()))
                || (Objects.isNull(reportIndex.getId()) && JobEnum.jh_ck45zidongpeimei.getCode().equals(reportIndex.getReportCategoryCode()))
                || (Objects.isNull(reportIndex.getId()) && JobEnum.gl_peiliaodan.getCode().equals(reportIndex.getReportCategoryCode()))
                || (Objects.isNull(reportIndex.getId()) && JobEnum.gl_peiliaodan7.getCode().equals(reportIndex.getReportCategoryCode()))
                || (Objects.isNull(reportIndex.getId()) && JobEnum.gl_peiliaodan6.getCode().equals(reportIndex.getReportCategoryCode()))) {
            reportIndex.setCreateTime(now);
            this.save(reportIndex);
        } else {
            if (JobEnum.sj_liushaogycanshu.getCode().equals(reportIndex.getReportCategoryCode())
                    || JobEnum.sj_gycanshutotal.getCode().equals(reportIndex.getReportCategoryCode())
            ) {
                boolean f = dealGongyi(report.getRecordDate(), reportIndex.getRecordDate());
                otherHand(f, reportIndex, report, now);
            } else if (JobEnum.jh_zhuyaogycs.getCode().equals(reportIndex.getReportCategoryCode())
                    || JobEnum.ygl_liaochangzuoyequ.getCode().equals(reportIndex.getReportCategoryCode())
                    || JobEnum.jh_zhibiaoguankong.getCode().equals(reportIndex.getReportCategoryCode())
            ) {
                boolean f = dealZhibiao(report.getRecordDate(), reportIndex.getRecordDate());
                otherHand(f, reportIndex, report, now);
            } else {
                if (!reportIndex.getPath().equals(report.getPath())) {
                    // 新报表名称变了，删除原始文件
                    FileUtils.deleteFile(report.getPath());
                }
                if (Objects.nonNull(reportIndex.getId())) {
                    reportIndex.setName(null);
                }
                reportIndex.setId(report.getId());
                this.updateById(reportIndex);
            }
        }
    }

    /**
     * 获取本年的报表
     * @param reportIndex
     * @return
     */
    private ReportIndex getCurrentYearReport(ReportIndex reportIndex) {
        Date currDate = reportIndex.getCurrDate();
        int dayOfYear = DateUtil.getDayOfYear(currDate);
        // CurrentDate是1号，则查找(创建时间在去年号到这个月1号之间)的报表
        if (dayOfYear == 1) { // 1月1号
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(currDate);
            calendar1.add(Calendar.YEAR, -1); // 去年1月1号
            calendar1.set(Calendar.DAY_OF_MONTH, 2); // 去年1月2号
            Date beginTime = calendar1.getTime();
            beginTime = DateUtil.getDateBeginTime(beginTime);

            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(currDate);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            Date endTime = calendar2.getTime();
            endTime = DateUtil.getDateEndTime59(endTime); // 今年1月1号23.59

            List<ReportIndex> reportList = reportIndexMapper.queryReport(reportIndex.getReportCategoryCode(), beginTime.getTime()/1000, endTime.getTime()/1000);
            if (CollectionUtils.isNotEmpty(reportList)) {
                return reportList.get(0);
            }
        } else if (dayOfYear == 2) {
            // currDate 是1月2号，则查找今天新生成的报表(为了排除1号，所以这样写)
            Date dateBeginTime = DateUtil.getDateBeginTime(currDate);
            Date dateEndTime59 = DateUtil.getDateEndTime59(currDate);
            List<ReportIndex> reportList = reportIndexMapper.queryReport(reportIndex.getReportCategoryCode(), dateBeginTime.getTime()/1000, dateEndTime59.getTime()/1000);
            if (CollectionUtils.isNotEmpty(reportList)) {
                return reportList.get(0);
            }
        } else {
            return reportIndexMapper.selectIdByParamter(reportIndex);
        }
        return null;
    }

    /**
     * description 因为每天的数据是在第二天运行，所以每月1号实际运行出来是上个月最后一天
     * 获取本月的月报表
     * @param reportIndex
     * @return
     */
    private ReportIndex getCurrentMonthReport(ReportIndex reportIndex) {
        Date currDate = reportIndex.getCurrDate();
        int dayOfMonth = DateUtil.getDayOfMonth(currDate);
        // CurrentDate是1号，则查找(创建时间在上个月2号到这个月1号之间)的报表
        if (dayOfMonth == 1) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(currDate);
            calendar1.add(Calendar.MONTH, -1);
            calendar1.set(Calendar.DAY_OF_MONTH, 2);
            Date beginTime = calendar1.getTime();
            beginTime = DateUtil.getDateBeginTime(beginTime);

            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(currDate);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            Date endTime = calendar2.getTime();
            endTime = DateUtil.getDateEndTime59(endTime);

            List<ReportIndex> reportList = reportIndexMapper.queryReport(reportIndex.getReportCategoryCode(), beginTime.getTime()/1000, endTime.getTime()/1000);
            if (CollectionUtils.isNotEmpty(reportList)) {
                return reportList.get(0);
            }
        } else if (dayOfMonth == 2) {
            // recordDate 是2号，则查找今天新生成的报表(为了排除1号，所以这样写)
            Date dateBeginTime = DateUtil.getDateBeginTime(currDate);
            Date dateEndTime59 = DateUtil.getDateEndTime59(currDate);
            List<ReportIndex> reportList = reportIndexMapper.queryReport(reportIndex.getReportCategoryCode(), dateBeginTime.getTime()/1000, dateEndTime59.getTime()/1000);
            if (CollectionUtils.isNotEmpty(reportList)) {
                return reportList.get(0);
            }
        } else {
            return reportIndexMapper.selectIdByParamter(reportIndex);
        }
        return null;
    }

    /**
     * description 因为每天的数据是在第二天运行，所以每周星期一实际运行出来是上周的报表
     * 获取本周的周报表
     * @param reportIndex
     * @return
     */
    private ReportIndex getCurrentWeekReport(ReportIndex reportIndex) {
        Date currDate = reportIndex.getCurrDate();
        int dayOfWeek = DateUtil.getDayOfWeekDay(currDate);
        if (dayOfWeek == 1) {
            // 如果是星期一 则获取上周的周报（周二到周一生成的）
            Date lastWeekTuesdayBeginTime = DateUtil.getDateBeginTime(DateUtil.addDays(currDate, -6));
            Date currentDateEndTime59 = DateUtil.getDateEndTime59(currDate);
            List<ReportIndex> reportList = reportIndexMapper.queryReport(reportIndex.getReportCategoryCode(), lastWeekTuesdayBeginTime.getTime()/1000, currentDateEndTime59.getTime()/1000);
            if (CollectionUtils.isNotEmpty(reportList)) {
                return reportList.get(0);
            }
        } else {
            // 如果不是星期一 获取本周二到周日的日报
            Date thisWeekBeginTime = DateUtil.getWeekBeginTime(currDate);
            Date thisWeekTuesdayBeginTime = DateUtil.getDateBeginTime(DateUtil.addDays(thisWeekBeginTime, 1));
            Date thisWeekSundayEndTime59 = DateUtil.getDateEndTime59(DateUtil.addDays(thisWeekBeginTime, 6));
            List<ReportIndex> reportList = reportIndexMapper.queryReport(reportIndex.getReportCategoryCode(), thisWeekTuesdayBeginTime.getTime()/1000, thisWeekSundayEndTime59.getTime()/1000);
            if (CollectionUtils.isNotEmpty(reportList)) {
                return reportList.get(0);
            }
        }
        return null;
    }

    private void otherHand(boolean f, ReportIndex reportIndex, ReportIndex report, Date date) {
        if (!f) {
            reportIndex.setCreateTime(date);
            this.save(reportIndex);
        } else {
            if (!reportIndex.getPath().equals(report.getPath())) {
                FileUtils.deleteFile(report.getPath());
            }
            if (Objects.nonNull(reportIndex.getId())) {
                reportIndex.setName(null);
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
        ReportIndex report;
        ReportTemplateTypeEnum reportTypeEnum = ReportTemplateTypeEnum.getType(reportIndex.getIndexType());
        switch (reportTypeEnum) {
            case report_month:
                report = getCurrentMonthReport(reportIndex);
                break;
            case report_week:
                report = getCurrentWeekReport(reportIndex);
                break;
            case report_year:
                report = getCurrentYearReport(reportIndex);
                break;
            default:
                report = reportIndexMapper.selectIdByParamter(reportIndex);
                break;
        }
        // 判断数据库是否存在报表
        if (Objects.isNull(report) || Objects.isNull(report.getPath())
                || JobEnum.jh_zidongpeimei.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.jh_ck12zidongpeimeinew.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.jh_ck45zidongpeimei.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.gl_peiliaodan.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.gl_peiliaodan6.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.gl_peiliaodan7.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.sj_liushaogycanshu.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.sj_gycanshutotal.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.jh_luwenguankong.getCode().equals(reportIndex.getReportCategoryCode())
                || JobEnum.gl_ludingzhuangliaozuoye_day1.getCode().equals(reportIndex.getReportCategoryCode())
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

    @Override
    public ReportIndex getReportIndexInfo(String code, String sequence, int editStatus) {
        LambdaQueryWrapper<ReportIndex> wrapper = new QueryWrapper<ReportIndex>().lambda();
        wrapper.eq(ReportIndex::getReportCategoryCode, code);
        wrapper.eq(StringUtils.isNotBlank(sequence), ReportIndex::getSequence, sequence);
        wrapper.eq(ReportIndex::getEditStatus, editStatus);
        wrapper.orderByDesc(ReportIndex::getCreateTime);
        wrapper.last("limit 1");

        return this.getOne(wrapper);
    }

}
