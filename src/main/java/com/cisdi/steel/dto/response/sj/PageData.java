package com.cisdi.steel.dto.response.sj;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 3/29/2018.
 */
@Data
public class PageData<T> implements Serializable{
    private int pageNum;
    private int pageSize;
    private long total;
    private List<T> data;

}
