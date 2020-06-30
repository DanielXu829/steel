package com.cisdi.steel.module.job.gl.writer;

import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.res.TapJyDTO;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;


@Component
@SuppressWarnings("ALL")
@Slf4j
public class HeGeGuanShuWriter extends BaseGaoLuWriter {

    /**
     * @param excelDTO 数据
     * @return
     */
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = "8.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
            log.error("在模板中获取version失败", e);
        }
        // 填充报表主工作表数据
        try {
            Sheet sheet = workbook.getSheetAt(0);
            // 填充报表主工作表数据
            mapDataHandler(sheet, version, excelDTO);
        } catch (Exception e) {
            log.error("处理主工作表时产生错误", e);
            throw e;
        }

        return workbook;
    }

    private void mapDataHandler(Sheet sheet, String version, WriterExcelDTO excelDTO) {
        // 标记行
        int itemRowNum = 4;
        BigDecimal defaultCellValue = new BigDecimal(0.0);
        // 获取excel占位符列
        List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
        List<CellData> cellDataList = new ArrayList<>();

        List<Date> allDayBeginTimeInCurrentMonth = DateUtil.getAllDayBeginTimeInCurrentMonthBeforeDays(new Date(), 1);

        for (int i = 0; i < allDayBeginTimeInCurrentMonth.size(); i++) {
            // 通过api获取按天的精益数据
            DateQuery dateQueryNoDelay = DateQueryUtil.buildTodayNoDelay(allDayBeginTimeInCurrentMonth.get(i));

            // 判断当前是何种精益报表，使用不同的dataType
            String dataType = null;
            if (JobEnum.gl_guiliushuangmingzhong.getCode().equals(excelDTO.getJobEnum().getCode())) {
                dataType = "gl";
            } else if (JobEnum.gl_yuleiguanzhuang.getCode().equals(excelDTO.getJobEnum().getCode())){
                dataType = "pz";
            }

            // 获取夜班数据
            TapJyDTO tapJyDTONight = this.getTapJyDTO(version, dateQueryNoDelay, dataType, "1");

            // 获取白班数据
            TapJyDTO tapJyDTODayTime = this.getTapJyDTO(version, dateQueryNoDelay, dataType, "2");
            if (Objects.isNull(tapJyDTONight) || Objects.isNull(tapJyDTODayTime)) {
                log.error("获取精益信息失败");
            }

            int row = itemRowNum + 1 + i;
            // 循环列
            for (int j = 0; j < itemNameList.size(); j++) {
                // 获取标记项单元格中的值
                String itemName = itemNameList.get(j);
                int col = j;
                if (StringUtils.isNotBlank(itemName)) {
                    switch (itemName) {
                        case "夜子项": {
                            // 获取子项
                            if (Objects.nonNull(tapJyDTONight)) {
                                Integer fz = tapJyDTONight.getFz();
                                if (Objects.nonNull(fz)) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, fz);
                                }
                            }
                            break;
                        }
                        case "夜母项": {
                            // 获取批次总数
                            if (Objects.nonNull(tapJyDTONight)) {
                                Integer fm = tapJyDTONight.getFm();
                                if (Objects.nonNull(fm)) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, fm);
                                }
                            }
                            break;
                        }
                        case "白子项": {
                            // 获取子项
                            if (Objects.nonNull(tapJyDTODayTime)) {
                                Integer fz = tapJyDTODayTime.getFz();
                                if (Objects.nonNull(fz)) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, fz);
                                }
                            }
                            break;
                        }
                        case "白母项": {
                            // 获取批次总数
                            if (Objects.nonNull(tapJyDTODayTime)) {
                                Integer fm = tapJyDTODayTime.getFm();
                                if (Objects.nonNull(fm)) {
                                    ExcelWriterUtil.addCellData(cellDataList, row, col, fm);
                                }
                            }
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            }
        }
        ExcelWriterUtil.setCellValue(sheet, cellDataList);
        // 替换当月天数和当前月份
        Date currentDate = DateUtil.addDays(new Date(), -1);
        ExcelWriterUtil.replaceCurrentMonthInTitle(sheet, 0, 1, currentDate);
        // 隐藏标题行
        sheet.getRow(itemRowNum).setZeroHeight(true);
    }

}
