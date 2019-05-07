package com.cisdi.steel.module.job.a1.writer;

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
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 配料单报表
 */
@Component
public class PeiLiaoDanWriter extends AbstractExcelReadWriter {

    @Autowired
    private ReportIndexMapper reportIndexMapper;

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = getUrl(version);
        int numberOfSheets = workbook.getNumberOfSheets();
        DateQuery date = this.getDateQuery(excelDTO);
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                List<CellData> cellDataList = this.mapDataHandler(url, workbook);
                ExcelWriterUtil.setCellValue(sheet, cellDataList);

            }

            if ("Sheet1".equals(sheetName)) {
                //处理关联到上一次数据
                if ("6.0".equals(version)) {
                    List<CellData> cellDataList = new ArrayList<>();
                    dealLastData(date.getRecordDate(), cellDataList);
                }
            }
        }
        return workbook;
    }

    private void dealLastData(Date date, List<CellData> cellDataList) {
        //查询配料单最后一条数据
        ReportIndex reportIndex = new ReportIndex();
        reportIndex.setIndexType("report_day");
        reportIndex.setIndexLang("cn_zh");
        reportIndex.setReportCategoryCode(JobEnum.gl_peiliaodan6.getCode());
        reportIndex.setSequence("6高炉");
        reportIndex.setCurrDate(date);
        ReportIndex reportIndex1 = reportIndexMapper.selectIdByParamter(reportIndex);
        if (Objects.nonNull(reportIndex1)) {
            //获取到指定路径下的excel
            Workbook workbook = this.getWorkbook(reportIndex1.getPath());
            //解析excel获取指定位置的值
            String v1 = PoiCustomUtil.getSheetCell(workbook, "Sheet1", 5, 17);
            String v2 = PoiCustomUtil.getSheetCell(workbook, "Sheet1", 6, 17);
            //写入到当前excel中
            ExcelWriterUtil.addCellData(cellDataList, 5, 17, v1);
            ExcelWriterUtil.addCellData(cellDataList, 6, 17, v2);
        }
    }

    protected List<CellData> mapDataHandler(String url, Workbook workbook) {
        String s = httpUtil.get(url, null);
        if (StringUtils.isBlank(s)) {
            return null;
        }
        JSONObject json = JSONObject.parseObject(s);
        if (Objects.isNull(json)) {
            return null;
        }
        List<CellData> cellDataList = new ArrayList<>();
        JSONObject jsonObject = json.getJSONObject("data");
        if (Objects.isNull(jsonObject)) {
            return null;
        }

        JSONArray data = jsonObject.getJSONArray("distribution");

        if (Objects.nonNull(data)) {
            JSONArray jsArr1 = new JSONArray();
            JSONArray jsArr2 = new JSONArray();

            Set<Object> set = new HashSet<>();
            Map<Object, Object> map = new HashMap<>();

            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonObject1 = data.getJSONObject(i);
                String typ = jsonObject1.getString("typ");
                if ("C".equals(typ)) {
                    jsArr1.add(jsonObject1);
                } else if ("O".equals(typ)) {
                    jsArr2.add(jsonObject1);
                }
            }
            dealData(jsArr1, cellDataList, 1, set, map);
            dealData(jsArr2, cellDataList, 4, set, map);
            int index = 0;
            if (set.size() > 0) {
                for (Object key : set) {
                    Object value = map.get(key);
                    ExcelWriterUtil.addCellData(cellDataList, 23, index, key);
                    ExcelWriterUtil.addCellData(cellDataList, 24, index, value);
                    index++;
                }
            }
        }

        JSONArray bookMaterials = jsonObject.getJSONArray("bookMaterials");
        //烧结矿
        List<JSONObject> sinterList = new ArrayList<>();
        //球团矿
        List<JSONObject> pellets = new ArrayList<>();
        //生矿
        List<JSONObject> lumpore = new ArrayList<>();
        //小粒烧
        List<JSONObject> ssinter = new ArrayList<>();
        //中灰石
        List<JSONObject> limeston = new ArrayList<>();
        //白云石
        List<JSONObject> dolomite = new ArrayList<>();
        //硅石
        List<JSONObject> quart = new ArrayList<>();
        //锰矿
        List<JSONObject> mnore = new ArrayList<>();

        //废钢
        List<JSONObject> scrap = new ArrayList<>();


        //小粒焦
        List<JSONObject> cokenut = new ArrayList<>();
        //焦炭
        List<JSONObject> coke = new ArrayList<>();

        int size = bookMaterials.size();
        for (int i = 0; i < size; i++) {
            JSONObject materialsJSONObject = bookMaterials.getJSONObject(i);
            if (Objects.nonNull(materialsJSONObject)) {
                String matclass = materialsJSONObject.getString("matClass");
                if ("SINTER".equals(matclass)) {
                    sinterList.add(materialsJSONObject);
                } else if ("PELLETS".equals(matclass)) {
                    pellets.add(materialsJSONObject);
                } else if ("LUMPORE".equals(matclass)) {
                    lumpore.add(materialsJSONObject);
                } else if ("SSINTER".equals(matclass)) {
                    ssinter.add(materialsJSONObject);
                } else if ("LIMESTON".equals(matclass)) {
                    limeston.add(materialsJSONObject);
                } else if ("DOLOMITE".equals(matclass)) {
                    dolomite.add(materialsJSONObject);
                } else if ("QUART".equals(matclass)) {
                    quart.add(materialsJSONObject);
                } else if ("MNORE".equals(matclass)) {
                    mnore.add(materialsJSONObject);
                } else if ("COKENUT".equals(matclass)) {
                    cokenut.add(materialsJSONObject);
                } else if ("COKE".equals(matclass)) {
                    coke.add(materialsJSONObject);
                } else if ("SCRAP".equals(matclass)) {
                    scrap.add(materialsJSONObject);
                }
            }
        }
        dealData1(sinterList, cellDataList, 8);
        dealData1(pellets, cellDataList, 10);
        dealData1(lumpore, cellDataList, 12);
        dealData1(ssinter, cellDataList, 14);
        dealData1(limeston, cellDataList, 15);
        dealData1(dolomite, cellDataList, 16);
        dealData1(quart, cellDataList, 17);
        dealData1(mnore, cellDataList, 18);
        dealData1(scrap, cellDataList, 19);


        dealData2(cokenut, cellDataList, 8);
        dealData2(coke, cellDataList, 10);

        JSONObject parameters = jsonObject.getJSONObject("parameters");

        if (Objects.nonNull(parameters)) {
            JSONObject components = parameters.getJSONObject("components");
            if (Objects.nonNull(components)) {
                Double clinkerRatio = components.getDouble("ClinkerRatio");
                Double oreWeight = components.getDouble("OreWeight");
                Double allOCRate = components.getDouble("AllOCRate");
                ExcelWriterUtil.addCellData(cellDataList, 13, 3, clinkerRatio);
                ExcelWriterUtil.addCellData(cellDataList, 14, 3, oreWeight);
                ExcelWriterUtil.addCellData(cellDataList, 15, 3, allOCRate);

                Double oreStock = components.getDouble("OreStock");
                Double cokeStock = components.getDouble("CokeStock");
                ExcelWriterUtil.addCellData(cellDataList, 0, 6, oreStock);
                ExcelWriterUtil.addCellData(cellDataList, 0, 7, cokeStock);


                Object cokeWithAdd = components.get("CokeWithAdd");
                ExcelWriterUtil.addCellData(cellDataList, 0, 9, cokeWithAdd);
            }
        }

        return cellDataList;
    }

    private void dealData2(List<JSONObject> list, List<CellData> cellDataList, int rowIndex) {
        BigDecimal weight = BigDecimal.ZERO;
        BigDecimal moisture = BigDecimal.ZERO;
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = list.get(i);
            weight = weight.add(object.getBigDecimal("weight"));
            moisture = moisture.add(object.getBigDecimal("moisture"));
        }
        ExcelWriterUtil.addCellData(cellDataList, rowIndex, 3, weight);
        ExcelWriterUtil.addCellData(cellDataList, ++rowIndex, 3, moisture);
    }


    private void dealData1(List<JSONObject> list, List<CellData> cellDataList, int rowIndex) {
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = list.get(i);
            Object descr = object.get("descr");
            Object weight = object.get("weight");
            Double moisture = object.getDouble("moisture");
            ExcelWriterUtil.addCellData(cellDataList, rowIndex, 0, descr);
            ExcelWriterUtil.addCellData(cellDataList, rowIndex, 1, weight);
            ExcelWriterUtil.addCellData(cellDataList, rowIndex, 2, moisture);
            rowIndex++;
        }
    }

    private void dealData(JSONArray data, List<CellData> cellDataList, int rowIndex, Set<Object> set, Map<Object, Object> mapData) {
        List<JSONObject> list = new ArrayList<>();
        int size = data.size();
        for (int i = 0; i < size; i++) {
            JSONObject map = data.getJSONObject(i);
//            Object val2 = map.get("angle");
            Object val3 = map.get("round");
            if (Objects.nonNull(val3)) {
                if ("0.0".equals(val3.toString())) {

                } else {
                    list.add(map);
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            JSONObject map = list.get(i);
            Object val1 = map.get("seq");
            Object val2 = map.get("angle");
            Object val3 = map.get("round");
            ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val1);
            ExcelWriterUtil.addCellData(cellDataList, rowIndex + 1, i, val2);
            ExcelWriterUtil.addCellData(cellDataList, rowIndex + 2, i, val3);
            //将装料编号和数据放入相应的集合中
            set.add(val1);
            mapData.put(val1, val2);
        }
    }

    private String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/burden/report";
    }
}
