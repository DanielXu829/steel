package com.cisdi.steel.module.job.strategy;

import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
public interface ApiStrategy {

    /**
     * 表单数据
     *
     * @param workbook  文件
     * @param sheet     指定sheet
     * @param queryList 查询条件
     * @return 结果
     */
    SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList);
}
