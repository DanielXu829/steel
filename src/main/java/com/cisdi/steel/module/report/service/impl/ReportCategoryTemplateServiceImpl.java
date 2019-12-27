package com.cisdi.steel.module.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cisdi.steel.common.base.service.impl.BaseServiceImpl;
import com.cisdi.steel.common.constant.Constants;
import com.cisdi.steel.common.exception.BusinessException;
import com.cisdi.steel.common.exception.LeafException;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.quartz.entity.QuartzEntity;
import com.cisdi.steel.module.quartz.mapper.QuartzMapper;
import com.cisdi.steel.module.report.entity.ReportCategory;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.enums.LanguageEnum;
import com.cisdi.steel.module.report.mapper.ReportCategoryMapper;
import com.cisdi.steel.module.report.mapper.ReportCategoryTemplateMapper;
import com.cisdi.steel.module.report.query.ReportCategoryTemplateQuery;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import com.cisdi.steel.module.sys.entity.SysConfig;
import com.cisdi.steel.module.sys.mapper.SysConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>Description: 分类模板配置 服务实现类 </p>
 * <P>Date: 2018-10-23 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Service
@Slf4j
public class ReportCategoryTemplateServiceImpl extends BaseServiceImpl<ReportCategoryTemplateMapper, ReportCategoryTemplate> implements ReportCategoryTemplateService {


    @Autowired
    private JobProperties jobProperties;

    @Autowired
    private QuartzMapper quartzMapper;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private ReportCategoryMapper reportCategoryMapper;

    @Autowired
    private ReportCategoryTemplateMapper reportCategoryTemplateMapper;
    @Override
    public ApiResult pageList(ReportCategoryTemplateQuery query) {
        LambdaQueryWrapper<ReportCategoryTemplate> wrapper = new QueryWrapper<ReportCategoryTemplate>().lambda();
        wrapper.eq(StringUtils.isNotBlank(query.getReportCategoryCode()), ReportCategoryTemplate::getReportCategoryCode, query.getReportCategoryCode());
        wrapper.eq(StringUtils.isNotBlank(query.getForbid()), ReportCategoryTemplate::getForbid, query.getForbid());
        if (StringUtils.isNotBlank(query.getSequence()) && query.getSequence().contains("烧结")) {
            wrapper.like(StringUtils.isNotBlank(query.getSequence()), ReportCategoryTemplate::getSequence, query.getSequence());
        } else {
            wrapper.eq(StringUtils.isNotBlank(query.getSequence()), ReportCategoryTemplate::getSequence, query.getSequence());
        }
        List<ReportCategoryTemplate> list = list(wrapper);
        if(StringUtils.isNotBlank(query.getSequence()) && ("焦化12".equals(query.getSequence()) || "焦化45".equals(query.getSequence())
            || "焦化".equals(query.getSequence()))){
            list = list.stream().sorted((a,b) ->Integer.valueOf(a.getAttr2()) - Integer.valueOf(b.getAttr2())).collect(Collectors.toList());
        }
        list.forEach(reportCategoryTemplate -> {
            QuartzEntity quartzEntity = quartzMapper.selectQuartzByCode(reportCategoryTemplate.getReportCategoryCode());
            reportCategoryTemplate.setQuartzEntity(quartzEntity);

            // 获取categoryParentId，categoryName
            String code = reportCategoryTemplate.getReportCategoryCode();
            LambdaQueryWrapper<ReportCategory> reportWrapper = new QueryWrapper<ReportCategory>().lambda();
            reportWrapper.eq(ReportCategory::getCode, code);
            ReportCategory reportCategory = reportCategoryMapper.selectOne(reportWrapper);
            long categoryParentId = reportCategory.getParentId();
            ReportCategory parentReportCategory = reportCategoryMapper.selectById(categoryParentId);
            reportCategoryTemplate.setCategoryParentId(categoryParentId);
            reportCategoryTemplate.setCategoryName(parentReportCategory.getName());
        });
        return ApiUtil.success(list);
    }

