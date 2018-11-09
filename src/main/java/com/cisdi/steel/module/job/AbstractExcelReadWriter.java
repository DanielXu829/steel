package com.cisdi.steel.module.job;

import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

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
        // 1、子类执行
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

    /**
     * 获取指定的 sheet name
     *
     * @param workbook     结果
     * @param sheetName    名称
     * @param templatePath 模板路径
     * @return 结果
     */
    protected Sheet getSheet(Workbook workbook, String sheetName, String templatePath) {
        Sheet sheet = workbook.getSheet(sheetName);
        checkNull(sheet, "没有对应的sheetName,请检查模板信息：" + templatePath);
        return sheet;
    }

    /**
     * 检查是否为null
     *
     * @param val     检查的值
     * @param message 消息
     */
    protected void checkNull(Object val, String message) {
        if (Objects.isNull(val)) {
            throw new NullPointerException(message);
        }
    }


}
