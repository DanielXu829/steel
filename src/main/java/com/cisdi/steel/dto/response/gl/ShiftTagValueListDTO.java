package com.cisdi.steel.dto.response.gl;

import com.cisdi.steel.dto.response.gl.res.ShiftTagValue;
import lombok.Data;

import java.util.List;

@Data
public class ShiftTagValueListDTO {
    private List<ShiftTagValue>  data;
}
