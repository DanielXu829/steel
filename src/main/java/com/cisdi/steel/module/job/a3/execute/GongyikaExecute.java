package com.cisdi.steel.module.job.a3.execute;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a3.writer.GongyikaWriter;
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
public class GongyikaExecute extends AbstractJobExecuteExecute {

    @Autowired
    private HttpProperties httpProperties;

    @Autowired
    private JobProperties jobProperties;

    @Autowired
    private GongyikaWriter gongyikaWriter;

    @Autowired
    private ReportIndexMapper reportIndexMapper;


    public void export(HttpServletRequest request, String id, String code, String save, HttpServletResponse response) throws IOException {
        JobEnum jobEnum = dealApiEnum(code);
        Workbook workbook = handlerFileWorkHook(jobEnum);
        int numberOfNames = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfNames; i++) {
            Sheet sheetAt = workbook.getSheetAt(i);
            if (sheetAt.getSheetName().startsWith("_tag")) {
                // 处理数据
                handlerFileData(workbook, sheetAt, id, code, save);
            }
        }

        boolean flag = false;
        //保存 调用最新接口
        if ("1".equals(save)) {
            flag = true;
            //打印 time为空 在线查询当前数据库  time不为空 离线查询时间接口
        } else if ("2".equals(save)) {
            if (StringUtils.isEmpty(id)) {
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


    private void handlerFileData(Workbook workbook, Sheet sheet, String id, String code, String save) {
        List<CellData> cellDataList = new ArrayList<>();
        boolean flag = false;

        //保存 调用最新接口
        if ("1".equals(save)) {
            flag = true;
            //打印 id为空 在线查询当前数据库  id不为空 离线查询时间接口
        } else if ("2".equals(save)) {
            if (StringUtils.isBlank(id)) {
                flag = false;
            } else {
                flag = true;
            }
        }
        if (!flag) {
            return;
        }

        String httpUrl = dealApiCode(code);
        String url = httpUrl;
        if (StringUtils.isNotBlank(id)) {
            url = url + "/processCard/selectByKey/" + id;
        } else {
            url = url + "/processCard/selectLatest";
        }

        String s = httpUtil.get(url, null);
        if (StringUtils.isBlank(s)) {
            return;
        }
        //7 8列
        List<Long> ids1 = dealParamList(httpUrl + "/processCard/paramList?type=1", cellDataList, 7);

        //10 11列
        List<Long> ids2 = dealParamList(httpUrl + "/processCard/paramList?type=2", cellDataList, 10);

        JSONObject object = JSONObject.parseObject(s);
        JSONArray rows = object.getJSONArray("rows");
        if (Objects.nonNull(rows) && rows.size() > 0) {
            JSONObject data = rows.getJSONObject(0);
            if (Objects.nonNull(data)) {
                JSONObject processCard = data.getJSONObject("processCard");
                if (Objects.nonNull(processCard)) {
                    //发布人
                    Object publisher = processCard.get("recorder");
                    //执行时间
                    Object onlineTime = processCard.get("recordDate");
                    //备注
                    Object remarks = processCard.get("note");
                    //白班签字人
                    Object wsignName = processCard.get("daySignature");
                    //中班签字人
                    Object msignName = processCard.get("middleSignature");
                    //夜班签字人
                    Object bsignName = processCard.get("nightSignature");

                    ExcelWriterUtil.addCellData(cellDataList, 0, 0, publisher);
                    ExcelWriterUtil.addCellData(cellDataList, 0, 1, onlineTime);
                    ExcelWriterUtil.addCellData(cellDataList, 0, 2, remarks);
                    ExcelWriterUtil.addCellData(cellDataList, 0, 3, wsignName);
                    ExcelWriterUtil.addCellData(cellDataList, 0, 4, msignName);
                    ExcelWriterUtil.addCellData(cellDataList, 0, 5, bsignName);
                }

                JSONArray goals = data.getJSONArray("goals");
                if (Objects.nonNull(goals) && goals.size() > 0) {
                    for (int i = 0; i < goals.size(); i++) {
                        JSONObject jsonObject = goals.getJSONObject(i);
                        if (Objects.nonNull(jsonObject)) {
                            Long paramId = jsonObject.getLong("paramId");
                            if (Objects.nonNull(paramId)) {
                                for (int j = 0; j < ids1.size(); j++) {
                                    if (paramId.longValue() == ids1.get(j).longValue()) {
                                        Object targetVal = jsonObject.get("targetVal");
                                        Object controlRange = jsonObject.get("controlRange");
                                        ExcelWriterUtil.addCellData(cellDataList, i, 13, targetVal);
                                        ExcelWriterUtil.addCellData(cellDataList, i, 14, controlRange);
                                    }
                                }
                            }
                        }
                    }
                }
                JSONArray keys = data.getJSONArray("keys");
                if (Objects.nonNull(keys) && keys.size() > 0) {
                    for (int i = 0; i < keys.size(); i++) {
                        JSONObject jsonObject = keys.getJSONObject(i);
                        if (Objects.nonNull(jsonObject)) {
                            Long paramId = jsonObject.getLong("paramId");
                            if (Objects.nonNull(paramId)) {
                                for (int j = 0; j < ids2.size(); j++) {
                                    if (paramId.longValue() == ids2.get(j).longValue()) {
                                        Object targetVal = jsonObject.get("targetVal");
                                        Object controlRange = jsonObject.get("controlRange");
                                        Object controlAction = jsonObject.get("controlAction");
                                        ExcelWriterUtil.addCellData(cellDataList, i, 16, targetVal);
                                        ExcelWriterUtil.addCellData(cellDataList, i, 17, controlRange);
                                        ExcelWriterUtil.addCellData(cellDataList, i, 18, controlAction);
                                    }
                                }
                            }
                        }
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

    //处理参数信息
    private List<Long> dealParamList(String url, List<CellData> cellDataList, int index) {
        List<Long> ids = new ArrayList<>();
        String cs = httpUtil.get(url, null);
        if (StringUtils.isBlank(cs)) {
            return ids;
        }
        JSONObject csObject = JSONObject.parseObject(cs);
        if (Objects.nonNull(csObject)) {
            JSONArray csRows = csObject.getJSONArray("rows");
            if (Objects.nonNull(csRows) && csRows.size() > 0) {
                for (int i = 0; i < csRows.size(); i++) {
                    JSONObject rowsJSONObject = csRows.getJSONObject(i);
                    if (Objects.nonNull(rowsJSONObject)) {
                        Long id1 = rowsJSONObject.getLong("id");
                        //参数名
                        String name = rowsJSONObject.getString("name");
                        //单位
                        String unit = rowsJSONObject.getString("unit");
                        ExcelWriterUtil.addCellData(cellDataList, i, index, name);
                        ExcelWriterUtil.addCellData(cellDataList, i, index + 1, unit);
                        if (Objects.nonNull(id1)) {
                            ids.add(id1);
                        }
                    }
                }
            }
        }
        return ids;
    }

    /**
     * 处理工序 服务器
     *
     * @param code
     * @return
     */
    private String dealApiCode(String code) {
        String url = httpProperties.getUrlApiSJOne();
        if (JobEnum.sj_gyicanshu5_export.getCode().equals(code)) {
            url = httpProperties.getUrlApiSJOne();
        } else if (JobEnum.sj_gyicanshu6_export.getCode().equals(code)) {
            url = httpProperties.getUrlApiSJTwo();
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
        if (JobEnum.sj_gyicanshu5_export.getCode().equals(code)) {
            return JobEnum.sj_gyicanshu5_export;
        } else if (JobEnum.sj_gyicanshu6_export.getCode().equals(code)) {
            return JobEnum.sj_gyicanshu6_export;
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
        if (JobEnum.sj_gyicanshu5_export.getCode().equals(code)) {
            return "5烧结";
        } else if (JobEnum.sj_gyicanshu6_export.getCode().equals(code)) {
            return "6烧结";
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
        return gongyikaWriter;
    }
}
