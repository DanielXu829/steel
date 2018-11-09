package com.cisdi.steel.module.job.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 单个表格值
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/5 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class CellData implements Comparable<CellData> {
    private Integer rowIndex;
    private Integer columnIndex;
    private Object cellValue;

    @Override
    public int compareTo(CellData o) {
        if (o == null) {
            return -1;
        }
        int i = this.rowIndex.compareTo(o.rowIndex);
        if (i == 0) {
            return this.columnIndex.compareTo(o.columnIndex);
        } else {
            return i;
        }
    }
}
