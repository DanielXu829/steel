package com.cisdi.steel.module.job.a1.doc;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ShangLiaoDTO {
    private String brandCode;
    private Date startDate;
    private Date endDate;
}
