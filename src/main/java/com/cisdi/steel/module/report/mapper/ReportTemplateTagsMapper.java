package com.cisdi.steel.module.report.mapper;

import com.cisdi.steel.module.report.entity.SysConfig;
import com.cisdi.steel.module.report.entity.TargetManagement;
import org.apache.ibatis.annotations.Mapper;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * <p>Description: 报表动态模板 - 参数列表 Mapper 接口 </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
@Mapper
public interface ReportTemplateTagsMapper extends BaseMapper<ReportTemplateTags> {

    List<String> selectTagNameBySheetId(@Param("sheetId") String sheetId);

    List<String> test1(@Param("sheetId") String sheetId);

    List<String> selectTagNameByCode(@Param("code")String code);


    SysConfig selectUrlByCode(@Param("code") String code);

}
