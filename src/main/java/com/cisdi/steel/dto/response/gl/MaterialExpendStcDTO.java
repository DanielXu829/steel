package com.cisdi.steel.dto.response.gl;

import com.cisdi.steel.dto.response.gl.res.MaterialExpend;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MaterialExpendStcDTO {
    private Map<String, List<MaterialExpend>> data;
}
