package com.cisdi.steel.module.job.a1.execute;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a1.writer.GongyiLuruWriter;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 工艺录入导出
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GongyiLuruExecute extends AbstractJobExecuteExecute {

    @Autowired
    private HttpProperties httpProperties;

    @Autowired
    private JobProperties jobProperties;

    @Autowired
    private GongyiLuruWriter gongyiLuruWriter;

    @Autowired
    private ReportIndexMapper reportIndexMapper;


    public void export(HttpServletRequest request, String time, String code, String save, HttpServletResponse response) throws IOException {
        JobEnum jobEnum = dealApiEnum(code);
        Workbook workbook = handlerFileWorkHook(jobEnum);
        int numberOfNames = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfNames; i++) {
            Sheet sheetAt = workbook.getSheetAt(i);
            if (sheetAt.getSheetName().startsWith("_tag")) {
                // 处理数据
                handlerFileData(workbook, sheetAt, time, code, save);
            }
        }

        boolean flag = false;
        //保存 调用最新接口
        if ("1".equals(save)) {
            flag = true;
            //打印 time为空 在线查询当前数据库  time不为空 离线查询时间接口
        } else if ("2".equals(save)) {
            if (StringUtils.isEmpty(time)) {
                flag = false;
                ReportIndex reportIndex = reportIndexMapper.queryLastOne(code);
                if (Objects.nonNull(reportIndex)) {
                    FileUtils.downFile(new File(reportIndex.getPath()), request, response, reportIndex.getName());
                }
            } else {
                flag = true;
            }

        }

        if (flag) {
            String sequencem = dealApiSequencem(code);
            String dateTime = DateUtil.getFormatDateTime(new Date(), "yyyy-MM-dd_HH_mm_ss");
            String fileName = sequencem + "工艺卡参数总览" + dateTime + ".xlsx";
            String tempFile = jobProperties.getFilePath() + File.separator + "gyexport" + File.separator + fileName;
            FileOutputStream fos = new FileOutputStream(tempFile);
            workbook.setForceFormulaRecalculation(true);
            workbook.write(fos);
            fos.close();
            FileUtils.downFile(new File(tempFile), request, response, fileName);

            ReportIndex reportIndex = new ReportIndex();
            reportIndex.setSequence(sequencem);
            reportIndex.setReportCategoryCode(code);
            reportIndex.setName(fileName);
            reportIndex.setIndexLang("cn_zh");
            reportIndex.setPath(tempFile);
            reportIndex.setIndexType("report_day");
            reportIndex.setCreateTime(new Date());
            reportIndex.setUpdateTime(new Date());
            reportIndex.setRecordDate(new Date());
            reportIndexMapper.insert(reportIndex);
        }
    }


    private void handlerFileData(Workbook workbook, Sheet sheet, String time, String code, String save) {
        List<CellData> cellDataList = new ArrayList<>();
        boolean flag = false;

        //保存 调用最新接口
        if ("1".equals(save)) {
            flag = true;
            //打印 time为空 在线查询当前数据库  time不为空 离线查询时间接口
        } else if ("2".equals(save)) {
            if (StringUtils.isBlank(time)) {
                flag = false;
            } else {
                flag = true;
            }
        }
        if (!flag) {
            return;
        }

        String url = dealApiCode(code);
        if (StringUtils.isNotBlank(time)) {
            url = url + "/process/findFormByTime?time=" + time;
        } else {
            url = url + "/process/findNewestFormData";
        }

        String s = httpUtil.get(url, null);
        if (StringUtils.isBlank(s)) {
            return;
        }
        JSONObject object = JSONObject.parseObject(s);
        JSONObject data = object.getJSONObject("data");

        if (Objects.nonNull(data)) {
            JSONObject tagetParameters = data.getJSONObject("tagetParameters");
            if (Objects.nonNull(tagetParameters)) {
                //发布人
                Object publisher = tagetParameters.get("publisher");
                //执行时间
                Object onlineTime = tagetParameters.get("onlineTime");
                //备注
                Object remarks = tagetParameters.get("remarks");
                //白班签字人
                Object wsignName = tagetParameters.get("wsignName");
                //中班签字人
                Object msignName = tagetParameters.get("msignName");
                //夜班签字人
                Object bsignName = tagetParameters.get("bsignName");

                ExcelWriterUtil.addCellData(cellDataList, 0, 0, publisher);
                ExcelWriterUtil.addCellData(cellDataList, 0, 1, onlineTime);
                ExcelWriterUtil.addCellData(cellDataList, 0, 2, remarks);
                ExcelWriterUtil.addCellData(cellDataList, 0, 3, wsignName);
                ExcelWriterUtil.addCellData(cellDataList, 0, 4, msignName);
                ExcelWriterUtil.addCellData(cellDataList, 0, 5, bsignName);
            }

            JSONArray goalParameters = data.getJSONArray("goalParameters");
            if (Objects.nonNull(goalParameters) && goalParameters.size() > 0) {
                for (int i = 0; i < goalParameters.size(); i++) {
                    JSONObject jsonObject = goalParameters.getJSONObject(i);
                    if (Objects.nonNull(jsonObject)) {
                        Object goalParaName = jsonObject.get("goalParaName");
                        Object goalCompany = jsonObject.get("goalCompany");
                        Object goalParaValue = jsonObject.get("goalParaValue");

                        ExcelWriterUtil.addCellData(cellDataList, 1, i, goalParaName);
                        ExcelWriterUtil.addCellData(cellDataList, 2, i, goalCompany);
                        ExcelWriterUtil.addCellData(cellDataList, 3, i, goalParaValue);
                    }
                }

            }

            JSONArray importantParameters = data.getJSONArray("importantParameters");
            if (Objects.nonNull(importantParameters) && importantParameters.size() > 0) {
                for (int i = 0; i < importantParameters.size(); i++) {
                    JSONObject jsonObject = importantParameters.getJSONObject(i);
                    if (Objects.nonNull(jsonObject)) {
                        Object importantParaName = jsonObject.get("importantParaName");
                        Object min = jsonObject.get("min");
                        Object max = jsonObject.get("max");
                        Object actionVolume = jsonObject.get("actionVolume");

                        ExcelWriterUtil.addCellData(cellDataList, (i + 4), 0, importantParaName);
                        ExcelWriterUtil.addCellData(cellDataList, (i + 4), 1, min);
                        ExcelWriterUtil.addCellData(cellDataList, (i + 4), 2, max);
                        ExcelWriterUtil.addCellData(cellDataList, (i + 4), 4, actionVolume);
                    }
                }
            }

            SheetRowCellData.builder()
                    .cellDataList(cellDataList)
                    .sheet(sheet)
                    .workbook(workbook)
                    .build().allValueWriteExcel();
        }
    }

    /**
     * 处理工序 服务器
     *
     * @param code
     * @return
     */
    private String dealApiCode(String code) {
        String url = httpProperties.getUrlApiGLTwo();
        if ("gl_8gyicanshu_export".equals(code)) {
            url = httpProperties.getUrlApiGLTwo();
        } else if ("gl_6gyicanshu_export".equals(code)) {
            url = httpProperties.getUrlApiGLOne();
        } else if ("gl_7gyicanshu_export".equals(code)) {
            url = httpProperties.getUrlApiGLThree();
        }
        return url;
    }

    /**
     * 处理工序 枚举
     *
     * @param code
     * @return
     */
    private JobEnum dealApiEnum(String code) {
        if (JobEnum.gl_8gyicanshu_export.getCode().equals(code)) {
            return JobEnum.gl_8gyicanshu_export;
        } else if (JobEnum.gl_6gyicanshu_export.getCode().equals(code)) {
            return JobEnum.gl_6gyicanshu_export;
        } else if (JobEnum.gl_7gyicanshu_export.getCode().equals(code)) {
            return JobEnum.gl_7gyicanshu_export;
        }

        return null;
    }

    /**
     * 处理工序 名称
     *
     * @param code
     * @return
     */
    private String dealApiSequencem(String code) {
        if (JobEnum.gl_8gyicanshu_export.getCode().equals(code)) {
            return "8高炉";
        } else if (JobEnum.gl_6gyicanshu_export.getCode().equals(code)) {
            return "6高炉";
        } else if (JobEnum.gl_7gyicanshu_export.getCode().equals(code)) {
            return "7高炉";
        }

        return null;
    }

    private Workbook handlerFileWorkHook(JobEnum jobEnum) {
        List<ReportCategoryTemplate> templateInfo = getTemplateInfo(jobEnum);
        try {
            return WorkbookFactory.create(new File(templateInfo.get(0).getTemplatePath()));
        } catch (IOException | InvalidFormatException e) {
            throw new NullPointerException("模板路径不存在");
        }
    }

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return gongyiLuruWriter;
    }
}
