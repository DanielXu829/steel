package com.cisdi.steel.dto.response.gl;

import com.cisdi.steel.dto.response.gl.res.TapSummary;
import lombok.Data;

import java.util.List;

@Data
public class TapSummaryListDTO {
    private List<TapSummary> data;
}
