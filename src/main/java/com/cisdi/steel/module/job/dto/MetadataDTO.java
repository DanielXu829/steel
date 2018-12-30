package com.cisdi.steel.module.job.dto;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.job.enums.JobExecuteEnum;
import com.cisdi.steel.module.report.enums.ReportTemplateTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * metadata 存放的数据
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/7 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class MetadataDTO {
    /**
     * 当前时间
     */
    private Date dateTime;
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 类型 日 班 月 年
     */
    private String type;

    /**
     * 模板存放路径
     */
    private String templatePath;
    /**
     * 自动构建 Enable Disable
     */
    private String autoBuild;

    /**
     * 时间
     */
    private Integer autoBuildDelay;
    /**
     * 单位
     */
    private String autoBuildDelayUnit;

    /**
     * 当前语言
     */
    private String language;

    /**
     * 创建日期 yyyy-MM-dd
     */
    private String blowingInDate;

    /**
     * 构建周期
     */
    private Integer build;
    /**
     * 周期单位
     */
    private String buildUnit;

    /**
     * 构建类型 自动或手动
     */
    private String buildType;

    /**
     * 构建开始时间
     * yyyy-MM-dd HH:mm:ss
     */
    private String buildStartTime;
    /**
     * 构建结束时间
     * yyyy-MM-dd HH:mm:ss
     */
    private String buildEndTime;

    /**
     * 文件存放路径
     */
    private String excelFile;

    /**
     * 默认构造器
     * 开始时间尚未设置
     *
     * @param excelDTO 数据
     */
    public MetadataDTO(WriterExcelDTO excelDTO) {
        Date date = new Date();
        this.dateTime = Objects.isNull(excelDTO.getDateQuery()) ? date : excelDTO.getDateQuery().getRecordDate();
        this.templateName = excelDTO.getTemplate().getTemplateName();
        String templateType = excelDTO.getTemplate().getTemplateType();
        this.type = ReportTemplateTypeEnum.getType(templateType).getName();
        this.templatePath = excelDTO.getTemplate().getTemplatePath();
        this.autoBuild = "Enable";
        this.build = excelDTO.getTemplate().getBuild();
        this.buildUnit = excelDTO.getTemplate().getBuildUnit();
        this.autoBuildDelay = excelDTO.getTemplate().getBuildDelay();
        this.autoBuildDelayUnit = excelDTO.getTemplate().getBuildDelayUnit();
        this.language = excelDTO.getTemplate().getTemplateLang();
        this.blowingInDate = DateUtil.getFormatDateTime(date, DateUtil.yyyyMMddFormat);

        this.buildType = Objects.isNull(excelDTO.getJobExecuteEnum()) ? JobExecuteEnum.automatic.getName() : excelDTO.getJobExecuteEnum().getName();
        this.excelFile = excelDTO.getExcelPathInfo().getSaveFilePath();
        this.buildStartTime = DateUtil.getFormatDateTime(excelDTO.getStartTime(), DateUtil.fullFormat);
        this.buildEndTime = DateUtil.getFormatDateTime(date, DateUtil.fullFormat);
    }

    /**
     * 构建需要的数据
     *
     * @return 内容
     */
    public Map<String, Object> buildMap() {
        // 按照顺序插入
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("DateTime", this.dateTime);
        result.put("DateTime1", DateUtil.getFormatDateTime(this.dateTime, DateUtil.yyyyMMddChineseFormat));
        result.put("DateTime2", DateUtil.getFormatDateTime(this.dateTime, DateUtil.MMddChineseFormat));
        result.put("DateTime3", DateUtil.getFormatDateTime(this.dateTime, "yyyy/MM/dd"));
        result.put("DateTime4", DateUtil.getFormatDateTime(this.dateTime, "MM/dd"));
        result.put("DateTime5", DateUtil.getFormatDateTime(this.dateTime, "MM月"));
        result.put("DateTime6", DateUtil.getFormatDateTime(this.dateTime, "yyyy年MM月"));
        result.put("TemplateName", this.templateName);
        result.put("Type", this.type);
        result.put("TemplatePath", this.templatePath);
        result.put("AutoBuild", this.autoBuild);
        result.put("Build", this.build);
        result.put("BuildUnit", this.buildUnit);
        result.put("AutoBuildDelay", this.autoBuildDelay);
        result.put("AutoBuildDelayUnit", this.autoBuildDelayUnit);
        result.put("Language", this.language);
        result.put("BlowingInDate", this.blowingInDate);
        result.put("Build_Type", this.buildType);
        result.put("Build_StartTime", this.buildStartTime);
        result.put("Build_EndTime", this.buildEndTime);
        result.put("ExcelFile", this.excelFile);
        return result;
    }
}
