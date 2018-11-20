package com.cisdi.steel.module.job;

import cn.afterturn.easypoi.cache.manager.POICacheManager;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.strategy.StrategyContext;
import com.cisdi.steel.module.job.strategy.date.DateStrategy;
import com.cisdi.steel.module.job.strategy.options.OptionsStrategy;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

    @Autowired
    protected StrategyContext strategyContext;

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
        // 2、构建元数据
        PoiCustomUtil.buildMetadata(workbook, excelDTO);
        return workbook;
    }


    /**
     * 获取操作的文件
     *
     * @param templatePath 模板路径
     * @return 文件
     */
    protected final Workbook getWorkbook(String templatePath) {
        try {
            return WorkbookFactory.create(POICacheManager.getFile(templatePath));
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
    protected final Sheet getSheet(Workbook workbook, String sheetName, String templatePath) {
        Sheet sheet = workbook.getSheet(sheetName);
        checkNull(sheet, "没有对应的sheetName,请检查模板信息：" + templatePath);
        return sheet;
    }

    /**
     * 查询条件
     * 一定存在 recordDate数据
     *
     * @return 结果
     */
    protected final DateQuery getDateQuery(WriterExcelDTO excelDTO) {
        DateQuery dateQuery = excelDTO.getDateQuery();
        if (Objects.isNull(dateQuery)) {
            // 默认取当前时间
            dateQuery = new DateQuery(new Date());
        }
        return dateQuery;
    }

    /**
     * 处理 时间参数
     * 请检查参数后再执行
     *
     * @param sheetSplit sheetName
     * @param date       时间
     * @return 时间段
     */
    protected final List<DateQuery> getHandlerData(String[] sheetSplit, Date date) {
        // 第二个参数
        DateStrategy dateStrategy = strategyContext.getDate(sheetSplit[2]);
        this.checkNull(dateStrategy, "命名错误");
        OptionsStrategy optionsStrategy = strategyContext.getOption(sheetSplit[3]);
        this.checkNull(optionsStrategy, "option错误");
        DateQuery dateQuery = dateStrategy.handlerDate(date);
        return optionsStrategy.execute(dateQuery);
    }


    /**
     * 同样处理 方式
     *
     * @param url      单个url
     * @param rowBatch 每个map占用多少行
     * @param excelDTO 数据
     * @return 结果
     */
    protected final Workbook getMapHandler(String url, Integer rowBatch, WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                dateQueries.forEach(item -> {
                    List<CellData> cellDataList = mapDataHandler(url, columns, item, rowBatch);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                });
            }
        }
        return workbook;
    }

    /**
     * 简单处理
     * 处理成map类型的数据
     *
     * @param url       对应的url
     * @param columns   列名
     * @param dateQuery 查询条件
     * @param rowBatch  每一个map对应几行数据
     * @return 所有单元格
     */
    protected List<CellData> mapDataHandler(String url, List<String> columns, DateQuery dateQuery, int rowBatch) {
        Map<String, String> queryParam = getQueryParam(dateQuery);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONArray data = jsonObject.getJSONArray("data");
        if (Objects.isNull(data)) {
            return null;
        }
        int startRow = 1;
        return handlerJsonArray(columns, rowBatch, data, startRow);
    }

    /**
     * 获取默认查询参数
     *
     * @param dateQuery 查询时间
     * @return 结果
     */
    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        return dateQuery.getQueryParam();
    }

    /**
     * 处理返回的 json格式
     *
     * @param columns  列名
     * @param rowBatch 占用多少行
     * @param data     数据
     * @param startRow 开始行
     * @return 结果
     */
    protected List<CellData> handlerJsonArray(List<String> columns, int rowBatch, JSONArray data, int startRow) {
        List<CellData> cellDataList = new ArrayList<>();
        int size = data.size();
        for (int i = 0; i < size; i++) {
            JSONObject map = data.getJSONObject(i);
            if (Objects.nonNull(map)) {
                List<CellData> cellDataList1 = ExcelWriterUtil.handlerRowData(columns, startRow, map);
                cellDataList.addAll(cellDataList1);
            }
            startRow += rowBatch;
        }
        return cellDataList;
    }

    /**
     * 检查是否为null
     *
     * @param val     检查的值
     * @param message 消息
     */
    protected final void checkNull(Object val, String message) {
        if (Objects.isNull(val)) {
            throw new NullPointerException(message);
        }
    }


}
