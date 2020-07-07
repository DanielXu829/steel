package com.cisdi.steel.module.job.drt.writer;

import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.jh.res.JHTagValueListDTO;
import com.cisdi.steel.dto.response.jh.res.TagValue;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.entity.TargetManagement;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("ALL")
@Slf4j
public class JHDynamicReportTemplateWriter extends DynamicReportTemplateWriter {
    @Autowired
    private TargetManagementMapper targetManagementMapper;

    @Override
    protected void handleData(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        Sheet sheet = workbook.getSheetAt(1);
        // 以下划线开头的sheet 表示 隐藏表 待处理
        String sheetName = sheet.getSheetName();
        String[] sheetSplit = sheetName.split("_");
        if (sheetSplit.length == 4) {
            DateQuery dateQuery = this.getDateQuery(excelDTO);
            List<DateQuery> dateQueries = DateQueryUtil.buildDay2HourFromYesEighteen(dateQuery.getRecordDate());
            List<String> tagNames = PoiCustomUtil.getFirstRowCelVal(sheet);
            String queryUrl = getUrl(version);

            for (int i = 0; i < dateQueries.size(); i++) {
                DateQuery item = dateQueries.get(i);
                if (item.getRecordDate().before(new Date())) {
                    int rowIndex = i + 1;
                    List<CellData> cellDataList = this.mapDataHandler(rowIndex, queryUrl, tagNames, item);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }
    }

    /**
     * 通过API拿数据，并且按传入行数和列名来组装List<CellData>
     * @param rowIndex 行数
     * @param url
     * @param columns
     * @param tagColumns
     * @param dateQuery
     * @return List<CellData>
     */
    private List<CellData> mapDataHandler(Integer rowIndex, String url, List<String> tagNames, DateQuery dateQuery) {
        List<CellData> cellDataList = new ArrayList<>();
        List<TargetManagement> targetManagements = targetManagementMapper.selectTargetManagementsByTargetNames(tagNames);
        List<String> tagFormulas = targetManagements.stream().map(TargetManagement::getTargetFormula).collect(Collectors.toList());
        JHTagValueListDTO jhTagValueListDTO = getTagListData(url, tagFormulas, dateQuery);

        if (Objects.nonNull(jhTagValueListDTO) && MapUtils.isNotEmpty(jhTagValueListDTO.getData())) {
            for (int i = 0; i < tagFormulas.size(); i++) {
                String column = tagFormulas.get(i);
                if (StringUtils.isNotBlank(column)) {
                    // 可能是处理方法加tag点的组合。 e.g: max,tag1,tag2 需要根据最前面的方式做特殊处理
                    String[] columnSplit = column.split(",");
                    if (Objects.nonNull(columnSplit) && columnSplit.length > 2) {
                        List<Double> specialValues = new ArrayList<Double>();
                        // 获取处理方式
                        String executeWay = columnSplit[0];
                        int columnSplitSize = columnSplit.length;
                        for (int k = 1; k < columnSplitSize; k++) {
                            LinkedHashMap<String, List<TagValue>> dataObject = jhTagValueListDTO.getData();
                            List<TagValue> tagValues = dataObject.get(columnSplit[k]);
                            if (CollectionUtils.isNotEmpty(tagValues)) {
                                Double val = getLatestNonZeroValue(tagValues);
                                specialValues.add(val);
                            }
                        }
                        // list中的值经过处理
                        Double executeVal = ExcelWriterUtil.executeSpecialList(executeWay, specialValues);
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, executeVal);
                    } else {
                        LinkedHashMap<String, List<TagValue>> dataObject = jhTagValueListDTO.getData();
                        List<TagValue> tagValues = dataObject.get(column);
                        if (CollectionUtils.isNotEmpty(tagValues)) {
                            Double val = getLatestNonZeroValue(tagValues);
                            ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
                        }
                    }
                }
            }
        }

        return cellDataList;
    }

    /**
     * 获取焦化JHTagValueListDTO
     * @param url
     * @param columnCells
     * @param dateQuery
     * @return
     */
    private JHTagValueListDTO getTagListData(String url, List<String> columnCells, DateQuery dateQuery) {
        Map<String, String> queryParam = new HashMap<String, String>();
        queryParam.put("startDate", String.valueOf(dateQuery.getQueryStartTime()));
        queryParam.put("endDate", String.valueOf(dateQuery.getQueryEndTime()));
        String searchParam = StringUtils.join(columnCells, ",");
        queryParam.put("tagNames", searchParam);
        String tagListDataJSONStr = httpUtil.get(url, queryParam);
        // 去除返回json字符串中的total : xxx, 防止解析JSON失败
        String totalRegex = "\"total\":.*?,";
        tagListDataJSONStr = tagListDataJSONStr.replaceFirst(totalRegex, "");
        JHTagValueListDTO jhTagValueListDTO = JSON.parseObject(tagListDataJSONStr, JHTagValueListDTO.class);

        return jhTagValueListDTO;
    }


    /**
     * 通过tag点拿数据的API，路径可能会改变
     * @param version
     * @return
     */
    private String getUrl(String version) {
        return httpProperties.getJHUrlVersion(version) + "/jhTagValue/getNewTagValue";
    }
}