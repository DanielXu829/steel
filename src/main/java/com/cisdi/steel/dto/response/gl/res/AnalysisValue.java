package com.cisdi.steel.dto.response.gl.res;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 3/20/2018.
 */
public class AnalysisValue {

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    private Analysis analysis;


    public Map<String, BigDecimal> getValues() {
        return values;
    }

    public void setValues(Map<String, BigDecimal> values) {
        this.values = values;
    }

    private Map<String,BigDecimal> values = new LinkedHashMap<>();
//    private List<AnalysisVal> values;


}
