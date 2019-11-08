package com.cisdi.steel.module.job.jh.writer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * <p>Description: 时间类，用于组装时间策略 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/05 </P>
 *
 *  @version 1.0
 */
@Data
@AllArgsConstructor
public class DateSegment {

    private Date startDate;

    private Date endDate;

}
