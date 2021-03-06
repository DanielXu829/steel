package com.cisdi.steel.module.job.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 每一行对应的值
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RowCellData implements Comparable<RowCellData>, Serializable {
    /**
     * 行号
     */
    private Integer rowIndex;

    private List<CellValInfo> values;

    @Override
    public int compareTo(RowCellData o) {
        if (o == null) {
            return -1;
        }
        return this.rowIndex.compareTo(o.rowIndex);
    }
}
