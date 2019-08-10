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
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.enums.LanguageEnum;
import com.cisdi.steel.module.report.mapper.ReportCategoryTemplateMapper;
import com.cisdi.steel.module.report.query.ReportCategoryTemplateQuery;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
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
public class ReportCategoryTemplateServiceImpl extends BaseServiceImpl<ReportCategoryTemplateMapper, ReportCategoryTemplate> implements ReportCategoryTemplateService {


    @Autowired
    private JobProperties jobProperties;

    @Autowired
    private QuartzMapper quartzMapper;

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
            || "焦化67".equals(query.getSequence()))){
            list = list.stream().sorted((a,b) ->Integer.valueOf(a.getAttr2()) - Integer.valueOf(b.getAttr2())).collect(Collectors.toList());
        }
        list.forEach(reportCategoryTemplate -> {
            QuartzEntity quartzEntity = quartzMapper.selectQuartzByCode(reportCategoryTemplate.getReportCategoryCode());
            reportCategoryTemplate.setQuartzEntity(quartzEntity);
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
        //ReportPathDTO reportPathDTO = reportCategoryService.selectReportInfoByCode(record.getReportCategoryCode());

        File file = new File(record.getTemplatePath());

        String fileExtension = FileUtils.getFileExtension(file.getName());
        String templatePath = jobProperties.getTemplatePath();
        String date = DateUtil.getFormatDateTime(new Date(), DateUtil.NO_SEPARATOR);
        String fileName = record.getTemplateName() + date + "." + fileExtension;

        String savePath = FileUtils.getSaveFilePathNoFilePath(templatePath, fileName, record.getTemplateLang());
        // 保存文件
        FileUtils.copyFile(record.getTemplatePath(), savePath);
        file.delete();

        record.setTemplatePath(savePath);
        this.save(record);
        return ApiUtil.success();
    }

    @Override
    public ApiResult updateRecord(ReportCategoryTemplate record) {
        File file = new File(record.getTemplatePath());
        if (file.exists()) {
            LambdaQueryWrapper<ReportCategoryTemplate> wrapper = new QueryWrapper<ReportCategoryTemplate>().lambda();
            wrapper.select(ReportCategoryTemplate::getTemplatePath);
            wrapper.eq(ReportCategoryTemplate::getId, record.getId());
            ReportCategoryTemplate one = this.getOne(wrapper);

            if (!one.getTemplatePath().equals(record.getTemplatePath())) {
                String fileExtension = FileUtils.getFileExtension(file.getName());
                String templatePath = jobProperties.getTemplatePath();
                String date = DateUtil.getFormatDateTime(new Date(), DateUtil.NO_SEPARATOR);
                String fileName = one.getTemplateName() + date + "." + fileExtension;

                String savePath = FileUtils.getSaveFilePathNoFilePath(templatePath, fileName, record.getTemplateLang());

                FileUtils.copyFile(record.getTemplatePath(), savePath);
                file.delete();

                record.setTemplatePath(savePath);
            }
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
                .setCron(entity.getCronExpression());
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
