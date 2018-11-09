package com.cisdi.steel.module.job.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * 每一行数据
 * 安装顺序排列
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class RowData implements Comparable<RowData>, Serializable {
    /**
     * 行号
     */
    private Integer rowIndex;

    /**
     * 结果
     */
    private LinkedList<Object> values;

    @Override
    public int compareTo(RowData o) {
        if (o == null) {
            return -1;
        }
        return this.rowIndex.compareTo(o.rowIndex);
    }
}
