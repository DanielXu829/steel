package com.cisdi.steel.dto.response.gl;

import lombok.Data;

import java.util.Map;

@Data
public class TagValueMapDTO {
    private Map<String, Map<Long, Double>> data;
}
