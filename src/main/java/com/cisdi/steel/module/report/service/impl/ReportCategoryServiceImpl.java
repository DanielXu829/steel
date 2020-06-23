package com.cisdi.steel.module.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cisdi.steel.common.base.service.impl.BaseServiceImpl;
import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.constant.Constants;
import com.cisdi.steel.common.exception.CodeRepeatException;
import com.cisdi.steel.common.exception.LeafException;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.report.dto.ReportPathDTO;
import com.cisdi.steel.module.report.entity.ReportCategory;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.mapper.ReportCategoryMapper;
import com.cisdi.steel.module.report.mapper.ReportCategoryTemplateMapper;
import com.cisdi.steel.module.report.service.ReportCategoryService;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import com.cisdi.steel.module.report.util.ReportCategoryUtil;
import com.cisdi.steel.module.sys.entity.SysConfig;
import com.cisdi.steel.module.sys.mapper.SysConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>Description: 报表分类 服务实现类 </p>
 * <P>Date: 2018-10-23 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Service
@Slf4j
public class ReportCategoryServiceImpl extends BaseServiceImpl<ReportCategoryMapper, ReportCategory> implements ReportCategoryService {

    @Autowired
    private JobProperties jobProperties;

    @Autowired
    ReportCategoryMapper reportCategoryMapper;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private ReportCategoryTemplateMapper reportCategoryTemplateMapper;

    @Autowired
    private ReportCategoryTemplateService reportCategoryTemplateService;

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
        }

        return super.insertRecord(record);
    }

    @Override
    @Transactional(rollbackFor = LeafException.class)
    public ApiResult deleteRecord(BaseId record) {
        // 如果没有记录 ，直接返回删除成功
        if (Objects.isNull(record)) {
            return ApiUtil.success();
        }
        // 判断是否id
        if (Objects.nonNull(record.getId())) {
            // 删除
            log.debug("delete  id" + record.getId());
            ReportCategory reportCategory = baseMapper.selectById(record.getId());
            BaseId baseId = new BaseId();
            LambdaQueryWrapper<ReportCategoryTemplate> categoryTemplateWrapper = new QueryWrapper<ReportCategoryTemplate>().lambda();
            categoryTemplateWrapper.eq(ReportCategoryTemplate::getReportCategoryCode, reportCategory.getCode());
            List<ReportCategoryTemplate> reportCategoryTemplates = reportCategoryTemplateMapper.selectList(categoryTemplateWrapper);
            if (CollectionUtils.isNotEmpty(reportCategoryTemplates)) {
                baseId.setIds(reportCategoryTemplates.stream().map(ReportCategoryTemplate::getId).collect(Collectors.toList()));
                // 删除模板
                reportCategoryTemplateService.deleteRecord(baseId);
            }
            Integer result = baseMapper.deleteById(record.getId());
            // 等于1 表示删除了1条记录
            return getResult(result);
        }
        if (Objects.nonNull(record.getIds()) && !record.getIds().isEmpty()) {
            log.debug("delete ids " + record.getIds());
            List<ReportCategory> reportCategories = baseMapper.selectBatchIds(record.getIds());
            // 查询reportCategories下所有的模板的id
            LambdaQueryWrapper<ReportCategoryTemplate> categoryTemplateWrapper = new QueryWrapper<ReportCategoryTemplate>().lambda();
            List<String> reportCategoryCodes = reportCategories.stream().map(ReportCategory::getCode).collect(Collectors.toList());
            categoryTemplateWrapper.in(ReportCategoryTemplate::getReportCategoryCode, reportCategoryCodes);
            // 批量查询模板

            List<ReportCategoryTemplate> reportCategoryTemplates = reportCategoryTemplateMapper.selectList(categoryTemplateWrapper);
            if (CollectionUtils.isNotEmpty(reportCategoryTemplates)) {
                BaseId baseId = new BaseId();
                baseId.setIds(reportCategoryTemplates.stream().map(ReportCategoryTemplate::getId).collect(Collectors.toList()));
                // 删除模板
                reportCategoryTemplateService.deleteRecord(baseId);
            }
            boolean result = this.removeByIds(record.getIds());
            // 多条记录
            return getResult(result);
        }
        return ApiUtil.success();
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
    public ApiResult<List<ReportCategory>> selectAllCategoryNoLeaf(ReportCategory record) {
        String name = record.getName();
        LambdaQueryWrapper<ReportCategory> wrapper = new QueryWrapper<ReportCategory>().lambda();
        if (StringUtils.isNotBlank(name)) {
            wrapper.like(true, ReportCategory::getRemark, name);
        }
        wrapper.orderByAsc(ReportCategory::getSort);
        List<ReportCategory> list = this.list(wrapper);
        List<ReportCategory> reportCategories = ReportCategoryUtil.list2TreeConverterNoLeaf(list, Constants.PARENT_ID);
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

    /**
     * 递归删除当前节点下的所有节点
     * @param record
     * @return
     */
    @Override
    @Transactional(rollbackFor = LeafException.class)
    public ApiResult deleteCurrentTarget(ReportCategory record) {
        if (Objects.isNull(record)) {
            return ApiUtil.success();
        }
        long recordId = record.getId();
        List<ReportCategory> list = this.list(null);
        List<ReportCategory> reportCategoryList = ReportCategoryUtil.list2TreeConverter(list, recordId);
        List<ReportCategory> childCategoryList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(reportCategoryList)) {
            ReportCategoryUtil.getAllChildCategorys(reportCategoryList, childCategoryList);
        }
        List<Long> categoryIds = childCategoryList.stream().map(ReportCategory::getId).collect(Collectors.toList());
        categoryIds.add(recordId);
        BaseId baseId = new BaseId();
        baseId.setIds(categoryIds);
        return this.deleteRecord(baseId);
    }

    /**
     * 删除节点和该节点下面所有的子节点
     * @param reportCategorys
     * @param list
     * @return
     */
    public void deleteTargetTree(List<ReportCategory> reportCategorys, List<ReportCategory> list) {
        for (ReportCategory reportCategory : reportCategorys) {
            removeTargetReportCategory(list, reportCategory);
        }
    }

    /**
     * 递归删除节点
     * @param list
     * @param t
     */
    public void removeTargetReportCategory(List<ReportCategory> list, ReportCategory t) {
        // 直接在数据库中删除数据
        BaseId baseId = new BaseId();
        baseId.setId(t.getId());
        this.deleteRecord(baseId);

        // 如果不是叶子节点，则需要删除该节点下面所有的节点
        if ("0".equals(t.getLeafNode())) {
            //只能获取当前t节点的子节点集,并不是所有子节点集
            List<ReportCategory> childsList = ReportCategoryUtil.getChildList(list, t);
            //迭代子集对象集
            //遍历完,则退出递归
            for (ReportCategory nextChild : childsList) {
                //判断子集对象是否还有子节点
                if (!CollectionUtils.isEmpty(childsList)) {
                    //有下一个子节点,继续递归
                    removeTargetReportCategory(list, nextChild);
                }
            }
        }
    }
}
