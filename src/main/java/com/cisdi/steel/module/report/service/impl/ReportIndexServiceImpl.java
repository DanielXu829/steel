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
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import com.cisdi.steel.module.report.query.ReportIndexQuery;
import com.cisdi.steel.module.report.service.ReportIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
            wrapper.select(ReportIndex::getPath);
            wrapper.eq(ReportIndex::getId, record.getId());
            ReportIndex one = this.getOne(wrapper);

            if (!one.getPath().equals(record.getPath())) {
                String fileExtension = FileUtils.getFileExtension(file.getName());
                String templatePath = jobProperties.getFilePath();
                String fileName = one.getName() + "." + fileExtension;

                String savePath = FileUtils.getSaveFilePathNoFilePath(templatePath, fileName, record.getIndexLang());

                FileUtils.copyFile(record.getPath(), savePath);
                file.delete();

                record.setPath(null);
            }
        }
        return super.updateRecord(record);
    }

    @Override
    public ApiResult pageList(ReportIndexQuery query) {
        Page<ReportIndex> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        LambdaQueryWrapper<ReportIndex> wrapper = new QueryWrapper<ReportIndex>().lambda();
        wrapper.eq(StringUtils.isNotBlank(query.getReportCategoryCode()), ReportIndex::getReportCategoryCode, query.getReportCategoryCode());
        wrapper.eq(StringUtils.isNotBlank(query.getIndexType()), ReportIndex::getIndexType, query.getIndexType());
        wrapper.eq(StringUtils.isNotBlank(query.getIndexLang()), ReportIndex::getIndexLang, query.getIndexLang());
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
        this.page(page, wrapper);
        return ApiUtil.successPage(page.getTotal(), page.getRecords());
    }

    @Override
    public void insertReportRecord(ReportIndex reportIndex) {
        if (StringUtils.isBlank(reportIndex.getReportCategoryCode())) {
            return;
        }
        Date now = new Date();
        reportIndex.setCreateTime(now);
        reportIndex.setUpdateTime(now);
        this.save(reportIndex);
    }


    @Override
    @Transactional(rollbackFor = LeafException.class)
    public void insertReportRecord(String code, String resultPath, String category,String indexType,String indexLang) {
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
}
