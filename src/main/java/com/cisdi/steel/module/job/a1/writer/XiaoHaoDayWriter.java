package com.cisdi.steel.module.job.a1.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.dto.Options;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.ExcelReadWriter;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.strategy.date.DateStrategy;
import com.cisdi.steel.module.job.strategy.options.OptionsStrategy;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/11 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class XiaoHaoDayWriter extends ExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = super.excelExecute(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            if (sheetName.startsWith("_ana")) {
                String[] split = sheetName.split("_");
                DateStrategy dateStrategy = strategyContext.getDate(split[2]);
                DateQuery handlerDate = dateStrategy.handlerDate(excelDTO.getDateQuery().getRecordDate());
                OptionsStrategy option = strategyContext.getOption(split[3]);
                List<DateQuery> dateQueries = option.execute(handlerDate);
                // 3、接口处理
                SheetRowCellData execute = execute(workbook, sheet, dateQueries);
                // 设置所有值
                execute.allValueWriteExcel();
            }
        }
        return workbook;
    }

    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version);


        List<String> firstRowCelVal = PoiCustomUtil.getFirstRowCelVal(sheet);
        Set<String> categorys = firstRowCelVal.stream().filter(StringUtils::isNotBlank).map(v -> v.split("/")[0]).collect(Collectors.toSet());
        Map<String, Options<Integer>> map = new HashMap<>();
        for (DateQuery dateQuery : queryList) {
            for (String category : categorys) {
                requestData(url, category, dateQuery);
            }
        }
        return null;
    }

    private void requestData(String url, String category, DateQuery dateQuery) {
        url = url + "/anaChargeValue/range";
        Map<String, String> queryParam = dateQuery.getQueryParam();
        queryParam.put("granularity", "day");
        queryParam.put("type", "LC");

        queryParam.put("category", category);
        String s = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(s)) {
            return;
        }
        JSONObject data = JSONObject.parseObject(s);
        JSONArray datas = data.getJSONArray("data");
        if (Objects.isNull(datas) || datas.size() == 0) {
            return;
        }
        int size = datas.size();
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = datas.getJSONObject(i);
            if (Objects.isNull(jsonObject)) {
                continue;
            }
            JSONObject analysisCharge = jsonObject.getJSONObject("analysisCharge");
            JSONObject values = jsonObject.getJSONObject("values");
            Long clock = analysisCharge.getLong("clock");
            Set<String> keySet = values.keySet();
            Map<String, Object> map = new HashMap<>();
            keySet.forEach(key->{
                BigDecimal bigDecimal = values.getBigDecimal(key);
            });
        }

    }
}
