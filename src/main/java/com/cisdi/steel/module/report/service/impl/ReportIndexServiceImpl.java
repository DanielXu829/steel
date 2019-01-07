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
import com.cisdi.steel.module.report.mapper.ReportCategoryTemplateMapper;
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
        String path = record.getPath();
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
                path = savePath;
                file.delete();

                record.setPath(savePath);
            }
        } else {
            record.setPath(null);
        }
        //部分报表修改时将模板同时修改
        if (JobEnum.nj_yasuokongqi.equals(record.getReportCategoryCode())
                || JobEnum.nj_sansigui_day.equals((record.getReportCategoryCode()))
                || JobEnum.nj_meiqihunhemei.equals((record.getReportCategoryCode()))
                || JobEnum.nj_guifengjimeiyaji.equals((record.getReportCategoryCode()))
        ) {
            FileUtils.copyFile(path, jobProperties.getTemplatePath());
        }
        return super.updateRecord(record);
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
                || JobEnum.gl_peiliaodan.getCode().equals(reportIndex.getReportCategoryCode())) {
            reportIndex.setCreateTime(now);
            this.save(reportIndex);
        } else {
            if (!reportIndex.getPath().equals(report.getPath())) {
                FileUtils.deleteFile(report.getPath());
            }
            reportIndex.setId(report.getId());
            this.updateById(reportIndex);
        }
//        if (StringUtils.isBlank(reportIndex.getReportCategoryCode())) {
//            return;
//        }
//        Date now = new Date();
//        reportIndex.setCreateTime(Objects.isNull(reportIndex.getCurrDate()) ? now : reportIndex.getCurrDate());
//        reportIndex.setUpdateTime(now);
//        reportIndex.setHidden("0");
//
//        if (StringUtils.isNotBlank(reportIndex.getReportCategoryCode()) && !"jh_zidongpeimei".equals(reportIndex.getReportCategoryCode())) {
//            reportIndexMapper.updateByMoreParamter(reportIndex);
//        }
//        this.save(reportIndex);
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
}
