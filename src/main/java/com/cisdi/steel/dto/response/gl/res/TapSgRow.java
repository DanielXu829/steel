package com.cisdi.steel.dto.response.gl.res;

import lombok.Data;
import org.apache.commons.collections.map.HashedMap;

import java.util.Date;
import java.util.Map;

/**
 * Created by w.wolf on 2019/1/19.
 */
@Data
public class TapSgRow {
    private String tapNo;
    private Short tapHole;
    private Date startTime;
    private Date slagTime;
    private Date endTime;
    private Date comeTime;
    private String tpcInfoDesc;
    private Short tpcNum;
    private String remark;
    private Map<String,Double> tapValues = new HashedMap();
    private Map<String,Double> hmAnalysis = new HashedMap();
    private Map<String,Double> slagAnalysis = new HashedMap();

}