    @Override
    public List<ReportCategoryTemplate> selectTemplateInfo(String code, String lang, String sequence) {
        if (StringUtils.isBlank(code)) {
            throw new BusinessException("生成模板编码不能为空");
        }
        LambdaQueryWrapper<ReportCategoryTemplate> wrapper = new QueryWrapper<ReportCategoryTemplate>().lambda();
//        wrapper.select(
//                ReportCategoryTemplate::getReportCategoryCode,
//                ReportCategoryTemplate::getTemplateName,
//                ReportCategoryTemplate::getTemplatePath,
//                ReportCategoryTemplate::getTemplateLang,
//                ReportCategoryTemplate::getTemplateType,
//                ReportCategoryTemplate::getSequence,
//                ReportCategoryTemplate::getExcelPath
//        );
        wrapper.eq(ReportCategoryTemplate::getReportCategoryCode, code);
        wrapper.eq(StringUtils.isNotBlank(lang), ReportCategoryTemplate::getTemplateLang, lang);
        wrapper.eq(StringUtils.isNotBlank(sequence), ReportCategoryTemplate::getSequence, sequence);
        // 查询生效的数据
        wrapper.eq(ReportCategoryTemplate::getForbid, Constants.NO);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = LeafException.class)
    public ApiResult insertRecord(ReportCategoryTemplate record) {
        if (Objects.nonNull(record)) {
            File file = new File(record.getTemplatePath());
            String fileExtension = FileUtils.getFileExtension(file.getName());

            // 通过CategoryParentId查询report_category对象,获取categoryName
            ReportCategory reportCategory = reportCategoryMapper.selectById(record.getCategoryParentId());
            String categoryName = reportCategory.getName();

            // 获取templatePath
            String templatePath = jobProperties.getTemplatePath();
            templatePath = templatePath + File.separator + record.getSequence() + File.separator + categoryName;

            // 组装文件名
            String fileName = record.getTemplateName() + "." + fileExtension;
            String savePath = FileUtils.getSaveFilePathNoFilePath(templatePath, fileName, record.getTemplateLang());

            // 如果有相同路径文件存在，需先删除此文件
            File oldFile = new File(savePath);
            if (oldFile.exists()) {
                oldFile.delete();
            }

            // 保存文件
            FileUtils.copyFile(record.getTemplatePath(), savePath);
            file.delete();
            record.setTemplatePath(savePath);

            //先通过CategoryCode查询category
            LambdaQueryWrapper<ReportCategory> categoryWrapper = new QueryWrapper<ReportCategory>().lambda();
            categoryWrapper.eq(ReportCategory::getCode, record.getReportCategoryCode());
            ReportCategory oldReportCategory = reportCategoryMapper.selectOne(categoryWrapper);
            if (Objects.nonNull(oldReportCategory)) {
                oldReportCategory.setParentId(record.getCategoryParentId());
                oldReportCategory.setName(record.getTemplateName());
                oldReportCategory.setRemark(record.getSequence());
                oldReportCategory.setLeafNode("1");
                oldReportCategory.setDelFlag("0");
                reportCategoryMapper.updateById(oldReportCategory);
            } else {
                // 向report_category表插入一条数据，做为当前模板的Category
                ReportCategory newReportCategory = new ReportCategory();
                newReportCategory.setParentId(record.getCategoryParentId());
                newReportCategory.setName(record.getTemplateName());
                newReportCategory.setCode(record.getReportCategoryCode());
                newReportCategory.setRemark(record.getSequence());
                newReportCategory.setLeafNode("1");
                newReportCategory.setDelFlag("0");
                reportCategoryMapper.insert(newReportCategory);
            }

            this.save(record);
        }

        return ApiUtil.success();
    }

    @Override
    public ApiResult updateRecord(ReportCategoryTemplate record) {
        File file = new File(record.getTemplatePath());
        if (file.exists()) {
            // 修改模板对应的category
            long categoryParentId = record.getCategoryParentId();
            LambdaQueryWrapper<ReportCategory> categoryWrapper = new QueryWrapper<ReportCategory>().lambda();
            categoryWrapper.eq(ReportCategory::getCode, record.getReportCategoryCode());
            ReportCategory reportCategory = reportCategoryMapper.selectOne(categoryWrapper);
            reportCategory.setParentId(categoryParentId);
            reportCategoryMapper.updateById(reportCategory);

            String fileExtension = FileUtils.getFileExtension(file.getName());
            ReportCategory parentIdReportCategory = reportCategoryMapper.selectById(categoryParentId);
            String categoryName = parentIdReportCategory.getName();

            // 获取templatePath
            String templatePath = jobProperties.getTemplatePath();
            templatePath = templatePath + File.separator + record.getSequence() + File.separator + categoryName;

            // 组装文件名
            String fileName = record.getTemplateName() + "." + fileExtension;
            String savePath = FileUtils.getSaveFilePathNoFilePath(templatePath, fileName, record.getTemplateLang());

            // 如果有相同路径文件存在，需先删除此文件
            File oldFile = new File(savePath);
            if (oldFile.exists()) {
                oldFile.delete();
            }

            // 保存文件
            FileUtils.copyFile(record.getTemplatePath(), savePath);
            file.delete();

            record.setTemplatePath(savePath);
        } else {
            log.error("临时目录中不存在该文件：" + record.getTemplatePath());
        }
        this.updateById(record);
        return ApiUtil.success();
    }

    @Override
    public void updateTemplateTask(QuartzEntity entity) {
        if (Objects.isNull(entity.getId())) {
            return;
        }
        ReportCategoryTemplate reportCategoryTemplate = new ReportCategoryTemplate();
        reportCategoryTemplate.setId(entity.getId())
                .setBuild(entity.getBuild())
                .setBuildUnit(entity.getBuildUnit())
                .setBuildDelay(entity.getBuildDelay())
                .setBuildDelayUnit(entity.getBuildDelayUnit())
                .setCron(entity.getCronExpression())
                .setMakeupInterval(entity.getMakeupInterval())
                .setCronSettingMethod(entity.getCronSettingMethod())
                .setCronJsonString(entity.getCronJsonString());
        this.updateById(reportCategoryTemplate);
    }

    /**
     * 获取处理后文件保存的路径
     *
     * @param path     文件目录
     * @param fileName 文件名
     * @param lang     所属语言
     * @return 文件路径
     */
    private String getSaveFilePath(String path, String fileName, String lang) {
        LanguageEnum langEnum = LanguageEnum.getByLang(lang);
        String parentPath = jobProperties.getFilePath() + path;
        if (StringUtils.isNotBlank(langEnum.getName())) {
            parentPath = jobProperties.getFilePath() + File.separator + langEnum.getName() + path;
        }
        File saveFile = new File(parentPath);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        return parentPath + File.separator + fileName;
    }
}
