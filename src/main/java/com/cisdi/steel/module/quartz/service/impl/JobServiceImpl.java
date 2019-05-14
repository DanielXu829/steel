package com.cisdi.steel.module.quartz.service.impl;

import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.module.quartz.entity.QuartzEntity;
import com.cisdi.steel.module.quartz.mapper.QuartzMapper;
import com.cisdi.steel.module.quartz.query.QuartzEntityQuery;
import com.cisdi.steel.module.quartz.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>Description: 任务执行实现类 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author leaf
 * @version 1.0
 * @since 2018/7/9
 */
@Service
public class JobServiceImpl implements JobService {

    private final QuartzMapper quartzMapper;

    @Autowired
    public JobServiceImpl(QuartzMapper quartzMapper) {
        this.quartzMapper = quartzMapper;
    }

    @Override
    public ApiResult listQuartzEntity(QuartzEntityQuery query) {
        long l = quartzMapper.selectQuartzCount(query);
        if(l==0){
            return ApiUtil.successPage(l, null);
        }
        List<QuartzEntity> quartzEntities = quartzMapper.selectQuartzList(query);
        return ApiUtil.successPage(l, quartzEntities);
    }

    @Override
    public ApiResult selectAllGroupName() {
        Set<String> result = quartzMapper.selectAllGroupName();
        return ApiUtil.success(result);
    }
}
