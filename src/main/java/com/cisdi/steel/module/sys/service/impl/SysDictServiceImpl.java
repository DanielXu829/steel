package com.cisdi.steel.module.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cisdi.steel.common.base.service.impl.BaseServiceImpl;
import com.cisdi.steel.common.constant.Constants;
import com.cisdi.steel.common.dto.Options;
import com.cisdi.steel.common.enums.ColumnEnum;
import com.cisdi.steel.common.exception.LeafException;
import com.cisdi.steel.common.resp.ApiPageResult;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.module.sys.entity.SysDict;
import com.cisdi.steel.module.sys.mapper.SysDictMapper;
import com.cisdi.steel.module.sys.query.SysDictQuery;
import com.cisdi.steel.module.sys.service.SysDictService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>Description: 系统字典 服务实现类 </p>
 * <P>Date: 2018-08-24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Service
public class SysDictServiceImpl extends BaseServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    /**
     * 需要查询的列
     */
    private static final String[] QUERY_COLUMNS = {"name", "parent_code"};

    @Override
    public ApiResult getOptions(String parentCode) {
        // 查询子类
        return ApiUtil.success(getValue(parentCode));
    }

    @Override
    public List<Map<String, Object>> selectTreeList() {
        QueryWrapper<SysDict> wrapper = new QueryWrapper<>();
        wrapper.select("id", "name", "parent_code AS parentCode");
        wrapper.eq("type", Constants.YES);
        wrapper.orderByAsc(ColumnEnum.CODE.getColumn());
        return this.listMaps(wrapper);
    }

    /**
     * 查询参数
     *
     * @param wrapper      查询条件
     * @param sysDictQuery 查询的参数
     */
    private void loadSearchParam(LambdaQueryWrapper<SysDict> wrapper, SysDictQuery sysDictQuery) {
        // 名称
        wrapper.likeRight(StringUtils.isNotBlank(sysDictQuery.getName()), SysDict::getName, sysDictQuery.getName());
        // 父类编码
        wrapper.nested(StringUtils.isNotBlank(sysDictQuery.getParentCode()),
                i -> i.eq(SysDict::getParentCode, sysDictQuery.getParentCode()).or().eq(SysDict::getCode, sysDictQuery.getParentCode()));
    }

    @Override
    @Transactional(rollbackFor = LeafException.class)
    public ApiResult insertRecord(SysDict record) {
        if(Constants.YES.equals(record.getType())) {
            record.setCode(null);
        } else {
            record.setType(null);
        }
        boolean insert = this.save(record);
        return getResult(insert);
    }

    @Override
    @Transactional(rollbackFor = LeafException.class)
    public ApiResult updateRecord(SysDict record) {
        boolean b = this.updateById(record);
        return getResult(b);
    }

    @Override
    public ApiPageResult pageList(SysDictQuery sysDictQuery) {
        Page<SysDict> page = initPageParam(sysDictQuery);
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<SysDict> wrapper = queryWrapper.lambda();
        // 搜索条件
        this.loadSearchParam(wrapper, sysDictQuery);
        // 排序
        super.loadSort(sysDictQuery.getSorts(), queryWrapper);
        this.page(page, wrapper);
        return ApiUtil.successPage(page.getTotal(), page.getRecords());
    }


    /**
     * 通过 父类编码 获取子类所有编码
     * label value
     *
     * @param parentCode 父类编码
     * @return 结果 集合
     */
    public List<Options<String>> getValue(String parentCode) {
        QueryWrapper<SysDict> wrapper = new QueryWrapper<>();
        // 设置查询的列
        wrapper.select(QUERY_COLUMNS);
        // 条件
        wrapper.eq(ColumnEnum.CODE.getColumn(), parentCode);
        // 排序
        wrapper.orderByAsc(ColumnEnum.CODE.getColumn());
        List<Map<String, Object>> maps = baseMapper.selectMaps(wrapper);
        List<Options<String>> result = new ArrayList<>(maps.size());
        Options<String> options;
        for (Map<String, Object> map : maps) {
            options = new Options<>();
            options.setLabel(map.get("name").toString());
            options.setValue(map.get("parent_code").toString());
            result.add(options);
        }
        return result;
    }
}
