package com.cisdi.steel.http;

import com.cisdi.steel.module.job.dto.CellData;
import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
public class RowData {

    private Integer rowNum;

    private Set<CellData> cellDataList = new HashSet<>();

    public void addCellData(int rowNum, int columnIndex, Object value) {
        cellDataList.add(new CellData(rowNum, columnIndex, Objects.isNull(value) ? "" : value));
    }
}
