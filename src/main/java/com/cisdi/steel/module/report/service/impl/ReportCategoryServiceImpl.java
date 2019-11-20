package com.cisdi.steel.module.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cisdi.steel.common.base.service.impl.BaseServiceImpl;
import com.cisdi.steel.common.constant.Constants;
import com.cisdi.steel.common.exception.CodeRepeatException;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.report.dto.ReportPathDTO;
import com.cisdi.steel.module.report.entity.ReportCategory;
import com.cisdi.steel.module.report.mapper.ReportCategoryMapper;
import com.cisdi.steel.module.report.service.ReportCategoryService;
import com.cisdi.steel.module.report.util.ReportCategoryUtil;
import com.cisdi.steel.module.sys.entity.SysConfig;
import com.cisdi.steel.module.sys.mapper.SysConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * <p>Description: 报表分类 服务实现类 </p>
 * <P>Date: 2018-10-23 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Service
public class ReportCategoryServiceImpl extends BaseServiceImpl<ReportCategoryMapper, ReportCategory> implements ReportCategoryService {

    @Autowired
    private JobProperties jobProperties;

    @Autowired
    ReportCategoryMapper reportCategoryMapper;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    /**
     * 插入数据到ReportCategory表中，同时在SysConfig表中插入数据，存储report路径
     * @param record 数据
     * @param sequence 单个工序
     * @return
     */
    @Override
    public ApiResult insertRecord(ReportCategory record, String sequence) {
        if (Constants.YES.equals(record.getLeafNode())) {
            if (StringUtils.isBlank(record.getCode())) {
                return ApiUtil.fail("编码不能为空");
            }
            this.checkReportCategoryCode(record);
            long parentId = record.getParentId();
            // 获得父节点的name
            ReportCategory parentReportCategory = reportCategoryMapper.selectById(parentId);
            String sequenceParent = parentReportCategory.getName();

            // 组装模板暂时存储路径和模板存储路径
            String templatePath = jobProperties.getTemplatePath();
            String tempPath = jobProperties.getTempPath();
            String actionPath = tempPath + File.separator + sequence  + File.separator + sequenceParent;
            String namePath = templatePath + File.separator + sequence + File.separator + sequenceParent;
            SysConfig sysConfig = new SysConfig();
            sysConfig.setCode(record.getCode());
            sysConfig.setAction(actionPath);
            sysConfig.setName(namePath);
            sysConfig.setClassName("code2path");
            sysConfigMapper.insert(sysConfig);
        }

        return super.insertRecord(record);
    }

    /**
     * 检查编码是否重复
     *
     * @param record 数据
     */
    private void checkReportCategoryCode(ReportCategory record) {
        LambdaQueryWrapper<ReportCategory> wrapper =
                new QueryWrapper<ReportCategory>().lambda();
        wrapper.eq(ReportCategory::getCode, record.getCode());
        int count = this.count(wrapper);
        if (count > 0) {
            throw new CodeRepeatException();
        }
    }

    @Override
    public ApiResult<List<ReportCategory>> selectAllCategory(ReportCategory record) {
        String name = record.getName();
        LambdaQueryWrapper<ReportCategory> wrapper = new QueryWrapper<ReportCategory>().lambda();
        if (StringUtils.isNotBlank(name)) {
//            if ("焦化".equals(name)) {
//                name = "焦化67";
//            }
            wrapper.like(true, ReportCategory::getRemark, name);
        }
        wrapper.orderByAsc(ReportCategory::getSort);
        List<ReportCategory> list = this.list(wrapper);
        List<ReportCategory> reportCategories = ReportCategoryUtil.list2TreeConverter(list, Constants.PARENT_ID);
        return ApiUtil.success(reportCategories);
    }

    @Override
    public ReportPathDTO selectReportInfoByCode(String code) {
        LambdaQueryWrapper<ReportCategory> wrapper = new QueryWrapper<ReportCategory>().lambda();
        wrapper.select(ReportCategory::getParentId, ReportCategory::getName);
        wrapper.eq(ReportCategory::getCode, code);
        ReportCategory current = this.getOne(wrapper);
        if (Objects.isNull(current)) {
            return null;
        }
        LinkedList<String> result = new LinkedList<>();
        Long parentId = current.getParentId();
        while (Objects.nonNull(parentId) && !parentId.equals(Constants.PARENT_ID)) {
            LambdaQueryWrapper<ReportCategory> wrapper2 = new QueryWrapper<ReportCategory>().lambda();
            wrapper.select(ReportCategory::getParentId, ReportCategory::getName);
            wrapper2.eq(ReportCategory::getId, parentId);
            ReportCategory one = this.getOne(wrapper2);
            if (Objects.isNull(one)) {
                break;
            }
            result.add(one.getName());
            parentId = one.getParentId();
        }
        if (result.isEmpty()) {
            return null;
        }
        int size = result.size();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append(File.separator)
                    .append(result.peekLast());
        }
        ReportPathDTO reportPathDTO = new ReportPathDTO();
        reportPathDTO.setPath(builder.toString());
        return reportPathDTO;
    }
}
