package com.cisdi.steel.module.quartz.service;

import com.cisdi.steel.module.quartz.mapper.QuartzMapper;
import com.cisdi.steel.module.quartz.entity.QuartzEntity;

public interface QuartzService {

    /**
     * 通过reportCategoryCode获取QuartzEntity
     * @param reportCategoryCode
     * @return QuartzEntity
     */
    public QuartzEntity selectQuartzByCode(String reportCategoryCode);
}
