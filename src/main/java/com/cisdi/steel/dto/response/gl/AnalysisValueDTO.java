package com.cisdi.steel.dto.response.gl;

import com.cisdi.steel.dto.response.gl.res.AnalysisValue;
import lombok.Data;

import java.util.List;

@Data
public class AnalysisValueDTO {
    private List<AnalysisValue> data;
}
