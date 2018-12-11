package com.cisdi.steel.module.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cisdi.steel.common.base.service.impl.BaseServiceImpl;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.module.sys.entity.SysConfig;
import com.cisdi.steel.module.sys.mapper.SysConfigMapper;
import com.cisdi.steel.module.sys.service.SysConfigService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>Description: 配置 服务实现类 </p>
 * <P>Date: 2018-10-24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Service
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public String selectNameByCode(String code) {
        LambdaQueryWrapper<SysConfig> wrapper = new QueryWrapper<SysConfig>().lambda();
        wrapper.select(SysConfig::getName);
        wrapper.eq(SysConfig::getCode, code);
        SysConfig one = this.getOne(wrapper);
        if (Objects.isNull(one)) {
            return null;
        }
        return one.getName();
    }

    @Override
    public String selectActionByCode(String code) {
        LambdaQueryWrapper<SysConfig> wrapper = new QueryWrapper<SysConfig>().lambda();
        wrapper.select(SysConfig::getAction);
        wrapper.eq(SysConfig::getCode, code);
        SysConfig one = this.getOne(wrapper);
        if (Objects.isNull(one)) {
            return null;
        }
        return one.getAction();
    }

    @Override
    public ApiResult systemParam() {
        LambdaQueryWrapper<SysConfig> wrapper = new QueryWrapper<SysConfig>().lambda();
        wrapper.select(SysConfig::getId, SysConfig::getName, SysConfig::getAction, SysConfig::getCode);
        wrapper.isNotNull(SysConfig::getName);
        wrapper.isNotNull(SysConfig::getAction);
        wrapper.isNotNull(SysConfig::getCode);
        List<SysConfig> sysConfigs = this.list(wrapper);
        return ApiUtil.success(sysConfigs);
    }

    @Override
    public ApiResult updateRecord(SysConfig record) {
        LambdaQueryWrapper<SysConfig> wrapper = new QueryWrapper<SysConfig>().lambda();
        wrapper.eq(SysConfig::getCode, record.getCode());
        SysConfig sysConfig = new SysConfig();
        sysConfig.setAction(record.getAction());
        sysConfig.setCode(record.getCode());
        this.update(sysConfig, wrapper);
        return ApiUtil.success();
    }
}
