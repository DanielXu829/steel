package com.cisdi.steel.module.job.a1.writer;

import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 管控指标
 */
@Component
@SuppressWarnings("ALL")
public class GuankongzhibiaoWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version) + "/indicators/overview/"+date.getRecordDate().getTime();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            if (sheetName.contains("管控指标")) {
                handlerFileData(workbook,sheet,url);
            }
        }
        return workbook;
    }

    private void handlerFileData(Workbook workbook, Sheet sheet, String url) {
        List<CellData> cellDataList = new ArrayList<>();

        String s = httpUtil.get(url, null);
        if (StringUtils.isBlank(s)) {
            return;
        }
        JSONObject object = JSONObject.parseObject(s);
        JSONObject data = object.getJSONObject("data");

        if (Objects.nonNull(data)) {
            Map<String, Object> innerMap = data.getInnerMap();
            Set<String> keySet = innerMap.keySet();
            Integer[] list = new Integer[keySet.size()];
            int i = 0;
            for (String key : keySet) {
                list[i++] = Integer.valueOf(key);
            }
            Arrays.sort(list);
            String ptTarget = "";
            String siTarget = "";
            int row = 2;
            for (Integer key : list) {
                if(key > 900){
                    continue;
                }
                JSONObject tmp = data.getJSONObject(key+"");
                if (Objects.nonNull(tmp)) {
                    //项目
                    String item = tmp.getString("item");
                    //目标
                    String target = tmp.getString("target");

                    switch (item){
                        case "铁水PT(℃)":
                            ptTarget = target;
                            break;
                        case "铁水PT命中率":
                            item = item + "(" + ptTarget + ")";
                            break;
                        case "铁水Si目标值":
                            siTarget = target;
                            break;
                        case "铁水Si小于目标值":
                            String[] siTargets = siTarget.split("-");
                            item = item + "(" + siTargets[0] + ")";
                            break;
                        case "铁水Si命中率":
                            item = item + "(" + siTarget + ")";
                            break;
                    }

                    //昨日实际
                    Object day = tmp.get("day");
                    //昨日对比目标
                    Object dayDevi = tmp.get("dayDevi");
                    //本月累计
                    Object month = tmp.get("month");
                    //本月对比目标
                    Object monthDevi = tmp.get("monthDevi");

                    ExcelWriterUtil.addCellData(cellDataList, row, 1, item);
                    ExcelWriterUtil.addCellData(cellDataList, row, 2, target);
                    ExcelWriterUtil.addCellData(cellDataList, row, 3, day);
                    ExcelWriterUtil.addCellData(cellDataList, row, 4, dayDevi);
                    ExcelWriterUtil.addCellData(cellDataList, row, 5, month);
                    ExcelWriterUtil.addCellData(cellDataList, row, 6, monthDevi);
                    row++;
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
