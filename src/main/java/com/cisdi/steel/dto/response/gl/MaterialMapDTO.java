package com.cisdi.steel.dto.response.gl;

import com.cisdi.steel.dto.response.gl.res.Material;
import lombok.Data;

import java.util.Map;

@Data
public class MaterialMapDTO {
    private Map<String, Material> data;
}
