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

import org.springframework.util.CollectionUtils;

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
    public ApiResult deleteCurrentTarget(ReportCategory record) {
        if (Objects.nonNull(record)) {
            long id = record.getId();
            List<ReportCategory> list = this.list(null);
            List<ReportCategory> reportCategorys = ReportCategoryUtil.list2TreeConverter(list, id);
            this.deleteTargetTree(reportCategorys, list);
        }
        // 删除所有子节点后，删除此节点
        this.removeById(record.getId());

        return ApiUtil.success();
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
        this.removeById(t.getId());

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
