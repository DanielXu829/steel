package com.cisdi.steel.module.job.a1.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 配料单报表
 */
@Component
public class PeiLiaoDanWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = getUrl(version);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
//                Row row = sheet.createRow(29);
//                row.createCell(0).setCellValue(shift);
//                row.getCell(0).setCellType(CellType.STRING);
                List<CellData> cellDataList = this.mapDataHandler(url, workbook);
                ExcelWriterUtil.setCellValue(sheet, cellDataList);

            }

        }
        return workbook;
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
            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonObject1 = data.getJSONObject(i);
                String typ = jsonObject1.getString("typ");
                if ("C".equals(typ)) {
                    jsArr1.add(jsonObject1);
                } else if ("O".equals(typ)) {
                    jsArr2.add(jsonObject1);
                }
            }
            dealData(jsArr1, cellDataList, 1);
            dealData(jsArr2, cellDataList, 4);
        }

        JSONObject parameters = jsonObject.getJSONObject("parameters");
        if (Objects.nonNull(parameters)) {
            JSONObject components = parameters.getJSONObject("components");
            if (Objects.nonNull(components)) {
                Double oreStock = components.getDouble("OreStock");
                Double cokeStock = components.getDouble("CokeStock");
                ExcelWriterUtil.addCellData(cellDataList, 0, 6, oreStock);
                ExcelWriterUtil.addCellData(cellDataList, 0, 7, cokeStock);
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


        dealData2(cokenut, cellDataList, 8);
        dealData2(coke, cellDataList, 10);

        if (Objects.nonNull(parameters)) {
            JSONObject components = parameters.getJSONObject("components");
            if (Objects.nonNull(components)) {
                Double clinkerRatio = components.getDouble("ClinkerRatio");
                Double oreWeight = components.getDouble("OreWeight");
                Double allOCRate = components.getDouble("AllOCRate");
                ExcelWriterUtil.addCellData(cellDataList, 13, 2, clinkerRatio);
                ExcelWriterUtil.addCellData(cellDataList, 14, 2, oreWeight);
                ExcelWriterUtil.addCellData(cellDataList, 15, 2, allOCRate);
            }
        }

        return cellDataList;
    }

    private void dealData2(List<JSONObject> list, List<CellData> cellDataList, int rowIndex) {
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = list.get(i);
            String weight = object.getString("weight");
            Double moisture = object.getDouble("moisture");
            ExcelWriterUtil.addCellData(cellDataList, rowIndex, 2, weight);
            ExcelWriterUtil.addCellData(cellDataList, ++rowIndex, 2, moisture);
        }
    }


    private void dealData1(List<JSONObject> list, List<CellData> cellDataList, int rowIndex) {
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = list.get(i);
            String descr = object.getString("descr");
            Double weight = object.getDouble("weight");
            ExcelWriterUtil.addCellData(cellDataList, rowIndex, 0, descr);
            ExcelWriterUtil.addCellData(cellDataList, rowIndex++, 1, weight);
        }
    }

    private void dealData(JSONArray data, List<CellData> cellDataList, int rowIndex) {
        int size = data.size();
        for (int i = 0; i < size; i++) {
            JSONObject map = data.getJSONObject(i);
            Object val1 = map.get("seq");
            Object val2 = map.get("angle");
            Object val3 = map.get("round");
            ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val1);
            ExcelWriterUtil.addCellData(cellDataList, rowIndex + 1, i, val2);
            ExcelWriterUtil.addCellData(cellDataList, rowIndex + 2, i, val3);
        }
    }

    private String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/burden/report";
    }
}
