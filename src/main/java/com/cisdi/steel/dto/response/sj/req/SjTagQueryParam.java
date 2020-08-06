package com.cisdi.steel.dto.response.sj.req;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SjTagQueryParam {
    private Long start;
    private Long end;
    private List<String> tagNames;
}
