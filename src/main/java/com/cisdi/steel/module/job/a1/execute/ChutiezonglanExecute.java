package com.cisdi.steel.module.job.a1.execute;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.CookieUtils;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a1.writer.ChutiezonglanWriter;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/25 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ChutiezonglanExecute extends AbstractJobExecuteExecute {

    @Autowired
    private ChutiezonglanWriter chutiezonglanWriter;

    @Autowired
    private HttpProperties httpProperties;

    @Autowired
    private JobProperties jobProperties;

    private static final String COOKIE_NAME = "apiCode";


    public void export(HttpServletRequest request, String starttime, String endtime, HttpServletResponse response) throws IOException {
        Workbook workbook = handlerFileWorkHook();
        int numberOfNames = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfNames; i++) {
            Sheet sheetAt = workbook.getSheetAt(i);
            if (sheetAt.getSheetName().startsWith("_tag")) {
                // 处理数据
                handlerFileData(workbook, sheetAt, request, starttime, endtime);
            }
        }
        String dateTime = DateUtil.getFormatDateTime(new Date(), "yyyy-MM-dd_HH");
        String fileName = "出铁总览" + dateTime + ".xlsx";
        String tempFile = jobProperties.getTemplatePath() + File.separator + fileName;
        FileOutputStream fos = new FileOutputStream(tempFile);
        workbook.setForceFormulaRecalculation(true);
        workbook.write(fos);
        fos.close();
        FileUtils.downFile(new File(tempFile), request, response, fileName);
        FileUtils.deleteFile(tempFile);
    }

    private void handlerFileData(Workbook workbook, Sheet sheet, HttpServletRequest request, String starttime, String endtime) {
        String cookieValue = CookieUtils.getCookieValue(request, COOKIE_NAME);
        String url = httpProperties.getUrlApiGLTwo();
        if (StringUtils.isNotBlank(cookieValue)) {
            if ("cms".equals(cookieValue)) {
                url = httpProperties.getUrlApiGLOne();
            } else if ("cms2".equals(cookieValue)) {
                url = httpProperties.getUrlApiGLTwo();
            }
        }
        url = url + "/taps/sg/period";
        Map<String, String> queries = new HashMap<>();
        queries.put("starttime", starttime);
        queries.put("endtime", endtime);
        queries.put("pagenum", "1");
        queries.put("pagesize", "100000");
        String s = httpUtil.get(url, queries);
        if (StringUtils.isBlank(s)) {
            return;
        }
        JSONObject object = JSONObject.parseObject(s);
        JSONArray data = object.getJSONArray("data");

        List<String> columns = PoiCustomUtil.getRowCelVal(sheet, 0);
        int rowIndex = 1;
        if (Objects.nonNull(data) && data.size() > 0) {
            List<CellData> cellDataList = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                JSONObject dataJSONObject = data.getJSONObject(i);
                List<CellData> cellDatas = ExcelWriterUtil.handlerRowData(columns, rowIndex + i, dataJSONObject);
                cellDataList.addAll(cellDatas);
            }
            SheetRowCellData.builder()
                    .cellDataList(cellDataList)
                    .sheet(sheet)
                    .workbook(workbook)
                    .build().allValueWriteExcel();
        }
    }

    private Workbook handlerFileWorkHook() {
        InputStream resourceAsStream = ChutiezonglanExecute.class.getClassLoader().getResourceAsStream("config/出铁信息记录表.xlsx");
        try {
            return WorkbookFactory.create(resourceAsStream);
        } catch (IOException | InvalidFormatException e) {
            throw new NullPointerException("模板路径不存在");
        }
    }


    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return chutiezonglanWriter;
    }
}
