package com.cisdi.steel.module.job.dto;

import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import lombok.Builder;
import lombok.Data;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * sheet包含的数据
 * 一个sheet 写入的数据
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Data
@Builder(toBuilder = true)
public class SheetRowCellData {

    /**
     * 当前文档
     */
    private Workbook workbook;
    /**
     * 指定sheet
     */
    private Sheet sheet;
    /**
     * 每一行的值
     */
    private List<RowCellData> rowCellDataList;

    /**
     * 对当前数据 全部填充到文件中
     */
    public void allValueWriteExcel() {
        ExcelWriterUtil.setSheetRowCelData(this);
    }
}
