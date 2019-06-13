package com.cisdi.steel.module.job.a2.writer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class DateSegment {
    private Date startDate;
    private Date endDate;
}
