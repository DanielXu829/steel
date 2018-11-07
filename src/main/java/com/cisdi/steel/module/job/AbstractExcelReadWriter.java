package com.cisdi.steel.module.job;

import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.config.http.HttpProperties;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

/**
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/6 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Slf4j
public abstract class AbstractExcelReadWriter implements IExcelReadWriter {

    @Autowired
    protected HttpUtil httpUtil;

    @Autowired
    protected HttpProperties httpProperties;

    /**
     * 所有 子类具体执行的方法
     *
     * @param excelDTO 数据
     * @return 结果
     */
    public abstract Workbook excelExecute(WriterExcelDTO excelDTO);

    @Override
    public Workbook writerExcelExecute(WriterExcelDTO excelDTO) {
        // 子类执行
        Workbook workbook = this.excelExecute(excelDTO);
        // 构建元数据
        PoiCustomUtil.buildMetadata(workbook, excelDTO);
        return workbook;
    }


    /**
     * 获取操作的文件
     *
     * @param templatePath 模板路径
     * @return 文件
     */
    protected Workbook getWorkbook(String templatePath) {
        try {
            return WorkbookFactory.create(new File(templatePath));
        } catch (IOException | InvalidFormatException e) {
            throw new NullPointerException("模板路径不存在" + templatePath);
        }

    }
}
