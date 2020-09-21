package com.cisdi.steel.module.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cisdi.steel.module.report.entity.ReportTemporaryFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportTemporaryFileMapper extends BaseMapper<ReportTemporaryFile> {
     void deleteAll();

     List<ReportTemporaryFile> selectAll();

     void insertOne(ReportTemporaryFile reportTemporaryFile);
}
