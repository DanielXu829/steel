package com.cisdi.steel.module.job.drt.writer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.TagValueMapDTO;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.enums.SequenceEnum;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description: 动态报表 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2020/1/7 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class DynamicReportTemplateWriter extends AbstractExcelReadWriter {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    protected TargetManagementMapper targetManagementMapper;

    /**
     * @param excelDTO 数据
     * @return
     */
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = null;
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
            log.error("在模板中获取version失败", e);
            throw e;
        }
        DynamicReportTemplateWriter writer = null;
        if (SequenceEnum.GL8.getSequenceCode().equals(excelDTO.getTemplate().getSequence())){
            writer = applicationContext.getBean(GLDynamicReportTemplateWriter.class);
        } else if (SequenceEnum.JH910.getSequenceCode().equals(excelDTO.getTemplate().getSequence())){
            writer = applicationContext.getBean(JHDynamicReportTemplateWriter.class);
        } else {
            // TODO 烧结通用解析器
        }

        if (writer == null){
            throw new RuntimeException("没有对应的子类解析器writer");
        } else {
            writer.handleData(excelDTO, workbook, version);
        }

        return workbook;
    }

    /**
     * 处理报表数据
     * @param excelDTO
     * @param workbook
     * @param version
     */
    protected void handleData(WriterExcelDTO excelDTO, Workbook workbook, String version){
        throw new RuntimeException("没有对应的子类解析器writer");
    }

}
