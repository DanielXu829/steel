package com.cisdi.steel.module.quartz.service.impl;

import com.cisdi.steel.module.quartz.mapper.QuartzMapper;
import com.cisdi.steel.module.quartz.entity.QuartzEntity;
import com.cisdi.steel.module.quartz.service.QuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuartzServiceImpl implements QuartzService {

    private final QuartzMapper quartzMapper;

    @Autowired
    public QuartzServiceImpl(QuartzMapper quartzMapper) {
        this.quartzMapper = quartzMapper;
    }

    public QuartzEntity selectQuartzByCode (String code){
        QuartzEntity quartzEntity = quartzMapper.selectQuartzByCode(code);
        return quartzEntity;
    }
}
