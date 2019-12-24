package com.cisdi.steel.dto.response.gl;

import com.cisdi.steel.dto.response.gl.res.TagValue;
import lombok.Data;

import java.util.List;

@Data
public class TagValueListDTO {
    private List<TagValue> data;
}
