package com.cisdi.steel.module.job.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>Description:         </p>
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
    private Integer rowNum;
    private Integer column;
    private Object value;

    @Override
    public int compareTo(CellData o) {
        if (o == null) {
            return -1;
        }
        int i = this.rowNum.compareTo(o.rowNum);
        if (i == 0) {
            return this.column.compareTo(o.column);
        } else {
            return i;
        }
    }
}
