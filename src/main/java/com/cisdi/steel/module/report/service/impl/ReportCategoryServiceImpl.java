package com.cisdi.steel.module.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cisdi.steel.common.base.service.impl.BaseServiceImpl;
import com.cisdi.steel.common.constant.Constants;
import com.cisdi.steel.common.exception.CodeRepeatException;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.report.dto.ReportPathDTO;
import com.cisdi.steel.module.report.entity.ReportCategory;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.mapper.ReportCategoryMapper;
import com.cisdi.steel.module.report.service.ReportCategoryService;
import com.cisdi.steel.module.report.util.ReportCategoryUtil;
import com.cisdi.steel.module.sys.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * <p>Description: 报表分类 服务实现类 </p>
 * <P>Date: 2018-10-23 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Service
public class ReportCategoryServiceImpl extends BaseServiceImpl<ReportCategoryMapper, ReportCategory> implements ReportCategoryService {


    @Override
    public ApiResult insertRecord(ReportCategory record) {
        if (Constants.YES.equals(record.getLeafNode())) {
            if (StringUtils.isBlank(record.getCode())) {
                return ApiUtil.fail("编码不能为空");
            }
            this.checkReportCategoryCode(record);
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
            name = Pattern.compile("[\\d]").matcher(name).replaceAll("");
            if (name.startsWith("烧结")) {
                wrapper.likeRight(true, ReportCategory::getCode, "sj_");
            } else if (name.startsWith("高炉")) {
                wrapper.likeRight(true, ReportCategory::getCode, "gl_");
            } else if (name.startsWith("能介")) {
                wrapper.likeRight(true, ReportCategory::getCode, "nj_");
            } else if (name.startsWith("原料")) {
                wrapper.likeRight(true, ReportCategory::getCode, "ygl_");
            }
        }
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
