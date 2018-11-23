package com.cisdi.steel.module.job.strategy.api;

import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.CellValInfo;
import com.cisdi.steel.module.job.dto.RowCellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 全部同一个接口 策略
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class TagStrategy extends AbstractApiStrategy {

    @Override
    public String getKey() {
        return "tag";
    }

    @Override
    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        List<String> columnCells = PoiCustomUtil.getFirstRowCelVal(sheet);

        String url = httpProperties.getUrlApiGLOne() + "/getTagValues/tagNamesInRange";
        List<CellData> rowCellDataList = new ArrayList<>();
        int size = queryList.size();
        for (int rowNum = 0; rowNum < size; rowNum++) {
            DateQuery eachDate = queryList.get(rowNum);
            List<CellData> cellValInfoList = eachData(columnCells, url, eachDate.getQueryParam());
            rowCellDataList.addAll(cellValInfoList);
        }
        return SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(rowCellDataList)
                .build();
    }


    private List<CellData> eachData(List<String> cellList, String url, Map<String, String> queryParam) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("starttime", queryParam.get("starttime"));
        jsonObject.put("endtime", queryParam.get("endtime"));
        jsonObject.put("tagnames", cellList);

        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        JSONObject obj = JSONObject.parseObject(result);
        obj=obj.getJSONObject("data");
        List<CellData> resultList = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < cellList.size(); columnIndex++) {
            String cell = cellList.get(columnIndex);
            if (StringUtils.isNotBlank(cell)) {
                JSONObject data = obj.getJSONObject(cell);
                if(Objects.nonNull(data)){
                    Set<String> keySet = data.keySet();
                    int rowIndex = 1;
                    for (String key : keySet) {
                        Object o = data.get(key);
                        ExcelWriterUtil.addCellData(resultList, rowIndex++, columnIndex, o);
                    }
                }
            }
        }
        return resultList;
    }

//    /**
//     * 遍历每个小时的值
//     *
//     * @param cellList   列名
//     * @param url        发送请求的地址
//     * @param queryParam 查询参数
//     * @return 结果
//     */
//    private List<CellValInfo> eachData(List<Cell> cellList, String url, Map<String, String> queryParam) {
//        List<CellValInfo> results = new ArrayList<>();
//        for (Cell cell : cellList) {
//            String column = PoiCellUtil.getCellValue(cell);
//            if (StringUtils.isNotBlank(column)) {
//                String[] columnSplit = column.split("/");
//                String method = "avg";
//                String tagName = columnSplit[0];
//                if (columnSplit.length > 2) {
//                    method = columnSplit[1];
//                }
//                queryParam.put("method", method);
//                queryParam.put("tagname", tagName);
//                // TODO: 暂时模拟数据
//                String result = httpUtil.get(url, queryParam);
////                JSONObject mock = new JSONObject();
////                mock.put("data", RandomUtils.nextInt(1, 999));
////                String result = mock.toJSONString();
//                if (StringUtils.isNotBlank(result)) {
//                    JSONObject jsonObject = JSONObject.parseObject(result);
//                    CellValInfo cellValInfo = CellValInfo.builder()
//                            .columnIndex(cell.getColumnIndex())
//                            .cellValue(jsonObject.get("data")).build();
//                    results.add(cellValInfo);
//                }
//            }
//        }
//        return results;
//    }
}
