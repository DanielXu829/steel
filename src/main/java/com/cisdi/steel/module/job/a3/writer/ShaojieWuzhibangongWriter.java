package com.cisdi.steel.module.job.a3.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 烧结无纸办公通用执行类
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ShaojieWuzhibangongWriter extends AbstractExcelReadWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        return this.getMapHandler2(excelDTO);
    }

    /**
     * 同样处理 方式
     *
     * @param excelDTO 数据
     * @return 结果
     */
    public Workbook getMapHandler2(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            String version = "5.0";
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                //获取版本
                String main1 = sheetSplit[1];
                if (main1.startsWith("6")) {
                    version = "6.0";
                }

                String url = getUrl(version);
                //获取对应的请求接口地址
                //工作流水账
                if (JobEnum.sj_gongzuoliushuizhang.getCode().equals(excelDTO.getJobEnum().getCode())
                        || JobEnum.sj_gongzuoliushuizhang6.getCode().equals(excelDTO.getJobEnum().getCode())) {
                    url = getUrl(version);

                    //雨季生产作业
                } else if (JobEnum.sj_yujizuoyequ.getCode().equals(excelDTO.getJobEnum().getCode())
                        || JobEnum.sj_yujizuoyequ6.getCode().equals(excelDTO.getJobEnum().getCode())) {
                    url = getUrl1(version);

                    //烧结混合机加水蒸汽预热温度统计表
                } else if (JobEnum.sj_hunhejiashuizhengqi5_month.getCode().equals(excelDTO.getJobEnum().getCode())
                        || JobEnum.sj_hunhejiashuizhengqi6_month.getCode().equals(excelDTO.getJobEnum().getCode())) {
                    url = getUrl2(version);
                } else if (JobEnum.sj_huanliaoqingkuang5_month.getCode().equals(excelDTO.getJobEnum().getCode())
                        || JobEnum.sj_huanliaoqingkuang6_month.getCode().equals(excelDTO.getJobEnum().getCode())) {
                    url = getUrl3(version);
                }

                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                List<CellData> cellDataList = mapDataHandler(url, columns);
                ExcelWriterUtil.setCellValue(sheet, cellDataList);
            }
        }
        return workbook;
    }

    private List<CellData> mapDataHandler(String url, List<String> columns) {
        List<CellData> cellDataList = new ArrayList<>();
        String result = httpUtil.get(url);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONArray rows = jsonObject.getJSONArray("rows");
                if (Objects.nonNull(rows) && rows.size() > 0) {
                    for (int i = 0; i < rows.size(); i++) {
                        JSONObject jsonObject1 = rows.getJSONObject(i);
                        List<CellData> cellData = ExcelWriterUtil.handlerRowData(columns, (i + 1), jsonObject1);
                        cellDataList.addAll(cellData);
                    }
                }
            }
        }

        return cellDataList;
    }


    /**
     * 不同的版本获取不同的接口地址
     *
     * @param version 版本号
     * @return 结果
     */
    private String getUrl(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/ploWorkNote/selectAll";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/ploWorkNote/selectAll";
        }
    }

    private String getUrl1(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/ploRainyProduce/selectAll";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/ploRainyProduce/selectAll";
        }
    }

    private String getUrl2(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/ploSteamTemp/selectAll";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/ploSteamTemp/selectAll";
        }
    }

    private String getUrl3(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/ploRetardMaterial/selectAll";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/ploRetardMaterial/selectAll";
        }
    }
}
