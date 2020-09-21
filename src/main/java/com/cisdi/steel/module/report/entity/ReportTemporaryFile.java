package com.cisdi.steel.module.report.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ReportTemporaryFile {
    private Long id;
    private String filePath;
    private Integer fileType;
}
