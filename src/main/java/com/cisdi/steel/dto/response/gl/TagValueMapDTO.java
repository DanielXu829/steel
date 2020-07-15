package com.cisdi.steel.dto.response.gl;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class TagValueMapDTO {
    private Map<String, LinkedHashMap<Long, Double>> data;
}
