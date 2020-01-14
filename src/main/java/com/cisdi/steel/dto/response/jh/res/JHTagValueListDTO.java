package com.cisdi.steel.dto.response.jh.res;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Data
public class JHTagValueListDTO {
    private boolean success;
    private String code;
    private String message;
    private LinkedHashMap<String, List<TagValue>> data;
    private ArrayList rows;
}
