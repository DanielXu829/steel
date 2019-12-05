package com.cisdi.steel.module.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.cisdi.steel.common.base.service.impl.BaseServiceImpl;
import com.cisdi.steel.common.constant.Constants;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.module.report.entity.TargetManagement;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import com.cisdi.steel.module.report.service.TargetManagementService;
import com.cisdi.steel.module.report.util.TargetManagementUtil;
import org.apache.poi.util.StringUtil;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * <p>Description: tag点别名 服务实现类 </p>
 * <P>Date: 2019-11-14 </P>
 *
 * @version 1.0
 */
@Service
public class TargetManagementServiceImpl extends BaseServiceImpl<TargetManagementMapper, TargetManagement> implements TargetManagementService {

    @Autowired
    private TargetManagementMapper targetManagementMapper;

    @Override
    public ApiResult<List<TargetManagement>> selectAllTargetManagement(TargetManagement record) {
        LambdaQueryWrapper<TargetManagement> wrapper = new QueryWrapper<TargetManagement>().lambda();
        wrapper.orderByAsc(TargetManagement::getWrittenName);
        List<TargetManagement> list = this.list(wrapper);
        List<TargetManagement> targetManagements = TargetManagementUtil.list2TreeConverter(list, record.getParentId());
        return ApiUtil.success(targetManagements);
    }

    @Override
    public ApiResult deleteCurrentTarget(TargetManagement record) {
        if (Objects.nonNull(record)) {
            long id = record.getId();
            List<TargetManagement> list = this.list(null);
            List<TargetManagement> targetManagements = TargetManagementUtil.list2TreeConverter(list, id);
            this.deleteTargetTree(targetManagements, list);
        }
        // 删除所有子节点后，删除此节点
        this.removeById(record.getId());

        return ApiUtil.success();
    }

    @Override
    public ApiResult selectTargetManagementByCondition(String condition) {
        List<TargetManagement> targetManagements = targetManagementMapper.selectTargetManagementByCondition(condition);
        return ApiUtil.success(targetManagements);
    }

    @Override
    public ApiResult insertRecord(TargetManagement record) {
        if (Objects.nonNull(record)) {
            String targetName = record.getTargetName();
            if (!StringPool.EMPTY.equals(targetName) && targetName.startsWith("ZP") &&record.getIsLeaf() == 1) {
                LambdaQueryWrapper<TargetManagement> wrapper = new QueryWrapper<TargetManagement>().lambda();
                wrapper.eq(TargetManagement::getTargetName, targetName);
                List<TargetManagement> list = targetManagementMapper.selectList(wrapper);
                if (Objects.isNull(list) || list.size() < 1) {
                    this.save(record);
                } else {
                    return ApiUtil.fail("新增失败，tag别点重复");
                }
            } else {
                this.save(record);
            }

            return ApiUtil.success();
        }

        return ApiUtil.fail();
    }

    @Override
    public ApiResult updateRecord(TargetManagement record) {
        if (Objects.nonNull(record)) {
            String targetName = record.getTargetName();
            if (!StringPool.EMPTY.equals(targetName) && targetName.startsWith("ZP") &&record.getIsLeaf() == 1) {
                LambdaQueryWrapper<TargetManagement> wrapper = new QueryWrapper<TargetManagement>().lambda();
                wrapper.eq(TargetManagement::getTargetName, targetName);
                List<TargetManagement> list = targetManagementMapper.selectList(wrapper);
                if (Objects.isNull(list) || list.size() < 1) {
                    this.updateById(record);
                } else {
                    return ApiUtil.fail("更新失败，tag别点重复");
                }
            } else {
                this.updateById(record);
            }

            return ApiUtil.success();
        }

        return ApiUtil.fail();
    }

    /**
     * 删除节点和该节点下面所有的子节点
     * @param targetManagements
     * @param list
     * @return
     */
    public void deleteTargetTree(List<TargetManagement> targetManagements, List<TargetManagement> list) {
        for (TargetManagement targetManagement : targetManagements) {
            removeTargetManagement(list, targetManagement);
        }
    }

    /**
     * 递归删除节点
     * @param list
     * @param t
     */
    public void removeTargetManagement(List<TargetManagement> list, TargetManagement t) {
        // 直接在数据库中删除数据
        this.removeById(t.getId());

        // 如果不是叶子节点，则需要删除该节点下面所有的节点
        if (t.getIsLeaf() == 0L) {
            //只能获取当前t节点的子节点集,并不是所有子节点集
            List<TargetManagement> childsList = TargetManagementUtil.getChildList(list, t);
            //迭代子集对象集
            //遍历完,则退出递归
            for (TargetManagement nextChild : childsList) {
                //判断子集对象是否还有子节点
                if (!CollectionUtils.isEmpty(childsList)) {
                    //有下一个子节点,继续递归
                    removeTargetManagement(list, nextChild);
                }
            }
        }
    }

}
