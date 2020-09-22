package com.cisdi.steel.module.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("temporary_file")
public class ReportTemporaryFile {
    private Long id;
    private String filePath;
    private Integer fileType;
}
