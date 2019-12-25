package com.cisdi.steel.dto.response.sj.res;

import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class AnalysisValue {
    private String materialClass;
    private String materialType;

    private Analysis analysis;

    private Map<String, BigDecimal> values = new LinkedHashMap<>();
}
