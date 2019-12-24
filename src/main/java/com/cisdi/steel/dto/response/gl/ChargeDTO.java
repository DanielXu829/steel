package com.cisdi.steel.dto.response.gl;

import com.cisdi.steel.dto.response.gl.res.BatchData;
import lombok.Data;

import java.util.List;

@Data
public class ChargeDTO {
    private List<BatchData> data;
}
